package rhizome.net.p2p;

import java.util.UUID;

public class Node {
    private final UUID id;
    private final String address;

    public Node(UUID id, String address) {
        this.id = id;
        this.address = address;
    }

    public UUID getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }
}
