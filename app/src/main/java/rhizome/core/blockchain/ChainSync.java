package rhizome.core.blockchain;

import rhizome.core.peer.Peer;

// TODO: basic translation from C++ , need refactor
public class ChainSync implements Runnable {
    private final Peer chain;

    public ChainSync(Peer chain) {
        this.chain = chain;
    }

    @Override
    public void run() {
        while (true) {
            if (!chain.isTriedBlockStoreCache() && chain.getBlockStore() != null) {
                long chainLength = chain.getBlockStore().getBlockCount();
                for (long i = 1; i <= chainLength; i++) {
                    chain.getBlockHashes().add(chain.getBlockStore().getBlock((int)i).getHash());
                }
                chain.setTotalWork(chain.getBlockStore().getTotalWork());
                chain.setChainLength(chainLength);
                chain.setTriedBlockStoreCache(true);
                chain.load();
            } else {
                chain.load();
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
