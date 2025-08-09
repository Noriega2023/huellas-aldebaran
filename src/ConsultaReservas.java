import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ConsultaReservas extends JDialog {
    private JTable tabla;
    private DefaultTableModel modelo;
    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy");

    public ConsultaReservas(JFrame parent) {
        super(parent, "Consultas de Reservas", true);
        setSize(1800, 1000);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));

        modelo = new DefaultTableModel(new Object[]{
                "ID", "Mascota", "Entrada", "Salida", "Días", "Total (€)", "Pagado"
        }, 0) {

            @Override public boolean isCellEditable(int r, int c) {
                return c == 6;
            }
            @Override public Class<?> getColumnClass(int c) {
                return switch (c) {
                    case 0 -> Integer.class;
                    case 4 -> Long.class;
                    case 5 -> Double.class;
                    case 6 -> Boolean.class;
                    default -> String.class;
                };
            }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(35);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        tabla.setSelectionBackground(new Color(200, 220, 255));
        tabla.setGridColor(new Color(220, 220, 220));

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(scroll, BorderLayout.CENTER);
        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < tabla.getColumnCount() -1; i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(centrado);
        }



        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        panelBotones.setBackground(new Color(245, 245, 245));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));

        JButton btnRefrescar = crearBoton("Refrescar", new Color(100, 100, 100));
        JButton btnEliminar = crearBoton("Eliminar", new Color(200, 50, 50));
        JButton btnAplicar = crearBoton("Guardar", new Color(50, 150, 50));
        JButton btnCerrar = crearBoton("Cerrar", new Color(70, 130, 180));

        btnRefrescar.addActionListener(e -> recargar());
        btnEliminar.addActionListener(e -> eliminarSeleccion());
        btnAplicar.addActionListener(e -> guardarPagado());
        btnCerrar.addActionListener(e -> dispose());

        panelBotones.add(btnRefrescar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnAplicar);
        panelBotones.add(btnCerrar);

        add(panelBotones, BorderLayout.SOUTH);
        recargar();
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void recargar() {
        LocalDate hoy = LocalDate.now();

        // Eliminar reservas pasadas
        List<Reserva> vencidas = ReservaDAO.listar().stream()
                .filter(r -> r.getFechaSalida().isBefore(hoy))
                .toList();
        vencidas.forEach(r -> ReservaDAO.eliminar(r.getId()));

        // Cargar y ordenar próximas
        modelo.setRowCount(0);
        ReservaDAO.listar().stream()
                .filter(r -> !r.getFechaEntrada().isBefore(hoy))
                .sorted((a, b) -> a.getFechaEntrada().compareTo(b.getFechaEntrada()))
                .forEach(r -> {
                    Mascota m = MascotaDAO.buscarPorId(r.getMascotaId());
                    long dias = ChronoUnit.DAYS.between(r.getFechaEntrada(), r.getFechaSalida()) + 1;
                    modelo.addRow(new Object[]{
                            r.getId(),
                            m != null ? m.getNombre() : "—",
                            FECHA_FMT.format(r.getFechaEntrada()),
                            FECHA_FMT.format(r.getFechaSalida()),
                            dias,
                            dias * r.getPrecioDia(),
                            r.isPagado()
                    });
                });
    }

    private void eliminarSeleccion() {
        int f = tabla.getSelectedRow();
        if (f < 0) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione una reserva para eliminar",
                    "¡Atención!",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (Integer) modelo.getValueAt(f, 0);
        int resp = JOptionPane.showConfirmDialog(this,
                "<html>¿Eliminar reserva ID <b>" + id + "</b>?</html>",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (resp == JOptionPane.YES_OPTION) {
            ReservaDAO.eliminar(id);
            recargar();
        }
    }

    private void guardarPagado() {
        for (int i = 0; i < modelo.getRowCount(); i++) {
            ReservaDAO.actualizarPago(
                    (Integer) modelo.getValueAt(i, 0),
                    (Boolean) modelo.getValueAt(i, 6)
            );
        }
        JOptionPane.showMessageDialog(this,
                "Estados de pago actualizados",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
        recargar();
    }
}
