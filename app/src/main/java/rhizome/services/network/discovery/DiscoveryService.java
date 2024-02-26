package rhizome.services.network.discovery;

import io.activej.async.callback.Callback;
import rhizome.net.p2p.PeerSystem;
import rhizome.net.p2p.peer.Peer;

import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public interface DiscoveryService {

    void discover(@Nullable Map<Object, Peer> previous, Callback<Map<Object, Peer>> cb);

    static DiscoveryService randomized(Map<Object, Peer> peers, PeerSystem peerSystem) {

		Map<Object, Peer> initialPeers = Collections.unmodifiableMap(new HashMap<>(peers));
		Map<Object, Peer> currentPeers = new HashMap<>(initialPeers);

		return (newPeers, cb) -> {


			
			if (!initialPeers.equals(newPeers)) {
				cb.accept(newPeers, null);
			}
		};
	}
}
