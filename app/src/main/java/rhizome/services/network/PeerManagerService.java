package rhizome.services.network;

import java.util.HashMap;
import java.util.Map;
import io.activej.eventloop.Eventloop;
import rhizome.net.p2p.DiscoveryService;
import rhizome.net.p2p.peer.Peer;
import rhizome.net.protocol.Message;

import static java.util.Collections.unmodifiableMap;

/**
 * This service execute the service manager process.
 * It pings all peers and marks them as dead or alive accordingly.
 * It also pings all dead peers and marks them as alive if they respond.
 * It also rediscover the peers.
 */
public interface PeerManagerService {

    static final int PING_INTERVAL = 10;

    static final Map<Object, Peer> peers = new HashMap<>();
    static final Map<Object, Peer> peersView = unmodifiableMap(peers);

    static final Map<Object, Peer> alivePeers = new HashMap<>();
    static final Map<Object, Peer> alivePeersView = unmodifiableMap(alivePeers);

    static final Map<Object, Peer> deadPeers = new HashMap<>();
    static final Map<Object, Peer> deadPeersView = unmodifiableMap(deadPeers);

    static final Map<Object, Peer> connectedPeers = new HashMap<>();
    static final Map<Object, Peer> connectedPeersView = unmodifiableMap(connectedPeers);

    /**
     * Factory method
     * 
     * @param eventloop
     * @param discoveryService
     * @return new instance of PeerManagerService
     */
    static PeerManagerService create(Eventloop eventloop, DiscoveryService discoveryService) {
        return new PeerManagerServiceImpl(eventloop, discoveryService);
    }

    /**
     * Publishes a message to connected peers following the strategy of the peer
     * system
     * 
     * @param message
     */
    void broadcast(Message message);


    /*
     * 
     * 
     */
    void sync();
}
