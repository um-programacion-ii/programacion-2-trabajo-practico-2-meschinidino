package um.prog2.cliente.reportes;

import um.prog2.Enums.CategoriaRecurso;
import um.prog2.interfaces.RecursoDigital;
import um.prog2.reportes.SistemaReportes;
import um.prog2.usuario.Usuario;

import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Clase que gestiona la interfaz de consola para el sistema de reportes.
 */
public class GestorReportesConsola {
    private final Scanner scanner;
    private final SistemaReportes sistemaReportes;

    /**
     * Constructor del gestor de reportes para consola.
     * 
     * @param scanner Scanner para leer entrada del usuario
     * @param sistemaReportes Sistema de reportes para generar los informes
     */
    public GestorReportesConsola(Scanner scanner, SistemaReportes sistemaReportes) {
        this.scanner = scanner;
        this.sistemaReportes = sistemaReportes;

        // Registrar hook para cerrar el ExecutorService al salir
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            cerrarSistemaReportes();
        }));
    }

    /**
     * Cierra el sistema de reportes, cancelando todas las tareas en progreso.
     */
    public void cerrarSistemaReportes() {
        if (sistemaReportes != null) {
            sistemaReportes.cerrar();
        }
    }

    /**
     * Muestra el menú de reportes y gestiona la interacción con el usuario.
     */
    public void mostrarMenuReportes() {
        boolean salir = false;

        try {
            while (!salir) {
                System.out.println("\n==== REPORTES Y ESTADÍSTICAS ====");
                System.out.println("1. Recursos más prestados");
                System.out.println("2. Usuarios más activos");
                System.out.println("3. Estadísticas por categoría");
                System.out.println("4. Volver al menú principal");
                System.out.print("Seleccione una opción: ");

                String opcion = scanner.nextLine();

                switch (opcion) {
                    case "1":
                        mostrarRecursosMasPrestados();
                        break;
                    case "2":
                        mostrarUsuariosMasActivos();
                        break;
                    case "3":
                        mostrarEstadisticasPorCategoria();
                        break;
                    case "4":
                        salir = true;
                        break;
                    default:
                        System.out.println("Opción no válida. Intente de nuevo.");
                }
            }
        } finally {
            // Asegurarse de que no queden tareas pendientes al salir del menú
            // No cerramos completamente el sistema porque podría volver a usarse
            // Solo limpiamos los reportes en progreso y resultados
            sistemaReportes.limpiarReportes();
        }
    }

    /**
     * Muestra el reporte de recursos más prestados.
     */
    private void mostrarRecursosMasPrestados() {
        System.out.println("\n==== RECURSOS MÁS PRESTADOS ====");
        System.out.print("Ingrese el número de recursos a mostrar (o Enter para mostrar todos): ");
        String input = scanner.nextLine();

        int limite = Integer.MAX_VALUE;
        if (!input.isEmpty()) {
            try {
                limite = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Entrada no válida. Se mostrarán todos los recursos.");
            }
        }

        System.out.println("\nGenerando reporte en segundo plano...");

        try {
            // Iniciar generación de reporte en segundo plano
            Future<Map<RecursoDigital, Integer>> futureReporte = sistemaReportes.iniciarReporteRecursosMasPrestados(limite);

            // Mostrar progreso mientras se genera el reporte
            while (!futureReporte.isDone()) {
                int progreso = sistemaReportes.obtenerProgresoReporte(SistemaReportes.REPORTE_RECURSOS_MAS_PRESTADOS);
                mostrarBarraProgreso(progreso);
                Thread.sleep(500); // Actualizar cada medio segundo
            }

            // Obtener resultado del reporte
            Map<RecursoDigital, Integer> recursosMasPrestados = futureReporte.get();

            // Limpiar línea de progreso
            System.out.print("\r" + " ".repeat(80) + "\r");

            if (recursosMasPrestados.isEmpty()) {
                System.out.println("No hay préstamos registrados en el sistema.");
                return;
            }

            System.out.println("\nRanking de recursos más prestados:");
            System.out.println("----------------------------------");
            int posicion = 1;

            for (Map.Entry<RecursoDigital, Integer> entry : recursosMasPrestados.entrySet()) {
                RecursoDigital recurso = entry.getKey();
                int cantidadPrestamos = entry.getValue();

                System.out.printf("%d. %s (ID: %s) - %d préstamo(s)%n", 
                        posicion++, 
                        obtenerTituloRecurso(recurso), 
                        recurso.getIdentificador(), 
                        cantidadPrestamos);
            }
        } catch (InterruptedException e) {
            System.out.println("\nLa generación del reporte fue interrumpida.");
        } catch (ExecutionException e) {
            System.out.println("\nError al generar el reporte: " + e.getCause().getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    /**
     * Muestra una barra de progreso en la consola.
     * 
     * @param progreso Porcentaje de progreso (0-100)
     */
    private void mostrarBarraProgreso(int progreso) {
        if (progreso < 0) progreso = 0;
        if (progreso > 100) progreso = 100;

        int barraLongitud = 40;
        int completado = (progreso * barraLongitud) / 100;

        StringBuilder barra = new StringBuilder("[");
        for (int i = 0; i < barraLongitud; i++) {
            if (i < completado) {
                barra.append("=");
            } else if (i == completado) {
                barra.append(">");
            } else {
                barra.append(" ");
            }
        }
        barra.append("] ").append(progreso).append("%");

        System.out.print("\rProgreso: " + barra);
    }

    /**
     * Muestra el reporte de usuarios más activos.
     */
    private void mostrarUsuariosMasActivos() {
        System.out.println("\n==== USUARIOS MÁS ACTIVOS ====");
        System.out.print("Ingrese el número de usuarios a mostrar (o Enter para mostrar todos): ");
        String input = scanner.nextLine();

        int limite = Integer.MAX_VALUE;
        if (!input.isEmpty()) {
            try {
                limite = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Entrada no válida. Se mostrarán todos los usuarios.");
            }
        }

        System.out.println("\nGenerando reporte en segundo plano...");

        try {
            // Iniciar generación de reporte en segundo plano
            Future<Map<Usuario, Integer>> futureReporte = sistemaReportes.iniciarReporteUsuariosMasActivos(limite);

            // Mostrar progreso mientras se genera el reporte
            while (!futureReporte.isDone()) {
                int progreso = sistemaReportes.obtenerProgresoReporte(SistemaReportes.REPORTE_USUARIOS_MAS_ACTIVOS);
                mostrarBarraProgreso(progreso);
                Thread.sleep(500); // Actualizar cada medio segundo
            }

            // Obtener resultado del reporte
            Map<Usuario, Integer> usuariosMasActivos = futureReporte.get();

            // Limpiar línea de progreso
            System.out.print("\r" + " ".repeat(80) + "\r");

            if (usuariosMasActivos.isEmpty()) {
                System.out.println("No hay préstamos registrados en el sistema.");
                return;
            }

            System.out.println("\nRanking de usuarios más activos:");
            System.out.println("--------------------------------");
            int posicion = 1;

            for (Map.Entry<Usuario, Integer> entry : usuariosMasActivos.entrySet()) {
                Usuario usuario = entry.getKey();
                int cantidadPrestamos = entry.getValue();

                System.out.printf("%d. %s %s (ID: %d) - %d préstamo(s)%n", 
                        posicion++, 
                        usuario.getNombre(), 
                        usuario.getApellido(), 
                        usuario.getID(), 
                        cantidadPrestamos);
            }
        } catch (InterruptedException e) {
            System.out.println("\nLa generación del reporte fue interrumpida.");
        } catch (ExecutionException e) {
            System.out.println("\nError al generar el reporte: " + e.getCause().getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    /**
     * Muestra las estadísticas de uso por categoría.
     */
    private void mostrarEstadisticasPorCategoria() {
        System.out.println("\n==== ESTADÍSTICAS POR CATEGORÍA ====");

        System.out.println("\nGenerando reporte en segundo plano...");

        try {
            // Iniciar generación de reporte en segundo plano
            Future<Map<CategoriaRecurso, Integer>> futureReporte = sistemaReportes.iniciarEstadisticasPorCategoria();

            // Mostrar progreso mientras se genera el reporte
            while (!futureReporte.isDone()) {
                int progreso = sistemaReportes.obtenerProgresoReporte(SistemaReportes.REPORTE_ESTADISTICAS_CATEGORIA);
                mostrarBarraProgreso(progreso);
                Thread.sleep(500); // Actualizar cada medio segundo
            }

            // Obtener resultado del reporte
            Map<CategoriaRecurso, Integer> estadisticasPorCategoria = futureReporte.get();

            // Limpiar línea de progreso
            System.out.print("\r" + " ".repeat(80) + "\r");

            if (estadisticasPorCategoria.values().stream().mapToInt(Integer::intValue).sum() == 0) {
                System.out.println("No hay préstamos registrados en el sistema.");
                return;
            }

            System.out.println("\nUso de recursos por categoría:");
            System.out.println("-----------------------------");
            int posicion = 1;

            for (Map.Entry<CategoriaRecurso, Integer> entry : estadisticasPorCategoria.entrySet()) {
                CategoriaRecurso categoria = entry.getKey();
                int cantidadPrestamos = entry.getValue();

                // Solo mostrar categorías con préstamos
                if (cantidadPrestamos > 0) {
                    System.out.printf("%d. %s - %d préstamo(s)%n", 
                            posicion++, 
                            formatearCategoria(categoria.name()), 
                            cantidadPrestamos);
                }
            }
        } catch (InterruptedException e) {
            System.out.println("\nLa generación del reporte fue interrumpida.");
        } catch (ExecutionException e) {
            System.out.println("\nError al generar el reporte: " + e.getCause().getMessage());
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    /**
     * Obtiene el título de un recurso.
     * 
     * @param recurso Recurso del que se quiere obtener el título
     * @return Título del recurso o su identificador si no se puede determinar
     */
    private String obtenerTituloRecurso(RecursoDigital recurso) {
        try {
            // Usar reflection para obtener el método getTitulo si existe
            return (String) recurso.getClass().getMethod("getTitulo").invoke(recurso);
        } catch (Exception e) {
            return recurso.getIdentificador();
        }
    }

    /**
     * Formatea el nombre de una categoría para mostrarla en consola.
     * 
     * @param categoria Nombre de la categoría en formato ENUM
     * @return Nombre de la categoría formateado
     */
    private String formatearCategoria(String categoria) {
        return categoria.replace("_", " ");
    }
}
