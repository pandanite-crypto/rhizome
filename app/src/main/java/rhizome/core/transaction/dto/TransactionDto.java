package rhizome.core.transaction.dto;

import org.jetbrains.annotations.NotNull;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import lombok.Getter;
import rhizome.core.net.BinarySerializable;

@Getter
public class TransactionDto implements BinarySerializable {
    @Serialize public final String signature;
    @Serialize public final String signingKey;
    @Serialize public final long timestamp;
    @Serialize public final byte[] to;
    @Serialize public final byte[] from;
    @Serialize public final long amount;
    @Serialize public final long fee;
    @Serialize public final boolean isTransactionFee;

    public static final int BUFFER_SIZE = 1024;

    public TransactionDto(
        @Deserialize("signature") String signature, 
        @Deserialize("signingKey") String signingKey, 
        @Deserialize("timestamp") long timestamp, 
        @Deserialize("to") byte[] to, 
        @Deserialize("from") byte[] from, 
        @Deserialize("amount") long amount, 
        @Deserialize("fee") long fee, 
        @Deserialize("isTransactionFee") boolean isTransactionFee) {

        this.signature = signature;
        this.signingKey = signingKey;
        this.timestamp = timestamp;
        this.to = to;
        this.from = from;
        this.amount = amount;
        this.fee = fee;
        this.isTransactionFee = isTransactionFee;
    }

    @Override
    public @NotNull int getSize() {
        return BUFFER_SIZE;
    }
}
