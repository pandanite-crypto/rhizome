package rhizome.core.crypto;

import java.util.HexFormat;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.util.encoders.Hex;

public record PublicKey(Ed25519PublicKeyParameters key) {


    public static PublicKey of(byte[] bytes) {
        return new PublicKey(new Ed25519PublicKeyParameters(bytes, 0));
    }

    public static PublicKey of(String s) {
        if ("".equals(s)) {
            return null;
        }
        if (s.length() != 64) {
            throw new IllegalArgumentException("Invalid public key string length. Expected 64 characters for a 32-byte key.");
        }
        return new PublicKey(new Ed25519PublicKeyParameters(Hex.decode(s), 0));    
    }

    public String toString() {
        if (key == null) {
            return "";
        }
        return HexFormat.of().withUpperCase().formatHex(key.getEncoded() == null ? new byte[0] : key.getEncoded());    
    }
}
