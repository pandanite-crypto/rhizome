package rhizome.net.transport;

import java.util.HashMap;
import java.util.Map;
import io.activej.csp.queue.ChannelBuffer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import rhizome.net.p2p.peer.Peer;
import rhizome.net.p2p.peer.Protocol;
import rhizome.net.protocol.Message;
import rhizome.net.protocol.MessageCode;
import rhizome.net.protocol.MessageHandler;

@Getter
@Setter
@Builder
@Slf4j
public class TransportChannel {

    // Message queue for the peer
    private final ChannelBuffer<Message> messageQueue = new ChannelBuffer<>(5, 10);

    // Handlers for messages
    static final Map<MessageCode, MessageHandler> messageHandlers = new HashMap<>();

    // Current state of the peer connection
    private ChannelOutput output;
	private ChannelInput input;

    // Protocol used to communicate with the peer
    protected Protocol protocol;

    // Stats of current peer connection
    protected ChannelStats stats;
    
    public static TransportChannel connect(Peer peer) {
        log.info("Connecting to peer: {}", peer);

        // Create a new peer channel
        return TransportChannel.builder()
            .output(new ChannelOutput() {
                @Override
                public void ping() {
                    log.info("Pinging peer: {}", peer);
                }

                @Override
                public boolean isClosed() {
                    return true;
                }
            
            })
            .build();
    }

    // Initialize the peer channel
    public static TransportChannel init(ChannelOutput output) {
        return builder()
            .output(output)
            .build();
    }

    public static TransportChannel init(ChannelInput input) {
        return builder()
            .input(input)
            .build();
    }

    public interface ChannelOutput {
        void ping();
        boolean isClosed();
    }

    public interface ChannelInput {}
}
