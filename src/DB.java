import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    private static final String URL = "jdbc:sqlite:residencia.db";

    public static Connection conectar() throws SQLException {
        String ruta = new java.io.File("residencia.db").getAbsolutePath();
        return DriverManager.getConnection("jdbc:sqlite:" + ruta);
    }
}
