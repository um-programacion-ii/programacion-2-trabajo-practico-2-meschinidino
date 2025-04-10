package um.prog2.cliente;

import um.prog2.usuario.GestorUsuario;
import um.prog2.usuario.Usuario;

import java.util.Scanner;

public class CLI {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Usuario usuario = null;

        while (true) {
            System.out.println("Bienvenido a la aplicación de gestión de recursos digitales.");
            System.out.println("Por favor, seleccione una opción:");
            System.out.println("1. Crear usuario");
            System.out.println("2. Ver usuarios");
            System.out.println("3. Salir");
            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    System.out.println("Ingrese el nombre del usuario:");
                    String nombre = scanner.nextLine();
                    System.out.println("Ingrese el apellido del usuario:");
                    String apellido = scanner.nextLine();
                    System.out.println("Ingrese el ID del usuario:");
                    int ID = Integer.parseInt(scanner.nextLine());
                    System.out.println("Ingrese el email del usuario:");
                    String email = scanner.nextLine();

                    // Crear el usuario
                    usuario = GestorUsuario.crearUsuario(nombre, apellido, ID, email);
                    System.out.println("Usuario creado: " + usuario);
                    break;

                case "2":
                    if (usuario == null) {
                        System.out.println("No hay ningún usuario registrado.");
                    } else {
                        System.out.println("Usuario registrado: " + usuario);
                    }
                    break;

                case "3":
                    System.out.println("Saliendo de la aplicación.");
                    break;

                default:
                    System.out.println("Opción no válida. Por favor, intente de nuevo.");
            }

            if ("3".equals(opcion)) {
                break;
            }
        }

        scanner.close();
    }
}