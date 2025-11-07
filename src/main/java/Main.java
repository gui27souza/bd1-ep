package main.java;

import main.java.db.DBConnector;

import java.sql.SQLException;

public class Main {
	public static void main(String[] args) throws SQLException {

		// Inicializa a conexao com o banco de dados
		DBConnector db = new DBConnector();

		// Menu inicial
		String[] primeirasOpcoes = {
			"Ver todas as tabelas",
			"Buscar tabela específica",
			"Listar o nome das tabelas",
		};

		while (true) {
			int opt = Menu.printOptions(primeirasOpcoes);
			switch (opt) {

				// Encerrar o programa
				case -1:
					System.out.println("\nEncerrando programa...");
					System.exit(0);
				break;

				// Ver todas as tabelas
				case 0:
					for (String tableName : db.availableTables) {
						Menu.printTabela(tableName, db);
						System.out.println();
					}
				break;

				// Buscar tabela específica
				case 1:
					System.out.println();
					String tableName = Menu.readStringInput("Digite o nome da tabela: ");
					Menu.printTabela(tableName, db);
//					System.out.println("\nOperação ainda não implementada!");
				break;

				// Listar o nome das tabelas
				case 2:
					System.out.println("\nTabelas disponíveis no BD:\n" + db.availableTables + "\n");
				break;

			}

			System.out.println();
		}


	}
}
