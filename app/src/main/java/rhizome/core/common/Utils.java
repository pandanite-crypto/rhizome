package rhizome.core.common;

import org.json.*;

import rhizome.core.transaction.TransactionAmount;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.bouncycastle.crypto.digests.RIPEMD160Digest;

import java.util.Formatter;
import java.util.Map;
import java.util.Vector;


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
        try (Formatter formatter = new Formatter(sb)) {
            for (byte b : p) {
                formatter.format("%02x", b);
            }
        }
        return sb.toString();
    }

    public static String publicKeyToString(byte[] pubKey) {
        StringBuilder sb = new StringBuilder();
        try (Formatter formatter = new Formatter(sb)) {
            for (byte b : pubKey) {
                formatter.format("%02x", b);
            }
        }
        return sb.toString();
    }

    public static String signatureToString(byte[] signature) {
        StringBuilder sb = new StringBuilder();
        try (Formatter formatter = new Formatter(sb)) {
            for (byte b : signature) {
                formatter.format("%02x", b);
            }
        }
        return sb.toString();
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
