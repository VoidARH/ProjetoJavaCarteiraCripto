/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import model.Investidor;
import model.carteira;

/**
 * Acesso ao banco para os saldos da carteira (tabela "usuario") e para o
 * extrato de operações (tabela "operacao").
 *
 * @author void
 */
public class carteiraDao {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    /**
     * Grava os saldos atuais da carteira E registra a operação no extrato,
     * tudo na mesma transação: ou salva as duas coisas, ou nenhuma.
     * Serve para depósito, saque, compra e venda.
     *
     * @param inv investidor dono da carteira (já com os saldos novos)
     * @param sinal "+" (entrou na carteira) ou "-" (saiu da carteira)
     * @param quantidade quantidade da moeda (em reais no depósito/saque)
     * @param moeda REAL, BTC, ETH ou XRP
     * @param cotacao cotação unitária usada (0 para REAL)
     * @param taxa percentual da taxa aplicada (ex.: 0.02), 0 se não houver
     * @param valorReais valor total da operação em reais (já com a taxa)
     * @throws SQLException se der erro no banco
     */
    public void salvarOperacao(Investidor inv, String sinal, double quantidade,
                               String moeda, double cotacao, double taxa,
                               double valorReais) throws SQLException {
        carteira c = inv.getCarteira();
        String upd = "UPDATE usuario SET saldo_reais=?, saldo_bitcoin=?, saldo_ethereum=?, saldo_ripple=? "
                   + "WHERE cpf=?";
        String ins = "INSERT INTO operacao (cpf_usuario, data_hora, tipo, moeda, quantidade, "
                   + "cotacao_unitaria, taxa, valor_reais, saldo_reais, saldo_bitcoin, saldo_ethereum, "
                   + "saldo_ripple) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = conexaoSql.abrir()) {
            con.setAutoCommit(false);
            try (PreparedStatement p1 = con.prepareStatement(upd);
                 PreparedStatement p2 = con.prepareStatement(ins)) {
                p1.setDouble(1, c.getReal().getSaldo());
                p1.setDouble(2, c.getBitcoin().getSaldo());
                p1.setDouble(3, c.getEthereum().getSaldo());
                p1.setDouble(4, c.getRipple().getSaldo());
                p1.setString(5, inv.getCpf());
                if (p1.executeUpdate() != 1) {
                    throw new SQLException("Investidor não encontrado: " + inv.getCpf());
                }

                p2.setString(1, inv.getCpf());
                p2.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                p2.setString(3, sinal);
                p2.setString(4, moeda);
                p2.setDouble(5, quantidade);
                p2.setDouble(6, cotacao);
                p2.setDouble(7, taxa);
                p2.setDouble(8, valorReais);
                p2.setDouble(9, c.getReal().getSaldo());
                p2.setDouble(10, c.getBitcoin().getSaldo());
                p2.setDouble(11, c.getEthereum().getSaldo());
                p2.setDouble(12, c.getRipple().getSaldo());
                p2.executeUpdate();

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    /**
     * Monta o extrato do investidor, uma linha por operação, na ordem em que
     * aconteceram, no formato do enunciado. Exemplo:
     * {@code 01-10-2026 13:00 + 100.00 XRP CT: 2.57 TX: 0.01 REAL: 399.00 BTC: 0.0 ETH: 0.0 XRP: 38.9}
     *
     * @param cpf CPF do investidor
     * @return as linhas do extrato (texto vazio se não houver operações)
     * @throws SQLException se der erro no banco
     */
    public String montarExtrato(String cpf) throws SQLException {
        String sql = "SELECT data_hora, tipo, moeda, quantidade, cotacao_unitaria, taxa, "
                   + "saldo_reais, saldo_bitcoin, saldo_ethereum, saldo_ripple "
                   + "FROM operacao WHERE cpf_usuario = ? ORDER BY data_hora, id";
        StringBuilder sb = new StringBuilder();
        try (Connection con = conexaoSql.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cpf);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String moeda = rs.getString("moeda");
                    double qtd = rs.getDouble("quantidade");
                    String qtdTxt = "REAL".equals(moeda)
                            ? String.format(Locale.US, "%.2f", qtd) : numero(qtd, 2);
                    sb.append(String.format(Locale.US,
                            "%s %s %s %-4s CT: %s TX: %.2f REAL: %.2f BTC: %s ETH: %s XRP: %s%n",
                            rs.getTimestamp("data_hora").toLocalDateTime().format(DATA),
                            rs.getString("tipo"), qtdTxt, moeda,
                            numero(rs.getDouble("cotacao_unitaria")),
                            rs.getDouble("taxa"),
                            rs.getDouble("saldo_reais"),
                            numero(rs.getDouble("saldo_bitcoin")),
                            numero(rs.getDouble("saldo_ethereum")),
                            numero(rs.getDouble("saldo_ripple"))));
                }
            }
        }
        return sb.toString();
    }

    /** Número sem zeros sobrando, mas sempre com pelo menos 1 casa (0.0, 38.9, 0.006). */
    private static String numero(double v) {
        return numero(v, 1);
    }

    /** Número sem zeros sobrando, mas com no mínimo {@code casasMin} casas (100.00, 0.006). */
    private static String numero(double v, int casasMin) {
        BigDecimal b = BigDecimal.valueOf(v).setScale(8, RoundingMode.HALF_UP).stripTrailingZeros();
        if (b.scale() < casasMin) b = b.setScale(casasMin);
        return b.toPlainString();
    }
}
