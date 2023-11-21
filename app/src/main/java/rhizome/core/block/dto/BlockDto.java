package rhizome.core.block.dto;

import org.jetbrains.annotations.NotNull;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeFixedSize;
import lombok.Getter;
import rhizome.core.crypto.SHA256Hash;
import rhizome.core.net.BinarySerializable;

@Getter
public class BlockDto implements BinarySerializable {
    @Serialize(order = 1) public final int id;
    @Serialize(order = 2) public final long timestamp;
    @Serialize(order = 3) public final int difficulty;
    @Serialize(order = 4) public final int numTranactions;
    @Serialize(order = 5) public final byte @SerializeFixedSize(SHA256Hash.SIZE) [] lastBlockHash;
    @Serialize(order = 6) public final byte @SerializeFixedSize(SHA256Hash.SIZE) [] merkleRoot;
    @Serialize(order = 7) public final byte @SerializeFixedSize(SHA256Hash.SIZE) [] nonce;

    public static final int BUFFER_SIZE = 116;

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
