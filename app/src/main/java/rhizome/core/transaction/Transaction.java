package rhizome.core.transaction;

import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.json.JSONObject;

import rhizome.core.common.Utils.PublicWalletAddress;
import rhizome.core.net.Serializable;


import static rhizome.core.common.Utils.SHA256toString;
import static rhizome.core.common.Utils.publicKeyToString;
import static rhizome.core.common.Utils.walletAddressToString;
import static rhizome.core.common.Utils.signatureToString;
import static rhizome.core.common.Utils.stringToPublicKey;
import static rhizome.core.common.Utils.stringToSignature;
import static rhizome.core.common.Utils.stringToWalletAddress;

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

    public static Transaction of(PublicWalletAddress from, PublicWalletAddress to, TransactionAmount amount, Ed25519PublicKeyParameters signingKey, TransactionAmount fee) {
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

    public static Transaction of(PublicWalletAddress from, PublicWalletAddress to, TransactionAmount amount, Ed25519PublicKeyParameters signingKey, TransactionAmount fee, long timestamp) {
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

    public static Transaction of(PublicWalletAddress from, PublicWalletAddress to, TransactionAmount amount, Ed25519PublicKeyParameters signingKey) {
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

    public void sign(Ed25519PublicKeyParameters publicKey, Ed25519PrivateKeyParameters privateKey);

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

        static final String TO = "to";
        static final String AMOUNT = "amount";
        static final String TIMESTAMP = "timestamp";
        static final String FEE = "fee";
        static final String TXID = "txid";
        static final String FROM = "from";
        static final String SIGNING_KEY = "signingKey";
        static final String SIGNATURE = "signature";

        static TransactionSerializer instance = new TransactionSerializer();

        @Override
        public TransactionInfo serialize(Transaction transaction) {
            throw new UnsupportedOperationException("Not implemented.");
        }
    
        @Override
        public Transaction deserialize(TransactionInfo transactionInfo) {
            throw new UnsupportedOperationException("Not implemented..");
        }
    
        @Override
        public JSONObject toJson(Transaction transaction) {
            var transactionImpl = (TransactionImpl) transaction;    
            JSONObject result = new JSONObject();
            result.put(TO, walletAddressToString(transactionImpl.getTo().address));
            result.put(AMOUNT, transactionImpl.getAmount().amount());
            result.put(TIMESTAMP, Long.toString(transactionImpl.getTimestamp()));
            result.put(FEE, transactionImpl.getFee().amount());
            
            if (!transactionImpl.isTransactionFee()) {
                result.put(TXID, SHA256toString(transactionImpl.getHash()));
                result.put(FROM, walletAddressToString(transactionImpl.getFrom().address));
                result.put(SIGNING_KEY, publicKeyToString(transactionImpl.getSigningKey().getEncoded()));
                result.put(SIGNATURE, signatureToString(transactionImpl.getSignature().signature));
            } else {
                result.put(TXID, SHA256toString(transactionImpl.getHash()));
                result.put(FROM, "");
            }
            
            return result;
        }
    
        public Transaction fromJson(JSONObject json) {     
            var builder = TransactionImpl.builder()
                .timestamp(json.getLong(TIMESTAMP))
                .fee(new TransactionAmount(json.getInt(FEE)))
                .to(stringToWalletAddress(json.getString(TO)));

        
            if (json.getString("from").isEmpty()) {
                builder.amount(new TransactionAmount(json.getInt(AMOUNT)))
                    .isTransactionFee(true);
            } else {
                builder.from(stringToWalletAddress(json.getString(FROM)))
                    .signature(stringToSignature(json.getString(SIGNATURE)))
                    .amount(new TransactionAmount(json.getInt(AMOUNT)))
                    .isTransactionFee(false)
                    .signingKey(stringToPublicKey(json.getString(SIGNING_KEY)));
            }   
            
            return builder.build();
        }
    }
}
