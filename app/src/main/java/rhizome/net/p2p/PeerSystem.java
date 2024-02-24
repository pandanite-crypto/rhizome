package rhizome.net.p2p;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.activej.async.function.AsyncConsumer;
import rhizome.net.p2p.peer.Peer;
import rhizome.net.p2p.peer.PeerState;

public interface PeerSystem {
    
    Set<Peer> alivePeers = new HashSet<>();
    Set<Peer> deadPeers = new HashSet<>();
    Queue<Peer> candidatePeers = new ConcurrentLinkedQueue<>();
    Queue<Peer> endpointMembers = new ConcurrentLinkedQueue<>();

    void start();

    void notify(Peer member, PeerState state, Object payload);

    AsyncConsumer<Peer> ping();
}
