package trees.nodes;

import java.util.Arrays;

public class NodeB {
    public int[] keys;
    public NodeB[] children;
    public int nKeys;      // número actual de claves
    public boolean isLeaf; // indica si es hoja

    private static final int T = 2; // Debe concordar con BTree.T

    public NodeB(boolean isLeaf) {
        this.isLeaf = isLeaf;
        // Máximo de claves = 2*T - 1
        // Máximo de hijos = 2*T
        this.keys = new int[2 * T - 1];
        this.children = new NodeB[2 * T];
        this.nKeys = 0;
    }

    // Método auxiliar para depuración/imprimir
    public int[] getKeysArray() {
        return Arrays.copyOf(keys, nKeys);
    }
}
