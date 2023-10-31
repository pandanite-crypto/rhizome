package rhizome;

import org.junit.jupiter.api.Test;

import rhizome.core.block.Block;
import rhizome.core.user.User;

import static org.junit.jupiter.api.Assertions.*;

class BlockTest {

    @Test
    void checkBlockJsonSerialization() {
        var a = Block.empty();
        var miner = new User();
        var receiver = new User();
        var t = miner.mine();
        a.addTransaction(t);
        // send tiny shares to receiver:
        for(int i = 0; i < 5; i++) {
            a.addTransaction(miner.send(receiver, 1));
        }
        Block b;
        b = Block.of(a.toJson());
        assertEquals(a, b);
    }

    @Test
    void checkBlockStructSerialization() {
        var a = Block.empty();
        var miner = new User();
        var receiver = new User();
        var t = miner.mine();
        a.addTransaction(t);
        // send tiny shares to receiver:
        for(int i = 0; i < 5; i++) {
            a.addTransaction(miner.send(receiver, 1));
        }
        var d = a.serialize();
        var b = Block.of(d, a.getTransactions());
        assertEquals(a, b);
    }

    // @Test
    // void checkBlockNetworkSerialization() {
    //     var a = Block.of();
    //     var miner = new User();
    //     var receiver = new User();
    //     var t = miner.mine();
    //     a.addTransaction(t);
    //     // send tiny shares to receiver:
    //     for(int i = 0; i < 5; i++) {
    //         a.addTransaction(miner.send(receiver, 1));
    //     }
    //     var d = a.serialize();
    //     // Assuming blockHeaderToBuffer and blockHeaderFromBuffer are defined
    //     char[] buf = blockHeaderToBuffer(d);
    //     BlockHeader c = blockHeaderFromBuffer(buf);
    //     AbstractBlock check = new AbstractBlock(c, a.getTransactions());
    //     assertEquals(check, a);
    // }

    // @Test
    // void blockHashConsistency() {
    //     String A = "...";  // The rest of your JSON string
    //     String B = "...";  // The rest of your JSON string
    //     AbstractBlock a = new AbstractBlock(A);
    //     AbstractBlock b = new AbstractBlock(B);
    //     assertNotEquals(a.getHash(), b.getHash());
    // }
}
