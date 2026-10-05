import javax.swing.*;

/**
 * Punto de entrada del juego 2D tipo Minecraft.
 * Arranca el hilo principal y crea la ventana del juego.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Game game = new Game();
            game.iniciar();
        });
    }
}
