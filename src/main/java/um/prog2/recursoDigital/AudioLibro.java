package um.prog2.recursoDigital;

public class AudioLibro implements RecursoDigital {
    private String identificador;
    private String titulo;
    private String autor;
    private String narrador;
    private double duracion;
    private String idioma;
    private String isbn;
    private EstadoRecurso estado;

    public AudioLibro() {
    }

    public AudioLibro(String identificador, String titulo, String autor, String narrador,
                      double duracion, String idioma, String isbn,
                      EstadoRecurso estado) {
        this.identificador = identificador;
        this.titulo = titulo;
        this.autor = autor;
        this.narrador = narrador;
        this.duracion = duracion;
        this.idioma = idioma;
        this.isbn = isbn;
        this.estado = estado;
    }

    @Override
    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    @Override
    public EstadoRecurso getEstado() {
        return estado;
    }

    @Override
    public void actualizarEstado(EstadoRecurso estado) {
        this.estado = estado;
    }

    public void setEstado(EstadoRecurso estado) {
        this.estado = estado;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getNarrador() {
        return narrador;
    }

    public void setNarrador(String narrador) {
        this.narrador = narrador;
    }

    public double getDuracion() {
        return duracion;
    }

    public void setDuracion(double duracion) {
        this.duracion = duracion;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
}