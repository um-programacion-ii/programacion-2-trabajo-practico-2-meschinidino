package um.prog2.interfaces;

import um.prog2.Enums.EstadoRecurso;

public interface RecursoDigital {
    String getIdentificador();
    EstadoRecurso getEstado();
    void actualizarEstado(EstadoRecurso estado);
}
