import javax.swing.*;

public class Main {
    private static final String DB_PATH = "residencia.db";

    public static void main(String[] args) {
        // Inicializar BD
        InicializadorBD.crearTablas();

        // Cargar estancias desde la base de datos
        Datos.estancias = EstanciaDAO.listar();

        // Registrar shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Aplicación cerrada");
        }));

        // Intentar hacer backup
        boolean backupRealizado = SQLiteBackupHelper.hacerBackupDiario(DB_PATH);

        // Mostrar interfaz
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();

            if (backupRealizado) {
                mostrarMensajeBackup(ventana);
            }

            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setVisible(true);
        });
    }

    private static void mostrarMensajeBackup(JFrame parent) {
        String nombreDia = SQLiteBackupHelper.obtenerNombreDiaActual();
        JOptionPane.showMessageDialog(parent,
                "Copia de seguridad (" + nombreDia + ") realizada correctamente.\n" +
                        "Archivo: backup_" + nombreDia + ".db",
                "Backup completado",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
