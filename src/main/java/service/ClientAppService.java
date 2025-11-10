package main.java.service;

import main.java.model.acesso.Acesso;
import main.java.model.grupo.Grupo;
import main.java.util.menu.MenuUtil;

import java.util.ArrayList;

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

				case 0:
					menuGrupos();
				break;

				case 1:
					menuTransacoes();
				break;

				case 2:
					System.out.println("Operação ainda não implementada.");
				break;
			}
		}
	}


	public void menuGrupos() {

		ArrayList<Grupo> grupos = this.acessoAtual.getGrupos();

		String header = "\n==== Grupos ====\n";

		String[] menuOptions = new String[grupos.size() + 1];
		for (int i = 0; i < grupos.size(); i++) {
			Grupo grupoAux = grupos.get(i);
			menuOptions[i] = grupoAux.getNome() + " (ID: " + grupoAux.getId() + ")";
			if (i == grupos.size() - 2) {
				menuOptions[i] = menuOptions[i].concat("\n");
			}
		}
		menuOptions[grupos.size()] = "Retonar ao menu anteior";

		while (true) {

			int opt = MenuUtil.printOptions(menuOptions, header, true);

			if (grupos.size() == 0 && opt == 0 || opt == grupos.size() - 1) {
				return;
			}
		}

	}


	public void menuTransacoes() {

		String header = "\n==== Transações ====";

		String[] menuOptions = {
			"Escolher grupo para ver transações",
			"Ver todas as transações\n",
			"Retornar ao menu anterior"
		};

		while (true) {


			int opt = MenuUtil.printOptions(menuOptions, header, true);

			switch (opt) {

				case 0:
					System.out.println("Operação ainda não implementada.");
				break;

				case 1:
					System.out.println("Operação ainda não implementada.");
				break;

				case 2:
					return;
			}

		}

	}
}
