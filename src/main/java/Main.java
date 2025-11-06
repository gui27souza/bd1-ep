package main.java;

import java.sql.SQLException;

public class Main {
	public static void main(String[] args) throws SQLException {

		DBConnector db = new DBConnector();
		System.out.println("Tabelas disponíveis no BD:\n"+db.availableTables+"\n");

		for (String tableName: db.availableTables) {
			Menu.printTabela(tableName, db);
		}
	}
}
