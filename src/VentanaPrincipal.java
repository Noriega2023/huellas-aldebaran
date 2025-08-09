import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VentanaPrincipal extends JFrame {

    private final Color[] coloresBotones = {
            new Color(70, 130, 180),    // Azul acero - Clientes
            new Color(60, 179, 113),    // Verde mar - Mascotas
            new Color(205, 92, 92),     // Rojo indio - Realizar Reserva
            new Color(186, 85, 211),    // Violeta medio - Consultar Reservas
            new Color(255, 165, 0),     // Naranja - Lista Clientes/Mascotas
            new Color(46, 139, 87),     // Verde mar oscuro - Ingreso Mascota
            new Color(100, 149, 237),   // Azul cielo - Dar Salida/Ampliar
            new Color(255, 105, 180)    // Rosa - Vista de Estancias
    };

    public VentanaPrincipal() {
        setTitle("Huellas de Aldebarán - Gestión de Residencia Canina");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(new FondoPanel());

        // Layout principal con la mitad superior vacía
        setLayout(new BorderLayout());

        // Panel para ocupar la mitad superior (vacío)
        JPanel panelSuperior = new JPanel();
        panelSuperior.setOpaque(false);
        panelSuperior.setPreferredSize(new Dimension(getWidth(), getHeight()/2));
        add(panelSuperior, BorderLayout.CENTER);

        // Panel para los botones en la parte inferior
        JPanel panelBotones = new JPanel(new GridBagLayout());
        panelBotones.setOpaque(false);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] opciones = {
                "Clientes", "Mascotas", "Realizar Reserva",
                "Consultar Reservas", "Lista Clientes/Mascotas", "Ingreso Mascota",
                "Dar Salida / Ampliar", "Vista de Estancias"
        };

        // Primera fila de botones
        gbc.gridy = 0;
        for (int i = 0; i < 4; i++) {
            gbc.gridx = i;
            JButton boton = crearBotonGrande(opciones[i], coloresBotones[i]);
            boton.addActionListener(crearActionListener(opciones[i]));
            panelBotones.add(boton, gbc);
        }

        // Segunda fila de botones
        gbc.gridy = 1;
        for (int i = 4; i < 8; i++) {
            gbc.gridx = i-4;
            JButton boton = crearBotonGrande(opciones[i], coloresBotones[i]);
            boton.addActionListener(crearActionListener(opciones[i]));

            // Posicionar "Vista de Estancias" más a la derecha
            if (opciones[i].equals("Vista de Estancias")) {
                gbc.gridwidth = 2;
                gbc.anchor = GridBagConstraints.EAST;
            } else {
                gbc.gridwidth = 1;
                gbc.anchor = GridBagConstraints.CENTER;
            }

            panelBotones.add(boton, gbc);
        }

        add(panelBotones, BorderLayout.SOUTH);
    }

    private JButton crearBotonGrande(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setPreferredSize(new Dimension(250, 80));
        boton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(color);
            }
        });

        return boton;
    }

    private ActionListener crearActionListener(String texto) {
        return e -> {
            switch (texto) {
                case "Clientes":
                    new GestionClientes(this).setVisible(true);
                    break;
                case "Mascotas":
                    if (DuenoDAO.listar().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Primero debes añadir un dueño.");
                    } else {
                        new GestionMascotas(this).setVisible(true);
                    }
                    break;
                case "Realizar Reserva":
                    if (MascotaDAO.listarTodos().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Primero debes añadir una mascota.");
                    } else {
                        new FormularioReserva(this).setVisible(true);
                    }
                    break;
                case "Consultar Reservas":
                    new ConsultaReservas(this).setVisible(true);
                    break;
                case "Lista Clientes/Mascotas":
                    new GestionClientesMascotas(this).setVisible(true);
                    break;
                case "Ingreso Mascota":
                    if (MascotaDAO.listarTodos().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Primero debes añadir una mascota.");
                    } else {
                        new FormularioEstancia(this).setVisible(true);
                    }
                    break;
                case "Dar Salida / Ampliar":
                    if (Datos.estancias.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "No hay estancias activas.");
                    } else {
                        new SalidaEstancia(this).setVisible(true);
                    }
                    break;
                case "Vista de Estancias":
                    new VistaEstancias(this).setVisible(true);
                    break;
            }
        };
    }

    class FondoPanel extends JPanel {
        private final Image fondo = new ImageIcon("resources/Huellas.png").getImage();

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);

            // Capa semitransparente solo en la parte inferior
            g.setColor(new Color(255, 255, 255, 180));
            g.fillRect(0, getHeight()/2, getWidth(), getHeight()/2);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}