/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package controller;

import dao.carteiraDao;
import java.sql.SQLException;
import model.Investidor;

/**
 *
 * @author void
 */

/** Operaçoes da carteira **/
public class carteiraController {
    private final Investidor investidor;
    private final carteiraDao dao = new carteiraDao();

    public carteiraController(Investidor investidor) { this.investidor = investidor; }

    /** true no deposito e false em valor invalido **/
    public boolean depositar(double valor) throws SQLException {
        if (!investidor.getCarteira().depositar(valor)) return false;
        dao.salvarOperacao(investidor, "+", valor, "REAL", 0.0, 0.0);
        return true;
    }

    /** Saldo no formato do enunciado (tambem serve para saldos atualizados) **/
    public String textoSaldo() {
        var c = investidor.getCarteira();
        return String.format("Nome: %s%nCPF: %s%n%nReais: %.2f%nBitcoin: %.4f%nEthereum: %.4f%nRipple: %.4f",
                investidor.getNome(), investidor.getCpf(), c.getReal().getSaldo(),
                c.getBitcoin().getSaldo(), c.getEthereum().getSaldo(), c.getRipple().getSaldo());
    }

    /** Pede a senha de novo (saldo, extrato, compra e venda exigem) **/
    public boolean senhaConfere(String senha) { return investidor.senhaCorreta(senha); }

    // ---- PARA VOCÊ IMPLEMENTAR ---------------------------------------------
    // TODO: boolean sacar(double valor)           -> carteira.sacar + dao.salvarOperacao("-", ...)
    // TODO: String comprar(String moeda, double valorReais)
    // TODO: String vender(String moeda, double quantidade)
    // TODO: String consultarExtrato()             -> cabeçalho (Nome/CPF) + dao.montarExtrato
    // TODO: void atualizarCotacao()               -> novo = antigo * (1 + (random*0.10 - 0.05)) para as 3 moedas
}
