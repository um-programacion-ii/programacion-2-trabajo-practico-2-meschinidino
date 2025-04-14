package um.prog2.recursoDigital;

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
