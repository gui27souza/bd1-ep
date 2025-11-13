package main.java.model;

import main.java.exceptions.DomainException;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Grupo {

	int id = -1;
	String nome;
	String status;
	final Date dataCriacao;
	String descricao;
	ArrayList<Cliente> clientes;

	public Grupo(int id, String nome, String status, Date dataCriacao, String descricao) {
		this.id = id;
		this.nome = nome;
		this.status = status;
		this.dataCriacao = dataCriacao;
		this.descricao = descricao;
		this.clientes = new ArrayList<>();
	}

	public Grupo(String nome, String status, Date dataCriacao, String descricao) {
		this.nome = nome;
		this.status = status;
		this.dataCriacao = dataCriacao;
		this.descricao = descricao;
		this.clientes = new ArrayList<>();
	}

	public int getId() { return id;	}
	public String getNome() { return nome; }
	public String getStatus() { return status;	}
	public Date getDataCriacao() { return dataCriacao; }
	public String getDescricao() { return descricao; }
	public ArrayList<Cliente> getClientes() { return new ArrayList<>(this.clientes); }

	public void setId(int id) throws DomainException {
		if (this.id == -1) {
			this.id = id;
		} else {
			throw new DomainException("Não é possível alterar um id de grupo já setado!");
		}
	}
	public void setNome(String nome) { this.nome = nome; }
	public void setStatus(String status) { this.status = status; }
	public void setDescricao(String descricao) { this.descricao = descricao; }

	public void addCliente(Cliente cliente) throws DomainException {

		if (this.clientes.contains(cliente)) {
			throw new DomainException(
				"Cliente " + cliente.getNome() + "(id: " + cliente.getId() + ")" +
				" já está no grupo " + this.nome + "(id: " + this.id + ")"
			);
		}

		this.clientes.add(cliente);
	}

	public void removeCliente(Cliente cliente) throws DomainException {

		if (!this.clientes.contains(cliente)) {
			throw new DomainException(
				"Cliente " + cliente.getNome() + "(id: " + cliente.getId() + ")" +
				" não está no grupo " + this.nome + "(id: " + this.id + ")"
			);
		}

		this.clientes.remove(cliente);
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Grupo grupo = (Grupo) o;
		return id == grupo.id;
	}
	public int hashCode() {
		return Objects.hash(id);
	}
}
