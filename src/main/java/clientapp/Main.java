package main.java.clientapp;

import main.java.db.DBConnector;
import main.java.exceptions.DomainException;
import main.java.model.acesso.Acesso;
import main.java.service.CadastroService;
import main.java.service.ClientAppService;
import main.java.service.ClienteService;
import main.java.util.menu.MenuUtil;

import java.sql.SQLException;

public class Main {

	public static void main(String[] args) throws DomainException, SQLException {

		DBConnector dbConnector = new DBConnector();
		ClienteService clienteService = new ClienteService(dbConnector);
		CadastroService cadastroService = new CadastroService(dbConnector, clienteService);

		Acesso acessoAtual = cadastroService.menuAcesso();
		MenuUtil.limparConsole();
		System.out.println("Acesso realizado com sucesso!");

		ClientAppService clientAppService = new ClientAppService(acessoAtual);
		clientAppService.menu();
	}
}
