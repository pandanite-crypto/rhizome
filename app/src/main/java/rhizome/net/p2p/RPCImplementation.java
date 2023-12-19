package rhizome.net.p2p;

import java.util.UUID;

public class RPCImplementation implements RPCInterface {
    private final DHTNetwork network;

    public RPCImplementation(DHTNetwork network) {
        this.network = network;
    }

    @Override
    public String findNode(UUID nodeId) {
        // Implémentation de la recherche de nœud
        return "Node details";
    }

    @Override
    public void store(UUID key, String value) {
        network.store(key, value);
    }

    @Override
    public String retrieve(UUID key) {
        return network.retrieve(key);
    }

}
