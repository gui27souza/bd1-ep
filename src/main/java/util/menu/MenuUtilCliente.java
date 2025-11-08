package main.java.util.menu;

import main.java.model.cliente.Cliente;

import java.util.ArrayList;

public class MenuUtilCliente {

	public static void printCliente(Cliente cliente) {
		System.out.println("\n====================");
		cliente.repr();
		System.out.println("====================");
	}

	public static void printListaCliente(ArrayList<Cliente> listaClientes) {
		for (Cliente cliente: listaClientes) {
			printCliente(cliente);
		}
	}

	public static Cliente chooseClienteFromLista(ArrayList<Cliente> listaClientes) {

		String[] menuOptions = new String[listaClientes.size()];

		for (int i = 0; i < listaClientes.size(); i++) {
			Cliente clienteAux = listaClientes.get(i);
			menuOptions[i] = clienteAux.getNome() + " | CPF: " + clienteAux.getCpf() + " | Id: " + clienteAux.getId();
		}

		System.out.println("Digite o número do cliente a ser escolhido: ");
		int opt = MenuUtil.printOptions(menuOptions);

		return listaClientes.get(opt);
	}
}
