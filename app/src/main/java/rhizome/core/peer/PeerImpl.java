package rhizome.core.peer;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rhizome.core.common.Pair;
import rhizome.persistence.BlockPersistence;

public class PeerImpl extends AbstractPeer {

    @Override
    public int size() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'size'");
    }

    @Override
    public String computeAddress() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'computeAddress'");
    }

    @Override
    public void refreshHostList() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'refreshHostList'");
    }

    @Override
    public void startPingingPeers() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'startPingingPeers'");
    }

    @Override
    public String getGoodHost() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getGoodHost'");
    }

    @Override
    public long getBlockCount() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBlockCount'");
    }

    @Override
    public BigInteger getTotalWork() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTotalWork'");
    }

    @Override
    public byte[] getBlockHash(String host, long blockId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBlockHash'");
    }

    @Override
    public Map<String, Pair<Long, String>> getHeaderChainStats() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getHeaderChainStats'");
    }

    @Override
    public Pair<String, Long> getRandomHost() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRandomHost'");
    }

    @Override
    public List<String> getHosts(boolean includeSelf) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getHosts'");
    }

    @Override
    public Set<String> sampleFreshHosts(int count) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sampleFreshHosts'");
    }

    @Override
    public Set<String> sampleAllHosts(int count) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sampleAllHosts'");
    }

    @Override
    public String getAddress() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAddress'");
    }

    @Override
    public long getNetworkTimestamp() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getNetworkTimestamp'");
    }

    @Override
    public void setBlockstore(BlockPersistence blockStore) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setBlockstore'");
    }

    @Override
    public void addPeer(String addr, long time, String version, String network) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addPeer'");
    }

    @Override
    public boolean isDisabled() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isDisabled'");
    }

    @Override
    public void syncHeadersWithPeers() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'syncHeadersWithPeers'");
    }
    
}
