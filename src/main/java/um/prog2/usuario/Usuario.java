package um.prog2.usuario;

public class Usuario {
    private String nombre;
    private String apellido;
    private String ID;
    private String email;

    public Usuario(String nombre, String apellido, String ID, String email) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.ID = ID;
        this.email = email;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getApellido() {
        return apellido;
    }
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    public String getID() {
        return ID;
    }
    public void setID(String ID) {
        this.ID = ID;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    @Override
    public String toString() {
        return "Usuario{" +
                "nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", ID='" + ID + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

}
