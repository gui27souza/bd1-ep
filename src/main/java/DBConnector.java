package main.java;

import java.lang.module.ResolutionException;
import java.sql.*;

public class DBConnector {

	Connection conn = null;

	public DBConnector() {
		this.conn = createConnection();
	}

	private Connection createConnection() {

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
			return null;
		}

		Connection conn;

		try{
			conn = DriverManager.getConnection(url, user, password);
			System.out.println("\nConexão estabelecida com sucesso!\n");
		} catch (SQLException e) {
			System.err.println("Erro ao tentar conectar ao banco de dados:\n"+e.getMessage());
			e.printStackTrace();
			return null;
		}

		return conn;
	}
}
