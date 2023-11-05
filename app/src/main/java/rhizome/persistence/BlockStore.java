package rhizome.persistence;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBException;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.ReadOptions;
import org.iq80.leveldb.WriteOptions;

import io.activej.bytebuf.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import rhizome.core.block.Block;
import rhizome.core.block.BlockHeader;
import rhizome.core.block.BlockImpl;
import rhizome.core.common.Utils.PublicWalletAddress;
import rhizome.core.common.Utils.SHA256Hash;
import rhizome.core.transaction.Transaction;
import rhizome.core.transaction.TransactionImpl;
import rhizome.core.transaction.TransactionInfo;
import static rhizome.core.transaction.TransactionInfo.TRANSACTIONINFO_BUFFER_SIZE;
import static rhizome.core.block.BlockHeader.BLOCKHEADER_BUFFER_SIZE;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class BlockStore extends DataStore {

    static final String BLOCK_COUNT_KEY = "BLOCK_COUNT";
    static final String TOTAL_WORK_KEY = "TOTAL_WORK";

    public BlockStore(String path) throws IOException {
        super.init(path);
    }

    public void setBlockCount(long count) {
        getDb().put(
            BLOCK_COUNT_KEY.getBytes(UTF_8), 
            ByteBuffer.allocate(Long.BYTES).putLong(count).array(), 
            new WriteOptions().sync(true)
        );
    }

    public long getBlockCount() {
        var value = getDb().get(BLOCK_COUNT_KEY.getBytes(), new ReadOptions());
        if (value == null || value.length != Long.BYTES) {
            throw new BlockStoreException("Invalid block count value");
        }
        return ByteBuffer.wrap(value).getLong();
    }

    public void setTotalWork(BigInteger count) {
            getDb().put(TOTAL_WORK_KEY.getBytes(UTF_8), 
                count.toByteArray(),
                new WriteOptions().sync(true)
            );
    }

    public BigInteger getTotalWork() {
        var value = getDb().get(TOTAL_WORK_KEY.getBytes(), new ReadOptions());
        if (value == null) {
            throw new BlockStoreException("Invalid block count value");
        }
        return new BigInteger(new String(value, StandardCharsets.UTF_8));
    }

    public boolean hasBlockCount() {
        try {
            return getDb().get(BLOCK_COUNT_KEY.getBytes(UTF_8)) != null;
        } catch (DBException e) {
            log.error("Error checking block count", e);
            return false;
        }
    }

    public boolean hasBlock(int blockId) {
        try {
            return getDb().get(ByteBuffer.allocate(Integer.BYTES).putInt(blockId).array()) != null;
        } catch (DBException e) {
            log.error("Error checking block with ID: " + blockId, e);
            return false;
        }
    }

    public BlockHeader getBlockHeader(int blockId) {
        var key = ByteBuf.wrapForWriting(new byte[Integer.BYTES]);
        key.writeInt(blockId);
        
        var value = ByteBuf.wrapForReading(getDb().get(key.asArray(), new ReadOptions()));
        if (!value.canRead()) {
            throw new BlockStoreException("Could not read block header " + blockId + " from BlockStore db.");
        }

        return BlockHeader.of(value);
    }

    public List<TransactionInfo> getBlockTransactions(BlockHeader block) {
        var transactions = new ArrayList<TransactionInfo>();
        for (int i = 0; i < block.numTranactions(); i++) {
            var keyBuffer = ByteBuf.wrapForWriting(new byte[2 * Integer.BYTES]);
            keyBuffer.writeInt(block.id());
            keyBuffer.writeInt(i);
            
            var value = ByteBuf.wrapForReading(getDb().get(keyBuffer.asArray(), new ReadOptions()));
            if (!value.canRead()) {
                throw new BlockStoreException("Could not read transaction from BlockStore db.");
            }
            
            transactions.add(TransactionInfo.of(value));
        }
        return transactions;
    }

    public ByteBuf getRawData(int blockId) {
        var blockHeader = this.getBlockHeader(blockId);
        var buffer = ByteBuf.wrapForWriting(new byte[BLOCKHEADER_BUFFER_SIZE + (TRANSACTIONINFO_BUFFER_SIZE * blockHeader.numTranactions())]);
        buffer.put(blockHeader.toBuffer());

        for (int i = 0; i < blockHeader.numTranactions(); i++) {
            var key = ByteBuf.wrapForWriting(new byte[2 * Integer.BYTES]);
            key.writeInt(blockId);
            key.writeInt(i);

            var value = ByteBuf.wrapForReading(getDb().get(key.asArray(), new ReadOptions()));
            if (!value.canRead()) {
                throw new BlockStoreException("Could not read transaction from BlockStore db.");
            }
            buffer.put(TransactionInfo.of(value).toBuffer());
        }

        buffer.head(0);
        return buffer;
    }

    public Block getBlock(int blockId) {
        BlockHeader block = this.getBlockHeader(blockId);
        List<TransactionInfo> transactionInfos = this.getBlockTransactions(block);
        List<Transaction> transactions = new ArrayList<>();

        for (TransactionInfo t : transactionInfos) {
            transactions.add(Transaction.of(t));
        }
        return Block.of(block, transactions);
    }


    public List<SHA256Hash> getTransactionsForWallet(PublicWalletAddress wallet) {
        WalletTransactionKey startKey = new WalletTransactionKey(wallet.address().getArray(), null, true);
        WalletTransactionKey endKey = new WalletTransactionKey(wallet.address().getArray(), null, false);
        
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
            throw new BlockStoreException("Failed to iterate over the database", e);
        }
        
        return transactions;
    }

    public void removeBlockWalletTransactions(Block block) {
        for(Transaction t : block.getTransactions()) {
            SHA256Hash txid = t.hashContents();
            
            WalletTransactionKey w1Key = new WalletTransactionKey(((TransactionImpl) t).getFrom().address().getArray(), txid.hash, true);
            WalletTransactionKey w2Key = new WalletTransactionKey(((TransactionImpl) t).getTo().address().getArray(), txid.hash, true);
            
        try {
            deleteTransaction(w1Key);
            deleteTransaction(w2Key);
        } catch (DBException e) {
            throw new BlockStoreException("Could not remove transaction from wallet in blockstore db: " + e.getMessage(), e);
        }
        }
    }

    private void deleteTransaction(WalletTransactionKey key) throws DBException {
        WriteOptions writeOptions = new WriteOptions().sync(true);
        getDb().delete(key.toByteArray(), writeOptions);
    }


    public void setBlock(Block block) throws BlockStoreException {
        var blockImpl = (BlockImpl) block;
        try {
            DB db = getDb();

            int blockId = blockImpl.getId();
            ByteBuffer keyBuffer = ByteBuffer.allocate(Integer.BYTES);
            keyBuffer.putInt(blockId);
            byte[] key = keyBuffer.array();
            
            BlockHeader blockStruct = block.serialize();
            byte[] value = blockStruct.toBuffer().getArray();
            db.put(key, value, new WriteOptions().sync(true));

            for (int i = 0; i < block.getTransactions().size(); i++) {
                ByteBuffer txKeyBuffer = ByteBuffer.allocate(2 * Integer.BYTES);
                txKeyBuffer.putInt(blockId);
                txKeyBuffer.putInt(i);
                byte[] txKey = txKeyBuffer.array();
                
                TransactionInfo t = block.getTransactions().get(i).serialize();
                byte[] txValue = t.toBuffer().getArray();
                db.put(txKey, txValue, new WriteOptions().sync(true));

                // Ajout des transactions aux portefeuilles (from et to)
                byte[] txid = block.getTransactions().get(i).hashContents().hash;
                byte[] w1Key = new byte[25 + 32];
                byte[] w2Key = new byte[25 + 32];

                System.arraycopy(t.from().address().getArray(), 0, w1Key, 0, 25);
                System.arraycopy(txid, 0, w1Key, 25, 32);
                System.arraycopy(t.to().address().getArray(), 0, w2Key, 0, 25);
                System.arraycopy(txid, 0, w2Key, 25, 32);

                db.put(w1Key, new byte[0], new WriteOptions().sync(true));
                db.put(w2Key, new byte[0], new WriteOptions().sync(true));
            }
        } catch (Exception e) {
            log.error("Could not write block to BlockStore db: ", e);
            throw new BlockStoreException("Could not write block to BlockStore db: " + e.getMessage(), e);
        }
    }

    private static class WalletTransactionKey {
        byte[] addr = new byte[25];
        byte[] txId = new byte[32];
        
        public WalletTransactionKey(byte[] walletData, byte[] transactionId, boolean isStartKey) {
            System.arraycopy(walletData, 0, this.addr, 0, walletData.length);
            if(isStartKey) {
                Arrays.fill(this.txId, (byte) 0);
            } else {
                Arrays.fill(this.txId, (byte) 255);
            }
            if (transactionId != null) {
                System.arraycopy(transactionId, 0, this.txId, 0, transactionId.length);
            }
        }
        
        public byte[] toByteArray() {
            ByteBuffer buffer = ByteBuffer.allocate(addr.length + txId.length);
            buffer.put(addr);
            buffer.put(txId);
            return buffer.array();
        }
    }
}
