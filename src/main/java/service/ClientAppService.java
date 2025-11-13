package main.java.service;

import main.java.model.Acesso;
import main.java.model.Grupo;
import main.java.util.menu.MenuUtil;

import java.util.ArrayList;

public class ClientAppService {

	GrupoService grupoService;

	Acesso acessoAtual;

	public ClientAppService(Acesso acessoAtual, GrupoService grupoService) {
		this.acessoAtual = acessoAtual;
		this.grupoService = grupoService;
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
			"Ver convites",
			"Ver/Editar cadastro"
		};

		while (true) {

			int opt = MenuUtil.printOptions(menuOptions, header, true);

			switch (opt) {

				case 0:
					ArrayList<Grupo> grupos = this.acessoAtual.getGrupos();
					Grupo grupoAtual = this.grupoService.menuGrupos(grupos);
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


	public void menuConvites() {

	}
}
