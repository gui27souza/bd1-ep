package main.java.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBConnector {


	// === Conexao com Banco de Dados ===
	Connection conn = null;
	// ==================================


	// ==================== Dados do banco ====================

	// Nome das tabelas disponiveis
	ArrayList<String> availableTables = null;

	// ========================================================


	// Construtor
	public DBConnector() throws SQLException {

		// Cria a conexao com o Banco de Dados
		createConnection();

		// Popula a estrutura com dados
		getAvailableTables();
	}

	private void createConnection() {

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
	}

	public Connection getConnection() {
		return conn;
	}

	public ArrayList<String> getAvailableTables() throws SQLException {

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

	public ResultSet executeQuery(String query) throws SQLException {

		if (query == null) {
			System.out.println("Query inválida!\n");
			return null;
		}

		if (this.conn == null || this.conn.isClosed()) {
			throw new SQLException("Conexão com Banco de Dados não estabelecida!\n");
		}

		ResultSet resultSet;
		Statement statement;

		try {
			statement = conn.createStatement();
			resultSet = statement.executeQuery(query);
		} catch (SQLException e) {
			System.err.println("Erro ao executar a query: " + e.getMessage());
			throw e;
		}

		return resultSet;
	}

	public ResultSet executeQuery(String query, List<Object> parameters) throws SQLException {

		if (this.conn == null || this.conn.isClosed()) {
			throw new SQLException("Conexão com Banco de Dados não estabelecida!");
		}

		try {

			PreparedStatement pstmt = conn.prepareStatement(query);

			if (parameters != null) {
				for (int i = 0; i < parameters.size(); i++) {
					Object param = parameters.get(i);
					int index = i + 1;

					if (param instanceof String) {
						pstmt.setString(index, (String) param);
					} else if (param instanceof Integer) {
						pstmt.setInt(index, (Integer) param);
					} else if (param instanceof Long) {
						pstmt.setLong(index, (Long) param);
					} else if (param instanceof Double) {
						pstmt.setDouble(index, (Double) param);
					} else if (param instanceof Boolean) {
						pstmt.setBoolean(index, (Boolean) param);
					} else if (param == null) {
						pstmt.setNull(index, java.sql.Types.VARCHAR);
					}
				}
			}

			return pstmt.executeQuery();

		} catch (SQLException e) {
			System.err.println("Erro ao executar PreparedStatement: " + e.getMessage());
			throw e;
		}
	}

	public ResultSet queryTable(String tableName) throws SQLException {

		ArrayList<String> availableTables = getAvailableTables();

		if (this.availableTables == null || availableTables.isEmpty()) {
			System.out.println("Nenhuma tabela disponível.");
			return null;
		}

		if  (!availableTables.contains(tableName.toUpperCase())) {
			System.out.println("Tabela "+tableName+" não disponível no banco de dados!");
			return null;
		}

		String query = "SELECT * FROM " + tableName;
		ResultSet resultSet = executeQuery(query);

		return resultSet;
	}

}
