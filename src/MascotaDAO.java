import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MascotaDAO {

    public static void insertar(Mascota m) {
        String sql = "INSERT INTO mascotas (nombre, raza, edad, peso, observaciones, dueno_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, m.getNombre());
            stmt.setString(2, m.getRaza());
            stmt.setDouble(3, m.getEdad());
            if (m.getPeso() != null) stmt.setDouble(4, m.getPeso());
            else stmt.setNull(4, Types.REAL);
            stmt.setString(5, m.getObservaciones());
            stmt.setInt(6, m.getDueno().getId());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) m.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Mascota> listar(int duenoId) {
        List<Mascota> res = new ArrayList<>();
        String sql = "SELECT * FROM mascotas WHERE dueno_id = ?";
        try (Connection conn = DB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, duenoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    res.add(new Mascota(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("raza"),
                            rs.getDouble("edad"),
                            rs.getString("observaciones"),
                            DuenoDAO.buscarPorId(rs.getInt("dueno_id")),
                            rs.getObject("peso") != null ? rs.getDouble("peso") : null
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

      /**
     * Busca una mascota por su identificador directamente en la base de datos.
     *
     * Este método ejecuta una consulta SQL para obtener una única mascota y así
     * evita iterar sobre todas las mascotas cargadas. Si no se encuentra la
     * mascota solicitada, devuelve {@code null}.
     *
     * @param id Identificador de la mascota a buscar.
     * @return Instancia de {@link Mascota} o {@code null} si no existe.
     */
    public static Mascota buscarPorId(int id) {
        String sql = "SELECT * FROM mascotas WHERE id = ?";
        try (Connection conn = DB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Dueno dueno = DuenoDAO.buscarPorId(rs.getInt("dueno_id"));
                    return new Mascota(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("raza"),
                            rs.getDouble("edad"),
                            rs.getString("observaciones"),
                            dueno,
                            rs.getObject("peso") != null ? rs.getDouble("peso") : null
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    }

    public static List<Mascota> listarTodos() {
        List<Mascota> todas = new ArrayList<>();
        for (Dueno d : DuenoDAO.listar()) {
            todas.addAll(MascotaDAO.listar(d.getId()));
        }
        return todas;
    }

    public static void actualizar(Mascota m) {
        String sql = "UPDATE mascotas SET nombre=?, raza=?, edad=?, peso=?, observaciones=?, dueno_id=? WHERE id=?";
        try (Connection conn = DB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, m.getNombre());
            stmt.setString(2, m.getRaza());
            stmt.setDouble(3, m.getEdad());
            if (m.getPeso() != null) stmt.setDouble(4, m.getPeso());
            else stmt.setNull(4, Types.REAL);
            stmt.setString(5, m.getObservaciones());
            stmt.setInt(6, m.getDueno().getId());
            stmt.setInt(7, m.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void eliminar(int id) {
        String sql = "DELETE FROM mascotas WHERE id = ?";
        try (Connection conn = DB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
