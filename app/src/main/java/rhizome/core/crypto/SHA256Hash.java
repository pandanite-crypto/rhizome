package rhizome.core.crypto;

import io.activej.bytebuf.ByteBuf;
import rhizome.core.common.SimpleHashType;

public record SHA256Hash (ByteBuf hash) implements SimpleHashType {

    public static SHA256Hash empty() {
        return new SHA256Hash(SimpleHashType.empty(SIZE));
    }

    public static SHA256Hash random() {
        return new SHA256Hash(SimpleHashType.random(SIZE));
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof SHA256Hash)) {
            return false;
        }
        return hash.isContentEqual(((SHA256Hash) other).hash());
    }

    public static final int SIZE = 32;
    @Override
    public int getSize() {
        return SIZE;
    }
}
