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

/**
 * This class implements the DiscoveryService interface and provides a method to discover peers using the PeerSystem protocol.
 */
@Builder
public class GossipDiscovery implements DiscoveryService {
	private final PeerSystem peerSystem;
	private Exception error;
	private List<InetSocketAddress> discovered;
	private Map<Object, Peer> totalDiscovered;

	/**
	 * Main call of the interface. It discovers peers using the PeerSystem provided.
	 */
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

	/**
	 * Call the PeerSystem getPeers method
	 * @param peer
	 * @return
	 */
	private Promise<List<InetSocketAddress>> doDiscover(Peer peer) {
		return peerSystem.getPeers(peer);
	}

	
	private void onDiscover(List<InetSocketAddress> discovered) {
		List<InetSocketAddress> old = new ArrayList<>(this.discovered);
		this.discovered.addAll(discovered);

		Map<Object, Peer> newTotalDiscovered = new HashMap<>(totalDiscovered);
		if (old != null) {
			newTotalDiscovered.keySet().removeAll(old);
		}
		discovered.forEach(address -> newTotalDiscovered.put(address, Peer.fromAddress(peerSystem.cluster(), address)));
		this.totalDiscovered = Collections.unmodifiableMap(newTotalDiscovered);
	}

	private void onError(@NotNull Exception e, Callback<Map<Object, Peer>> cb) {
		error = e;
		if (error != null) {
			cb.accept(null, error);
		} else {
			cb.accept(discovered, error);
		}
	}
}
