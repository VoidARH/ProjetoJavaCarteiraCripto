-- Exchange de Criptomoedas
-- Template de banco fornecido pela professora (tabelas usuario, cotacao e operacao).
-- Rode no DBeaver / psql conectado ao banco "exchange". Recria as tabelas do zero.

DROP TABLE IF EXISTS operacao;
DROP TABLE IF EXISTS cotacao;
DROP TABLE IF EXISTS usuario;
-- tabelas da versao antiga do projeto (pode ignorar se nao existirem)
DROP TABLE IF EXISTS extrato;
DROP TABLE IF EXISTS carteira;
DROP TABLE IF EXISTS investidor;
DROP TABLE IF EXISTS pessoa;

-- Tabela: usuario
CREATE TABLE usuario (
    cpf VARCHAR(11),
    nome VARCHAR(100),
    senha VARCHAR(6),
    tipo VARCHAR(15),
    saldo_reais NUMERIC(15,2),
    saldo_bitcoin NUMERIC(20,8),
    saldo_ethereum NUMERIC(20,8),
    saldo_ripple NUMERIC(20,8)
);

-- Tabela: cotacao
CREATE TABLE cotacao (
    moeda VARCHAR(3),
    valor_reais NUMERIC(20,8),
    taxa_compra NUMERIC(5,4),
    taxa_venda NUMERIC(5,4)
);

-- Tabela: operacao
CREATE TABLE operacao (
    id SERIAL,
    cpf_usuario VARCHAR(11),
    data_hora TIMESTAMP,
    tipo VARCHAR(10),
    moeda VARCHAR(4),
    quantidade NUMERIC(20,8),
    cotacao_unitaria NUMERIC(20,8),
    taxa NUMERIC(5,4),
    valor_reais NUMERIC(20,2),
    saldo_reais NUMERIC(15,2),
    saldo_bitcoin NUMERIC(20,8),
    saldo_ethereum NUMERIC(20,8),
    saldo_ripple NUMERIC(20,8)
);

-- Cotações iniciais
INSERT INTO cotacao
    (moeda, valor_reais, taxa_compra, taxa_venda)
VALUES
    ('BTC', 300000.00, 0.0200, 0.0300),
    ('ETH', 10000.00, 0.0100, 0.0200),
    ('XRP', 2.50, 0.0100, 0.0100);

-- Investidor de teste (nao faz parte do template): CPF 12345678900, senha 123456
INSERT INTO usuario
    (cpf, nome, senha, tipo, saldo_reais, saldo_bitcoin, saldo_ethereum, saldo_ripple)
VALUES
    ('12345678900', 'Seu Nome', '123456', 'investidor', 0, 0, 0, 0);
