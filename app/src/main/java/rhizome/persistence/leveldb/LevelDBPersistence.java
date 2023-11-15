package rhizome.persistence.leveldb;

import org.iq80.leveldb.DBException;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.ReadOptions;
import org.iq80.leveldb.WriteOptions;

import io.activej.bytebuf.ByteBuf;
import io.activej.bytebuf.ByteBufPool;
import io.activej.common.MemSize;
import rhizome.core.block.Block;
import rhizome.core.block.BlockHeader;
import rhizome.core.common.Utils.PublicWalletAddress;
import rhizome.core.common.Utils.SHA256Hash;
import rhizome.core.transaction.Transaction;
import rhizome.core.transaction.TransactionInfo;
import rhizome.persistence.BlockPersistence;

import static rhizome.core.transaction.TransactionInfo.TRANSACTIONINFO_BUFFER_SIZE;
import static rhizome.core.block.BlockHeader.BLOCKHEADER_BUFFER_SIZE;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LevelDBPersistence extends DataStore implements BlockPersistence {

    static final String BLOCK_COUNT_KEY = "BLOCK_COUNT";
    static final String TOTAL_WORK_KEY = "TOTAL_WORK";

    public LevelDBPersistence(String path) throws IOException {
        super.init(path);
    }

    public void setBlockCount(long count) {
        set(BLOCK_COUNT_KEY, count);
    }

    public long getBlockCount() {
        return (long) get(BLOCK_COUNT_KEY, Long.class);
    }

    public void setTotalWork(BigInteger count) {
        set(TOTAL_WORK_KEY, count);
    }

    public BigInteger getTotalWork() {
        return (BigInteger) get(TOTAL_WORK_KEY, BigInteger.class);
    }

    public boolean hasBlockCount() {
        return hasKey(BLOCK_COUNT_KEY);        
    }

    public boolean hasBlock(int blockId) {
        return hasKey(blockId);
    }

    public BlockHeader getBlockHeader(int blockId) {       
        return BlockHeader.of(getBlockHeadeAsByteBuf(blockId));
    }
    public ByteBuf getBlockHeadeAsByteBuf(int blockId) {       
        return (ByteBuf) get(blockId, ByteBuf.class);
    }

    public List<TransactionInfo> getBlockTransactions(BlockHeader block) {
        var transactions = new ArrayList<TransactionInfo>();
        for (int i = 0; i < block.numTranactions(); i++) {            
            var value = (ByteBuf) get(composeKey(block.id(), i), ByteBuf.class);
            transactions.add(TransactionInfo.of(value));
        }
        return transactions;
    }

    public ByteBuf getRawData(int blockId) {
        var blockHeader = getBlockHeader(blockId);
        var bufferSize = MemSize.of((long) BLOCKHEADER_BUFFER_SIZE + (TRANSACTIONINFO_BUFFER_SIZE * blockHeader.numTranactions()));
        var buffer = ByteBufPool.allocateExact(bufferSize);
        buffer.put(blockHeader.toBuffer());

        getBlockTransactions(blockHeader).forEach(transaction -> buffer.put(transaction.toBuffer()));
        buffer.head(0);
        return buffer;
    }

    public Block getBlock(int blockId) {
        BlockHeader block = getBlockHeader(blockId);
        List<Transaction> transactions = new ArrayList<>();
        getBlockTransactions(block).forEach(transaction -> transactions.add(Transaction.of(transaction)));
        return Block.of(block, transactions);
    }


    public List<SHA256Hash> getTransactionsForWallet(PublicWalletAddress wallet) {
        var startKey = new WalletTransactionKey(wallet, null, true);
        var endKey = new WalletTransactionKey(wallet, null, false);
        
        List<SHA256Hash> transactions = new ArrayList<>();

        try (DBIterator iterator = getDb().iterator(new ReadOptions())) {
            for(iterator.seek(startKey.toByteArray()); iterator.hasNext(); iterator.next()) {
                byte[] key = iterator.peekNext().getKey();
                if (Arrays.compare(key, endKey.toByteArray()) >= 0) {
                    break;
                }
                byte[] txidBytes = Arrays.copyOfRange(key, 25, 57);
                SHA256Hash txid = new SHA256Hash(txidBytes);
                transactions.add(txid);
            }
        } catch (IOException e) {
            throw new DataStoreException("Failed to iterate over the database", e);
        }
        
        return transactions;
    }

    public void removeBlockWalletTransactions(Block block) {
        for(Transaction t : block.getTransactions()) {
            SHA256Hash txid = t.hashContents();
            
            var w1Key = new WalletTransactionKey(t.getFrom(), txid, true);
            var w2Key = new WalletTransactionKey(t.getTo(), txid, true);
            
        try {
            deleteTransaction(w1Key);
            deleteTransaction(w2Key);
        } catch (DBException e) {
            throw new DataStoreException("Could not remove transaction from wallet in blockstore db: " + e.getMessage(), e);
        }
        }
    }

    private void deleteTransaction(WalletTransactionKey key) throws DBException {
        WriteOptions writeOptions = new WriteOptions().sync(true);
        getDb().delete(key.toByteArray(), writeOptions);
    }


    public void addBlock(Block block) throws DataStoreException {
        set(block.getId(), block.serialize().toBuffer().asArray());

        for (int i = 0; i < block.getTransactions().size(); i++) {
            var transaction = block.getTransactions().get(i);
            var transactionInfo = transaction.serialize();
            set(composeKey(block.getId(), i), transactionInfo.toBuffer().asArray());
            set(WalletTransactionKey.key(transactionInfo.from(), transaction.hashContents()), new byte[0]);
            set(WalletTransactionKey.key(transactionInfo.to(), transaction.hashContents()), new byte[0]);
        }
    }

    private static class WalletTransactionKey {
        PublicWalletAddress addr;
        SHA256Hash txId;
        
        public WalletTransactionKey(PublicWalletAddress address, SHA256Hash txId, boolean isStartKey) {
            this.addr = address;
            this.txId = txId;

            if(isStartKey) {
                Arrays.fill(this.txId.hash, (byte) 0);
            } else {
                Arrays.fill(this.txId.hash, (byte) 255);
            }

            if(txId.hash != null) {
                System.arraycopy(txId.hash, 0, this.txId.hash, 0, txId.hash.length);
            }
        }
                
        byte[] toByteArray() {
            return key(addr, txId);
        }

        static byte[] key(PublicWalletAddress address, SHA256Hash sha256) {
            return composeKey(address.address().getArray(), sha256.hash);
        }
    }
}
