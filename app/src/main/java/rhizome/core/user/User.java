package rhizome.core.user;

import java.security.SecureRandom;
import java.util.Optional;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.util.Arrays;

import io.activej.bytebuf.ByteBuf;
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
        Transaction transaction = Transaction.of(fromWallet, toWallet, amount, this.publicKey);
        signTransaction(transaction);
        return transaction;
    }

    public void signTransaction(Transaction transaction) {
        transaction.sign(this.publicKey, this.privateKey);
    }

    private PublicWalletAddress walletAddressFromPublicKey(Ed25519PublicKeyParameters inputKey) {
        byte[] publicKeyBytes = inputKey.getEncoded();

        SHA256Digest sha256 = new SHA256Digest();
        byte[] hash1 = new byte[32];
        sha256.update(publicKeyBytes, 0, publicKeyBytes.length);
        sha256.doFinal(hash1, 0);

        RIPEMD160Digest ripemd160 = new RIPEMD160Digest();
        byte[] hash2 = new byte[20];
        ripemd160.update(hash1, 0, hash1.length);
        ripemd160.doFinal(hash2, 0);

        byte[] hash3 = new byte[32];
        byte[] hash4 = new byte[32];
        sha256.reset();
        sha256.update(hash2, 0, hash2.length);
        sha256.doFinal(hash3, 0);
        sha256.reset();
        sha256.update(hash3, 0, hash3.length);
        sha256.doFinal(hash4, 0);

        ByteBuf buf = ByteBuf.wrapForWriting(new byte[25]);

        buf.writeByte((byte) 0);
        buf.put(hash2);
        buf.put(Arrays.copyOfRange(hash4, 0, 4));

        return new PublicWalletAddress(buf);
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
