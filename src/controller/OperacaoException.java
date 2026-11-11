package controller;

import java.sql.SQLException;

/**
 * Erro de uma operação, com uma mensagem pronta para mostrar ao usuário.
 * Serve tanto para regra de negócio violada (saldo insuficiente, valor inválido...)
 * quanto para falha no banco. Assim a tela só precisa tratar este tipo de erro e
 * não precisa conhecer SQL.
 */
public class OperacaoException extends RuntimeException {

    public OperacaoException(String mensagem) {
        super(mensagem);
    }

    public OperacaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }

    /** Converte um erro do banco numa mensagem para o usuário. */
    public static OperacaoException erroBanco(SQLException causa) {
        return new OperacaoException("Erro no banco de dados: " + causa.getMessage(), causa);
    }
}
