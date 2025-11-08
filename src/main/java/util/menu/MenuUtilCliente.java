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

}
