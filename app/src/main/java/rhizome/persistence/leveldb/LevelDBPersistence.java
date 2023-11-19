package rhizome.persistence.leveldb;

import org.iq80.leveldb.DBException;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.ReadOptions;
import org.iq80.leveldb.WriteOptions;

import io.activej.bytebuf.ByteBuf;
import io.activej.bytebuf.ByteBufPool;
import io.activej.common.MemSize;
import lombok.extern.slf4j.Slf4j;
import rhizome.core.block.Block;
import rhizome.core.block.dto.BlockDto;
import rhizome.core.common.Utils.PublicWalletAddress;
import rhizome.core.common.Utils.SHA256Hash;
import rhizome.core.net.BinarySerializable;
import rhizome.core.transaction.Transaction;
import rhizome.core.transaction.dto.TransactionDto;
import rhizome.persistence.BlockPersistence;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
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

    public BlockDto  getBlockHeader(int blockId) {       
        return BinarySerializable.fromBuffer((byte[])get(blockId, byte[].class), BlockDto.class);
    }
    public ByteBuf getBlockHeadeAsByteBuf(int blockId) {       
        return (ByteBuf) get(blockId, ByteBuf.class);
    }

    public List<TransactionDto> getBlockTransactions(BlockDto block) {
        var transactions = new ArrayList<TransactionDto>();
        for (int i = 0; i < block.getNumTranactions(); i++) {            
            var value = (byte[]) get(composeKey(block.getId(), i), byte[].class);
            transactions.add(BinarySerializable.fromBuffer(value, TransactionDto.class));
        }
        return transactions;
    }

    public ByteBuf getRawData(int blockId) {
        var blockHeader = getBlockHeader(blockId);
        var bufferSize = MemSize.of((long) BlockDto.BUFFER_SIZE + (TransactionDto.BUFFER_SIZE * blockHeader.getNumTranactions()));
        var buffer = ByteBufPool.allocateExact(bufferSize);
        buffer.put(blockHeader.toBuffer());

        getBlockTransactions(blockHeader).forEach(transaction -> buffer.put(transaction.toBuffer()));
        buffer.head(0);
        return buffer;
    }

    public Block getBlock(int blockId) {
        BlockDto block = getBlockHeader(blockId);
        List<Transaction> transactions = new ArrayList<>();
        getBlockTransactions(block).forEach(transaction -> transactions.add(Transaction.of(transaction)));
        return Block.of(block, transactions);
    }

    // NOTE: seek method looks like bugged as it return everything wi
    public List<SHA256Hash> getTransactionsForWallet(PublicWalletAddress wallet) {
         var addrress = wallet.address().getArray();
        List<SHA256Hash> transactions = new ArrayList<>();

        try (DBIterator iterator = getDb().iterator(new ReadOptions())) {
            for(iterator.seek(addrress); iterator.hasNext(); iterator.next()) {
                byte[] key = iterator.peekNext().getKey();
                byte[] addressKey = Arrays.copyOfRange(key, 0, 25);
                if (!Arrays.equals(addressKey, addrress)) {
                    continue;
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
            
            var w1Key = new WalletTransactionKey(t.getFrom(), txid, false);
            var w2Key = new WalletTransactionKey(t.getTo(), txid, false);
            
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
        set(block.getId(), block.serialize().toBuffer());

        for (int i = 0; i < block.getTransactions().size(); i++) {
            var transaction = block.getTransactions().get(i);
            var transactionDto = transaction.serialize();
            set(composeKey(block.getId(), i), transactionDto.toBuffer());
            set(composeKey(transactionDto.getFrom(), transaction.hashContents().hash), new byte[0]);
            set(composeKey(transactionDto.getTo(), transaction.hashContents().hash), new byte[0]);
        }
    }

    private static class WalletTransactionKey {
        PublicWalletAddress addr;
        SHA256Hash txId;
        
        public WalletTransactionKey(PublicWalletAddress address, SHA256Hash txId, boolean isStartKey) {
            this.addr = address;
            this.txId = new SHA256Hash();
            
            if(isStartKey) {
                Arrays.fill(this.txId.hash, (byte) -128);
            } else {
                Arrays.fill(this.txId.hash, (byte) 127);
            }

            if(txId.hash != null) {
                System.arraycopy(txId.hash, 0, this.txId.hash, 0, txId.hash.length);
            }
        }
                
        byte[] toByteArray() {
            return key(addr, txId);
        }

        static byte[] key(PublicWalletAddress address, SHA256Hash sha256) {
            return composeKey(address.address().array(), sha256.hash);
        }
    }
}
