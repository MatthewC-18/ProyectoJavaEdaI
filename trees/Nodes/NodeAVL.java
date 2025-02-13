package trees.nodes;

public class NodeAVL {
    public int key;
    public int height;
    public NodeAVL left, right;

    public NodeAVL(int d) {
        this.key = d;
        this.height = 1;
    }
}
