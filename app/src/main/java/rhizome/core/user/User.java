package rhizome.core.user;

import org.json.JSONObject;

import lombok.Setter;
import rhizome.core.common.Utils.PublicWalletAddress;
import rhizome.core.transaction.Transaction;
import rhizome.core.transaction.TransactionAmount;
import lombok.Getter;

import static rhizome.core.common.Helpers.PDN;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Optional;


@Getter
@Setter
public class User {
    private PublicKey publicKey;
    private PrivateKey privateKey;

    public User() {
        generateKeyPair().ifPresentOrElse(
                kp -> {
                    this.publicKey = kp.getPublic();
                    this.privateKey = kp.getPrivate();
                },
                () -> {
                    throw new UserException("Could not generate key pair");
                }
        );
    }

    // Constructor from JSON object
    public User(JSONObject u) {
        this.publicKey = stringToPublicKey(u.getString("publicKey"));
        this.privateKey = stringToPrivateKey(u.getString("privateKey"));
    }

    // Convert User object to JSON object
    public JSONObject toJson() {
        JSONObject result = new JSONObject();
        result.put("publicKey", publicKeyToString(this.publicKey));
        result.put("privateKey", privateKeyToString(this.privateKey));
        return result;
    }

    // Getters
    public PublicWalletAddress getAddress() {
        return walletAddressFromPublicKey(this.publicKey);
    }

    // Methods for transactions
    public Transaction mine() {
        return new Transaction(this.getAddress(), PDN(50));
    }

    public Transaction send(User receiver, int i) {
        return send(receiver, PDN(i));
    }

    public Transaction send(User to, TransactionAmount amount) {
        PublicWalletAddress fromWallet = this.getAddress();
        PublicWalletAddress toWallet = to.getAddress();
        Transaction t = new Transaction(fromWallet, toWallet, amount, this.publicKey);
        this.signTransaction(t);
        return t;
    }

    public void signTransaction(Transaction t) {
        t.sign(this.publicKey, this.privateKey);
    }

    private PublicKey stringToPublicKey(String key) {
        return publicKey;
    }

    private PrivateKey stringToPrivateKey(String key) {
        return privateKey;
    }

    private String publicKeyToString(PublicKey key) {
        return null;
    }

    private String privateKeyToString(PrivateKey key) {
        return null;
    }

    private PublicWalletAddress walletAddressFromPublicKey(PublicKey key) {
        return null;
    }

    public static Optional<KeyPair> generateKeyPair() {
        try {
            var keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);  // Adjust key size if necessary
            return Optional.of(keyGen.generateKeyPair());
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Optional.empty();
    }

}
