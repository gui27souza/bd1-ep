package main.java;

import java.util.Scanner;
import java.sql.*;
import java.time.LocalDate;

public class EpApplication {

	private static Connection conn = null;
	static Scanner opcao = new Scanner(System.in);

	public static void main(String[] args) {

		String host = System.getenv("EP_DB");
		String dbName = System.getenv("EP_NAME_DB");
		String user = System.getenv("EP_DB_USER");
		String password = System.getenv("EP_DB_PASS");

		String url = String.format("jdbc:postgresql://%s/%s", host, dbName);


		try{

			conn = DriverManager.getConnection(url, user, password);
			System.out.println("\nConexão estabelecida com sucesso!\n");

			var sentinela = 1;
			while(sentinela == 1){

				imprimeMenu(1);

				int numero = opcao.nextInt();
				switch (numero) {

					case 1:
						inserirDado();
					break;

					case 2:
						mostrarTabela();
						excluirDado();
					break;

					case 3:
						mostrarTabela();
						alterarDado();
					break;

					case 4:
						consultarDado();
					break;

					case 5:
						mostrarTabela();
					break;

					case 6:
						relatorioTransacoesPorCpf();
					break;

					default: break;
				}

				imprimeMenu(2);
				sentinela = opcao.nextInt();
			}

		} catch (SQLException e) {
				System.err.println("Erro ao conectar ao banco de dados!");
				e.printStackTrace();
		}
	}

	public static void imprimeMenu(int session){
		if (session == 1){
			System.out.println(
					"===== MENU PRINCIPAL =====\n" +
							"Operações na tabela CLIENTE:\n" +
							"1. Inserir dado\n" +
							"2. Excluir dado\n" +
							"3. Alterar dado\n" +
							"4. Consultar dado\n" +
							"5. Ver tabela completa\n" +
							"\n" +
							"Relatórios e Consultas:\n" +
							"6. Relatório de Transações (Cliente + Transacao + Categoria)\n" +
							"===========================\n" +
							"Escolha uma opção:"
			);
		}else if (session == 2){
			System.out.print("\n0: para encerrar o programa\n" +
							"1: para exibir o menu novamente: ");
		}
	}

	public static void inserirDado() throws SQLException {

		opcao.nextLine();

		System.out.println("Nome: ");
		String nomeInput = opcao.nextLine();

		System.out.println("CPF: ");
		long cpfInput = Long.parseLong(opcao.nextLine());

		System.out.println("Data de Nascimento (YYYY-MM-DD): ");
		String nascimentoInput = opcao.nextLine();

		System.out.println("ID do Plano: ");
		int idPlanoInput = Integer.parseInt(opcao.nextLine());

		String sql = "INSERT INTO cliente (nome, cpf, data_nasc, id_plano) VALUES (?, ?, ?, ?)";


		try (PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, nomeInput);
			stmt.setLong(2, cpfInput);

			LocalDate localDate = LocalDate.parse(nascimentoInput);
			Date sqlDate = Date.valueOf(localDate);
			stmt.setDate(3, sqlDate);

			stmt.setInt(4, idPlanoInput);

			stmt.executeUpdate();
			System.out.println("Salvo com sucesso!");

		} catch (SQLException e) {
			System.err.println("Erro ao inserir dado! Verifique restrições.");
			e.printStackTrace();
		} catch (java.time.format.DateTimeParseException e) {
			System.err.println("Erro de formato de data! Use o padrão YYYY-MM-DD.");
		}
	}

	public static  void excluirDado() throws SQLException {
		opcao.nextLine();

		System.out.println("CPF: ");
		long cpfInput = Long.parseLong(opcao.nextLine());

		String sql = "DELETE FROM cliente WHERE CPF = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, cpfInput);

			int linhasAfetadas = stmt.executeUpdate();
			if (linhasAfetadas > 0) {
				System.out.println("Cliente com CPF " + cpfInput + " EXCLUÍDO com sucesso!");
			} else {
				System.out.println("Nenhum cliente encontrado com o CPF " + cpfInput + "!");
			}

		} catch (SQLException e) {
			System.err.println("Erro ao excluir dado! Verifique restrições.");
			e.printStackTrace();
		}

	}

	public static void alterarDado() throws SQLException {
		opcao.nextLine();

		System.out.print("Digite o CPF do cliente que deseja ALTERAR: ");
		long cpfBusca = Long.parseLong(opcao.nextLine());

		if (!clienteExiste(cpfBusca)) {
			System.out.println("Cliente com CPF " + cpfBusca + " não encontrado no sistema.");
			return;
		}

		System.out.print("Digite o NOVO NOME para o cliente: ");
		String novoNome = opcao.nextLine();

		String sql = "UPDATE cliente SET nome = ? WHERE cpf = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, novoNome);
			stmt.setLong(2, cpfBusca);

			int linhasAfetadas = stmt.executeUpdate();

			if (linhasAfetadas > 0) {
				System.out.println("Nome do cliente com CPF " + cpfBusca + " atualizado com sucesso!");
			} else {
				System.out.println("Nenhuma alteração foi realizada.");
			}

		} catch (SQLException e) {
			System.err.println("Erro ao alterar dado! Falha na comunicação com o banco.");
			e.printStackTrace();
		}
	}

	private static boolean clienteExiste(long cpf) throws SQLException {
		String sql = "SELECT COUNT(cpf) FROM cliente WHERE cpf = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, cpf);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}
		} catch (SQLException e) {
			System.err.println("Erro na verificação de existência do cliente: " + e.getMessage());
		}
		return false;
	}

	public static void consultarDado() throws SQLException {
		opcao.nextLine();

		System.out.print("Digite o CPF do cliente para consulta: ");
		long cpfBusca = Long.parseLong(opcao.nextLine());

		String sql = "SELECT id, nome, cpf, data_nasc, id_plano FROM cliente WHERE cpf = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, cpfBusca);

			try (ResultSet rs = stmt.executeQuery()) {
				if (!rs.isBeforeFirst()) {
					System.out.println("Nenhum cliente encontrado com o CPF " + cpfBusca + ".");
					return;
				}

				System.out.println("--------------------------------------------------------------------------------");
				System.out.printf("%-5s %-30s %-15s %-12s %-8s\n", "ID", "NOME", "CPF", "NASC.", "ID_PLANO");
				System.out.println("--------------------------------------------------------------------------------");

				while (rs.next()) {
					int id = rs.getInt("id");
					String nome = rs.getString("nome");
					int cpf = rs.getInt("cpf");
					Date dataNasc = rs.getDate("data_nasc");
					int idPlano = rs.getInt("id_plano");

					System.out.printf("%-5d %-30s %-15s %-12s %-8d\n", id, nome, cpf, dataNasc.toString(), idPlano);
				}
			}

		} catch (SQLException e) {
			System.err.println("Erro ao realizar consulta específica!");
			e.printStackTrace();
		}
	}

	public static void mostrarTabela() throws SQLException {
		String sql = "SELECT id, nome, cpf, data_nasc, id_plano FROM cliente ORDER BY id";

		try (Statement stmt = conn.createStatement();
				 ResultSet rs = stmt.executeQuery(sql)) {

			System.out.println("\n--- TABELA COMPLETA: CLIENTE ---");
			System.out.printf("%-5s %-30s %-15s %-12s %-8s\n", "ID", "NOME", "CPF", "NASC.", "ID_PLANO");
			System.out.println("--------------------------------------------------------------------------------");

			if (!rs.isBeforeFirst()) {
				System.out.println("A tabela CLIENTE está vazia.");
				return;
			}

			while (rs.next()) {
				int id = rs.getInt("id");
				String nome = rs.getString("nome");
				long cpf = rs.getLong("cpf");
				Date dataNasc = rs.getDate("data_nasc");
				int idPlano = rs.getInt("id_plano");

				System.out.printf("%-5d %-30s %-15s %-12s %-8d\n", id, nome, cpf, dataNasc.toString(), idPlano);
			}
			System.out.println("--------------------------------------------------------------------------------");

		} catch (SQLException e) {
			System.err.println("Erro ao listar a tabela completa!");
			e.printStackTrace();
		}
	}

	public static void relatorioTransacoesPorCpf() throws SQLException {
		opcao.nextLine(); // consumir quebra de linha pendente do Scanner

		System.out.print("Digite o CPF do cliente: ");
		long cpfBusca = Long.parseLong(opcao.nextLine());

		String sql =
				"SELECT c.nome AS cliente, " +
						"       t.descricao AS transacao, " +
						"       t.valor AS valor, " +
						"       cat.nome AS categoria " +
						"FROM Transacao t " +
						"JOIN Cliente c ON t.id_cliente = c.id " +
						"JOIN Categoria cat ON t.id_categoria = cat.id " +
						"WHERE c.cpf = ? " +
						"ORDER BY t.id";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, cpfBusca);

			try (ResultSet rs = stmt.executeQuery()) {
				System.out.printf("%-20s | %-20s | %-10s | %-15s%n",
						"Cliente", "Transação", "Valor", "Categoria");
				System.out.println("---------------------------------------------------------------------");

				boolean temRegistro = false;
				while (rs.next()) {
					temRegistro = true;

					String cliente   = rs.getString("cliente");
					String transacao = rs.getString("transacao");
					double valor     = rs.getDouble("valor");
					String categoria = rs.getString("categoria");

					System.out.printf("%-20s | %-20s | %-10.2f | %-15s%n",
							cliente, transacao, valor, categoria);
				}

				if (!temRegistro) {
					System.out.println("Nenhuma transação encontrada para o CPF " + cpfBusca + ".");
				}
			}

		} catch (SQLException e) {
			System.err.println("Erro ao gerar relatório: " + e.getMessage());
		}
	}

}