package um.prog2.usuario;

public class GestorUsuario {

    public static Usuario crearUsuario(String nombre, String apellido, int ID, String email, String telefono) {
        return new Usuario(nombre, apellido, ID, email, telefono);
    }

}
