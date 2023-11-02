package rhizome;

import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.util.Arrays;
import org.junit.jupiter.api.Test;

import rhizome.core.common.Utils.PublicWalletAddress;
import rhizome.core.common.Utils.TransactionSignature;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static rhizome.core.common.Utils.walletAddressToString;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import static rhizome.core.common.Utils.bytesToHex;
import static rhizome.core.common.Utils.hexStringToByteArray;
import static rhizome.core.common.Utils.signatureToString;
import static rhizome.core.common.Utils.stringToSignature;
import static rhizome.core.common.Utils.stringToWalletAddress;

class UtilsTest {

    static String TEST_PUBLIC_KEY = "471979EB816A71948474F86EED38D26DC1DB94506E776031C91958BEDF2A8862";
    static String TEST_PRIVATE_KEY = "FA0D5D01EB4965A6B19E62AF17215731A4BFD50CBE2881F395917446DE2EC996471979EB816A71948474F86EED38D26DC1DB94506E776031C91958BEDF2A8862";
    static String TEST_MESSAGE = "test";
    static String LEGACY_SIGNATURE = "914D303286115695366F6977B7748AA51CAFFEDFFC0560652D17DDEE809E8120DBE00715204D7D71B96A6350C0DCF6BFE4EB3B018810A9F50052E8934E900107";
    static String ADDRESS = "00A7D087CC62C349B10D8EE7BD923047F881E21F8BFB94E0CA";

    @Test
    void checkAddressSerialisation() {
        var a = PublicWalletAddress.random();
        var b = stringToWalletAddress(walletAddressToString(a.address()));
        assertEquals(a,b);
    }

    @Test
    void checkTransactionSignatureSerialisation() {
        var a = TransactionSignature.random();
        var b = stringToSignature(signatureToString(a.signature));
        assertEquals(a,b);
    }

    @Test
    void checkSignWithPrivateKey() {
        var messageToSign = TEST_MESSAGE;
        var signtureExpected = LEGACY_SIGNATURE;
        var pubKey = hexStringToByteArray(TEST_PUBLIC_KEY);
        var privateKey = hexStringToByteArray(TEST_PRIVATE_KEY);
        var signature = signWithPrivateKey(messageToSign.getBytes(UTF_8), pubKey, privateKey);
        assertEquals(signtureExpected,bytesToHex(signature));
    }

    // public static byte[] ed25519Sign(byte[] message, byte[] publicKey, byte[] privateKey) {
    //     SHA512Digest sha512 = new SHA512Digest();
        
    //     sha512.update(publicKey, 0, publicKey.length);
    //     sha512.update(message, 0, message.length);
    //     byte[] hashedMessage = new byte[64];
    //     sha512.doFinal(hashedMessage, 0);
    //     Ed25519Signer signer = new Ed25519Signer();
    //     Ed25519PrivateKeyParameters privateKeyParameters = new Ed25519PrivateKeyParameters(privateKey, 0);
    //     signer.init(true, privateKeyParameters);
    //     signer.update(hashedMessage, 0, hashedMessage.length);
    //     return signer.generateSignature();
    // }

    // public static byte[] signWithPrivateKey(byte[] bytes, byte[] publicKey, byte[] privateKey) {
    //     return ed25519Sign(bytes, publicKey, privateKey);
    // }

    public static byte[] signWithPrivateKey(byte[] message, byte[] publicKeyBytes, byte[] privateKeyBytes) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("EdDSA");
                
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            Signature sig = Signature.getInstance("EdDSA");
            sig.initSign(privateKey);
            sig.update(message);
            return sig.sign();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SignatureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }
}
