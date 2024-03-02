package rhizome.net.p2p.peer;

import java.util.HashMap;
import java.util.Map;

import io.activej.csp.queue.ChannelBuffer;
import io.activej.promise.Promise;
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
    final protected Peer peer;

    // Message queue for the peer
    final protected ChannelBuffer<Message> messageQueue = new ChannelBuffer<>(5, 10);

    // Handlers for messages
    final static Map<MessageCode, MessageHandler> messageHandlers = new HashMap<>();

    // Current state of the peer connection
    private PeerOutput output;
	private PeerInput input;

    // Protocol used to communicate with the peer
    protected Protocol protocol;

    // Stats of current peer connection
    protected PeerStats stats;

    public static Promise<PeerChannel> connect(Peer peer, PeerOutput output) {
        return Promise.of(builder()
            .peer(peer)
            .output(output)
            .build());
    }

    public static Promise<PeerChannel> connect(Peer peer, PeerInput input) {
        return Promise.of(builder()
            .peer(peer)
            .input(input)
            .build());
    }

    public interface PeerOutput {
        void ping();
    }

    public interface PeerInput {}
}
