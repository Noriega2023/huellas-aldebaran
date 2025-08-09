import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

import static java.awt.Component.CENTER_ALIGNMENT;

public class GestionClientes extends JDialog {
    private JComboBox<Dueno> comboClientes;
    private DefaultComboBoxModel<Dueno> modeloClientes;
    private JTextField campoNombre, campoTelefono, campoEmail;
    private JButton btnAnadir, btnActualizar, btnEliminar, btnLimpiar;
    private JLabel lblAdvertencia;
    private List<Dueno> listaClientes;

    public GestionClientes(JFrame parent) {
        super(parent, "Gestión de Clientes", true);
        setSize(1000, 650);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 240, 240));

        // Panel superior - Centrado
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelSuperior.setBackground(new Color(240, 240, 240));

        modeloClientes = new DefaultComboBoxModel<>();
        comboClientes = new JComboBox<>(modeloClientes);
        comboClientes.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        comboClientes.setPreferredSize(new Dimension(450, 45));
        comboClientes.addActionListener(e -> mostrarDatosCliente());
        comboClientes.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setHorizontalAlignment(SwingConstants.CENTER); // Centrar el texto
                return this;
            }
        });

        JLabel lblSeleccionar = new JLabel("Seleccionar cliente:");
        lblSeleccionar.setFont(new Font("Segoe UI", Font.BOLD, 20));

        panelSuperior.add(lblSeleccionar);
        panelSuperior.add(comboClientes);
        add(panelSuperior, BorderLayout.NORTH);

        // Panel central con formulario
        JPanel panelCentral = new JPanel();
        panelCentral.setBackground(new Color(240, 240, 240));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));

        // Panel para advertencia de duplicados (centrado y más grande)
        JPanel panelAdvertencia = new JPanel();
        panelAdvertencia.setBackground(new Color(240, 240, 240));
        panelAdvertencia.setLayout(new FlowLayout(FlowLayout.CENTER));
        lblAdvertencia = new JLabel();
        lblAdvertencia.setForeground(Color.RED);
        lblAdvertencia.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Texto más grande
        panelAdvertencia.add(lblAdvertencia);
        panelCentral.add(panelAdvertencia);

        // Línea divisoria
        JSeparator separador = new JSeparator(JSeparator.HORIZONTAL);
        separador.setForeground(new Color(200, 200, 200));
        separador.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));
        panelCentral.add(Box.createRigidArea(new Dimension(0, 15)));
        panelCentral.add(separador);
        panelCentral.add(Box.createRigidArea(new Dimension(0, 25)));

        // Campos del formulario
        JPanel panelCampos = new JPanel();
        panelCampos.setBackground(new Color(240, 240, 240));
        panelCampos.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 20, 8);
        gbc.anchor = GridBagConstraints.WEST;

        Font fuenteCampo = new Font("Segoe UI", Font.PLAIN, 20);
        Font fuenteEtiqueta = new Font("Segoe UI", Font.BOLD, 20);

        campoNombre = crearCampoConPlaceholder("Nombre completo", fuenteCampo, 500, 45);
        campoTelefono = crearCampoConPlaceholder("Teléfono", fuenteCampo, 500, 45);
        campoEmail = crearCampoConPlaceholder("Email (opcional)", fuenteCampo, 500, 45);

        campoNombre.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                verificarDuplicados();
            }

            public void removeUpdate(DocumentEvent e) {
                verificarDuplicados();
            }

            public void changedUpdate(DocumentEvent e) {
                verificarDuplicados();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelCampos.add(crearEtiqueta("Nombre:", fuenteEtiqueta), gbc);
        gbc.gridx = 1;
        panelCampos.add(campoNombre, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panelCampos.add(crearEtiqueta("Teléfono:", fuenteEtiqueta), gbc);
        gbc.gridx = 1;
        panelCampos.add(campoTelefono, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panelCampos.add(crearEtiqueta("Email:", fuenteEtiqueta), gbc);
        gbc.gridx = 1;
        panelCampos.add(campoEmail, gbc);

        panelCentral.add(panelCampos);
        add(panelCentral, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 20));
        panelBotones.setBackground(new Color(240, 240, 240));

        btnAnadir = crearBoton("Añadir", new Color(50, 150, 50), 20);
        btnActualizar = crearBoton("Modificar", new Color(70, 130, 180), 20);
        btnEliminar = crearBoton("Eliminar", new Color(200, 50, 50), 20);
        btnLimpiar = crearBoton("Limpiar", new Color(100, 100, 100), 20);

        btnAnadir.addActionListener(e -> guardarCliente());
        btnActualizar.addActionListener(e -> actualizarCliente());
        btnEliminar.addActionListener(e -> confirmarEliminacion());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        panelBotones.add(btnAnadir);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        add(panelBotones, BorderLayout.SOUTH);
        cargarClientes();
    }

    private void limpiarCampos() {
        // Restaurar placeholders manteniendo las descripciones
        if (campoNombre.getForeground() != Color.GRAY) {
            campoNombre.setText("Nombre completo");
            campoNombre.setForeground(Color.GRAY);
        }

        if (campoTelefono.getForeground() != Color.GRAY) {
            campoTelefono.setText("Teléfono");
            campoTelefono.setForeground(Color.GRAY);
        }

        if (campoEmail.getForeground() != Color.GRAY) {
            campoEmail.setText("Email (opcional)");
            campoEmail.setForeground(Color.GRAY);
        }

        comboClientes.setSelectedIndex(-1);
        campoNombre.requestFocusInWindow();
    }

    private void mostrarDatosCliente() {
        Dueno d = (Dueno) comboClientes.getSelectedItem();
        if (d != null) {
            campoNombre.setText(d.getNombre());
            campoNombre.setForeground(Color.BLACK);

            campoTelefono.setText(d.getTelefono() != null ? d.getTelefono() : "Teléfono");
            campoTelefono.setForeground(d.getTelefono() != null ? Color.BLACK : Color.GRAY);

            campoEmail.setText(d.getEmail() != null ? d.getEmail() : "Email (opcional)");
            campoEmail.setForeground(d.getEmail() != null ? Color.BLACK : Color.GRAY);
        }
        actualizarEstadoBotones();
    }

    private void verificarDuplicados() {
        if (campoNombre.getForeground() == Color.GRAY || campoNombre.getText().trim().isEmpty()) {
            lblAdvertencia.setText("");
            return;
        }

        String nombreBusqueda = campoNombre.getText().trim().toLowerCase();
        List<Dueno> coincidencias = listaClientes.stream()
                .filter(c -> c.getNombre().toLowerCase().contains(nombreBusqueda))
                .collect(Collectors.toList());

        lblAdvertencia.setText(coincidencias.isEmpty() ? "" :
                "Posible duplicado: " + coincidencias.stream()
                        .map(Dueno::getNombre)
                        .collect(Collectors.joining(", ")));
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

        campo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (campo.getText().equals(placeholder)) {
                    campo.setText("");
                    campo.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
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

    private void cargarClientes() {
        modeloClientes.removeAllElements();
        listaClientes = DuenoDAO.listar();
        listaClientes.forEach(modeloClientes::addElement);
        actualizarEstadoBotones();
    }

    private void actualizarEstadoBotones() {
        boolean hayClientes = modeloClientes.getSize() > 0;
        btnActualizar.setEnabled(hayClientes);
        btnEliminar.setEnabled(hayClientes);
    }

    private void guardarCliente() {
        String nombre = campoNombre.getForeground() == Color.GRAY ? "" : campoNombre.getText().trim();
        String telefono = campoTelefono.getForeground() == Color.GRAY ? "" : campoTelefono.getText().trim();
        String email = campoEmail.getForeground() == Color.GRAY ? "" : campoEmail.getText().trim();

        // Validación de campos obligatorios
        if (nombre.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nombre y teléfono son obligatorios",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validación estricta de teléfono (exactamente 9 dígitos)
        if (!telefono.matches("\\d{9}")) {
            JOptionPane.showMessageDialog(this,
                    "El teléfono debe tener exactamente 9 dígitos numéricos",
                    "Teléfono inválido",
                    JOptionPane.WARNING_MESSAGE);
            campoTelefono.requestFocus();
            return;
        }

        // Validación de email solo si se ha introducido algo (campo opcional)
        if (!email.isEmpty()) {
            if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                JOptionPane.showMessageDialog(this,
                        "Formato de email inválido. Debe contener @ y dominio válido\nEjemplo: usuario@dominio.com",
                        "Email inválido",
                        JOptionPane.WARNING_MESSAGE);
                campoEmail.requestFocus();
                return;
            }
        }

        // Resto de la lógica para guardar el cliente...
        boolean existeCliente = listaClientes.stream()
                .anyMatch(c -> c.getNombre().equalsIgnoreCase(nombre));

        if (existeCliente && JOptionPane.showConfirmDialog(this,
                "Ya existe un cliente con ese nombre. ¿Desea añadirlo de todos modos?",
                "Cliente duplicado", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }

        Dueno d = new Dueno(0, nombre, telefono, email.isEmpty() ? null : email);
        DuenoDAO.insertar(d);
        JOptionPane.showMessageDialog(this,
                "Cliente añadido correctamente",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);

        limpiarCampos();
        cargarClientes();
    }

    private void actualizarCliente() {
        Dueno d = (Dueno) comboClientes.getSelectedItem();
        if (d == null) return;

        String nuevoTelefono = campoTelefono.getForeground() == Color.GRAY ? "" : campoTelefono.getText().trim();
        String nuevoEmail = campoEmail.getForeground() == Color.GRAY ? "" : campoEmail.getText().trim();

        // Validación estricta de teléfono
        if (!nuevoTelefono.matches("\\d{9}")) {
            JOptionPane.showMessageDialog(this,
                    "El teléfono debe tener exactamente 9 dígitos numéricos",
                    "Teléfono inválido",
                    JOptionPane.WARNING_MESSAGE);
            campoTelefono.requestFocus();
            return;
        }

        // Validación de email solo si se ha introducido
        if (!nuevoEmail.isEmpty()) {
            if (!nuevoEmail.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                JOptionPane.showMessageDialog(this,
                        "Formato de email inválido. Debe contener @ y dominio válido\nEjemplo: usuario@dominio.com",
                        "Email inválido",
                        JOptionPane.WARNING_MESSAGE);
                campoEmail.requestFocus();
                return;
            }
        }

        d.setNombre(campoNombre.getForeground() == Color.GRAY ? "" : campoNombre.getText().trim());
        d.setTelefono(nuevoTelefono);
        d.setEmail(nuevoEmail.isEmpty() ? null : nuevoEmail);

        DuenoDAO.actualizar(d);
        JOptionPane.showMessageDialog(this,
                "Cliente actualizado",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
        cargarClientes();
    }

    private void confirmarEliminacion() {
        Dueno d = (Dueno) comboClientes.getSelectedItem();
        if (d == null) {
            JOptionPane.showMessageDialog(this,
                    "No hay ningún cliente seleccionado para eliminar",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Versión simplificada sin icono (para evitar problemas con la ruta)
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "<html><div style='font-size:14pt;'>¿Eliminar al cliente <b>" + d.getNombre() + "</b>?</div></html>",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            try {
                DuenoDAO.eliminar(d.getId());

                // Mensaje de éxito simplificado
                JOptionPane.showMessageDialog(this,
                        "Cliente eliminado correctamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

                limpiarCampos();
                cargarClientes();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error al eliminar el cliente: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}