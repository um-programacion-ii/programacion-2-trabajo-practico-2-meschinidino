package um.prog2.cliente;

import um.prog2.recursoDigital.*;
import um.prog2.usuario.GestorUsuario;
import um.prog2.usuario.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CLI {
    private static final Scanner scanner = new Scanner(System.in);
    private static Usuario usuario = null;
    private static final List<RecursoDigital> recursos = new ArrayList<>();

    public static void main(String[] args) {
        while (true) {
            mostrarSeparador();
            System.out.println("SISTEMA DE GESTIÓN DE BIBLIOTECA DIGITAL");
            mostrarSeparador();
            System.out.println("1. Gestión de usuarios");
            System.out.println("2. Gestión de recursos");
            System.out.println("3. Salir");
            mostrarSeparador();
            System.out.print("Seleccione una opción: ");
            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    menuUsuarios();
                    break;
                case "2":
                    menuRecursos();
                    break;
                case "3":
                    System.out.println("¡Gracias por usar el sistema!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Opción no válida. Por favor, intente de nuevo.");
            }
        }
    }

    private static void menuUsuarios() {
        mostrarSeparador();
        System.out.println("GESTIÓN DE USUARIOS");
        mostrarSeparador();
        System.out.println("1. Crear usuario");
        System.out.println("2. Ver usuarios");
        System.out.println("3. Volver al menú principal");
        mostrarSeparador();
        System.out.print("Seleccione una opción: ");
        String opcion = scanner.nextLine();

        switch (opcion) {
            case "1":
                crearUsuario();
                break;
            case "2":
                mostrarUsuarios();
                break;
            case "3":
                return;
            default:
                System.out.println("Opción no válida. Por favor, intente de nuevo.");
        }
    }

    private static void menuRecursos() {
        mostrarSeparador();
        System.out.println("GESTIÓN DE RECURSOS DIGITALES");
        mostrarSeparador();
        System.out.println("1. Crear libro");
        System.out.println("2. Crear audiolibro");
        System.out.println("3. Crear revista");
        System.out.println("4. Ver todos los recursos");
        System.out.println("5. Ver detalles de un recurso");
        System.out.println("6. Cambiar estado de un recurso");
        System.out.println("7. Volver al menú principal");
        mostrarSeparador();
        System.out.print("Seleccione una opción: ");
        String opcion = scanner.nextLine();

        switch (opcion) {
            case "1":
                crearLibro();
                break;
            case "2":
                crearAudioLibro();
                break;
            case "3":
                crearRevista();
                break;
            case "4":
                mostrarRecursos();
                break;
            case "5":
                verDetallesRecurso();
                break;
            case "6":
                cambiarEstadoRecurso();
                break;
            case "7":
                return;
            default:
                System.out.println("Opción no válida. Por favor, intente de nuevo.");
        }
    }

    private static void verDetallesRecurso() {
        mostrarSeparador();
        System.out.println("DETALLES DE RECURSO");
        mostrarSeparador();

        if (recursos.isEmpty()) {
            System.out.println("No hay recursos registrados.");
            return;
        }

        // Show simplified list of resources
        for (int i = 0; i < recursos.size(); i++) {
            RecursoDigital recurso = recursos.get(i);
            System.out.println("[" + i + "] ID: " + recurso.getIdentificador() +
                    ", Tipo: " + recurso.getClass().getSimpleName());
        }

        int index = -1;
        try {
            System.out.print("\nSeleccione el número del recurso para ver detalles: ");
            index = Integer.parseInt(scanner.nextLine());

            if (index < 0 || index >= recursos.size()) {
                System.out.println("Índice inválido.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: El índice debe ser un número entero.");
            return;
        }

        // Show full details using toString
        mostrarSeparador();
        System.out.println("DETALLE COMPLETO:");
        System.out.println(recursos.get(index).toString());
        mostrarSeparador();
    }

    private static void crearUsuario() {
        mostrarSeparador();
        System.out.println("CREAR NUEVO USUARIO");
        mostrarSeparador();

        System.out.print("Ingrese el nombre del usuario: ");
        String nombre = scanner.nextLine();

        System.out.print("Ingrese el apellido del usuario: ");
        String apellido = scanner.nextLine();

        int ID = 0;
        try {
            System.out.print("Ingrese el ID del usuario: ");
            ID = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Error: El ID debe ser un número entero.");
            return;
        }

        System.out.print("Ingrese el email del usuario: ");
        String email = scanner.nextLine();

        usuario = GestorUsuario.crearUsuario(nombre, apellido, ID, email);
        System.out.println("Usuario creado exitosamente: " + usuario);
    }

    private static void mostrarUsuarios() {
        mostrarSeparador();
        System.out.println("USUARIOS REGISTRADOS");
        mostrarSeparador();

        if (usuario == null) {
            System.out.println("No hay ningún usuario registrado.");
        } else {
            System.out.println("Usuario: " + usuario);
        }
    }

    private static void crearLibro() {
        mostrarSeparador();
        System.out.println("CREAR NUEVO LIBRO");
        mostrarSeparador();

        System.out.print("Ingrese el identificador: ");
        String id = scanner.nextLine();

        System.out.print("Ingrese el título: ");
        String titulo = scanner.nextLine();

        System.out.print("Ingrese el autor: ");
        String autor = scanner.nextLine();

        try {
            Libro libro = new Libro(EstadoRecurso.DISPONIBLE, autor, titulo, id);
            recursos.add(libro);
            System.out.println("Libro creado exitosamente.");
        } catch (Exception e) {
            System.out.println("Error al crear el libro: " + e.getMessage());
        }
    }

    private static void crearRevista() {
        mostrarSeparador();
        System.out.println("CREAR NUEVA REVISTA");
        mostrarSeparador();

        System.out.print("Ingrese el identificador: ");
        String id = scanner.nextLine();

        System.out.print("Ingrese el título: ");
        String titulo = scanner.nextLine();

        System.out.print("Ingrese la editorial: ");
        String editorial = scanner.nextLine();

        System.out.print("Ingrese la categoría: ");
        String categoria = scanner.nextLine();

        System.out.print("Ingrese la fecha de publicación: ");
        String fechaPublicacion = scanner.nextLine();

        System.out.print("Ingrese el ISSN: ");
        String issn = scanner.nextLine();

        int numeroPaginas = 0;
        try {
            System.out.print("Ingrese el número de páginas: ");
            numeroPaginas = Integer.parseInt(scanner.nextLine());
            if (numeroPaginas <= 0) {
                System.out.println("Error: El número de páginas debe ser positivo.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: El número de páginas debe ser un número entero.");
            return;
        }

        try {
            Revista revista = new Revista(
                    EstadoRecurso.DISPONIBLE,
                    numeroPaginas,
                    issn,
                    fechaPublicacion,
                    categoria,
                    editorial,
                    titulo,
                    id
            );
            recursos.add(revista);
            System.out.println("Revista creada exitosamente.");
        } catch (Exception e) {
            System.out.println("Error al crear la revista: " + e.getMessage());
        }
    }

    private static void crearAudioLibro() {
        mostrarSeparador();
        System.out.println("CREAR NUEVO AUDIOLIBRO");
        mostrarSeparador();

        System.out.print("Ingrese el identificador: ");
        String id = scanner.nextLine();

        System.out.print("Ingrese el título: ");
        String titulo = scanner.nextLine();

        System.out.print("Ingrese el autor: ");
        String autor = scanner.nextLine();

        System.out.print("Ingrese el narrador: ");
        String narrador = scanner.nextLine();

        double duracion = 0;
        try {
            System.out.print("Ingrese la duración (en minutos): ");
            duracion = Double.parseDouble(scanner.nextLine());
            if (duracion <= 0) {
                System.out.println("Error: La duración debe ser positiva.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: La duración debe ser un número.");
            return;
        }

        System.out.print("Ingrese el idioma: ");
        String idioma = scanner.nextLine();

        System.out.print("Ingrese el ISBN: ");
        String isbn = scanner.nextLine();

        try {
            AudioLibro audioLibro = new AudioLibro(id, titulo, autor, narrador, duracion, idioma, isbn, EstadoRecurso.DISPONIBLE);
            recursos.add(audioLibro);
            System.out.println("Audiolibro creado exitosamente.");
        } catch (Exception e) {
            System.out.println("Error al crear el audiolibro: " + e.getMessage());
        }
    }

    private static void mostrarRecursos() {
        mostrarSeparador();
        System.out.println("RECURSOS DISPONIBLES");
        mostrarSeparador();

        if (recursos.isEmpty()) {
            System.out.println("No hay recursos registrados.");
        } else {
            for (int i = 0; i < recursos.size(); i++) {
                RecursoDigital recurso = recursos.get(i);
                System.out.println("[" + i + "] " + recurso.toString());
            }
        }
    }

    private static void cambiarEstadoRecurso() {
        mostrarSeparador();
        System.out.println("CAMBIAR ESTADO DE RECURSO");
        mostrarSeparador();

        if (recursos.isEmpty()) {
            System.out.println("No hay recursos registrados.");
            return;
        }

        mostrarRecursos();

        int index = -1;
        try {
            System.out.print("Seleccione el número del recurso: ");
            index = Integer.parseInt(scanner.nextLine());

            if (index < 0 || index >= recursos.size()) {
                System.out.println("Índice inválido.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: El índice debe ser un número entero.");
            return;
        }

        System.out.println("Estados disponibles:");
        System.out.println("1. DISPONIBLE");
        System.out.println("2. PRESTADO");
        System.out.println("3. RESERVADO");

        System.out.print("Seleccione el nuevo estado: ");
        String estadoStr = scanner.nextLine();

        EstadoRecurso nuevoEstado;
        switch (estadoStr) {
            case "1":
                nuevoEstado = EstadoRecurso.DISPONIBLE;
                break;
            case "2":
                nuevoEstado = EstadoRecurso.PRESTADO;
                break;
            case "3":
                nuevoEstado = EstadoRecurso.RESERVADO;
                break;
            default:
                System.out.println("Opción inválida.");
                return;
        }

        recursos.get(index).actualizarEstado(nuevoEstado);
        System.out.println("Estado actualizado exitosamente.");
    }


    private static void mostrarSeparador() {
        System.out.println("------------------------------------------------");
    }
}