package rhizome.net.p2p.peer;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.UUID;

import io.activej.async.function.AsyncConsumer;
import io.activej.async.function.AsyncFunction;
import io.activej.promise.Promise;

public record Peer(String cluster, UUID id, InetSocketAddress address, PeerState state, long lastPingTime, long clockDelta, long version) {
    
    /**
     * 
     * @param cluster
     * @param address
     * @return
     */
    public static Peer initLocalPeer(String cluster, InetSocketAddress address) {
        return new Peer(cluster, UUID.randomUUID(), address, PeerState.JOIN, System.currentTimeMillis() / 1000, 0, 0);
    }

    /**
     * 
     * @param cluster
     * @param address
     * @return
     */
    public static Peer fromAddress(String cluster, InetSocketAddress address) {
        return new Peer(cluster, UUID.randomUUID(), address, PeerState.DISCONNECTED, System.currentTimeMillis() / 1000, 0, 0);
    }

    /**
     * 
     * @param startRequestTime
     * @return
     */
    public Peer refresh(long startRequestTime) {
        return new Peer(cluster, id, address, state, System.currentTimeMillis() / 1000, System.currentTimeMillis() / 1000 - startRequestTime, version);
    }

    /**
     * 
     * @param consumer
     * @return
     */
    public Promise<Void> ping(AsyncConsumer<Peer> consumer) {
		return consumer.accept(this);
	}

    public Promise<List<Peer>> discover(AsyncFunction<Peer, List<Peer>> function) {
        return function.apply(this);
    }

    public Promise<PeerChannel> connect() {
        return PeerChannel.connect(this);
    }
}
