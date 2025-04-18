// src/main/java/um/prog2/utilsRecursosCLI/GestorRecursosConsola.java
package um.prog2.cliente.utilsRecursosCLI;

import um.prog2.Enums.EstadoRecurso;
import um.prog2.interfaces.Prestable;
import um.prog2.interfaces.RecursoDigital;
import um.prog2.interfaces.Renovable;
import um.prog2.interfaces.ServicioNotificaciones;
import um.prog2.recursoDigital.*;
import um.prog2.usuario.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GestorRecursosConsola {
    private final Scanner scanner;
    private final List<RecursoDigital> recursos;
    private final ServicioNotificaciones servicioNotificaciones;

    public GestorRecursosConsola(Scanner scanner, List<RecursoDigital> recursos, ServicioNotificaciones servicioNotificaciones) {
        this.scanner = scanner;
        this.recursos = recursos;
        this.servicioNotificaciones = servicioNotificaciones;
    }

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
            recursos.add(nuevoRecurso);
            System.out.println("Recurso creado exitosamente");
        }
    }

    private Libro crearLibro(String id, String titulo) {
        System.out.print("Autor: ");
        String autor = scanner.nextLine();
        System.out.print("Género: ");
        String genero = scanner.nextLine();
        return new Libro(EstadoRecurso.DISPONIBLE, autor, titulo, id, genero);
    }

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

        System.out.print("Género: ");
        String genero = scanner.nextLine();
        return new AudioLibro(id, titulo, autor, narrador, duracion, "Español", "", genero, EstadoRecurso.DISPONIBLE);
    }

    private Revista crearRevista(String id, String titulo) {
        System.out.print("Editorial: ");
        String editorial = scanner.nextLine();
        System.out.print("Categoría: ");
        String categoria = scanner.nextLine();
        return new Revista(EstadoRecurso.DISPONIBLE, 0, "", "", categoria, editorial, titulo, id);
    }

    public void listarRecursos() {
        if (recursos.isEmpty()) {
            System.out.println("No hay recursos registrados");
            return;
        }

        System.out.println("\n-- RECURSOS DISPONIBLES --");
        for (int i = 0; i < recursos.size(); i++) {
            RecursoDigital r = recursos.get(i);
            System.out.println(i + " - " + r.getIdentificador() + " - " +
                    getTitulo(r) + " (" + r.getClass().getSimpleName() + ") - " + r.getEstado());
        }
    }

    public void prestarRecurso(Usuario usuarioActual) {
        if (usuarioActual == null) {
            System.out.println("Debe seleccionar un usuario primero");
            return;
        }

        List<RecursoDigital> disponibles = new ArrayList<>();
        System.out.println("\n-- RECURSOS DISPONIBLES PARA PRÉSTAMO --");

        for (RecursoDigital r : recursos) {
            if (r instanceof Prestable && r.getEstado() == EstadoRecurso.DISPONIBLE) {
                disponibles.add(r);
                System.out.println(disponibles.size()-1 + " - " + r.getIdentificador() +
                        " - " + getTitulo(r) + " (" + r.getClass().getSimpleName() + ")");
            }
        }

        if (disponibles.isEmpty()) {
            System.out.println("No hay recursos disponibles para préstamo");
            return;
        }

        System.out.print("Seleccione número de recurso: ");
        try {
            int index = Integer.parseInt(scanner.nextLine());
            if (index >= 0 && index < disponibles.size()) {
                prestarRecursoEspecifico(disponibles.get(index), usuarioActual);
            } else {
                System.out.println("Índice no válido");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Debe ingresar un número");
        }
    }

    public void renovarRecurso(Usuario usuarioActual) {
        if (usuarioActual == null) {
            System.out.println("Debe seleccionar un usuario primero");
            return;
        }

        List<RecursoDigital> prestados = new ArrayList<>();
        System.out.println("\n-- RECURSOS PARA RENOVAR --");

        for (RecursoDigital r : recursos) {
            if (r instanceof Renovable && r.getEstado() == EstadoRecurso.PRESTADO) {
                prestados.add(r);
                System.out.println(prestados.size()-1 + " - " + r.getIdentificador() +
                        " - " + getTitulo(r) + " (" + r.getClass().getSimpleName() + ")");
            }
        }

        if (prestados.isEmpty()) {
            System.out.println("No hay recursos para renovar");
            return;
        }

        System.out.print("Seleccione número de recurso: ");
        try {
            int index = Integer.parseInt(scanner.nextLine());
            if (index >= 0 && index < prestados.size()) {
                renovarRecursoEspecifico(prestados.get(index), usuarioActual);
            } else {
                System.out.println("Índice no válido");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Debe ingresar un número");
        }
    }

    private void prestarRecursoEspecifico(RecursoDigital recurso, Usuario usuario) {
        Prestable prestable = (Prestable) recurso;
        prestable.prestar(usuario);

        servicioNotificaciones.enviarNotificacion(
                "Préstamo: " + recurso.getIdentificador() + " - " +
                        getTitulo(recurso) + ". Devolución: " + prestable.getFechaDevolucion(),
                usuario
        );

        System.out.println("Recurso prestado hasta: " + prestable.getFechaDevolucion());
    }

    private void renovarRecursoEspecifico(RecursoDigital recurso, Usuario usuario) {
        Renovable renovable = (Renovable) recurso;
        renovable.renovar();

        servicioNotificaciones.enviarNotificacion(
                "Renovación: " + recurso.getIdentificador() + " - " +
                        getTitulo(recurso) + ". Nueva devolución: " + renovable.getFechaDevolucion(),
                usuario
        );

        System.out.println("Recurso renovado hasta: " + renovable.getFechaDevolucion());
    }

    private String getTitulo(RecursoDigital r) {
        if (r instanceof Libro) return ((Libro)r).getTitulo();
        if (r instanceof AudioLibro) return ((AudioLibro)r).getTitulo();
        if (r instanceof Revista) return ((Revista)r).getTitulo();
        return "";
    }
}