package main.java.service;

import main.java.util.menu.MenuUtil;
import main.java.model.Cliente;
import main.java.db.DBConnector;
import main.java.exceptions.DomainException;
import main.java.util.menu.MenuUtilCliente;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ClienteService {

	DBConnector dbConnector;

	public ClienteService(DBConnector dbConnector) {
		this.dbConnector = dbConnector;
	}

	public void menu() throws DomainException, SQLException {

		String header =
			"\n===============================================" +
			"\n=============== Cliente Service ===============" +
			"===============================================\n"
		;
		String[] menuOptions = {
			"Ver todos os clientes",
			"Criar cliente",
			"Buscar cliente",
			"Atualizar cliente",
			"Deletar cliente",
			"Retornar ao menu principal"
		};

		while (true) {

			int opt = MenuUtil.printOptions(menuOptions, header, true);

			switch (opt) {

				// Ver todos os clientes
				case 0:
					MenuUtil.printTabela("CLIENTE", this.dbConnector);
					break;

				// Criar cliente
				case 1:
					menuCreate();
					break;

				// Buscar cliente
				case 2:
					menuRead();
					break;

				// Atualizar cliente
				case 3:
					// menuUpdate();
					System.out.println("\nOperação ainda não implementada!");
					break;

				// Deletar cliente
				case 4:
					menuDelete();
					break;

				// Retornar ao menu principal
				case 5:
					return;
			}
		}
	}


	// ==================== CRUD Section ==================== //


	// ===== Create ====================

	final int idPlanoBasico = 1;

	public Cliente menuCreate() {
		System.out.println("Digite os dados do cliente a ser inserido:");

		String nome = MenuUtil.readStringInput("\tNome: ");
		String cpf = MenuUtil.readStringInput("\tCpf: ");
		String dataNascimentoStr = MenuUtil.readStringInput("\tData de Nascimento (YYYY-MM-DD): ");

		Cliente novoCliente = null;

		try {
			LocalDate localDate = LocalDate.parse(dataNascimentoStr);
			Date dataNascimentoSqlDate = Date.valueOf(localDate);

			novoCliente = createCliente(nome, cpf, dataNascimentoSqlDate);
			System.out.println("Cliente " + novoCliente.getNome() + " cadastrado com sucesso! ID: " + novoCliente.getId());

		} catch (DateTimeParseException e) {
			System.err.println("ERRO DE ENTRADA: O formato da data está incorreto. Use YYYY-MM-DD.");
		} catch (DomainException e) {
			System.err.println("ERRO DE CADASTRO: " + e.getMessage());
		} catch (SQLException e) {
			System.err.println("\nERRO CRÍTICO NO BANCO DE DADOS!");
			System.err.println("Não foi possível completar a operação. Detalhes: " + e.getMessage());
			e.printStackTrace();
		}

		return novoCliente;
	}

	public Cliente createCliente(String nome, String cpf, Date dataNascimento) throws SQLException, DomainException {

		if (nome == null || nome.trim().isEmpty()) {
			throw new DomainException("O nome do cliente não pode ser vazio.");
		}
		if (cpf.length() != 11) {
			throw new DomainException("CPF inválido, deve conter 11 dígitos numéricos.");
		}
		try {
			Long.parseLong(cpf);
		} catch (NumberFormatException e) {
			throw new DomainException("CPF inválido, deve conter 11 dígitos numéricos.");
		}

		Cliente novoCliente = new Cliente(nome, cpf, dataNascimento, this.idPlanoBasico);

		String query = "INSERT INTO cliente (nome, cpf, data_nasc, id_plano) VALUES (?, ?, ?, ?) RETURNING id";

		try (PreparedStatement pstmt = this.dbConnector.getConnection().prepareStatement(query)) {

			pstmt.setString(1, novoCliente.getNome());
			pstmt.setString(2, novoCliente.getCpf());
			pstmt.setDate(3, novoCliente.getDataNascimento());
			pstmt.setInt(4, novoCliente.getIdPlano());

			try (ResultSet rs = pstmt.executeQuery()) {

				if (rs.next()) {

					int novoId = rs.getInt("id");

					novoCliente.setId(novoId);

					return novoCliente;
				} else {
					throw new SQLException("Falha ao obter o ID do cliente após a inserção.");
				}
			}

		} catch (SQLException e) {

			if (e.getSQLState().equals("23505")) {
				throw new DomainException("Já existe um cliente cadastrado com este CPF.");
			}

			throw e;
		}
	}


	// ===== Read ====================

	public Cliente menuRead() throws DomainException, SQLException {

		Cliente clienteBuscado;

		String header = "\n=============== Busca Cliente ===============\n";
		String[] menuOptions = {
			"Id",
			"Nome",
			"CPF",
			"Retornar ao menu Cliente Service"
		};

		while (true) {

			int opt = MenuUtil.printOptions(menuOptions, header, false);

			switch (opt) {

				// Busca por Id
				case 0:
					int idInput = MenuUtil.readIntInput("Digite o Id a ser buscado: ");
					clienteBuscado = findById(idInput);
					if (clienteBuscado == null) {
						System.out.println("\nNenhum cliente com o Id " + idInput + " encontrado!");
					} else {
						MenuUtilCliente.printCliente(clienteBuscado);
					}
					return clienteBuscado;

				// Busca por nome
				case 1:
					String nomeInput = MenuUtil.readStringInput("Digite o nome a ser buscado: ");
					ArrayList<Cliente> clientesBuscados = findByName(nomeInput);
					System.out.println(clientesBuscados.size() + " clientes encontrados!\n");
					MenuUtilCliente.printListaCliente(clientesBuscados);
					clienteBuscado = MenuUtilCliente.chooseClienteFromLista(clientesBuscados);
					return clienteBuscado;

				// Busca por CPF
				case 2:
					String cpfInput = MenuUtil.readStringInput("Digite o CPF a ser buscado: ");
					clienteBuscado = findByCpf(cpfInput);
					if (clienteBuscado == null) {
						System.out.println("\nNenhum cliente com o CPF " + cpfInput + " encontrado!");
					} else {
						MenuUtilCliente.printCliente(clienteBuscado);
					}
					return clienteBuscado;


				// Retornar ao menu anterior
				case 3:
					return null;
			}
		}
	}

	public Cliente findById(int id) throws DomainException, SQLException {

		if (id <= 0) {
			throw new DomainException("ID inválido, deve ser positivo");
		}

		String query = "SELECT id, nome, cpf, data_nasc, id_plano FROM cliente WHERE id = ?";
		List<Object> parameters = new ArrayList<>();
		parameters.add(id);

		try (ResultSet resultSet = this.dbConnector.executeQuery(query, parameters)) {

			if (resultSet.next()) {
				String nome = resultSet.getString("nome");
				String cpf = resultSet.getString("cpf");
				Date dataNascimento = resultSet.getDate("data_nasc");
				int idPlano = resultSet.getInt("id_plano");
				Cliente clienteBuscado = new Cliente(id, nome, cpf, dataNascimento, idPlano);

				return clienteBuscado;
			}

			return null;
		}
	}

	public ArrayList<Cliente> findByName(String name) throws SQLException {

		String query = "SELECT id, nome, cpf, data_nasc, id_plano FROM cliente WHERE nome LIKE ?";
		List<Object> parameters = new ArrayList<>();
		parameters.add("%" + name + "%");

		ArrayList<Cliente> clientesBuscados = new ArrayList<>();

		try (ResultSet resultSet = this.dbConnector.executeQuery(query, parameters)) {

			while (resultSet.next()) {

				int id = resultSet.getInt("id");
				String nomeDB = resultSet.getString("nome");
				String cpf = resultSet.getString("cpf");
				Date dataNascimento = resultSet.getDate("data_nasc");
				int idPlano = resultSet.getInt("id_plano");

				Cliente cliente = new Cliente(id, nomeDB, cpf, dataNascimento, idPlano);
				clientesBuscados.add(cliente);
			}
		}

		return clientesBuscados;
	}

	public Cliente findByCpf(String cpf) throws DomainException, SQLException {

		if (cpf.length() != 11) {
			throw new DomainException("CPF inválido, deve conter 11 dígitos.");
		}

		try {
			Long.parseLong(cpf);
		} catch (NumberFormatException e) {
			throw new DomainException("CPF inválido, deve conter 11 dígitos numéricos.");
		}

		String query = "SELECT id, nome, cpf, data_nasc, id_plano FROM cliente WHERE cpf = ?";
		List<Object> parameters = new ArrayList<>();
		parameters.add(cpf);

		try (ResultSet resultSet = this.dbConnector.executeQuery(query, parameters)) {

			if (resultSet.next()) {
				int id = resultSet.getInt("id");
				String nome = resultSet.getString("nome");
				Date dataNascimento = resultSet.getDate("data_nasc");
				int idPlano = resultSet.getInt("id_plano");
				Cliente clienteBuscado = new Cliente(id, nome, cpf, dataNascimento, idPlano);

				return clienteBuscado;
			}

			return null;
		}
	}


	// ===== Update ====================

	public Cliente menuUpdate() throws DomainException, SQLException {

		Cliente clienteBuscado = menuRead();

		return null;
	}


	// ===== Delete ====================

	public void menuDelete() throws DomainException, SQLException {

		Cliente clienteBuscado = menuRead();

		if (clienteBuscado == null) {
			System.out.println("\nOperação de exclusão cancelada.");
			return;
		}

		System.out.println("\n--=== Confirmação de Exclusão ===--");
		MenuUtilCliente.printCliente(clienteBuscado);

		String confirmacao = MenuUtil.readStringInput("Tem certeza que deseja DELETAR o clienta acima? (S/N): ");

		if (confirmacao.equalsIgnoreCase("S")) {
			try {
				deleteCliente(clienteBuscado);
				System.out.println("Cliente ID " + clienteBuscado.getId() + " excluído com sucesso.");
			} catch (SQLException e) {
				System.err.println("\nERRO CRÍTICO NO BANCO: Falha na exclusão. Detalhes: " + e.getMessage());
				e.printStackTrace();
			}
		} else {
			System.out.println("\nOperação de exclusão cancelada pelo usuário.");
		}
	}

	public void deleteCliente(Cliente cliente) throws DomainException, SQLException {

		if (cliente == null) {
			throw new DomainException("Cliente nulo não pode ser deletado!");
		}

		String query = "DELETE FROM cliente WHERE cliente.id = ?";
		List<Object> parameters = new ArrayList<>();
		parameters.add(cliente.getId());

		int rowsAffected = this.dbConnector.executeUpdate(query, parameters);

		if (rowsAffected == 0) {
			throw new SQLException("Falha ao deletar cliente ID " + cliente.getId() + ". Registro não encontrado.");
		} else if (rowsAffected != 1) {
			throw new RuntimeException("ERRO DE INTEGRIDADE: Mais de um cliente deletado. Rows affected: " + rowsAffected);
		}

	}

	public void deleteCliente(int id) throws DomainException, SQLException {

	}

	public void deleteCliente(String cpf) throws DomainException, SQLException {

	}
}