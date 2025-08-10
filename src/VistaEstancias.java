import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VistaEstancias extends JDialog {
    // Mantiene una referencia a la instancia visible para permitir refrescos en tiempo real
    public static VistaEstancias vistaActual;
    private JPanel grid;
    private int totalEstancias = Datos.plazasDisponibles;

    // Etiqueta para mostrar estadísticas (total, ocupadas y libres)
    private JLabel lblEstadisticas;

    public VistaEstancias(JFrame parent) {
        super(parent, "Panel de Estancias", true);
        // Registrar esta instancia como la actual
        vistaActual = this;
        // No establecemos tamaño fijo aquí. Ajustaremos a pantalla completa más adelante.
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 240, 240));

        // Panel superior con estadísticas, título y fecha
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(new Color(240, 240, 240));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Etiqueta de estadísticas en la parte izquierda
        lblEstadisticas = new JLabel();
        // Aumentamos el tamaño de la fuente para que las estadísticas se vean más claras
        lblEstadisticas.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblEstadisticas.setForeground(new Color(70, 130, 180));
        panelSuperior.add(lblEstadisticas, BorderLayout.WEST);

        // Título en el centro
        JLabel lblTitulo = new JLabel("Estado de Estancias", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(70, 130, 180));
        panelSuperior.add(lblTitulo, BorderLayout.CENTER);

        // Fecha en la parte derecha
        JLabel lblFecha = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy")),
                SwingConstants.RIGHT);
        // Aumentamos el tamaño de la fuente para la fecha
        lblFecha.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        panelSuperior.add(lblFecha, BorderLayout.EAST);

        add(panelSuperior, BorderLayout.NORTH);

        // Panel principal con margen
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(240, 240, 240));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        // Panel de estancias con scroll
        grid = new JPanel(new GridLayout(0, 6, 15, 15));
        grid.setBackground(new Color(240, 240, 240));

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        panelPrincipal.add(scroll, BorderLayout.CENTER);
        add(panelPrincipal, BorderLayout.CENTER);

        // Panel de botones (diseño consistente)
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panelBotones.setBackground(new Color(240, 240, 240));
        panelBotones.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));

        JButton btnActualizar = crearBoton("Actualizar", new Color(70, 130, 180), 16);
        JButton btnCambiarCap = crearBoton("Cambiar Capacidad", new Color(60, 179, 113), 16);
        JButton btnCerrar = crearBoton("Cerrar", new Color(100, 100, 100), 16);

        btnActualizar.addActionListener(e -> actualizarCuadros());
        btnCambiarCap.addActionListener(e -> cambiarCapacidad());
        btnCerrar.addActionListener(e -> dispose());

        panelBotones.add(btnActualizar);
        panelBotones.add(btnCambiarCap);
        panelBotones.add(btnCerrar);

        add(panelBotones, BorderLayout.SOUTH);

        actualizarCuadros();

        // Ajustar tamaño y comportamiento de la ventana:
        // Hacemos que el diálogo ocupe la mayor parte de la pantalla sin
        // superponerse a la barra de tareas u otros elementos del sistema.
        GraphicsConfiguration gc = getGraphicsConfiguration();
        Rectangle screenBounds = gc.getBounds();
        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
        int width = screenBounds.width - screenInsets.left - screenInsets.right;
        int height = screenBounds.height - screenInsets.top - screenInsets.bottom;
        setSize(width, height);
        setLocation(screenBounds.x + screenInsets.left, screenBounds.y + screenInsets.top);
    }

    private JButton crearBoton(String texto, Color color, int fontSize) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
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

    /**
     * Actualiza la cuadrícula de estancias. Se hace pública para permitir que otras
     * clases (por ejemplo SalidaEstancia) refresquen la vista en tiempo real tras
     * modificar una estancia.
     */
    public void actualizarCuadros() {
        grid.removeAll();

        LocalDate hoy = LocalDate.now();
        List<Estancia> hoyActivas = new ArrayList<>();
        // Conjunto para evitar duplicar mascotas en estancias activas
        java.util.Set<Integer> mascotasOcupadas = new java.util.HashSet<>();
        for (Estancia e : Datos.estancias) {
            // Incluir solamente si la estancia está activa hoy, no está pagada y la mascota aún no ha sido añadida
            if (!e.isPagado() && !hoy.isBefore(e.getFechaIngreso()) && !hoy.isAfter(e.getFechaSalida())) {
                if (!mascotasOcupadas.contains(e.getMascotaId())) {
                    hoyActivas.add(e);
                    mascotasOcupadas.add(e.getMascotaId());
                }
            }
        }

        for (int i = 0; i < totalEstancias; i++) {
            JPanel casilla = new JPanel(new BorderLayout());
            casilla.setPreferredSize(new Dimension(150, 120));
            casilla.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            if (i < hoyActivas.size()) {
                Estancia estancia = hoyActivas.get(i);
                Mascota m = MascotaDAO.buscarPorId(estancia.getMascotaId());

                if (m != null) {
                    // Estilo para estancia ocupada
                    casilla.setBackground(new Color(255, 230, 230));
                    casilla.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(220, 100, 100), 2),
                            BorderFactory.createEmptyBorder(10, 10, 10, 10)
                    ));

                    JLabel lblNombre = new JLabel(m.getNombre(), SwingConstants.CENTER);
                    lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    lblNombre.setForeground(new Color(150, 50, 50));

                    JLabel lblDueno = new JLabel("Dueño: " + m.getDueno().getNombre(), SwingConstants.CENTER);
                    lblDueno.setFont(new Font("Segoe UI", Font.PLAIN, 11));

                    long dias = ChronoUnit.DAYS.between(estancia.getFechaIngreso(), hoy) + 1;
                    JLabel lblDias = new JLabel("Día " + dias + " de estancia", SwingConstants.CENTER);
                    lblDias.setFont(new Font("Segoe UI", Font.ITALIC, 10));

                    JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
                    infoPanel.setOpaque(false);
                    infoPanel.add(lblNombre);
                    infoPanel.add(lblDueno);
                    infoPanel.add(lblDias);

                    casilla.add(infoPanel, BorderLayout.CENTER);

                    // Hacer clicable
                    casilla.addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                            mostrarDetalleEstancia(estancia);
                        }
                    });
                    casilla.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
            } else {
                // Estilo para estancia disponible
                casilla.setBackground(new Color(230, 255, 230));
                casilla.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(100, 180, 100), 2),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));

                JLabel lblLibre = new JLabel("DISPONIBLE", SwingConstants.CENTER);
                lblLibre.setFont(new Font("Segoe UI", Font.BOLD, 16));
                lblLibre.setForeground(new Color(50, 120, 50));
                casilla.add(lblLibre, BorderLayout.CENTER);
            }

            grid.add(casilla);
        }

        grid.revalidate();
        grid.repaint();

        // Actualizar estadísticas (total, ocupadas y libres)
        if (lblEstadisticas != null) {
            int ocupadas = hoyActivas.size();
            int libres = totalEstancias - ocupadas;
            lblEstadisticas.setText(
                    String.format("Total: %d  |  Ocupadas: %d  |  Libres: %d", totalEstancias, ocupadas, libres)
            );
        }
    }

    private void mostrarDetalleEstancia(Estancia e) {
        Mascota m = MascotaDAO.buscarPorId(e.getMascotaId());
        if (m == null) {
            JOptionPane.showMessageDialog(this,
                    "No se encontró la mascota asociada",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String pesoTexto = m.getPeso() != null ? String.format("%.1f kg", m.getPeso()) : "No indicado";
        long dias = ChronoUnit.DAYS.between(e.getFechaIngreso(), e.getFechaSalida()) + 1;
        double total = dias * e.getPrecioDia();

        // Diseño mejorado del diálogo de detalle
        JPanel panelDetalle = new JPanel(new BorderLayout(10, 10));
        panelDetalle.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Encabezado
        JPanel panelHeader = new JPanel(new BorderLayout());
        JLabel lblTitulo = new JLabel("Detalle de Estancia");
        // Aumentamos el tamaño de fuente para que el título sea más destacado
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(70, 130, 180));
        panelHeader.add(lblTitulo, BorderLayout.WEST);

        JLabel lblEstado = new JLabel(e.isPagado() ? "PAGADO" : "PENDIENTE");
        // Aumentamos el tamaño de fuente de la etiqueta de estado
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblEstado.setForeground(e.isPagado() ? new Color(50, 150, 50) : new Color(200, 50, 50));
        panelHeader.add(lblEstado, BorderLayout.EAST);

        panelDetalle.add(panelHeader, BorderLayout.NORTH);

        // Cuerpo
        JPanel panelInfo = new JPanel(new GridLayout(0, 2, 10, 5));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        agregarFila(panelInfo, "Mascota:", m.getNombre() + " (" + m.getRaza() + ")");
        agregarFila(panelInfo, "Edad/Peso:", String.format("%.1f años / %s", m.getEdad(), pesoTexto));
        agregarFila(panelInfo, "Dueño:", m.getDueno().getNombre());
        agregarFila(panelInfo, "Teléfono:", m.getDueno().getTelefono());
        agregarFila(panelInfo, "Fecha Ingreso:", e.getFechaIngreso().format(formato));
        agregarFila(panelInfo, "Fecha Salida:", e.getFechaSalida().format(formato));
        agregarFila(panelInfo, "Días totales:", String.valueOf(dias));
        agregarFila(panelInfo, "Precio por día:", String.format("€%.2f", e.getPrecioDia()));
        agregarFila(panelInfo, "Total a pagar:", String.format("€%.2f", total));

        panelDetalle.add(panelInfo, BorderLayout.CENTER);

        // Mostrar diálogo
        JOptionPane.showOptionDialog(
                this,
                panelDetalle,
                "Detalle de Estancia",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                new Object[]{},
                null
        );
    }

    private void agregarFila(JPanel panel, String etiqueta, String valor) {
        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(lblEtiqueta);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        panel.add(lblValor);
    }

    private void cambiarCapacidad() {
        JPanel panelInput = new JPanel(new BorderLayout(10, 10));
        panelInput.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Calcular estancias ocupadas hoy
        LocalDate hoy = LocalDate.now();
        long estanciasOcupadas = Datos.estancias.stream()
                .filter(e -> !hoy.isBefore(e.getFechaIngreso()) && !hoy.isAfter(e.getFechaSalida()))
                .count();

        JLabel lblInfo = new JLabel("<html>Capacidad actual: " + totalEstancias + " estancias<br>" +
                "Estancias ocupadas hoy: " + estanciasOcupadas + "</html>");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panelInput.add(lblInfo, BorderLayout.NORTH);

        JTextField txtCapacidad = new JTextField(String.valueOf(totalEstancias));
        txtCapacidad.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtCapacidad.setHorizontalAlignment(JTextField.CENTER);
        panelInput.add(txtCapacidad, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
                this,
                panelInput,
                "Cambiar Capacidad",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                int nuevaCapacidad = Integer.parseInt(txtCapacidad.getText());

                // Validaciones
                if (nuevaCapacidad < 1) {
                    throw new NumberFormatException("La capacidad debe ser al menos 1");
                }

                if (nuevaCapacidad < estanciasOcupadas) {
                    throw new IllegalArgumentException("No puede reducir la capacidad por debajo de " + estanciasOcupadas +
                            " porque hay estancias ocupadas actualmente");
                }

                totalEstancias = nuevaCapacidad;
                Datos.plazasDisponibles = totalEstancias;

                // Guardar la nueva capacidad para que se persista entre sesiones
                Datos.guardarCapacidad();

                actualizarCuadros();

                JOptionPane.showMessageDialog(this,
                        "Capacidad actualizada exitosamente a " + totalEstancias + " estancias",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Debe ingresar un número válido mayor a 0",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this,
                        e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}