package rhizome.services.network;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rhizome.core.common.Pair;
import rhizome.persistence.BlockPersistence;

public interface PeerManager {
        int size();
    String computeAddress();
    void refreshHostList();
    void startPingingPeers();
    String getGoodHost();
    long getBlockCount();
    BigInteger getTotalWork();
    byte[] getBlockHash(String host, long blockId);
    Map<String, Pair<Long, String>> getHeaderChainStats();
    Pair<String, Long> getRandomHost();
    List<String> getHosts(boolean includeSelf);
    Set<String> sampleFreshHosts(int count);
    Set<String> sampleAllHosts(int count);
    String getAddress();
    long getNetworkTimestamp();
    void setBlockstore(BlockPersistence blockStore);
    void addPeer(String addr, long time, String version, String network);
    boolean isDisabled();
    void syncHeadersWithPeers();
}
