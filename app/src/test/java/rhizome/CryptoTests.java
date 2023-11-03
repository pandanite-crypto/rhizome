package rhizome;

import org.junit.jupiter.api.Test;

import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static rhizome.core.common.Crypto.SHA256;
import static rhizome.core.common.Crypto.generateKeyPair;
import static rhizome.core.common.Crypto.signWithPrivateKey;
import static rhizome.core.common.Crypto.checkSignature;
import static rhizome.core.common.Utils.signatureToString;
import static rhizome.core.common.Utils.SHA256toString;
import static rhizome.core.common.Utils.stringToSHA256;
import static rhizome.core.common.Utils.stringToPublicKey;
import static rhizome.core.common.Utils.publicKeyToString;
import static rhizome.core.common.Utils.stringToSignature;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

class CryptoTests {

    @Test
    void testKeyStringConversion() {
        var keys = generateKeyPair();
        var publicKey = (Ed25519PublicKeyParameters) keys.getPublic();
        var publicKeyData = publicKey.getEncoded();

        var publicKeyString = publicKeyToString(publicKey);
        var convertedPublicKey = stringToPublicKey(publicKeyString);
        var convertedPublicKeyData = convertedPublicKey.getEncoded();

        assertArrayEquals(publicKeyData, convertedPublicKeyData);
    }

    @Test
    void testSignatureStringConversion() {
        var keys = generateKeyPair();
        var privateKey = (Ed25519PrivateKeyParameters) keys.getPrivate();
        var message = "FOOBAR";
        var signature = signWithPrivateKey(message.getBytes(StandardCharsets.UTF_8), privateKey);

        var signatureString = signatureToString(signature);
        var convertedSignature = stringToSignature(signatureString);

        assertArrayEquals(signature, convertedSignature.signature);
    }

    @Test
    void testSignatureVerifications() {
        var keys = generateKeyPair();
        var privateKey = (Ed25519PrivateKeyParameters) keys.getPrivate();
        var publicKey = (Ed25519PublicKeyParameters) keys.getPublic();

        var message = "FOOBAR";
        var signature = signWithPrivateKey(message.getBytes(StandardCharsets.UTF_8), privateKey);
        var status = checkSignature(message.getBytes(StandardCharsets.UTF_8), signature, publicKey);
        assertTrue(status);

        // check with wrong public key
        var wrongKeys = generateKeyPair();
        var wrongPrivateKey = (Ed25519PrivateKeyParameters) wrongKeys.getPrivate();
        var wrongSignature = signWithPrivateKey(message.getBytes(StandardCharsets.UTF_8), wrongPrivateKey);
        status = checkSignature(message.getBytes(StandardCharsets.UTF_8), wrongSignature, publicKey);
        assertFalse(status);
    }

    @Test
    void total_work() {
        var work = BigInteger.ZERO;
        work = addWork(work, 16);
        work = addWork(work, 16);
        work = addWork(work, 16);
        var base = BigInteger.valueOf(2);
        var mult = BigInteger.valueOf(3);
        var expected = mult.multiply(base.pow(16));
        assertEquals(expected, work);
        assertEquals(new BigInteger("196608"), work);
        work = addWork(work, 32);
        work = addWork(work, 28);
        work = addWork(work, 74);
        work = addWork(work, 174);
        var b = expected;
        b = b.add(base.pow(32));
        b = b.add(base.pow(28));
        b = b.add(base.pow(74));
        b = b.add(base.pow(174));
        
        assertEquals(b, work);
        assertEquals("23945242826029513411849172299242470459974281928572928", work.toString());
    }

    // @Test
    // void mine_hash() {
    //     var hash = SHA256("Hello World".getBytes());
    //     var answer = mineHash(hash, 6);
    //     var newHash = concatHashes(hash, answer);
    //     byte[] data = newHash.data();
        
    //     // check first 6 bits are 0
    //     assertTrue((data[0] & 0b11111100) == 0);
    // }

    @Test
    void sha256ToString() {
        var message = "FOOBAR";
        var hash = SHA256(message.getBytes(StandardCharsets.UTF_8));
        var hashString = SHA256toString(hash);
        var convertedHash = stringToSHA256(hashString);

        assertArrayEquals(hash.hash, convertedHash.hash);
    }

    private BigInteger addWork(BigInteger work, int exponent) {
        BigInteger base = BigInteger.valueOf(2);
        return work.add(base.pow(exponent));
    }
}
