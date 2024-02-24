package rhizome.services.network.discovery;

import io.activej.async.callback.Callback;
import rhizome.net.p2p.peer.Peer;

import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public interface DiscoveryService {
    
    void discover(@Nullable Map<Object, Peer> previous, Callback<Map<Object, Peer>> cb);

    static DiscoveryService constant(Map<Object, Peer> peers) {
		Map<Object, Peer> constant = Collections.unmodifiableMap(new HashMap<>(peers));
		return (previous, cb) -> {
			if (!constant.equals(previous)) {
				cb.accept(constant, null);
			}
		};
	}
}
