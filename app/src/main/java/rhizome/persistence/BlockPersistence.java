package rhizome.persistence;

import java.math.BigInteger;
import java.util.List;

import rhizome.core.block.Block;
import rhizome.core.block.BlockHeader;
import rhizome.core.common.Utils.PublicWalletAddress;
import rhizome.core.common.Utils.SHA256Hash;
import rhizome.core.transaction.TransactionInfo;

public interface BlockPersistence {
    long getBlockCount();

    BigInteger getTotalWork();

    boolean hasBlock(int blockId);

    BlockHeader getBlockHeader(int blockId);

    List<TransactionInfo> getBlockTransactions(BlockHeader block);

    Block getBlock(int blockId);

    List<SHA256Hash> getTransactionsForWallet(PublicWalletAddress wallet);

    void removeBlockWalletTransactions(Block block);
    
    void addBlock(Block block);
}
