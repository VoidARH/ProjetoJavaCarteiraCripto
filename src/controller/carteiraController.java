/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package controller;

import dao.carteiraDao;
import dao.cotacaoDao;
import java.sql.SQLException;
import java.util.Locale;
import model.Investidor;
import model.Moedas;
import model.Tarifacao;
import model.carteira;

/**
 * Operações da carteira do investidor logado: depositar, sacar, comprar,
 * vender e consultar saldo/extrato. As telas só chamam estes métodos.
 * <p>
 * Convenção: quando uma regra de negócio é violada (saldo insuficiente, valor
 * inválido...) lança {@link IllegalArgumentException} com a mensagem para o
 * usuário; erros de banco saem como {@link SQLException}.
 *
 * @author void
 */
public class carteiraController {
    private static final Locale BR = Locale.forLanguageTag("pt-BR");
    /** Maior saldo em reais aceito (a coluna do banco é NUMERIC(15,2)). */
    private static final double SALDO_MAXIMO = 1e12;

    private final Investidor investidor;
    private final carteiraDao dao = new carteiraDao();
    private final cotacaoDao cotDao = new cotacaoDao();

    /**
     * @param investidor investidor logado
     */
    public carteiraController(Investidor investidor) { this.investidor = investidor; }

    /**
     * Converte o texto digitado em número, aceitando vírgula ou ponto.
     *
     * @param texto texto do campo (ex.: "10,50" ou "10.5")
     * @return o valor digitado
     * @throws IllegalArgumentException se não for um número válido
     */
    public static double lerValor(String texto) {
        try {
            double v = Double.parseDouble(texto.trim().replace(',', '.'));
            if (Double.isNaN(v) || Double.isInfinite(v)) throw new NumberFormatException();
            return v;
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException("Digite um número válido.");
        }
    }

    /**
     * Deposita reais.
     *
     * @param valor valor do depósito (maior que zero)
     * @return saldos atualizados, no formato do enunciado
     * @throws SQLException se der erro no banco
     */
    public String depositar(double valor) throws SQLException {
        carteira c = investidor.getCarteira();
        if (!(valor > 0)) throw new IllegalArgumentException("O valor do depósito deve ser maior que zero.");
        if (c.getReal().getSaldo() + valor >= SALDO_MAXIMO)
            throw new IllegalArgumentException("Valor muito alto.");
        double antes[] = copiarSaldos();
        valor = carteira.arredondarReais(valor);
        if (!(valor > 0)) throw new IllegalArgumentException("O valor do depósito deve ser de pelo menos R$ 0,01.");
        c.depositar(valor);
        persistir(antes, "+", valor, "REAL", 0.0, 0.0, valor);
        return textoSaldo();
    }

    /**
     * Saca reais (o saldo não pode ficar negativo).
     *
     * @param valor valor do saque (maior que zero e até o saldo)
     * @return saldos atualizados, no formato do enunciado
     * @throws SQLException se der erro no banco
     */
    public String sacar(double valor) throws SQLException {
        carteira c = investidor.getCarteira();
        valor = carteira.arredondarReais(valor);
        if (!(valor > 0)) throw new IllegalArgumentException("O valor do saque deve ser de pelo menos R$ 0,01.");
        double antes[] = copiarSaldos();
        if (!c.sacar(valor)) throw new IllegalArgumentException("Saldo insuficiente para o saque.");
        persistir(antes, "-", valor, "REAL", 0.0, 0.0, valor);
        return textoSaldo();
    }

    /**
     * Compra criptomoeda, cobrando a taxa de compra.
     *
     * @param sigla BTC, ETH ou XRP
     * @param quantidade quantidade de moeda a comprar
     * @return saldos atualizados, no formato do enunciado
     * @throws SQLException se der erro no banco
     */
    public String comprar(String sigla, double quantidade) throws SQLException {
        carteira c = investidor.getCarteira();
        Moedas moeda = moedaDe(sigla);
        quantidade = carteira.arredondarCripto(quantidade);
        if (!(quantidade > 0)) throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        double cotacao = cotDao.buscar(sigla);
        double taxa = ((Tarifacao) moeda).taxaCompra();
        double bruto = carteira.valorBruto(quantidade, cotacao);
        double total = carteira.arredondarReais(bruto + carteira.valorTaxa(bruto, taxa));
        double antes[] = copiarSaldos();
        if (!c.comprar(moeda, quantidade, cotacao))
            throw new IllegalArgumentException(String.format(BR,
                    "Saldo insuficiente: essa compra custa R$ %.2f (com taxa).", total));
        persistir(antes, "+", quantidade, sigla, cotacao, taxa, total);
        return textoSaldo();
    }

    /**
     * Vende criptomoeda, cobrando a taxa de venda.
     *
     * @param sigla BTC, ETH ou XRP
     * @param quantidade quantidade de moeda a vender
     * @return saldos atualizados, no formato do enunciado
     * @throws SQLException se der erro no banco
     */
    public String vender(String sigla, double quantidade) throws SQLException {
        carteira c = investidor.getCarteira();
        Moedas moeda = moedaDe(sigla);
        quantidade = carteira.arredondarCripto(quantidade);
        if (!(quantidade > 0)) throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        double cotacao = cotDao.buscar(sigla);
        double taxa = ((Tarifacao) moeda).taxaVenda();
        double bruto = carteira.valorBruto(quantidade, cotacao);
        double liquido = carteira.arredondarReais(bruto - carteira.valorTaxa(bruto, taxa));
        double antes[] = copiarSaldos();
        if (!c.vender(moeda, quantidade, cotacao))
            throw new IllegalArgumentException("Saldo de " + moeda.getNome() + " insuficiente para essa venda.");
        persistir(antes, "-", quantidade, sigla, cotacao, taxa, liquido);
        return textoSaldo();
    }

    /**
     * Saldo no formato do enunciado (também serve para "saldos atualizados").
     *
     * @return texto com nome, CPF e os 4 saldos
     */
    public String textoSaldo() {
        var c = investidor.getCarteira();
        return String.format(BR, "Nome: %s%nCPF: %s%n%nReais: %.2f%nBitcoin: %.4f%nEthereum: %.4f%nRipple: %.2f",
                investidor.getNome(), formatarCpf(investidor.getCpf()), c.getReal().getSaldo(),
                c.getBitcoin().getSaldo(), c.getEthereum().getSaldo(), c.getRipple().getSaldo());
    }

    /**
     * Extrato de operações no formato do enunciado.
     *
     * @return cabeçalho (nome e CPF) e uma linha por operação
     * @throws SQLException se der erro no banco
     */
    public String consultarExtrato() throws SQLException {
        String linhas = dao.montarExtrato(investidor.getCpf());
        if (linhas.isEmpty()) linhas = "(nenhuma operação realizada)";
        return String.format("Nome: %s%nCPF: %s%n%n%s",
                investidor.getNome(), formatarCpf(investidor.getCpf()), linhas);
    }

    /**
     * Confere a senha (saldo, extrato, compra e venda pedem a senha de novo).
     *
     * @param senha senha digitada
     * @return true se estiver correta
     */
    public boolean senhaConfere(String senha) { return investidor.senhaCorreta(senha); }

    /** 12345678900 vira 123.456.789-00. */
    private static String formatarCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) return cpf;
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
    }

    private Moedas moedaDe(String sigla) {
        carteira c = investidor.getCarteira();
        return switch (sigla) {
            case "BTC" -> c.getBitcoin();
            case "ETH" -> c.getEthereum();
            case "XRP" -> c.getRipple();
            default -> throw new IllegalArgumentException("Moeda inválida: " + sigla);
        };
    }

    private double[] copiarSaldos() {
        var c = investidor.getCarteira();
        return new double[] {c.getReal().getSaldo(), c.getBitcoin().getSaldo(),
                             c.getEthereum().getSaldo(), c.getRipple().getSaldo()};
    }

    /** Grava no banco; se falhar, desfaz a mudança na memória para não ficar diferente do banco. */
    private void persistir(double[] antes, String sinal, double quantidade, String moeda,
                           double cotacao, double taxa, double valorReais) throws SQLException {
        try {
            dao.salvarOperacao(investidor, sinal, quantidade, moeda, cotacao, taxa, valorReais);
        } catch (SQLException e) {
            var c = investidor.getCarteira();
            c.getReal().setSaldo(antes[0]);
            c.getBitcoin().setSaldo(antes[1]);
            c.getEthereum().setSaldo(antes[2]);
            c.getRipple().setSaldo(antes[3]);
            throw e;
        }
    }
}
