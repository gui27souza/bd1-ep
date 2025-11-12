package main.java.model;

import main.java.exceptions.DomainException;

import java.util.ArrayList;

public class Acesso {

	public final int id;
	String email;
	private final String senha_hash;
	Cliente cliente;
	ArrayList<Grupo> grupos;

	public Acesso(int id, String email, String senha_hash, Cliente cliente) {
		this.id = id;
		this.email = email;
		this.senha_hash = senha_hash;
		this.cliente = cliente;
		this.grupos = new ArrayList<>();
	}

	public int getId() { return id;	}
	public String getEmail() { return email; }
	public Cliente getCliente() { return cliente; }
	public ArrayList<Grupo> getGrupos() { return new ArrayList<>(this.grupos); }

	public void setCliente(Cliente cliente) throws DomainException {

		if (this.cliente != null) {
			throw new DomainException("Acesso já tem um cliente vinculado!");
		}

		this.cliente = cliente;
	}

	public void addGrupo(Grupo grupo) throws DomainException {

		if (this.grupos.contains(grupo)) {
			throw new DomainException(
				"Cliente " + this.cliente.getNome() + "(id: " + this.cliente.getId() + ")" +
				" já está no grupo " + grupo.getNome() + "(id: " + grupo.getId() + ")"
			);
		}

		this.grupos.add(grupo);
		grupo.addCliente(this.cliente);
	}

	public void removeGrupo(Grupo grupo) throws DomainException {

		if (!this.grupos.contains(grupo)) {
			throw new DomainException(
				"Cliente " + this.cliente.getNome() + "(id: " + this.cliente.getId() + ")" +
				" não está no grupo " + grupo.getNome() + "(id: " + grupo.getId() + ")"
			);
		}

		this.grupos.remove(grupo);
		grupo.removeCliente(this.cliente);
	}
}
