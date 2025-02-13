package trees;

import trees.nodes.NodeBPlus;
import java.util.*;

public class BPlusTree implements ITree {

    private static final int T = 2; 
    private NodeBPlus root; 

    @Override
    public void generarArbolAltura3() {
        /*
         * Insertamos un conjunto de claves para forzar ~3 niveles.
         * Ajusta según tu definición exacta.
         */
        int[] keys = {15, 20, 5, 25, 1, 7, 10, 12, 18, 30, 40, 45, 50};
        for (int k : keys) {
            insertarClave(k);
        }
    }

    @Override
    public void imprimirArbol() {
        if (root == null) {
            System.out.println("Árbol B+ vacío.");
            return;
        }
        imprimirNodo(root, 0);
    }

    private void imprimirNodo(NodeBPlus node, int level) {
        System.out.print("Nivel " + level + (node.isLeaf ? " (Hoja)" : " (Interno)") + " Claves: [");
        for (int i = 0; i < node.n; i++) {
            System.out.print(node.keys[i]);
            if (i < node.n - 1) System.out.print(", ");
        }
        System.out.println("]");

        if (!node.isLeaf) {
            for (int i = 0; i <= node.n; i++) {
                imprimirNodo(node.children[i], level + 1);
            }
        }
    }

    @Override
    public void imprimirMatrizAdyacencia() {
        if (root == null) {
            System.out.println("Árbol B+ vacío.");
            return;
        }
        // Recorremos todos los nodos (internos y hojas) en BFS
        List<NodeBPlus> nodeList = new ArrayList<>();
        Map<NodeBPlus, Integer> indexMap = new HashMap<>();
        buildBFSList(nodeList, indexMap);

        int n = nodeList.size();
        int[][] matrix = new int[n][n];

        // Para cada nodo, marcamos hijos y (opcional) nextLeaf
        for (int i = 0; i < n; i++) {
            NodeBPlus current = nodeList.get(i);
            if (!current.isLeaf) {
                for (int c = 0; c <= current.n; c++) {
                    NodeBPlus child = current.children[c];
                    if (child != null) {
                        int j = indexMap.get(child);
                        matrix[i][j] = 1;
                    }
                }
            }
            // Marcamos el enlace a la siguiente hoja (si existe)
            if (current.isLeaf && current.nextLeaf != null) {
                int j = indexMap.get(current.nextLeaf);
                matrix[i][j] = 1;
            }
        }

        System.out.println("Matriz de Adyacencia (B+ Tree):");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    private void buildBFSList(List<NodeBPlus> nodeList, Map<NodeBPlus, Integer> indexMap) {
        Queue<NodeBPlus> queue = new LinkedList<>();
        queue.offer(root);
        int idx = 0;
        while (!queue.isEmpty()) {
            NodeBPlus current = queue.poll();
            nodeList.add(current);
            indexMap.put(current, idx++);
            if (!current.isLeaf) {
                for (int i = 0; i <= current.n; i++) {
                    queue.offer(current.children[i]);
                }
            }
        }
        // También podríamos recorrer hojas mediante nextLeaf, 
        // pero BFS ya agregó todo.
    }

    @Override
    public boolean buscarClave(int key) {
        if (root == null) {
            System.out.println("Árbol B+ vacío.");
            return false;
        }
        System.out.println("\n=== Búsqueda paso a paso en B+ Tree ===");
        NodeBPlus leaf = buscarHoja(root, key);
        // Buscamos dentro de la hoja
        for (int i = 0; i < leaf.n; i++) {
            if (leaf.keys[i] == key) {
                System.out.println("¡Clave encontrada! (Hoja con claves " + leaf + ")");
                return true;
            }
        }
        System.out.println("No existe en el árbol, un nodo con la clave solicitada.");
        return false;
    }

    private NodeBPlus buscarHoja(NodeBPlus node, int key) {
        // Paso a paso
        if (node.isLeaf) {
            System.out.println("Llegamos a hoja: " + node);
            return node;
        }
        int i = 0;
        while (i < node.n && key >= node.keys[i]) {
            i++;
        }
        System.out.println("NODO interno " + node + " => bajando al hijo " + i);
        return buscarHoja(node.children[i], key);
    }

    @Override
    public void insertarClave(int key) {
        System.out.println("\n=== Insertando " + key + " en B+ Tree ===");
        if (root == null) {
            root = new NodeBPlus(true);
            root.keys[0] = key;
            root.n = 1;
            return;
        }
        // Buscamos la hoja donde insertar
        NodeBPlus leaf = buscarHoja(root, key);
        // Insertamos la clave en la hoja
        insertarEnHoja(leaf, key);
        // Si la hoja está llena, spliteamos
        if (leaf.n == 2 * T) {
            splitLeaf(leaf);
        }
    }

    private void insertarEnHoja(NodeBPlus leaf, int key) {
        int i = leaf.n - 1;
        while (i >= 0 && leaf.keys[i] > key) {
            leaf.keys[i + 1] = leaf.keys[i];
            i--;
        }
        leaf.keys[i + 1] = key;
        leaf.n++;
    }

    private void splitLeaf(NodeBPlus leaf) {
        System.out.println("Split en hoja: " + leaf);
        NodeBPlus newLeaf = new NodeBPlus(true);
        int mid = leaf.n / 2;
        // Copiamos la mitad de las claves al newLeaf
        for (int i = mid; i < leaf.n; i++) {
            newLeaf.keys[i - mid] = leaf.keys[i];
            newLeaf.n++;
            leaf.keys[i] = 0; // Opcional, limpiar
        }
        leaf.n = mid;
        // Ajustamos puntero de siguiente hoja
        newLeaf.nextLeaf = leaf.nextLeaf;
        leaf.nextLeaf = newLeaf;

        // Tenemos que insertar la clave de separación en el nodo padre
        int separationKey = newLeaf.keys[0];
        insertarEnPadre(leaf, separationKey, newLeaf);
    }

    private void insertarEnPadre(NodeBPlus node, int key, NodeBPlus newNode) {
        if (node.parent == null) {
            // Crear un nuevo nodo raíz
            NodeBPlus newRoot = new NodeBPlus(false);
            newRoot.keys[0] = key;
            newRoot.children[0] = node;
            newRoot.children[1] = newNode;
            newRoot.n = 1;

            node.parent = newRoot;
            newNode.parent = newRoot;
            root = newRoot;
            return;
        }

        NodeBPlus parent = node.parent;
        // Insertar key en el parent
        int i = parent.n - 1;
        while (i >= 0 && parent.keys[i] > key) {
            parent.keys[i + 1] = parent.keys[i];
            parent.children[i + 2] = parent.children[i + 1];
            i--;
        }
        parent.keys[i + 1] = key;
        parent.children[i + 2] = newNode;
        parent.n++;
        newNode.parent = parent;

        // Si el padre se desborda, spliteamos
        if (parent.n == 2 * T) {
            splitInternal(parent);
        }
    }

    private void splitInternal(NodeBPlus internalNode) {
        System.out.println("Split en nodo interno: " + internalNode);
        NodeBPlus newInternal = new NodeBPlus(false);
        int mid = internalNode.n / 2;
        // La clave del medio se sube al padre
        int upKey = internalNode.keys[mid];

        // Copiar la mitad de la derecha a newInternal
        newInternal.n = 0;
        for (int i = mid + 1; i < internalNode.n; i++) {
            newInternal.keys[newInternal.n] = internalNode.keys[i];
            newInternal.children[newInternal.n] = internalNode.children[i];
            if (newInternal.children[newInternal.n] != null) {
                newInternal.children[newInternal.n].parent = newInternal;
            }
            newInternal.n++;
        }
        // Último child
        newInternal.children[newInternal.n] = internalNode.children[internalNode.n];
        if (newInternal.children[newInternal.n] != null) {
            newInternal.children[newInternal.n].parent = newInternal;
        }

        internalNode.children[internalNode.n] = null; 
        // Limpieza
        for (int i = mid; i < internalNode.n; i++) {
            internalNode.keys[i] = 0;
            internalNode.children[i + 1] = null;
        }
        internalNode.n = mid;

        // Insertamos en el padre
        insertarEnPadre(internalNode, upKey, newInternal);
    }

    @Override
    public void eliminarClave(int key) {
        System.out.println("\n=== Eliminando " + key + " en B+ Tree ===");
        if (root == null) {
            System.out.println("El árbol B+ está vacío.");
            return;
        }
        // Buscamos la hoja que contendrá la clave
        NodeBPlus leaf = buscarHoja(root, key);
        // Eliminamos de la hoja
        int pos = -1;
        for (int i = 0; i < leaf.n; i++) {
            if (leaf.keys[i] == key) {
                pos = i;
                break;
            }
        }
        if (pos == -1) {
            // No encontrada
            System.out.println("No existe en el árbol, un nodo con la clave solicitada.");
            return;
        }
        // Eliminamos
        System.out.println("Clave " + key + " encontrada en hoja. Eliminando...");
        for (int i = pos; i < leaf.n - 1; i++) {
            leaf.keys[i] = leaf.keys[i + 1];
        }
        leaf.n--;

        // Si la hoja es la raíz, y se quedó vacía
        if (leaf == root && leaf.n == 0) {
            root = null;
            return;
        }
        // Verificamos si viola la ocupación mínima
        if (leaf != root && leaf.n < T - 1) {
            rebalanceLeaf(leaf);
        }
    }

    private void rebalanceLeaf(NodeBPlus leaf) {
        System.out.println("Rebalance de hoja con " + leaf.n + " claves.");
        NodeBPlus parent = leaf.parent;
        if (parent == null) return; // es la raíz

        // Buscar índice de leaf en parent
        int idx = 0;
        while (idx <= parent.n && parent.children[idx] != leaf) {
            idx++;
        }

        // Intentar tomar prestado de hermano izquierdo
        if (idx > 0) {
            NodeBPlus leftSibling = parent.children[idx - 1];
            if (leftSibling.isLeaf && leftSibling.n > T - 1) {
                // Tomar prestado
                for (int i = leaf.n; i > 0; i--) {
                    leaf.keys[i] = leaf.keys[i - 1];
                }
                leaf.keys[0] = leftSibling.keys[leftSibling.n - 1];
                leftSibling.keys[leftSibling.n - 1] = 0;
                leaf.n++;
                leftSibling.n--;
                // Actualizar la clave del parent
                parent.keys[idx - 1] = leaf.keys[0];
                return;
            }
        }

        // Intentar tomar prestado de hermano derecho
        if (idx < parent.n) {
            NodeBPlus rightSibling = parent.children[idx + 1];
            if (rightSibling.isLeaf && rightSibling.n > T - 1) {
                // Tomar prestado
                leaf.keys[leaf.n] = rightSibling.keys[0];
                leaf.n++;
                for (int i = 0; i < rightSibling.n - 1; i++) {
                    rightSibling.keys[i] = rightSibling.keys[i + 1];
                }
                rightSibling.n--;
                // Actualizar la clave del parent
                parent.keys[idx] = rightSibling.keys[0];
                return;
            }
        }

        // Si no se pudo prestar, toca hacer merge
        if (idx > 0) {
            mergeLeaf(parent.children[idx - 1], leaf, parent, idx - 1);
        } else {
            mergeLeaf(leaf, parent.children[idx + 1], parent, idx);
        }
    }

    private void mergeLeaf(NodeBPlus leftLeaf, NodeBPlus rightLeaf, NodeBPlus parent, int parentKeyIndex) {
        System.out.println("Merge de hojas B+: " + leftLeaf + " <--> " + rightLeaf);
        // Copiamos las claves de rightLeaf en leftLeaf
        for (int i = 0; i < rightLeaf.n; i++) {
            leftLeaf.keys[leftLeaf.n + i] = rightLeaf.keys[i];
        }
        leftLeaf.n += rightLeaf.n;
        leftLeaf.nextLeaf = rightLeaf.nextLeaf;

        // Eliminamos la clave en el padre
        for (int i = parentKeyIndex; i < parent.n - 1; i++) {
            parent.keys[i] = parent.keys[i + 1];
            parent.children[i + 1] = parent.children[i + 2];
        }
        parent.n--;

        // Si el padre se quedó sin claves, subimos de nivel
        if (parent == root && parent.n == 0) {
            root = leftLeaf;
            leftLeaf.parent = null;
        } else if (parent != root && parent.n < T - 1) {
            rebalanceInternal(parent);
        }
    }

    private void rebalanceInternal(NodeBPlus node) {
        // Similar a la operación en B-Tree, 
        // se puede prestar o merge con hermanos.
        if (node == root) {
            if (node.n == 0) {
                // Se reduce la altura
                root = node.children[0];
                root.parent = null;
            }
            return;
        }
        NodeBPlus parent = node.parent;
        int idx = 0;
        while (idx <= parent.n && parent.children[idx] != node) {
            idx++;
        }

        // Intentar prestar de hermano izquierdo
        if (idx > 0) {
            NodeBPlus leftSibling = parent.children[idx - 1];
            if (leftSibling.n > T - 1) {
                // Tomar prestado
                for (int i = node.n; i > 0; i--) {
                    node.keys[i] = node.keys[i - 1];
                    node.children[i + 1] = node.children[i];
                }
                node.children[1] = node.children[0];
                node.keys[0] = parent.keys[idx - 1];
                node.children[0] = leftSibling.children[leftSibling.n];
                if (node.children[0] != null) {
                    node.children[0].parent = node;
                }
                node.n++;
                parent.keys[idx - 1] = leftSibling.keys[leftSibling.n - 1];
                leftSibling.children[leftSibling.n] = null;
                leftSibling.n--;
                return;
            }
        }
        // Intentar prestar de hermano derecho
        if (idx < parent.n) {
            NodeBPlus rightSibling = parent.children[idx + 1];
            if (rightSibling.n > T - 1) {
                node.keys[node.n] = parent.keys[idx];
                node.children[node.n + 1] = rightSibling.children[0];
                if (node.children[node.n + 1] != null) {
                    node.children[node.n + 1].parent = node;
                }
                node.n++;
                parent.keys[idx] = rightSibling.keys[0];
                for (int i = 0; i < rightSibling.n - 1; i++) {
                    rightSibling.keys[i] = rightSibling.keys[i + 1];
                    rightSibling.children[i] = rightSibling.children[i + 1];
                }
                rightSibling.children[rightSibling.n - 1] = rightSibling.children[rightSibling.n];
                rightSibling.n--;
                return;
            }
        }
        // Merge
        if (idx > 0) {
            mergeInternal(parent.children[idx - 1], node, parent, idx - 1);
        } else {
            mergeInternal(node, parent.children[idx + 1], parent, idx);
        }
    }

    private void mergeInternal(NodeBPlus leftNode, NodeBPlus rightNode, NodeBPlus parent, int parentKeyIndex) {
        System.out.println("Merge interno B+...");
        // Insertamos la clave del parent
        leftNode.keys[leftNode.n] = parent.keys[parentKeyIndex];
        leftNode.n++;
        // Copiamos las claves del rightNode
        for (int i = 0; i < rightNode.n; i++) {
            leftNode.keys[leftNode.n + i] = rightNode.keys[i];
            leftNode.children[leftNode.n + i] = rightNode.children[i];
            if (leftNode.children[leftNode.n + i] != null) {
                leftNode.children[leftNode.n + i].parent = leftNode;
            }
        }
        leftNode.children[leftNode.n + rightNode.n] = rightNode.children[rightNode.n];
        if (leftNode.children[leftNode.n + rightNode.n] != null) {
            leftNode.children[leftNode.n + rightNode.n].parent = leftNode;
        }
        leftNode.n += rightNode.n;

        // Ajustar el padre
        for (int i = parentKeyIndex; i < parent.n - 1; i++) {
            parent.keys[i] = parent.keys[i + 1];
            parent.children[i + 1] = parent.children[i + 2];
        }
        parent.n--;

        if (parent == root && parent.n == 0) {
            root = leftNode;
            leftNode.parent = null;
        } else if (parent != root && parent.n < T - 1) {
            rebalanceInternal(parent);
        }
    }

    @Override
    public void recorridoInOrder() {
        System.out.println("=== Recorrido In-order en B+ ===");
        NodeBPlus leaf = getLeftmostLeaf();
        while (leaf != null) {
            for (int i = 0; i < leaf.n; i++) {
                System.out.print(leaf.keys[i] + " ");
            }
            leaf = leaf.nextLeaf;
        }
        System.out.println();
    }

    private NodeBPlus getLeftmostLeaf() {
        if (root == null) return null;
        NodeBPlus current = root;
        while (!current.isLeaf) {
            current = current.children[0];
        }
        return current;
    }

    @Override
    public void recorridoPreOrder() {
        System.out.println("=== Recorrido Pre-order en B+ (Internos + hojas) ===");
        preOrderRec(root);
        System.out.println();
    }

    private void preOrderRec(NodeBPlus node) {
        if (node == null) return;
        // Imprimimos las claves del nodo
        for (int i = 0; i < node.n; i++) {
            System.out.print(node.keys[i] + " ");
        }
        if (!node.isLeaf) {
            for (int i = 0; i <= node.n; i++) {
                preOrderRec(node.children[i]);
            }
        }
    }

    @Override
    public void recorridoPostOrder() {
        System.out.println("=== Recorrido Post-order en B+ (Internos + hojas) ===");
        postOrderRec(root);
        System.out.println();
    }

    private void postOrderRec(NodeBPlus node) {
        if (node == null) return;
        if (!node.isLeaf) {
            for (int i = 0; i <= node.n; i++) {
                postOrderRec(node.children[i]);
            }
        }
        for (int i = 0; i < node.n; i++) {
            System.out.print(node.keys[i] + " ");
        }
    }

    @Override
    public void recorridoLevelOrder() {
        System.out.println("=== Recorrido Level-order en B+ ===");
        if (root == null) return;
        Queue<NodeBPlus> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            NodeBPlus current = queue.poll();
            for (int i = 0; i < current.n; i++) {
                System.out.print(current.keys[i] + " ");
            }
            System.out.print("| ");
            if (!current.isLeaf) {
                for (int i = 0; i <= current.n; i++) {
                    if (current.children[i] != null) {
                        queue.offer(current.children[i]);
                    }
                }
            }
        }
        System.out.println();
    }
}
