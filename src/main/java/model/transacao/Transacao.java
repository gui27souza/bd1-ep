package main.java.model.transacao;

import java.sql.Timestamp;

public abstract class Transacao {

	int id;
	int id_cliente;
	int id_grupo;
	float valor;
	CategoriaTransacao categoria;
	String descricao;
	Timestamp dataTransacao;

	public Transacao(
		int id, int id_cliente, int id_grupo, float valor, CategoriaTransacao categoria, String descricao, Timestamp dataTransacao
	) {
		this.id = id;
		this.id_cliente = id_cliente;
		this.id_grupo = id_grupo;
		this.valor = valor;
		this.categoria = categoria;
		this.descricao = descricao;
		this.dataTransacao = dataTransacao;
	}

	public Transacao(
		int id_cliente, int id_grupo, float valor, CategoriaTransacao categoria, String descricao, Timestamp dataTransacao
	) {
		this.id_cliente = id_cliente;
		this.id_grupo = id_grupo;
		this.valor = valor;
		this.categoria = categoria;
		this.descricao = descricao;
		this.dataTransacao = dataTransacao;
	}

	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	public int getId_cliente() { return id_cliente; }
	public int getId_grupo() { return id_grupo; }
	public void setId_grupo(int id_grupo) { this.id_grupo = id_grupo; }
	public float getValor() { return valor; }
	public void setValor(float valor) { this.valor = valor; }
	public CategoriaTransacao getCategoria() { return categoria; }
	public void setCategoria(CategoriaTransacao categoria) { this.categoria = categoria; }
	public String getDescricao() { return descricao; }
	public void setDescricao(String descricao) { this.descricao = descricao; }
	public Timestamp getDataTransacao() { return dataTransacao; }
}
