package rhizome;

import org.junit.jupiter.api.Test;

import rhizome.core.transaction.Transaction;
import rhizome.core.transaction.TransactionImpl;
import rhizome.core.transaction.TransactionInfo;
import rhizome.core.user.User;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTests {

    @Test
    void checkTransactionJsonSerialization() {
        User miner = new User();
        User receiver = new User();

        Transaction t = miner.mine();
        Transaction t2 = miner.send(receiver, 30.0);

        assertTrue(t2.signatureValid());

        // Test the send transaction
        long ts = ((TransactionImpl) t2).getTimestamp();
        Transaction deserialized = Transaction.of(t2.toJson());

        assertTrue(deserialized.signatureValid());
        assertEquals(t2, deserialized);
        assertEquals(ts, ((TransactionImpl) deserialized).getTimestamp());

        // Test mining transaction
        deserialized = Transaction.of(t.toJson());
        ts = ((TransactionImpl) t).getTimestamp();

        assertEquals(t.hashContents(), deserialized.hashContents());
        assertEquals(t, deserialized);
        assertEquals(ts, ((TransactionImpl) deserialized).getTimestamp());
    }

    // @Test
    // void checkTransactionStructSerialization() {
    //     User miner = new User();
    //     User receiver = new User();

    //     Transaction t = miner.mine();
    //     Transaction t2 = miner.send(receiver, 30.0);

    //     assertTrue(t2.signatureValid());

    //     // Test the send transaction
    //     long ts = t2.getTimestamp();
    //     TransactionInfo serialized = t2.serialize();
    //     Transaction deserialized = new Transaction(serialized);

    //     assertTrue(deserialized.signatureValid());
    //     assertEquals(t2, deserialized);
    //     assertEquals(ts, deserialized.getTimestamp());

    //     // Test mining transaction
    //     serialized = t.serialize();
    //     deserialized = new Transaction(serialized);
    //     ts = t.getTimestamp();

    //     assertEquals(t.hashContents(), deserialized.hashContents());
    //     assertEquals(t, deserialized);
    //     assertEquals(ts, deserialized.getTimestamp());
    // }

    // @Test
    // void checkTransactionCopy() {
    //     User miner = new User();
    //     User receiver = new User();

    //     Transaction t = miner.mine();
    //     Transaction t2 = miner.send(receiver, 30.0);

    //     Transaction a = new Transaction(t);
    //     Transaction b = new Transaction(t2);

    //     assertEquals(a, t);
    //     assertEquals(b, t2);
    // }

    // @Test
    // void checkTransactionNetworkSerialization() {
    //     User miner = new User();
    //     User receiver = new User();

    //     Transaction a = miner.mine();
    //     Transaction b = miner.send(receiver, 30.0);

    //     TransactionInfo t1 = a.serialize();
    //     TransactionInfo t2 = b.serialize();

    //     byte[] buf1 = new byte[TransactionInfo.BUFFER_SIZE];
    //     byte[] buf2 = new byte[TransactionInfo.BUFFER_SIZE];

    //     TransactionInfo.toBuffer(t1, buf1);
    //     TransactionInfo.toBuffer(t2, buf2);

    //     Transaction a2 = new Transaction(TransactionInfo.fromBuffer(buf1));
    //     Transaction b2 = new Transaction(TransactionInfo.fromBuffer(buf2));

    //     assertEquals(a, a2);
    //     assertEquals(b, b2);
    // }
}