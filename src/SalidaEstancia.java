import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.DateChangeListener;
import java.time.format.DateTimeFormatter;

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
        // Ajustamos la ventana al finalizar la construcción con pack() para que se
        // ajuste al contenido. No establecemos un tamaño fijo para permitir el
        // redimensionado adecuado.
        // No establecemos la posición inicial aquí, la estableceremos al final
        setLayout(new BorderLayout(15, 15));
        // Color de fondo claro y uniforme para todo el diálogo.
        getContentPane().setBackground(new Color(240, 240, 240));

        // Panel principal con GridBagLayout para un mejor posicionamiento y para
        // centrar los componentes. Separamos las etiquetas a la izquierda y los
        // campos a la derecha.
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setBackground(new Color(240, 240, 240));

        // Fuentes coherentes con el resto de interfaces (20 puntos)
        Font fuenteEtiqueta = new Font("Segoe UI", Font.BOLD, 20);
        Font fuenteCampo   = new Font("Segoe UI", Font.PLAIN, 20);

        // Componentes de selección y entrada
        comboEstancia = new JComboBox<>(Datos.estancias.toArray(new Estancia[0]));
        comboEstancia.setFont(fuenteCampo);
        comboEstancia.addItemListener(e -> actualizarInfoEstancia());

        DatePickerSettings settings = new DatePickerSettings();
        settings.setAllowEmptyDates(false);
        settings.setFormatForDatesCommonEra("dd/MM/yyyy");

        dateNuevaSalida = new DatePicker(settings);
        dateNuevaSalida.setFont(fuenteCampo);
        dateNuevaSalida.setPreferredSize(new Dimension(250, 40));

        // Calcular automáticamente el importe cuando cambia la fecha de salida
        dateNuevaSalida.addDateChangeListener(event -> calcularImporte());

        // Etiquetas de importe y detalles
        labelImporte = new JLabel("Total a pagar: €0,00");
        labelImporte.setFont(fuenteEtiqueta);

        infoEstancia = new JLabel(" ");
        infoEstancia.setFont(fuenteCampo);
        infoEstancia.setVerticalAlignment(SwingConstants.TOP);

        // Configuramos GridBagConstraints para distribuir las filas y columnas
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.NONE;

        // Fila 0: Mascota
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(crearEtiqueta("Mascota:", fuenteEtiqueta), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(comboEstancia, gbc);

        // Fila 1: Nueva fecha de salida
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(crearEtiqueta("Nueva fecha de salida:", fuenteEtiqueta), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(dateNuevaSalida, gbc);

        // Fila 2: Detalles (alineado arriba a la derecha para el texto multilínea)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        panel.add(crearEtiqueta("Detalles:", fuenteEtiqueta), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(infoEstancia, gbc);

        // Fila 3: Importe
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(crearEtiqueta("Importe:", fuenteEtiqueta), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(labelImporte, gbc);

        // Panel de botones con FlowLayout centrado
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        botones.setBackground(new Color(240, 240, 240));

        JButton btnSalida     = crearBoton("Dar salida hoy", new Color(200, 50, 50));
        JButton btnActualizar = crearBoton("Actualizar fecha", new Color(70, 130, 180));
        JButton btnCalcular   = crearBoton("Calcular importe", new Color(60, 179, 113));
        JButton btnCerrar     = crearBoton("Cerrar", new Color(100, 100, 100));

        btnSalida.addActionListener(e -> darSalida());
        btnActualizar.addActionListener(e -> actualizarFecha());
        btnCalcular.addActionListener(e -> calcularImporte());
        btnCerrar.addActionListener(e -> dispose());

        botones.add(btnSalida);
        botones.add(btnActualizar);
        botones.add(btnCalcular);
        botones.add(btnCerrar);

        // Añadimos paneles al diálogo
        add(panel, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);

        // Actualizamos la información de la estancia seleccionada
        actualizarInfoEstancia();

        // Ajustamos el tamaño y establecemos el tamaño mínimo para conservar la
        // estructura al redimensionar
        pack();
        setMinimumSize(getPreferredSize());

        // Centrar la ventana en la pantalla una vez configurada (null = centro de pantalla)
        setLocationRelativeTo(null);
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

        // Formateamos las fechas en formato día/mes/año
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaIn = e.getFechaIngreso().format(formato);
        String fechaOut = e.getFechaSalida().format(formato);
        infoEstancia.setText(String.format(
                "<html>Nombre: <b>%s</b><br>Dueño: %s<br>Ingreso: %s<br>Salida prevista: %s</html>",
                m.getNombre(), m.getDueno().getNombre(),
                fechaIn, fechaOut
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

    /**
     * Crea una etiqueta con la fuente indicada y alineación a la derecha.
     * Este método simplifica la creación de las etiquetas del formulario,
     * garantizando que todas utilicen las mismas propiedades de fuente y
     * alineación.
     *
     * @param texto  El texto que mostrará la etiqueta.
     * @param fuente La fuente a aplicar.
     * @return JLabel configurado.
     */
    private JLabel crearEtiqueta(String texto, Font fuente) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(fuente);
        etiqueta.setHorizontalAlignment(SwingConstants.RIGHT);
        return etiqueta;
    }
}
