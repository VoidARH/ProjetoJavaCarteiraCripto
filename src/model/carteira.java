/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package model;

/**
 *
 * @author void
 */

/** Carteira de investimentos: guarda um saldo para cada moeda (composicao no UML). ***/
public class carteira {
    private final Real real;
    private final Bitcoin bitcoin;
    private final Ethereum ethereum;
    private final Ripple ripple;

    public carteira(double reais, double btc, double eth, double xrp) {
        this.real = new Real(reais);
        this.bitcoin = new Bitcoin(btc);
        this.ethereum = new Ethereum(eth);
        this.ripple = new Ripple(xrp);
    }

    public Real getReal()         { return real; }
    public Bitcoin getBitcoin()   { return bitcoin; }
    public Ethereum getEthereum() { return ethereum; }
    public Ripple getRipple()     { return ripple; }

    /** Soma o valor em real. false se o valor nao for positivo. **/
    public boolean depositar(double valor) {
        if (valor <= 0) return false;
        real.setSaldo(real.getSaldo() + valor);
        return true;
    }

    // ---- PARA VOCÊ IMPLEMENTAR ---------------------------------------------
    /** TODO: subtrair dos reais; NÃO pode deixar o saldo negativo (devolva false nesse caso). */
    public boolean sacar(double valor) {
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * TODO: comprar uma cripto gastando 'valorReais'.
     * Dicas: taxa = valorReais * moeda.taxaCompra(); total debitado = valorReais + taxa
     * (ou só valorReais, escolha e documente); quantidade = valorReais / cotacao;
     * confira se há reais suficientes antes de alterar qualquer saldo.
     */
    public boolean comprar(Moedas moeda, double valorReais, double cotacao) {
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * TODO: vender 'quantidade' de uma cripto.
     * Dicas: bruto = quantidade * cotacao; taxa = bruto * moeda.taxaVenda();
     * credita (bruto - taxa) em reais; confira se há quantidade suficiente.
     */
    public boolean vender(Moedas moeda, double quantidade, double cotacao) {
        throw new UnsupportedOperationException("TODO");
    }
}
