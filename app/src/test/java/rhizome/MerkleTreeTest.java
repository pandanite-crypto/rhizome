package rhizome;

import org.junit.jupiter.api.Test;

import rhizome.core.common.Utils.SHA256Hash;
import rhizome.core.merkletree.MerkleTree;
import rhizome.core.merkletree.MerkleTree.HashTree;
import rhizome.core.transaction.Transaction;
import rhizome.core.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static rhizome.core.common.Crypto.concatHashes;

import java.util.ArrayList;
import java.util.List;

class MerkleTreeTest {

    @Test
    void singleNodeWorks() {
        MerkleTree m = new MerkleTree();
        User miner = User.create();
        Transaction a = miner.mine();
        List<Transaction> items = new ArrayList<>();
        items.add(a);
        m.setItems(items);
        var proof = m.getMerkleProof(a);
        assertEquals(proof.get().getLeft().getHash(), a.getHash());
        assertEquals(proof.get().getRight().getHash(), a.getHash());
        SHA256Hash ha = a.getHash();
        assertEquals(proof.get().getHash(), concatHashes(ha, ha, false, false));
    }

    private boolean checkProofRecursive(HashTree hashTree) {
        if (hashTree.getLeft() == null && hashTree.getRight() == null) {
            // fringe node
            return true;
        } else {
            if (!concatHashes(hashTree.getLeft().getHash(), hashTree.getRight().getHash(), false, false).equals(hashTree.getHash())) return false;
            return checkProofRecursive(hashTree.getLeft()) && checkProofRecursive(hashTree.getRight());
        }
    }

    @Test
    void singleThreeNodesWorks() {
        MerkleTree m = new MerkleTree();
        User miner = User.create();
        User receiver = User.create();
        Transaction a = miner.mine();
        Transaction b = miner.send(receiver, 50);
        Transaction c = miner.send(receiver, 50);
        List<Transaction> items = new ArrayList<>();
        items.add(a);
        items.add(b);
        items.add(c);
        m.setItems(items);
        var proof = m.getMerkleProof(a);
        
        assertEquals(concatHashes(proof.get().getLeft().getHash(), proof.get().getRight().getHash(), false, false), proof.get().getHash());
        assertTrue(checkProofRecursive(proof.get()));
    }

    @Test
    void largerTreeWorks() {
        MerkleTree m = new MerkleTree();
        User miner = User.create();
        User receiver = User.create();

        List<Transaction> items = new ArrayList<>();
        for (int i = 0; i < 4000; i++) {
            items.add(miner.send(receiver, i));
        }
        m.setItems(items);
        var proof = m.getMerkleProof(items.get(4));
        assertTrue(checkProofRecursive(proof.get()));
    }
}
