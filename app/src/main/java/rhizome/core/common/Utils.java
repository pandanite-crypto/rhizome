package rhizome.core.common;

import rhizome.core.transaction.TransactionAmount;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Map;

import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;


public class Utils {

    private Utils() {}
    
    public static class PublicWalletAddress {
        public byte[] address = new byte[25];
    }

    // public static class PublicKey {
    //     public byte[] key = new byte[32];
    // }
    
    // public static class PrivateKey {
    //     public byte[] key = new byte[64];
    // }
    
    public static class TransactionSignature {
        public byte[] signature = new byte[64];
    }
    
    public static class LedgerState {
        public Map<PublicWalletAddress, TransactionAmount> state;
    }
    
    public static class SHA256Hash {
        public byte[] hash = new byte[32];
    }
    
    public static class RIPEMD160Hash {
        public byte[] hash = new byte[20];
    }


    public static String walletAddressToString(byte[] p) {
        StringBuilder sb = new StringBuilder();
        for (byte b : p) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static PublicWalletAddress stringToWalletAddress(String s) throws IllegalArgumentException {
        if (s.length() != 50) {
            throw new IllegalArgumentException("Invalid wallet address string");
        }
        
        var wallet = new PublicWalletAddress();
        for (int i = 0; i < s.length(); i += 2) {
            wallet.address[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }

        if (wallet.address.length != 25) {
            throw new IllegalArgumentException("Invalid decoded byte array size");
        }

        return wallet;
    }
    public static String publicKeyToString(byte[] pubKey) {
        StringBuilder sb = new StringBuilder();
        for (byte b : pubKey) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static Ed25519PublicKeyParameters stringToPublicKey(String s) {
        if (s.length() != 64) {
            throw new IllegalArgumentException("Invalid public key string");
        }

        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }

        return new Ed25519PublicKeyParameters(data, 0);
    }

    public static String signatureToString(byte[] signature) {
        StringBuilder sb = new StringBuilder();
        for (byte b : signature) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static TransactionSignature stringToSignature(String s) {
        if (s.length() != 128) {
            throw new IllegalArgumentException("Invalid signature string");
        }

        var signature = new TransactionSignature();
        int len = s.length();
        for (int i = 0; i < len; i += 2) {
            signature.signature[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return signature;
    }
    
    public static SHA256Hash stringToSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(
              input.getBytes(StandardCharsets.UTF_8));
            SHA256Hash sha256Hash = new SHA256Hash();
            System.arraycopy(encodedhash, 0, sha256Hash.hash, 0, encodedhash.length);
            return sha256Hash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    public static String SHA256toString(SHA256Hash sha256Hash) {
        StringBuilder hexString = new StringBuilder(2 * sha256Hash.hash.length);
        for (byte b : sha256Hash.hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static byte[] longToBytes(long x) {
        byte[] buffer = new byte[8];
        for (int i = 0; i < 8; i++) {
            buffer[7 - i] = (byte) (x >>> (i * 8));
        }
        return buffer;
    }
}
