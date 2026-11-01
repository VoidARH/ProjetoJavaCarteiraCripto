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
 *
 * @author void
 */

/** Acesso ao banco para investidores. **/
public class insvestidorDao {

    public Investidor buscarPorCpf(String cpf) throws SQLException {
        String sql = "SELECT i.nome, i.cpf, i.senha, c.reais, c.bitcoin, c.ethereum, c.ripple "
                   + "FROM investidor i JOIN carteira c ON c.cpf = i.cpf WHERE i.cpf = ?";
        try (Connection con = conexaoSql.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cpf);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                carteira cart = new carteira(rs.getDouble("reais"), rs.getDouble("bitcoin"),
                                             rs.getDouble("ethereum"), rs.getDouble("ripple"));
                return new Investidor(rs.getString("nome"), rs.getString("cpf"),
                                      rs.getString("senha"), cart);
            }
        }
    }

    // ---- PARA VOCÊ IMPLEMENTAR (só se for dupla) ---------------------------
    // TODO: inserir(Investidor)  -> INSERT em investidor + INSERT em carteira (uma transação)
    // TODO: excluir(String cpf)  -> DELETE FROM investidor WHERE cpf = ?
}
