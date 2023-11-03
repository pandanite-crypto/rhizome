package rhizome.core.merkletree;
import lombok.Getter;
import lombok.Setter;
import rhizome.core.common.Utils.SHA256Hash;
import rhizome.core.transaction.Transaction;


import static rhizome.core.common.Crypto.concatHashes;

import java.util.*;

@Getter
@Setter
public class MerkleTree {
    private HashTree root;
    private Map<SHA256Hash, HashTree> fringeNodes;

    public MerkleTree() {
        this.root = null;
        this.fringeNodes = new HashMap<>();
    }

    public void setItems(List<Transaction> items) {
        var sortedItems = new ArrayList<>(items);
        sortedItems.sort(Comparator.comparing(Transaction::getHash).reversed());

        var q = new LinkedList<HashTree>();
        sortedItems.forEach(item -> {
            var hash = item.getHash();
            var node = new HashTree(hash);
            fringeNodes.put(hash, node);
            q.add(node);
        });

        if (q.size() % 2 == 1) {
            q.add(new HashTree(q.peek().getHash()));
        }

        while (q.size() > 1) {
            var a = q.poll();
            var b = q.poll();
            var parent = new HashTree(concatHashes(a.getHash(), b.getHash(), false, false));
            a.setParent(parent);
            b.setParent(parent);
            parent.setLeft(a);
            parent.setRight(b);
            q.add(parent);
        }

        this.root = q.poll();
    }

    public SHA256Hash getRootHash() {
        return this.root.getHash();
    }

    public Optional<HashTree> getMerkleProof(Transaction t) {
        return Optional.ofNullable(fringeNodes.get(t.getHash()))
                       .map(f -> buildProof(f, null));
    }
    
    private HashTree buildProof(HashTree fringe, HashTree previousNode) {
        var result = new HashTree(fringe.getHash());
        if (previousNode != null) {
            if (fringe.getLeft() != null && fringe.getLeft() != previousNode) {
                result.setLeft(fringe.getLeft());
                result.setRight(previousNode);
            } else if (fringe.getRight() != null && fringe.getRight() != previousNode) {
                result.setRight(fringe.getRight());
                result.setLeft(previousNode);
            }
        }
        if (fringe.getParent() != null) {
            return buildProof(fringe.getParent(), fringe);
        } else {
            return result;
        }
    }

    @Getter
    @Setter
    public static class HashTree {
        private SHA256Hash hash;
        private HashTree parent;
        private HashTree left;
        private HashTree right;

        public HashTree(SHA256Hash hash) {
            this.hash = hash;
            this.parent = this.left = this.right = null;
        }
    }

}
