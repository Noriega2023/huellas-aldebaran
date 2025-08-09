import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class SalidaEstancia extends JDialog {

    private JComboBox<Estancia> comboEstancia;
    private DatePicker dateNuevaSalida;
    private JLabel labelImporte, infoEstancia;

    public SalidaEstancia(JFrame parent) {
        super(parent, "Gestión de Salidas", true);
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(240, 240, 240));

        // Panel principal
        JPanel panel = new JPanel(new GridLayout(5, 2, 15,15));
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        panel.setBackground(new Color(240,240,240));

        comboEstancia = new JComboBox<>(Datos.estancias.toArray(new Estancia[0]));
        comboEstancia.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        comboEstancia.addItemListener(e -> actualizarInfoEstancia());

        DatePickerSettings settings = new DatePickerSettings();
        settings.setAllowEmptyDates(false);
        settings.setFormatForDatesCommonEra("dd/MM/yyyy");

        dateNuevaSalida = new DatePicker(settings);
        dateNuevaSalida.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        dateNuevaSalida.setPreferredSize(new Dimension(250, 40));

        labelImporte = new JLabel("Total a pagar: €0.00");
        labelImporte.setFont(new Font("Segoe UI", Font.BOLD, 18));

        infoEstancia = new JLabel(" ");
        infoEstancia.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        infoEstancia.setVerticalAlignment(SwingConstants.TOP);

        panel.add(new JLabel("Mascota:", JLabel.RIGHT)); panel.add(comboEstancia);
        panel.add(new JLabel("Nueva fecha de salida:", JLabel.RIGHT)); panel.add(dateNuevaSalida);
        panel.add(new JLabel("Detalles:", JLabel.RIGHT)); panel.add(infoEstancia);
        panel.add(new JLabel("Importe:", JLabel.RIGHT)); panel.add(labelImporte);

        // Botones
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15,10));
        botones.setBackground(new Color(240,240,240));

        JButton btnSalida = crearBoton("Dar salida hoy", new Color(200,50,50));
        JButton btnActualizar = crearBoton("Actualizar fecha", new Color(70,130,180));
        JButton btnCalcular = crearBoton("Calcular importe", new Color(60,179,113));
        JButton btnCerrar = crearBoton("Cerrar", new Color(100,100,100));

        btnSalida.addActionListener(e -> darSalida());
        btnActualizar.addActionListener(e -> actualizarFecha());
        btnCalcular.addActionListener(e -> calcularImporte());
        btnCerrar.addActionListener(e -> dispose());

        botones.add(btnSalida);
        botones.add(btnActualizar);
        botones.add(btnCalcular);
        botones.add(btnCerrar);

        add(panel, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);

        actualizarInfoEstancia();
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(color);
            }
        });
        return btn;
    }

    private void actualizarInfoEstancia() {
        Estancia e = (Estancia) comboEstancia.getSelectedItem();
        if (e == null) {
            infoEstancia.setText(" ");
            labelImporte.setText("Total a pagar: €0.00");
            return;
        }
        Mascota m = MascotaDAO.buscarPorId(e.getMascotaId());
        if (m == null) return;

        long dias = ChronoUnit.DAYS.between(e.getFechaIngreso(), LocalDate.now()) + 1;
        double total = dias * e.getPrecioDia();
        labelImporte.setText(String.format("Total a pagar: €%.2f", total));

        infoEstancia.setText(String.format(
                "<html>Nombre: <b>%s</b><br>Dueño: %s<br>Ingreso: %s<br>Salida prevista: %s</html>",
                m.getNombre(), m.getDueno().getNombre(),
                e.getFechaIngreso(), e.getFechaSalida()
        ));
    }

    private void calcularImporte() {
        // recalc total usando fecha nueva
        Estancia e = (Estancia) comboEstancia.getSelectedItem();
        LocalDate nueva = dateNuevaSalida.getDate();
        if (e == null || nueva == null || nueva.isBefore(e.getFechaIngreso())) {
            return;
        }
        long dias = ChronoUnit.DAYS.between(e.getFechaIngreso(), nueva) + 1;
        labelImporte.setText(String.format("Total a pagar: €%.2f", dias * e.getPrecioDia()));
    }

    private void actualizarFecha() {
        Estancia e = (Estancia) comboEstancia.getSelectedItem();
        LocalDate nueva = dateNuevaSalida.getDate();
        if (e == null || nueva == null || nueva.isBefore(e.getFechaIngreso())) return;

// Actualiza en DB
        EstanciaDAO.actualizarFechaSalida(e.getId(), nueva);

// Reemplaza en memoria
        Estancia nuevaE = new Estancia(e.getId(), e.getMascotaId(),
                e.getFechaIngreso(), nueva, e.getPrecioDia(), e.isPagado());
        Datos.estancias.remove(e);
        Datos.estancias.add(nuevaE);

// Actualiza combo
        comboEstancia.removeItem(e);
        comboEstancia.addItem(nuevaE);
        comboEstancia.setSelectedItem(nuevaE);

// Refresca datos en UI
        actualizarInfoEstancia();
    }

        private void darSalida () {
            Estancia e = (Estancia) comboEstancia.getSelectedItem();
            if (e == null) return;

            LocalDate hoy = LocalDate.now();

// Marca pagado y actualiza fecha en BD
            EstanciaDAO.actualizarFechaSalida(e.getId(), hoy);
            EstanciaDAO.actualizarPago(e.getId(), true);

// Reemplaza instancia en memoria
            Estancia finalizada = new Estancia(e.getId(), e.getMascotaId(),
                    e.getFechaIngreso(), hoy, e.getPrecioDia(), true);
            Datos.estancias.remove(e);

// Elimina del combo – libera plaza
            comboEstancia.removeItem(e);

// Limpia detalles UI
            infoEstancia.setText(" ");
            labelImporte.setText("Total a pagar: €0.00");

    }
}
