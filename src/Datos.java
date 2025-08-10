import java.util.ArrayList;
import java.util.List;

public class Datos {
    public static List<Dueno> duenos = new ArrayList<>();
    public static List<Mascota> mascotas = new ArrayList<>();
    public static List<Reserva> reservas = new ArrayList<>();
    public static List<Estancia> estancias = new ArrayList<>();
    public static int plazasDisponibles = 30;

    /**
     * Carga la capacidad de estancias disponible desde un archivo en disco.
     * Si el archivo no existe o el contenido no es válido, se mantiene
     * el valor por defecto definido en la variable plazasDisponibles.
     */
    public static void cargarCapacidad() {
        java.io.File fichero = new java.io.File("plazas.txt");
        if (!fichero.exists()) {
            return;
        }
        try (java.util.Scanner sc = new java.util.Scanner(fichero)) {
            if (sc.hasNextInt()) {
                plazasDisponibles = sc.nextInt();
            }
        } catch (Exception e) {
            // En caso de error, mantener el valor por defecto
            e.printStackTrace();
        }
    }

    /**
     * Guarda la capacidad de estancias disponible en un archivo en disco.
     * Este método se invoca cada vez que el usuario modifica la capacidad
     * disponible, para que persista entre ejecuciones del programa.
     */
    public static void guardarCapacidad() {
        try (java.io.PrintWriter out = new java.io.PrintWriter("plazas.txt")) {
            out.println(plazasDisponibles);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

