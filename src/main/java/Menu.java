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

}
