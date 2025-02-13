package utils;

public class TreePrinter {

    // Ejemplo de método estático genérico
    public static void print(String mensaje) {
        System.out.println("TreePrinter => " + mensaje);
    }

    /*
     * Podrías implementar métodos:
     *   printAVL(AVLTree avl)
     *   printBTree(BTree btree)
     *   printBPlusTree(BPlusTree bptree)
     * y en cada uno usar la lógica de recorrido que ya implementaste,
     * formateada de manera bonita (por ejemplo, dibujando ramas).
     *
     * Debido a las diferencias de cada estructura, no es trivial
     * hacer un único método 'universal' a menos que uses reflexión
     * o un esquema unificado de nodos.
     */
}
