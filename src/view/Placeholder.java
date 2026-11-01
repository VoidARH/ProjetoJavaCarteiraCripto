package view;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JPasswordField;
import javax.swing.text.JTextComponent;

/**
 * Texto de dica (placeholder) para JTextField e JPasswordField.
 * Aparece em cinza, some ao clicar no campo e volta se o campo ficar vazio.
 */
public class Placeholder {

    private static final String ATIVO = "placeholder.ativo";

    public static void aplicar(JTextComponent campo, String dica) {
        final Color corNormal = campo.getForeground();
        final boolean senha = campo instanceof JPasswordField;
        final char mascara = senha ? ((JPasswordField) campo).getEchoChar() : 0;

        mostrarDica(campo, dica, senha);

        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (estaComDica(campo)) {
                    campo.setText("");
                    campo.setForeground(corNormal);
                    if (senha) {
                        ((JPasswordField) campo).setEchoChar(mascara);
                    }
                    campo.putClientProperty(ATIVO, false);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (campo.getText().isEmpty()) {
                    mostrarDica(campo, dica, senha);
                }
            }
        });
    }

    private static void mostrarDica(JTextComponent campo, String dica, boolean senha) {
        campo.setText(dica);
        campo.setForeground(Color.GRAY);
        if (senha) {
            ((JPasswordField) campo).setEchoChar((char) 0); // mostra o texto, sem bolinhas
        }
        campo.putClientProperty(ATIVO, true);
    }

    private static boolean estaComDica(JTextComponent campo) {
        return Boolean.TRUE.equals(campo.getClientProperty(ATIVO));
    }

    /** Devolve o que a pessoa digitou, ou "" se ainda estiver a dica. */
    public static String getTexto(JTextComponent campo) {
        if (estaComDica(campo)) {
            return "";
        }
        if (campo instanceof JPasswordField) {
            return new String(((JPasswordField) campo).getPassword());
        }
        return campo.getText();
    }
}
