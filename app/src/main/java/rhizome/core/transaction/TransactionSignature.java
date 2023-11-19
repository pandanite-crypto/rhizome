package rhizome.core.transaction;

import io.activej.bytebuf.ByteBuf;
import rhizome.core.common.SimpleHashType;

public record TransactionSignature(ByteBuf signature) implements SimpleHashType {

    public static TransactionSignature empty() {
        return new TransactionSignature(SimpleHashType.empty(SIZE));
    }

    public static TransactionSignature random() {
        return new TransactionSignature(SimpleHashType.random(SIZE));
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TransactionSignature)) {
            return false;
        }
        return signature.isContentEqual(((TransactionSignature) other).signature());
    }

    public static final int SIZE = 64;
    @Override
    public int getSize() {
        return SIZE;
    }
    
}
