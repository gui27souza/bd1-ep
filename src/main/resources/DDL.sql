CREATE DATABASE planejei;

CREATE TABLE cliente(
	id SERIAL INTEGER NOT NULL PRIMARY KEY,
	nome VARCHAR(50),
	cpf VARCHAR(11) UNIQUE,
	data_nasc DATE,
	---id_plano INTEGER,
	---FOREIGN KEY (id_plano) REFERENCES plano(id)
);

INSERT INTO cliente (nome, cpf, data_nasc) VALUES
('Machado de Assis', '11111111111', '1839-06-21'),
('Graciliano Ramos', '22222222222', '1892-10-27'),
('Lygia Fagundes Telles', '33333333333', '1923-04-19'),
('Clarice Lispector', '44444444444', '1920-12-10'),
('Antonio Candido', '55555555555', '1918-07-24');
('Fiódor Dostoiévski', '66666666666', '1821-11-11'),
('Lev Tolstói', '77777777777', '1828-09-09'),
('Antón Tchékhov', '88888888888', '1860-01-29');
('Victor Hugo', '99999999999', '1802-02-26'),
('Stendhal', '10101010101', '1783-01-23');
('Jane Austen', '12121212121', '1775-12-16');