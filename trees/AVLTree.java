package trees;

import trees.nodes.NodeAVL;
import java.util.*;

public class AVLTree implements ITree {

    private NodeAVL root;

    @Override
    public void generarArbolAltura3() {
        /*
         * Se genera un árbol AVL “base” de altura 3 (aprox) de ejemplo.
         * Para simplificar, aquí insertamos manualmente algunos valores.
         */
        root = null;
        int[] keys = {50, 30, 70, 20, 40, 60, 80};
        for (int k : keys) {
            root = insertAVL(root, k);
        }
    }

    @Override
    public void imprimirArbol() {
        imprimirArbolRec(root, 0);
    }

    private void imprimirArbolRec(NodeAVL node, int nivel) {
        if (node == null) return;
        // Imprimimos el subárbol derecho con un nivel más de indentación
        imprimirArbolRec(node.right, nivel + 1);
        // Indentación sencilla
        for (int i = 0; i < nivel; i++) {
            System.out.print("    ");
        }
        // Imprimimos la clave
        System.out.println(node.key);
        // Imprimimos el subárbol izquierdo
        imprimirArbolRec(node.left, nivel + 1);
    }

    @Override
    public void imprimirMatrizAdyacencia() {
        // Construimos una lista de nodos para indexarlos
        List<NodeAVL> nodeList = new ArrayList<>();
        buildNodeList(root, nodeList);

        int n = nodeList.size();
        int[][] matriz = new int[n][n];

        // Map para ubicar cada nodo en un índice de la lista
        Map<NodeAVL, Integer> mapIndex = new HashMap<>();
        for (int i = 0; i < n; i++) {
            mapIndex.put(nodeList.get(i), i);
        }

        // Llenamos la matriz de adyacencia
        for (int i = 0; i < n; i++) {
            NodeAVL current = nodeList.get(i);
            if (current.left != null) {
                int j = mapIndex.get(current.left);
                matriz[i][j] = 1;
            }
            if (current.right != null) {
                int j = mapIndex.get(current.right);
                matriz[i][j] = 1;
            }
        }

        System.out.println("Matriz de Adyacencia (AVL):");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(matriz[i][j] + " ");
            }
            System.out.println();
        }
    }

    private void buildNodeList(NodeAVL node, List<NodeAVL> list) {
        if (node == null) return;
        list.add(node);
        buildNodeList(node.left, list);
        buildNodeList(node.right, list);
    }

    @Override
    public boolean buscarClave(int key) {
        System.out.println("\n=== Búsqueda Paso a Paso (AVL) ===");
        return buscarClaveRec(root, key);
    }

    private boolean buscarClaveRec(NodeAVL node, int key) {
        if (node == null) {
            System.out.println("No existe en el árbol, un nodo con la clave solicitada.");
            return false;
        }
        System.out.println("Visitando nodo con clave: " + node.key);
        if (key == node.key) {
            System.out.println("¡Clave encontrada!: " + key);
            return true;
        } else if (key < node.key) {
            System.out.println("La clave buscada es menor, bajo al hijo izquierdo...");
            return buscarClaveRec(node.left, key);
        } else {
            System.out.println("La clave buscada es mayor, bajo al hijo derecho...");
            return buscarClaveRec(node.right, key);
        }
    }

    @Override
    public void insertarClave(int key) {
        System.out.println("\n=== Insertando clave " + key + " en AVL ===");
        root = insertAVL(root, key);
        // Mostrar el árbol resultante
        System.out.println("Árbol resultante tras insertar " + key + ":");
        imprimirArbol();
    }

    private NodeAVL insertAVL(NodeAVL node, int key) {
        if (node == null) {
            return new NodeAVL(key);
        }
        if (key < node.key) {
            node.left = insertAVL(node.left, key);
        } else if (key > node.key) {
            node.right = insertAVL(node.right, key);
        } else {
            // Clave duplicada, no insertamos
            return node;
        }

        // Actualizar la altura
        node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));

        // Obtener el factor de equilibrio
        int balance = getBalance(node);

        // Caso Rotación: Izq - Izq
        if (balance > 1 && key < node.left.key) {
            System.out.println("Violación AVL detectada (Izquierda-Izquierda). Rotación Derecha.");
            return rotateRight(node);
        }

        // Caso Rotación: Der - Der
        if (balance < -1 && key > node.right.key) {
            System.out.println("Violación AVL detectada (Derecha-Derecha). Rotación Izquierda.");
            return rotateLeft(node);
        }

        // Caso Rotación: Izq - Der
        if (balance > 1 && key > node.left.key) {
            System.out.println("Violación AVL detectada (Izquierda-Derecha). Rotación Izquierda + Derecha.");
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // Caso Rotación: Der - Izq
        if (balance < -1 && key < node.right.key) {
            System.out.println("Violación AVL detectada (Derecha-Izquierda). Rotación Derecha + Izquierda.");
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    @Override
    public void eliminarClave(int key) {
        System.out.println("\n=== Eliminando clave " + key + " en AVL ===");
        root = deleteAVL(root, key);
    }

    private NodeAVL deleteAVL(NodeAVL node, int key) {
        if (node == null) {
            System.out.println("No existe en el árbol, un nodo con la clave solicitada.");
            return null;
        }
        System.out.println("Visitando nodo con clave: " + node.key);

        if (key < node.key) {
            node.left = deleteAVL(node.left, key);
        } else if (key > node.key) {
            node.right = deleteAVL(node.right, key);
        } else {
            System.out.println("¡Clave encontrada! Eliminando nodo...");
            // Caso 1 o 2: nodo hoja o con un solo hijo
            if (node.left == null || node.right == null) {
                NodeAVL temp = (node.left != null) ? node.left : node.right;
                node = temp; // node podría ser null si no hay hijos
            } else {
                // Caso 3: nodo con dos hijos, tomamos el sucesor in-order
                NodeAVL sucesor = getMinValueNode(node.right);
                node.key = sucesor.key;
                node.right = deleteAVL(node.right, sucesor.key);
            }
        }

        // Si tras eliminar el nodo quedamos en null, retornamos
        if (node == null) {
            return null;
        }

        // Actualizar altura
        node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));
        // Factor de equilibrio
        int balance = getBalance(node);

        // Caso Izq - Izq
        if (balance > 1 && getBalance(node.left) >= 0) {
            System.out.println("Rotación derecha tras eliminación (Izquierda-Izquierda).");
            return rotateRight(node);
        }
        // Caso Izq - Der
        if (balance > 1 && getBalance(node.left) < 0) {
            System.out.println("Rotación izquierda-derecha tras eliminación.");
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        // Caso Der - Der
        if (balance < -1 && getBalance(node.right) <= 0) {
            System.out.println("Rotación izquierda tras eliminación (Derecha-Derecha).");
            return rotateLeft(node);
        }
        // Caso Der - Izq
        if (balance < -1 && getBalance(node.right) > 0) {
            System.out.println("Rotación derecha-izquierda tras eliminación.");
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    private NodeAVL getMinValueNode(NodeAVL node) {
        NodeAVL current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    @Override
    public void recorridoInOrder() {
        System.out.println("\n=== Recorrido In-order (AVL) ===");
        inOrderRec(root);
        System.out.println();
    }

    private void inOrderRec(NodeAVL node) {
        if (node == null) return;
        inOrderRec(node.left);
        System.out.print(node.key + " ");
        inOrderRec(node.right);
    }

    @Override
    public void recorridoPreOrder() {
        System.out.println("\n=== Recorrido Pre-order (AVL) ===");
        preOrderRec(root);
        System.out.println();
    }

    private void preOrderRec(NodeAVL node) {
        if (node == null) return;
        System.out.print(node.key + " ");
        preOrderRec(node.left);
        preOrderRec(node.right);
    }

    @Override
    public void recorridoPostOrder() {
        System.out.println("\n=== Recorrido Post-order (AVL) ===");
        postOrderRec(root);
        System.out.println();
    }

    private void postOrderRec(NodeAVL node) {
        if (node == null) return;
        postOrderRec(node.left);
        postOrderRec(node.right);
        System.out.print(node.key + " ");
    }

    @Override
    public void recorridoLevelOrder() {
        System.out.println("\n=== Recorrido Level-order (AVL) ===");
        if (root == null) return;
        Queue<NodeAVL> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            NodeAVL current = queue.poll();
            System.out.print(current.key + " ");
            if (current.left != null) queue.offer(current.left);
            if (current.right != null) queue.offer(current.right);
        }
        System.out.println();
    }

    // Métodos auxiliares
    private int getHeight(NodeAVL node) {
        return (node == null) ? 0 : node.height;
    }

    private int getBalance(NodeAVL node) {
        if (node == null) return 0;
        return getHeight(node.left) - getHeight(node.right);
    }

    private NodeAVL rotateRight(NodeAVL y) {
        NodeAVL x = y.left;
        NodeAVL t2 = x.right;

        // Rotación
        x.right = y;
        y.left = t2;

        // Actualizar alturas
        y.height = Math.max(getHeight(y.left), getHeight(y.right)) + 1;
        x.height = Math.max(getHeight(x.left), getHeight(x.right)) + 1;

        // x se convierte en la nueva raíz
        return x;
    }

    private NodeAVL rotateLeft(NodeAVL x) {
        NodeAVL y = x.right;
        NodeAVL t2 = y.left;

        // Rotación
        y.left = x;
        x.right = t2;

        // Actualizar alturas
        x.height = Math.max(getHeight(x.left), getHeight(x.right)) + 1;
        y.height = Math.max(getHeight(y.left), getHeight(y.right)) + 1;

        // y se convierte en la nueva raíz
        return y;
    }
}
