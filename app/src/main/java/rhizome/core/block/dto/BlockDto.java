package rhizome.core.block.dto;

import org.jetbrains.annotations.NotNull;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import lombok.Getter;
import rhizome.core.net.BinarySerializable;

@Getter
public class BlockDto implements BinarySerializable {
    @Serialize public final int id;
    @Serialize public final long timestamp;
    @Serialize public final int difficulty;
    @Serialize public final int numTranactions;
    @Serialize public final byte[] lastBlockHash;
    @Serialize public final byte[] merkleRoot;
    @Serialize public final byte[] nonce;

    public static final int BUFFER_SIZE = 119;

    public BlockDto(
        @Deserialize("id") int id, 
        @Deserialize("timestamp") long timestamp, 
        @Deserialize("difficulty") int difficulty, 
        @Deserialize("numTranactions") int numTranactions, 
        @Deserialize("lastBlockHash") byte[] lastBlockHash, 
        @Deserialize("merkleRoot") byte[] merkleRoot, 
        @Deserialize("nonce") byte[] nonce) {

        this.id = id;
        this.timestamp = timestamp;
        this.difficulty = difficulty;
        this.numTranactions = numTranactions;
        this.lastBlockHash = lastBlockHash;
        this.merkleRoot = merkleRoot;
        this.nonce = nonce;
    }

    @Override
    public @NotNull int getSize() {
        return BUFFER_SIZE;
    }
}
