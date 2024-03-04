package rhizome.net.p2p.gossip;

import io.activej.async.callback.Callback;
import lombok.Builder;
import rhizome.net.p2p.DiscoveryService;
import rhizome.net.p2p.PeerSystem;
import rhizome.net.p2p.peer.Peer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Builder
public class GossipDiscovery implements DiscoveryService {
	private final PeerSystem peerSystem;
	private Exception error;

	@Override
	public void discover(@Nullable Map<Object, Peer> previous, Callback<Map<Object, Peer>> cb) {
		// if (error != null) {
		// 	cb.accept(null, error);
		// }

		// if (discovered.size() == discoveryServicesSize && !totalDiscovered.equals(previous)) {
		// 	cb.accept(totalDiscovered, null);
		// }

		// callbacks.add(cb);
	}

	private void doDiscover(Peer discoveryService) {
		// discoveryService.discover(discovered.get(discoveryService), (result, e) -> {
		// 	if (error != null) return;

		// 	if (e == null) {
		// 		onDiscover(discoveryService, result);
		// 	} else {
		// 		onError(e);
		// 	}
		// });
	}

	private void onError(@NotNull Exception e) {
		error = e;
		completeCallbacks();
	}

	private void onDiscover(DiscoveryService discoveryService, Map<Object, Peer> discovered) {
		// Map<Object, Peer> old = this.discovered.put(discoveryService, discovered);

		// Map<Object, Peer> newTotalDiscovered = new HashMap<>(totalDiscovered);
		// if (old != null) {
		// 	newTotalDiscovered.keySet().removeAll(old.keySet());
		// }
		// newTotalDiscovered.putAll(discovered);
		// this.totalDiscovered = Collections.unmodifiableMap(newTotalDiscovered);

		// if (discovered.size() == discoveryServicesSize) {
		// 	completeCallbacks();
		// }
	}

	private void completeCallbacks() {
		// Set<Callback<Map<Object, Peer>>> callbacks = new HashSet<>(this.callbacks);
		// this.callbacks.clear();
		// if (error != null) {
		// 	callbacks.forEach(cb -> cb.accept(null, error));
		// } else {
		// 	callbacks.forEach(cb -> cb.accept(totalDiscovered, error));
		// }
	}
}
