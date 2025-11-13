package main.java.service;

import main.java.db.DBConnector;
import main.java.exceptions.DomainException;
import main.java.model.Acesso;
import main.java.model.Cliente;
import main.java.model.Grupo;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CadastroService {

	DBConnector dbConnector;
	ClienteService clienteService;
	GrupoService grupoService;

	public CadastroService(DBConnector connector, ClienteService clienteService, GrupoService grupoService) {
		this.dbConnector = connector;
		this.clienteService = clienteService;
		this.grupoService = grupoService;
	}

	// ==================== CADASTRO ====================

	public Acesso processarCadastro(
		String nome, String cpf, String email, String senha, Date dataNascimento
	) throws DomainException, SQLException {

		Cliente novoCliente = this.clienteService.createCliente(nome, cpf, dataNascimento);

		try{
			createCredenciais(novoCliente.getId(), email, senha);
		} catch (SQLException e) {
			this.clienteService.deleteCliente(novoCliente);
			throw e;
		}

		Acesso novoAcesso = new Acesso(novoCliente.getId(), email, senha, novoCliente);

		return novoAcesso;
	}

	public void createCredenciais(int id, String email, String senha) throws SQLException, DomainException {

		String query = "INSERT INTO Credenciais (id_cliente, email, senha_hash) VALUES (?, ?, ?)";
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(id);
		parameters.add(email);
		parameters.add(senha);

		try {
			int rowsAffected = this.dbConnector.executeUpdate(query, parameters);

			if (rowsAffected == 0) {
				throw new SQLException("Falha ao criar credenciais para o cliente de ID " + id + "Realizando rollback.");
			} else if (rowsAffected != 1) {
				throw new RuntimeException("ERRO DE INTEGRIDADE: Mais de uma credencial criada. Rows affected: " + rowsAffected);
			}
		} catch (SQLException e) {
			if (e.getSQLState().equals("23505")) {
				throw new DomainException("Já existe uma conta cadastrada com este e-mail.");
			}
			throw e;
		}

	}

	// ==================================================



	// ==================== LOGIN ====================

	public Acesso verificarCredenciais(String email, String senha) throws SQLException, DomainException {

		if ( email == null || senha == null || email.isEmpty() || senha.isEmpty()) {
			throw new DomainException("Email e senha não podem ser vazios!");
		}

		String query = "SELECT id_cliente FROM Credenciais WHERE email = ? AND senha_hash = ?";
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(email);
		parameters.add(senha);

		try (ResultSet resultSet = this.dbConnector.executeQuery(query, parameters)) {

			if (resultSet.next()) {

				int id = resultSet.getInt("id_cliente");

				Cliente clienteVinculado = this.clienteService.findById(id);

				if (clienteVinculado == null) {
					throw new DomainException("Erro de integridade: Cliente vinculado (ID: " + id + ") não encontrado.");
				}

				ArrayList<Grupo> grupos = this.grupoService.getGrupos(clienteVinculado);

				Acesso acesso = new Acesso(id, email, senha, clienteVinculado, grupos);

				return acesso;
			}

			throw new DomainException("Credenciais inválidas! E-mail ou senha incorretos.");
		}
	}

	// ===============================================

	// ==================== UPDATE ====================

	public void updateEmail(int idCliente, String novoEmail) throws DomainException, SQLException {
		
		if (novoEmail == null || novoEmail.trim().isEmpty()) {
			throw new DomainException("O e-mail não pode ser vazio.");
		}

		String query = "UPDATE Credenciais SET email = ? WHERE id_cliente = ?";
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(novoEmail);
		parameters.add(idCliente);

		try {
			int rowsAffected = this.dbConnector.executeUpdate(query, parameters);

			if (rowsAffected == 0) {
				throw new SQLException("Nenhuma credencial encontrada para o cliente ID " + idCliente);
			}
		} catch (SQLException e) {
			if (e.getSQLState().equals("23505")) {
				throw new DomainException("Já existe uma conta cadastrada com este e-mail.");
			}
			throw e;
		}
	}

	// ================================================
}
