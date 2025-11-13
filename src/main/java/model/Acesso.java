package main.java.model;

import main.java.exceptions.DomainException;

import java.util.ArrayList;

public class Acesso {

	public final int id;
	String email;
	private final String senha_hash;
	Cliente cliente;
	ArrayList<Grupo> grupos;

	public Acesso(int id, String email, String senha_hash, Cliente cliente, ArrayList<Grupo> grupos) {
		this.id = id;
		this.email = email;
		this.senha_hash = senha_hash;
		this.cliente = cliente;
		this.grupos = grupos;
	}

	public int getId() { return id;	}
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public Cliente getCliente() { return cliente; }
	public ArrayList<Grupo> getGrupos() { return new ArrayList<>(this.grupos); }
	public void setGrupos(ArrayList<Grupo> grupos) { this.grupos = grupos; }

	public void setCliente(Cliente cliente) throws DomainException {

		if (this.cliente != null) {
			throw new DomainException("Acesso já tem um cliente vinculado!");
		}

		this.cliente = cliente;
	}

}
