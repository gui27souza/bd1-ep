package main.java.model.grupo;

import main.java.model.cliente.Cliente;

import java.util.ArrayList;

public class Grupo {

	int id;
	String nome;
	GrupoStatus status;
	String dataCriacao;
	String descricao;
	ArrayList<Cliente> clientes;

	public Grupo(int id, String nome, GrupoStatus status, String dataCriacao, String descricao) {
		this.id = id;
		this.nome = nome;
		this.status = status;
		this.dataCriacao = dataCriacao;
		this.descricao = descricao;
		this.clientes = new ArrayList<>();
	}

}
