import java.util.*;
import trees.*;

public class Main {
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        ITree arbolSeleccionado = null;
        boolean salir = false;

        while (!salir) {
            System.out.println("\n======================================");
            System.out.println("   MENÚ PRINCIPAL - TIPO DE ÁRBOL   ");
            System.out.println("======================================");
            System.out.println("1. Algoritmos de Árboles AVL");
            System.out.println("2. Algoritmos de Árboles B");
            System.out.println("3. Algoritmos de Árboles B+");
            System.out.println("4. Salir");
            System.out.print("Seleccione una opción: ");
            int opcionArbol = leerEntero();

            if (opcionArbol == 4) {
                System.out.println("Saliendo de la aplicación...");
                salir = true;
                continue;
            }

            // Instanciamos el árbol según la elección
            switch (opcionArbol) {
                case 1:
                    arbolSeleccionado = new AVLTree();
                    break;
                case 2:
                    arbolSeleccionado = new BTree();
                    break;
                case 3:
                    arbolSeleccionado = new BPlusTree();
                    break;
                default:
                    System.out.println("Opción inválida. Intente de nuevo.");
                    continue;
            }

            System.out.println("\n======================================");
            System.out.println("   MENÚ DE OPERACIONES CON EL ÁRBOL  ");
            System.out.println("======================================");
            System.out.println("1. Búsqueda de la clave de un nodo");
            System.out.println("2. Inserción de un nuevo nodo en el árbol");
            System.out.println("3. Eliminación de un nodo del árbol");
            System.out.println("4. Recorrido del árbol");
            System.out.print("Seleccione una opción: ");
            int opcionOperacion = leerEntero();

            switch (opcionOperacion) {
                case 1: // Búsqueda
                    arbolSeleccionado.generarArbolAltura3();
                    System.out.println("\nÁrbol generado (altura=3) en forma gráfica:");
                    arbolSeleccionado.imprimirArbol();
                    System.out.println();
                    arbolSeleccionado.imprimirMatrizAdyacencia();

                    System.out.print("\nIngrese la clave a buscar: ");
                    int claveBusqueda = leerEntero();
                    arbolSeleccionado.buscarClave(claveBusqueda);
                    break;

                case 2: // Inserción
                    System.out.println("\n=== Inserción en árbol seleccionado ===");
                    // Para un árbol de altura=3, definimos un rango base (ajusta según tu necesidad)
                    int minNodos = 7;
                    int maxNodos = 15;
                    int cantClaves = numeroAleatorioEntre(minNodos, maxNodos);
                    System.out.println("Número de claves a generar: " + cantClaves);

                    List<Integer> listaClaves = new ArrayList<>();
                    for (int i = 0; i < cantClaves; i++) {
                        listaClaves.add(numeroAleatorioEntre(1, 100));
                    }
                    System.out.println("Claves generadas aleatoriamente: " + listaClaves);

                    // Insertamos cada clave en orden, mostrando paso a paso
                    for (int clave : listaClaves) {
                        arbolSeleccionado.insertarClave(clave);
                    }
                    break;

                case 3: // Eliminación
                    System.out.println("\n=== Eliminación en árbol seleccionado ===");
                    minNodos = 7;
                    maxNodos = 15;
                    cantClaves = numeroAleatorioEntre(minNodos, maxNodos);
                    List<Integer> listaClavesEliminar = new ArrayList<>();
                    for (int i = 0; i < cantClaves; i++) {
                        listaClavesEliminar.add(numeroAleatorioEntre(1, 100));
                    }
                    System.out.println("Se generará un árbol con las claves: " + listaClavesEliminar);

                    // Construir el árbol sin mostrar paso a paso (solo el resultado final)
                    arbolSeleccionado = construirArbolConLista(arbolSeleccionado, listaClavesEliminar);

                    System.out.println("\nÁrbol resultante (gráfico):");
                    arbolSeleccionado.imprimirArbol();
                    arbolSeleccionado.imprimirMatrizAdyacencia();

                    System.out.print("\nIngrese la clave a eliminar: ");
                    int claveEliminar = leerEntero();
                    arbolSeleccionado.eliminarClave(claveEliminar);

                    System.out.println("\nÁrbol resultante tras eliminación:");
                    arbolSeleccionado.imprimirArbol();
                    arbolSeleccionado.imprimirMatrizAdyacencia();
                    break;

                case 4: // Recorrido
                    arbolSeleccionado.generarArbolAltura3();
                    System.out.println("\nÁrbol generado (altura=3) en forma gráfica:");
                    arbolSeleccionado.imprimirArbol();
                    System.out.println();
                    arbolSeleccionado.imprimirMatrizAdyacencia();

                    // Tercer menú: tipo de recorrido
                    System.out.println("\nSeleccione el tipo de recorrido:");
                    System.out.println("1. In-order");
                    System.out.println("2. Pre-order");
                    System.out.println("3. Post-order");
                    System.out.println("4. Level-order");
                    System.out.print("Opción: ");
                    int opcionRecorrido = leerEntero();

                    switch (opcionRecorrido) {
                        case 1:
                            arbolSeleccionado.recorridoInOrder();
                            break;
                        case 2:
                            arbolSeleccionado.recorridoPreOrder();
                            break;
                        case 3:
                            arbolSeleccionado.recorridoPostOrder();
                            break;
                        case 4:
                            arbolSeleccionado.recorridoLevelOrder();
                            break;
                        default:
                            System.out.println("Opción inválida en el menú de recorridos.");
                            break;
                    }
                    break;

                default:
                    System.out.println("Opción inválida para operación de árbol.");
            }
        }
    }

    // ============ Métodos auxiliares ============

    private static int leerEntero() {
        while (!sc.hasNextInt()) {
            sc.next();
            System.out.print("Por favor, ingrese un número válido: ");
        }
        return sc.nextInt();
    }

    private static int numeroAleatorioEntre(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    private static ITree construirArbolConLista(ITree arbol, List<Integer> claves) {
        for (int c : claves) {
            arbol.insertarClave(c);
        }
        return arbol;
    }
}
