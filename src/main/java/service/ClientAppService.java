package main.java.service;

import main.java.model.acesso.Acesso;
import main.java.util.menu.MenuUtil;

public class ClientAppService {

	Acesso acessoAtual;

	public ClientAppService(Acesso acessoAtual) {
		this.acessoAtual = acessoAtual;
	}

	public void menu() {

		String[] menuOptions = {
			"Ver grupos",
			"Ver transações",
			"Ver/Editar cadastro"
		};

		while (true) {

			System.out.println("\n================================");
			System.out.println("========== ClienteApp ==========");
			System.out.println("Bem vindo " + this.acessoAtual.getClienteVinculado().getNome() + "!\n");

			int opt = MenuUtil.printOptions(menuOptions);

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
