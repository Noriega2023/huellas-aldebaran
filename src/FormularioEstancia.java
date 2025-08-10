import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Diálogo para registrar una estancia en la residencia canina.
 * <p>
 * Permite seleccionar una mascota disponible, establecer las fechas de ingreso y salida,
 * indicar el precio por día y si la estancia ha sido pagada. El formulario valida los
 * campos introducidos y evita que se creen estancias solapadas para la misma mascota.
 */
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
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 20));
        JTextField campoBuscar = new JTextField(20);
        campoBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        comboMascota = new JComboBox<>();
        comboMascota.setFont(new Font("Segoe UI", Font.PLAIN, 20));
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
        panelC.setBackground(new Color(240, 240, 240));
        panelC.setBorder(new EmptyBorder(20, 80, 20, 80));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.WEST;

        // Unificar tamaños de fuente en todo el formulario (20 pt)
        Font fuenteLabel = new Font("Segoe UI", Font.BOLD, 20);
        Font fuenteCampo = new Font("Segoe UI", Font.PLAIN, 20);

        // Fechas
        DatePickerSettings settingsIngreso = new DatePickerSettings();
        settingsIngreso.setAllowEmptyDates(false);
        settingsIngreso.setFormatForDatesCommonEra("dd/MM/yyyy");
        // Ajustar fuentes internas del calendario al mismo tamaño
        settingsIngreso.setFontCalendarDateLabels(fuenteCampo);
        settingsIngreso.setFontValidDate(fuenteCampo);
        dateIngreso = new DatePicker(settingsIngreso);
        dateIngreso.setFont(fuenteCampo);
        dateIngreso.setPreferredSize(new Dimension(250, 40));

        DatePickerSettings settingsSalida = new DatePickerSettings();
        settingsSalida.setAllowEmptyDates(false);
        settingsSalida.setFormatForDatesCommonEra("dd/MM/yyyy");
        settingsSalida.setFontCalendarDateLabels(fuenteCampo);
        settingsSalida.setFontValidDate(fuenteCampo);
        dateSalida = new DatePicker(settingsSalida);
        dateSalida.setFont(fuenteCampo);
        dateSalida.setPreferredSize(new Dimension(250, 40));

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelC.add(new JLabel("Fecha ingreso:"), gbc);
        gbc.gridx = 1;
        panelC.add(dateIngreso, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelC.add(new JLabel("Fecha salida:"), gbc);
        gbc.gridx = 1;
        panelC.add(dateSalida, gbc);

        // Precio y pagado
        campoPrecioDia = new JTextField(" ");
        campoPrecioDia.setFont(fuenteCampo);
        campoPrecioDia.setPreferredSize(new Dimension(250, 40));

        checkPagado = new JCheckBox("Pagado");
        checkPagado.setFont(fuenteLabel);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panelC.add(new JLabel("Precio por día (€):"), gbc);
        gbc.gridx = 1;
        panelC.add(campoPrecioDia, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panelC.add(checkPagado, gbc);

        add(panelC, BorderLayout.CENTER);

        // Botón inferior
        JButton btnIngreso = new JButton("Registrar Estancia");
        btnIngreso.setFont(fuenteLabel);
        btnIngreso.setBackground(new Color(70, 130, 180));
        btnIngreso.setForeground(Color.WHITE);
        btnIngreso.setFocusPainted(false);
        btnIngreso.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnIngreso.addActionListener(e -> guardarEstancia());

        JPanel panelBot = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        panelBot.setBackground(new Color(240, 240, 240));
        panelBot.add(btnIngreso);
        add(panelBot, BorderLayout.SOUTH);

        // Ajustar tamaño y comportamiento de la ventana para que conserve la estructura al redimensionar
        pack();
        setMinimumSize(getPreferredSize());
    }

    /**
     * Carga las mascotas disponibles en el combo, aplicando un filtro por nombre y
     * descartando aquellas que ya tengan una estancia activa en la fecha actual.
     *
     * @param filtro Texto por el que filtrar el nombre de las mascotas
     */
    private void cargarMascotas(String filtro) {
        comboMascota.removeAllItems();
        List<Mascota> lista = MascotaDAO.listarTodos();
        LocalDate hoy = LocalDate.now();

        for (Mascota m : lista) {
            // Verificar si la mascota ya está en una estancia activa (no pagada).
            // Consideramos activa sólo si la fecha actual está dentro del rango de la estancia de forma estricta
            // (desde la fecha de ingreso inclusive hasta la fecha de salida exclusiva). De este modo, si una
            // estancia finaliza hoy, la mascota queda disponible inmediatamente para una nueva estancia.
            boolean enEstancia = Datos.estancias.stream()
                    .anyMatch(e -> e.getMascotaId() == m.getId()
                            && !e.isPagado()
                            && !hoy.isBefore(e.getFechaIngreso())
                            && hoy.isBefore(e.getFechaSalida()));

            // Solo agregar si cumple el filtro y NO está en estancia activa
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

    /**
     * Guarda una nueva estancia en la base de datos y actualiza la lista en memoria.
     * <p>
     * Este método valida que exista una mascota seleccionada, que las fechas de
     * ingreso y salida sean válidas y que el precio por día sea un número. También
     * comprueba que la mascota no tenga otra estancia activa que se solape con
     * el rango de fechas propuesto. Si todas las validaciones se cumplen, se
     * crea un nuevo objeto {@link Estancia}, se inserta mediante {@link EstanciaDAO}
     * y se añade a la lista estática {@link Datos#estancias}. Finalmente se
     * muestra un mensaje de éxito al usuario y se cierra el diálogo.
     */
    private void guardarEstancia() {
        try {
            // Validar que haya una mascota seleccionada
            Mascota mascota = (Mascota) comboMascota.getSelectedItem();
            if (mascota == null) {
                JOptionPane.showMessageDialog(this,
                        "Debe seleccionar una mascota",
                        "Mascota no seleccionada",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Obtener fechas del selector de fechas
            LocalDate fechaIngreso = dateIngreso.getDate();
            LocalDate fechaSalida = dateSalida.getDate();
            if (fechaIngreso == null || fechaSalida == null) {
                JOptionPane.showMessageDialog(this,
                        "Debes seleccionar las fechas de ingreso y salida",
                        "Fechas faltantes",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (fechaIngreso.isAfter(fechaSalida)) {
                JOptionPane.showMessageDialog(this,
                        "La fecha de ingreso no puede ser posterior a la fecha de salida",
                        "Fechas inválidas",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validar y convertir el precio por día
            String precioTexto = campoPrecioDia.getText().trim();
            if (precioTexto.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Debe introducir el precio por día",
                        "Precio faltante",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            double precio;
            try {
                precio = Double.parseDouble(precioTexto);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "El precio debe ser un número válido",
                        "Precio inválido",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean pagado = checkPagado.isSelected();

            // Comprobar si la mascota ya tiene una estancia que se solape con las fechas ingresadas.
            // Ignoramos estancias que estén pagadas (finalizadas). Para determinar si hay solapamiento,
            // utilizamos comparaciones estrictas: las fechas se consideran solapadas únicamente si
            // la fecha de ingreso propuesta es estrictamente anterior a la fecha de salida existente y
            // la fecha de salida propuesta es estrictamente posterior a la fecha de ingreso existente.
            for (Estancia e : Datos.estancias) {
                // Ignorar estancias finalizadas
                if (e.getMascotaId() == mascota.getId() && !e.isPagado()) {
                    // Si las fechas se solapan con alguna estancia no pagada, mostrar error
                    if (fechaIngreso.isBefore(e.getFechaSalida()) && fechaSalida.isAfter(e.getFechaIngreso())) {
                        JOptionPane.showMessageDialog(this,
                                "La mascota seleccionada ya tiene una estancia que se solapa con las fechas seleccionadas.",
                                "Conflicto de estancia",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            // Crear y persistir la nueva estancia
            Estancia nueva = new Estancia(mascota.getId(), fechaIngreso, fechaSalida, precio, pagado);
            EstanciaDAO.insertar(nueva);
            Datos.estancias.add(nueva);

            JOptionPane.showMessageDialog(this,
                    "Estancia guardada con éxito",
                    "Operación exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Se produjo un error al guardar la estancia: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para verificar si la mascota ya está en una estancia activa
    private boolean estaMascotaEnEstanciaActiva(int mascotaId) {
        LocalDate hoy = LocalDate.now();
        for (Estancia estancia : Datos.estancias) {
            if (estancia.getMascotaId() == mascotaId && !estancia.isPagado()) {
                // La estancia está activa si hoy está dentro del intervalo [fechaIngreso, fechaSalida)
                if (!hoy.isBefore(estancia.getFechaIngreso()) && hoy.isBefore(estancia.getFechaSalida())) {
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