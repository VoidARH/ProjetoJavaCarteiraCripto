/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.carteira;
import model.Investidor;

/**
 * Acesso ao banco para investidores (tabela "usuario" do template, com tipo = 'investidor').
 *
 * @author void
 */
public class investidorDao {

    /**
     * Busca um investidor (e os saldos da carteira) pelo CPF.
     *
     * @param cpf CPF só com números
     * @return o investidor, ou null se não existir
     * @throws SQLException se der erro no banco
     */
    public Investidor buscarPorCpf(String cpf) throws SQLException {
        String sql = "SELECT nome, cpf, senha, saldo_reais, saldo_bitcoin, saldo_ethereum, saldo_ripple "
           + "FROM usuario WHERE TRIM(cpf) = ? AND LOWER(TRIM(COALESCE(tipo, ''))) <> 'administrador'";
        try (Connection con = conexaoSql.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cpf);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                carteira cart = new carteira(rs.getDouble("saldo_reais"), rs.getDouble("saldo_bitcoin"),
                                             rs.getDouble("saldo_ethereum"), rs.getDouble("saldo_ripple"));
                return new Investidor(rs.getString("nome"), rs.getString("cpf"),
                                      rs.getString("senha"), cart);
            }
        }
    }
}
