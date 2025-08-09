import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class FormularioEstancia extends JDialog {
    private JComboBox<Mascota> comboMascota;
    private DatePicker dateIngreso, dateSalida;
    private JTextField campoPrecioDia;
    private JCheckBox checkPagado;

    public FormularioEstancia(JFrame parent) {
        super(parent, "Registro de Estancia", true);
        setSize(800, 550);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 240, 240));

        // Panel superior: buscador de mascota
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBusqueda.setBackground(new Color(240, 240, 240));
        JLabel lblBuscar = new JLabel("Buscar mascota:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JTextField campoBuscar = new JTextField(20);
        campoBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        comboMascota = new JComboBox<>();
        comboMascota.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        cargarMascotas("");

        campoBuscar.getDocument().addDocumentListener(new SimpleDocListener() {
            public void update() {
                cargarMascotas(campoBuscar.getText().trim());
            }
        });

        panelBusqueda.add(lblBuscar);
        panelBusqueda.add(campoBuscar);
        panelBusqueda.add(comboMascota);
        add(panelBusqueda, BorderLayout.NORTH);

        // Panel central: formulario
        JPanel panelC = new JPanel(new GridBagLayout());
        panelC.setBackground(new Color(240,240,240));
        panelC.setBorder(new EmptyBorder(20, 80, 20, 80));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12,12,12,12);
        gbc.anchor = GridBagConstraints.WEST;

        Font fuenteLabel = new Font("Segoe UI", Font.BOLD, 18);
        Font fuenteCampo = new Font("Segoe UI", Font.PLAIN, 18);

        // Fechas
        DatePickerSettings settingsIngreso = new DatePickerSettings();
        settingsIngreso.setAllowEmptyDates(false);
        settingsIngreso.setFormatForDatesCommonEra("dd/MM/yyyy");
        dateIngreso = new DatePicker(settingsIngreso);
        dateIngreso.setFont(fuenteCampo);
        dateIngreso.setPreferredSize(new Dimension(250, 40));

        DatePickerSettings settingsSalida = new DatePickerSettings();
        settingsSalida.setAllowEmptyDates(false);
        settingsSalida.setFormatForDatesCommonEra("dd/MM/yyyy");
        dateSalida = new DatePicker(settingsSalida);
        dateSalida.setFont(fuenteCampo);
        dateSalida.setPreferredSize(new Dimension(250, 40));

        gbc.gridx = 0; gbc.gridy = 0;
        panelC.add(new JLabel("Fecha ingreso:"), gbc);
        gbc.gridx = 1;
        panelC.add(dateIngreso, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelC.add(new JLabel("Fecha salida:"), gbc);
        gbc.gridx = 1;
        panelC.add(dateSalida, gbc);

        // Precio y pagado
        campoPrecioDia = new JTextField(" ");
        campoPrecioDia.setFont(fuenteCampo);
        campoPrecioDia.setPreferredSize(new Dimension(250, 40));

        checkPagado = new JCheckBox("Pagado");
        checkPagado.setFont(fuenteLabel);

        gbc.gridx = 0; gbc.gridy = 2;
        panelC.add(new JLabel("Precio por día (€):"), gbc);
        gbc.gridx = 1;
        panelC.add(campoPrecioDia, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panelC.add(checkPagado, gbc);

        add(panelC, BorderLayout.CENTER);

        // Botón inferior
        JButton btnIngreso = new JButton("Registrar Estancia");
        btnIngreso.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnIngreso.setBackground(new Color(70, 130, 180));
        btnIngreso.setForeground(Color.WHITE);
        btnIngreso.setFocusPainted(false);
        btnIngreso.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnIngreso.addActionListener(e -> guardarEstancia());

        JPanel panelBot = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        panelBot.setBackground(new Color(240,240,240));
        panelBot.add(btnIngreso);
        add(panelBot, BorderLayout.SOUTH);
    }

    private void cargarMascotas(String filtro) {
        comboMascota.removeAllItems();
        List<Mascota> lista = MascotaDAO.listarTodos();
        LocalDate hoy = LocalDate.now();

        for (Mascota m : lista) {
            // Verificar si la mascota ya está en una estancia activa
            boolean enEstancia = Datos.estancias.stream()
                    .anyMatch(e -> e.getMascotaId() == m.getId() &&
                            !hoy.isBefore(e.getFechaIngreso()) &&
                            !hoy.isAfter(e.getFechaSalida()));

            // Solo agregar si cumple el filtro y NO está en estancia
            if (!enEstancia && (filtro.isEmpty() || m.getNombre().toLowerCase().contains(filtro.toLowerCase()))) {
                comboMascota.addItem(m);
            }
        }

        if (comboMascota.getItemCount() > 0) {
            comboMascota.setSelectedIndex(0);
        } else {
            JOptionPane.showMessageDialog(this,
                    "No hay mascotas disponibles que coincidan con el criterio de búsqueda",
                    "Sin resultados",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void guardarEstancia() {
        try {
            Mascota mascota = (Mascota) comboMascota.getSelectedItem();
            // ... resto del código existente ...

            // Esta validación ya no sería necesaria técnicamente, pero es buena práctica mantenerla
            if (estaMascotaEnEstanciaActiva(mascota.getId())) {
                JOptionPane.showMessageDialog(this,
                        "Error: La mascota seleccionada ya está en una estancia activa.\n" +
                                "Por favor, actualiza la lista de mascotas.",
                        "Conflicto de estancia",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // ... resto del código existente ...
        } catch (Exception ex) {
            // ... manejo de errores existente ...
        }
    }

    // Método para verificar si la mascota ya está en una estancia activa
    private boolean estaMascotaEnEstanciaActiva(int mascotaId) {
        LocalDate hoy = LocalDate.now();

        for (Estancia estancia : Datos.estancias) {
            if (estancia.getMascotaId() == mascotaId) {
                // Verificar si las fechas de la estancia existente se solapan con hoy
                if (!hoy.isBefore(estancia.getFechaIngreso()) && !hoy.isAfter(estancia.getFechaSalida())) {
                    return true;
                }
            }
        }
        return false;
    }

    // Listener para buscar cambios en texto
    private abstract static class SimpleDocListener implements javax.swing.event.DocumentListener {
        public abstract void update();
        public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
        public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
        public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
    }
}
