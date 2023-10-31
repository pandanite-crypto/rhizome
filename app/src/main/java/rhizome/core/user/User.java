package rhizome.core.user;

import java.security.SecureRandom;
import java.util.Optional;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.json.JSONObject;

import rhizome.core.common.Utils.PublicWalletAddress;
import rhizome.core.transaction.Transaction;
import rhizome.core.transaction.TransactionAmount;
import lombok.Setter;
import lombok.Getter;

import static rhizome.core.common.Helpers.PDN;

@Getter
@Setter
public class User {
    private Ed25519PublicKeyParameters publicKey;
    private Ed25519PrivateKeyParameters privateKey;

    public User() {
        generateKeyPair().ifPresentOrElse(
                kp -> {
                    this.publicKey = (Ed25519PublicKeyParameters) kp.getPublic();
                    this.privateKey = (Ed25519PrivateKeyParameters) kp.getPrivate();
                },
                () -> {
                    throw new RuntimeException("Could not generate key pair");
                }
        );
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

    // // Getters
    public PublicWalletAddress getAddress() {
        return walletAddressFromPublicKey(this.publicKey);
    }

    // Methods for transactions
    public Transaction mine() {
        return Transaction.of(this.getAddress(), PDN(50));
    }

    public Transaction send(User receiver, int i) {
        return send(receiver, PDN(i));
    }

    public Transaction send(User to, TransactionAmount amount) {
        PublicWalletAddress fromWallet = this.getAddress();
        PublicWalletAddress toWallet = to.getAddress();
        Transaction t = Transaction.of(fromWallet, toWallet, amount, this.publicKey);
        this.signTransaction(t);
        return t;
    }

    public void signTransaction(Transaction t) {
        t.sign(this.publicKey, this.privateKey);
    }

    private Ed25519PublicKeyParameters stringToPublicKey(String key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private Ed25519PrivateKeyParameters stringToPrivateKey(String key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private String publicKeyToString(Ed25519PublicKeyParameters key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private String privateKeyToString(Ed25519PrivateKeyParameters key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private PublicWalletAddress walletAddressFromPublicKey(Ed25519PublicKeyParameters key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    public Optional<AsymmetricCipherKeyPair> generateKeyPair() {
        try {
            Ed25519KeyPairGenerator keyGen = new Ed25519KeyPairGenerator();
            keyGen.init(new Ed25519KeyGenerationParameters(new SecureRandom()));
            return Optional.of(keyGen.generateKeyPair());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
