package rhizome.net.p2p;

import io.activej.eventloop.Eventloop;
import io.activej.rpc.client.RpcClient;
import io.activej.inject.annotation.Inject;
import io.activej.inject.annotation.Provides;
import io.activej.launcher.Launcher;
import io.activej.service.ServiceGraphModule;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DHTClient {
    private final Node node;

    @Inject
    RpcClient client;

    public DHTClient(Node node) {
        this.node = node;
    }

    @Provides
    RpcClient rpcClient(Eventloop eventloop) {
        return RpcClient.create(eventloop)
                .withMessageTypes(String.class, UUID.class)
                .withConnectTimeout(Duration.ofSeconds(10))
                .withReconnectInterval(Duration.ofSeconds(10))
                .withRemoteAddress(new InetSocketAddress(node.getAddress(), 8080));
    }

    @Provides
    Eventloop eventloop() {
        return Eventloop.create().withCurrentThread();
    }

    public CompletableFuture<String> findNode(UUID nodeId) {
        return client.sendRequest(nodeId, 5000L); // Send a request with a timeout
    }

    // public static void main(String[] args) throws Exception {
    //     Node node = new Node(UUID.randomUUID(), "remote-node-address"); // Replace with actual server address
    //     DHTClient client = new DHTClient(node);
    //     client.launch(args);

    //     // Example usage
    //     client.findNode(UUID.randomUUID()).thenAccept(response -> {
    //         System.out.println("Response: " + response);
    //     });
    // }
}