public class Mascota {
    private int id;
    private String nombre, raza, observaciones;
    private double edad;
    private Double peso;
    private Dueno dueno;

    public Mascota(int id, String nombre, String raza, double edad, String observaciones, Dueno dueno, Double peso) {
        this.id = id;
        this.nombre = nombre;
        this.raza = raza;
        this.edad = edad;
        this.observaciones = observaciones;
        this.dueno = dueno;
        this.peso = peso;
    }


    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getRaza() { return raza; }
    public double getEdad() { return edad; }
    public Double getPeso() { return peso; }
    public String getObservaciones() { return observaciones; }
    public Dueno getDueno() { return dueno; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setRaza(String raza) { this.raza = raza; }
    public void setEdad(double edad) { this.edad = edad; }
    public void setPeso(Double peso) { this.peso = peso; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return nombre + " (" + raza + ")";
    }
}
