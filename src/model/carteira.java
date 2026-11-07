/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 * Carteira de investimentos: guarda o saldo em reais e nas três criptomoedas.
 * Aqui ficam as regras de negócio (o saldo nunca pode ficar negativo).
 *
 * @author void
 */
public class carteira {
    private final Real real;
    private final Bitcoin bitcoin;
    private final Ethereum ethereum;
    private final Ripple ripple;

    /**
     * Cria a carteira com os saldos informados.
     *
     * @param reais saldo em reais
     * @param btc saldo em bitcoin
     * @param eth saldo em ethereum
     * @param xrp saldo em ripple
     */
    public carteira(double reais, double btc, double eth, double xrp) {
        this.real = new Real(reais);
        this.bitcoin = new Bitcoin(btc);
        this.ethereum = new Ethereum(eth);
        this.ripple = new Ripple(xrp);
    }

    /** @return saldo em reais */
    public Real getReal()         { return real; }
    /** @return saldo em bitcoin */
    public Bitcoin getBitcoin()   { return bitcoin; }
    /** @return saldo em ethereum */
    public Ethereum getEthereum() { return ethereum; }
    /** @return saldo em ripple */
    public Ripple getRipple()     { return ripple; }

    /**
     * Arredonda um valor em reais para centavos.
     *
     * @param v valor
     * @return valor com 2 casas decimais
     */
    public static double arredondarReais(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    /**
     * Arredonda uma quantidade de criptomoeda para 8 casas (precisão do banco).
     *
     * @param v quantidade
     * @return quantidade com 8 casas decimais
     */
    public static double arredondarCripto(double v) {
        return Math.round(v * 1e8) / 1e8;
    }

    /**
     * Deposita reais na carteira.
     *
     * @param valor valor do depósito (precisa ser maior que zero)
     * @return true se depositou, false se o valor for inválido
     */
    public boolean depositar(double valor) {
        if (!(valor > 0)) return false;
        real.setSaldo(arredondarReais(real.getSaldo() + valor));
        return true;
    }

    /**
     * Saca reais da carteira.
     *
     * @param valor valor do saque (maior que zero e no máximo o saldo)
     * @return true se sacou, false se o valor for inválido ou maior que o saldo
     */
    public boolean sacar(double valor) {
        if (!(valor > 0) || valor > real.getSaldo()) return false;
        real.setSaldo(arredondarReais(real.getSaldo() - valor));
        return true;
    }

    /**
     * Valor em reais da compra sem a taxa (quantidade x cotação).
     *
     * @param quantidade quantidade de moeda
     * @param cotacao valor em reais de 1 unidade
     * @return valor bruto em reais
     */
    public static double valorBruto(double quantidade, double cotacao) {
        return arredondarReais(quantidade * cotacao);
    }

    /**
     * Taxa em reais cobrada sobre um valor bruto.
     *
     * @param valorBruto valor bruto em reais
     * @param taxa percentual da taxa (ex.: 0.02 para 2%)
     * @return taxa em reais
     */
    public static double valorTaxa(double valorBruto, double taxa) {
        return arredondarReais(valorBruto * taxa);
    }

    /**
     * Compra criptomoeda: debita (quantidade x cotação + taxa de compra) dos reais.
     *
     * @param moeda moeda comprada (Bitcoin, Ethereum ou Ripple)
     * @param quantidade quantidade de moeda a comprar
     * @param cotacao cotação atual em reais
     * @return true se comprou; false se a quantidade for inválida, a moeda não
     *         for negociável ou o saldo em reais não cobrir o valor + taxa
     */
    public boolean comprar(Moedas moeda, double quantidade, double cotacao) {
        if (!(moeda instanceof Tarifacao t) || !(quantidade > 0) || !(cotacao > 0)) return false;
        double bruto = valorBruto(quantidade, cotacao);
        double total = arredondarReais(bruto + valorTaxa(bruto, t.taxaCompra()));
        if (total > real.getSaldo()) return false;
        real.setSaldo(arredondarReais(real.getSaldo() - total));
        moeda.setSaldo(arredondarCripto(moeda.getSaldo() + quantidade));
        return true;
    }

    /**
     * Vende criptomoeda: credita (quantidade x cotação - taxa de venda) nos reais.
     *
     * @param moeda moeda vendida (Bitcoin, Ethereum ou Ripple)
     * @param quantidade quantidade de moeda a vender
     * @param cotacao cotação atual em reais
     * @return true se vendeu; false se a quantidade for inválida, a moeda não
     *         for negociável ou o saldo da moeda não cobrir a quantidade
     */
    public boolean vender(Moedas moeda, double quantidade, double cotacao) {
        if (!(moeda instanceof Tarifacao t) || !(quantidade > 0) || !(cotacao > 0)) return false;
        if (quantidade > moeda.getSaldo()) return false;
        double bruto = valorBruto(quantidade, cotacao);
        double liquido = arredondarReais(bruto - valorTaxa(bruto, t.taxaVenda()));
        moeda.setSaldo(arredondarCripto(moeda.getSaldo() - quantidade));
        real.setSaldo(arredondarReais(real.getSaldo() + liquido));
        return true;
    }
}
