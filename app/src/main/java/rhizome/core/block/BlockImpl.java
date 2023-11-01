package rhizome.core.block;

import static rhizome.core.common.Constants.*;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import org.json.JSONObject;

import lombok.Builder;
import lombok.Data;
import rhizome.core.common.Utils.SHA256Hash;
import rhizome.core.transaction.Transaction;

@Data
@Builder
public final class BlockImpl implements Block {

    /**
     * Variable Definitions
     */
    @Builder.Default
    private int id = 1;

    @Builder.Default
    private long timestamp = System.currentTimeMillis();

    @Builder.Default
    private int difficulty = MIN_DIFFICULTY;

    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();

    @Builder.Default
    private SHA256Hash merkleRoot = NULL_SHA256_HASH;

    @Builder.Default
    private SHA256Hash lastBlockHash = NULL_SHA256_HASH;

    @Builder.Default
    private SHA256Hash nonce = NULL_SHA256_HASH;

    /**
     * Serialization
     */
    public BlockHeader serialize() {
       return serialize(this);
    }

    public JSONObject toJson() {
        return toJson(this);
    }

    /**
     * Cryptographic Hashing
     * @return
     * @throws NoSuchAlgorithmException
     */
    public SHA256Hash getHash() throws NoSuchAlgorithmException {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

            sha256.update(merkleRoot.hash);
            sha256.update(lastBlockHash.hash);
            
            // Convert int and long to byte arrays and update hash
            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + Long.BYTES);
            buffer.putInt(difficulty);
            buffer.putLong(timestamp);
            sha256.update(buffer.array());

            SHA256Hash hash = new SHA256Hash();
            hash.hash = sha256.digest();
            return hash;
        } catch (NoSuchAlgorithmException e) {
            throw new BlockException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * Utils
     */
    public void addTransaction(Transaction t) {
        transactions.add(t);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockImpl block = (BlockImpl) o;
        return id == block.id && difficulty == block.difficulty && timestamp == block.timestamp &&
            Arrays.equals(nonce.hash, block.nonce.hash) && Arrays.equals(merkleRoot.hash, block.merkleRoot.hash) &&
            Arrays.equals(lastBlockHash.hash, block.lastBlockHash.hash) && transactions.containsAll(block.transactions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nonce, id, difficulty, timestamp, merkleRoot, lastBlockHash, transactions);
    }
}
