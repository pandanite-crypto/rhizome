package rhizome.net.p2p.peer;

import java.util.HashMap;
import java.util.Map;
import io.activej.csp.queue.ChannelBuffer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import rhizome.net.protocol.Message;
import rhizome.net.protocol.MessageCode;
import rhizome.net.protocol.MessageHandler;

@Getter
@Setter
@Builder
@Slf4j
public class PeerChannel {

    // Reference to the peer
    private Peer peer;

    // Message queue for the peer
    private final ChannelBuffer<Message> messageQueue = new ChannelBuffer<>(5, 10);

    // Handlers for messages
    static final Map<MessageCode, MessageHandler> messageHandlers = new HashMap<>();

    // Current state of the peer connection
    private PeerOutput output;
	private PeerInput input;

    // Protocol used to communicate with the peer
    protected Protocol protocol;

    // Stats of current peer connection
    protected PeerStats stats;
    
    // Initialize the peer channel
    public static PeerChannel init(Peer peer, PeerOutput output) {
        return builder()
            .peer(peer)
            .output(output)
            .build();
    }

    public static PeerChannel init(Peer peer, PeerInput input) {
        return builder()
            .peer(peer)
            .input(input)
            .build();
    }

    public interface PeerOutput {
        void ping();
    }

    public interface PeerInput {}
}
