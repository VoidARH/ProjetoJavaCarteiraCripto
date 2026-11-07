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
        String sql = "SELECT valor_reais FROM cotacao WHERE moeda = ?";
        try (Connection con = conexaoSql.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, moeda);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Moeda sem cotação cadastrada: " + moeda);
                }
                return rs.getDouble("valor_reais");
            }
        }
    }

    /**
     * Grava o novo valor de várias moedas de uma vez, na mesma transação:
     * ou todas são atualizadas, ou nenhuma.
     *
     * @param moedas siglas das moedas
     * @param valores novos valores em reais (mesma ordem de {@code moedas})
     * @throws SQLException se der erro no banco
     */
    public void salvarTodas(String[] moedas, double[] valores) throws SQLException {
        String sql = "UPDATE cotacao SET valor_reais = ? WHERE moeda = ?";
        try (Connection con = conexaoSql.abrir()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                for (int i = 0; i < moedas.length; i++) {
                    ps.setDouble(1, valores[i]);
                    ps.setString(2, moedas[i]);
                    ps.executeUpdate();
                }
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }
}
