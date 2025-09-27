package com.usp.ep;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EpApplication {

	public static void main(String[] args) {

		String host = System.getenv("EP_DB");
		String dbName = System.getenv("EP_NAME_DB");
		String user = System.getenv("EP_DB_USER");
		String password = System.getenv("EP_DB_PASS");

		String url = String.format("jdbc:postgresql://%s/%s", host, dbName);

        Connection conn = null;

        try{

            conn = DriverManager.getConnection(url, user, password);

			System.out.println("\nConexão estabelecida com sucesso!\n");

			DatabaseMetaData meta = conn.getMetaData();
			ResultSet rs = meta.getTables(null, null, "%", new String[] { "TABLE" });

			System.out.println("Tabelas no banco:");
			while (rs.next()) {
				System.out.println("   " + rs.getString("TABLE_NAME"));
			}

			System.out.println();

			rs.close();
			conn.close();

        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados!");
            e.printStackTrace();
        }
    }
}