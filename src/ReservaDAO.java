import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {

    public static void insertar(Reserva r) {
        String sql = "INSERT INTO reservas (mascota_id, fecha_entrada, fecha_salida, precio_dia, pagado) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, r.getMascotaId());
            stmt.setString(2, r.getFechaEntrada().toString());
            stmt.setString(3, r.getFechaSalida().toString());
            stmt.setDouble(4, r.getPrecioDia());
            stmt.setBoolean(5, r.isPagado());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) r.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void actualizarPago(int id, boolean pagado) {
        String sql = "UPDATE reservas SET pagado = ? WHERE id = ?";
        try (Connection conn = DB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, pagado);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void eliminar(int id) {
        String sql = "DELETE FROM reservas WHERE id=?";
        try (Connection conn = DB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Reserva> listar() {
        List<Reserva> lista = new ArrayList<>();
        String sql = "SELECT * FROM reservas";
        try (Connection conn = DB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Reserva r = new Reserva(
                        rs.getInt("id"),
                        rs.getInt("mascota_id"),
                        LocalDate.parse(rs.getString("fecha_entrada")),
                        LocalDate.parse(rs.getString("fecha_salida")),
                        rs.getDouble("precio_dia"),
                        rs.getBoolean("pagado")
                );
                lista.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
