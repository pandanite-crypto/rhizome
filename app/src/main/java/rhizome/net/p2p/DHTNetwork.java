package rhizome.net.p2p;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DHTNetwork {
    private final Map<UUID, String> dhtTable;
    private final List<Node> neighbors;

    public DHTNetwork() {
        this.dhtTable = new ConcurrentHashMap<>();
        this.neighbors = new ArrayList<>();
    }

    public void store(UUID key, String value) {
        dhtTable.put(key, value);
    }

    public String retrieve(UUID key) {
        return dhtTable.get(key);
    }

    public void addNeighbor(Node neighbor) {
        neighbors.add(neighbor);
    }
}
