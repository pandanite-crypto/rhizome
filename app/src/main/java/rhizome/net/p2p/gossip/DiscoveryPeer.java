package rhizome.net.p2p.gossip;

import java.net.InetSocketAddress;
import java.util.UUID;

import rhizome.net.p2p.peer.Peer;
import rhizome.net.p2p.peer.PeerState;

public record DiscoveryPeer(String cluster, UUID id, InetSocketAddress address, PeerState state, long lastPingTime, long clockDelta, long version) implements Peer {

    @Override
    public Peer refresh(long startRequestTime) {
        return new DiscoveryPeer(cluster, id, address, state, System.currentTimeMillis() / 1000, System.currentTimeMillis() / 1000 - startRequestTime, version);
    }
}