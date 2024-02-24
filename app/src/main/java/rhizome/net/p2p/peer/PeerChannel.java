package rhizome.net.p2p.peer;

import java.util.HashMap;
import java.util.Map;

import io.activej.csp.queue.ChannelBuffer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import rhizome.net.protocol.Message;
import rhizome.net.protocol.MessageCode;
import rhizome.net.protocol.MessageHandler;

@Getter
@Setter
@Slf4j
public class PeerChannel {

    // Reference to the peer
    protected Peer peer;

    // Message queue for the peer
    protected ChannelBuffer<Message> messageQueue = new ChannelBuffer<>(5, 10);

    // Handlers for messages
    static Map<MessageCode, MessageHandler> messageHandlers = new HashMap<>();

    // Current state of the peer connection
    private PeerClientConnection output;
	private PeerServerConnection input;

    // Protocol used to communicate with the peer
    protected Protocol protocol;

    // Stats of current peer connection
    protected PeerStats stats;

    // Whether the peer is active or not
    protected boolean isActive;
    protected boolean isDisconnected = false;
}
