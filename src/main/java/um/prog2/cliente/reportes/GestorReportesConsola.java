package um.prog2.cliente.reportes;

import um.prog2.Enums.CategoriaRecurso;
import um.prog2.interfaces.RecursoDigital;
import um.prog2.reportes.SistemaReportes;
import um.prog2.usuario.Usuario;

import java.util.Map;
import java.util.Scanner;

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
    }

    /**
     * Muestra el menú de reportes y gestiona la interacción con el usuario.
     */
    public void mostrarMenuReportes() {
        boolean salir = false;
        
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
        
        Map<RecursoDigital, Integer> recursosMasPrestados = sistemaReportes.generarReporteRecursosMasPrestados(limite);
        
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
        
        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
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
        
        Map<Usuario, Integer> usuariosMasActivos = sistemaReportes.generarReporteUsuariosMasActivos(limite);
        
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
        
        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    /**
     * Muestra las estadísticas de uso por categoría.
     */
    private void mostrarEstadisticasPorCategoria() {
        System.out.println("\n==== ESTADÍSTICAS POR CATEGORÍA ====");
        
        Map<CategoriaRecurso, Integer> estadisticasPorCategoria = sistemaReportes.generarEstadisticasPorCategoria();
        
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