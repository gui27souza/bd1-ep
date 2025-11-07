package main.java;

import main.java.db.DBConnector;
import main.java.service.ClienteService;

import java.sql.SQLException;

public class Main {
	public static void main(String[] args) throws SQLException {

		// Inicializa a conexao com o banco de dados
		DBConnector db = new DBConnector();

		ClienteService clienteService = new ClienteService(db);

		// Menu inicial
		String[] primeirasOpcoes = {
			"Ver todas as tabelas",
			"Buscar tabela específica",
			"Listar o nome das tabelas",
			"Acessar ClientService"
		};

		while (true) {
			int opt = MenuUtil.printOptions(primeirasOpcoes);
			switch (opt) {

				// Encerrar o programa
				case -1:
					System.out.println("\nEncerrando programa...");
					System.exit(0);
				break;

				// Ver todas as tabelas
				case 0:
					for (String tableName : db.availableTables) {
						MenuUtil.printTabela(tableName, db);
						System.out.println();
					}
				break;

				// Buscar tabela específica
				case 1:
					System.out.println();
					String tableName = MenuUtil.readStringInput("Digite o nome da tabela (ou \\q para voltar): ");
					if (tableName.equals("\\q")) {
						break;
					}
					System.out.println();
					MenuUtil.printTabela(tableName, db);
				break;

				// Listar o nome das tabelas
				case 2:
					System.out.println("\nTabelas disponíveis no BD:\n" + db.availableTables);
				break;

				// Acessar ClientService
				case 3:
					clienteService.menu();
				break;

			}

			System.out.println();
		}
	}
}
