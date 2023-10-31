package rhizome.core.transaction;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Objects;

import org.json.JSONObject;

import lombok.Builder;
import lombok.Data;
import rhizome.core.common.Utils.PublicWalletAddress;
import rhizome.core.common.Utils.TransactionSignature;

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

    private PublicKey signingKey;

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

    // Method to get hash
    public byte[] getHash() {
        throw new UnsupportedOperationException("Not supported yet...");
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
    public void sign(PublicKey pubKey, PrivateKey signingKey) {
        throw new UnsupportedOperationException("Not supported yet..");
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
