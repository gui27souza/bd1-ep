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

	int idPlanoBasico = 1;

	public ClienteService(DBConnector dbConnector) {
		this.dbConnector = dbConnector;
	}

	public Cliente createCliente(String nome, long cpf, Date dataNascimento) throws SQLException, DomainException {

		if (nome == null || nome.trim().isEmpty()) {
			throw new DomainException("O nome do cliente não pode ser vazio.");
		}
		if (cpf <= 0) {
			throw new DomainException("CPF inválido.");
		}
		
		Cliente novoCliente = new Cliente(nome, cpf, dataNascimento, this.idPlanoBasico);

		String query = "INSERT INTO cliente (nome, cpf, data_nasc, id_plano) VALUES (?, ?, ?, ?) RETURNING id";

		try (PreparedStatement pstmt = this.dbConnector.getConnection().prepareStatement(query)) {

			pstmt.setString(1, novoCliente.getNome());
			pstmt.setLong(2, novoCliente.getCpf());
			pstmt.setDate(3, novoCliente.getDataNascimento());
			pstmt.setInt(4, novoCliente.getIdPlano());

			try (ResultSet rs = pstmt.executeQuery()) {

				if (rs.next()) {

					int novoId = rs.getInt("id");

					novoCliente.setId(novoId);

					return novoCliente;
				} else {
					throw new SQLException("Falha ao obter o ID do cliente após a inserção.");
				}
			}

		} catch (SQLException e) {

			if (e.getSQLState().equals("23505")) {
				throw new DomainException("Já existe um cliente cadastrado com este CPF.");
			}

			throw e;
		}
	}
}
