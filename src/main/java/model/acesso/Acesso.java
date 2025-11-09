package main.java.model.acesso;

import main.java.exceptions.DomainException;
import main.java.model.cliente.Cliente;
import main.java.model.grupo.Grupo;

import java.util.ArrayList;

public class Acesso {

	public final int id;
	String email;
	private final String senha_hash;
	Cliente clienteVinculado;
	ArrayList<Grupo> grupos;

	public Acesso(int id, String email, String senha_hash, Cliente clienteVinculado) {
		this.id = id;
		this.email = email;
		this.senha_hash = senha_hash;
		this.clienteVinculado = clienteVinculado;
		this.grupos = new ArrayList<>();
	}

	public int getId() { return id;	}
	public String getEmail() { return email; }
	public Cliente getClienteVinculado() { return clienteVinculado; }
	public ArrayList<Grupo> getGrupos() { return new ArrayList<>(this.grupos); }

	public void setClienteVinculado(Cliente clienteVinculado) throws DomainException {

		if (this.clienteVinculado != null) {
			throw new DomainException("Acesso já tem um cliente vinculado!");
		}

		this.clienteVinculado = clienteVinculado;
	}

	public void addGrupo(Grupo grupo) throws DomainException {

		if (this.grupos.contains(grupo)) {
			throw new DomainException(
				"Cliente " + this.clienteVinculado.getNome() + "(id: " + this.clienteVinculado.getId() + ")" +
				" já está no grupo " + grupo.getNome() + "(id: " + grupo.getId() + ")"
			);
		}

		this.grupos.add(grupo);
		grupo.addCliente(this.clienteVinculado);
	}

	public void removeGrupo(Grupo grupo) throws DomainException {

		if (!this.grupos.contains(grupo)) {
			throw new DomainException(
				"Cliente " + this.clienteVinculado.getNome() + "(id: " + this.clienteVinculado.getId() + ")" +
				" não está no grupo " + grupo.getNome() + "(id: " + grupo.getId() + ")"
			);
		}

		this.grupos.remove(grupo);
		grupo.removeCliente(this.clienteVinculado);
	}
}
