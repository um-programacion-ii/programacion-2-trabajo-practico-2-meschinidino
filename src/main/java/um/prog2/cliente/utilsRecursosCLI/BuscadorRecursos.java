package um.prog2.cliente.utilsRecursosCLI;

import um.prog2.interfaces.RecursoDigital;
import um.prog2.recursoDigital.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;

public class BuscadorRecursos {
    private final Scanner scanner;
    private final List<RecursoDigital> recursos;

    public BuscadorRecursos(Scanner scanner, List<RecursoDigital> recursos) {
        this.scanner = scanner;
        this.recursos = recursos;
    }

    public void mostrarMenuBusqueda() {
        System.out.println("\n-- BÚSQUEDA DE RECURSOS --");
        System.out.println("1. Buscar por título");
        System.out.println("2. Buscar por identificador/ID");
        System.out.println("3. Buscar por autor");
        System.out.println("4. Buscar por género/categoría");
        System.out.println("5. Volver");

        System.out.print("Seleccione una opción: ");
        String opcion = scanner.nextLine();

        switch (opcion) {
            case "1": buscarPorTitulo(); break;
            case "2": buscarPorIdentificador(); break;
            case "3": buscarPorAutor(); break;
            case "4": buscarPorGenero(); break;
            case "5": return;
            default: System.out.println("Opción no válida");
        }
    }

    private void buscarPorTitulo() {
        System.out.print("Ingrese el título o parte del título a buscar: ");
        String terminoBusqueda = scanner.nextLine().toLowerCase();

        List<RecursoDigital> resultados = buscar(recurso ->
                getTitulo(recurso).toLowerCase().contains(terminoBusqueda));

        mostrarResultadosBusqueda(resultados, "título");
    }

    private void buscarPorIdentificador() {
        System.out.print("Ingrese el identificador a buscar: ");
        String terminoBusqueda = scanner.nextLine();

        List<RecursoDigital> resultados = buscar(recurso ->
                recurso.getIdentificador().equals(terminoBusqueda));

        mostrarResultadosBusqueda(resultados, "identificador");
    }

    private void buscarPorAutor() {
        System.out.print("Ingrese el autor a buscar: ");
        String terminoBusqueda = scanner.nextLine().toLowerCase();

        List<RecursoDigital> resultados = buscar(recurso -> {
            if (recurso instanceof Libro) {
                return ((Libro) recurso).getAutor().toLowerCase().contains(terminoBusqueda);
            } else if (recurso instanceof AudioLibro) {
                return ((AudioLibro) recurso).getAutor().toLowerCase().contains(terminoBusqueda);
            }
            return false;
        });

        mostrarResultadosBusqueda(resultados, "autor");
    }

    private void buscarPorGenero() {
        System.out.print("Ingrese el género/categoría a buscar: ");
        String terminoBusqueda = scanner.nextLine().toLowerCase();

        List<RecursoDigital> resultados = buscar(recurso -> {
            if (recurso instanceof Libro) {
                return ((Libro) recurso).getGenero().toLowerCase().contains(terminoBusqueda);
            } else if (recurso instanceof AudioLibro) {
                return ((AudioLibro) recurso).getGenero().toLowerCase().contains(terminoBusqueda);
            } else if (recurso instanceof Revista) {
                return ((Revista) recurso).getCategoria().toLowerCase().contains(terminoBusqueda);
            }
            return false;
        });

        mostrarResultadosBusqueda(resultados, "género/categoría");
    }

    private List<RecursoDigital> buscar(Predicate<RecursoDigital> condicion) {
        List<RecursoDigital> resultados = new ArrayList<>();
        for (RecursoDigital recurso : recursos) {
            if (condicion.test(recurso)) {
                resultados.add(recurso);
            }
        }
        return resultados;
    }

    private void mostrarResultadosBusqueda(List<RecursoDigital> resultados, String criterioBusqueda) {
        System.out.println("\n-- RESULTADOS DE BÚSQUEDA POR " + criterioBusqueda.toUpperCase() + " --");

        if (resultados.isEmpty()) {
            System.out.println("No se encontraron recursos que coincidan con la búsqueda.");
            return;
        }

        for (int i = 0; i < resultados.size(); i++) {
            RecursoDigital r = resultados.get(i);
            System.out.println(i + " - " + r.getIdentificador() + " - " +
                    getTitulo(r) + " (" + r.getClass().getSimpleName() + ") - " + r.getEstado());
        }
    }

    private String getTitulo(RecursoDigital r) {
        if (r instanceof Libro) return ((Libro)r).getTitulo();
        if (r instanceof AudioLibro) return ((AudioLibro)r).getTitulo();
        if (r instanceof Revista) return ((Revista)r).getTitulo();
        return "";
    }
}