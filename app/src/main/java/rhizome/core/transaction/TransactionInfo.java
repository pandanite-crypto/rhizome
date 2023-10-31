package rhizome.core.transaction;

import rhizome.core.common.Utils.PublicWalletAddress;

public record TransactionInfo(
    String signature,
    String signingKey,
    long timestamp,
    PublicWalletAddress to,
    PublicWalletAddress from,
    TransactionAmount amount,
    TransactionAmount fee,
    boolean isTransactionFee
) {}
