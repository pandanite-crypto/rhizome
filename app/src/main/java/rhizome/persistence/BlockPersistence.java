package rhizome.persistence;

import java.math.BigInteger;
import java.util.List;

import rhizome.core.block.Block;
import rhizome.core.block.dto.BlockDto;
import rhizome.core.common.Utils.PublicWalletAddress;
import rhizome.core.common.Utils.SHA256Hash;
import rhizome.core.transaction.dto.TransactionDto;

public interface BlockPersistence {
    long getBlockCount();

    BigInteger getTotalWork();

    boolean hasBlock(int blockId);

    BlockDto getBlockHeader(int blockId);

    List<TransactionDto> getBlockTransactions(BlockDto block);

    Block getBlock(int blockId);

    List<SHA256Hash> getTransactionsForWallet(PublicWalletAddress wallet);

    void removeBlockWalletTransactions(Block block);
    
    void addBlock(Block block);
}
