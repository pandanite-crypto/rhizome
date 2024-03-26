package rhizome.net.p2p.gossip;

import io.activej.async.callback.Callback;
import io.activej.promise.Promise;
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

		// Initialize the discovered addresses list
		previous.values().stream()
			// Call the PeerSystem getPeers method
			.map(this::doDiscover)
			// Merge logic for the discovered address of each peer
			.forEach(
				p -> p.whenComplete((result, e) -> {
					if (e == null) {
						onDiscover(result);
					} else {
						onError(e, cb);
					}
				})
			);

		// Error handling
		if (error != null) {
			cb.accept(null, error);
		}

		// Check if the discovered addresses are the same as the previous ones
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

	/**
	 * Merge logic for the discovered address of each peer
	 * @param discovered
	 */
	private void onDiscover(List<InetSocketAddress> discovered) {

		// Keep local track of the instance's old discovered addresses
		List<InetSocketAddress> old = new ArrayList<>(this.discovered);

		// Add the new discovered addresses to the instance list
		this.discovered.addAll(discovered);

		// Copy the old discovered peers maps to the new total discovered peers
		Map<Object, Peer> newTotalDiscovered = new HashMap<>(totalDiscovered);

		// Remove the old discovered addresses from the new total discovered peers
		if (old != null) {
			newTotalDiscovered.keySet().removeAll(old);
		}

		// Add the new discovered addresses to the new total discovered peers
		discovered.forEach(address -> newTotalDiscovered.put(address, Peer.fromAddress(peerSystem.cluster(), address)));
		
		// Update the total discovered peers
		this.totalDiscovered = Collections.unmodifiableMap(newTotalDiscovered);
	}

	/**
	 * Error handling
	 * @param e
	 * @param cb
	 */
	private void onError(@NotNull Exception e, Callback<Map<Object, Peer>> cb) {
		error = e;
		if (error != null) {
			cb.accept(null, error);
		} else {
			Map<Object, Peer> totalDiscoveredMap = new HashMap<>();
			discovered.forEach(address -> totalDiscoveredMap.put(address, null));
			cb.accept(totalDiscoveredMap, error);
		}
	}
}
