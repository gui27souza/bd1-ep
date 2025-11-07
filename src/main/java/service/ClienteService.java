package main.java.service;

import main.java.MenuUtil;
import main.java.Models.Cliente;
import main.java.db.DBConnector;
import main.java.exceptions.DomainException;

import java.awt.*;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ClienteService {

	DBConnector dbConnector = null;


	public ClienteService(DBConnector dbConnector) {
		this.dbConnector = dbConnector;
	}

}
