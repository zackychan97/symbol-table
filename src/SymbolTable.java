import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class SymbolTable<Key extends Comparable<Key>, Value> implements ISymbolTable<Key , Value> {
    private class Node{
        Key key;            /* The key that the node will store */
        Value val;          /* The value the node will store */
        Node left, right;   /* pointers to the left and right children of this node */
        int size;           /* The number of nodes in the subtree rooted by this node */
        public Node(Key k, Value v, int s){
            this.key = k;
            this.val = v;
            this.size = s;
            this.left = null;
            this.right = null;
        }
    }

    /* the root of our tree */
    Node root;


    public void put(Key k, Value v) throws InvalidParameterException {
        if(k == null) { throw new InvalidParameterException("no key was passed"); }
        if (v == null) {
            throw new InvalidParameterException("no val was passed");
        }
        else{
            root = put(root, k, v);
        }
    }

    private Node put(Node currentRoot, Key key, Value val){
        if (currentRoot == null) {
            return new Node(key, val, 1); // 0 would be 3rd param??
        }
        int compareDiff = key.compareTo(currentRoot.key);
        if (compareDiff == 0) {
            throw new InvalidParameterException("node is a duplicate");
        } else if (compareDiff < 0) {
            currentRoot.size++;
            currentRoot.left = put(currentRoot.left, key, val);
        } else {
            currentRoot.size++;
            currentRoot.right = put(currentRoot.right, key, val);
        }

        if (currentRoot.left != null && currentRoot.right != null) {
            currentRoot.size = 1 + currentRoot.left.size + currentRoot.right.size;
        } else if (currentRoot.left != null) {
            currentRoot.size = 1 + currentRoot.left.size;
        } else {
            currentRoot.size = 1 + currentRoot.right.size;
        }
        return currentRoot;
    }

    public Value get(Key k) throws NoSuchElementException, InvalidParameterException {
        if(k == null) { throw new InvalidParameterException("no key was passed"); }

        return get(root, k);
    }

    private Value get(Node currentRoot, Key key){
        if (currentRoot == null) {
            throw new NoSuchElementException("root was null so element can't exist");
            //return null;
        }
        int compareDiff = key.compareTo(currentRoot.key);
        if (compareDiff == 0) {
            return currentRoot.val;
        } else if (compareDiff < 0 && currentRoot.left != null) {
            return get(currentRoot.left, key); // may need to change this
        } else if (compareDiff > 0 && currentRoot.right != null) {
            return get(currentRoot.right, key); // may need to change this
        } else {
            throw new NoSuchElementException("root was null so element can't exist");
        }
    }

    public void del(Key k) throws NoSuchElementException {
        if(k == null) { throw new InvalidParameterException("no key was passed"); }
        root = del(root, k);
    }

    private Node del(Node currentRoot, Key key){
        if (currentRoot == null) {
            throw new NoSuchElementException("currentRoot is null");
        }
        int compareDiff = key.compareTo(currentRoot.key);
        if (compareDiff < 0) {
            currentRoot.left = del(currentRoot.left, key);
        } else if (compareDiff > 0) {
            currentRoot.right = del(currentRoot.right, key);
        } else {
            if(currentRoot.left == null) {
                return currentRoot.right;
            } else if (currentRoot.right == null) {
                return currentRoot.left;
            }

            Node tmp = currentRoot;
            currentRoot = getMin(tmp.right);
            currentRoot.right = deleteMin(tmp.right);
            currentRoot.left = tmp.left;
        }

        if (currentRoot.left != null && currentRoot.right != null) {
            currentRoot.size = currentRoot.left.size + currentRoot.right.size + 1;
        } else if (currentRoot.left != null) {
            currentRoot.size = currentRoot.left.size + 1;
        } else if (currentRoot.right != null){
            currentRoot.size = currentRoot.right.size + 1;
        } else {
            currentRoot.size = 1;
        }

        return currentRoot;
    }

    private Node getMin(Node currentRoot){
        if (currentRoot == null) {
            return null;
        }
        if (currentRoot.left == null) {
            return currentRoot;
        } else {
            return getMin(currentRoot.left);
        }
    }

    private Node deleteMin(Node currentRoot){
        if (currentRoot == null) {
            return null;
        }
        if (currentRoot.left == null) {
            return currentRoot.right;
        } else {
            currentRoot.left = deleteMin(currentRoot.left);
        }
        return currentRoot;
    }


    // need to complete
    public boolean contains(Key k){
        if (k == null) {
            throw new InvalidParameterException("key is null");
        }
        return contains(root, k);
    }

    private boolean contains(Node currentRoot, Key key){
        if (currentRoot == null) {
            return false;
        }
        int compareDiff = key.compareTo(currentRoot.key);
        if (compareDiff == 0) {
            return true;
        } else if (compareDiff < 0) {
            return contains(currentRoot.left, key);
        } else {
            return contains(currentRoot.right, key);
        }
    }

    public boolean isEmpty() {
        return root == null;
    }

    public int size() {
        return size(root);
    }

    private int size(Node x) {
        if (x == null) return 0;
        else return x.size;
    }

    public LinkedList<Key> keys() {
        return keys(root);
    }

    private LinkedList<Key> keys(Node currentRoot){
        if (currentRoot == null) {
            return new LinkedList<>();
        }

        LinkedList<Key> myList = new LinkedList<>();
        myList.addAll(keys(currentRoot.left));
        myList.add(currentRoot.key);
        myList.addAll(keys(currentRoot.right));
        return myList;
    }

    /*******************************************************************************************************************
     * Tree integrity checking functions as written by the book's authors. Do not modify any of the following functions,
     * they should be used to ensure that your tree is correctly structured
     ******************************************************************************************************************/

    public boolean check() {
        if (!isBST())            StdOut.println("Not in symmetric order");
        if (!isSizeConsistent()) StdOut.println("Subtree counts not consistent");
        if (!isRankConsistent()) StdOut.println("Ranks not consistent");
        return isBST() && isSizeConsistent() && isRankConsistent();
    }

    // does this binary tree satisfy symmetric order?
    // Note: this test also ensures that data structure is a binary tree since order is strict
    public boolean isBST() {
        return isBST(root, null, null);
    }

    // is the tree rooted at x a BST with all keys strictly between min and max
    // (if min or max is null, treat as empty constraint)
    // Credit: Bob Dondero's elegant solution
    private boolean isBST(Node x, Key min, Key max) {
        if (x == null) return true;
        if (min != null && x.key.compareTo(min) <= 0) return false;
        if (max != null && x.key.compareTo(max) >= 0) return false;
        return isBST(x.left, min, x.key) && isBST(x.right, x.key, max);
    }

    // are the size fields correct?
    private boolean isSizeConsistent() { return isSizeConsistent(root); }
    private boolean isSizeConsistent(Node x) {
        if (x == null) return true;
        if (x.size != size(x.left) + size(x.right) + 1) return false;
        return isSizeConsistent(x.left) && isSizeConsistent(x.right);
    }

    // check that ranks are consistent
    private boolean isRankConsistent() {
        for (int i = 0; i < size(); i++)
            if (i != rank(select(i))) return false;
        for (Key key : keys())
            if (key.compareTo(select(rank(key))) != 0) return false;
        return true;
    }

    /**
     * Return the number of keys in the symbol table strictly less than {@code key}.
     *
     * @param  key the key
     * @return the number of keys in the symbol table strictly less than {@code key}
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public int rank(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to rank() is null");
        return rank(key, root);
    }

    // Number of keys in the subtree less than key.
    private int rank(Key key, Node x) {
        if (x == null) return 0;
        int cmp = key.compareTo(x.key);
        if      (cmp < 0) return rank(key, x.left);
        else if (cmp > 0) return 1 + size(x.left) + rank(key, x.right);
        else              return size(x.left);
    }

    public Key select(int rank) {
        if (rank < 0 || rank >= size()) {
            throw new IllegalArgumentException("argument to select() is invalid: " + rank);
        }
        return select(root, rank);
    }

    // Return key in BST rooted at x of given rank.
    // Precondition: rank is in legal range.
    private Key select(Node x, int rank) {
        if (x == null) return null;
        int leftSize = size(x.left);
        if      (leftSize > rank) return select(x.left,  rank);
        else if (leftSize < rank) return select(x.right, rank - leftSize - 1);
        else                      return x.key;
    }
}
