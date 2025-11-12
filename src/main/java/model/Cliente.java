package main.java.model;

import main.java.exceptions.DomainException;

import java.sql.Date;
import java.util.Objects;

public class Cliente {

	int id = -1;
	String nome;
	String cpf;
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
	public Cliente(int id, String nome, String cpf, Date dataNascimento, int idPlano) {
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
	public Cliente(String nome, String cpf, Date dataNascimento, int idPlano) {
		this.nome = nome;
		this.cpf = cpf;
		this.dataNascimento = dataNascimento;
		this.idPlano = idPlano;
	}

	public int getId() { return this.id; }
	public String getNome() { return this.nome; }
	public String getCpf() { return this.cpf; }
	public Date getDataNascimento() { return this.dataNascimento; }
	public int getIdPlano() { return this.idPlano; }

	public void setId(int id) throws DomainException {
		if (this.id == -1) {
			this.id = id;
		} else {
			throw new DomainException("Não é possível alterar o id já setado de um Cliente!");
		}
	}
	public void setNome(String nome) { this.nome = nome; }
	public void setCpf(String cpf) { this.cpf = cpf; }
	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	public void setIdPlano(int idPlano) { this.idPlano = idPlano; }

	public void repr() {
		System.out.println("ID: " + this.id);
		System.out.println("Nome: " + this.nome);
		System.out.println("CPF: " + this.cpf);
		System.out.println("Data de Nascimento: " + this.dataNascimento);
		System.out.println("Id de Plano: " + this.idPlano);
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Cliente cliente = (Cliente) o;
		return id == cliente.id;
	}
	public int hashCode() {
		return Objects.hash(id);
	}
}
