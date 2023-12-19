package rhizome.net.p2p;

import java.util.UUID;

public interface RPCInterface {
    String findNode(UUID nodeId);
    void store(UUID key, String value);
    String retrieve(UUID key);
}
