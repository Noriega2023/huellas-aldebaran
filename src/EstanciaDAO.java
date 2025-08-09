import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EstanciaDAO {

    public static void insertar(Estancia e) {
        String sql = "INSERT INTO estancias (mascota_id, fecha_ingreso, fecha_salida, precio_dia, pagado) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, e.getMascotaId());
            stmt.setString(2, e.getFechaIngreso().toString());
            stmt.setString(3, e.getFechaSalida().toString());
            stmt.setDouble(4, e.getPrecioDia());
            stmt.setBoolean(5, e.isPagado());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) e.setId(rs.getInt(1));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static List<Estancia> listar() {
        List<Estancia> lista = new ArrayList<>();
        String sql = "SELECT * FROM estancias";
        try (Connection conn = DB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Estancia e = new Estancia(
                        rs.getInt("id"),
                        rs.getInt("mascota_id"),
                        LocalDate.parse(rs.getString("fecha_ingreso")),
                        LocalDate.parse(rs.getString("fecha_salida")),
                        rs.getDouble("precio_dia"),
                        rs.getBoolean("pagado")
                );
                lista.add(e);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    public static void actualizarPago(int id, boolean pagado) {
        String sql = "UPDATE estancias SET pagado = ? WHERE id = ?";
        try (Connection conn = DB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, pagado);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // <-- Este es el método que actualiza la fecha de salida de una estancia
    public static void actualizarFechaSalida(int id, LocalDate nuevaFecha) {
        String sql = "UPDATE estancias SET fecha_salida = ? WHERE id = ?";
        try (Connection conn = DB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nuevaFecha.toString());
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
