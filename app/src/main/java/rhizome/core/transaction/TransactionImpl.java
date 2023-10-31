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
public class TransactionImpl implements Transaction, Comparable<Transaction> {
    
    @Builder.Default
    private PublicWalletAddress from = new PublicWalletAddress();

    @Builder.Default
    private PublicWalletAddress to = new PublicWalletAddress();

    private TransactionAmount amount;

    @Builder.Default
    private boolean isTransactionFee = false;

    @Builder.Default
    private long timestamp = System.currentTimeMillis();

    private TransactionAmount fee;

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

    public byte[] getHash() {
        SHA256Digest digest = new SHA256Digest();
        byte[] ret = new byte[32];
        digest.update(to.address, 0, to.address.length);
        if (!isTransactionFee) {
            digest.update(from.address, 0, from.address.length);
        }
        digest.update(longToBytes(fee.amount()), 0, 8);
        digest.update(longToBytes(amount.amount()), 0, 8);
        digest.update(longToBytes(timestamp), 0, 8);
        digest.doFinal(ret, 0);

        return ret;
    }

    // Method to get from wallet
    public PublicWalletAddress fromWallet() {
        return this.from;
    }

    // Method to get to wallet
    public PublicWalletAddress toWallet() {
        return this.to;
    }

    // Method to sign
    public void sign(Ed25519PublicKeyParameters pubKey, Ed25519PrivateKeyParameters signingKey) {
        SHA256Hash sha256 = new SHA256Hash();
        sha256.hash = getHash();
        byte[] transactionSignature = signWithPrivateKey(sha256.hash, pubKey, signingKey);

        this.signature.signature = transactionSignature;
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
