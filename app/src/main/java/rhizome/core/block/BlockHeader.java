package rhizome.core.block;

import io.activej.bytebuf.ByteBuf;
import io.activej.bytebuf.ByteBufPool;
import io.activej.common.MemSize;
import rhizome.core.common.Utils.SHA256Hash;
import rhizome.core.net.NetworkUtilities;

@Deprecated
public record BlockHeader (
    int id,
    long timestamp,
    int difficulty,
    int numTranactions,
    SHA256Hash lastBlockHash,
    SHA256Hash merkleRoot,
    SHA256Hash nonce
) {

    public static final int BLOCKHEADER_BUFFER_SIZE = 116;

	public static BlockHeader of(ByteBuf buffer) {
        int id = NetworkUtilities.readNetworkUint32(buffer);
        long timestamp = NetworkUtilities.readNetworkUint64(buffer);
        int difficulty = NetworkUtilities.readNetworkUint32(buffer);
        int numTransactions = NetworkUtilities.readNetworkUint32(buffer);
        SHA256Hash lastBlockHash = new SHA256Hash(NetworkUtilities.readNetworkSHA256(buffer));
        SHA256Hash merkleRoot = new SHA256Hash(NetworkUtilities.readNetworkSHA256(buffer));
        SHA256Hash nonce = new SHA256Hash(NetworkUtilities.readNetworkSHA256(buffer));
        buffer.recycle();
        return new BlockHeader(id, timestamp, difficulty, numTransactions, lastBlockHash, merkleRoot, nonce);
	}

	public ByteBuf toBuffer() {
        var buffer = ByteBufPool.allocateExact(MemSize.of(BLOCKHEADER_BUFFER_SIZE));
        NetworkUtilities.writeNetworkUint32(buffer, this.id());
        NetworkUtilities.writeNetworkUint64(buffer, this.timestamp());
        NetworkUtilities.writeNetworkUint32(buffer, this.difficulty());
        NetworkUtilities.writeNetworkUint32(buffer, this.numTranactions());
        NetworkUtilities.writeNetworkSHA256(buffer, this.lastBlockHash().hash);
        NetworkUtilities.writeNetworkSHA256(buffer, this.merkleRoot().hash);
        NetworkUtilities.writeNetworkSHA256(buffer, this.nonce().hash);
        return buffer;
	}
}
