// src/main/java/um/prog2/utilsRecursosCLI/GestorRecursosConsola.java
package um.prog2.cliente.utilsRecursosCLI;

import um.prog2.Enums.CategoriaRecurso;
import um.prog2.Enums.EstadoRecurso;
import um.prog2.excepciones.RecursoNoDisponibleException;
import um.prog2.interfaces.Prestable;
import um.prog2.interfaces.RecursoDigital;
import um.prog2.interfaces.Renovable;
import um.prog2.interfaces.ServicioNotificaciones;
import um.prog2.prestamos.SistemaPrestamos;
import um.prog2.recursoDigital.*;
import um.prog2.usuario.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Clase que gestiona la interacción con el usuario para operaciones relacionadas con recursos.
 * Utiliza GestorRecursos para la lógica de negocio.
 */
public class GestorRecursosConsola {
    private final Scanner scanner;
    private final List<RecursoDigital> recursos;
    private final ServicioNotificaciones servicioNotificaciones;
    private final SistemaPrestamos sistemaPrestamos;
    private final GestorRecursos gestorRecursos;

    /**
     * Constructor que inicializa el gestor de recursos de consola.
     * 
     * @param scanner Scanner para leer la entrada del usuario
     * @param recursos Lista de recursos a gestionar
     * @param servicioNotificaciones Servicio para enviar notificaciones a los usuarios
     * @param sistemaPrestamos Sistema de préstamos para gestionar los préstamos
     */
    public GestorRecursosConsola(Scanner scanner, List<RecursoDigital> recursos, ServicioNotificaciones servicioNotificaciones, SistemaPrestamos sistemaPrestamos) {
        this.scanner = scanner;
        this.recursos = recursos;
        this.servicioNotificaciones = servicioNotificaciones;
        this.sistemaPrestamos = sistemaPrestamos;
        this.gestorRecursos = new GestorRecursos(recursos, servicioNotificaciones, sistemaPrestamos);
    }

    /**
     * Muestra el menú para crear un recurso y procesa la entrada del usuario.
     */
    public void crearRecurso() {
        System.out.println("\n-- CREAR RECURSO --");
        System.out.println("1. Libro");
        System.out.println("2. AudioLibro");
        System.out.println("3. Revista");
        System.out.print("Tipo de recurso: ");
        String tipo = scanner.nextLine();

        System.out.print("ID: ");
        String id = scanner.nextLine();

        System.out.print("Título: ");
        String titulo = scanner.nextLine();

        RecursoDigital nuevoRecurso = null;

        switch (tipo) {
            case "1":
                nuevoRecurso = crearLibro(id, titulo);
                break;
            case "2":
                nuevoRecurso = crearAudioLibro(id, titulo);
                break;
            case "3":
                nuevoRecurso = crearRevista(id, titulo);
                break;
            default:
                System.out.println("Tipo no válido");
                return;
        }

        if (nuevoRecurso != null) {
            System.out.println("Recurso creado exitosamente");
        }
    }

    /**
     * Solicita información al usuario para crear un libro.
     * 
     * @param id Identificador del libro
     * @param titulo Título del libro
     * @return El libro creado o null si hubo un error
     */
    private Libro crearLibro(String id, String titulo) {
        System.out.print("Autor: ");
        String autor = scanner.nextLine();

        System.out.println("Categorías disponibles:");
        CategoriaRecurso[] categorias = CategoriaRecurso.values();
        for (int i = 0; i < categorias.length; i++) {
            System.out.println((i + 1) + ". " + categorias[i].name().replace("_", " "));
        }

        CategoriaRecurso categoria = CategoriaRecurso.NO_FICCION;
        System.out.print("Seleccione número de categoría: ");
        try {
            int seleccion = Integer.parseInt(scanner.nextLine());
            if (seleccion >= 1 && seleccion <= categorias.length) {
                categoria = categorias[seleccion - 1];
            } else {
                System.out.println("Selección fuera de rango, usando categoría por defecto: NO_FICCION");
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada no válida, usando categoría por defecto: NO_FICCION");
        }

        // Usar el GestorRecursos para crear el libro
        return gestorRecursos.crearLibro(id, titulo, autor, categoria);
    }

    /**
     * Solicita información al usuario para crear un audiolibro.
     * 
     * @param id Identificador del audiolibro
     * @param titulo Título del audiolibro
     * @return El audiolibro creado o null si hubo un error
     */
    private AudioLibro crearAudioLibro(String id, String titulo) {
        System.out.print("Autor: ");
        String autor = scanner.nextLine();
        System.out.print("Narrador: ");
        String narrador = scanner.nextLine();

        double duracion = 0;
        try {
            System.out.print("Duración (horas): ");
            duracion = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Error: La duración debe ser un número");
            return null;
        }

        System.out.println("Categorías disponibles:");
        CategoriaRecurso[] categorias = CategoriaRecurso.values();
        for (int i = 0; i < categorias.length; i++) {
            System.out.println((i + 1) + ". " + categorias[i].name().replace("_", " "));
        }

        CategoriaRecurso categoria = CategoriaRecurso.NO_FICCION;
        System.out.print("Seleccione número de categoría: ");
        try {
            int seleccion = Integer.parseInt(scanner.nextLine());
            if (seleccion >= 1 && seleccion <= categorias.length) {
                categoria = categorias[seleccion - 1];
            } else {
                System.out.println("Selección fuera de rango, usando categoría por defecto: NO_FICCION");
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada no válida, usando categoría por defecto: NO_FICCION");
        }

        // Usar el GestorRecursos para crear el audiolibro
        return gestorRecursos.crearAudioLibro(id, titulo, autor, narrador, duracion, categoria);
    }

    /**
     * Solicita información al usuario para crear una revista.
     * 
     * @param id Identificador de la revista
     * @param titulo Título de la revista
     * @return La revista creada o null si hubo un error
     */
    private Revista crearRevista(String id, String titulo) {
        System.out.print("Editorial: ");
        String editorial = scanner.nextLine();

        System.out.println("Categorías disponibles:");
        CategoriaRecurso[] categorias = CategoriaRecurso.values();
        for (int i = 0; i < categorias.length; i++) {
            System.out.println((i + 1) + ". " + categorias[i].name().replace("_", " "));
        }

        CategoriaRecurso categoria = CategoriaRecurso.NO_FICCION; // Default category
        System.out.print("Seleccione número de categoría: ");
        try {
            int seleccion = Integer.parseInt(scanner.nextLine());
            if (seleccion >= 1 && seleccion <= categorias.length) {
                categoria = categorias[seleccion - 1];
            } else {
                System.out.println("Selección fuera de rango, usando categoría por defecto: NO_FICCION");
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada no válida, usando categoría por defecto: NO_FICCION");
        }

        // Usar el GestorRecursos para crear la revista
        return gestorRecursos.crearRevista(id, titulo, editorial, categoria);
    }

    /**
     * Obtiene el gestor de recursos.
     * 
     * @return Gestor de recursos
     */
    public GestorRecursos getGestorRecursos() {
        return gestorRecursos;
    }

    /**
     * Muestra la lista de todos los recursos registrados.
     */
    public void listarRecursos() {
        if (recursos.isEmpty()) {
            System.out.println("No hay recursos registrados");
            return;
        }

        System.out.println("\n-- RECURSOS DISPONIBLES --");
        for (int i = 0; i < recursos.size(); i++) {
            RecursoDigital r = recursos.get(i);
            System.out.println(i + " - " + r.getIdentificador() + " - " +
                    gestorRecursos.getTitulo(r) + " (" + r.getClass().getSimpleName() + ") - " + r.getEstado());
        }
    }

    /**
     * Muestra los recursos disponibles para préstamo y permite al usuario seleccionar uno.
     * 
     * @param usuarioActual Usuario que solicita el préstamo
     */
    public void prestarRecurso(Usuario usuarioActual) {
        if (usuarioActual == null) {
            System.out.println("Debe seleccionar un usuario primero");
            return;
        }

        // Obtener recursos disponibles para préstamo usando GestorRecursos
        List<RecursoDigital> disponibles = gestorRecursos.obtenerRecursosDisponiblesParaPrestamo();
        System.out.println("\n-- RECURSOS DISPONIBLES PARA PRÉSTAMO --");

        if (disponibles.isEmpty()) {
            System.out.println("No hay recursos disponibles para préstamo");
            return;
        }

        // Mostrar recursos disponibles
        for (int i = 0; i < disponibles.size(); i++) {
            RecursoDigital r = disponibles.get(i);
            System.out.println(i + " - " + r.getIdentificador() +
                    " - " + gestorRecursos.getTitulo(r) + " (" + r.getClass().getSimpleName() + ")");
        }

        System.out.print("Seleccione número de recurso: ");
        try {
            int index = Integer.parseInt(scanner.nextLine());
            if (index >= 0 && index < disponibles.size()) {
                try {
                    // Usar GestorRecursos para prestar el recurso
                    gestorRecursos.prestarRecurso(disponibles.get(index), usuarioActual);
                    System.out.println("Préstamo solicitado. El recurso estará disponible en unos momentos.");
                } catch (RecursoNoDisponibleException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else {
                System.out.println("Índice no válido");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Debe ingresar un número");
        }
    }

    /**
     * Muestra los recursos que pueden ser renovados y permite al usuario seleccionar uno.
     * 
     * @param usuarioActual Usuario que solicita la renovación
     */
    public void renovarRecurso(Usuario usuarioActual) {
        if (usuarioActual == null) {
            System.out.println("Debe seleccionar un usuario primero");
            return;
        }

        // Obtener recursos para renovar usando GestorRecursos
        List<RecursoDigital> renovables = gestorRecursos.obtenerRecursosParaRenovar();
        System.out.println("\n-- RECURSOS PARA RENOVAR --");

        if (renovables.isEmpty()) {
            System.out.println("No hay recursos para renovar");
            return;
        }

        // Mostrar recursos renovables
        for (int i = 0; i < renovables.size(); i++) {
            RecursoDigital r = renovables.get(i);
            System.out.println(i + " - " + r.getIdentificador() +
                    " - " + gestorRecursos.getTitulo(r) + " (" + r.getClass().getSimpleName() + ")");
        }

        System.out.print("Seleccione número de recurso: ");
        try {
            int index = Integer.parseInt(scanner.nextLine());
            if (index >= 0 && index < renovables.size()) {
                try {
                    // Usar GestorRecursos para renovar el recurso
                    gestorRecursos.renovarRecurso(renovables.get(index), usuarioActual);
                    System.out.println("Recurso renovado hasta: " + 
                            ((Renovable)renovables.get(index)).getFechaDevolucion());
                } catch (RecursoNoDisponibleException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else {
                System.out.println("Índice no válido");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Debe ingresar un número");
        }
    }

    /**
     * Muestra los recursos que pueden ser devueltos y permite al usuario seleccionar uno.
     * 
     * @param usuarioActual Usuario que solicita la devolución
     */
    public void devolverRecurso(Usuario usuarioActual) {
        if (usuarioActual == null) {
            System.out.println("Debe seleccionar un usuario primero");
            return;
        }

        // Obtener recursos para devolver usando GestorRecursos
        List<RecursoDigital> prestados = gestorRecursos.obtenerRecursosParaDevolver();
        System.out.println("\n-- RECURSOS PARA DEVOLVER --");

        if (prestados.isEmpty()) {
            System.out.println("No hay recursos para devolver");
            return;
        }

        // Mostrar recursos prestados
        for (int i = 0; i < prestados.size(); i++) {
            RecursoDigital r = prestados.get(i);
            System.out.println(i + " - " + r.getIdentificador() +
                    " - " + gestorRecursos.getTitulo(r) + " (" + r.getClass().getSimpleName() + ")");
        }

        System.out.print("Seleccione número de recurso: ");
        try {
            int index = Integer.parseInt(scanner.nextLine());
            if (index >= 0 && index < prestados.size()) {
                try {
                    // Usar GestorRecursos para devolver el recurso
                    gestorRecursos.devolverRecurso(prestados.get(index), usuarioActual);
                    System.out.println("Recurso devuelto con éxito");
                } catch (RecursoNoDisponibleException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else {
                System.out.println("Índice no válido");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Debe ingresar un número");
        }
    }
}
