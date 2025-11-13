package main.java.model.transacao;

import java.util.ArrayList;

public class CategoriaTransacao {

	int id;
	String nome;
	String descricao;
	ArrayList<Transacao> transacoes;

	public CategoriaTransacao(int id, String nome, String descricao) {
		this.id = id;
		this.nome = nome;
		this.descricao = descricao;
	}

	public CategoriaTransacao(String nome, String descricao) {
		this.nome = nome;
		this.descricao = descricao;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public ArrayList<Transacao> getTransacoes() {
		return transacoes;
	}

	public void addTransacao(Transacao transacao) {
		transacoes.add(transacao);
	}
	public void removeTransacao(Transacao transacao) {
		transacoes.remove(transacao);
	}

}
