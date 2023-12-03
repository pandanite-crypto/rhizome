package rhizome.services.network.discovery;

import rhizome.services.network.discovery.PeerDiscoveryService.DiscoveryPeer;

public interface PeerDiscoveryListener {
    void onNewPeerDiscovered(DiscoveryPeer peer);
}
