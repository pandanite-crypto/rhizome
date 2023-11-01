package rhizome.core.transaction;

import static rhizome.core.common.Utils.longToBytes;

import java.util.Objects;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.json.JSONObject;

import lombok.Builder;
import lombok.Data;
import rhizome.core.common.Utils.PublicWalletAddress;
import rhizome.core.common.Utils.SHA256Hash;
import rhizome.core.common.Utils.TransactionSignature;

import static rhizome.core.common.Crypto.signWithPrivateKey;

@Data
@Builder
public final class TransactionImpl implements Transaction, Comparable<Transaction> {
    
    @Builder.Default
    private PublicWalletAddress from = PublicWalletAddress.empty();

    @Builder.Default
    private PublicWalletAddress to = PublicWalletAddress.empty();

    private TransactionAmount amount;

    @Builder.Default
    private boolean isTransactionFee = false;

    @Builder.Default
    private long timestamp = System.currentTimeMillis();

    @Builder.Default
    private TransactionAmount fee = new TransactionAmount(0);

    private Ed25519PublicKeyParameters signingKey;

    @Builder.Default
    private TransactionSignature signature = new TransactionSignature();

    /**
     * Serialization
     */
    public TransactionInfo serialize() {
       return serialize(this);
    }

    public JSONObject toJson() {
        return toJson(this);
    }
    
    // Method to check if the signature is valid
    public boolean signatureValid() {
        throw new UnsupportedOperationException("Not supported yet....");
    }

    public SHA256Hash getHash() {
        var digest = new SHA256Digest();
        var sha256Hash = new SHA256Hash();

        digest.update(to.address().array(), 0, to.address().readRemaining());
        if (!isTransactionFee) {
            digest.update(from.address().array(), 0, from.address().readRemaining());
        }
        digest.update(longToBytes(fee.amount()), 0, 8);
        digest.update(longToBytes(amount.amount()), 0, 8);
        digest.update(longToBytes(timestamp), 0, 8);
        digest.doFinal(sha256Hash.hash, 0);

        return sha256Hash;
    }

    public void sign(Ed25519PublicKeyParameters pubKey, Ed25519PrivateKeyParameters signingKey) {
        this.signature.signature = signWithPrivateKey(getHash().hash, pubKey, signingKey);
    }

    @Override
    public int compareTo(Transaction other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TransactionImpl that = (TransactionImpl) obj;
        return timestamp == that.timestamp &&
                isTransactionFee == that.isTransactionFee &&
                Objects.equals(from, that.from) &&
                Objects.equals(to, that.to) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(fee, that.fee) &&
                Objects.equals(signingKey, that.signingKey) &&
                Objects.equals(signature, that.signature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, amount, isTransactionFee, timestamp, fee, signingKey, signature);
    }
}
