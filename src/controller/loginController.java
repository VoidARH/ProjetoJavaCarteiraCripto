/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package controller;

import dao.insvestidorDao;
import java.sql.SQLException;
import model.Investidor;

/**
 *
 * @author void
 */

/** Login, a tela so chama este metodo e mostra a mensagem **/
public class loginController {
    private final insvestidorDao dao = new insvestidorDao();

    public Investidor autenticar(String cpf, String senha) {
        if (!cpf.matches("\\d{11}"))
            throw new IllegalArgumentException("CPF deve ter 11 números, sem pontos e traços.");
        if (!senha.matches("\\d{6}"))
            throw new IllegalArgumentException("A senha deve ter 6 dígitos numéricos.");
        try {
            Investidor inv = dao.buscarPorCpf(cpf);
            if (inv == null || !inv.senhaCorreta(senha))
                throw new IllegalArgumentException("CPF ou senha inválidos.");
            return inv;
        } catch (SQLException e) {
            throw new IllegalArgumentException("Erro no banco de dados: " + e.getMessage());
        }
    }
}
