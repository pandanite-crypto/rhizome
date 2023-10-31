package rhizome.core.block;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rhizome.core.net.Serializable;
import rhizome.core.transaction.Transaction;

import static rhizome.core.common.Helpers.longToString;
import static rhizome.core.common.Utils.SHA256toString;
import static rhizome.core.common.Utils.stringToSHA256;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface Block {

    public static Block empty() {
        return BlockImpl.builder().build();
    }

    public static Block of(JSONObject json){
        return serializer().fromJson(json);
    }

    public static Block of(Block block) {
        var blockImpl = (BlockImpl) block;
        return BlockImpl.builder()
                .id(blockImpl.getId())
                .timestamp(blockImpl.getTimestamp())
                .difficulty(blockImpl.getDifficulty())
                .merkleRoot(blockImpl.getMerkleRoot())
                .lastBlockHash(blockImpl.getLastBlockHash())
                .nonce(blockImpl.getNonce())
                .transactions(blockImpl.getTransactions())
                .build();
    }

    public static Block of(BlockHeader blockHeader, List<Transaction> transactions) {
        return BlockImpl.builder()
                .id(blockHeader.id())
                .timestamp(blockHeader.timestamp())
                .difficulty(blockHeader.difficulty())
                .merkleRoot(blockHeader.merkleRoot())
                .lastBlockHash(blockHeader.lastBlockHash())
                .nonce(blockHeader.nonce())
                .transactions(transactions)
                .build();
    }

    public BlockHeader serialize();
    default BlockHeader serialize(Block block) {
        return serializer().serialize(block);
    }

    public JSONObject toJson();
    default JSONObject toJson(Block block) {
        return serializer().toJson(block);
    }

    void addTransaction(Transaction t);
    List<Transaction> getTransactions();

    /**
     * Get instance of the serializer
     * @return
     */
    static BlockSerializer serializer(){
        return BlockSerializer.instance;
    }

    /**
     * Serializes the block
     */
    static class BlockSerializer implements Serializable<BlockHeader, Block> {

        static BlockSerializer instance = new BlockSerializer();

        @Override
        public BlockHeader serialize(Block block) {
            var blockImpl = (BlockImpl) block;
            return new BlockHeader(
                blockImpl.getId(),
                blockImpl.getTimestamp(),
                blockImpl.getDifficulty(),
                blockImpl.getTransactions().size(),
                blockImpl.getLastBlockHash(),
                blockImpl.getMerkleRoot(),
                blockImpl.getNonce()
            );
        }
    
        @Override
        public Block deserialize(BlockHeader object) {
            throw new UnsupportedOperationException("Not implemented");
        }
    
        @Override
        public JSONObject toJson(Block block) {
            var blockImpl = (BlockImpl) block;
            JSONObject result = new JSONObject();
            result.put("id", blockImpl.getId());
            try {
                result.put("hash", SHA256toString(blockImpl.getHash()));
            } catch (JSONException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            result.put("difficulty", blockImpl.getDifficulty());
            result.put("nonce", SHA256toString(blockImpl.getNonce()));
            result.put("timestamp", longToString(blockImpl.getTimestamp()));
            result.put("merkleRoot", SHA256toString(blockImpl.getMerkleRoot()));
            result.put("lastBlockHash", SHA256toString(blockImpl.getLastBlockHash()));
            JSONArray transactionsArray = new JSONArray();
            for (Transaction transaction : blockImpl.getTransactions()) {
                transactionsArray.put(transaction.toJson());
            }
            result.put("transactions", transactionsArray);
            return result;
        }
    
        public Block fromJson(JSONObject json) {
            return BlockImpl.builder()
                .id(json.getInt("id"))
                .timestamp(json.getLong("timestamp"))
                .difficulty(json.getInt("difficulty"))
                .merkleRoot(stringToSHA256(json.getString("merkleRoot")))
                .lastBlockHash(stringToSHA256(json.getString("lastBlockHash")))
                .nonce(stringToSHA256(json.getString("nonce")))
                .transactions(
                    IntStream.range(0, json.getJSONArray("transactions").length())
                        .mapToObj(i -> Transaction.of(json.getJSONArray("transactions").getJSONObject(i)))
                        .collect(Collectors.toList())
                )
                .build();
        }
    }
}
