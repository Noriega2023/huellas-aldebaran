import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ConsultaEstancias extends JDialog {
    private JTable tabla;
    private DefaultTableModel modelo;

    public ConsultaEstancias(JFrame parent) {
        super(parent, "Consulta de Estancias", true);
        setSize(900, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 240, 240));

        // Configuración de la tabla
        modelo = new DefaultTableModel(new Object[]{
                "ID", "Mascota", "Ingreso", "Salida", "Días", "Total (€)", "Pagado"
        }, 0) {
            @Override public boolean isCellEditable(int row, int col) {
                return col == 6;
            }
            @Override public Class<?> getColumnClass(int col) {
                return col == 6 ? Boolean.class : super.getColumnClass(col);
            }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(30);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabla.setSelectionBackground(new Color(200, 220, 255));
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        // Botones con estilo moderno
        JButton btnRefrescar = crearBoton("↻ Refrescar", new Color(70, 130, 180));
        btnRefrescar.addActionListener(e -> cargarDatos());

        JButton btnAplicar = crearBoton("✓ Aplicar cambios", new Color(50, 150, 50));
        btnAplicar.addActionListener(e -> aplicarCambios());

        JButton btnCerrar = crearBoton("✕ Cerrar", new Color(200, 50, 50));
        btnCerrar.addActionListener(e -> dispose());

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panelBotones.setBackground(new Color(240, 240, 240));
        panelBotones.add(btnRefrescar);
        panelBotones.add(btnAplicar);
        panelBotones.add(btnCerrar);

        add(scroll, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        cargarDatos();
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return btn;
    }

    private void cargarDatos() {
        modelo.setRowCount(0);
        List<Estancia> lista = EstanciaDAO.listar();
        for (Estancia e : lista) {
            Mascota m = MascotaDAO.buscarPorId(e.getMascotaId());
            long dias = ChronoUnit.DAYS.between(e.getFechaIngreso(), e.getFechaSalida()) + 1;
            double total = dias * e.getPrecioDia();
            modelo.addRow(new Object[]{
                    e.getId(),
                    m != null ? m.getNombre() : "Desconocido",
                    e.getFechaIngreso(),
                    e.getFechaSalida(),
                    dias,
                    String.format("€%.2f", total),
                    e.isPagado()
            });
        }
    }

    private void aplicarCambios() {
        for (int i = 0; i < modelo.getRowCount(); i++) {
            int id = (Integer) modelo.getValueAt(i, 0);
            boolean pagado = (Boolean) modelo.getValueAt(i, 6);
            EstanciaDAO.actualizarPago(id, pagado);
        }
        JOptionPane.showMessageDialog(this, "Cambios guardados correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
}