package rhizome.core.common;

import java.security.SecureRandom;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.util.Arrays;

public class Crypto {

    private Crypto() {}

    public static byte[] signWithPrivateKey(String content, Ed25519PublicKeyParameters publicKey, Ed25519PrivateKeyParameters privateKey) {
        return signWithPrivateKey(content.getBytes(), publicKey, privateKey);
    }

    public static byte[] signWithPrivateKey(byte[] message, Ed25519PublicKeyParameters publicKey, Ed25519PrivateKeyParameters privateKey) {
        try {
            Signer signer = new Ed25519Signer();
            signer.init(true, privateKey);
            byte[] combined = Arrays.concatenate(publicKey.getEncoded(), message);
            signer.update(combined, 0, combined.length);
            return signer.generateSignature();

        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static boolean checkSignature(String content, byte[] signature, Ed25519PublicKeyParameters publicKey) {
        return checkSignature(content.getBytes(), signature, publicKey);
    }
    
    public static boolean checkSignature(byte[] bytes, byte[] signature, Ed25519PublicKeyParameters publicKey) {
        Ed25519Signer signer = new Ed25519Signer();
        signer.init(false, publicKey);
        signer.update(bytes, 0, bytes.length);
        return signer.verifySignature(signature);
    }

    public static AsymmetricCipherKeyPair generateKeyPair() {
        Ed25519KeyPairGenerator keyGen = new Ed25519KeyPairGenerator();
        keyGen.init(new Ed25519KeyGenerationParameters(new SecureRandom()));
        return keyGen.generateKeyPair();
    }
}
