-- DDL:

CREATE TABLE Plano (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(20) NOT NULL UNIQUE,
    valor DECIMAL(10, 2) NOT NULL,
    qtd_convites INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE Cliente (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf BIGINT NOT NULL UNIQUE,
    data_nasc DATE NOT NULL,
    id_plano INTEGER NOT NULL,

    CONSTRAINT fk_plano FOREIGN KEY(id_plano) REFERENCES Plano(id)
);

CREATE TABLE Credenciais (
    id_cliente SERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,

    CONSTRAINT fk_cliente FOREIGN KEY(id_cliente) REFERENCES Cliente(id)
);

CREATE TABLE Grupo (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(40) NOT NULL,
    status VARCHAR(20) CHECK (status IN ('ativo', 'inativo', 'arquivado')),
    data_criacao TIMESTAMP WITH TIME ZONE DEFAULT now(),
    descricao TEXT
);

CREATE TABLE Categoria (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(20) NOT NULL UNIQUE,
    descricao TEXT
);

CREATE TABLE Transacao (
    id SERIAL PRIMARY KEY,
    descricao TEXT,
    valor DECIMAL(10, 2) NOT NULL,
    id_cliente INTEGER NOT NULL,
    id_grupo INTEGER NOT NULL,
    id_categoria INTEGER NOT NULL,

    CONSTRAINT fk_cliente FOREIGN KEY(id_cliente) REFERENCES Cliente(id),
    CONSTRAINT fk_grupo FOREIGN KEY(id_grupo) REFERENCES Grupo(id),
    CONSTRAINT fk_categoria FOREIGN KEY(id_categoria) REFERENCES Categoria(id)
);

CREATE TABLE Pix (
    id_transacao INTEGER PRIMARY KEY,
    chave VARCHAR(255) NOT NULL,

    CONSTRAINT fk_transacao FOREIGN KEY(id_transacao) REFERENCES Transacao(id)
);

CREATE TABLE Cartao (
    id_transacao INTEGER PRIMARY KEY,
    bandeira VARCHAR(50) NOT NULL,
    digitos_finais VARCHAR(4) NOT NULL,

    CONSTRAINT fk_transacao FOREIGN KEY(id_transacao) REFERENCES Transacao(id)
);

CREATE TABLE MembroGrupo (
    id SERIAL PRIMARY KEY,
    id_cliente INTEGER NOT NULL,
    id_grupo INTEGER NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'membro' CHECK (role IN ('membro', 'admin')),

    CONSTRAINT fk_cliente FOREIGN KEY(id_cliente) REFERENCES Cliente(id),
    CONSTRAINT fk_grupo FOREIGN KEY(id_grupo) REFERENCES Grupo(id),

    UNIQUE (id_cliente, id_grupo)
);

CREATE TABLE Convite (
    id SERIAL PRIMARY KEY,
    id_remetente INTEGER NOT NULL,
    id_destino INTEGER,
    id_grupo INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pendente' CHECK (status IN ('pendente', 'aceito', 'recusado')),

    CONSTRAINT fk_remetente FOREIGN KEY(id_remetente) REFERENCES Cliente(id),
    CONSTRAINT fk_destino FOREIGN KEY(id_destino) REFERENCES Cliente(id),
    CONSTRAINT fk_grupo FOREIGN KEY(id_grupo) REFERENCES Grupo(id)
);

-- DML:

INSERT INTO Plano (nome, valor, qtd_convites) VALUES
    ('free', 0, 0),
    ('admin', 50, 20),
    ('premium', 100, 50),
    ('gold', 200, 100),
    ('silver', 150, 80),
    ('bronze', 75, 30),
    ('family', 120, 40),
    ('student', 30, 10),
    ('corporate', 500, 200),
    ('trial', 10, 5);

INSERT INTO Cliente (nome, cpf, data_nasc, id_plano) VALUES
     ('João Silva', 11111111111, '1990-01-01', 1),
     ('Maria Souza', 22222222222, '1992-02-02', 2),
     ('Pedro Santos', 33333333333, '1988-03-03', 3),
     ('Ana Oliveira', 44444444444, '1995-04-04', 4),
     ('Lucas Lima', 55555555555, '2000-05-05', 5),
     ('Carla Rocha', 66666666666, '1998-06-06', 6),
     ('Marcos Alves', 77777777777, '1985-07-07', 7),
     ('Fernanda Costa', 88888888888, '1993-08-08', 8),
     ('Rafael Gomes', 99999999999, '1991-09-09', 9),
     ('Patrícia Mendes', 10101010101, '1997-10-10', 10);

INSERT INTO Credenciais (id_cliente, email, senha_hash) VALUES
    (1, 'joao@example.com', 'hash1'),
    (2, 'maria@example.com', 'hash2'),
    (3, 'pedro@example.com', 'hash3'),
    (4, 'ana@example.com', 'hash4'),
    (5, 'lucas@example.com', 'hash5'),
    (6, 'carla@example.com', 'hash6'),
    (7, 'marcos@example.com', 'hash7'),
    (8, 'fernanda@example.com', 'hash8'),
    (9, 'rafael@example.com', 'hash9'),
    (10, 'patricia@example.com', 'hash10');

INSERT INTO Grupo (nome, status, descricao) VALUES
    ('Família Silva', 'ativo', 'Grupo da família Silva'),
    ('Trabalho TI', 'ativo', 'Grupo de colegas de TI'),
    ('Amigos da Faculdade', 'ativo', 'Ex-colegas de faculdade'),
    ('Projeto XPTO', 'ativo', 'Equipe do projeto XPTO'),
    ('Investidores', 'inativo', 'Grupo de investidores antigos'),
    ('Viagem 2025', 'ativo', 'Planejamento da viagem'),
    ('Condomínio Alpha', 'ativo', 'Gestão do condomínio'),
    ('Time de Futebol', 'ativo', 'Amigos do futebol de domingo'),
    ('Voluntariado', 'arquivado', 'Ações sociais antigas'),
    ('Startup Beta', 'ativo', 'Equipe da startup Beta');

INSERT INTO Categoria (nome, descricao) VALUES
    ('Alimentação', 'Gastos com comida e restaurantes'),
    ('Transporte', 'Gastos com transporte'),
    ('Educação', 'Cursos e materiais'),
    ('Saúde', 'Consultas e remédios'),
    ('Lazer', 'Cinema, shows, etc.'),
    ('Viagem', 'Despesas de viagem'),
    ('Assinaturas', 'Serviços recorrentes'),
    ('Investimento', 'Aportes financeiros'),
    ('Utilidades', 'Água, luz, internet'),
    ('Outros', 'Despesas diversas');

INSERT INTO Transacao (descricao, valor, id_cliente, id_grupo, id_categoria) VALUES
    ('Supermercado', 150.00, 1, 1, 1),
    ('Uber', 25.00, 2, 2, 2),
    ('Curso online', 200.00, 3, 3, 3),
    ('Consulta médica', 300.00, 4, 4, 4),
    ('Cinema', 50.00, 5, 5, 5),
    ('Passagem aérea', 800.00, 6, 6, 6),
    ('Netflix', 40.00, 7, 7, 7),
    ('Aporte Tesouro', 500.00, 8, 8, 8),
    ('Conta de luz', 120.00, 9, 9, 9),
    ('Compra diversa', 60.00, 10, 10, 10);

INSERT INTO Transacao (descricao, valor, id_cliente, id_grupo, id_categoria) VALUES
-- Cliente 1
('Padaria', 25.00, 1, 1, 1),              -- Alimentação
('Restaurante', 80.00, 1, 1, 1),          -- Alimentação
('Farmácia', 60.00, 1, 1, 4),             -- Saúde
('Posto de gasolina', 150.00, 1, 1, 2),   -- Transporte
('Roupas', 200.00, 1, 1, 10),             -- Outros

-- Cliente 2
('Mercado', 130.00, 2, 2, 1),             -- Alimentação
('Streaming', 45.00, 2, 2, 7),            -- Assinaturas
('Combustível', 90.00, 2, 2, 2),          -- Transporte
('Academia', 120.00, 2, 2, 4),            -- Saúde
('Café', 15.00, 2, 2, 1),                 -- Alimentação

-- Cliente 3
('Livraria', 70.00, 3, 3, 3),             -- Educação
('Supermercado', 140.00, 3, 3, 1),        -- Alimentação
('Cinema', 50.00, 3, 3, 5),               -- Lazer
('Aplicativo de transporte', 22.00, 3, 3, 2), -- Transporte
('Curso de idiomas', 250.00, 3, 3, 3),    -- Educação

-- Cliente 4
('Exame laboratorial', 180.00, 4, 4, 4),  -- Saúde
('Farmácia', 90.00, 4, 4, 4),             -- Saúde
('Seguro saúde', 400.00, 4, 4, 4),        -- Saúde
('Supermercado', 160.00, 4, 4, 1),        -- Alimentação
('Restaurante', 75.00, 4, 4, 1),          -- Alimentação

-- Cliente 5
('Bar', 60.00, 5, 5, 5),                  -- Lazer
('Show', 150.00, 5, 5, 5),                -- Lazer
('Restaurante', 90.00, 5, 5, 1),          -- Alimentação
('Loja de roupas', 200.00, 5, 5, 10),     -- Outros
('Streaming', 40.00, 5, 5, 7),            -- Assinaturas

-- Cliente 6
('Hotel', 500.00, 6, 6, 6),               -- Viagem
('Transporte local', 60.00, 6, 6, 2),     -- Transporte
('Restaurante', 180.00, 6, 6, 1),         -- Alimentação
('Bagagem extra', 100.00, 6, 6, 6),       -- Viagem
('Lembranças de viagem', 90.00, 6, 6, 6), -- Viagem

-- Cliente 7
('Spotify', 35.00, 7, 7, 7),              -- Assinaturas
('Cinema', 50.00, 7, 7, 5),               -- Lazer
('Livraria', 80.00, 7, 7, 3),             -- Educação
('Restaurante', 120.00, 7, 7, 1),         -- Alimentação
('Games', 200.00, 7, 7, 5),               -- Lazer

-- Cliente 8
('Investimento em ações', 600.00, 8, 8, 8), -- Investimento
('Aporte CDB', 500.00, 8, 8, 8),            -- Investimento
('Compra de livros', 120.00, 8, 8, 3),      -- Educação
('Restaurante', 90.00, 8, 8, 1),            -- Alimentação
('Café', 18.00, 8, 8, 1),                   -- Alimentação

-- Cliente 9
('Conta de água', 90.00, 9, 9, 9),          -- Utilidades
('Conta de internet', 130.00, 9, 9, 9),     -- Utilidades
('Supermercado', 150.00, 9, 9, 1),          -- Alimentação
('Farmácia', 45.00, 9, 9, 4),               -- Saúde
('Seguro residência', 200.00, 9, 9, 9),     -- Utilidades

-- Cliente 10
('Loja de eletrônicos', 900.00, 10, 10, 10), -- Outros
('Supermercado', 130.00, 10, 10, 1),         -- Alimentação
('Combustível', 100.00, 10, 10, 2),          -- Transporte
('Restaurante', 85.00, 10, 10, 1),           -- Alimentação
('Farmácia', 60.00, 10, 10, 4);              -- Saúde

INSERT INTO Pix (id_transacao, chave) VALUES
    (1, 'chavepix1'),
    (2, 'chavepix2'),
    (3, 'chavepix3'),
    (4, 'chavepix4'),
    (5, 'chavepix5'),
    (6, 'chavepix6'),
    (7, 'chavepix7'),
    (8, 'chavepix8'),
    (9, 'chavepix9'),
    (10, 'chavepix10');

INSERT INTO Cartao (id_transacao, bandeira, digitos_finais) VALUES
    (1, 'Visa', '1111'),
    (2, 'Mastercard', '2222'),
    (3, 'Amex', '3333'),
    (4, 'Elo', '4444'),
    (5, 'Hipercard', '5555'),
    (6, 'Visa', '6666'),
    (7, 'Mastercard', '7777'),
    (8, 'Amex', '8888'),
    (9, 'Elo', '9999'),
    (10, 'Hipercard', '0000');

INSERT INTO MembroGrupo (id_cliente, id_grupo, role) VALUES
    (1, 1, 'admin'),
    (2, 1, 'membro'),
    (3, 2, 'membro'),
    (4, 2, 'membro'),
    (5, 3, 'admin'),
    (6, 3, 'membro'),
    (7, 4, 'membro'),
    (8, 4, 'admin'),
    (9, 5, 'membro'),
    (10, 6, 'membro');

INSERT INTO Convite (id_remetente, id_destino, id_grupo, status) VALUES
    (1, 2, 1, 'pendente'),
    (2, 3, 2, 'aceito'),
    (3, 4, 3, 'recusado'),
    (4, 5, 4, 'pendente'),
    (5, 6, 5, 'aceito'),
    (6, 7, 6, 'pendente'),
    (7, 8, 7, 'recusado'),
    (8, 9, 8, 'pendente'),
    (9, 10, 9, 'aceito'),
    (10, 1, 10, 'pendente');



