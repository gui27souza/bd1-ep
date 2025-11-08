package main.java.service;

import main.java.util.menu.MenuUtil;
import main.java.model.cliente.Cliente;
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

	static DBConnector dbConnector;

	public ClienteService(DBConnector dbConnector) {
		ClienteService.dbConnector = dbConnector;
	}

	public void menu() throws DomainException, SQLException {

		String[] menuOptions = {
			"Ver todos os clientes",
			"Criar cliente",
			"Buscar cliente",
			"Retornar ao menu principal"
		};

		while (true) {
			System.out.println("\n===============================================");
			System.out.println("=============== Cliente Service ===============");
			System.out.println("===============================================\n");
			int opt = MenuUtil.printOptions(menuOptions);

			switch (opt) {

				// Encerrar o programa
				case -1:
					System.out.println("\nEncerrando programa...");
					System.exit(0);
				break;

				// Ver todos os clientes
				case 0:
					MenuUtil.printTabela("CLIENTE", dbConnector);
				break;

				// Criar cliente
				case 1:
					menuCreate();
				break;

				// Buscar cliente
				case 2:
					menuRead();
				break;

				// Retornar ao menu principal
				case 3: return;

			}
		}
	}

	// ==================== CRUD Section ==================== //


	// ===== Create ====================

	static final int idPlanoBasico = 1;

	public static Cliente menuCreate() {
		System.out.println("Digite os dados do cliente a ser inserido:");

		String nome = MenuUtil.readStringInput("\tNome: ");
		Long cpf = MenuUtil.readLongInput("\tCpf: ");
		String dataNascimentoStr = MenuUtil.readStringInput("\tData de Nascimento (YYYY-MM-DD): ");

		Cliente novoCliente = null;

		try {
			LocalDate localDate = LocalDate.parse(dataNascimentoStr);
			Date dataNascimentoSqlDate = Date.valueOf(localDate);

			novoCliente = createCliente(nome, cpf, dataNascimentoSqlDate, dbConnector);
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

	public static Cliente createCliente(String nome, long cpf, Date dataNascimento, DBConnector dbConnector) throws SQLException, DomainException {

		if (nome == null || nome.trim().isEmpty()) {
			throw new DomainException("O nome do cliente não pode ser vazio.");
		}
		if (cpf <= 0) {
			throw new DomainException("CPF inválido.");
		}

		Cliente novoCliente = new Cliente(nome, cpf, dataNascimento, idPlanoBasico);

		String query = "INSERT INTO cliente (nome, cpf, data_nasc, id_plano) VALUES (?, ?, ?, ?) RETURNING id";

		try (PreparedStatement pstmt = dbConnector.getConnection().prepareStatement(query)) {

			pstmt.setString(1, novoCliente.getNome());
			pstmt.setLong(2, novoCliente.getCpf());
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

	public static Cliente menuRead() throws DomainException, SQLException {

		Cliente clienteBuscado;

		String[] menuOptions = {
			"Id",
			"Nome",
			"CPF",
			"Retornar ao menu Cliente Service"
		};

		while (true) {

			System.out.println("\n=============== Busca Cliente ===============\n");
			int opt = MenuUtil.printOptions(menuOptions);

			switch (opt) {

				// Encerrar o programa
				case -1:
					System.out.println("\nEncerrando programa...");
					System.exit(0);
				break;

				// Busca por Id
				case 0:
					int idInput = MenuUtil.readIntInput("Digite o Id a ser buscado: ");
					clienteBuscado = findById(idInput, dbConnector);
					if (clienteBuscado == null) {
						System.out.println("\nNenhum cliente com o Id "+idInput+" encontrado!");
					} else {
						MenuUtilCliente.printCliente(clienteBuscado);
					}
				return clienteBuscado;

				// Busca por nome
				case 1:
					System.out.println("Operação ainda não implementada!");
				break;

				// Busca por CPF
				case 2:
					System.out.println("Operação ainda não implementada!");
				break;


				// Retornar ao menu anterior
				case 3: return null;
			}
		}
	}

	public static Cliente findById(int id, DBConnector dbConnector) throws DomainException, SQLException {

		if (id <= 0) {
			throw new DomainException("ID inválido, deve ser positivo");
		}

		String query = "SELECT id, nome, cpf, data_nasc, id_plano FROM cliente WHERE id = ?";
		List<Object> parameters =  new ArrayList<>();
		parameters.add(id);

		ResultSet resultSet = dbConnector.executeQuery(query, parameters);

		if (resultSet.next()) {
			String nome = resultSet.getString("nome");
			long cpf = resultSet.getLong("cpf");
			Date dataNascimento = resultSet.getDate("data_nasc");
			int  idPlano = resultSet.getInt("id_plano");
			Cliente clienteBuscado = new Cliente(id, nome, cpf, dataNascimento, idPlano);

			resultSet.close();
			return clienteBuscado;
		}

		resultSet.close();
		return null;
	}

	public static Cliente findByName(String name) {
		return null;
	}

	public static Cliente findByCpf(long cpf) {

	// ===== Update ====================

	public static Cliente menuUpdate() throws DomainException, SQLException {
		return null;
	}


	// ===== Delete ====================

	public static void menuDelete() throws DomainException, SQLException {
}