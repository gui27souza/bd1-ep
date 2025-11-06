package main.java;

import java.sql.*;
import java.util.ArrayList;

public class DBConnector {

	Connection conn = null;

	ArrayList<String> availableTables = null;

	public DBConnector() throws SQLException {
		createConnection();
	}

	private void createConnection() throws SQLException {

		String host;
		String dbName;
		String user;
		String password;
		String url;

		try {
			host = System.getenv("EP_DB");
			dbName = System.getenv("EP_NAME_DB");
			user = System.getenv("EP_DB_USER");
			password = System.getenv("EP_DB_PASS");
			url = String.format("jdbc:postgresql://%s/%s", host, dbName);
		} catch (Exception e) {
			System.out.println("Erro ao buscar variáveis de ambiente para conexão com o banco de dados:\n"+e.getMessage());
			e.printStackTrace();
			return;
		}

		Connection conn;

		try{
			conn = DriverManager.getConnection(url, user, password);
			System.out.println("\nConexão estabelecida com sucesso!\n");
		} catch (SQLException e) {
			System.err.println("Erro ao tentar conectar ao banco de dados:\n"+e.getMessage());
			e.printStackTrace();
			return;
		}

		this.conn = conn;

		getAvailableTables();
	}

	private ArrayList<String> getAvailableTables() throws SQLException {

		ArrayList<String> availableList;

		try {

			// Caching - Caso já esteja na memória
			if (this.availableTables != null) {
				return this.availableTables;
			}

			DatabaseMetaData metaData = conn.getMetaData();
			ResultSet tables = metaData.getTables(
				null, null, "%",
				new String[]{"TABLE"}
			);

			availableList = new ArrayList<>();
			while (tables.next()) {
				availableList.add(tables.getString("TABLE_NAME").toUpperCase());
			}

			tables.close();
		} catch (SQLException e) {
			this.availableTables = null;
			throw e;
		}

		// Armazena na memória
		this.availableTables = availableList;

		return this.availableTables;
	}

	public void closeConnection() {
		if (conn != null) {
			try {
				conn.close();
				System.out.println("Conexão com o banco de dados fechada.");
			} catch (SQLException e) {
				System.err.println("Erro ao tentar fechar a conexão: " + e.getMessage());
			}
		}
	}

}
