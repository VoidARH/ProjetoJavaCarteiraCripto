-- Exchange de Criptomoedas: tabelas conforme o seu DAO (investidor, carteira, operacao, cotacao)
-- Rode no DBeaver conectado ao banco "exchange" (Alt+X). Apaga as tabelas antigas.

DROP TABLE IF EXISTS operacao;
DROP TABLE IF EXISTS extrato;
DROP TABLE IF EXISTS carteira;
DROP TABLE IF EXISTS cotacao;
DROP TABLE IF EXISTS investidor;
DROP TABLE IF EXISTS pessoa;

CREATE TABLE investidor (
    cpf   CHAR(11) PRIMARY KEY CHECK (cpf ~ '^[0-9]{11}$'),
    nome  VARCHAR(100) NOT NULL,
    senha CHAR(6) NOT NULL CHECK (senha ~ '^[0-9]{6}$')
);

CREATE TABLE carteira (
    cpf      CHAR(11) PRIMARY KEY REFERENCES investidor(cpf) ON DELETE CASCADE,
    reais    NUMERIC(18,2) NOT NULL DEFAULT 0 CHECK (reais >= 0),
    bitcoin  NUMERIC(18,8) NOT NULL DEFAULT 0 CHECK (bitcoin >= 0),
    ethereum NUMERIC(18,8) NOT NULL DEFAULT 0 CHECK (ethereum >= 0),
    ripple   NUMERIC(18,8) NOT NULL DEFAULT 0 CHECK (ripple >= 0)
);

CREATE TABLE cotacao (
    moeda VARCHAR(10) PRIMARY KEY,          -- BTC, ETH, XRP
    valor NUMERIC(18,4) NOT NULL CHECK (valor > 0)
);

CREATE TABLE operacao (
    id          SERIAL PRIMARY KEY,
    cpf         CHAR(11) NOT NULL REFERENCES investidor(cpf) ON DELETE CASCADE,
    data_hora   TIMESTAMP NOT NULL DEFAULT NOW(),
    sinal       CHAR(1) NOT NULL CHECK (sinal IN ('+', '-')),
    valor       NUMERIC(18,8) NOT NULL,
    moeda       VARCHAR(10) NOT NULL,       -- REAL, BTC, ETH, XRP
    cotacao     NUMERIC(18,4) NOT NULL DEFAULT 0,
    taxa        NUMERIC(18,8) NOT NULL DEFAULT 0,
    saldo_reais NUMERIC(18,2) NOT NULL,
    saldo_btc   NUMERIC(18,8) NOT NULL,
    saldo_eth   NUMERIC(18,8) NOT NULL,
    saldo_xrp   NUMERIC(18,8) NOT NULL
);

INSERT INTO cotacao (moeda, valor) VALUES ('BTC', 350000), ('ETH', 16000), ('XRP', 2.50);

-- Usuário de teste: CPF 12345678900, senha 123456
INSERT INTO investidor (cpf, nome, senha) VALUES ('12345678900', 'Seu Nome', '123456');
INSERT INTO carteira (cpf) VALUES ('12345678900');
