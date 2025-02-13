package trees;

import Nodes.NodeB;
import java.util.*;

public class BTree implements ITree {
    private static final int T = 2;   // Grado mínimo (cada nodo puede tener hasta 2*T-1 claves)
    private NodeB root;

    @Override
    public void generarArbolAltura3() {
        /*
         * Se inserta un conjunto de claves que, típicamente,
         * hará que el árbol B alcance ~3 niveles (altura = 3).
         * Ajusta la cantidad si deseas forzar más splits.
         */
        int[] keys = {10, 20, 5, 6, 12, 30, 7, 17, 1, 21, 25, 26, 28};
        for (int k : keys) {
            insertarClave(k);
        }
    }

    @Override
    public void imprimirArbol() {
        if (root == null) {
            System.out.println("Árbol B vacío.");
            return;
        }
        imprimirNodo(root, 0);
    }

    private void imprimirNodo(NodeB nodo, int nivel) {
        // Mostramos las claves del nodo
        // (indentación según el nivel)
        System.out.print("Nivel " + nivel + " Claves: [");
        for (int i = 0; i < nodo.nKeys; i++) {
            System.out.print(nodo.keys[i]);
            if (i < nodo.nKeys - 1) System.out.print(", ");
        }
        System.out.println("]");

        // Si no es hoja, imprimimos los hijos
        if (!nodo.isLeaf) {
            for (int i = 0; i <= nodo.nKeys; i++) {
                if (nodo.children[i] != null) {
                    imprimirNodo(nodo.children[i], nivel + 1);
                }
            }
        }
    }

    @Override
    public void imprimirMatrizAdyacencia() {
        /*
         * Para la matriz de adyacencia, consideramos cada NodeB como un vértice.
         * Recorremos el árbol en BFS, y para cada nodo registramos sus hijos.
         */
        List<NodeB> nodos = new ArrayList<>();
        Map<NodeB, Integer> indexMap = new HashMap<>();
        buildNodeListBFS(nodos, indexMap);

        int n = nodos.size();
        int[][] matriz = new int[n][n];

        // Llenamos la matriz de adyacencia
        for (int i = 0; i < n; i++) {
            NodeB nb = nodos.get(i);
            for (int c = 0; c <= nb.nKeys; c++) {
                if (nb.children[c] != null) {
                    int j = indexMap.get(nb.children[c]);
                    matriz[i][j] = 1;
                }
            }
        }

        System.out.println("Matriz de Adyacencia (B-Tree):");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(matriz[i][j] + " ");
            }
            System.out.println();
        }
    }

    private void buildNodeListBFS(List<NodeB> nodos, Map<NodeB, Integer> indexMap) {
        if (root == null) return;
        Queue<NodeB> queue = new LinkedList<>();
        queue.offer(root);

        int index = 0;
        while (!queue.isEmpty()) {
            NodeB current = queue.poll();
            nodos.add(current);
            indexMap.put(current, index++);
            if (!current.isLeaf) {
                for (int i = 0; i <= current.nKeys; i++) {
                    if (current.children[i] != null) {
                        queue.offer(current.children[i]);
                    }
                }
            }
        }
    }

    @Override
    public boolean buscarClave(int key) {
        if (root == null) {
            System.out.println("Árbol B vacío.");
            return false;
        }
        System.out.println("\n=== Búsqueda paso a paso en B-Tree ===");
        return buscarEnNodo(root, key);
    }

    private boolean buscarEnNodo(NodeB nodo, int key) {
        int i = 0;
        // Encontrar la primera clave >= key
        while (i < nodo.nKeys && key > nodo.keys[i]) {
            i++;
        }
        System.out.println("NODO con claves " + Arrays.toString(nodo.getKeysArray()) + 
                           " => Comparando en índice " + i);

        // Si la clave está en este nodo, retornamos true
        if (i < nodo.nKeys && nodo.keys[i] == key) {
            System.out.println("¡Clave encontrada!: " + key);
            return true;
        }
        // Si es hoja, no se encontró
        if (nodo.isLeaf) {
            System.out.println("No existe en el árbol, un nodo con la clave solicitada.");
            return false;
        }
        // Bajamos al hijo correspondiente
        System.out.println("La clave " + key + (i < nodo.nKeys ? 
            " es menor que " + nodo.keys[i] + ", voy al hijo " + i : 
            " es mayor que todas, voy al hijo " + i)
        );
        return buscarEnNodo(nodo.children[i], key);
    }

    @Override
    public void insertarClave(int key) {
        System.out.println("\n=== Insertando " + key + " en B-Tree ===");
        if (root == null) {
            root = new NodeB(true);
            root.keys[0] = key;
            root.nKeys = 1;
            return;
        }
        // Si la raíz está llena, la dividimos
        if (root.nKeys == (2 * T - 1)) {
            NodeB newRoot = new NodeB(false);
            newRoot.children[0] = root;
            splitChild(newRoot, 0, root);
            // Insertar en el hijo adecuado
            insertNonFull(newRoot, key);
            root = newRoot;
        } else {
            insertNonFull(root, key);
        }
    }

    private void insertNonFull(NodeB node, int key) {
        int i = node.nKeys - 1;
        if (node.isLeaf) {
            // Inserción en hoja
            while (i >= 0 && key < node.keys[i]) {
                node.keys[i + 1] = node.keys[i];
                i--;
            }
            node.keys[i + 1] = key;
            node.nKeys++;
        } else {
            // Buscamos el hijo para bajar
            while (i >= 0 && key < node.keys[i]) {
                i--;
            }
            i++;
            // Si el hijo está lleno, split
            if (node.children[i].nKeys == (2 * T - 1)) {
                splitChild(node, i, node.children[i]);
                // Tras el split, vemos en cuál de los 2 hijos descender
                if (key > node.keys[i]) {
                    i++;
                }
            }
            insertNonFull(node.children[i], key);
        }
    }

    private void splitChild(NodeB parent, int index, NodeB fullChild) {
        System.out.println("Split: nodo lleno con claves " + Arrays.toString(fullChild.getKeysArray()));
        NodeB newChild = new NodeB(fullChild.isLeaf);
        newChild.nKeys = T - 1;

        // Copiamos las últimas T-1 claves del hijo lleno
        for (int j = 0; j < T - 1; j++) {
            newChild.keys[j] = fullChild.keys[j + T];
        }

        if (!fullChild.isLeaf) {
            // Copiamos los T hijos si no es hoja
            for (int j = 0; j < T; j++) {
                newChild.children[j] = fullChild.children[j + T];
            }
        }

        fullChild.nKeys = T - 1;

        // Ajustamos el array de hijos del padre para insertar el nuevoChild
        for (int j = parent.nKeys; j >= index + 1; j--) {
            parent.children[j + 1] = parent.children[j];
        }
        parent.children[index + 1] = newChild;

        // Ajustamos el array de claves del padre
        for (int j = parent.nKeys - 1; j >= index; j--) {
            parent.keys[j + 1] = parent.keys[j];
        }
        parent.keys[index] = fullChild.keys[T - 1];
        parent.nKeys++;
    }

    @Override
    public void eliminarClave(int key) {
        System.out.println("\n=== Eliminando " + key + " en B-Tree ===");
        if (root == null) {
            System.out.println("El árbol está vacío.");
            return;
        }
        eliminarNodo(root, key);

        // Si la raíz queda sin claves y no es hoja, bajamos un nivel
        if (root.nKeys == 0 && !root.isLeaf) {
            root = root.children[0];
        }
    }

    private void eliminarNodo(NodeB node, int key) {
        int idx = encontrarClave(node, key);

        if (idx < node.nKeys && node.keys[idx] == key) {
            // Clave encontrada en el nodo "node" en la posición idx
            if (node.isLeaf) {
                // Caso 1: nodo hoja, se elimina la clave directamente
                System.out.println("Clave " + key + " encontrada en hoja. Eliminando...");
                for (int i = idx; i < node.nKeys - 1; i++) {
                    node.keys[i] = node.keys[i + 1];
                }
                node.nKeys--;
            } else {
                // Caso 2: nodo interno
                System.out.println("Clave " + key + " encontrada en nodo interno. Procesando eliminación...");
                eliminarInterno(node, idx);
            }
        } else {
            // Clave no está en este nodo
            if (node.isLeaf) {
                System.out.println("No existe en el árbol, un nodo con la clave solicitada.");
                return;
            }
            // Bajamos al hijo correspondiente
            boolean alUltimoHijo = (idx == node.nKeys);
            NodeB hijo = node.children[idx];
            // Si el hijo tiene menos de T claves, debemos "llenarlo"
            if (hijo.nKeys < T) {
                fill(node, idx);
            }
            if (alUltimoHijo && idx > node.nKeys) {
                eliminarNodo(node.children[idx - 1], key);
            } else {
                eliminarNodo(node.children[idx], key);
            }
        }
    }

    private void eliminarInterno(NodeB node, int idx) {
        int key = node.keys[idx];
        NodeB izq = node.children[idx];
        NodeB der = node.children[idx + 1];

        if (izq.nKeys >= T) {
            // Caso 2a: Tomar el predecesor
            int pred = getPredecesor(izq);
            node.keys[idx] = pred;
            eliminarNodo(izq, pred);
        } else if (der.nKeys >= T) {
            // Caso 2b: Tomar el sucesor
            int succ = getSucesor(der);
            node.keys[idx] = succ;
            eliminarNodo(der, succ);
        } else {
            // Caso 2c: Combinar hijos y luego eliminar
            merge(node, idx);
            eliminarNodo(izq, key);
        }
    }

    private int getPredecesor(NodeB node) {
        // Nos movemos al hijo derecho repetidamente
        while (!node.isLeaf) {
            node = node.children[node.nKeys];
        }
        return node.keys[node.nKeys - 1];
    }

    private int getSucesor(NodeB node) {
        // Nos movemos al hijo izquierdo repetidamente
        while (!node.isLeaf) {
            node = node.children[0];
        }
        return node.keys[0];
    }

    private int encontrarClave(NodeB node, int key) {
        int idx = 0;
        while (idx < node.nKeys && node.keys[idx] < key) {
            idx++;
        }
        return idx;
    }

    private void fill(NodeB parent, int idx) {
        // "Llena" al hijo children[idx] que tiene < T claves
        if (idx != 0 && parent.children[idx - 1].nKeys >= T) {
            // Tomamos una clave del hermano izquierdo
            prestarDelHermanoIzq(parent, idx);
        } else if (idx != parent.nKeys && parent.children[idx + 1].nKeys >= T) {
            // Tomamos una clave del hermano derecho
            prestarDelHermanoDer(parent, idx);
        } else {
            // Fusionamos con un hermano
            if (idx != parent.nKeys) {
                merge(parent, idx);
            } else {
                merge(parent, idx - 1);
            }
        }
    }

    private void prestarDelHermanoIzq(NodeB parent, int idx) {
        NodeB hijo = parent.children[idx];
        NodeB hermano = parent.children[idx - 1];

        // Mover claves en hijo 1 posición a la derecha
        for (int i = hijo.nKeys - 1; i >= 0; i--) {
            hijo.keys[i + 1] = hijo.keys[i];
        }
        if (!hijo.isLeaf) {
            for (int i = hijo.nKeys; i >= 0; i--) {
                hijo.children[i + 1] = hijo.children[i];
            }
        }
        // Tomar la clave del padre y ponerla como primera en hijo
        hijo.keys[0] = parent.keys[idx - 1];
        // Tomar el último hijo del hermano si no es hoja
        if (!hijo.isLeaf) {
            hijo.children[0] = hermano.children[hermano.nKeys];
        }
        // Subir la última clave del hermano al padre
        parent.keys[idx - 1] = hermano.keys[hermano.nKeys - 1];
        hijo.nKeys++;
        hermano.nKeys--;
    }

    private void prestarDelHermanoDer(NodeB parent, int idx) {
        NodeB hijo = parent.children[idx];
        NodeB hermano = parent.children[idx + 1];

        // La clave del padre baja al hijo
        hijo.keys[hijo.nKeys] = parent.keys[idx];
        if (!hijo.isLeaf) {
            hijo.children[hijo.nKeys + 1] = hermano.children[0];
        }
        // La primera clave del hermano sube al padre
        parent.keys[idx] = hermano.keys[0];

        // Desplazar claves del hermano a la izquierda
        for (int i = 1; i < hermano.nKeys; i++) {
            hermano.keys[i - 1] = hermano.keys[i];
        }
        if (!hermano.isLeaf) {
            for (int i = 1; i <= hermano.nKeys; i++) {
                hermano.children[i - 1] = hermano.children[i];
            }
        }
        hijo.nKeys++;
        hermano.nKeys--;
    }

    private void merge(NodeB parent, int idx) {
        NodeB hijoIzq = parent.children[idx];
        NodeB hijoDer = parent.children[idx + 1];

        // Bajamos la clave del padre al hijoIzq
        hijoIzq.keys[hijoIzq.nKeys] = parent.keys[idx];
        hijoIzq.nKeys++;

        // Copiar las claves del hijoDer a hijoIzq
        for (int i = 0; i < hijoDer.nKeys; i++) {
            hijoIzq.keys[hijoIzq.nKeys + i] = hijoDer.keys[i];
        }
        // Copiar los hijos si no es hoja
        if (!hijoIzq.isLeaf) {
            for (int i = 0; i <= hijoDer.nKeys; i++) {
                hijoIzq.children[hijoIzq.nKeys + i] = hijoDer.children[i];
            }
        }
        hijoIzq.nKeys += hijoDer.nKeys;

        // Mover claves del padre para llenar el hueco
        for (int i = idx; i < parent.nKeys - 1; i++) {
            parent.keys[i] = parent.keys[i + 1];
        }
        for (int i = idx + 1; i < parent.nKeys; i++) {
            parent.children[i] = parent.children[i + 1];
        }
        parent.nKeys--;

        System.out.println("Merge: fusionando nodos en índice " + idx + " del padre.");
    }

    @Override
    public void recorridoInOrder() {
        System.out.println("=== Recorrido In-order en B-Tree ===");
        inOrderRec(root);
        System.out.println();
    }

    private void inOrderRec(NodeB node) {
        if (node == null) return;
        for (int i = 0; i < node.nKeys; i++) {
            if (!node.isLeaf) {
                inOrderRec(node.children[i]);
            }
            System.out.print(node.keys[i] + " ");
        }
        if (!node.isLeaf) {
            inOrderRec(node.children[node.nKeys]);
        }
    }

    @Override
    public void recorridoPreOrder() {
        System.out.println("=== Recorrido Pre-order en B-Tree ===");
        preOrderRec(root);
        System.out.println();
    }

    private void preOrderRec(NodeB node) {
        if (node == null) return;
        int i;
        for (i = 0; i < node.nKeys; i++) {
            System.out.print(node.keys[i] + " ");
        }
        if (!node.isLeaf) {
            for (i = 0; i <= node.nKeys; i++) {
                preOrderRec(node.children[i]);
            }
        }
    }

    @Override
    public void recorridoPostOrder() {
        System.out.println("=== Recorrido Post-order en B-Tree ===");
        postOrderRec(root);
        System.out.println();
    }

    private void postOrderRec(NodeB node) {
        if (node == null) return;
        if (node.isLeaf) {
            for (int i = 0; i < node.nKeys; i++) {
                System.out.print(node.keys[i] + " ");
            }
        } else {
            for (int i = 0; i < node.nKeys; i++) {
                postOrderRec(node.children[i]);
                System.out.print(node.keys[i] + " ");
            }
            postOrderRec(node.children[node.nKeys]);
        }
    }

    @Override
    public void recorridoLevelOrder() {
        System.out.println("=== Recorrido Level-order en B-Tree ===");
        if (root == null) return;
        Queue<NodeB> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            NodeB current = queue.poll();
            // Imprimimos las claves del nodo
            for (int i = 0; i < current.nKeys; i++) {
                System.out.print(current.keys[i] + " ");
            }
            System.out.print("| ");
            // Agregamos sus hijos
            if (!current.isLeaf) {
                for (int i = 0; i <= current.nKeys; i++) {
                    queue.offer(current.children[i]);
                }
            }
        }
        System.out.println();
    }
}
