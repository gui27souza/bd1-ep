package main.java;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class EpApplication {

	public static void main(String[] args) {

		String host = System.getenv("EP_DB");
		String dbName = System.getenv("EP_NAME_DB");
		String user = System.getenv("EP_DB_USER");
		String password = System.getenv("EP_DB_PASS");

		String url = String.format("jdbc:postgresql://%s/%s", host, dbName);

        Connection conn = null;

		Scanner opcao = new Scanner(System.in);

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

			var sentinela = 1;
			while(sentinela == 1){

				imprimeMenu(1);

				int numero = opcao.nextInt();
				switch (numero) {
					case 1:
						// inserirDado();
						System.out.println("inserirDado()");
						break;
					case 2:
						// excluirDado();
						System.out.println("excluirDado()");
						break;
					case 3:
						// alterarDado();
						System.out.println("alterarDado()");
						break;
					case 4:
						// fazerConsulta();
						System.out.println("fazerConsulta()");
						break;
					default:
						break;
				}

				imprimeMenu(2);
				sentinela = opcao.nextInt();
			}

			rs.close();
			conn.close();

        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados!");
            e.printStackTrace();
        }
    }

	public static void imprimeMenu(int session){

		if (session == 1){
			System.out.println("\nInsira a opção desejada\n" +
			"1. Inserir dados em uma tabela\n" +
			"2. Excluir dados de um tabela\n" +
			"3. Alterar dados de uma tabela\n" +
			"4. Fazer uma consulta\n");
		}else if (session == 2){
			System.out.print("\n0: para encerrar o programa\n" +
							"1: para exibir o menu novamente: ");
		}
	}

}