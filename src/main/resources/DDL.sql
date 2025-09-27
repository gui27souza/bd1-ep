CREATE DATABASE planejei;

CREATE TABLE plano (
    id SERIAL INT NOT NULL PRIMARY KEY,
    nome VARCHAR(50),
    valor DECIMAL(10,2),
	qtd_convites INTEGER
);

CREATE TABLE cliente(
	id SERIAL INTEGER NOT NULL PRIMARY KEY,
	nome VARCHAR(50),
	cpf VARCHAR(11) UNIQUE,
	data_nasc DATE,
	---id_plano INTEGER,
	---FOREIGN KEY (id_plano) REFERENCES plano(id)
);