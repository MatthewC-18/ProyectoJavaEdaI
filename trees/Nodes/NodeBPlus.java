package trees.nodes;

import java.util.Arrays;

public class NodeBPlus {
    public int[] keys;              // array de claves
    public NodeBPlus[] children;    // array de punteros a hijos
    public NodeBPlus parent;        // puntero al padre
    public NodeBPlus nextLeaf;      // puntero a la hoja siguiente (si es hoja)
    public boolean isLeaf;
    public int n;                   // número de claves actualmente almacenadas

    private static final int T = 2; // Debe coincidir con BPlusTree.T

    public NodeBPlus(boolean isLeaf) {
        this.isLeaf = isLeaf;
        this.keys = new int[2 * T];       // en B+ a menudo se reserva 2T en la hoja
        this.children = new NodeBPlus[2 * T + 1];
        this.n = 0;
        this.nextLeaf = null;
        this.parent = null;
    }

    @Override
    public String toString() {
        return "NodeBPlus{" +
                "isLeaf=" + isLeaf +
                ", keys=" + Arrays.toString(Arrays.copyOf(keys, n)) +
                '}';
    }
}
