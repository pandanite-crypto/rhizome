package rhizome.services.network.discovery;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import com.dslplatform.json.DslJson;

import io.activej.async.service.EventloopService;
import io.activej.config.Config;
import io.activej.eventloop.Eventloop;
import io.activej.http.AsyncHttpClient;
import io.activej.http.HttpRequest;
import io.activej.promise.Promise;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import rhizome.core.net.NetworkUtils;
import rhizome.core.common.SchedulerUtil;

import static io.activej.config.converter.ConfigConverters.ofBoolean;
import static io.activej.config.converter.ConfigConverters.ofInteger;
import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Getter
public class PeerDiscoveryService implements EventloopService {

    private static final int PING_INTERVAL = 60;

    private Eventloop eventloop;
    private AsyncHttpClient httpClient;
    private PeerDiscoveryListener peerManagerService;
    private Map<String, DiscoveryPeer> discoveredAddressList = new HashMap<>();
    private String hostIp;
    private DslJson<Object> dslJson = new DslJson<>();
    protected Set<String> blacklist;
    protected Set<String> whitelist;

    PeerDiscoveryService(Eventloop eventloop, Config config, PeerDiscoveryListener peerManagerService) {
        this.httpClient = AsyncHttpClient.create(eventloop);
        this.eventloop = eventloop;
        this.peerManagerService = peerManagerService;
        this.hostIp = NetworkUtils.computeAddress(config.get("ip"), config.get(ofInteger(), "port"), config.get(ofBoolean(), "firewall"));
    }

    @Override
    public @NotNull Promise<?> start() {
        log.info("|PEER DISCOVERY SERVICE CLIENT STARTING|");
        // TODO: INITALIZE DISCOVERED ADDRESS LIST AND VAIRABLES
        String nodeId = "localhost"; 
        return Promise.ofBlocking(eventloop, () -> SchedulerUtil.scheduleEveryMinute(eventloop,  () -> Promise.ofBlocking(eventloop, this::handshake)).start())
            .whenResult(() -> log.info("|PEER DISCOVERY SERVICE CLIENT STARTED|"));
    }

    @Override
    public @NotNull Promise<?>stop() {
        return Promise.complete()
            .whenResult(() -> log.info("|PEER DISCOVERY SERVICE CLIENT STOPPED|"));
    }

    /**
     * Shaking hand
     */
    private void handshake() {
        log.debug("Sending handshake...");
        discoveredAddressList.forEach((key, value) -> {

            if(value.lastPingTime > System.currentTimeMillis() / 1000 - PING_INTERVAL) {
                return;
            }

            log.debug("Sending handshake to {}", key);

            // benchmarking
            var startRequestTime = System.currentTimeMillis() / 1000;

            httpClient.request(HttpRequest.get(key + "/peers"))
                .then(response -> response.loadBody())
                .map(body -> {
                    var peerBytes = body.getString(UTF_8).getBytes();
                    return dslJson.deserializeList(String.class, peerBytes, peerBytes.length);
                })
                .getResult()
                .forEach(discoveredIp -> {
                    if (this.blacklist.contains(discoveredIp)) return;

                    discoveredAddressList.compute(discoveredIp, (ip, peer) -> {
                            if (peer != null) {
                                peer.lastPingTime = System.currentTimeMillis() / 1000;
                                peer.clockDelta = System.currentTimeMillis() / 1000 - startRequestTime;
                                return peer;
                            }

                            DiscoveryPeer newPeer = new DiscoveryPeer(new InetSocketAddress(discoveredIp, 8080), System.currentTimeMillis(), 0L);
                            notifyPeerAdded(newPeer);
                            return newPeer;
                        });
                    }
                );
        });
    }

    private void notifyPeerAdded(DiscoveryPeer newPeer) {
        peerManagerService.onNewPeerDiscovered(newPeer);
    }

    public static class DiscoveryPeer {
        InetSocketAddress address;
        long lastPingTime; 
        long clockDelta;
    
        public DiscoveryPeer(InetSocketAddress address, long lastPingTime, long clockDelta) {
            this.address = address;
            this.lastPingTime = lastPingTime;
            this.clockDelta = clockDelta;
        }
     }
}
