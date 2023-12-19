package rhizome.net.p2p;

import io.activej.eventloop.Eventloop;
import io.activej.rpc.server.RpcServer;
import io.activej.inject.annotation.Inject;
import io.activej.inject.annotation.Provides;
import io.activej.service.ServiceGraphModule;

import java.net.InetSocketAddress;
import java.util.UUID;

public class DHTServer {
    private final Node node;
    private final RPCImplementation rpcImplementation;

    @Inject
    RpcServer server;

    public DHTServer(Node node, RPCImplementation rpcImplementation) {
        this.node = node;
        this.rpcImplementation = rpcImplementation;
    }

    @Provides
    RpcServer rpcServer(Eventloop eventloop) {
        return RpcServer.create(eventloop)
                .withMessageTypes(String.class, UUID.class) 
                .withHandler(RPCInterface.class, rpcImplementation) 
                .withListenPort(new InetSocketAddress(node.getAddress(), 8080));
    }

    // public static void main(String[] args) throws Exception {
    //     Node node = new Node(UUID.randomUUID(), "localhost");
    //     DHTNetwork network = new DHTNetwork();
    //     RPCImplementation rpcImplementation = new RPCImplementation(network);

    //     DHTServer server = new DHTServer(node, rpcImplementation);
    //     server.launch(args);
    // }
}
