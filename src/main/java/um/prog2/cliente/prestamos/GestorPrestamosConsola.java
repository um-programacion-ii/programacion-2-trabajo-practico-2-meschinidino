package um.prog2.cliente.prestamos;

import um.prog2.interfaces.RecursoDigital;
import um.prog2.interfaces.ServicioNotificaciones;
import um.prog2.prestamos.Prestamo;
import um.prog2.prestamos.SistemaPrestamos;
import um.prog2.usuario.Usuario;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * Clase que gestiona la interfaz de consola para el sistema de préstamos.
 */
public class GestorPrestamosConsola {
    private final Scanner scanner;
    private final SistemaPrestamos sistemaPrestamos;
    private final List<RecursoDigital> recursos;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    /**
     * Constructor del gestor de préstamos para consola.
     * 
     * @param scanner Scanner para leer entrada del usuario
     * @param sistemaPrestamos Sistema de préstamos a utilizar
     * @param recursos Lista de recursos disponibles en el sistema
     */
    public GestorPrestamosConsola(Scanner scanner, SistemaPrestamos sistemaPrestamos, List<RecursoDigital> recursos) {
        this.scanner = scanner;
        this.sistemaPrestamos = sistemaPrestamos;
        this.recursos = recursos;
    }
    
    /**
     * Muestra el menú principal de gestión de préstamos.
     * 
     * @param usuarioActual Usuario que está utilizando el sistema
     */
    public void mostrarMenuPrestamos(Usuario usuarioActual) {
        if (usuarioActual == null) {
            System.out.println("Debe seleccionar un usuario primero");
            return;
        }
        
        while (true) {
            System.out.println("\n==== GESTIÓN DE PRÉSTAMOS ====");
            System.out.println("1. Solicitar préstamo");
            System.out.println("2. Devolver recurso");
            System.out.println("3. Renovar préstamo");
            System.out.println("4. Ver préstamos activos");
            System.out.println("5. Ver historial de préstamos");
            System.out.println("6. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            String opcion = scanner.nextLine();
            
            switch (opcion) {
                case "1":
                    solicitarPrestamo(usuarioActual);
                    break;
                case "2":
                    devolverRecurso(usuarioActual);
                    break;
                case "3":
                    renovarPrestamo(usuarioActual);
                    break;
                case "4":
                    verPrestamosActivos(usuarioActual);
                    break;
                case "5":
                    verHistorialPrestamos(usuarioActual);
                    break;
                case "6":
                    return;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
    }
    
    /**
     * Permite al usuario solicitar un préstamo de un recurso.
     * 
     * @param usuarioActual Usuario que solicita el préstamo
     */
    private void solicitarPrestamo(Usuario usuarioActual) {
        // Mostrar recursos disponibles
        System.out.println("\n-- RECURSOS DISPONIBLES PARA PRÉSTAMO --");
        List<RecursoDigital> disponibles = mostrarRecursosDisponibles();
        
        if (disponibles.isEmpty()) {
            System.out.println("No hay recursos disponibles para préstamo");
            return;
        }
        
        // Seleccionar recurso
        System.out.print("Seleccione número de recurso: ");
        try {
            int index = Integer.parseInt(scanner.nextLine());
            if (index >= 0 && index < disponibles.size()) {
                RecursoDigital recurso = disponibles.get(index);
                
                // Solicitar duración del préstamo
                System.out.print("Ingrese duración del préstamo en días (7-30): ");
                int dias = Integer.parseInt(scanner.nextLine());
                if (dias < 7 || dias > 30) {
                    System.out.println("La duración debe estar entre 7 y 30 días. Se usará el valor por defecto (14 días).");
                    dias = 14;
                }
                
                // Solicitar préstamo
                sistemaPrestamos.solicitarPrestamo(usuarioActual, recurso, dias);
                System.out.println("Solicitud de préstamo enviada. El recurso estará disponible en breve.");
            } else {
                System.out.println("Índice no válido");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Debe ingresar un número");
        }
    }
    
    /**
     * Permite al usuario devolver un recurso prestado.
     * 
     * @param usuarioActual Usuario que devuelve el recurso
     */
    private void devolverRecurso(Usuario usuarioActual) {
        // Mostrar préstamos activos
        System.out.println("\n-- PRÉSTAMOS ACTIVOS --");
        List<Prestamo> prestamosActivos = sistemaPrestamos.obtenerPrestamosActivos(usuarioActual);
        
        if (prestamosActivos.isEmpty()) {
            System.out.println("No tiene préstamos activos");
            return;
        }
        
        mostrarPrestamos(prestamosActivos);
        
        // Seleccionar préstamo a devolver
        System.out.print("Seleccione número de préstamo a devolver: ");
        try {
            int index = Integer.parseInt(scanner.nextLine());
            if (index >= 0 && index < prestamosActivos.size()) {
                Prestamo prestamo = prestamosActivos.get(index);
                
                // Solicitar devolución
                sistemaPrestamos.solicitarDevolucion(prestamo.getId(), usuarioActual);
                System.out.println("Solicitud de devolución enviada. El recurso será devuelto en breve.");
            } else {
                System.out.println("Índice no válido");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Debe ingresar un número");
        }
    }
    
    /**
     * Permite al usuario renovar un préstamo existente.
     * 
     * @param usuarioActual Usuario que renueva el préstamo
     */
    private void renovarPrestamo(Usuario usuarioActual) {
        // Mostrar préstamos activos
        System.out.println("\n-- PRÉSTAMOS ACTIVOS --");
        List<Prestamo> prestamosActivos = sistemaPrestamos.obtenerPrestamosActivos(usuarioActual);
        
        if (prestamosActivos.isEmpty()) {
            System.out.println("No tiene préstamos activos");
            return;
        }
        
        mostrarPrestamos(prestamosActivos);
        
        // Seleccionar préstamo a renovar
        System.out.print("Seleccione número de préstamo a renovar: ");
        try {
            int index = Integer.parseInt(scanner.nextLine());
            if (index >= 0 && index < prestamosActivos.size()) {
                Prestamo prestamo = prestamosActivos.get(index);
                
                // Solicitar duración de la renovación
                System.out.print("Ingrese días adicionales para el préstamo (1-14): ");
                int dias = Integer.parseInt(scanner.nextLine());
                if (dias < 1 || dias > 14) {
                    System.out.println("La duración debe estar entre 1 y 14 días. Se usará el valor por defecto (7 días).");
                    dias = 7;
                }
                
                // Solicitar renovación
                sistemaPrestamos.solicitarRenovacion(prestamo.getId(), usuarioActual, dias);
                System.out.println("Solicitud de renovación enviada. El préstamo será renovado en breve.");
            } else {
                System.out.println("Índice no válido");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Debe ingresar un número");
        }
    }
    
    /**
     * Muestra los préstamos activos del usuario.
     * 
     * @param usuarioActual Usuario del que se muestran los préstamos
     */
    private void verPrestamosActivos(Usuario usuarioActual) {
        System.out.println("\n-- PRÉSTAMOS ACTIVOS --");
        List<Prestamo> prestamosActivos = sistemaPrestamos.obtenerPrestamosActivos(usuarioActual);
        
        if (prestamosActivos.isEmpty()) {
            System.out.println("No tiene préstamos activos");
            return;
        }
        
        mostrarPrestamos(prestamosActivos);
    }
    
    /**
     * Muestra el historial de préstamos del usuario.
     * 
     * @param usuarioActual Usuario del que se muestra el historial
     */
    private void verHistorialPrestamos(Usuario usuarioActual) {
        System.out.println("\n-- HISTORIAL DE PRÉSTAMOS --");
        List<Prestamo> historialPrestamos = sistemaPrestamos.obtenerHistorialPrestamos(usuarioActual);
        
        if (historialPrestamos.isEmpty()) {
            System.out.println("No tiene historial de préstamos");
            return;
        }
        
        mostrarPrestamos(historialPrestamos);
    }
    
    /**
     * Muestra una lista de préstamos en la consola.
     * 
     * @param prestamos Lista de préstamos a mostrar
     */
    private void mostrarPrestamos(List<Prestamo> prestamos) {
        for (int i = 0; i < prestamos.size(); i++) {
            Prestamo prestamo = prestamos.get(i);
            RecursoDigital recurso = prestamo.getRecurso();
            
            System.out.println(i + " - ID: " + prestamo.getId() + 
                    " - Recurso: " + recurso.getIdentificador() + 
                    " - Fecha devolución: " + prestamo.getFechaDevolucion().format(FORMATO_FECHA) +
                    (prestamo.estaVencido() ? " (VENCIDO)" : ""));
        }
    }
    
    /**
     * Muestra y devuelve una lista de recursos disponibles para préstamo.
     * 
     * @return Lista de recursos disponibles
     */
    private List<RecursoDigital> mostrarRecursosDisponibles() {
        List<RecursoDigital> disponibles = new java.util.ArrayList<>();
        
        for (int i = 0; i < recursos.size(); i++) {
            RecursoDigital recurso = recursos.get(i);
            if (recurso.getEstado() == um.prog2.Enums.EstadoRecurso.DISPONIBLE) {
                disponibles.add(recurso);
                System.out.println(disponibles.size() - 1 + " - " + recurso.getIdentificador() + 
                        " - " + getTitulo(recurso) + " (" + recurso.getClass().getSimpleName() + ")");
            }
        }
        
        return disponibles;
    }
    
    /**
     * Obtiene el título de un recurso.
     * 
     * @param recurso Recurso del que se quiere obtener el título
     * @return Título del recurso o cadena vacía si no se puede obtener
     */
    private String getTitulo(RecursoDigital recurso) {
        if (recurso instanceof um.prog2.recursoDigital.Libro) {
            return ((um.prog2.recursoDigital.Libro) recurso).getTitulo();
        } else if (recurso instanceof um.prog2.recursoDigital.AudioLibro) {
            return ((um.prog2.recursoDigital.AudioLibro) recurso).getTitulo();
        } else if (recurso instanceof um.prog2.recursoDigital.Revista) {
            return ((um.prog2.recursoDigital.Revista) recurso).getTitulo();
        }
        return "";
    }
}