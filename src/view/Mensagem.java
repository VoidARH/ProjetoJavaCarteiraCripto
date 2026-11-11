package view;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 * Caixas de mensagem (showMessageDialog) usadas por todas as telas.
 * Fica num só lugar para as telas não repetirem o mesmo código.
 */
public class Mensagem {

    /** Mensagem de erro, com o ícone vermelho. */
    public static void erro(Component tela, String texto) {
        JOptionPane.showMessageDialog(tela, texto, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    /** Mensagem de erro com título personalizado (ex.: "Erro de login"). */
    public static void erro(Component tela, String texto, String titulo) {
        JOptionPane.showMessageDialog(tela, texto, titulo, JOptionPane.ERROR_MESSAGE);
    }

    /** Mensagem informativa, com botão OK. */
    public static void info(Component tela, String texto, String titulo) {
        JOptionPane.showMessageDialog(tela, texto, titulo, JOptionPane.INFORMATION_MESSAGE);
    }
}
