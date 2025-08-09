import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.sql.*;
import java.util.Properties;

public class SQLiteBackupHelper {
    private static final String[] DIAS_SEMANA = {
            "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo"
    };
    private static final String BACKUP_DIR = "backups";
    private static final String CONFIG_FILE = "backup_config.properties";

    public static boolean hacerBackupDiario(String dbPath) {
        try {
            // Cargar configuración previa
            Properties props = cargarConfiguracion();
            String ultimaFechaStr = props.getProperty("ultima_fecha", "");
            LocalDate ultimaFecha = ultimaFechaStr.isEmpty() ? null : LocalDate.parse(ultimaFechaStr);

            LocalDate hoy = LocalDate.now();

            // Verificar si ya se hizo backup hoy
            if (hoy.equals(ultimaFecha)) {
                return false;
            }

            // Crear directorio si no existe
            Files.createDirectories(Paths.get(BACKUP_DIR));

            // Obtener día de la semana (0=Lunes, 6=Domingo)
            int diaSemana = hoy.getDayOfWeek().getValue() - 1;
            String nombreArchivo = "backup_" + DIAS_SEMANA[diaSemana] + ".db";
            Path destino = Paths.get(BACKUP_DIR, nombreArchivo);

            // Realizar backup nativo de SQLite
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("backup to '" + destino.toString() + "'");
            stmt.close();
            conn.close();

            // Actualizar y guardar configuración
            props.setProperty("ultima_fecha", hoy.toString());
            guardarConfiguracion(props);

            return true;

        } catch (Exception e) {
            System.err.println("Error al realizar backup: " + e.getMessage());
            return false;
        }
    }

    private static Properties cargarConfiguracion() throws IOException {
        Properties props = new Properties();
        Path configPath = Paths.get(CONFIG_FILE);

        if (Files.exists(configPath)) {
            try (InputStream is = Files.newInputStream(configPath)) {
                props.load(is);
            }
        }

        return props;
    }

    private static void guardarConfiguracion(Properties props) throws IOException {
        try (OutputStream os = Files.newOutputStream(Paths.get(CONFIG_FILE))) {
            props.store(os, "Configuración de backups");
        }
    }

    public static String obtenerNombreDiaActual() {
        return DIAS_SEMANA[LocalDate.now().getDayOfWeek().getValue() - 1];
    }
}