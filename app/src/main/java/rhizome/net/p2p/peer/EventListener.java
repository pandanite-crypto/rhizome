package rhizome.net.p2p.peer;

import io.activej.promise.Promise;

@FunctionalInterface
public interface EventListener {
    Promise<Void> fire(Peer peer, PeerState state, Object payload);
}
