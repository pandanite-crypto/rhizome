package rhizome.core.transaction;

import java.security.PublicKey;

import org.json.JSONObject;

import rhizome.core.common.Utils.PublicWalletAddress;
import rhizome.core.net.Serializable;

public interface Transaction {

    public static Transaction empty() {
        return TransactionImpl.builder().build();
    }

    public static Transaction of(JSONObject json){
        return serializer().fromJson(json);
    }

    public static Transaction of(Transaction transaction) {
        var transactionImpl = (TransactionImpl) transaction;
        return TransactionImpl.builder()
                .from(transactionImpl.getFrom())
                .to(transactionImpl.getTo())
                .amount(transactionImpl.getAmount())
                .isTransactionFee(transactionImpl.isTransactionFee())
                .timestamp(transactionImpl.getTimestamp())
                .fee(transactionImpl.getFee())
                .signingKey(transactionImpl.getSigningKey())
                .signature(transactionImpl.getSignature())
                .build();
    }

    public static Transaction of(PublicWalletAddress from, PublicWalletAddress to, TransactionAmount amount, PublicKey signingKey, TransactionAmount fee) {
        return TransactionImpl.builder()
                .from(from)
                .to(to)
                .amount(amount)
                .isTransactionFee(false)
                .timestamp(System.currentTimeMillis())
                .fee(fee)
                .signingKey(signingKey)
                .build();
    }

    public static Transaction of(PublicWalletAddress from, PublicWalletAddress to, TransactionAmount amount, PublicKey signingKey, TransactionAmount fee, long timestamp) {
        return TransactionImpl.builder()
                .from(from)
                .to(to)
                .amount(amount)
                .isTransactionFee(false)
                .timestamp(timestamp)
                .fee(fee)
                .signingKey(signingKey)
                .build();
    }

    public static Transaction of(PublicWalletAddress from, PublicWalletAddress to, TransactionAmount amount, PublicKey signingKey) {
        return TransactionImpl.builder()
                .from(from)
                .to(to)
                .amount(amount)
                .isTransactionFee(false)
                .timestamp(System.currentTimeMillis())
                .signingKey(signingKey)
                .build();
    }

    public static Transaction of(PublicWalletAddress to, TransactionAmount amount) {
        return TransactionImpl.builder()
                .to(to)
                .amount(amount)
                .isTransactionFee(true)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public TransactionInfo serialize();
    default TransactionInfo serialize(Transaction transaction) {
        return serializer().serialize(transaction);
    }

    public JSONObject toJson();
    default JSONObject toJson(Transaction transaction) {
        return serializer().toJson(transaction);
    }

    /**
     * Get instance of the serializer
     * @return
     */
    static TransactionSerializer serializer(){
        return TransactionSerializer.instance;
    }

    /**
     * Serializes the Transaction
     */
    static class TransactionSerializer implements Serializable<TransactionInfo, Transaction> {

        static TransactionSerializer instance = new TransactionSerializer();

        @Override
        public TransactionInfo serialize(Transaction block) {
            throw new UnsupportedOperationException("Not implemented.");
        }
    
        @Override
        public Transaction deserialize(TransactionInfo object) {
            throw new UnsupportedOperationException("Not implemented..");
        }
    
        @Override
        public JSONObject toJson(Transaction block) {            
            throw new UnsupportedOperationException("Not implemented");
        }
    
        public Transaction fromJson(JSONObject json) {            
            throw new UnsupportedOperationException("Not implemented...");
        }
    }
}
