package rhizome.core.common;

import rhizome.core.transaction.TransactionAmount;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Map;

import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.util.encoders.Hex;

import io.activej.bytebuf.ByteBuf;

public class Utils {

    private static final HexFormat hexFormat = HexFormat.of().withUpperCase();

    private Utils() {}
    
    public record PublicWalletAddress(ByteBuf address) implements SimpleHashType {
        public PublicWalletAddress {
            checkSize(address);
        }

        public static PublicWalletAddress empty() {
            return new PublicWalletAddress(SimpleHashType.empty(SIZE));
        }

        public static PublicWalletAddress random() {
            return new PublicWalletAddress(SimpleHashType.random(SIZE));
        }

        public static PublicWalletAddress fromPublicKey(Ed25519PublicKeyParameters publicKey){
            byte[] publicKeyBytes = publicKey.getEncoded();

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

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof PublicWalletAddress)) {
                return false;
            }
            return address.isContentEqual(((PublicWalletAddress) other).address());
        }

        public static int SIZE = 25;
        @Override
        public int getSize() {
            return 25;
        }
    }
    
    public static class TransactionSignature {
        public byte[] signature = new byte[64];

        public static TransactionSignature random() {
            var random = new TransactionSignature();
            new SecureRandom().nextBytes(random.signature);
            return random;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof TransactionSignature)) {
                return false;
            }

            return Arrays.equals(this.signature, ((TransactionSignature) other).signature);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(signature);
        }
    }
    
    public static class LedgerState {
        public Map<PublicWalletAddress, TransactionAmount> state;
    }
    
    public static class SHA256Hash implements Comparable<SHA256Hash> {
        public byte[] hash = new byte[32];
    
        public SHA256Hash(byte[] readNetworkSHA256) {
            this.hash = readNetworkSHA256;
        }
    
        public SHA256Hash() {
        }
    
        @Override
        public boolean equals(Object other) {
            if (!(other instanceof SHA256Hash)) {
                return false;
            }
            return Arrays.equals(this.hash, ((SHA256Hash) other).hash);
        }
    
        @Override
        public int hashCode() {
            return Arrays.hashCode(hash);
        }
    
        @Override
        public int compareTo(SHA256Hash o) {
            for (int i = 0; i < this.hash.length; i++) {
                int compare = Byte.compare(this.hash[i], o.hash[i]);
                if (compare != 0) {
                    return compare;
                }
            }
            return 0;
        }
    }
    
    public static class RIPEMD160Hash {
        public byte[] hash = new byte[20];
    }

    public static String walletAddressToString(ByteBuf p) {
        StringBuilder sb = new StringBuilder();
        while (p.canRead()) {
            sb.append(String.format("%02x", p.readByte()));
		}
        p.head(0);
        return sb.toString();
    }
    
    public static PublicWalletAddress stringToWalletAddress(String s) {
        if (s.length() != 50) {
            throw new IllegalArgumentException("Invalid wallet address string");
        }

        ByteBuf buf = ByteBuf.wrapForWriting(new byte[25]);

        for (int i = 0; i < s.length(); i += 2) {
            byte b = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
            buf.writeByte(b);
        }

        return new PublicWalletAddress(buf);
    }  

    public static String publicKeyToString(Ed25519PublicKeyParameters pubKey) {
        if (pubKey == null) {
            return "";
        }
        return hexFormat.formatHex(pubKey.getEncoded() == null ? new byte[0] : pubKey.getEncoded());
    }

    public static Ed25519PublicKeyParameters stringToPublicKey(String s) {
        if ("".equals(s)) {
            return null;
        }
        if (s.length() != 64) {
            throw new IllegalArgumentException("Invalid public key string length. Expected 64 characters for a 32-byte key.");
        }
        return new Ed25519PublicKeyParameters(Hex.decode(s), 0);
    }

    public static String privateKeyToString(Ed25519PrivateKeyParameters privateKey) {
        return hexFormat.formatHex(privateKey.getEncoded() == null ? new byte[0] : privateKey.getEncoded());
    }

    public static Ed25519PrivateKeyParameters stringToPrivateKey(String hexString) throws IllegalArgumentException {
        if (hexString.length() != 64) { // 32 bytes * 2 characters per byte
            throw new IllegalArgumentException("Invalid private key string length");
        }
        return new Ed25519PrivateKeyParameters(Hex.decode(hexString), 0);
    }

    public static String signatureToString(byte[] signature) {
        return hexFormat.formatHex(signature);
    }

    public static TransactionSignature stringToSignature(String s) {
        if (s.length() != 128) {
            throw new IllegalArgumentException("Invalid signature string");
        }
        
        var signature = new TransactionSignature();
        byte[] bytes = HexFormat.of().parseHex(s);
        System.arraycopy(bytes, 0, signature.signature, 0, bytes.length);
        
        return signature;
    }
    
    public static SHA256Hash stringToSHA256(String hexString) {
        return new SHA256Hash(hexStringToByteArray(hexString));
    }

    
    public static String SHA256toString(SHA256Hash sha256Hash) {
        return hexFormat.formatHex(sha256Hash.hash);
    }
    
    public static byte[] longToBytes(long value) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putLong(value);
        return buffer.array();
    }
    
    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.BIG_ENDIAN);
        return buffer.getLong();
    }

    public static String bytesToHex(byte[] bytes) {
        return hexFormat.formatHex(bytes);
    }

    public static byte[] hexStringToByteArray(String s) {
        return hexFormat.parseHex(s);
    }
}
