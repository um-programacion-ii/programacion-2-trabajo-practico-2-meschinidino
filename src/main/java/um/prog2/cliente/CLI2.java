// src/main/java/um/prog2/cliente/CLI2.java
package um.prog2.cliente;

import um.prog2.Enums.EstadoRecurso;
import um.prog2.cliente.prestamos.GestorPrestamosConsola;
import um.prog2.cliente.reservas.GestorReservasConsola;
import um.prog2.cliente.utilsRecursosCLI.BuscadorRecursos;
import um.prog2.interfaces.RecursoDigital;
import um.prog2.interfaces.ServicioNotificaciones;
import um.prog2.notificaciones.Notificacion;
import um.prog2.notificaciones.ServicioEnvioNotificaciones;
import um.prog2.notificaciones.ServicioNotificacionesAdapter;
import um.prog2.notificaciones.ServicioNotificacionesEmail;
import um.prog2.notificaciones.ServicioNotificacionesSMS;
import um.prog2.prestamos.SistemaPrestamos;
import um.prog2.recursoDigital.*;
import um.prog2.usuario.GestorUsuario;
import um.prog2.usuario.Usuario;
import um.prog2.cliente.utilsRecursosCLI.GestorRecursosConsola;
import um.prog2.cliente.utilsUsuariosCLI.GestorUsuarioConsola;

import java.util.*;

public class CLI2 {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<String, Usuario> usuarios = new HashMap<>();
    private static Usuario usuarioActual = null;
    private static final List<RecursoDigital> recursos = new ArrayList<>();
    private static ServicioEnvioNotificaciones servicioEnvioNotificaciones = new ServicioEnvioNotificaciones();
    private static ServicioNotificaciones servicioNotificaciones;

    // Gestores modulares
    private static GestorUsuarioConsola gestorUsuario;
    private static GestorRecursosConsola gestorRecursos;
    private static BuscadorRecursos buscadorRecursos;
    private static SistemaPrestamos sistemaPrestamos;
    private static GestorPrestamosConsola gestorPrestamos;
    private static GestorReservasConsola gestorReservas;

    public static void main(String[] args) {
        // Inicializar sistema de notificaciones
        // Registrar servicios de notificación
        ServicioNotificacionesEmail servicioEmail = new ServicioNotificacionesEmail();
        ServicioNotificacionesSMS servicioSMS = new ServicioNotificacionesSMS();
        servicioEnvioNotificaciones.registrarServicio(servicioEmail);
        servicioEnvioNotificaciones.registrarServicio(servicioSMS);

        // Crear adaptador para compatibilidad con la interfaz ServicioNotificaciones
        servicioNotificaciones = new ServicioNotificacionesAdapter(servicioEnvioNotificaciones, "Sistema");

        // Inicializar gestores
        gestorUsuario = new GestorUsuarioConsola(scanner, usuarios, servicioNotificaciones);
        gestorRecursos = new GestorRecursosConsola(scanner, recursos, servicioNotificaciones);
        buscadorRecursos = new BuscadorRecursos(scanner, recursos);
        sistemaPrestamos = new SistemaPrestamos(servicioNotificaciones);
        gestorPrestamos = new GestorPrestamosConsola(scanner, sistemaPrestamos, recursos);
        gestorReservas = new GestorReservasConsola(servicioNotificaciones, sistemaPrestamos);

        cargarDatosDePrueba();

        while (true) {
            mostrarMenuPrincipal();
            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1": gestionUsuarios(); break;
                case "2": gestionRecursos(); break;
                case "3": gestionPrestamos(); break;
                case "4": gestionReservas(); break;
                case "5": verNotificaciones(); break;
                case "6":
                    System.out.println("¡Gracias por usar el sistema!");
                    sistemaPrestamos.cerrar();
                    gestorReservas.cerrar();
                    servicioEnvioNotificaciones.cerrar();
                    scanner.close();
                    return;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
    }

    private static void mostrarMenuPrincipal() {
        System.out.println("\n==== SISTEMA DE BIBLIOTECA DIGITAL ====");
        mostrarUsuarioActual();
        System.out.println("1. Gestión de usuarios");
        System.out.println("2. Gestión de recursos");
        System.out.println("3. Gestión de préstamos");
        System.out.println("4. Gestión de reservas");
        System.out.println("5. Ver notificaciones");
        System.out.println("6. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private static void mostrarUsuarioActual() {
        if (usuarioActual != null) {
            System.out.println("Usuario actual: " + usuarioActual.getNombre() + " " + usuarioActual.getApellido());
        } else {
            System.out.println("No hay usuario seleccionado");
        }
    }

    // === GESTIÓN DE USUARIOS ===
    private static void gestionUsuarios() {
        while (true) {
            System.out.println("\n==== GESTIÓN DE USUARIOS ====");
            System.out.println("1. Crear usuario");
            System.out.println("2. Ver y seleccionar usuario");
            System.out.println("3. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    usuarioActual = gestorUsuario.crearUsuario();
                    // Actualizar servicio de notificaciones si cambia
                    servicioNotificaciones = gestorUsuario.getServicioNotificaciones();
                    break;
                case "2":
                    Usuario seleccionado = gestorUsuario.seleccionarUsuario();
                    if (seleccionado != null) {
                        usuarioActual = seleccionado;
                    }
                    break;
                case "3": return;
                default: System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
    }

    // === GESTIÓN DE PRÉSTAMOS ===
    private static void gestionPrestamos() {
        if (usuarioActual == null) {
            System.out.println("Debe seleccionar un usuario primero");
            return;
        }

        gestorPrestamos.mostrarMenuPrestamos(usuarioActual);
    }

    // === GESTIÓN DE RESERVAS ===
    private static void gestionReservas() {
        if (usuarioActual == null) {
            System.out.println("Debe seleccionar un usuario primero");
            return;
        }

        boolean volverAlMenuPrincipal = false;
        while (!volverAlMenuPrincipal) {
            volverAlMenuPrincipal = gestorReservas.mostrarMenuReservas(usuarioActual);
        }
    }

    // === VER NOTIFICACIONES ===
    private static void verNotificaciones() {
        System.out.println("\n==== NOTIFICACIONES ====");

        if (usuarioActual == null) {
            System.out.println("Debe seleccionar un usuario primero");
            return;
        }

        List<Notificacion> historial = servicioEnvioNotificaciones.getHistorialNotificaciones();

        if (historial.isEmpty()) {
            System.out.println("No hay notificaciones para mostrar");
            return;
        }

        System.out.println("Notificaciones para " + usuarioActual.getNombre() + " " + usuarioActual.getApellido() + ":");

        int contador = 0;
        for (Notificacion notificacion : historial) {
            if (notificacion.getDestinatario().getID() == usuarioActual.getID()) {
                System.out.println((++contador) + ". " + notificacion);
            }
        }

        if (contador == 0) {
            System.out.println("No hay notificaciones para este usuario");
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    // === GESTIÓN DE RECURSOS ===
    private static void gestionRecursos() {
        while (true) {
            System.out.println("\n==== GESTIÓN DE RECURSOS ====");
            System.out.println("1. Crear recurso");
            System.out.println("2. Ver recursos");
            System.out.println("3. Prestar recurso");
            System.out.println("4. Renovar recurso");
            System.out.println("5. Buscar recursos"); // New option
            System.out.println("6. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1": gestorRecursos.crearRecurso(); break;
                case "2": gestorRecursos.listarRecursos(); break;
                case "3": gestorRecursos.prestarRecurso(usuarioActual); break;
                case "4": gestorRecursos.renovarRecurso(usuarioActual); break;
                case "5": buscadorRecursos.mostrarMenuBusqueda(); break; // Call the new search menu
                case "6": return;
                default: System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
    }

    // Método para cargar datos de prueba iniciales
    private static void cargarDatosDePrueba() {
        // Crear un usuario de ejemplo
        Usuario usuario = GestorUsuario.crearUsuario("Juan", "Pérez", 1, "juan@example.com", 123456789);
        usuarios.put("1", usuario);

        // Crear algunos recursos de ejemplo
        Libro libro = new Libro(EstadoRecurso.DISPONIBLE, "Gabriel García Márquez",
                "Cien años de soledad", "L001", "Realismo mágico");
        recursos.add(libro);

        AudioLibro audioLibro = new AudioLibro("A001", "1984", "George Orwell", "John Smith",
                10.5, "Español", "123-456", "Distopía", EstadoRecurso.DISPONIBLE);
        recursos.add(audioLibro);

        Revista revista = new Revista(EstadoRecurso.DISPONIBLE, 50, "2345-6789", "2023-05",
                "Ciencia", "Editorial XYZ", "National Geographic", "R001");
        recursos.add(revista);

        // Enviar algunas notificaciones de prueba
        servicioEnvioNotificaciones.enviarNotificacionSistema(
                "Bienvenido al sistema de biblioteca digital",
                usuario,
                Notificacion.TipoNotificacion.SISTEMA,
                "Sistema"
        );

        servicioEnvioNotificaciones.enviarNotificacionPrestamo(
                "Nuevo libro disponible: Cien años de soledad",
                usuario,
                Notificacion.TipoNotificacion.SISTEMA,
                libro,
                "P-TEST001"
        );

        servicioEnvioNotificaciones.enviarNotificacionReserva(
                "Recurso reservado: 1984",
                usuario,
                Notificacion.TipoNotificacion.RESERVA,
                audioLibro,
                "R-TEST001",
                1
        );
    }
}
