package um.prog2.cliente;

import um.prog2.notificaciones.ServicioNotificaciones;
import um.prog2.notificaciones.ServicioNotificacionesEmail;
import um.prog2.notificaciones.ServicioNotificacionesSMS;
import um.prog2.recursoDigital.*;
import um.prog2.usuario.GestorUsuario;
import um.prog2.usuario.Usuario;

import java.util.*;
import java.util.stream.Collectors;

public class CLI {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<String, Usuario> usuarios = new HashMap<>();
    private static Usuario usuarioActual = null;
    private static final List<RecursoDigital> recursos = new ArrayList<>();
    private static final ServicioNotificaciones servicioEmail = new ServicioNotificacionesEmail();
    private static final ServicioNotificaciones servicioSMS = new ServicioNotificacionesSMS();
    private static ServicioNotificaciones servicioPreferido;

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
        System.out.println("3. Seleccionar usuario");
        System.out.println("4. Buscar usuarios");
        System.out.println("5. Volver al menú principal");
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
                seleccionarUsuario();
                break;
            case "4":
                buscarUsuarios();
                break;
            case "5":
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
        System.out.println("6. Buscar recursos");
        System.out.println("7. Cambiar estado de un recurso");
        System.out.println("8. Prestar un recurso");
        System.out.println("9. Renovar un recurso");
        System.out.println("10. Volver al menú principal");
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
                buscarRecursos();
                break;
            case "7":
                cambiarEstadoRecurso();
                break;
            case "8":
                prestarRecurso();
                break;
            case "9":
                renovarRecurso();
                break;
            case "10":
                return;
            default:
                System.out.println("Opción no válida. Por favor, intente de nuevo.");
        }
    }

    private static void buscarRecursos() {
        mostrarSeparador();
        System.out.println("BUSCAR RECURSOS");
        mostrarSeparador();

        if (recursos.isEmpty()) {
            System.out.println("No hay recursos registrados.");
            return;
        }

        System.out.println("Seleccione criterio de búsqueda:");
        System.out.println("1. Por identificador");
        System.out.println("2. Por tipo de recurso");
        System.out.println("3. Por estado");
        System.out.println("4. Por título (usando Streams)");
        System.out.println("5. Por género/categoría");  // New option
        System.out.println("6. Búsqueda general");     // Updated number

        String opcion = scanner.nextLine();
        List<RecursoDigital> resultados = new ArrayList<>();
        String criterioBusqueda;

        switch (opcion) {
            case "1":
                System.out.print("Ingrese el identificador a buscar: ");
                criterioBusqueda = scanner.nextLine().toLowerCase();

                for (RecursoDigital recurso : recursos) {
                    if (recurso.getIdentificador().toLowerCase().contains(criterioBusqueda)) {
                        resultados.add(recurso);
                    }
                }
                break;
            case "2":
                System.out.println("Seleccione el tipo de recurso:");
                System.out.println("1. Libro");
                System.out.println("2. AudioLibro");
                System.out.println("3. Revista");
                String tipoRecurso = scanner.nextLine();

                Class<?> claseRecurso = null;
                switch (tipoRecurso) {
                    case "1":
                        claseRecurso = Libro.class;
                        break;
                    case "2":
                        claseRecurso = AudioLibro.class;
                        break;
                    case "3":
                        claseRecurso = Revista.class;
                        break;
                    default:
                        System.out.println("Tipo de recurso no válido.");
                        return;
                }

                for (RecursoDigital recurso : recursos) {
                    if (recurso.getClass().equals(claseRecurso)) {
                        resultados.add(recurso);
                    }
                }
                break;
            case "3":
                System.out.println("Seleccione el estado:");
                System.out.println("1. DISPONIBLE");
                System.out.println("2. PRESTADO");
                System.out.println("3. RESERVADO");
                String estadoStr = scanner.nextLine();

                EstadoRecurso estado = null;
                switch (estadoStr) {
                    case "1":
                        estado = EstadoRecurso.DISPONIBLE;
                        break;
                    case "2":
                        estado = EstadoRecurso.PRESTADO;
                        break;
                    case "3":
                        estado = EstadoRecurso.RESERVADO;
                        break;
                    default:
                        System.out.println("Estado no válido.");
                        return;
                }

                for (RecursoDigital recurso : recursos) {
                    if (recurso.getEstado() == estado) {
                        resultados.add(recurso);
                    }
                }
                break;
            case "4":
                // New case for title search using Streams
                System.out.print("Ingrese el título a buscar: ");
                final String tituloBusqueda = scanner.nextLine().toLowerCase();

                resultados = recursos.stream()
                        .filter(recurso -> {
                            // Match based on resource type
                            if (recurso instanceof Libro) {
                                return ((Libro) recurso).getTitulo().toLowerCase().contains(tituloBusqueda);
                            } else if (recurso instanceof AudioLibro) {
                                return ((AudioLibro) recurso).getTitulo().toLowerCase().contains(tituloBusqueda);
                            } else if (recurso instanceof Revista) {
                                return ((Revista) recurso).getTitulo().toLowerCase().contains(tituloBusqueda);
                            }
                            return false;
                        })
                        .collect(Collectors.toList());
                break;
            case "5":
                // Search by genre/category
                System.out.print("Ingrese el género o categoría a buscar: ");
                final String generoBusqueda = scanner.nextLine().toLowerCase();

                resultados = recursos.stream()
                        .filter(recurso -> {
                            if (recurso instanceof Libro) {
                                return ((Libro) recurso).getGenero() != null &&
                                        ((Libro) recurso).getGenero().toLowerCase().contains(generoBusqueda);
                            } else if (recurso instanceof AudioLibro) {
                                return ((AudioLibro) recurso).getGenero() != null &&
                                        ((AudioLibro) recurso).getGenero().toLowerCase().contains(generoBusqueda);
                            } else if (recurso instanceof Revista) {
                                return ((Revista) recurso).getCategoria() != null &&
                                        ((Revista) recurso).getCategoria().toLowerCase().contains(generoBusqueda);
                            }
                            return false;
                        })
                        .collect(Collectors.toList());
                break;
            case "6":
                System.out.print("Ingrese texto a buscar en cualquier campo: ");
                criterioBusqueda = scanner.nextLine().toLowerCase();

                for (RecursoDigital recurso : recursos) {
                    String infoRecurso = recurso.toString().toLowerCase();
                    if (infoRecurso.contains(criterioBusqueda)) {
                        resultados.add(recurso);
                    }
                }
                break;
            default:
                System.out.println("Opción no válida.");
                return;
        }

        mostrarResultadosRecursos(resultados);
    }

    private static void mostrarResultadosRecursos(List<RecursoDigital> resultados) {
        mostrarSeparador();
        if (resultados.isEmpty()) {
            System.out.println("No se encontraron recursos que coincidan con el criterio de búsqueda.");
        } else {
            System.out.println("RESULTADOS DE BÚSQUEDA (" + resultados.size() + " encontrados):");
            for (int i = 0; i < resultados.size(); i++) {
                RecursoDigital recurso = resultados.get(i);
                System.out.println("[" + i + "] ID: " + recurso.getIdentificador() +
                        ", Tipo: " + recurso.getClass().getSimpleName() +
                        ", Estado: " + recurso.getEstado());
            }

            // Option to view details of a resource from results
            System.out.print("¿Desea ver los detalles de algún recurso? (S/N): ");
            String seleccionar = scanner.nextLine();
            if (seleccionar.equalsIgnoreCase("S")) {
                try {
                    System.out.print("Ingrese el número del recurso a ver: ");
                    int index = Integer.parseInt(scanner.nextLine());

                    if (index >= 0 && index < resultados.size()) {
                        mostrarSeparador();
                        System.out.println("DETALLE COMPLETO:");
                        System.out.println(resultados.get(index).toString());
                        mostrarSeparador();

                        // Additional actions menu for the selected resource
                        mostrarAccionesRecurso(resultados.get(index));
                    } else {
                        System.out.println("Índice inválido.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: El índice debe ser un número entero.");
                }
            }
        }
    }

    private static void mostrarAccionesRecurso(RecursoDigital recurso) {
        System.out.println("\nAcciones disponibles para este recurso:");
        System.out.println("1. Cambiar estado");
        if (recurso instanceof Prestable && recurso.getEstado() == EstadoRecurso.DISPONIBLE) {
            System.out.println("2. Prestar recurso");
        }
        if (recurso instanceof Renovable && recurso.getEstado() == EstadoRecurso.PRESTADO) {
            System.out.println("3. Renovar préstamo");
        }
        System.out.println("4. Volver al menú");

        System.out.print("Seleccione una opción: ");
        String opcion = scanner.nextLine();

        switch (opcion) {
            case "1":
                cambiarEstadoRecursoEspecifico(recurso);
                break;
            case "2":
                if (recurso instanceof Prestable && recurso.getEstado() == EstadoRecurso.DISPONIBLE) {
                    prestarRecursoEspecifico(recurso);
                } else {
                    System.out.println("Opción no válida para este recurso.");
                }
                break;
            case "3":
                if (recurso instanceof Renovable && recurso.getEstado() == EstadoRecurso.PRESTADO) {
                    renovarRecursoEspecifico(recurso);
                } else {
                    System.out.println("Opción no válida para este recurso.");
                }
                break;
            case "4":
                return;
            default:
                System.out.println("Opción no válida.");
        }
    }

    private static void cambiarEstadoRecursoEspecifico(RecursoDigital recurso) {
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

        recurso.actualizarEstado(nuevoEstado);
        System.out.println("Estado actualizado exitosamente.");
    }

    private static void prestarRecursoEspecifico(RecursoDigital recurso) {
        if (usuarioActual == null) {
            System.out.println("Error: Debe seleccionar un usuario antes de prestar un recurso.");
            return;
        }

        Prestable prestable = (Prestable) recurso;

        if (!prestable.estaDisponible()) {
            System.out.println("Este recurso no está disponible para préstamo.");
            return;
        }

        prestable.prestar(usuarioActual);

        if (servicioPreferido != null) {
            servicioPreferido.enviarNotificacion(
                    "Has tomado prestado recurso ID: " + recurso.getIdentificador() +
                            " (" + recurso.getClass().getSimpleName() + ")" +
                            ". Fecha de devolución: " + prestable.getFechaDevolucion(),
                    usuarioActual
            );
        }

        System.out.println("Recurso prestado exitosamente hasta: " + prestable.getFechaDevolucion());
    }

    private static void renovarRecursoEspecifico(RecursoDigital recurso) {
        if (usuarioActual == null) {
            System.out.println("Error: Debe seleccionar un usuario antes de renovar un recurso.");
            return;
        }

        Renovable renovable = (Renovable) recurso;
        renovable.renovar();

        if (servicioPreferido != null) {
            servicioPreferido.enviarNotificacion(
                    "Has renovado recurso ID: " + recurso.getIdentificador() +
                            " (" + recurso.getClass().getSimpleName() + ")" +
                            ". Nueva fecha de devolución: " + renovable.getFechaDevolucion(),
                    usuarioActual
            );
        }

        System.out.println("Recurso renovado exitosamente hasta: " + renovable.getFechaDevolucion());
    }

    private static void buscarUsuarios() {
        mostrarSeparador();
        System.out.println("BUSCAR USUARIOS");
        mostrarSeparador();

        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }

        System.out.println("Seleccione criterio de búsqueda:");
        System.out.println("1. Por nombre");
        System.out.println("2. Por apellido");
        System.out.println("3. Por email");
        System.out.println("4. Búsqueda general");

        String opcion = scanner.nextLine();
        String criterioBusqueda;
        List<Usuario> resultados = new ArrayList<>();

        switch (opcion) {
            case "1":
                System.out.print("Ingrese el nombre a buscar: ");
                criterioBusqueda = scanner.nextLine().toLowerCase();

                for (Usuario usuario : usuarios.values()) {
                    if (usuario.getNombre().toLowerCase().contains(criterioBusqueda)) {
                        resultados.add(usuario);
                    }
                }
                break;
            case "2":
                System.out.print("Ingrese el apellido a buscar: ");
                criterioBusqueda = scanner.nextLine().toLowerCase();

                for (Usuario usuario : usuarios.values()) {
                    if (usuario.getApellido().toLowerCase().contains(criterioBusqueda)) {
                        resultados.add(usuario);
                    }
                }
                break;
            case "3":
                System.out.print("Ingrese el email a buscar: ");
                criterioBusqueda = scanner.nextLine().toLowerCase();

                for (Usuario usuario : usuarios.values()) {
                    if (usuario.getEmail().toLowerCase().contains(criterioBusqueda)) {
                        resultados.add(usuario);
                    }
                }
                break;
            case "4":
                System.out.print("Ingrese texto a buscar en cualquier campo: ");
                criterioBusqueda = scanner.nextLine().toLowerCase();

                for (Usuario usuario : usuarios.values()) {
                    if (usuario.getNombre().toLowerCase().contains(criterioBusqueda) ||
                            usuario.getApellido().toLowerCase().contains(criterioBusqueda) ||
                            usuario.getEmail().toLowerCase().contains(criterioBusqueda) ||
                            String.valueOf(usuario.getID()).contains(criterioBusqueda)) {
                        resultados.add(usuario);
                    }
                }
                break;
            default:
                System.out.println("Opción no válida.");
                return;
        }

        // Display results
        mostrarResultadosBusqueda(resultados);
    }

    private static void mostrarResultadosBusqueda(List<Usuario> resultados) {
        mostrarSeparador();
        if (resultados.isEmpty()) {
            System.out.println("No se encontraron usuarios que coincidan con el criterio de búsqueda.");
        } else {
            System.out.println("RESULTADOS DE BÚSQUEDA (" + resultados.size() + " encontrados):");
            for (Usuario usuario : resultados) {
                System.out.println("ID: " + usuario.getID() +
                        " - " + usuario.getNombre() + " " + usuario.getApellido() +
                        " - Email: " + usuario.getEmail());
            }

            // Option to select a user from results
            System.out.print("¿Desea seleccionar algún usuario? (S/N): ");
            String seleccionar = scanner.nextLine();
            if (seleccionar.equalsIgnoreCase("S")) {
                System.out.print("Ingrese el ID del usuario a seleccionar: ");
                String userID = scanner.nextLine();

                if (usuarios.containsKey(userID)) {
                    usuarioActual = usuarios.get(userID);
                    System.out.println("Usuario seleccionado: " + usuarioActual);
                } else {
                    System.out.println("No se encontró un usuario con ese ID.");
                }
            }
        }
    }

    private static void prestarRecurso() {
        mostrarSeparador();
        System.out.println("PRESTAR RECURSO");
        mostrarSeparador();

        // Check if there's a user
        if (usuarioActual == null) {
            System.out.println("Error: Debe crear un usuario antes de prestar un recurso.");
            return;
        }

        if (recursos.isEmpty()) {
            System.out.println("No hay recursos registrados.");
            return;
        }

        // List only prestable resources
        System.out.println("Recursos que pueden ser prestados:");
        List<RecursoDigital> prestables = new ArrayList<>();

        for (RecursoDigital recurso : recursos) {
            if (recurso instanceof Prestable) {
                prestables.add(recurso);
                System.out.println("[" + (prestables.size() - 1) + "] ID: " + recurso.getIdentificador() +
                        ", Tipo: " + recurso.getClass().getSimpleName() +
                        ", Estado: " + recurso.getEstado());
            }
        }

        if (prestables.isEmpty()) {
            System.out.println("No hay recursos que puedan ser prestados.");
            return;
        }

        int index = -1;
        try {
            System.out.print("\nSeleccione el número del recurso para prestar: ");
            index = Integer.parseInt(scanner.nextLine());

            if (index < 0 || index >= prestables.size()) {
                System.out.println("Índice inválido.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: El índice debe ser un número entero.");
            return;
        }

        RecursoDigital recursoSeleccionado = prestables.get(index);
        Prestable prestable = (Prestable) recursoSeleccionado;

        if (!prestable.estaDisponible()) {
            System.out.println("Este recurso no está disponible para préstamo.");
            return;
        }

        prestable.prestar(usuarioActual);

        if (servicioPreferido != null && usuarioActual != null) {
            servicioPreferido.enviarNotificacion(
                    "Has tomado prestado recurso ID: " + recursoSeleccionado.getIdentificador() +
                            " (" + recursoSeleccionado.getClass().getSimpleName() + ")" +
                            ". Fecha de devolución: " + prestable.getFechaDevolucion(),
                    usuarioActual
            );
        }

        System.out.println("Recurso prestado exitosamente hasta: " + prestable.getFechaDevolucion());
    }

    private static void renovarRecurso() {
        mostrarSeparador();
        System.out.println("RENOVAR RECURSO");
        mostrarSeparador();

        // Check if there's a user
        if (usuarioActual == null) {
            System.out.println("Error: Debe crear un usuario antes de renovar un recurso.");
            return;
        }

        if (recursos.isEmpty()) {
            System.out.println("No hay recursos registrados.");
            return;
        }

        // List only renewable resources that are currently loaned
        System.out.println("Recursos que pueden ser renovados:");
        List<RecursoDigital> renovables = new ArrayList<>();

        for (int i = 0; i < recursos.size(); i++) {
            RecursoDigital recurso = recursos.get(i);
            if (recurso instanceof Renovable && recurso.getEstado() == EstadoRecurso.PRESTADO) {
                renovables.add(recurso);
                System.out.println("[" + (renovables.size() - 1) + "] ID: " + recurso.getIdentificador() +
                        ", Tipo: " + recurso.getClass().getSimpleName());
            }
        }

        if (renovables.isEmpty()) {
            System.out.println("No hay recursos que puedan ser renovados.");
            return;
        }

        int index = -1;
        try {
            System.out.print("\nSeleccione el número del recurso para renovar: ");
            index = Integer.parseInt(scanner.nextLine());

            if (index < 0 || index >= renovables.size()) {
                System.out.println("Índice inválido.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: El índice debe ser un número entero.");
            return;
        }

        RecursoDigital recursoSeleccionado = renovables.get(index);
        Renovable renovable = (Renovable) recursoSeleccionado;

        renovable.renovar();
        if (servicioPreferido != null && usuarioActual != null) {
            servicioPreferido.enviarNotificacion(
                    "Has renovado recurso ID: " + recursoSeleccionado.getIdentificador() +
                            " (" + recursoSeleccionado.getClass().getSimpleName() + ")" +
                            ". Nueva fecha de devolución: " + renovable.getFechaDevolucion(),
                    usuarioActual
            );
        }

        System.out.println("Recurso renovado exitosamente hasta: " + renovable.getFechaDevolucion());
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

        int telefono = 0;
        try {
            System.out.print("Ingrese el numero de telefono del usuario: ");
            telefono = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Error: El teléfono debe ser un número entero.");
            return;
        }

        // Create user and add to map
        Usuario nuevoUsuario = GestorUsuario.crearUsuario(nombre, apellido, ID, email, telefono);
        usuarios.put(String.valueOf(ID), nuevoUsuario);
        usuarioActual = nuevoUsuario; // Set as current user

        // Ask for notification preference with validation
        boolean validOption = false;
        while (!validOption) {
            System.out.println("Seleccione el método de notificación preferido:");
            System.out.println("1. Email");
            System.out.println("2. SMS");

            String opcionNotificacion = scanner.nextLine();

            switch(opcionNotificacion) {
                case "1":
                    servicioPreferido = servicioEmail;
                    validOption = true;
                    break;
                case "2":
                    servicioPreferido = servicioSMS;
                    validOption = true;
                    break;
                default:
                    System.out.println("Por favor elija un tipo de notificacion para continuar");
            }
        }

        servicioPreferido.enviarNotificacion("Bienvenido al sistema de biblioteca digital!", usuarioActual);
        System.out.println("Usuario creado exitosamente: " + nuevoUsuario);
    }

    private static void mostrarUsuarios() {
        mostrarSeparador();
        System.out.println("USUARIOS REGISTRADOS");
        mostrarSeparador();

        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
        } else {
            for (Usuario usuario : usuarios.values()) {
                System.out.println("ID: " + usuario.getID() + " - " + usuario);
            }

            if (usuarioActual != null) {
                System.out.println("\nUsuario actual: " + usuarioActual);
            } else {
                System.out.println("\nNo hay usuario seleccionado.");
            }
        }
    }

    private static void seleccionarUsuario() {
        mostrarSeparador();
        System.out.println("SELECCIONAR USUARIO");
        mostrarSeparador();

        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }

        System.out.println("Usuarios disponibles:");
        for (Usuario usuario : usuarios.values()) {
            System.out.println("ID: " + usuario.getID() + " - " + usuario.getNombre() + " " + usuario.getApellido());
        }

        System.out.print("\nIngrese el ID del usuario a seleccionar: ");
        String userID = scanner.nextLine();

        if (usuarios.containsKey(userID)) {
            usuarioActual = usuarios.get(userID);
            System.out.println("Usuario seleccionado: " + usuarioActual);
        } else {
            System.out.println("No se encontró un usuario con ese ID.");
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

        System.out.print("Ingrese el género: ");
        String genero = scanner.nextLine();

        try {
            Libro libro = new Libro(EstadoRecurso.DISPONIBLE, autor, titulo, id, genero);
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

        System.out.print("Ingrese la duración (en horas): ");
        double duracion = Double.parseDouble(scanner.nextLine());

        System.out.print("Ingrese el idioma: ");
        String idioma = scanner.nextLine();

        System.out.print("Ingrese el ISBN: ");
        String isbn = scanner.nextLine();

        System.out.print("Ingrese el género: ");
        String genero = scanner.nextLine();

        try {
            AudioLibro audioLibro = new AudioLibro(id, titulo, autor, narrador, duracion, idioma, isbn, genero, EstadoRecurso.DISPONIBLE);
            recursos.add(audioLibro);
            System.out.println("AudioLibro creado exitosamente.");
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