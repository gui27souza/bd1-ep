package main.java.service;

import main.java.db.DBConnector;
import main.java.exceptions.DomainException;
import main.java.model.Acesso;
import main.java.model.Cliente;
import main.java.model.Grupo;
import main.java.util.menu.MenuUtil;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class CadastroService {

	DBConnector dbConnector;
	ClienteService clienteService;

	public CadastroService(DBConnector connector, ClienteService clienteService) {
		this.dbConnector = connector;
		this.clienteService = clienteService;
	}


	public Acesso menuAcesso() {

		String header =
			"\n========================" +
			"==== Login/Cadastro ====" +
			"========================\n"
		;

		String[] menuOptions = {
			"Fazer Login",
			"Fazer cadastro"
		};

		Acesso acessoAtual = null;

		while (acessoAtual == null) {

			int opt = MenuUtil.printOptions(menuOptions, header, true);

			switch (opt) {

				case 0:
					acessoAtual = login();
				break;

				case 1:
					acessoAtual = cadastro();
				break;

			}

			System.out.println();
		}
		return acessoAtual;
	}



	// ==================== CADASTRO ====================

	public Acesso cadastro() {

		MenuUtil.limparConsole();

		System.out.println("\n========== Cadastro ==========");
		System.out.println("Insira suas credenciais para criar uma conta na plataforma.");

		String cpf = MenuUtil.readStringInput("CPF: ");
		String email = MenuUtil.readStringInput("E-mail: ");
		String senha = MenuUtil.readStringInput("Senha: ");
		String nome = MenuUtil.readStringInput("Nome: ");
		String dataNascStr = MenuUtil.readStringInput("Data de Nascimento (YYYY-MM-DD): ");

		try {

			if (nome.trim().isEmpty()) {
				throw new DomainException("O nome é obrigatório!");
			}

			if (cpf.length() != 11) {
				throw new DomainException("O CPF deve conter 11 dígitos!");
			}

			LocalDate localDate = LocalDate.parse(dataNascStr);
			Date dataNascimento = Date.valueOf(localDate);

			return processarCadastro(nome, cpf, email, senha, dataNascimento);

		} catch (java.time.format.DateTimeParseException e) {
			System.err.println("ERRO: Formato de data inválido. Use YYYY-MM-DD.");
		} catch (DomainException e) {
			System.err.println("Falha no Cadastro: " + e.getMessage());
		} catch (SQLException e) {
			System.err.println("Erro crítico ao acessar o banco de dados." + e.getMessage());
			e.printStackTrace();
		}

		return null;
	}

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

	public void createCredenciais(int id, String email, String senha) throws SQLException {

		String query = "INSERT INTO Credenciais (id_cliente, email, senha_hash) VALUES (?, ?, ?)";
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(id);
		parameters.add(email);
		parameters.add(senha);

		int rowsAffected = this.dbConnector.executeUpdate(query, parameters);

		if (rowsAffected == 0) {
			throw new SQLException("Falha ao criar credenciais para o cliente de ID " + id + "Realizando rollback.");
		} else if (rowsAffected != 1) {
			throw new RuntimeException("ERRO DE INTEGRIDADE: Mais de uma credencial criada. Rows affected: " + rowsAffected);
		}

	}

	// ==================================================



	// ==================== LOGIN ====================

	public Acesso login() {

		MenuUtil.limparConsole();

		System.out.println("\n========== Login ==========");
		System.out.println("Insira suas credenciais para acessar a plataforma.");

		String email = MenuUtil.readStringInput("E-mail: ");
		String senha = MenuUtil.readStringInput("Senha: ");

		try {
			return this.verificarCredenciais(email, senha);
		} catch (DomainException e) {
			System.out.println("Falha ao realizar Login: " + e.getMessage());
			return null;
		} catch (SQLException e) {
			System.out.println("Erro crítico ao acessar o banco de dados!\n" + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

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

				Acesso acesso = new Acesso(id, email, senha, clienteVinculado);

				return acesso;
			}

			throw new DomainException("Credenciais inválidas! E-mail ou senha incorretos.");
		}
	}

	// ===============================================
}
