package rhizome.services.network;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import rhizome.core.api.PeerInterface;
import rhizome.core.blockchain.HeaderChain;
import rhizome.core.common.Constants;
import rhizome.core.common.Pair;
import rhizome.core.crypto.SHA256Hash;
import rhizome.persistence.BlockPersistence;

@Slf4j
@Getter
@Setter
public class PeerManagerImpl implements PeerManager {

    protected List<HeaderChain> currPeers;
    protected BlockPersistence blockStore;
    protected final Lock lock = new ReentrantLock();
    protected boolean disabled;
    protected boolean firewall;
    protected String ip;
    protected int port;
    protected String name;
    protected String address;
    protected String version;
    protected String minHostVersion;
    protected String networkName;
    protected Map<String, Long> hostPingTimes;
    protected Map<String, Integer> peerClockDeltas;
    protected Map<Long, SHA256Hash> checkpoints;
    protected Map<Long, SHA256Hash> bannedHashes;
    protected List<String> hostSources;
    protected List<String> hosts;
    protected Set<String> blacklist;
    protected Set<String> whitelist;
    protected List<Thread> syncThread;
    protected List<Thread> headerStatsThread;

    @Override
    public int size() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'size'");
    }

    @Override
    public String computeAddress() {
        if (firewall) {
            return "http://undiscoverable";
        }
        if (ip.isEmpty()) {
            boolean found = false;
            List<String> lookupServices = Arrays.asList(
                "http://checkip.amazonaws.com", 
                "http://icanhazip.com", 
                "http://ifconfig.co", 
                "http://wtfismyip.com/text", 
                "http://ifconfig.io"
            );

            for (String lookupService : lookupServices) {
                try {
                    String rawUrl = fetchUrlContent(lookupService);
                    String ip = rawUrl.trim();
                    if (isValidIPv4(ip)) {
                        address = "http://" + ip + ":" + port;
                        found = true;
                        break;
                    }
                } catch (IOException e) {
                    log.error(null, e);
                }
            }

            if (!found) {
                log.error("Could not determine current IP address");
            }
        } else {
            address = ip + ":" + port;
        }
        return address;
    }

    public void startPeerSync() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            while (true) {
                try {
                    for (String host : this.hosts) {
                        PeerInterface.pingPeer(host, this.computeAddress(), System.currentTimeMillis() / 1000, this.version, this.networkName);
                    }
                    Thread.sleep(300000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void headerStats() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                while (true) {
                    TimeUnit.SECONDS.sleep(30);
                    log.debug("================ Header Sync Status ===============");
                    Map<String, Pair<Long, String>> stats = getHeaderChainStats();
                    for (Map.Entry<String, Pair<Long, String>> entry : stats.entrySet()) {
                        log.debug(String.format("Host: %s blocks: %d, node_ver: %s",
                                entry.getKey(), entry.getValue(), entry.getValue()));
                    }
                    log.debug("===================================================");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.debug("Thread interrupted");
            }
        });
    }

    public PeerManagerImpl(JSONObject config) {
        this.name = config.getString("name");
        this.port = config.getInt("port");
        this.ip = config.getString("ip");
        this.firewall = config.getBoolean("firewall");
        this.version = Constants.BUILD_VERSION;
        this.networkName = config.getString("networkName");
        computeAddress();

        // Parse checkpoints
        JSONArray checkpointsArray = config.getJSONArray("checkpoints");
        for (int i = 0; i < checkpointsArray.length(); i++) {
            JSONArray checkpoint = checkpointsArray.getJSONArray(i);
            this.checkpoints.put(checkpoint.getLong(0), SHA256Hash.of(checkpoint.getString(1)));
        }

        // Parse banned hashes
        JSONArray bannedHashesArray = config.getJSONArray("bannedHashes");
        for (int i = 0; i < bannedHashesArray.length(); i++) {
            JSONArray bannedHash = bannedHashesArray.getJSONArray(i);
            this.bannedHashes.put(bannedHash.getLong(0), SHA256Hash.of(bannedHash.getString(1)));
        }

        this.minHostVersion = config.getString("minHostVersion");

        // Check if a blacklist file exists
        try (BufferedReader blacklistReader = new BufferedReader(new FileReader("blacklist.txt"))) {
            String line;
            while ((line = blacklistReader.readLine()) != null) {
                if (line.charAt(0) != '#') {
                    String blocked = line.trim();
                    this.blacklist.add(blocked);
                    log.info("Ignoring host {}", blocked);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Check if a whitelist file exists
        try (BufferedReader whitelistReader = new BufferedReader(new FileReader("whitelist.txt"))) {
            String line;
            while ((line = whitelistReader.readLine()) != null) {
                if (line.charAt(0) != '#') {
                    String enabled = line.trim();
                    this.whitelist.add(enabled);
                    log.info("Enabling host {}", enabled);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.disabled = false;
        JSONArray hostSourcesArray = config.getJSONArray("hostSources");
        for (int i = 0; i < hostSourcesArray.length(); i++) {
            this.hostSources.add(hostSourcesArray.getString(i));
        }

        if (this.hostSources.isEmpty()) {
            String localhost = "http://localhost:3000";
            this.hosts.add(localhost);
            this.hostPingTimes.put(localhost, System.currentTimeMillis() / 1000);
            this.peerClockDeltas.put(localhost, 0);
            syncHeadersWithPeers();
        } else {
            refreshHostList();
        }

        // Start thread to print header chain stats
        boolean showHeaderStats = config.getBoolean("showHeaderStats");
        if (showHeaderStats) {
            this.headerStatsThread.add(new Thread(() -> headerStats()));
        }
    }

    @Override
    public void refreshHostList() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'refreshHostList'");
    }
    @Override
    public void startPingingPeers() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'startPingingPeers'");
    }
    @Override
    public String getGoodHost() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getGoodHost'");
    }
    @Override
    public long getBlockCount() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBlockCount'");
    }
    @Override
    public BigInteger getTotalWork() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTotalWork'");
    }
    @Override
    public byte[] getBlockHash(String host, long blockId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBlockHash'");
    }
    @Override
    public Map<String, Pair<Long, String>> getHeaderChainStats() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getHeaderChainStats'");
    }
    @Override
    public Pair<String, Long> getRandomHost() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRandomHost'");
    }
    @Override
    public List<String> getHosts(boolean includeSelf) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getHosts'");
    }
    @Override
    public Set<String> sampleFreshHosts(int count) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sampleFreshHosts'");
    }
    @Override
    public Set<String> sampleAllHosts(int count) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sampleAllHosts'");
    }
    @Override
    public long getNetworkTimestamp() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getNetworkTimestamp'");
    }
    @Override
    public void setBlockstore(BlockPersistence blockStore) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setBlockstore'");
    }
    @Override
    public void addPeer(String addr, long time, String version, String network) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addPeer'");
    }
    @Override
    public void syncHeadersWithPeers() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'syncHeadersWithPeers'");
    }

    public static boolean isValidIPv4(String ip) {
        String ipPattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        return ip.matches(ipPattern);
    }

    public static boolean isJsHost(String addr) {
        return addr.contains("peer://");
    }

    private String fetchUrlContent(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();

        return content.toString();
    }

    private static Optional<String> extractHostVersion(JSONObject hostInfo) {
        try {
            String version = hostInfo.optString("version");
            return Optional.ofNullable(version.isEmpty() ? null : version);
        } catch (Exception e) {
            return Optional.empty();
        }
    }    
    
}
