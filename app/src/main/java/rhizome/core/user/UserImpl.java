package rhizome.core.user;

import java.util.Arrays;
import java.util.Objects;

import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.json.JSONObject;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserImpl implements User {
    private Ed25519PublicKeyParameters publicKey;
    private Ed25519PrivateKeyParameters privateKey;

    @Override
    public JSONObject toJson() {
        return toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Arrays.equals(getPublicKey().getEncoded(), user.getPublicKey().getEncoded()) &&
            Arrays.equals(getPrivateKey().getEncoded(), user.getPrivateKey().getEncoded());
    }

    @Override
    public int hashCode() {
        return Objects.hash(publicKey, privateKey);
    }
}
