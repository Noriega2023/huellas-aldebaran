import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GestionClientesMascotas extends JDialog {
    public GestionClientesMascotas(JFrame parent) {
        super(parent, "Clientes y sus Mascotas", true);
        setSize(1200, 800);
        setLocationRelativeTo(parent);

        JTextArea area = new JTextArea();
        area.setFont(new Font("Monospaced", Font.PLAIN, 20));
        area.setEditable(false);
        JScrollPane scroll = new JScrollPane(area);
        add(scroll);

        StringBuilder sb = new StringBuilder();
        for (Dueno d : DuenoDAO.listar()) {
            sb.append("🧑 Cliente: ").append(d.getNombre())
                    .append(" (").append(d.getTelefono()).append(")\n");
            List<Mascota> mascotas = MascotaDAO.listar(d.getId());
            if (mascotas.isEmpty()) {
                sb.append("   ➤ Sin mascotas registradas.\n");
            } else {
                for (Mascota m : mascotas) {
                    sb.append("   🐾 ").append(m.getNombre())
                            .append(" — ").append(m.getRaza())
                            .append("\n");
                }
            }
            sb.append("\n");
        }
        area.setText(sb.toString());
    }
}
