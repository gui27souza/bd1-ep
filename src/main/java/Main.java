package main.java;

import java.sql.SQLException;

public class Main {
	public static void main(String[] args) throws SQLException {

		DBConnector db = new DBConnector();

		String[] primeirasOpcoes = {
			"Ver todas as tabelas",
			"Buscar tabela específica",
			"Listar o nome das tabelas",
		};

		while (true) {

			int opt = Menu.printOptions(primeirasOpcoes);

			switch (opt) {

				case -1:
					System.out.println("\nEncerrando programa...");
					System.exit(0);
				break;

				case 0:
					for (String tableName : db.availableTables) {
						Menu.printTabela(tableName, db);
						System.out.println();
					}
				break;

				case 1:
					System.out.println();
					String tableName = Menu.readStringInput("Digite o nome da tabela: ");
					Menu.printTabela(tableName, db);
//					System.out.println("\nOperação ainda não implementada!");
				break;

				case 2:
					System.out.println("\nTabelas disponíveis no BD:\n" + db.availableTables + "\n");
				break;

			}

			System.out.println();
		}


	}
}
