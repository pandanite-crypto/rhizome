package rhizome.core.block;

import rhizome.core.common.Utils.SHA256Hash;

public record BlockHeader(
    int id,
    long timestamp,
    int difficulty,
    int numTranactions,
    SHA256Hash lastBlockHash,
    SHA256Hash merkleRoot,
    SHA256Hash nonce
) {}
