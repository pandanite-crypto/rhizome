package rhizome.net.protocol;

import java.net.InetSocketAddress;

import io.activej.datastream.StreamDataAcceptor;
import io.activej.net.socket.tcp.AsyncTcpSocket;
import lombok.Getter;
import lombok.Setter;
import rhizome.core.peer.MessageQueue;

@Getter
@Setter
public abstract class Channel {

    // Socket channel
    protected AsyncTcpSocket socketChannel;

    // Peer address
    protected InetSocketAddress inetSocketAddress;

    // Peer Stream
    protected PeerStream stream;
    protected boolean isActive;
    protected boolean isDisconnected = false;

    private StreamDataAcceptor<Message> downstreamDataAcceptor = null;


    public abstract InetSocketAddress getInetSocketAddress();

    public abstract boolean isActive();

    public abstract void setActive(boolean b);

    // public abstract Node getNode();

    // public abstract void sendNewBlock(BlockWrapper blockWrapper);

    public abstract void onDisconnect();

    public abstract void dropConnection();


    public abstract boolean isDisconnected();

    public abstract MessageQueue getMessageQueue();

}
