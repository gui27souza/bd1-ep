package main.java.model.grupo;

import main.java.exceptions.DomainException;
import main.java.model.cliente.Cliente;

import java.util.ArrayList;
import java.util.Objects;

public class Grupo {

	final int id;
	String nome;
	GrupoStatus status;
	final String dataCriacao;
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

	public int getId() { return id;	}
	public String getNome() { return nome; }
	public GrupoStatus getStatus() { return status;	}
	public String getDataCriacao() { return dataCriacao; }
	public String getDescricao() { return descricao; }
	public ArrayList<Cliente> getClientes() { return new ArrayList<>(this.clientes); }

	public void setNome(String nome) { this.nome = nome; }
	public void setStatus(GrupoStatus status) { this.status = status; }
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
