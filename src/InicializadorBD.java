import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class InicializadorBD {
    public static void crearTablas() {
        try (Connection conn = DB.conectar();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS duenos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    telefono TEXT NOT NULL,
                    email TEXT
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS mascotas (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    raza TEXT NOT NULL,
                    edad REAL NOT NULL,
                    peso REAL,
                    observaciones TEXT,
                    dueno_id INTEGER NOT NULL,
                    FOREIGN KEY(dueno_id) REFERENCES duenos(id) ON DELETE CASCADE
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS reservas (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    mascota_id INTEGER NOT NULL,
                    fecha_entrada TEXT NOT NULL,
                    fecha_salida TEXT NOT NULL,
                    precio_dia REAL NOT NULL,
                    pagado INTEGER NOT NULL,
                    FOREIGN KEY(mascota_id) REFERENCES mascotas(id) ON DELETE CASCADE
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS estancias (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    mascota_id INTEGER NOT NULL,
                    fecha_ingreso TEXT NOT NULL,
                    fecha_salida TEXT NOT NULL,
                    precio_dia REAL NOT NULL,
                    pagado INTEGER NOT NULL,
                    FOREIGN KEY(mascota_id) REFERENCES mascotas(id) ON DELETE CASCADE
                );
            """);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
