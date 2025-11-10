package main.java.service;

import main.java.model.acesso.Acesso;
import main.java.util.menu.MenuUtil;

public class ClientAppService {

	Acesso acessoAtual;

	public ClientAppService(Acesso acessoAtual) {
		this.acessoAtual = acessoAtual;
	}

	public void menu() {

		String header =
			"\n================================" +
			"\n========== ClienteApp ==========" +
			"\n\nBem vindo " + this.acessoAtual.getCliente().getNome() + "!\n"
		;
		String[] menuOptions = {
			"Ver grupos",
			"Ver transações",
			"Ver/Editar cadastro"
		};

		while (true) {

			int opt = MenuUtil.printOptions(menuOptions, header, true);

			switch (opt) {

				case -1:
					System.out.println("Encerrando programa...");
					System.exit(0);
				break;

				case 0:
					System.out.println("Operação ainda não implementada.");
				break;

				case 1:
					System.out.println("Operação ainda não implementada.");
				break;

				case 2:
					System.out.println("Operação ainda não implementada.");
				break;

			}

		}

	}
}
