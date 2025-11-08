package main.java.service;

import main.java.util.MenuUtil;
import main.java.model.cliente.Cliente;
import main.java.db.DBConnector;
import main.java.exceptions.DomainException;

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

		String[] menuOptions = {
			"Ver todos os clientes",
			"Criar cliente",
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
					MenuUtil.printTabela("CLIENTE", this.dbConnector);
				break;

				// Criar cliente
				case 1:
					CreateCliente.menu(this.dbConnector);
				break;

				// Retornar ao menu principal
				case 2: return;

			}
		}
	}

class CreateCliente {

	static final int idPlanoBasico = 1;

	public static Cliente menu(DBConnector dbConnector) {
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
}

class ReadCliente {
}

class UpdateCliente {}

class DeleteCliente {}