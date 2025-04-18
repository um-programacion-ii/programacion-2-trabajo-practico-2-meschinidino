package um.prog2.cliente.utilsUsuariosCLI;

import um.prog2.interfaces.ServicioNotificaciones;
import um.prog2.notificaciones.*;
import um.prog2.usuario.GestorUsuario;
import um.prog2.usuario.Usuario;

import java.util.Map;
import java.util.Scanner;

public class GestorUsuarioConsola {
    private final Scanner scanner;
    private final Map<String, Usuario> usuarios;
    private ServicioNotificaciones servicioNotificaciones;

    public GestorUsuarioConsola(Scanner scanner, Map<String, Usuario> usuarios, ServicioNotificaciones servicioNotificaciones) {
        this.scanner = scanner;
        this.usuarios = usuarios;
        this.servicioNotificaciones = servicioNotificaciones;
    }

    public Usuario crearUsuario() {
        System.out.println("\n-- CREAR USUARIO --");

        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();

        System.out.print("Apellido: ");
        String apellido = scanner.nextLine();

        int id;
        try {
            System.out.print("ID: ");
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Error: El ID debe ser un número");
            return null;
        }

        System.out.print("Email: ");
        String email = scanner.nextLine();

        int telefono;
        try {
            System.out.print("Teléfono: ");
            telefono = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Error: El teléfono debe ser un número");
            return null;
        }

        Usuario nuevoUsuario = GestorUsuario.crearUsuario(nombre, apellido, id, email, telefono);
        usuarios.put(String.valueOf(id), nuevoUsuario);

        System.out.print("Método de notificación (1: Email, 2: SMS): ");
        String metodo = scanner.nextLine();
        servicioNotificaciones = "1".equals(metodo) ?
                new ServicioNotificacionesEmail() :
                new ServicioNotificacionesSMS();

        servicioNotificaciones.enviarNotificacion("Bienvenido a la biblioteca digital", nuevoUsuario);
        System.out.println("Usuario creado exitosamente");

        return nuevoUsuario;
    }

    public Usuario seleccionarUsuario() {
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados");
            return null;
        }

        System.out.println("\n-- USUARIOS DISPONIBLES --");
        usuarios.values().forEach(u ->
                System.out.println(u.getID() + " - " + u.getNombre() + " " + u.getApellido()));

        System.out.print("ID del usuario a seleccionar (o vacío para cancelar): ");
        String id = scanner.nextLine();

        if (!id.isEmpty() && usuarios.containsKey(id)) {
            Usuario seleccionado = usuarios.get(id);
            System.out.println("Usuario seleccionado: " + seleccionado.getNombre() + " " + seleccionado.getApellido());
            return seleccionado;
        }

        System.out.println("No se seleccionó ningún usuario");
        return null;
    }

    public ServicioNotificaciones getServicioNotificaciones() {
        return servicioNotificaciones;
    }
}