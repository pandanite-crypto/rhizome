package rhizome.core.peer;

import lombok.Getter;
import lombok.Setter;
import rhizome.services.network.discovery.PeerDiscoveryService.DiscoveryPeer;

@Getter
@Setter
public class PeerChannel {

    // Reference of Peer in DiscoveryService
    protected DiscoveryPeer peer;


    protected Protocol protocol;
    protected PeerStats stats;
    protected boolean isActive;
    protected boolean isDisconnected = false;
    protected MessageQueue messageQueue;

}
