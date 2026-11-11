package controller;

import dao.cotacaoDao;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Random;

/**
 * Regras da cotação das criptomoedas: sorteia a variação e monta os textos
 * mostrados na tela. A tela só chama estes métodos.
 */
public class cotacaoController {
    private static final String[] MOEDAS = {"BTC", "ETH", "XRP"};
    private static final String[] NOMES  = {"Bitcoin", "Ethereum", "Ripple"};

    /** Variação máxima permitida pelo enunciado: 5% para cima ou para baixo. */
    private static final double LIMITE = 0.05;
    /** Casas decimais guardadas no banco (coluna NUMERIC(20,8)). */
    private static final double ESCALA = 10000.0;

    private static final Locale BR = Locale.forLanguageTag("pt-BR");

    private final cotacaoDao dao = new cotacaoDao();
    private final Random random = new Random();

    /**
     * Calcula o novo valor de uma moeda.
     *
     * @param antigo valor atual da cotação
     * @param sorteio número entre 0 (inclusive) e 1 (exclusivo), vindo do random
     * @return novo valor, sempre entre -5% e +5% do valor antigo
     */
    public static double novoValor(double antigo, double sorteio) {
        // transforma o sorteio (0 a 1) em uma variação de -0.05 a +0.05
        double variacao = sorteio * (2 * LIMITE) - LIMITE;
        double novo = Math.round(antigo * (1 + variacao) * ESCALA) / ESCALA;

        // o arredondamento pode "vazar" alguns décimos de milésimo do limite,
        // então encosta o resultado dentro da faixa permitida
        double minimo = Math.ceil(antigo * (1 - LIMITE) * ESCALA) / ESCALA;
        double maximo = Math.floor(antigo * (1 + LIMITE) * ESCALA) / ESCALA;
        return Math.max(minimo, Math.min(maximo, novo));
    }

    /**
     * Atualiza as 3 cotações no banco.
     *
     * @return texto com as cotações novas, pronto para o showMessageDialog
     * @throws OperacaoException se der erro no banco
     */
    public String atualizarCotacoes() {
        try {
            // primeiro calcula tudo (lê do banco e sorteia), depois grava,
            // para uma falha de leitura não deixar só uma parte atualizada
            double[] novos = new double[MOEDAS.length];
            for (int i = 0; i < MOEDAS.length; i++) {
                novos[i] = novoValor(this.dao.buscar(MOEDAS[i]), this.random.nextDouble());
            }
            this.dao.salvarTodas(MOEDAS, novos);   // numa transação só: ou atualiza as 3 ou nenhuma
        } catch (SQLException e) {
            throw OperacaoException.erroBanco(e);
        }
        return this.textoCotacoes();
    }

    /**
     * Monta o texto com as cotações atuais (útil também nas telas de compra e venda).
     *
     * @return uma linha por moeda, ex.: "Bitcoin: R$ 350000,00"
     * @throws OperacaoException se der erro no banco
     */
    public String textoCotacoes() {
        return "Cotações atuais:\n\n" + this.linhasCotacoes();
    }

    /**
     * Monta só as linhas das cotações atuais, sem título (usado na tela de compra e venda).
     *
     * @return uma linha por moeda, ex.: "Bitcoin: R$ 350000,00"
     * @throws OperacaoException se der erro no banco
     */
    public String linhasCotacoes() {
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < MOEDAS.length; i++) {
                sb.append(String.format(BR, "%s: R$ %.2f%n", NOMES[i], this.dao.buscar(MOEDAS[i])));
            }
            return sb.toString();
        } catch (SQLException e) {
            throw OperacaoException.erroBanco(e);
        }
    }

    /**
     * Lê a cotação de uma moeda (para usar na compra e na venda).
     *
     * @param moeda sigla BTC, ETH ou XRP
     * @return valor em reais
     * @throws OperacaoException se der erro no banco
     */
    public double cotacaoDe(String moeda) {
        try {
            return this.dao.buscar(moeda);
        } catch (SQLException e) {
            throw OperacaoException.erroBanco(e);
        }
    }
}
