package rhizome;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import rhizome.core.block.Block;
import rhizome.core.block.BlockImpl;
import rhizome.core.common.Utils.PublicWalletAddress;
import rhizome.core.common.Utils.SHA256Hash;
import rhizome.core.transaction.Transaction;
import rhizome.core.transaction.TransactionImpl;
import rhizome.core.user.User;
import rhizome.persistence.leveldb.LevelDBPersistence;

class LevelDBPersistenceTests {

    private static final String TEST_DB_PATH = "./test-data/tmpdb";
    private LevelDBPersistence blocks;
    private User miner;
    private User receiver;

    @BeforeEach
    void setUp() throws IOException {
        blocks = new LevelDBPersistence(TEST_DB_PATH);
        miner = User.create();
        receiver = User.create();
    }

    @AfterEach
    void tearDown() throws IOException {
        blocks.deleteDB();
    }

    @Test
    void testStoresBlock() throws IOException {

        Block a = Block.empty();
        ((BlockImpl) a).setId(2);
        Transaction t = miner.mine();
        a.addTransaction(t);

        // send tiny shares to receiver:
        for (int i = 0; i < 5; i++) {
            Transaction t2 = miner.send(receiver, 1);
            ((TransactionImpl)t2).setTimestamp(i);
            a.addTransaction(t2);
        }

        assertFalse(blocks.hasBlock(2));
        blocks.addBlock(a);
        assertTrue(blocks.hasBlock(2));
        Block b = blocks.getBlock(2);
        assertEquals(b, a);

        // test we can get transactions for wallets
        PublicWalletAddress to = receiver.getAddress();
        List<SHA256Hash> txIdsTo = blocks.getTransactionsForWallet(to);
        assertEquals(5, txIdsTo.size());

        PublicWalletAddress from = miner.getAddress();
        List<SHA256Hash> txIdsFrom = blocks.getTransactionsForWallet(from);
        assertEquals(6, txIdsFrom.size());

        // test transactions are removed
        blocks.removeBlockWalletTransactions(b);
        txIdsTo = blocks.getTransactionsForWallet(to);
        txIdsFrom = blocks.getTransactionsForWallet(from);
        assertEquals(0, txIdsTo.size());
        assertEquals(0, txIdsFrom.size());
    }
}