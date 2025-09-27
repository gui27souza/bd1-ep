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

INSERT INTO plano (nome, valor, qtd_convites) VALUES ('free', 0, 0), ('admin', 50, 20)