package main.java;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Menu {

	static Scanner opcao = new Scanner(System.in);

	/**
	 * @param options Vetor de Strings que contém as opções a serem impressos
	 * @return Posição do array OU -1 (usuário quer encerrar o programa)
	 */
	public static int printOptions(String[] options) {

		while (true) {

			System.out.println("Selecione uma opção abaixo:\n");
			for (int i = 0; i < options.length; i++) {
				System.out.println((i + 1) + " - " + options[i]);
			}
			System.out.println("\n0 - Encerrar o programa\n");
			System.out.print("Digite a opção: ");

			int inputOpcao;

			try{
				inputOpcao = opcao.nextInt();

				if ((inputOpcao - 1) >= options.length || inputOpcao < 0) {
					throw new NumberFormatException();
				}

			} catch (InputMismatchException | NumberFormatException e) {
				System.out.println("Opção inválida!\n");
				if (e instanceof InputMismatchException) {
					opcao.next();
				}
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

		// ===== Estruturas de controle =====

		// Quantidade de colunas
		int columnCount;

		// Nomes das colunas
		String[] columnNames;

		// Largura da string mais longa de cada coluna
		int[] columnSizes;

		// Lista com todas as linhas
		ArrayList<String[]> allLines;

		// ==================================

		// Execução da query no banco de dados
		try {

			// Prepara a query
			String query = "SELECT * FROM " + tableName;
			ResultSet resultSet = connector.executeQuery(query);

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

		String linhaJogoDaVelha = "";
		for (int i = 0; i<larguraLinha; i++) {
			linhaJogoDaVelha += "#";
		}

		// Imprime a linha com o nome das colunas
		System.out.println("Tabela: "+tableName.toUpperCase());
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
