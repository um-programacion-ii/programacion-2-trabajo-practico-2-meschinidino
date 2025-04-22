package um.prog2;

import um.prog2.cliente.CLI2;

/**
 * Clase principal que inicia la aplicación.
 * Actualmente utiliza una interfaz de línea de comandos (CLI),
 * pero en el futuro podría cambiarse a una interfaz gráfica (GUI).
 */
public class Main {
    public static void main(String[] args) {
        // Iniciar la aplicación con la interfaz de línea de comandos
        CLI2.main(args);
    }
}
