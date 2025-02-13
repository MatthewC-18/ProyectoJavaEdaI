package trees;

public interface ITree {
    void generarArbolAltura3();        // Genera un árbol del tipo correspondiente de altura 3
    void imprimirArbol();             // Muestra el árbol de forma gráfica en consola
    void imprimirMatrizAdyacencia();  // Muestra la matriz de adyacencia del árbol actual
    boolean buscarClave(int key);     // Búsqueda paso a paso de una clave
    void insertarClave(int key);      // Inserción paso a paso
    void eliminarClave(int key);      // Eliminación paso a paso
    void recorridoInOrder();          // Recorrido in-order
    void recorridoPreOrder();         // Recorrido pre-order
    void recorridoPostOrder();        // Recorrido post-order
    void recorridoLevelOrder();       // Recorrido level-order
}
