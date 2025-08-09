import java.io.*;
import java.util.List;

public class Persistencia {

    private static final String ARCHIVO_ESTANCIAS = "estancias.dat";
    private static final String ARCHIVO_PLAZAS = "plazas.dat";

    public static void guardarTodo() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ARCHIVO_ESTANCIAS))) {
            out.writeObject(Datos.estancias);
        } catch (IOException e) {
            System.out.println("Error guardando estancias: " + e.getMessage());
        }

        try (FileWriter writer = new FileWriter(ARCHIVO_PLAZAS)) {
            writer.write(String.valueOf(Datos.plazasDisponibles));
        } catch (IOException e) {
            System.out.println("Error guardando plazas: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static void cargarTodo() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(ARCHIVO_ESTANCIAS))) {
            Datos.estancias = (List<Estancia>) in.readObject();
        } catch (Exception e) {
            Datos.estancias.clear(); // Si falla, iniciamos vacío
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_PLAZAS))) {
            Datos.plazasDisponibles = Integer.parseInt(reader.readLine());
        } catch (Exception e) {
            Datos.plazasDisponibles = 30; // Por defecto
        }
    }
}
