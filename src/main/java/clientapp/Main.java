package main.java.clientapp;

import main.java.exceptions.DomainException;
import main.java.util.menu.MenuUtil;

import java.sql.SQLException;

public class Main {
	public static void main(String[] args) throws DomainException, SQLException {

		String[] menuOptions = {
			"Fazer Login",
			"Fazer cadastro"
		};


		while (acessoAtual != null) {

			int opt = MenuUtil.printOptions(menuOptions);

			switch (opt) {

				case -1:
					System.out.println("Encerrando programa...");
					System.exit(0);
				break;

				case 0:
				break;

				case 1:
				break;

			}
		}

	}
}
