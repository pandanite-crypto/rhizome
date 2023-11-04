package rhizome.persistence;

import org.iq80.leveldb.DBException;
import org.iq80.leveldb.ReadOptions;
import org.iq80.leveldb.WriteOptions;

import io.activej.bytebuf.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import rhizome.core.block.BlockHeader;
import rhizome.core.transaction.TransactionInfo;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
        ByteBuffer keyBuffer = ByteBuffer.allocate(Integer.BYTES);
        keyBuffer.putInt(blockId);
        byte[] key = keyBuffer.array();
        
        var value = ByteBuf.wrapForWriting(getDb().get(key, new ReadOptions()));
        if (value == null) {
            throw new BlockStoreException("Could not read block header " + blockId + " from BlockStore db.");
        }

        return BlockHeader.of(value);
    }

    public List<TransactionInfo> getBlockTransactions(BlockHeader block) {
        List<TransactionInfo> transactions = new ArrayList<>();
        for (int i = 0; i < block.numTranactions(); i++) {
            ByteBuffer keyBuffer = ByteBuffer.allocate(2 * Integer.BYTES);
            keyBuffer.putInt(block.id());
            keyBuffer.putInt(i);
            byte[] key = keyBuffer.array();
            
            byte[] value = getDb().get(key, new ReadOptions());
            if (value == null) {
                throw new BlockStoreException("Could not read transaction from BlockStore db.");
            }
            
            TransactionInfo transaction = TransactionInfo.of(ByteBuf.wrapForReading(value)); // Implémentation hypothétique de la désérialisation
            transactions.add(transaction);
        }
        return transactions;
    }
}
