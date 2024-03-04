package rhizome.net.p2p;
import java.util.List;
import java.util.Map;
import java.net.InetSocketAddress;

import io.activej.async.function.AsyncConsumer;
import io.activej.promise.Promise;
import rhizome.net.p2p.peer.Peer;
import rhizome.net.p2p.peer.PeerState;

public interface PeerSystem {

    // void start();

    // void notify(Peer member, PeerState state, Object payload);

    // AsyncConsumer<Peer> ping();

    // List<Peer> random();
    DiscoveryService create(Map<Object, Peer> peers, PeerSystem peerSystem);

    Promise<List<InetSocketAddress>> getPeers(Peer peer);
}
