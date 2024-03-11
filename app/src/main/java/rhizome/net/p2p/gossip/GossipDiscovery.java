package rhizome.net.p2p.gossip;

import io.activej.async.callback.Callback;
import io.activej.promise.Promise;
import io.activej.promise.Promises;
import lombok.Builder;
import rhizome.net.p2p.DiscoveryService;
import rhizome.net.p2p.PeerSystem;
import rhizome.net.p2p.peer.Peer;
import java.net.InetSocketAddress;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Builder
public class GossipDiscovery implements DiscoveryService {
	private final PeerSystem peerSystem;
	private Exception error;
	private Map<Object, List<InetSocketAddress>> discovered;
	private Map<Object, Peer> totalDiscovered;

	@Override
	public void discover(@Nullable Map<Object, Peer> previous, Callback<Map<Object, Peer>> cb) {

		previous.values().stream()
			.map(this::doDiscover)
			.forEach(
				p -> p.whenComplete((result, e) -> {
					if (e == null) {
						onDiscover(result);
					} else {
						onError(e, cb);
					}
				})
			);

		if (error != null) {
			cb.accept(null, error);
		}

		if (discovered.size() == totalDiscovered.size() && !totalDiscovered.equals(previous)) {
			cb.accept(totalDiscovered, null);
		}
	}

	private Promise<Map<Object, Peer>> doDiscover(Peer peer) {
		return peerSystem.getPeers(peer);
	}

	private void onError(@NotNull Exception e, Callback<Map<Object, Peer>> cb) {
		error = e;
		if (error != null) {
			cb.accept(null, error);
		} else {
			cb.accept(discovered, error);
		}
	}

	private void onDiscover(Map<Object, Peer> discovered) {
		Map<Object, Peer> old = this.discovered.put(discovered);

		Map<Object, Peer> newTotalDiscovered = new HashMap<>(totalDiscovered);
		if (old != null) {
			newTotalDiscovered.keySet().removeAll(old.keySet());
		}
		newTotalDiscovered.putAll(discovered);
		this.totalDiscovered = Collections.unmodifiableMap(newTotalDiscovered);

		if (discovered.size() == discoveryServicesSize) {
			completeCallbacks();
		}
	}
}
