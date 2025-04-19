package um.prog2.cliente.reservas;

import um.prog2.interfaces.RecursoDigital;
import um.prog2.interfaces.ServicioNotificaciones;
import um.prog2.prestamos.SistemaPrestamos;
import um.prog2.reservas.Reserva;
import um.prog2.reservas.SistemaReservas;
import um.prog2.usuario.Usuario;

import java.util.List;
import java.util.Scanner;

/**
 * Clase que gestiona las operaciones de reservas desde la consola.
 */
public class GestorReservasConsola {
    private final SistemaReservas sistemaReservas;
    private final Scanner scanner;

    /**
     * Constructor del gestor de reservas por consola.
     * 
     * @param servicioNotificaciones Servicio para enviar notificaciones a los usuarios
     * @param sistemaPrestamos Sistema de préstamos para convertir reservas en préstamos
     */
    public GestorReservasConsola(ServicioNotificaciones servicioNotificaciones, SistemaPrestamos sistemaPrestamos) {
        this.sistemaReservas = new SistemaReservas(servicioNotificaciones, sistemaPrestamos);
        this.scanner = new Scanner(System.in);
    }

    /**
     * Muestra el menú de reservas y procesa la opción seleccionada.
     * 
     * @param usuario Usuario actual
     * @return true si se debe volver al menú principal, false en caso contrario
     */
    public boolean mostrarMenuReservas(Usuario usuario) {
        System.out.println("\n===== MENÚ DE RESERVAS =====");
        System.out.println("1. Realizar una reserva");
        System.out.println("2. Cancelar una reserva");
        System.out.println("3. Convertir reserva a préstamo");
        System.out.println("4. Ver mis reservas activas");
        System.out.println("5. Ver mi historial de reservas");
        System.out.println("6. Ver todas las reservas (administrador)");
        System.out.println("0. Volver al menú principal");
        System.out.print("Seleccione una opción: ");

        int opcion = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea

        switch (opcion) {
            case 1:
                realizarReserva(usuario);
                return false;
            case 2:
                cancelarReserva(usuario);
                return false;
            case 3:
                convertirReservaAPrestamo(usuario);
                return false;
            case 4:
                mostrarReservasActivas(usuario);
                return false;
            case 5:
                mostrarHistorialReservas(usuario);
                return false;
            case 6:
                mostrarTodasLasReservas();
                return false;
            case 0:
                return true;
            default:
                System.out.println("Opción no válida. Intente nuevamente.");
                return false;
        }
    }

    /**
     * Solicita los datos para realizar una reserva.
     * 
     * @param usuario Usuario que realiza la reserva
     */
    private void realizarReserva(Usuario usuario) {
        System.out.println("\n===== REALIZAR RESERVA =====");
        System.out.print("Ingrese el identificador del recurso a reservar: ");
        String identificadorRecurso = scanner.nextLine();

        // Aquí se debería buscar el recurso en el sistema
        // Por simplicidad, asumimos que tenemos acceso al recurso
        RecursoDigital recurso = null; // Esto debería ser reemplazado por la búsqueda real

        if (recurso == null) {
            System.out.println("El recurso no existe o no está disponible para reserva.");
            return;
        }

        System.out.print("Ingrese la prioridad de la reserva (1-10, donde 10 es la máxima prioridad): ");
        int prioridad = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea

        if (prioridad < 1 || prioridad > 10) {
            System.out.println("La prioridad debe estar entre 1 y 10.");
            return;
        }

        try {
            sistemaReservas.solicitarReserva(usuario, recurso, prioridad);
            System.out.println("Solicitud de reserva enviada correctamente.");
        } catch (Exception e) {
            System.out.println("Error al realizar la reserva: " + e.getMessage());
        }
    }

    /**
     * Solicita los datos para cancelar una reserva.
     * 
     * @param usuario Usuario que cancela la reserva
     */
    private void cancelarReserva(Usuario usuario) {
        System.out.println("\n===== CANCELAR RESERVA =====");

        List<Reserva> reservasActivas = sistemaReservas.obtenerReservasActivas(usuario);
        if (reservasActivas.isEmpty()) {
            System.out.println("No tiene reservas activas para cancelar.");
            return;
        }

        System.out.println("Sus reservas activas:");
        for (int i = 0; i < reservasActivas.size(); i++) {
            Reserva reserva = reservasActivas.get(i);
            System.out.println((i + 1) + ". " + reserva.toString());
        }

        System.out.print("Seleccione el número de la reserva a cancelar (0 para cancelar): ");
        int seleccion = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea

        if (seleccion == 0) {
            System.out.println("Operación cancelada.");
            return;
        }

        if (seleccion < 1 || seleccion > reservasActivas.size()) {
            System.out.println("Selección no válida.");
            return;
        }

        Reserva reservaSeleccionada = reservasActivas.get(seleccion - 1);
        try {
            sistemaReservas.solicitarCancelacion(reservaSeleccionada.getId(), usuario);
            System.out.println("Solicitud de cancelación enviada correctamente.");
        } catch (Exception e) {
            System.out.println("Error al cancelar la reserva: " + e.getMessage());
        }
    }

    /**
     * Solicita los datos para convertir una reserva a préstamo.
     * 
     * @param usuario Usuario que convierte la reserva
     */
    private void convertirReservaAPrestamo(Usuario usuario) {
        System.out.println("\n===== CONVERTIR RESERVA A PRÉSTAMO =====");

        List<Reserva> reservasActivas = sistemaReservas.obtenerReservasActivas(usuario);
        if (reservasActivas.isEmpty()) {
            System.out.println("No tiene reservas activas para convertir a préstamo.");
            return;
        }

        System.out.println("Sus reservas activas:");
        for (int i = 0; i < reservasActivas.size(); i++) {
            Reserva reserva = reservasActivas.get(i);
            System.out.println((i + 1) + ". " + reserva.toString());
        }

        System.out.print("Seleccione el número de la reserva a convertir (0 para cancelar): ");
        int seleccion = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea

        if (seleccion == 0) {
            System.out.println("Operación cancelada.");
            return;
        }

        if (seleccion < 1 || seleccion > reservasActivas.size()) {
            System.out.println("Selección no válida.");
            return;
        }

        System.out.print("Ingrese la duración del préstamo en días: ");
        int diasPrestamo = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea

        if (diasPrestamo <= 0) {
            System.out.println("La duración del préstamo debe ser mayor a 0.");
            return;
        }

        Reserva reservaSeleccionada = reservasActivas.get(seleccion - 1);
        try {
            sistemaReservas.solicitarConversionAPrestamo(reservaSeleccionada.getId(), usuario, diasPrestamo);
            System.out.println("Solicitud de conversión a préstamo enviada correctamente.");
        } catch (Exception e) {
            System.out.println("Error al convertir la reserva a préstamo: " + e.getMessage());
        }
    }

    /**
     * Muestra las reservas activas del usuario.
     * 
     * @param usuario Usuario del que se muestran las reservas
     */
    private void mostrarReservasActivas(Usuario usuario) {
        System.out.println("\n===== MIS RESERVAS ACTIVAS =====");

        List<Reserva> reservasActivas = sistemaReservas.obtenerReservasActivas(usuario);
        if (reservasActivas.isEmpty()) {
            System.out.println("No tiene reservas activas.");
            return;
        }

        for (Reserva reserva : reservasActivas) {
            System.out.println(reserva.toString());
        }
    }

    /**
     * Muestra el historial de reservas del usuario.
     * 
     * @param usuario Usuario del que se muestra el historial
     */
    private void mostrarHistorialReservas(Usuario usuario) {
        System.out.println("\n===== MI HISTORIAL DE RESERVAS =====");

        List<Reserva> historialReservas = sistemaReservas.obtenerHistorialReservas(usuario);
        if (historialReservas.isEmpty()) {
            System.out.println("No tiene historial de reservas.");
            return;
        }

        for (Reserva reserva : historialReservas) {
            System.out.println(reserva.toString());
        }
    }

    /**
     * Muestra todas las reservas activas (solo para administradores).
     */
    private void mostrarTodasLasReservas() {
        System.out.println("\n===== TODAS LAS RESERVAS ACTIVAS =====");

        List<Reserva> todasLasReservas = sistemaReservas.obtenerTodasLasReservasActivas();
        if (todasLasReservas.isEmpty()) {
            System.out.println("No hay reservas activas en el sistema.");
            return;
        }

        for (Reserva reserva : todasLasReservas) {
            System.out.println(reserva.toString());
        }
    }

    /**
     * Cierra el gestor de reservas.
     */
    public void cerrar() {
        sistemaReservas.cerrar();
    }
}
