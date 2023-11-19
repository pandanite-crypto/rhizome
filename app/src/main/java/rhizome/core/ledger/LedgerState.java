package rhizome.core.ledger;

import java.util.Map;
import rhizome.core.common.Utils.PublicWalletAddress;
import rhizome.core.transaction.TransactionAmount;

public record LedgerState(Map<PublicWalletAddress, TransactionAmount> state) {}
