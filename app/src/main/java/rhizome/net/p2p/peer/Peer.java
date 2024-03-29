package rhizome.net.p2p.peer;

import java.net.InetSocketAddress;
import java.util.UUID;

import io.activej.promise.Promise;
import lombok.Builder;
import lombok.Getter;

@Builder @Getter
public class Peer {
    
    private final UUID id;
    private final InetSocketAddress address;
    private final PeerState state;
    private final long lastPingTime;
    private final long clockDelta;
    private final long version;
    private final PeerChannel channel;

    /**
     * 
     * @param startRequestTime
     * @return
     */
    public Peer refresh(long startRequestTime){
        return new Peer(
            id, 
            address, 
            state, 
            lastPingTime, 
            System.currentTimeMillis() / 1000 - startRequestTime, 
            version, 
            channel
        );
    }

    public Promise<Void> ping() {
        return Promise.ofCallback(cb -> channel.getOutput().ping());
    }
}
