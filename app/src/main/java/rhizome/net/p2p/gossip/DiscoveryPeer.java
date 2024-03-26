package rhizome.net.p2p.gossip;

import java.net.InetSocketAddress;
import java.util.UUID;

import rhizome.net.p2p.peer.Peer;
import rhizome.net.p2p.peer.PeerChannel;
import rhizome.net.p2p.peer.PeerState;

public record DiscoveryPeer(
        UUID id,
        InetSocketAddress address,
        PeerState state,
        long lastPingTime,
        long clockDelta,
        long version,
        PeerChannel peerChannel)
    implements Peer {

    @Override
    public Peer refresh(long startRequestTime) {
        return new DiscoveryPeer(id, address, state, System.currentTimeMillis() / 1000,
                System.currentTimeMillis() / 1000 - startRequestTime, version, peerChannel);
    }

    @Override
    public PeerChannel getPeerChannel() {
        return peerChannel;
    }
}