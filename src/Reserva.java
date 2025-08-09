import java.time.LocalDate;

public class Reserva {
    private int id;
    private int mascotaId;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private double precioDia;
    private boolean pagado;

    public Reserva(int id, int mascotaId, LocalDate fechaEntrada, LocalDate fechaSalida, double precioDia, boolean pagado) {
        this.id = id;
        this.mascotaId = mascotaId;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.precioDia = precioDia;
        this.pagado = pagado;
    }

    // Constructor sin ID (para insertar)
    public Reserva(int mascotaId, LocalDate fechaEntrada, LocalDate fechaSalida, double precioDia, boolean pagado) {
        this(0, mascotaId, fechaEntrada, fechaSalida, precioDia, pagado);
    }

    public int getId() {
        return id;
    }

    public int getMascotaId() {
        return mascotaId;
    }

    public LocalDate getFechaEntrada() {
        return fechaEntrada;
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
}
