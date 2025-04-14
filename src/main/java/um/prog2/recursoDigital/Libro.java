package um.prog2.recursoDigital;

public class Libro extends RecursoBase {
    private String identificador;
    private String titulo;
    private String autor;



    public Libro(EstadoRecurso estado, String autor, String titulo, String identificador) {
        this.estado = estado;
        this.autor = autor;
        this.titulo = titulo;
        this.identificador = identificador;
    }

    public Libro() {
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    @Override
    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    @Override
    public String toString() {
        return "Libro{" +
                "identificador='" + identificador + '\'' +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", estado=" + estado +
                '}';
    }
}