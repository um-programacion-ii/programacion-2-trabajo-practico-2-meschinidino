package um.prog2.recursoDigital;

import um.prog2.Enums.CategoriaRecurso;
import um.prog2.Enums.EstadoRecurso;
import um.prog2.excepciones.RecursoNoDisponibleException;
import um.prog2.interfaces.Prestable;
import um.prog2.interfaces.RecursoDigital;
import um.prog2.interfaces.Renovable;
import um.prog2.interfaces.ServicioNotificaciones;
import um.prog2.usuario.Usuario;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que gestiona las operaciones relacionadas con los recursos digitales.
 * Implementa la lógica de negocio para crear, prestar y renovar recursos.
 */
public class GestorRecursos {
    private final List<RecursoDigital> recursos;
    private final ServicioNotificaciones servicioNotificaciones;

    /**
     * Constructor que inicializa el gestor de recursos.
     * 
     * @param recursos Lista de recursos a gestionar
     * @param servicioNotificaciones Servicio para enviar notificaciones a los usuarios
     */
    public GestorRecursos(List<RecursoDigital> recursos, ServicioNotificaciones servicioNotificaciones) {
        this.recursos = recursos;
        this.servicioNotificaciones = servicioNotificaciones;
    }

    /**
     * Crea un nuevo libro y lo añade a la lista de recursos.
     * 
     * @param id Identificador del libro
     * @param titulo Título del libro
     * @param autor Autor del libro
     * @param categoria Categoría del libro
     * @return El libro creado
     */
    public Libro crearLibro(String id, String titulo, String autor, CategoriaRecurso categoria) {
        Libro libro = new Libro(EstadoRecurso.DISPONIBLE, autor, titulo, id, categoria);
        recursos.add(libro);
        return libro;
    }

    /**
     * Crea un nuevo audiolibro y lo añade a la lista de recursos.
     * 
     * @param id Identificador del audiolibro
     * @param titulo Título del audiolibro
     * @param autor Autor del audiolibro
     * @param narrador Narrador del audiolibro
     * @param duracion Duración del audiolibro en horas
     * @param categoria Categoría del audiolibro
     * @return El audiolibro creado
     */
    public AudioLibro crearAudioLibro(String id, String titulo, String autor, String narrador, 
                                     double duracion, CategoriaRecurso categoria) {
        AudioLibro audioLibro = new AudioLibro(id, titulo, autor, narrador, duracion, 
                                              "Español", "", categoria, EstadoRecurso.DISPONIBLE);
        recursos.add(audioLibro);
        return audioLibro;
    }

    /**
     * Crea una nueva revista y la añade a la lista de recursos.
     * 
     * @param id Identificador de la revista
     * @param titulo Título de la revista
     * @param editorial Editorial de la revista
     * @param categoria Categoría de la revista
     * @return La revista creada
     */
    public Revista crearRevista(String id, String titulo, String editorial, CategoriaRecurso categoria) {
        Revista revista = new Revista(EstadoRecurso.DISPONIBLE, 0, "", "", 
                                     categoria, editorial, titulo, id);
        recursos.add(revista);
        return revista;
    }

    /**
     * Obtiene todos los recursos disponibles para préstamo.
     * 
     * @return Lista de recursos disponibles para préstamo
     */
    public List<RecursoDigital> obtenerRecursosDisponiblesParaPrestamo() {
        List<RecursoDigital> disponibles = new ArrayList<>();
        for (RecursoDigital r : recursos) {
            if (r instanceof Prestable && r.getEstado() == EstadoRecurso.DISPONIBLE) {
                disponibles.add(r);
            }
        }
        return disponibles;
    }

    /**
     * Obtiene todos los recursos que pueden ser renovados.
     * 
     * @return Lista de recursos que pueden ser renovados
     */
    public List<RecursoDigital> obtenerRecursosParaRenovar() {
        List<RecursoDigital> renovables = new ArrayList<>();
        for (RecursoDigital r : recursos) {
            if (r instanceof Renovable && r.getEstado() == EstadoRecurso.PRESTADO) {
                renovables.add(r);
            }
        }
        return renovables;
    }

    /**
     * Obtiene todos los recursos que pueden ser devueltos.
     * 
     * @return Lista de recursos que pueden ser devueltos
     */
    public List<RecursoDigital> obtenerRecursosParaDevolver() {
        List<RecursoDigital> prestados = new ArrayList<>();
        for (RecursoDigital r : recursos) {
            if (r instanceof Prestable && r.getEstado() == EstadoRecurso.PRESTADO) {
                prestados.add(r);
            }
        }
        return prestados;
    }

    /**
     * Presta un recurso a un usuario.
     * 
     * @param recurso Recurso a prestar
     * @param usuario Usuario al que se presta el recurso
     * @throws RecursoNoDisponibleException Si el recurso no está disponible para préstamo
     */
    public void prestarRecurso(RecursoDigital recurso, Usuario usuario) throws RecursoNoDisponibleException {
        if (recurso.getEstado() != EstadoRecurso.DISPONIBLE) {
            throw new RecursoNoDisponibleException("El recurso " + getTitulo(recurso) + " no está disponible para préstamo");
        }

        if (!(recurso instanceof Prestable)) {
            throw new RecursoNoDisponibleException("El recurso " + getTitulo(recurso) + " no es prestable");
        }

        Prestable prestable = (Prestable) recurso;
        prestable.prestar(usuario);

        servicioNotificaciones.enviarNotificacion(
                "Préstamo: " + recurso.getIdentificador() + " - " +
                        getTitulo(recurso) + ". Devolución: " + prestable.getFechaDevolucion(),
                usuario
        );
    }

    /**
     * Renueva un recurso prestado.
     * 
     * @param recurso Recurso a renovar
     * @param usuario Usuario que solicita la renovación
     * @throws RecursoNoDisponibleException Si el recurso no está prestado o no es renovable
     */
    public void renovarRecurso(RecursoDigital recurso, Usuario usuario) throws RecursoNoDisponibleException {
        if (recurso.getEstado() != EstadoRecurso.PRESTADO) {
            throw new RecursoNoDisponibleException("El recurso " + getTitulo(recurso) + " no está prestado y no puede ser renovado");
        }

        if (!(recurso instanceof Renovable)) {
            throw new RecursoNoDisponibleException("El recurso " + getTitulo(recurso) + " no es renovable");
        }

        Renovable renovable = (Renovable) recurso;
        renovable.renovar();

        servicioNotificaciones.enviarNotificacion(
                "Renovación: " + recurso.getIdentificador() + " - " +
                        getTitulo(recurso) + ". Nueva devolución: " + renovable.getFechaDevolucion(),
                usuario
        );
    }

    /**
     * Devuelve un recurso prestado.
     * 
     * @param recurso Recurso a devolver
     * @param usuario Usuario que devuelve el recurso
     * @throws RecursoNoDisponibleException Si el recurso no está prestado o no es prestable
     */
    public void devolverRecurso(RecursoDigital recurso, Usuario usuario) throws RecursoNoDisponibleException {
        if (recurso.getEstado() != EstadoRecurso.PRESTADO) {
            throw new RecursoNoDisponibleException("El recurso " + getTitulo(recurso) + " no está prestado y no puede ser devuelto");
        }

        if (!(recurso instanceof Prestable)) {
            throw new RecursoNoDisponibleException("El recurso " + getTitulo(recurso) + " no es prestable");
        }

        Prestable prestable = (Prestable) recurso;
        boolean devuelto = prestable.devolver();

        if (devuelto) {
            servicioNotificaciones.enviarNotificacion(
                    "Devolución: " + recurso.getIdentificador() + " - " + getTitulo(recurso),
                    usuario
            );
        } else {
            throw new RecursoNoDisponibleException("No se pudo devolver el recurso " + getTitulo(recurso));
        }
    }

    /**
     * Obtiene el título de un recurso.
     * 
     * @param r Recurso del que se quiere obtener el título
     * @return Título del recurso
     */
    public String getTitulo(RecursoDigital r) {
        if (r instanceof Libro) return ((Libro)r).getTitulo();
        if (r instanceof AudioLibro) return ((AudioLibro)r).getTitulo();
        if (r instanceof Revista) return ((Revista)r).getTitulo();
        return "";
    }
}
