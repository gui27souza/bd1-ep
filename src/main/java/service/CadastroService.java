package main.java.service;

import main.java.db.DBConnector;

public class CadastroService {

	DBConnector connector;

	public CadastroService(DBConnector connector) {
		this.connector = connector;
	}

}
