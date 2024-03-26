package rhizome.net.p2p.peer;

import java.net.InetSocketAddress;
import java.util.UUID;

import io.activej.promise.Promise;
import rhizome.net.p2p.gossip.DiscoveryPeer;

public interface Peer {
    
    /**
     * 
     * @param address
     * @return
     */
    public static Peer initLocalPeer(InetSocketAddress address) {
        return new DiscoveryPeer(UUID.randomUUID(), address, PeerState.JOIN, System.currentTimeMillis() / 1000, 0, 0);
    }

    /**
     * 
     * @param address
     * @return
     */
    public static Peer fromAddress(InetSocketAddress address) {
        var peerChannel = PeerChannel.init(address);
        return new DiscoveryPeer(UUID.randomUUID(), address, PeerState.DISCONNECTED, System.currentTimeMillis() / 1000, 0, 0);
    }

    /**
     * 
     * @param startRequestTime
     * @return
     */
    public Peer refresh(long startRequestTime);

    public default Promise<Void> ping() {
        return Promise.ofCallback(cb -> getPeerChannel().getOutput().ping());
    }

    PeerChannel getPeerChannel();
    InetSocketAddress address();
    UUID id();
}
