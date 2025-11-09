package main.java.service;

import main.java.db.DBConnector;
import main.java.exceptions.DomainException;
import main.java.model.acesso.Acesso;
import main.java.model.cliente.Cliente;
import main.java.util.menu.MenuUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CadastroService {

	DBConnector dbConnector;

	public CadastroService(DBConnector connector) {
		this.dbConnector = connector;
	}

	public Acesso login(ClienteService clienteService) {

		System.out.println("\n========== Login ==========");
		System.out.println("Insira suas credenciais para acessar a plataforma.");

		String email = MenuUtil.readStringInput("E-mail: ");
		String senha = MenuUtil.readStringInput("Senha: ");

		try {
			return this.verificarCredenciais(email, senha, clienteService);
		} catch (DomainException e) {
			System.out.println("Falha ao realizar Login: " + e.getMessage());
			return null;
		} catch (SQLException e) {
			System.out.println("Erro crítico ao acessar o banco de dados!\n" + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public Acesso verificarCredenciais(String email, String senha, ClienteService clienteService) throws SQLException, DomainException {

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

				Cliente clienteVinculado = clienteService.findById(id);

				if (clienteVinculado == null) {
					throw new DomainException("Erro de integridade: Cliente vinculado (ID: " + id + ") não encontrado.");
				}

				Acesso acesso = new Acesso(id, email, senha, clienteVinculado);

				return acesso;
			}

			throw new DomainException("Credenciais inválidas! E-mail ou senha incorretos.");
		}
	}
}
