package rhizome.core.user;

import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserImpl implements User {
    private Ed25519PublicKeyParameters publicKey;
    private Ed25519PrivateKeyParameters privateKey;
}
