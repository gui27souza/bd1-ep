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
    cpf VARCHAR(11) NOT NULL UNIQUE,
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
    status VARCHAR(20) CHECK (status IN ('ativo', 'inativo', 'arquivado')) DEFAULT 'ativo',
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
    data_transacao TIMESTAMP WITH TIME ZONE DEFAULT now(),
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

-- Planos diversos
INSERT INTO Plano (nome, valor, qtd_convites) VALUES
    ('free', 0, 5),
    ('basic', 29.90, 15),
    ('premium', 59.90, 30),
    ('family', 89.90, 50),
    ('business', 149.90, 100);

-- Clientes variados
INSERT INTO Cliente (nome, cpf, data_nasc, id_plano) VALUES
    ('Ana Costa', '12345678901', '1995-03-15', 3),        -- Premium
    ('Bruno Silva', '23456789012', '1988-07-22', 4),      -- Family
    ('Carla Mendes', '34567890123', '1992-11-08', 2),     -- Basic
    ('Daniel Souza', '45678901234', '1990-01-30', 3),     -- Premium
    ('Elena Rodrigues', '56789012345', '1985-05-12', 5),  -- Business
    ('Felipe Santos', '67890123456', '1998-09-25', 2),    -- Basic
    ('Gabriela Lima', '78901234567', '1993-04-18', 1),    -- Free
    ('Henrique Alves', '89012345678', '1987-12-03', 4),   -- Family
    ('Isabela Martins', '90123456789', '1996-06-27', 3),  -- Premium
    ('João Pereira', '01234567890', '1991-08-14', 2),     -- Basic
    ('Larissa Oliveira', '11234567890', '1994-02-20', 3), -- Premium
    ('Marcos Ferreira', '12334567890', '1989-10-05', 1),  -- Free
    ('Natália Gomes', '13234567890', '1997-07-11', 2),    -- Basic
    ('Paulo Ribeiro', '14234567890', '1986-03-28', 4),    -- Family
    ('Renata Castro', '15234567890', '1999-12-09', 1);    -- Free

-- Credenciais dos clientes (senha: 1234)
INSERT INTO Credenciais (id_cliente, email, senha_hash) VALUES
    (1, 'ana.costa@email.com', '1234'),
    (2, 'bruno.silva@email.com', '1234'),
    (3, 'carla.mendes@email.com', '1234'),
    (4, 'daniel.souza@email.com', '1234'),
    (5, 'elena.rodrigues@email.com', '1234'),
    (6, 'felipe.santos@email.com', '1234'),
    (7, 'gabriela.lima@email.com', '1234'),
    (8, 'henrique.alves@email.com', '1234'),
    (9, 'isabela.martins@email.com', '1234'),
    (10, 'joao.pereira@email.com', '1234'),
    (11, 'larissa.oliveira@email.com', '1234'),
    (12, 'marcos.ferreira@email.com', '1234'),
    (13, 'natalia.gomes@email.com', '1234'),
    (14, 'paulo.ribeiro@email.com', '1234'),
    (15, 'renata.castro@email.com', '1234');

-- Grupos diversos e interessantes
INSERT INTO Grupo (nome, status, descricao) VALUES
    ('Família Costa-Silva', 'ativo', 'Despesas compartilhadas da família'),
    ('República dos Amigos', 'ativo', 'Contas do apartamento compartilhado'),
    ('Viagem Europa 2025', 'ativo', 'Planejamento financeiro da viagem'),
    ('Churrasco Mensal', 'ativo', 'Organização dos churrascos de domingo'),
    ('Projeto Startup', 'ativo', 'Investimentos e despesas da startup'),
    ('Presente Coletivo Maria', 'ativo', 'Vaquinha para presente de casamento'),
    ('Condomínio Edifício Sol', 'ativo', 'Gestão de despesas do condomínio'),
    ('Time de Futebol', 'ativo', 'Mensalidades e equipamentos'),
    ('Curso de Inglês', 'ativo', 'Divisão de custos do professor particular'),
    ('Festa de Fim de Ano', 'ativo', 'Organização da confraternização'),
    ('Passeio Pet', 'arquivado', 'Grupo de passeio com cachorros (encerrado)'),
    ('Investimento Imóvel', 'ativo', 'Compra conjunta de apartamento');

-- Categorias mais específicas
INSERT INTO Categoria (nome, descricao) VALUES
    ('Alimentação', 'Supermercado, restaurantes e delivery'),
    ('Moradia', 'Aluguel, condomínio e manutenção'),
    ('Transporte', 'Combustível, transporte público e apps'),
    ('Saúde', 'Planos, consultas e medicamentos'),
    ('Educação', 'Cursos, livros e materiais'),
    ('Lazer', 'Entretenimento, cinema e eventos'),
    ('Viagem', 'Passagens, hospedagem e passeios'),
    ('Vestuário', 'Roupas, calçados e acessórios'),
    ('Tecnologia', 'Eletrônicos e assinaturas digitais'),
    ('Pet', 'Veterinário, ração e acessórios'),
    ('Presente', 'Presentes e comemorações'),
    ('Investimento', 'Aplicações e aportes financeiros');

-- Membros dos grupos (mix de admins e membros)
INSERT INTO MembroGrupo (id_cliente, id_grupo, role) VALUES
    -- Família Costa-Silva (4 membros)
    (1, 1, 'admin'),
    (2, 1, 'admin'),
    (3, 1, 'membro'),
    (4, 1, 'membro'),
    
    -- República dos Amigos (3 membros)
    (5, 2, 'admin'),
    (6, 2, 'membro'),
    (7, 2, 'membro'),
    
    -- Viagem Europa 2025 (5 membros)
    (1, 3, 'admin'),
    (4, 3, 'membro'),
    (9, 3, 'membro'),
    (11, 3, 'membro'),
    (13, 3, 'membro'),
    
    -- Churrasco Mensal (6 membros)
    (2, 4, 'admin'),
    (5, 4, 'membro'),
    (8, 4, 'membro'),
    (10, 4, 'membro'),
    (12, 4, 'membro'),
    (14, 4, 'membro'),
    
    -- Projeto Startup (3 membros)
    (5, 5, 'admin'),
    (9, 5, 'admin'),
    (11, 5, 'membro'),
    
    -- Presente Coletivo Maria (7 membros)
    (1, 6, 'admin'),
    (3, 6, 'membro'),
    (6, 6, 'membro'),
    (8, 6, 'membro'),
    (10, 6, 'membro'),
    (12, 6, 'membro'),
    (14, 6, 'membro'),
    
    -- Condomínio (8 membros)
    (2, 7, 'admin'),
    (4, 7, 'admin'),
    (6, 7, 'membro'),
    (7, 7, 'membro'),
    (9, 7, 'membro'),
    (11, 7, 'membro'),
    (13, 7, 'membro'),
    (15, 7, 'membro'),
    
    -- Time de Futebol (5 membros)
    (8, 8, 'admin'),
    (2, 8, 'membro'),
    (10, 8, 'membro'),
    (12, 8, 'membro'),
    (14, 8, 'membro'),
    
    -- Curso de Inglês (4 membros)
    (3, 9, 'admin'),
    (7, 9, 'membro'),
    (11, 9, 'membro'),
    (15, 9, 'membro'),
    
    -- Festa Fim de Ano (6 membros)
    (1, 10, 'admin'),
    (2, 10, 'admin'),
    (5, 10, 'membro'),
    (9, 10, 'membro'),
    (11, 10, 'membro'),
    (13, 10, 'membro'),
    
    -- Passeio Pet - arquivado (2 membros)
    (7, 11, 'admin'),
    (15, 11, 'membro'),
    
    -- Investimento Imóvel (3 membros)
    (5, 12, 'admin'),
    (9, 12, 'admin'),
    (14, 12, 'membro');

-- Transações variadas e realistas (valores negativos = gastos, positivos = ganhos)
INSERT INTO Transacao (descricao, valor, data_transacao, id_cliente, id_grupo, id_categoria) VALUES
    -- Família Costa-Silva (com rateio de contas e contribuições)
    ('Supermercado Extra - compras do mês', -850.50, '2025-11-01 10:30:00', 1, 1, 1),
    ('Conta de luz - Dezembro', -245.80, '2025-11-02 14:20:00', 2, 1, 2),
    ('Internet fibra 500MB', -129.90, '2025-11-03 09:15:00', 1, 1, 2),
    ('Gás de cozinha', -120.00, '2025-11-04 16:45:00', 3, 1, 2),
    ('iFood - Pizza sexta', -95.00, '2025-11-08 20:30:00', 4, 1, 1),
    ('Farmácia - remédios', -187.50, '2025-11-10 11:00:00', 2, 1, 4),
    ('Contribuição mensal - Ana', 500.00, '2025-11-01 08:00:00', 1, 1, 2),
    ('Contribuição mensal - Bruno', 500.00, '2025-11-01 08:05:00', 2, 1, 2),
    
    -- República dos Amigos (rateio de despesas compartilhadas)
    ('Aluguel - Novembro', -2400.00, '2025-11-01 08:00:00', 5, 2, 2),
    ('Condomínio', -450.00, '2025-11-01 08:05:00', 6, 2, 2),
    ('Supermercado Atacadão', -380.00, '2025-11-05 15:30:00', 7, 2, 1),
    ('Netflix compartilhado', -55.90, '2025-11-06 10:00:00', 5, 2, 9),
    ('Conta de água', -89.00, '2025-11-07 13:45:00', 6, 2, 2),
    ('Limpeza do apartamento', -150.00, '2025-11-09 09:00:00', 7, 2, 2),
    ('Rateio mensal - Elena', 1200.00, '2025-11-01 09:00:00', 5, 2, 2),
    ('Rateio mensal - Felipe', 1200.00, '2025-11-01 09:05:00', 6, 2, 2),
    ('Rateio mensal - Gabriela', 1200.00, '2025-11-01 09:10:00', 7, 2, 2),
    
    -- Viagem Europa 2025 (economia coletiva para viagem)
    ('Passagem aérea SP-Lisboa', -3850.00, '2025-10-15 14:00:00', 1, 3, 7),
    ('Passagem aérea SP-Lisboa', -3850.00, '2025-10-15 14:05:00', 4, 3, 7),
    ('Passagem aérea SP-Lisboa', -3850.00, '2025-10-15 14:10:00', 9, 3, 7),
    ('Reserva Airbnb Paris (7 dias)', -2100.00, '2025-10-20 16:30:00', 1, 3, 7),
    ('Seguro viagem Europa', -280.00, '2025-10-22 11:15:00', 4, 3, 7),
    ('Trem Paris-Amsterdam', -195.00, '2025-10-25 09:45:00', 9, 3, 7),
    ('Economia mensal - Ana', 2000.00, '2025-09-01 10:00:00', 1, 3, 7),
    ('Economia mensal - Ana', 2000.00, '2025-10-01 10:00:00', 1, 3, 7),
    ('Economia mensal - Daniel', 2000.00, '2025-09-01 10:05:00', 4, 3, 7),
    ('Economia mensal - Daniel', 2000.00, '2025-10-01 10:05:00', 4, 3, 7),
    ('Economia mensal - Isabela', 2000.00, '2025-09-01 10:10:00', 9, 3, 7),
    ('Economia mensal - Isabela', 2000.00, '2025-10-01 10:10:00', 9, 3, 7),
    
    -- Churrasco Mensal (vaquinha para evento)
    ('Carne Açougue Boi Bravo', -320.00, '2025-11-02 08:30:00', 2, 4, 1),
    ('Bebidas e refrigerantes', -180.00, '2025-11-02 09:00:00', 5, 4, 1),
    ('Carvão e sal grosso', -45.00, '2025-11-02 09:30:00', 8, 4, 1),
    ('Pão de alho e farofa', -38.00, '2025-11-02 10:00:00', 10, 4, 1),
    ('Vaquinha - Bruno', 100.00, '2025-11-01 08:00:00', 2, 4, 1),
    ('Vaquinha - Elena', 100.00, '2025-11-01 08:05:00', 5, 4, 1),
    ('Vaquinha - Henrique', 100.00, '2025-11-01 08:10:00', 8, 4, 1),
    ('Vaquinha - João', 100.00, '2025-11-01 08:15:00', 10, 4, 1),
    ('Vaquinha - Marcos', 100.00, '2025-11-01 08:20:00', 12, 4, 1),
    ('Vaquinha - Paulo', 100.00, '2025-11-01 08:25:00', 14, 4, 1),
    
    -- Projeto Startup (investimento coletivo)
    ('Registro de domínio .com', -120.00, '2025-10-01 10:00:00', 5, 5, 9),
    ('AWS - hospedagem cloud', -450.00, '2025-10-05 14:30:00', 9, 5, 9),
    ('Designer freelancer - logo', -800.00, '2025-10-10 16:00:00', 5, 5, 12),
    ('Google Ads - marketing', -1200.00, '2025-10-15 11:00:00', 11, 5, 12),
    ('Advogado - contrato social', -2500.00, '2025-10-20 09:30:00', 5, 5, 12),
    ('Investimento inicial - Elena', 5000.00, '2025-09-15 10:00:00', 5, 5, 12),
    ('Investimento inicial - Isabela', 5000.00, '2025-09-15 10:05:00', 9, 5, 12),
    ('Investimento inicial - Larissa', 3000.00, '2025-09-15 10:10:00', 11, 5, 12),
    
    -- Presente Coletivo Maria (vaquinha finalizada)
    ('Cota presente casamento', 150.00, '2025-10-28 14:00:00', 1, 6, 11),
    ('Cota presente casamento', 150.00, '2025-10-28 14:15:00', 3, 6, 11),
    ('Cota presente casamento', 150.00, '2025-10-28 14:30:00', 6, 6, 11),
    ('Cota presente casamento', 150.00, '2025-10-28 14:45:00', 8, 6, 11),
    ('Cota presente casamento', 150.00, '2025-10-28 15:00:00', 10, 6, 11),
    ('Compra conjunto panelas', -1050.00, '2025-10-30 10:00:00', 1, 6, 11),
    
    -- Condomínio Edifício Sol (despesas coletivas e rateio)
    ('Manutenção elevador', -1800.00, '2025-11-01 09:00:00', 2, 7, 2),
    ('Salário porteiro', -2200.00, '2025-11-01 09:30:00', 4, 7, 2),
    ('Limpeza áreas comuns', -950.00, '2025-11-03 08:00:00', 2, 7, 2),
    ('Conta de luz áreas comuns', -580.00, '2025-11-05 15:00:00', 6, 7, 2),
    ('Jardinagem e paisagismo', -420.00, '2025-11-07 10:30:00', 4, 7, 2),
    ('Rateio condomínio - Bruno', 800.00, '2025-11-01 08:00:00', 2, 7, 2),
    ('Rateio condomínio - Daniel', 800.00, '2025-11-01 08:05:00', 4, 7, 2),
    ('Rateio condomínio - Felipe', 800.00, '2025-11-01 08:10:00', 6, 7, 2),
    ('Rateio condomínio - Gabriela', 800.00, '2025-11-01 08:15:00', 7, 7, 2),
    ('Rateio condomínio - Isabela', 800.00, '2025-11-01 08:20:00', 9, 7, 2),
    ('Rateio condomínio - Larissa', 800.00, '2025-11-01 08:25:00', 11, 7, 2),
    ('Rateio condomínio - Natália', 800.00, '2025-11-01 08:30:00', 13, 7, 2),
    ('Rateio condomínio - Renata', 800.00, '2025-11-01 08:35:00', 15, 7, 2),
    
    -- Time de Futebol (mensalidade e despesas)
    ('Mensalidade quadra', -400.00, '2025-11-01 07:00:00', 8, 8, 6),
    ('Camisetas personalizadas', -750.00, '2025-10-25 13:00:00', 8, 8, 8),
    ('Bolas profissionais (3 un)', -285.00, '2025-10-26 14:30:00', 2, 8, 6),
    ('Churrasco pós-jogo', -220.00, '2025-11-03 17:00:00', 10, 8, 1),
    ('Mensalidade - Henrique', 150.00, '2025-11-01 08:00:00', 8, 8, 6),
    ('Mensalidade - Bruno', 150.00, '2025-11-01 08:05:00', 2, 8, 6),
    ('Mensalidade - João', 150.00, '2025-11-01 08:10:00', 10, 8, 6),
    ('Mensalidade - Marcos', 150.00, '2025-11-01 08:15:00', 12, 8, 6),
    ('Mensalidade - Paulo', 150.00, '2025-11-01 08:20:00', 14, 8, 6),
    
    -- Curso de Inglês (rateio do professor)
    ('Professor particular - mês 1', -800.00, '2025-11-01 18:00:00', 3, 9, 5),
    ('Material didático Oxford', -180.00, '2025-11-02 12:00:00', 7, 9, 5),
    ('Assinatura Duolingo Plus', -89.90, '2025-11-04 19:00:00', 11, 9, 5),
    ('Rateio curso - Carla', 300.00, '2025-11-01 08:00:00', 3, 9, 5),
    ('Rateio curso - Gabriela', 300.00, '2025-11-01 08:05:00', 7, 9, 5),
    ('Rateio curso - Larissa', 300.00, '2025-11-01 08:10:00', 11, 9, 5),
    ('Rateio curso - Renata', 300.00, '2025-11-01 08:15:00', 15, 9, 5),
    
    -- Festa Fim de Ano (vaquinha para evento)
    ('Buffet completo 50 pessoas', -3200.00, '2025-10-18 10:00:00', 1, 10, 6),
    ('Decoração e balões', -450.00, '2025-10-19 11:30:00', 2, 10, 6),
    ('DJ profissional', -800.00, '2025-10-20 14:00:00', 5, 10, 6),
    ('Bebidas e refrigerantes', -580.00, '2025-10-21 16:00:00', 9, 10, 1),
    ('Lembrancinhas personalizadas', -290.00, '2025-10-22 13:00:00', 1, 10, 11),
    ('Vaquinha - Ana', 1000.00, '2025-10-15 10:00:00', 1, 10, 6),
    ('Vaquinha - Bruno', 1000.00, '2025-10-15 10:05:00', 2, 10, 6),
    ('Vaquinha - Elena', 1000.00, '2025-10-15 10:10:00', 5, 10, 6),
    ('Vaquinha - Isabela', 1000.00, '2025-10-15 10:15:00', 9, 10, 6),
    ('Vaquinha - Larissa', 1000.00, '2025-10-15 10:20:00', 11, 10, 6),
    ('Vaquinha - Natália', 500.00, '2025-10-15 10:25:00', 13, 10, 6),
    
    -- Investimento Imóvel (contribuições e despesas)
    ('Entrada apartamento 20%', -85000.00, '2025-09-15 10:00:00', 5, 12, 12),
    ('Entrada apartamento 20%', -85000.00, '2025-09-15 10:05:00', 9, 12, 12),
    ('Documentação e ITBI', -12500.00, '2025-09-20 14:00:00', 5, 12, 12),
    ('Vistoria e avaliação', -1200.00, '2025-09-25 11:00:00', 14, 12, 12),
    ('Investimento - Elena', 90000.00, '2025-09-10 10:00:00', 5, 12, 12),
    ('Investimento - Isabela', 90000.00, '2025-09-10 10:05:00', 9, 12, 12),
    ('Investimento - Paulo', 10000.00, '2025-09-10 10:10:00', 14, 12, 12);

-- Metade das transações via PIX
INSERT INTO Pix (id_transacao, chave) VALUES
    (1, 'ana.costa@email.com'),
    (3, '12345678901'),
    (5, '11987654321'),
    (7, 'republica.amigos@pix.com'),
    (9, 'e7f8g9h0-i1j2-k3l4-m5n6-o7p8q9r0s1t2'),
    (11, 'felipesantos@email.com'),
    (13, '23456789012'),
    (15, 'ana.costa@email.com'),
    (17, '11998877665'),
    (19, 'churrascodomengo@pix.com'),
    (21, 'startup.pix@email.com'),
    (23, 'd4e5f6g7-h8i9-j0k1-l2m3-n4o5p6q7r8s9'),
    (25, '12345678901'),
    (27, '34567890123'),
    (29, 'presente.maria@pix.com'),
    (31, 'condominio.sol@pix.com'),
    (33, 'bruno.silva@email.com'),
    (35, '11976543210'),
    (37, 'futebol.time@pix.com'),
    (39, 'teacher.english@email.com'),
    (41, '78901234567'),
    (43, 'festa2025@pix.com'),
    (45, '11965432109'),
    (47, 'imovel.invest@pix.com'),
    (49, '56789012345');

-- Metade das transações via Cartão
INSERT INTO Cartao (id_transacao, bandeira, digitos_finais) VALUES
    (2, 'Visa', '4532'),
    (4, 'Mastercard', '5412'),
    (6, 'Elo', '6362'),
    (8, 'Visa', '4916'),
    (10, 'Mastercard', '5184'),
    (12, 'Hipercard', '6062'),
    (14, 'Visa', '4024'),
    (16, 'Amex', '3782'),
    (18, 'Mastercard', '5376'),
    (20, 'Visa', '4539'),
    (22, 'Elo', '6504'),
    (24, 'Mastercard', '5463'),
    (26, 'Visa', '4716'),
    (28, 'Amex', '3714'),
    (30, 'Mastercard', '5228'),
    (32, 'Visa', '4485'),
    (34, 'Elo', '6367'),
    (36, 'Mastercard', '5294'),
    (38, 'Visa', '4532'),
    (40, 'Amex', '3787'),
    (42, 'Mastercard', '5412'),
    (44, 'Visa', '4916'),
    (46, 'Elo', '6362'),
    (48, 'Mastercard', '5184'),
    (50, 'Visa', '4024');

-- Convites pendentes e processados
INSERT INTO Convite (id_remetente, id_destino, id_grupo, status) VALUES
    -- Pendentes
    (1, 5, 1, 'pendente'),      -- Ana convida Elena para Família
    (5, 1, 2, 'pendente'),      -- Elena convida Ana para República
    (2, 3, 4, 'pendente'),      -- Bruno convida Carla para Churrasco
    (9, 6, 5, 'pendente'),      -- Isabela convida Felipe para Startup
    (1, 15, 6, 'pendente'),     -- Ana convida Renata para Presente
    
    -- Aceitos (já são membros)
    (1, 4, 3, 'aceito'),        -- Ana convidou Daniel para Viagem
    (2, 5, 4, 'aceito'),        -- Bruno convidou Elena para Churrasco
    (5, 11, 5, 'aceito'),       -- Elena convidou Larissa para Startup
    (2, 6, 7, 'aceito'),        -- Bruno convidou Felipe para Condomínio
    (8, 10, 8, 'aceito'),       -- Henrique convidou João para Futebol
    
    -- Recusados
    (3, 5, 9, 'recusado'),      -- Carla convidou Elena para Inglês (recusou)
    (7, 1, 11, 'recusado'),     -- Gabriela convidou Ana para Pet (recusou)
    (1, 7, 10, 'recusado'),     -- Ana convidou Gabriela para Festa (recusou)
    (5, 3, 12, 'recusado');     -- Elena convidou Carla para Imóvel (recusou)
