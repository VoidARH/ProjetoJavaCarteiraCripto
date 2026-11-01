/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import model.carteira;
import model.Investidor;

/**
 *
 * @author void
 */

/** Extrato **/
public class carteiraDao {

    // ---- EXEMPLO COMPLETO -------------------------------------------------
    /**
     * Grava os saldos atuais da carteira E registra a operação no extrato,
     * tudo na mesma transação: ou salva as duas coisas, ou nenhuma.
     * Serve para deposito, saque, compra e venda.
     */
    public void salvarOperacao(Investidor inv, String sinal, double valor,
                               String moeda, double cotacao, double taxa) throws SQLException {
        carteira c = inv.getCarteira();
        String upd = "UPDATE carteira SET reais=?, bitcoin=?, ethereum=?, ripple=? WHERE cpf=?";
        String ins = "INSERT INTO operacao (cpf, sinal, valor, moeda, cotacao, taxa, "
                   + "saldo_reais, saldo_btc, saldo_eth, saldo_xrp) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = conexaoSql.abrir()) {
            con.setAutoCommit(false);
            try (PreparedStatement p1 = con.prepareStatement(upd);
                 PreparedStatement p2 = con.prepareStatement(ins)) {
                p1.setDouble(1, c.getReal().getSaldo());
                p1.setDouble(2, c.getBitcoin().getSaldo());
                p1.setDouble(3, c.getEthereum().getSaldo());
                p1.setDouble(4, c.getRipple().getSaldo());
                p1.setString(5, inv.getCpf());
                p1.executeUpdate();

                p2.setString(1, inv.getCpf());
                p2.setString(2, sinal);
                p2.setDouble(3, valor);
                p2.setString(4, moeda);
                p2.setDouble(5, cotacao);
                p2.setDouble(6, taxa);
                p2.setDouble(7, c.getReal().getSaldo());
                p2.setDouble(8, c.getBitcoin().getSaldo());
                p2.setDouble(9, c.getEthereum().getSaldo());
                p2.setDouble(10, c.getRipple().getSaldo());
                p2.executeUpdate();

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    // ---- PARA VOCÊ IMPLEMENTAR ---------------------------------------------
    // TODO: String montarExtrato(String cpf)
    //   SELECT * FROM operacao WHERE cpf = ? ORDER BY data_hora
    //   e monte cada linha no formato do enunciado (use String.format / SimpleDateFormat).
    // TODO: double buscarCotacao(String moeda)      -> SELECT valor FROM cotacao WHERE moeda = ?
    // TODO: void atualizarCotacao(String moeda, double novoValor)
}
