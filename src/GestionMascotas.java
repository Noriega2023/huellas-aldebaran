import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;
import java.util.stream.Collectors;

public class GestionMascotas extends JDialog {
    private JComboBox<Dueno> comboDuenos;
    private DefaultComboBoxModel<Dueno> modeloDuenos;
    private JComboBox<Mascota> comboMascotas;
    private DefaultComboBoxModel<Mascota> modeloMascotas;
    private JTextField campoNombre, campoRaza, campoEdad, campoPeso, campoBusquedaDueno;
    private JTextArea campoObs;
    private JButton btnAnadir, btnActualizar, btnEliminar, btnLimpiar, btnBuscarDueno;
    private JLabel lblAdvertencia;
    private List<Mascota> listaMascotas;
    private JScrollPane scrollObservaciones;

    public GestionMascotas(JFrame parent) {
        super(parent, "Gestión de Mascotas", true);
        setSize(1200, 850); // Aumentado para acomodar el nuevo campo de búsqueda
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 240, 240));

        // Panel superior con búsqueda
        JPanel panelSuperior = new JPanel(new GridBagLayout());
        panelSuperior.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbcSuperior = new GridBagConstraints();
        gbcSuperior.insets = new Insets(5, 5, 5, 5);
        gbcSuperior.fill = GridBagConstraints.HORIZONTAL;

        // Componentes de búsqueda
        JLabel lblBuscarDueno = new JLabel("Buscar dueño:");
        lblBuscarDueno.setFont(new Font("Segoe UI", Font.BOLD, 16));

        campoBusquedaDueno = new JTextField();
        campoBusquedaDueno.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        campoBusquedaDueno.setPreferredSize(new Dimension(250, 35));
        campoBusquedaDueno.getDocument().addDocumentListener(new DocumentListener() {
            private Timer timer;
            public void insertUpdate(DocumentEvent e) { buscarDuenos(); }
            public void removeUpdate(DocumentEvent e) { buscarDuenos(); }
            public void changedUpdate(DocumentEvent e) { buscarDuenos(); }
            private void iniciarBusquedaRetardada() {
                if (timer != null && timer.isRunning()) {
                    timer.stop();
                }
                timer = new Timer(500, e -> buscarDuenos()); // 500ms de retardo
                timer.setRepeats(false);
                timer.start();
            }
        });

        btnBuscarDueno = new JButton("Buscar");
        btnBuscarDueno.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBuscarDueno.setBackground(new Color(70, 130, 180));
        btnBuscarDueno.setForeground(Color.WHITE);
        btnBuscarDueno.setFocusPainted(false);
        btnBuscarDueno.addActionListener(e -> buscarDuenos());

        // Añadir componentes de búsqueda
        gbcSuperior.gridx = 0; gbcSuperior.gridy = 0;
        panelSuperior.add(lblBuscarDueno, gbcSuperior);

        gbcSuperior.gridx = 1; gbcSuperior.gridy = 0;
        panelSuperior.add(campoBusquedaDueno, gbcSuperior);

        gbcSuperior.gridx = 2; gbcSuperior.gridy = 0;
        panelSuperior.add(btnBuscarDueno, gbcSuperior);

        // Combo de dueños
        JLabel lblSeleccionarDueno = new JLabel("Seleccionar dueño:");
        lblSeleccionarDueno.setFont(new Font("Segoe UI", Font.BOLD, 16));

        modeloDuenos = new DefaultComboBoxModel<>();
        comboDuenos = new JComboBox<>(modeloDuenos);
        comboDuenos.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        comboDuenos.setPreferredSize(new Dimension(300, 35));
        comboDuenos.addActionListener(e -> cargarMascotas());
        comboDuenos.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setHorizontalAlignment(SwingConstants.LEFT);
                return this;
            }

        });

        // Añadir combo de dueños
        gbcSuperior.gridx = 0; gbcSuperior.gridy = 1;
        gbcSuperior.gridwidth = 3;
        panelSuperior.add(lblSeleccionarDueno, gbcSuperior);

        gbcSuperior.gridx = 0; gbcSuperior.gridy = 2;
        gbcSuperior.gridwidth = 3;
        panelSuperior.add(comboDuenos, gbcSuperior);

        // Panel de advertencia
        JPanel panelAdvertencia = new JPanel();
        panelAdvertencia.setBackground(new Color(240, 240, 240));
        panelAdvertencia.setLayout(new FlowLayout(FlowLayout.CENTER));
        lblAdvertencia = new JLabel();
        lblAdvertencia.setForeground(Color.RED);
        lblAdvertencia.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelAdvertencia.add(lblAdvertencia);

        gbcSuperior.gridx = 0; gbcSuperior.gridy = 3;
        gbcSuperior.gridwidth = 3;
        panelSuperior.add(panelAdvertencia, gbcSuperior);

        add(panelSuperior, BorderLayout.NORTH);

        // Panel central con formulario
        JPanel panelCentral = new JPanel();
        panelCentral.setBackground(new Color(240, 240, 240));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));

        JSeparator separador = new JSeparator(JSeparator.HORIZONTAL);
        separador.setForeground(new Color(200, 200, 200));
        separador.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));
        panelCentral.add(Box.createRigidArea(new Dimension(0, 10)));
        panelCentral.add(separador);
        panelCentral.add(Box.createRigidArea(new Dimension(0, 20)));

        // Campos del formulario
        JPanel panelCampos = new JPanel();
        panelCampos.setBackground(new Color(240, 240, 240));
        panelCampos.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 20, 8);
        gbc.anchor = GridBagConstraints.WEST;

        Font fuenteCampo = new Font("Segoe UI", Font.PLAIN, 20);
        Font fuenteEtiqueta = new Font("Segoe UI", Font.BOLD, 20);

        // Combo Mascotas
        modeloMascotas = new DefaultComboBoxModel<>();
        comboMascotas = new JComboBox<>(modeloMascotas);
        comboMascotas.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        comboMascotas.setPreferredSize(new Dimension(600, 45));
        comboMascotas.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setHorizontalAlignment(SwingConstants.LEFT);
                setFont(new Font("Segoe UI", Font.PLAIN, 20));
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                if (isSelected) {
                    setBackground(new Color(70, 130, 180));
                    setForeground(Color.WHITE);
                }
                return this;
            }
        });
        comboMascotas.addActionListener(e -> mostrarDatos());

        // Campos con placeholders
        campoNombre = crearCampoConPlaceholder("Nombre mascota", fuenteCampo, 500, 45);
        campoRaza = crearCampoConPlaceholder("Raza", fuenteCampo, 500, 45);
        campoEdad = crearCampoConPlaceholder("Edad (años)", fuenteCampo, 500, 45);
        campoPeso = crearCampoConPlaceholder("Peso (kg)", fuenteCampo, 500, 45);

        // Área de observaciones
        campoObs = new JTextArea(6, 40) {
            @Override
            public boolean getScrollableTracksViewportWidth() {
                return false;
            }
        };
        campoObs.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        campoObs.setBackground(Color.WHITE);
        campoObs.setLineWrap(true);
        campoObs.setWrapStyleWord(true);
        campoObs.setMargin(new Insets(10, 15, 10, 15));

        scrollObservaciones = new JScrollPane(campoObs);
        scrollObservaciones.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        // Ampliamos el área de observaciones para que sea más cómoda de usar
        scrollObservaciones.setPreferredSize(new Dimension(600, 150));
        scrollObservaciones.setMinimumSize(new Dimension(600, 150));
        scrollObservaciones.setMaximumSize(new Dimension(600, 150));

        // Placeholder para observaciones
        campoObs.setText("Observaciones (opcional)");
        campoObs.setForeground(Color.GRAY);
        campoObs.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (campoObs.getText().equals("Observaciones (opcional)")) {
                    campoObs.setText("");
                    campoObs.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (campoObs.getText().isEmpty()) {
                    campoObs.setText("Observaciones (opcional)");
                    campoObs.setForeground(Color.GRAY);
                }
            }
        });

        // Listener para verificar duplicados
        campoNombre.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { verificarDuplicados(); }
            public void removeUpdate(DocumentEvent e) { verificarDuplicados(); }
            public void changedUpdate(DocumentEvent e) { verificarDuplicados(); }
        });

        // Añadir campos al panel
        gbc.gridx = 0; gbc.gridy = 0;
        panelCampos.add(crearEtiqueta("Mascota:", fuenteEtiqueta), gbc);
        gbc.gridx = 1;
        panelCampos.add(comboMascotas, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelCampos.add(crearEtiqueta("Nombre:", fuenteEtiqueta), gbc);
        gbc.gridx = 1;
        panelCampos.add(campoNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelCampos.add(crearEtiqueta("Raza:", fuenteEtiqueta), gbc);
        gbc.gridx = 1;
        panelCampos.add(campoRaza, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panelCampos.add(crearEtiqueta("Edad:", fuenteEtiqueta), gbc);
        gbc.gridx = 1;
        panelCampos.add(campoEdad, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panelCampos.add(crearEtiqueta("Peso:", fuenteEtiqueta), gbc);
        gbc.gridx = 1;
        panelCampos.add(campoPeso, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panelCampos.add(crearEtiqueta("Observaciones:", fuenteEtiqueta), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.NONE;
        panelCampos.add(scrollObservaciones, gbc);

        panelCentral.add(panelCampos);
        add(panelCentral, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 20));
        panelBotones.setBackground(new Color(240, 240, 240));

        btnAnadir = crearBoton("Añadir", new Color(50, 150, 50), 20);
        btnActualizar = crearBoton("Modificar", new Color(70, 130, 180), 20);
        btnEliminar = crearBoton("Eliminar", new Color(200, 50, 50), 20);
        btnLimpiar = crearBoton("Limpiar", new Color(100, 100, 100), 20);

        btnAnadir.addActionListener(e -> insertarMascota());
        btnActualizar.addActionListener(e -> actualizarMascota());
        btnEliminar.addActionListener(e -> confirmarEliminacion());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        panelBotones.add(btnAnadir);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        add(panelBotones, BorderLayout.SOUTH);
        cargarDuenos();

        // Ajustar tamaño y comportamiento de la ventana para que conserve la estructura al redimensionar
        pack();
        setMinimumSize(getPreferredSize());
    }

    private void buscarDuenos() {
        boolean tieneFoco = campoBusquedaDueno.hasFocus();
        String textoBusqueda = campoBusquedaDueno.getText().trim().toLowerCase();
        List<Dueno> todosDuenos = DuenoDAO.listar();

        if (textoBusqueda.isEmpty()) {
            cargarDuenos();
            if (tieneFoco) campoBusquedaDueno.requestFocus();
            return;
        }

        List<Dueno> duenosFiltrados = todosDuenos.stream()
                .filter(d -> d.getNombre().toLowerCase().contains(textoBusqueda))
                .collect(Collectors.toList());

        modeloDuenos.removeAllElements();
        duenosFiltrados.forEach(modeloDuenos::addElement);

        if (modeloDuenos.getSize() > 0) {
            comboDuenos.setSelectedIndex(0);
            lblAdvertencia.setText("");
        } else {
            lblAdvertencia.setText("No se encontraron dueños con ese nombre");
        }

        // Restaurar el foco si lo tenía
        if (tieneFoco) {
            SwingUtilities.invokeLater(() -> campoBusquedaDueno.requestFocus());
        }
    }

    private void limpiarCampos() {
        if (campoNombre.getForeground() != Color.GRAY) {
            campoNombre.setText("Nombre mascota");
            campoNombre.setForeground(Color.GRAY);
        }

        if (campoRaza.getForeground() != Color.GRAY) {
            campoRaza.setText("Raza");
            campoRaza.setForeground(Color.GRAY);
        }

        if (campoEdad.getForeground() != Color.GRAY) {
            campoEdad.setText("Edad (años)");
            campoEdad.setForeground(Color.GRAY);
        }

        if (campoPeso.getForeground() != Color.GRAY) {
            campoPeso.setText("Peso (kg)");
            campoPeso.setForeground(Color.GRAY);
        }

        campoObs.setText("Observaciones (opcional)");
        campoObs.setForeground(Color.GRAY);
        comboMascotas.setSelectedIndex(-1);
        lblAdvertencia.setText("");
        campoNombre.requestFocusInWindow();
    }

    private void mostrarDatos() {
        Mascota m = (Mascota) comboMascotas.getSelectedItem();
        if (m != null) {
            campoNombre.setText(m.getNombre());
            campoNombre.setForeground(Color.BLACK);

            if (m.getRaza() != null && !m.getRaza().isEmpty()) {
                campoRaza.setText(m.getRaza());
                campoRaza.setForeground(Color.BLACK);
            } else {
                campoRaza.setText("Raza");
                campoRaza.setForeground(Color.GRAY);
            }

            if (m.getEdad() > 0) {
                campoEdad.setText(String.valueOf(m.getEdad()));
                campoEdad.setForeground(Color.BLACK);
            } else {
                campoEdad.setText("Edad (años)");
                campoEdad.setForeground(Color.GRAY);
            }

            if (m.getPeso() != null) {
                campoPeso.setText(m.getPeso().toString());
                campoPeso.setForeground(Color.BLACK);
            } else {
                campoPeso.setText("Peso (kg)");
                campoPeso.setForeground(Color.GRAY);
            }

            if (m.getObservaciones() != null && !m.getObservaciones().isEmpty()) {
                campoObs.setText(m.getObservaciones());
                campoObs.setForeground(Color.BLACK);
            } else {
                campoObs.setText("Observaciones (opcional)");
                campoObs.setForeground(Color.GRAY);
            }
        }
        actualizarBotones();
    }

    private void verificarDuplicados() {
        if (campoNombre.getForeground() == Color.GRAY || campoNombre.getText().trim().isEmpty()) {
            lblAdvertencia.setText("");
            return;
        }

        String nombreBusqueda = campoNombre.getText().trim().toLowerCase();
        boolean existeDuplicado = listaMascotas != null && listaMascotas.stream()
                .anyMatch(m -> m.getNombre().toLowerCase().equals(nombreBusqueda));

        lblAdvertencia.setText(existeDuplicado ? "¡Posible duplicado!" : "");
    }

    private JLabel crearEtiqueta(String texto, Font font) {
        JLabel label = new JLabel(texto);
        label.setFont(font);
        return label;
    }

    private JTextField crearCampoConPlaceholder(String placeholder, Font font, int ancho, int alto) {
        JTextField campo = new JTextField();
        campo.setFont(font);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        campo.setPreferredSize(new Dimension(ancho, alto));
        campo.setMinimumSize(new Dimension(ancho, alto));
        campo.setText(placeholder);
        campo.setForeground(Color.GRAY);

        campo.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if (campo.getText().equals(placeholder)) {
                    campo.setText("");
                    campo.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent evt) {
                if (campo.getText().isEmpty()) {
                    campo.setText(placeholder);
                    campo.setForeground(Color.GRAY);
                }
            }
        });

        return campo;
    }

    private JButton crearBoton(String texto, Color color, int fontSize) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
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

    private void cargarDuenos() {
        modeloDuenos.removeAllElements();
        DuenoDAO.listar().forEach(modeloDuenos::addElement);
        campoBusquedaDueno.setText("");
        lblAdvertencia.setText("");

        if (modeloDuenos.getSize() > 0) {
            comboDuenos.setSelectedIndex(0);
            cargarMascotas();
        }
        actualizarBotones();
    }

    private void cargarMascotas() {
        modeloMascotas = new DefaultComboBoxModel<>();
        comboMascotas.setModel(modeloMascotas);
        Dueno d = (Dueno) comboDuenos.getSelectedItem();
        if (d == null) return;
        listaMascotas = MascotaDAO.listar(d.getId());
        listaMascotas.forEach(modeloMascotas::addElement);
        if (modeloMascotas.getSize() > 0) {
            comboMascotas.setSelectedIndex(0);
            mostrarDatos();
        } else {
            limpiarCampos();
        }
        actualizarBotones();
    }

    private void actualizarBotones() {
        boolean hayMascotas = modeloMascotas != null && modeloMascotas.getSize() > 0;
        btnActualizar.setEnabled(hayMascotas);
        btnEliminar.setEnabled(hayMascotas);
    }

    private void insertarMascota() {
        String nombre = campoNombre.getForeground() == Color.GRAY ? "" : campoNombre.getText().trim();
        String raza = campoRaza.getForeground() == Color.GRAY ? "" : campoRaza.getText().trim();
        String edadStr = campoEdad.getForeground() == Color.GRAY ? "" : campoEdad.getText().trim();
        String pesoStr = campoPeso.getForeground() == Color.GRAY ? "" : campoPeso.getText().trim();
        String observaciones = campoObs.getForeground() == Color.GRAY ? "" : campoObs.getText().trim();

        if (nombre.isEmpty() || raza.isEmpty() || edadStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nombre, raza y edad son obligatorios",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double edad = Double.parseDouble(edadStr);
            Double peso = pesoStr.isEmpty() ? null : Double.parseDouble(pesoStr);

            Dueno d = (Dueno) comboDuenos.getSelectedItem();
            if (d == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un dueño");
                return;
            }

            if (listaMascotas.stream().anyMatch(m -> m.getNombre().equalsIgnoreCase(nombre))) {
                if (JOptionPane.showConfirmDialog(this,
                        "Ya existe una mascota con ese nombre. ¿Desea añadirla de todos modos?",
                        "Mascota duplicada",
                        JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            Mascota m = new Mascota(0, nombre, raza, edad, observaciones, d, peso);
            MascotaDAO.insertar(m);

            JOptionPane.showMessageDialog(this,
                    "Mascota añadida correctamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

            cargarMascotas();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Edad y peso deben ser valores numéricos válidos",
                    "Error en datos",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarMascota() {
        Mascota m = (Mascota) comboMascotas.getSelectedItem();
        if (m == null) return;

        try {
            m.setNombre(campoNombre.getForeground() == Color.GRAY ? "" : campoNombre.getText().trim());
            m.setRaza(campoRaza.getForeground() == Color.GRAY ? "" : campoRaza.getText().trim());
            m.setEdad(Double.parseDouble(
                    campoEdad.getForeground() == Color.GRAY ? "0" : campoEdad.getText().trim()));
            m.setPeso(campoPeso.getForeground() == Color.GRAY ? null :
                    Double.parseDouble(campoPeso.getText().trim()));
            m.setObservaciones(campoObs.getForeground() == Color.GRAY ? "" : campoObs.getText().trim());

            MascotaDAO.actualizar(m);

            JOptionPane.showMessageDialog(this,
                    "Mascota actualizada correctamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

            cargarMascotas();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Edad y peso deben ser valores numéricos válidos",
                    "Error en datos",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void confirmarEliminacion() {
        Mascota m = (Mascota) comboMascotas.getSelectedItem();
        if (m == null) {
            JOptionPane.showMessageDialog(this,
                    "No hay ninguna mascota seleccionada para eliminar",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "<html><div style='font-size:14pt;'>¿Eliminar a la mascota <b>" + m.getNombre() + "</b>?</div></html>",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            try {
                MascotaDAO.eliminar(m.getId());

                JOptionPane.showMessageDialog(this,
                        "Mascota eliminada correctamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

                cargarMascotas();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error al eliminar la mascota: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}