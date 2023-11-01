package rhizome.core.user;

import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import rhizome.core.common.Utils.PublicWalletAddress;
import rhizome.core.transaction.Transaction;
import rhizome.core.transaction.TransactionAmount;

import static rhizome.core.common.Helpers.PDN;
import static rhizome.core.common.Crypto.generateKeyPair;

public interface User {

    public static User create() {
        var kp = generateKeyPair();
        return UserImpl.builder()
                .publicKey((Ed25519PublicKeyParameters) kp.getPublic())
                .privateKey((Ed25519PrivateKeyParameters) kp.getPrivate())
                .build();
    }

    // Constructor from JSON object
    // public User(JSONObject u) {
    //     this.publicKey = stringToPublicKey(u.getString("publicKey"));
    //     this.privateKey = stringToPrivateKey(u.getString("privateKey"));
    // }

    // Convert User object to JSON object
    // public JSONObject toJson() {
    //     JSONObject result = new JSONObject();
    //     result.put("publicKey", publicKeyToString(this.publicKey));
    //     result.put("privateKey", privateKeyToString(this.privateKey));
    //     return result;
    // }

    public Ed25519PublicKeyParameters getPublicKey();
    public Ed25519PrivateKeyParameters getPrivateKey();

    default PublicWalletAddress getAddress() {
        return PublicWalletAddress.fromPublicKey(getPublicKey());
    }

    default Transaction mine() {
        return Transaction.of(getAddress(), PDN(50));
    }

    default Transaction send(User receiver, double i) {
        return send(receiver, PDN(i));
    }

    default Transaction send(User to, TransactionAmount amount) {
        PublicWalletAddress fromWallet = getAddress();
        PublicWalletAddress toWallet = to.getAddress();
        Transaction transaction = Transaction.of(fromWallet, toWallet, amount, getPublicKey());
        signTransaction(transaction);
        return transaction;
    }

    default void signTransaction(Transaction transaction) {
        transaction.sign(getPublicKey(), getPrivateKey());
    }
}
