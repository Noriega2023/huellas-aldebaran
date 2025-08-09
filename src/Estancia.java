import java.time.LocalDate;

public class Estancia {
    private int id;
    private int mascotaId;
    private LocalDate fechaIngreso;
    private LocalDate fechaSalida;
    private double precioDia;
    private boolean pagado;

    public Estancia(int id, int mascotaId, LocalDate fechaIngreso, LocalDate fechaSalida, double precioDia, boolean pagado) {
        this.id = id;
        this.mascotaId = mascotaId;
        this.fechaIngreso = fechaIngreso;
        this.fechaSalida = fechaSalida;
        this.precioDia = precioDia;
        this.pagado = pagado;
    }

    // Constructor sin ID (para insertar)
    public Estancia(int mascotaId, LocalDate fechaIngreso, LocalDate fechaSalida, double precioDia, boolean pagado) {
        this(0, mascotaId, fechaIngreso, fechaSalida, precioDia, pagado);
    }

    public int getId() {
        return id;
    }

    public int getMascotaId() {
        return mascotaId;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public double getPrecioDia() {
        return precioDia;
    }

    public boolean isPagado() {
        return pagado;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPagado(boolean pagado) {
        this.pagado = pagado;
    }
    @Override
    public String toString() {
        Mascota m = MascotaDAO.buscarPorId(this.getMascotaId());
        return m != null ? m.getNombre() : "Desconocida";
    }

}
