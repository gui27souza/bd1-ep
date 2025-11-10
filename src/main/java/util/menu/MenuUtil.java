package main.java.util.menu;

import main.java.db.DBConnector;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class MenuUtil {

	static Scanner opcao = new Scanner(System.in);

	public static String readStringInput(String prompt) {
		System.out.print(prompt);
		return opcao.nextLine().trim();
	}

	public static int readIntInput(String prompt) {
		System.out.print(prompt);
		return Integer.parseInt(opcao.nextLine().trim());
	}

	public static Long readLongInput(String prompt) {
		System.out.print(prompt);
		return Long.parseLong(opcao.nextLine().trim());
	}

	public static void limparConsole() {

		//noinspection ConstantValue
		if (true)
			return;

		try {
			final String os = System.getProperty("os.name");

			if (os.contains("Windows")) {
				new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			} else {
				System.out.print("\033[H\033[2J");
				System.out.flush();
			}
		} catch (final Exception ignored) {}
	}

	/**
	 * @param options Vetor de Strings que contém as opções a serem impressos
	 * @return Posição do array OU -1 (usuário quer encerrar o programa)
	 */
	public static int printOptions(String[] options, String header, boolean clearScreen) {

		if (options.length == 0) {
			System.out.println("Menu de opções vazio!");
			throw new RuntimeException();
		}

		boolean lastInputInvalid = false;

		while (true) {

			if (clearScreen)
				limparConsole();

			System.out.println(header);

			System.out.println("Selecione uma opção abaixo:\n");
			for (int i = 0; i < options.length; i++) {
				System.out.println((i + 1) + " - " + options[i]);
			}
			System.out.println("\n0 - Encerrar o programa");

			if (lastInputInvalid) {
				System.out.println("Opção inválida!");
				lastInputInvalid = false;
			} else {
				System.out.println();
			}

			System.out.print("Digite a opção: ");

			int inputOpcao;

			try{
				String inputLine = opcao.nextLine().trim();

				if (inputLine.isEmpty()) {
					throw new NumberFormatException();
				}

				inputOpcao = Integer.parseInt(inputLine);

				if (inputOpcao == 0){
					System.out.println("Encerrando programa");
					System.exit(0);
				}

				if ((inputOpcao - 1) >= options.length || inputOpcao < 0) {
					throw new NumberFormatException();
				}

			} catch (InputMismatchException | NumberFormatException e) {
				lastInputInvalid = true;
				continue;
			}

			return inputOpcao-1;
		}
	}

	/**
	 * Imprime uma linha já extraída de um ResultSet
	 * @param values Valor de cada coluna da linha
	 * @param columnCount Quantidade de colunas que a linha tem
	 * @param columnMaxSizes Largura máxima de cada linha
	 */
	public static void printLinha( String[] values, int columnCount, int[] columnMaxSizes) {

		// Percorre cada coluna
		for (int i = 0; i < columnCount; i++) {

			// Define a largura total necessária (largura máxima da string + padding)
			int totalWidth = columnMaxSizes[i] + 4;

			// Formato: %-Xs (Alinhamento à esquerda, X é a largura total)
			String formatSpecifier = "%-" + totalWidth + "s";

			// Prepara o valor com a marcação inicial
			String outputValue = "# " + values[i];

			// Imprime o valor formatado
			System.out.printf(formatSpecifier, outputValue);
		}

		// Quebra de linha
		System.out.println("#");
	}

	/**
	 * Imprime uma tabela formatada no terminal
	 * @param tableName Nome da tabela presente no Banco de Dados
	 * @param connector Objeto conector do Banco de Dados
	 */
	public static void printTabela(String tableName, DBConnector connector) {

		// Execução da query no banco de dados
		try {

			ResultSet resultSet = connector.queryTable(tableName);

			if (resultSet == null) {
				System.out.println("Nenhum resultado a ser impresso!");
				return;
			}

			System.out.println("Tabela: "+tableName.toUpperCase());
			printResultSet(resultSet);

		} catch (SQLException e) {
			System.err.println("Erro ao acessar a tabela: " + e.getMessage());
		}
	}

	public static void printResultSet(ResultSet resultSet) {

		// ========== Estruturas de controle ==========

		// Quantidade de colunas
		int columnCount;

		// Nomes das colunas
		String[] columnNames;

		// Largura da string mais longa de cada coluna
		int[] columnSizes;

		// Lista com todas as linhas
		ArrayList<String[]> allLines;

		// ============================================

		try {

			// Pega os nomes e contagem de colunas
			ResultSetMetaData metaData = resultSet.getMetaData();
			columnCount = metaData.getColumnCount();

			// Verifica a largura de caracteres do nome de cada coluna
			columnNames = new String[columnCount];
			columnSizes = new int[columnCount];
			for (int i = 0; i < columnCount; i++) {
				columnNames[i] = metaData.getColumnName(i+1);
				columnSizes[i] = columnNames[i].length();
			}

			// Inicializa a lista de linhas
			allLines = new ArrayList<>();
			String[] lineItems;

			// Armazena cada linha na lista
			while (resultSet.next()) {

				// Cria um vetor de string do tamanho de colunas disponíveis
				lineItems = new String[columnCount];

				// Lê os valores de cada linha e armazena
				for (int i=0; i<columnCount; i++) {

					// Pega o valor da linha no ResultSet
					String value = resultSet.getString(i+1);
					// Valida existência
					if (value == null) {
						value = "NULL";
					}
					// Armazena na linha
					lineItems[i] = value;

					// Atualiza o maior valor de caracters da coluna, se for o caso
					if (lineItems[i].length() > columnSizes[i]) {
						columnSizes[i] = lineItems[i].length();
					}
				}

				// Adiciona a linha na lista
				allLines.add(lineItems);
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		int larguraLinha = 0;
		for (int largura: columnSizes) {
			// Tamanho da maior string
			larguraLinha += largura;
			// Jogo da velha inicial + padding
			larguraLinha += 4;
		}
		// Jogo da velha final
		larguraLinha += 1;

		// Linha de jogos da velha
		String linhaJogoDaVelha = "";
		for (int i = 0; i<larguraLinha; i++) {
			linhaJogoDaVelha += "#";
		}

		// Imprime a linha com o nome das colunas
		System.out.println(linhaJogoDaVelha);
		printLinha(columnNames, columnCount, columnSizes);
		System.out.println(linhaJogoDaVelha);

		// Imprime cada linha
		for (int i = 0; i < allLines.size(); i++) {
			String[] lineItem = allLines.get(i);
			printLinha(lineItem, columnCount, columnSizes);
		}
		System.out.println(linhaJogoDaVelha);
	}
}
