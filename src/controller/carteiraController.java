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
 * Convenção: qualquer problema (regra violada, valor inválido, erro de banco)
 * sai como {@link OperacaoException}, com a mensagem pronta para o usuário.
 * Assim as telas não conhecem SQL nem o model: só chamam este controller.
 *
 * @author void
 */
public class carteiraController {
    private static final Locale BR = Locale.forLanguageTag("pt-BR");
    /** Maior saldo em reais aceito (a coluna do banco é NUMERIC(15,2)). */
    private static final double SALDO_MAXIMO = 1e12;

    /** Ordem das moedas nos combos da tela de operação: Bitcoin, Ethereum, Ripple. */
    private static final String[] SIGLAS = {"BTC", "ETH", "XRP"};

    /** Valor de {@code tipo} em {@link #operar(int, int, String)}: índice de "Comprar" no combo. */
    public static final int COMPRAR = 0;
    /** Valor de {@code tipo} em {@link #operar(int, int, String)}: índice de "Vender" no combo. */
    public static final int VENDER = 1;

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
     * @throws OperacaoException se não for um número válido
     */
    private static double lerValor(String texto) {
        try {
            double v = Double.parseDouble(texto.trim().replace(',', '.'));
            if (Double.isNaN(v) || Double.isInfinite(v)) throw new NumberFormatException();
            return v;
        } catch (NumberFormatException | NullPointerException e) {
            throw new OperacaoException("Digite um número válido.");
        }
    }

    /**
     * Deposita reais a partir do texto do campo da tela (aceita vírgula ou ponto).
     *
     * @param texto valor digitado
     * @return saldos atualizados
     * @throws OperacaoException se o texto ou a operação forem inválidos
     */
    public String depositar(String texto) {
        return this.depositar(lerValor(texto));
    }

    /**
     * Saca reais a partir do texto do campo da tela (aceita vírgula ou ponto).
     *
     * @param texto valor digitado
     * @return saldos atualizados
     * @throws OperacaoException se o texto ou a operação forem inválidos
     */
    public String sacar(String texto) {
        return this.sacar(lerValor(texto));
    }

    /**
     * Compra ou vende criptomoeda a partir do que a tela escolheu. Quem decide qual
     * é a operação e qual é a moeda é o controller, e não a tela.
     *
     * @param tipo {@link #COMPRAR} ou {@link #VENDER} (índice do combo "Operação")
     * @param indiceMoeda índice do combo "Criptomoeda": 0 Bitcoin, 1 Ethereum, 2 Ripple
     * @param quantidadeTexto quantidade digitada (aceita vírgula ou ponto)
     * @return saldos atualizados
     * @throws OperacaoException se algo for inválido ou o saldo for insuficiente
     */
    public String operar(int tipo, int indiceMoeda, String quantidadeTexto) {
        if (indiceMoeda < 0 || indiceMoeda >= SIGLAS.length) {
            throw new OperacaoException("Escolha uma criptomoeda.");
        }
        double quantidade = lerValor(quantidadeTexto);
        String sigla = SIGLAS[indiceMoeda];
        if (tipo == COMPRAR) {
            return this.comprar(sigla, quantidade);
        }
        if (tipo == VENDER) {
            return this.vender(sigla, quantidade);
        }
        throw new OperacaoException("Escolha comprar ou vender.");
    }

    /**
     * Deposita reais.
     *
     * @param valor valor do depósito (maior que zero)
     * @return saldos atualizados, no formato do enunciado
     * @throws OperacaoException se algo for inválido ou der erro no banco
     */
    public String depositar(double valor) {
        carteira c = this.investidor.getCarteira();
        if (!(valor > 0)) throw new OperacaoException("O valor do depósito deve ser maior que zero.");
        if (c.getReal().getSaldo() + valor >= SALDO_MAXIMO)
            throw new OperacaoException("Valor muito alto.");
        double[] antes = c.copiarSaldos();
        valor = carteira.arredondarReais(valor);
        if (!(valor > 0)) throw new OperacaoException("O valor do depósito deve ser de pelo menos R$ 0,01.");
        c.depositar(valor);
        this.persistir(antes, "+", valor, "REAL", 0.0, 0.0, valor);
        return this.textoSaldo();
    }

    /**
     * Saca reais (o saldo não pode ficar negativo).
     *
     * @param valor valor do saque (maior que zero e até o saldo)
     * @return saldos atualizados, no formato do enunciado
     * @throws OperacaoException se algo for inválido ou der erro no banco
     */
    public String sacar(double valor) {
        carteira c = this.investidor.getCarteira();
        valor = carteira.arredondarReais(valor);
        if (!(valor > 0)) throw new OperacaoException("O valor do saque deve ser de pelo menos R$ 0,01.");
        double[] antes = c.copiarSaldos();
        if (!c.sacar(valor)) throw new OperacaoException("Saldo insuficiente para o saque.");
        this.persistir(antes, "-", valor, "REAL", 0.0, 0.0, valor);
        return this.textoSaldo();
    }

    /**
     * Compra criptomoeda, cobrando a taxa de compra.
     *
     * @param sigla BTC, ETH ou XRP
     * @param quantidade quantidade de moeda a comprar
     * @return saldos atualizados, no formato do enunciado
     * @throws OperacaoException se algo for inválido ou der erro no banco
     */
    public String comprar(String sigla, double quantidade) {
        carteira c = this.investidor.getCarteira();
        Moedas moeda = this.moedaDe(sigla);
        quantidade = carteira.arredondarCripto(quantidade);
        if (!(quantidade > 0)) throw new OperacaoException("A quantidade deve ser maior que zero.");
        double cotacao = this.buscarCotacao(sigla);
        double taxa = ((Tarifacao) moeda).taxaCompra();
        double bruto = carteira.valorBruto(quantidade, cotacao);
        double total = carteira.arredondarReais(bruto + carteira.valorTaxa(bruto, taxa));
        double[] antes = c.copiarSaldos();
        if (!c.comprar(moeda, quantidade, cotacao))
            throw new OperacaoException(String.format(BR,
                    "Saldo insuficiente: essa compra custa R$ %.2f (com taxa).", total));
        this.persistir(antes, "+", quantidade, sigla, cotacao, taxa, total);
        return this.textoSaldo();
    }

    /**
     * Vende criptomoeda, cobrando a taxa de venda.
     *
     * @param sigla BTC, ETH ou XRP
     * @param quantidade quantidade de moeda a vender
     * @return saldos atualizados, no formato do enunciado
     * @throws OperacaoException se algo for inválido ou der erro no banco
     */
    public String vender(String sigla, double quantidade) {
        carteira c = this.investidor.getCarteira();
        Moedas moeda = this.moedaDe(sigla);
        quantidade = carteira.arredondarCripto(quantidade);
        if (!(quantidade > 0)) throw new OperacaoException("A quantidade deve ser maior que zero.");
        double cotacao = this.buscarCotacao(sigla);
        double taxa = ((Tarifacao) moeda).taxaVenda();
        double bruto = carteira.valorBruto(quantidade, cotacao);
        double liquido = carteira.arredondarReais(bruto - carteira.valorTaxa(bruto, taxa));
        double[] antes = c.copiarSaldos();
        if (!c.vender(moeda, quantidade, cotacao))
            throw new OperacaoException("Saldo de " + moeda.getNome() + " insuficiente para essa venda.");
        this.persistir(antes, "-", quantidade, sigla, cotacao, taxa, liquido);
        return this.textoSaldo();
    }

    /**
     * Saldo no formato do enunciado (também serve para "saldos atualizados").
     *
     * @return texto com nome, CPF e os 4 saldos
     */
    public String textoSaldo() {
        var c = this.investidor.getCarteira();
        return String.format(BR, "Nome: %s%nCPF: %s%n%nReais: %.2f%nBitcoin: %.4f%nEthereum: %.4f%nRipple: %.2f",
                this.investidor.getNome(), formatarCpf(this.investidor.getCpf()), c.getReal().getSaldo(),
                c.getBitcoin().getSaldo(), c.getEthereum().getSaldo(), c.getRipple().getSaldo());
    }

    /**
     * Extrato de operações no formato do enunciado.
     *
     * @return cabeçalho (nome e CPF) e uma linha por operação
     * @throws OperacaoException se algo for inválido ou der erro no banco
     */
    public String consultarExtrato() {
        String linhas;
        try {
            linhas = this.dao.montarExtrato(this.investidor.getCpf());
        } catch (SQLException e) {
            throw OperacaoException.erroBanco(e);
        }
        if (linhas.isEmpty()) linhas = "(nenhuma operação realizada)";
        return String.format("Nome: %s%nCPF: %s%n%n%s",
                this.investidor.getNome(), formatarCpf(this.investidor.getCpf()), linhas);
    }

    /**
     * Confere a senha (saldo, extrato, compra e venda pedem a senha de novo).
     *
     * @param senha senha digitada
     * @return true se estiver correta
     */
    public boolean senhaConfere(String senha) { return this.investidor.senhaCorreta(senha); }

    /** 12345678900 vira 123.456.789-00. */
    private static String formatarCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) return cpf;
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
    }

    private Moedas moedaDe(String sigla) {
        carteira c = this.investidor.getCarteira();
        return switch (sigla) {
            case "BTC" -> c.getBitcoin();
            case "ETH" -> c.getEthereum();
            case "XRP" -> c.getRipple();
            default -> throw new OperacaoException("Moeda inválida: " + sigla);
        };
    }

    private double buscarCotacao(String sigla) {
        try {
            return this.cotDao.buscar(sigla);
        } catch (SQLException e) {
            throw OperacaoException.erroBanco(e);
        }
    }

    /** Grava no banco; se falhar, desfaz a mudança na memória para não ficar diferente do banco. */
    private void persistir(double[] antes, String sinal, double quantidade, String moeda,
                           double cotacao, double taxa, double valorReais) {
        try {
            this.dao.salvarOperacao(this.investidor, sinal, quantidade, moeda, cotacao, taxa, valorReais);
        } catch (SQLException e) {
            this.investidor.getCarteira().restaurarSaldos(antes);
            throw OperacaoException.erroBanco(e);
        }
    }
}
