package controller;

import dao.investidorDao;
import java.sql.SQLException;
import model.Investidor;

/**
 * Login do investidor. Valida o formato do CPF e da senha, consulta o banco e,
 * se estiver tudo certo, entrega o controller da sessão.
 */
public class loginController {

    private final investidorDao dao = new investidorDao();

    /**
     * @param cpf   CPF digitado (11 números)
     * @param senha senha digitada (6 números)
     * @return o controller do investidor que entrou (as telas usam só ele, sem tocar no model)
     * @throws OperacaoException se os dados forem inválidos ou o login falhar
     */
    public carteiraController autenticar(String cpf, String senha) {
        if (!cpf.matches("\\d{11}")) {
            throw new OperacaoException("CPF deve ter 11 números, sem pontos e traços.");
        }
        if (!senha.matches("\\d{6}")) {
            throw new OperacaoException("A senha deve ter 6 dígitos numéricos.");
        }
        try {
            Investidor inv = this.dao.buscarPorCpf(cpf);
            if (inv == null || !inv.senhaCorreta(senha)) {
                throw new OperacaoException("CPF ou senha inválidos.");
            }
            return new carteiraController(inv);
        } catch (SQLException e) {
            throw OperacaoException.erroBanco(e);
        }
    }
}
