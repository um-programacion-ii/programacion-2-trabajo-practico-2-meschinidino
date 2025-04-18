package um.prog2.recursoDigital;

import um.prog2.Enums.EstadoRecurso;
import um.prog2.interfaces.RecursoDigital;

public abstract class RecursoBase implements RecursoDigital {
    protected EstadoRecurso estado;

    @Override
    public EstadoRecurso getEstado() {
        return estado;
    }

    public void setEstado(EstadoRecurso estado) {
        this.estado = estado;
    }

    @Override
    public void actualizarEstado(EstadoRecurso estado) {
        this.estado = estado;
    }
}
