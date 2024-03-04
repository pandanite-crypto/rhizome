package rhizome.services.network;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.activej.async.function.AsyncRunnable;
import io.activej.async.function.AsyncRunnables;
import io.activej.async.service.EventloopService;
import io.activej.eventloop.Eventloop;
import io.activej.promise.Promise;
import io.activej.promise.Promises;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import rhizome.net.p2p.DiscoveryService;
import rhizome.net.p2p.PeerSystem;
import rhizome.net.p2p.peer.Peer;
import rhizome.net.p2p.peer.PeerChannel;

import static java.util.Collections.unmodifiableMap;
import static java.util.Map.Entry;
import static io.activej.async.util.LogUtils.toLogger;

/**
 * This service execute the service discovery process. It pings all peers and marks them as dead or alive accordingly.
 * It also relaunches the discovery process.
 */
@Slf4j
@Getter
public class PeerDiscoveryService implements EventloopService {

    private static final int PING_INTERVAL = 10;

    private final Eventloop eventloop;
    private final DiscoveryService discoveryService;
    private final PeerSystem peerSystem;

    private final AsyncRunnable checkAllPeers = AsyncRunnables.reuse(this::doCheckAllPeers);
    private final AsyncRunnable checkDeadPeers = AsyncRunnables.reuse(this::doCheckDeadPeers);

    private final Map<Object, Peer> peers = new HashMap<>();
    private final Map<Object, Peer> peersView = unmodifiableMap(peers);

    private final Map<Object, PeerChannel> alivePeers = new HashMap<>();
    private final Map<Object, PeerChannel> alivePeersView = unmodifiableMap(alivePeers);

    private final Map<Object, Peer> deadPeers = new HashMap<>();
    private final Map<Object, Peer> deadPeersView = unmodifiableMap(deadPeers);

    private final Map<Object, Peer> candidatePeers = new HashMap<>();
    private final Map<Object, Peer> candidatePeersView = unmodifiableMap(candidatePeers);

    /**
     * 
     * @param eventloop
     * @param discoveryService
     * @param peerSystem
     */
    private PeerDiscoveryService(Eventloop eventloop, DiscoveryService discoveryService, PeerSystem peerSystem) {
        this.eventloop = eventloop;
        this.discoveryService = discoveryService;
        this.peerSystem = peerSystem;
    }

    public static PeerDiscoveryService create(Eventloop eventloop, DiscoveryService discoveryService, PeerSystem peerSystem) {
        return new PeerDiscoveryService(eventloop, discoveryService, peerSystem);
    }

    @Override
    public @NotNull Promise<?> start() {
        log.info("|PEER DISCOVERY SERVICE CLIENT STARTING|");
        return Promise.ofCallback(cb -> discoveryService.discover(null, (result, e) -> {
            if (e == null) {
                this.peers.putAll(result);
                this.alivePeers.putAll(result);
                checkAllPeers().run(cb);
            } else {
                cb.setException(e);
            }
        }))
                .whenResult(this::rediscover);
    }

    @Override
    public @NotNull Promise<?> stop() {
        return Promise.complete().whenResult(() -> log.info("|PEER DISCOVERY SERVICE CLIENT STOPPED|"));
    }

    /**
     * Starts a check process, which pings all peers and marks them as dead or alive
     * accordingly
     *
     * @return promise of the check
     */
    public Promise<Void> checkAllPeers() {
        return checkAllPeers.run().whenComplete(toLogger(log, "checkAllPeers"));
    }

    /**
     * Relanches the discovery process
     */
    private void rediscover() {
        discoveryService.discover(peers, (result, e) -> {
            if (e == null) {
                updatePeers(result);
                checkAllPeers().whenResult(this::rediscover);
            } else {
                log.warn("Could not discover peers", e);
                eventloop.delayBackground(Duration.ofSeconds(PING_INTERVAL), this::rediscover);
            }
        });
    }

    /**
     * Starts a check process, which pings all dead peers and marks them as alive if
     * they respond
     *
     * @return promise of the check
     */
    private void updatePeers(Map<Object, Peer> newPeers) {
        peers.clear();
        peers.putAll(newPeers);

        alivePeers.keySet().retainAll(peers.keySet());
        deadPeers.keySet().retainAll(peers.keySet());

        for (Entry<Object, Peer> entry : peers.entrySet()) {
            Object peerId = entry.getKey();
            Peer peer = entry.getValue();

            Peer deadPeer = deadPeers.get(peerId);
            if (deadPeer != null) {
                if (deadPeer == peer)
                    continue;

                deadPeers.remove(peerId);
            }
            alivePeers.put(peerId, peer);
        }

        alivePeers.clear();
        deadPeers.clear();
    }

    /**
     * Starts a check process, which pings all peers and marks them as alive if they
     * respond
     * 
     * @return
     */
    private Promise<Void> doCheckAllPeers() {
        return Promises.all(
                peers.entrySet().stream()
                        .map(entry -> {
                            Object id = entry.getKey();
                            return entry.getValue()
                                    .ping()
                                    .map((o, e) -> {
                                        if (e == null) {
                                            markAlive(id);
                                        } else {
                                            markDead(id, e);
                                        }
                                        return null;
                                    });
                        }));
    }

    /**
     * Starts a check process, which pings all dead peers and marks them as alive if
     * they respond
     * 
     * @return
     */
    private Promise<Void> doCheckDeadPeers() {
        return Promises.all(
                deadPeers.entrySet().stream()
                        .map(entry -> entry.getValue()
                                .ping()
                                .map((o, e) -> {
                                    if (e == null) {
                                        markAlive(entry.getKey());
                                    }
                                    return null;
                                })));
    }

    /**
     * Mark a peer as dead. It means that no operations will use it, and it would
     * not be given to the server selector.
     * Next call to {@link #checkDeadPeers()} or {@link #checkAllPeers()} will ping
     * this peer and possibly
     * mark it as alive again.
     *
     * @param peerId id of the peer to be marked
     * @param e      optional exception for logging
     * @return <code>true</code> if peer was alive and <code>false</code> otherwise
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean markDead(Object peerId, @Nullable Exception e) {
        Peer peer = alivePeers.remove(peerId);
        if (peer != null) {
            log.warn("marking {} as dead ", peerId, e);
            deadPeers.put(peerId, peer);
            return true;
        }
        return false;
    }

    public void markAlive(Object peerId) {
        Peer peer = deadPeers.remove(peerId);
        if (peer != null) {
            log.info("Peer {} is alive again!", peerId);
            alivePeers.put(peerId, peer);
        }
    }
}
