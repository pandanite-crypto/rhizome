package rhizome.persistence;

import java.math.BigInteger;
import java.util.List;


import io.activej.bytebuf.ByteBuf;
import rhizome.core.block.Block;
import rhizome.core.block.BlockHeader;
import rhizome.core.common.Utils.PublicWalletAddress;
import rhizome.core.common.Utils.SHA256Hash;
import rhizome.core.transaction.TransactionInfo;

public interface BlockPersistence {
    void setBlockCount(long count) throws PersistenceException;
    long getBlockCount() throws PersistenceException;
    void setTotalWork(BigInteger count) throws PersistenceException;
    BigInteger getTotalWork() throws PersistenceException;
    boolean hasBlockCount() throws PersistenceException;
    boolean hasBlock(int blockId) throws PersistenceException;
    BlockHeader getBlockHeader(int blockId) throws PersistenceException;
    List<TransactionInfo> getBlockTransactions(BlockHeader block) throws PersistenceException;
    ByteBuf getRawData(int blockId) throws PersistenceException;
    Block getBlock(int blockId) throws PersistenceException;
    List<SHA256Hash> getTransactionsForWallet(PublicWalletAddress wallet) throws PersistenceException;
    void removeBlockWalletTransactions(Block block) throws PersistenceException;
    void setBlock(Block block) throws PersistenceException;
}
