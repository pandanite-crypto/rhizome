package rhizome.core.peer;

import java.util.List;
import java.util.Map;
import java.util.Set;

import rhizome.core.blockchain.HeaderChain;
import rhizome.persistence.BlockPersistence;

public abstract class AbstractPeer implements Peer {
    protected List<HeaderChain> currPeers; 
    protected BlockPersistence blockStore;
    protected final Object lock = new Object();
    protected boolean disabled;
    protected boolean firewall;
    protected String ip;
    protected int port;
    protected String name;
    protected String address;
    protected String version;
    protected String minHostVersion;
    protected String networkName;
    protected Map<String, Long> hostPingTimes;
    protected Map<String, Integer> peerClockDeltas;
    protected Map<Long, byte[]> checkpoints;
    protected Map<Long, byte[]> bannedHashes;
    protected List<String> hostSources;
    protected List<String> hosts;
    protected Set<String> blacklist;
    protected Set<String> whitelist;
}
