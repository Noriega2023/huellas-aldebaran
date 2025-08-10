import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class FormularioReserva extends JDialog {
    private JTextField buscarMascota;
    private JComboBox<Mascota> comboMascota;
    private JLabel lblDueno;
    private DatePicker dateEntrada, dateSalida;
    private JTextField campoPrecio;
    private JLabel etiquetaTotal;
    private JButton btnLimpiar, btnGuardar;
    private List<Mascota> todasMascotas;

    public FormularioReserva(JFrame parent) {
        super(parent, "Reserva de Estancia", true);
        setSize(850, 450);
        setLocationRelativeTo(parent);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(245, 245, 245));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 20);
        Font fontField = new Font("Segoe UI", Font.PLAIN, 20);

        // Fila 0: buscador mascotas
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Buscar mascota:"), gbc);
        buscarMascota = new JTextField();
        buscarMascota.setFont(fontField);
        add(buscarMascota, gbc = nextCell(gbc));

        // Fila 1: combo + dueño
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Mascota:"), gbc);
        comboMascota = new JComboBox<>();
        comboMascota.setFont(fontField);
        add(comboMascota, gbc = nextCell(gbc));
        gbc.gridx = 2;
        lblDueno = new JLabel("Dueño: ");
        lblDueno.setFont(fontLabel);
        add(lblDueno, gbc);

        buscarMascota.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrarMascotas(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrarMascotas(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrarMascotas(); }
        });
        comboMascota.addActionListener(e -> mostrarDueno());

        cargarTodasMascotas();

        // Fecha entrada
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Fecha entrada:"), gbc);
        dateEntrada = createDatePicker();
        add(dateEntrada, gbc = nextCell(gbc));

        // Fecha salida
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Fecha salida:"), gbc);
        dateSalida = createDatePicker();
        add(dateSalida, gbc = nextCell(gbc));

        // Precio
        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Precio por día (€):"), gbc);
        campoPrecio = new JTextField("0.00");
        campoPrecio.setFont(fontField);
        campoPrecio.setForeground(Color.GRAY);
        aplicarPlaceholder(campoPrecio, "0.00");
        add(campoPrecio, gbc = nextCell(gbc));

        // Total
        gbc.gridy++;
        gbc.gridx = 0; gbc.gridwidth = 3;
        etiquetaTotal = new JLabel("Total: €0.00", SwingConstants.CENTER);
        etiquetaTotal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(etiquetaTotal, gbc);
        gbc.gridwidth = 1;

        // Botones
        btnLimpiar = crearBoton("Limpiar", Color.GRAY);
        btnGuardar  = crearBoton("Registrar Reserva", new Color(50, 150, 50));
        JPanel pBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        pBotones.setBackground(getContentPane().getBackground());
        pBotones.add(btnLimpiar);
        pBotones.add(btnGuardar);
        gbc.gridy++;
        gbc.gridx = 1; gbc.gridwidth = 2;
        add(pBotones, gbc);

        // Listeners
        dateEntrada.addDateChangeListener(e -> actualizarTotal());
        dateSalida.addDateChangeListener(e -> actualizarTotal());
        campoPrecio.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { actualizarTotal(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { actualizarTotal(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { actualizarTotal(); }
        });
        btnLimpiar.addActionListener(e -> limpiarCampos());
        btnGuardar.addActionListener(e -> guardarReserva());
                pack();
setMinimumSize(getPreferredSize());
    }

    private GridBagConstraints nextCell(GridBagConstraints gbc) {
        GridBagConstraints n = (GridBagConstraints) gbc.clone();
        n.gridx++;
        return n;
    }

    private DatePicker createDatePicker() {
        DatePickerSettings s = new DatePickerSettings();
        s.setAllowEmptyDates(false);
        s.setFormatForDatesCommonEra("dd/MM/yyyy");

        // Configuración de tamaño aumentado
             s.setFontCalendarDateLabels(new Font("Segoe UI", Font.PLAIN, 20));
        s.setFontValidDate(new Font("Segoe UI", Font.PLAIN, 20));
        s.setSizeTextFieldMinimumWidth(Integer.valueOf(150)); // Ancho mínimo del campo de texto

        DatePicker picker = new DatePicker(s);

        // Aumentar tamaño del botón del calendario
        picker.getComponentToggleCalendarButton().setPreferredSize(new Dimension(40, 40));
        picker.getComponentToggleCalendarButton().setFont(new Font("Segoe UI", Font.PLAIN, 20));

        // Aumentar tamaño del panel principal
        picker.setPreferredSize(new Dimension(250, 45));

        return picker;
    }

    private void cargarTodasMascotas() {
        todasMascotas = MascotaDAO.listarTodos();
        if (todasMascotas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay mascotas registradas.");
            dispose();
            return;
        }
        filtrarMascotas();
    }

    private void filtrarMascotas() {
        String texto = buscarMascota.getText().trim().toLowerCase();
        comboMascota.removeAllItems();
        todasMascotas.stream()
                .filter(m -> m.getNombre().toLowerCase().contains(texto))
                .forEach(comboMascota::addItem);
        if (comboMascota.getItemCount()>0) {
            comboMascota.setSelectedIndex(0);
            mostrarDueno();
        } else {
            lblDueno.setText("Dueño: -");
        }
    }

    private void mostrarDueno() {
        Mascota m = (Mascota) comboMascota.getSelectedItem();
        lblDueno.setText("Dueño: " + (m != null && m.getDueno()!=null ? m.getDueno().getNombre() : "-"));
    }

    private JButton crearBoton(String texto, Color bg) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(180, 45));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(bg.brighter()); }
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    private void aplicarPlaceholder(JTextField campo, String texto) {
        campo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (campo.getText().equals(texto)) {
                    campo.setText("");
                    campo.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (campo.getText().isEmpty()) {
                    campo.setText(texto);
                    campo.setForeground(Color.GRAY);
                }
            }
        });
    }

    private void limpiarCampos() {
        buscarMascota.setText("");
        cargarTodasMascotas();
        dateEntrada.clear();
        dateSalida.clear();
        campoPrecio.setText("0.00");
        campoPrecio.setForeground(Color.GRAY);
        etiquetaTotal.setText("Total: €0.00");
    }

    private void actualizarTotal() {
        try {
            LocalDate ent = dateEntrada.getDate();
            LocalDate sal = dateSalida.getDate();
            double precio = Double.parseDouble(campoPrecio.getText());
            if (ent != null && sal != null && !sal.isBefore(ent)) {
                long dias = ChronoUnit.DAYS.between(ent, sal) + 1;
                etiquetaTotal.setText("Total: €" + String.format("%.2f", dias * precio));
            } else {
                etiquetaTotal.setText("Total: €0.00");
            }
        } catch (Exception e) {
            etiquetaTotal.setText("Total: €0.00");
        }
    }

    private void guardarReserva() {
        try {
            Mascota m = (Mascota) comboMascota.getSelectedItem();
            LocalDate ent = dateEntrada.getDate();
            LocalDate sal = dateSalida.getDate();
            double precio = Double.parseDouble(campoPrecio.getText());
            if (m == null || ent == null || sal == null || sal.isBefore(ent)) {
                JOptionPane.showMessageDialog(this, "Completa los datos correctamente", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Reserva r = new Reserva(m.getId(), ent, sal, precio, false);
            ReservaDAO.insertar(r);
            JOptionPane.showMessageDialog(this, "Reserva guardada con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
