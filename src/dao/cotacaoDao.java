package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Acesso ao banco para as cotações das criptomoedas (tabela "cotacao").
 * Cada moeda tem uma linha: BTC, ETH ou XRP.
 */
public class cotacaoDao {

    /**
     * Lê a cotação atual de uma moeda.
     *
     * @param moeda sigla da moeda (BTC, ETH ou XRP)
     * @return valor em reais de 1 unidade da moeda
     * @throws SQLException se der erro no banco ou a moeda não existir na tabela
     */
    public double buscar(String moeda) throws SQLException {
        String sql = "SELECT valor FROM cotacao WHERE moeda = ?";
        try (Connection con = conexaoSql.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, moeda);                       // troca o "?" pela sigla
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {                         // nenhuma linha encontrada
                    throw new SQLException("Moeda sem cotação cadastrada: " + moeda);
                }
                return rs.getDouble("valor");
            }
        }
    }

    /**
     * Grava o novo valor de uma moeda.
     *
     * @param moeda sigla da moeda
     * @param valor novo valor em reais
     * @throws SQLException se der erro no banco
     */
    public void salvar(String moeda, double valor) throws SQLException {
        String sql = "UPDATE cotacao SET valor = ? WHERE moeda = ?";
        try (Connection con = conexaoSql.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, valor);
            ps.setString(2, moeda);
            ps.executeUpdate();
        }
    }
}
