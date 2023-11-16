package rhizome.core.transaction;

import static rhizome.core.common.Utils.hexStringToByteArray;

import io.activej.bytebuf.ByteBuf;
import io.activej.bytebuf.ByteBufPool;
import io.activej.common.MemSize;
import rhizome.core.common.Utils.PublicWalletAddress;
import rhizome.core.common.Utils.SHA256Hash;
import rhizome.core.common.Utils.TransactionSignature;
import rhizome.core.net.NetworkUtilities;

@Deprecated
public record TransactionInfo(
    String signature,
    String signingKey,
    long timestamp,
    PublicWalletAddress to,
    PublicWalletAddress from,
    TransactionAmount amount,
    TransactionAmount fee,
    boolean isTransactionFee
) {

    public static int TRANSACTIONINFO_BUFFER_SIZE = 149;

    public static TransactionInfo of(ByteBuf buffer) {
        String signature = NetworkUtilities.readNetworkString(buffer, TransactionSignature.SIZE);
        String signingKey = NetworkUtilities.readNetworkString(buffer, SHA256Hash.SHA256_LENGTH);
        long timestamp = NetworkUtilities.readNetworkUint64(buffer);
        PublicWalletAddress to = new PublicWalletAddress(buffer.slice(PublicWalletAddress.SIZE));
        PublicWalletAddress from = new PublicWalletAddress(buffer.slice(PublicWalletAddress.SIZE));
        TransactionAmount amount = new TransactionAmount(NetworkUtilities.readNetworkUint64(buffer));
        TransactionAmount fee = new TransactionAmount(NetworkUtilities.readNetworkUint64(buffer));
        boolean isTransactionFee = NetworkUtilities.readNetworkBoolean(buffer);

        return new TransactionInfo(signature, signingKey, timestamp, to, from, amount, fee, isTransactionFee);
    }

    public ByteBuf toBuffer() {
        ByteBuf buffer = ByteBufPool.allocate(MemSize.of(TRANSACTIONINFO_BUFFER_SIZE));
        NetworkUtilities.writeNetworkNBytes(buffer, hexStringToByteArray(this.signature), TransactionSignature.SIZE);
        NetworkUtilities.writeNetworkNBytes(buffer, hexStringToByteArray(this.signingKey), SHA256Hash.SHA256_LENGTH);
        NetworkUtilities.writeNetworkUint64(buffer, this.timestamp);
        NetworkUtilities.writeNetworkNBytes(buffer, this.to.address().getArray(), PublicWalletAddress.SIZE);
        NetworkUtilities.writeNetworkNBytes(buffer, this.from.address().getArray(), PublicWalletAddress.SIZE);
        NetworkUtilities.writeNetworkUint64(buffer, this.amount.amount());
        NetworkUtilities.writeNetworkUint64(buffer, this.fee.amount());
        NetworkUtilities.writeNetworkBoolean(buffer, this.isTransactionFee);
        return buffer;
    }
}
