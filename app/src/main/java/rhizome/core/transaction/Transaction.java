package rhizome.core.transaction;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;
import rhizome.core.common.Utils.PrivateKey;
import rhizome.core.common.Utils.PublicKey;
import rhizome.core.common.Utils.PublicWalletAddress;
import rhizome.core.common.Utils.TransactionSignature;

@Getter
@Setter
public class Transaction implements Comparable<Transaction> {

    private PublicWalletAddress from;
    private PublicWalletAddress to;
    private TransactionAmount amount;
    private boolean isTransactionFee;
    private long timestamp;
    private TransactionAmount fee;
    private PublicKey signingKey;
    private TransactionSignature signature;

    public Transaction(PublicWalletAddress from, PublicWalletAddress to, TransactionAmount amount, PublicKey signingKey, TransactionAmount fee) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.isTransactionFee = false;
        this.timestamp = System.currentTimeMillis();
        this.fee = fee;
        this.signingKey = signingKey;
    }

    public Transaction(PublicWalletAddress from, PublicWalletAddress to, TransactionAmount amount, PublicKey signingKey, TransactionAmount fee, long timestamp) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.isTransactionFee = false;
        this.timestamp = timestamp;
        this.fee = fee;
        this.signingKey = signingKey;
    }

    public Transaction() {
    }

    public Transaction(Transaction t) {
        this.to = t.to;
        this.from = t.from;
        this.signature = t.signature;
        this.amount = t.amount;
        this.isTransactionFee = t.isTransactionFee;
        this.timestamp = t.timestamp;
        this.fee = t.fee;
        this.signingKey = t.signingKey;
    }

    public Transaction(PublicWalletAddress fromWallet, PublicWalletAddress toWallet, TransactionAmount amount,
            java.security.PublicKey publicKey) {
                throw new UnsupportedOperationException("Not supported yet.");
    }

    public Transaction(PublicWalletAddress address, TransactionAmount pdn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Transaction(JSONObject jsonObject) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Method to check if the signature is valid
    public boolean signatureValid() {
        // You need to implement your own logic here
        return true;
    }

    // Method to get signing key
    public PublicKey getSigningKey() {
        return this.signingKey;
    }

    // Method to get hash
    public byte[] getHash() {
        throw new UnsupportedOperationException("Not supported yet.");
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int compareTo(Transaction other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Transaction that = (Transaction) obj;
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

    public void sign(java.security.PublicKey publicKey, java.security.PrivateKey privateKey) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<?> toJson() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
