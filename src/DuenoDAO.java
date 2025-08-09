import java.sql.*;
import java.util.ArrayList;
import java.util.List;



public class DuenoDAO {

    

    public static void insertar(Dueno dueno) {
        String sql = "INSERT INTO duenos (nombre, telefono, email) VALUES (?, ?, ?)";
        try (Connection conn = DB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, dueno.getNombre());
            stmt.setString(2, dueno.getTelefono());
            stmt.setString(3, dueno.getEmail() == null ? null : dueno.getEmail());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) dueno.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Dueno> listar() {
        List<Dueno> lista = new ArrayList<>();
        String sql = "SELECT * FROM duenos ORDER BY nombre";
        try (Connection conn = DB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Dueno(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("email")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static void actualizar(Dueno d) {
        String sql = "UPDATE duenos SET nombre=?, telefono=?, email=? WHERE id=?";
        try (Connection conn = DB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, d.getNombre());
            stmt.setString(2, d.getTelefono());
            stmt.setString(3, d.getEmail());
            stmt.setInt(4, d.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void eliminar(int id) {
        String sql = "DELETE FROM duenos WHERE id = ?";
        try (Connection conn = DB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

       /**
     * Busca un dueño por su identificador directamente en la base de datos.
     *
     * Este método evita cargar todos los dueños en memoria y realiza una
     * consulta parametrizada para encontrar el registro deseado. Devuelve
     * {@code null} si no existe ningún dueño con el ID especificado.
     *
     * @param id Identificador del dueño a buscar.
     * @return Una instancia de {@link Dueno} o {@code null} si no se encuentra.
     */
    public static Dueno buscarPorId(int id) {
        String sql = "SELECT * FROM duenos WHERE id = ?";
        try (Connection conn = DB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Dueno(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("telefono"),
                            rs.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
