import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

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

			

			System.out.println();

			var sentinela = 1;
			while(sentinela == 1){

				imprimeMenu(1);

				int numero = opcao.nextInt();
				switch (numero) {
					case 1:
						inserirDado();
						// System.out.println("inserirDado()");
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



	public static void inserirDado() throws SQLException {

		String tabelaEscolhida = escolherTabela();
		List<String> foreignKeys = buscarForeignKeys(tabelaEscolhida);

		List<String> nomesColunas = new ArrayList<>();
		List<Integer> tiposColunas = new ArrayList<>();
		buscarColunas(tabelaEscolhida, foreignKeys, nomesColunas, tiposColunas);

		List<Object> valores = coletarValoresUsuario(nomesColunas, tiposColunas);

		executarInsert(tabelaEscolhida, nomesColunas, valores);
	}

	// 1. Listar tabelas e deixar usuário escolher
	private static String escolherTabela() throws SQLException {
		ResultSet rs = buscaTabelas();
		List<String> tabelas = new ArrayList<>();
		int num = 1;

		System.out.println("\nEscolha uma tabela no banco para inserir os dados:");
		while (rs.next()) {
			String nome = rs.getString("TABLE_NAME");
			tabelas.add(nome);
			System.out.println("   " + num + ". " + nome);
			num++;
		}
		rs.close();

		System.out.print("\nDigite o número da tabela: ");
		int escolha = opcao.nextInt();
		opcao.nextLine(); // limpar buffer
		return tabelas.get(escolha - 1);
	}

	public static ResultSet buscaTabelas() throws SQLException{

		DatabaseMetaData meta = conn.getMetaData();
		ResultSet rs = meta.getTables(null, null, "%", new String[] { "TABLE" });
		return rs;
	}

	// 2. Buscar chaves estrangeiras da tabela
	private static List<String> buscarForeignKeys(String tabela) throws SQLException {
		ResultSet fks = conn.getMetaData().getImportedKeys(null, null, tabela);
		List<String> foreignKeys = new ArrayList<>();
		while (fks.next()) {
			foreignKeys.add(fks.getString("FKCOLUMN_NAME"));
		}
		fks.close();
		return foreignKeys;
	}

	// 3. Buscar colunas da tabela (ignorando auto-increment e FKs)
	private static void buscarColunas(
			String tabela,
			List<String> foreignKeys,
			List<String> nomesColunas,
			List<Integer> tiposColunas) throws SQLException {

		ResultSet colunas = conn.getMetaData().getColumns(null, null, tabela, null);

		while (colunas.next()) {
			String coluna = colunas.getString("COLUMN_NAME");
			int tipo = colunas.getInt("DATA_TYPE");
			String isAuto = colunas.getString("IS_AUTOINCREMENT"); // "YES" ou "NO"

			if ("YES".equals(isAuto) || foreignKeys.contains(coluna)) continue;

			nomesColunas.add(coluna);
			tiposColunas.add(tipo);
		}
		colunas.close();
	}

	// 4. Perguntar valores ao usuário e converter
	private static List<Object> coletarValoresUsuario(List<String> nomes, List<Integer> tipos) {
		List<Object> valores = new ArrayList<>();

		for (int i = 0; i < nomes.size(); i++) {
			String coluna = nomes.get(i);
			int tipo = tipos.get(i);

			if (coluna.equalsIgnoreCase("data_nasc"))
				System.out.print("Digite a " + coluna.toUpperCase() + " (YYYY-MM-DD): ");
			else
				System.out.print("Digite o " + coluna.toUpperCase() + ": ");

			String input = opcao.nextLine();
			valores.add(converterValor(input, tipo));
		}
		return valores;
	}

	// 4a. Conversão de tipos
	private static Object converterValor(String input, int tipo) {
		switch (tipo) {
			case Types.INTEGER:
			case Types.SMALLINT:
			case Types.TINYINT: return Integer.parseInt(input);
			case Types.BIGINT: return Long.parseLong(input);
			case Types.FLOAT:
			case Types.DOUBLE:
			case Types.REAL: return Double.parseDouble(input);
			case Types.BOOLEAN:
			case Types.BIT: return Boolean.parseBoolean(input);
			case Types.DATE: return java.sql.Date.valueOf(input);
			default: return input;
		}
	}

	// 5. Montar e executar INSERT
	private static void executarInsert(String tabela, List<String> colunas, List<Object> valores) throws SQLException {
		String sql = "INSERT INTO " + tabela + " (" +
				String.join(", ", colunas) + ") VALUES (" +
				String.join(", ", Collections.nCopies(colunas.size(), "?")) + ")";
		PreparedStatement stmt = conn.prepareStatement(sql);

		for (int i = 0; i < valores.size(); i++) {
			Object v = valores.get(i);
			if (v instanceof Integer) stmt.setInt(i + 1, (Integer) v);
			else if (v instanceof Long) stmt.setLong(i + 1, (Long) v);
			else if (v instanceof Double) stmt.setDouble(i + 1, (Double) v);
			else if (v instanceof Boolean) stmt.setBoolean(i + 1, (Boolean) v);
			else if (v instanceof java.sql.Date) stmt.setDate(i + 1, (java.sql.Date) v);
			else stmt.setString(i + 1, (String) v);
		}

		int linhas = stmt.executeUpdate();
		System.out.println("\nLinhas inseridas: " + linhas);
		stmt.close();
	}

}