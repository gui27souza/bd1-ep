package main.java.Models;

import java.sql.Date;

public class Cliente {

	int id;
	String nome;
	long cpf;
	Date dataNascimento;
	int idPlano;

	/**
	 * Construtor de Cliente JÁ EXISTENTE
	 * @param id
	 * @param nome
	 * @param cpf
	 * @param dataNascimento
	 * @param idPlano
	 */
	public Cliente(int id, String nome, long cpf, Date dataNascimento, int idPlano) {
		this.id = id;
		this.nome = nome;
		this.cpf = cpf;
		this.dataNascimento = dataNascimento;
		this.idPlano = idPlano;
	}

	/**
	 * Construtor de NOVO cliente, id sera populado depois
	 * @param nome
	 * @param cpf
	 * @param dataNascimento
	 * @param idPlano
	 */
	public Cliente(String nome, long cpf, Date dataNascimento, int idPlano) {
		this.nome = nome;
		this.cpf = cpf;
		this.dataNascimento = dataNascimento;
		this.idPlano = idPlano;
	}

	public int getId() { return this.id; }
	public String getNome() { return this.nome; }
	public long getCpf() { return this.cpf; }
	public Date getDataNascimento() { return this.dataNascimento; }
	public int getIdPlano() { return this.idPlano; }

	public void setId(int id) { this.id = id; }
	public void setNome(String nome) { this.nome = nome; }
	public void setCpf(long cpf) { this.cpf = cpf; }
	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	public void setIdPlano(int idPlano) { this.idPlano = idPlano; }

}
