package rhizome.core.api;

import java.math.BigInteger;
import java.util.List;
import io.activej.promise.Promise;
import rhizome.core.block.Block;
import rhizome.core.block.dto.BlockDto;
import rhizome.core.transaction.dto.TransactionDto;

public interface NodeInterface {
    Promise<Boolean> isOnline();
    Promise<Long> getCurrentBlockCount();
    Promise<BigInteger> getTotalWork();
    Promise<String> getName();
    Promise<?> getPeers();
    Promise<?> addPeer();
    Promise<BlockDto> getBlock();
    Promise<Boolean> submitBlock(BlockDto block);
    Promise<List<Block>> readBlocks(long startId, long endId);
    Promise<List<BlockDto>> readHeaders(long startId, long endId);
    Promise<?> getMiningProblem();
    Promise<Boolean> sendTransaction(TransactionDto transaction);
    Promise<?> verifyTransaction(TransactionDto transaction);
    Promise<Double> getNetworkDifficulty();
}
