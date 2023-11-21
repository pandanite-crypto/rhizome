package rhizome.services.network;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import rhizome.core.blockchain.HeaderChain;
import rhizome.persistence.BlockPersistence;

@Getter
@Setter
public class AbstractPeerManager implements PeerManager {

    protected List<HeaderChain> currPeers; // Assumant une classe HeaderChain similaire en Java
    protected BlockPersistence blockStore;       // Utilisation de la classe BlockStore adaptée en Java
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
    protected Map<Long, String> checkpoints; // SHA256Hash mappé à String
    protected Map<Long, String> bannedHashes; // SHA256Hash mappé à String
    protected List<String> hostSources;
    protected List<String> hosts;
    protected Set<String> blacklist;
    protected Set<String> whitelist;
    protected List<Thread> syncThread;
    protected List<Thread> headerStatsThread;
    
}
