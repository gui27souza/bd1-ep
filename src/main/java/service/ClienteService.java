package main.java.service;

import main.java.model.Cliente;
import main.java.db.DBConnector;
import main.java.exceptions.DomainException;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ClienteService {

	DBConnector dbConnector;
	final int idPlanoBasico = 1;

	public ClienteService(DBConnector dbConnector) {
		this.dbConnector = dbConnector;
	}

	public Cliente createCliente(String nome, String cpf, Date dataNascimento) throws SQLException, DomainException {

		if (nome == null || nome.trim().isEmpty()) {
			throw new DomainException("O nome do cliente não pode ser vazio.");
		}
		if (cpf.length() != 11) {
			throw new DomainException("CPF inválido, deve conter 11 dígitos numéricos.");
		}
		try {
			Long.parseLong(cpf);
		} catch (NumberFormatException e) {
			throw new DomainException("CPF inválido, deve conter 11 dígitos numéricos.");
		}

		Cliente novoCliente = new Cliente(nome, cpf, dataNascimento, this.idPlanoBasico);

		String query = "INSERT INTO cliente (nome, cpf, data_nasc, id_plano) VALUES (?, ?, ?, ?) RETURNING id";

		try (PreparedStatement pstmt = this.dbConnector.getConnection().prepareStatement(query)) {

			pstmt.setString(1, novoCliente.getNome());
			pstmt.setString(2, novoCliente.getCpf());
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

	public Cliente findById(int id) throws DomainException, SQLException {

		if (id <= 0) {
			throw new DomainException("ID inválido, deve ser positivo");
		}

		String query = "SELECT id, nome, cpf, data_nasc, id_plano FROM cliente WHERE id = ?";
		List<Object> parameters = new ArrayList<>();
		parameters.add(id);

		try (ResultSet resultSet = this.dbConnector.executeQuery(query, parameters)) {

			if (resultSet.next()) {
				String nome = resultSet.getString("nome");
				String cpf = resultSet.getString("cpf");
				Date dataNascimento = resultSet.getDate("data_nasc");
				int idPlano = resultSet.getInt("id_plano");
				Cliente clienteBuscado = new Cliente(id, nome, cpf, dataNascimento, idPlano);

				return clienteBuscado;
			}

			return null;
		}
	}

	public Cliente findByCpf(String cpf) throws DomainException, SQLException {

		if (cpf.length() != 11) {
			throw new DomainException("CPF inválido, deve conter 11 dígitos.");
		}

		try {
			Long.parseLong(cpf);
		} catch (NumberFormatException e) {
			throw new DomainException("CPF inválido, deve conter 11 dígitos numéricos.");
		}

		String query = "SELECT id, nome, cpf, data_nasc, id_plano FROM cliente WHERE cpf = ?";
		List<Object> parameters = new ArrayList<>();
		parameters.add(cpf);

		try (ResultSet resultSet = this.dbConnector.executeQuery(query, parameters)) {

			if (resultSet.next()) {
				int id = resultSet.getInt("id");
				String nome = resultSet.getString("nome");
				Date dataNascimento = resultSet.getDate("data_nasc");
				int idPlano = resultSet.getInt("id_plano");
				Cliente clienteBuscado = new Cliente(id, nome, cpf, dataNascimento, idPlano);

				return clienteBuscado;
			}

			return null;
		}
	}

	public ArrayList<Cliente> findAll() throws SQLException {

		String query = "SELECT id, nome, cpf, data_nasc, id_plano FROM cliente ORDER BY nome";

		ArrayList<Cliente> clientes = new ArrayList<>();

		try (ResultSet resultSet = this.dbConnector.executeQuery(query, new ArrayList<>())) {

			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String nome = resultSet.getString("nome");
				String cpf = resultSet.getString("cpf");
				Date dataNascimento = resultSet.getDate("data_nasc");
				int idPlano = resultSet.getInt("id_plano");

				Cliente cliente = new Cliente(id, nome, cpf, dataNascimento, idPlano);
				clientes.add(cliente);
			}
		}

		return clientes;
	}

	public void updateNome(int idCliente, String novoNome) throws DomainException, SQLException {
		
		if (novoNome == null || novoNome.trim().isEmpty()) {
			throw new DomainException("O nome não pode ser vazio.");
		}

		String query = "UPDATE Cliente SET nome = ? WHERE id = ?";
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(novoNome);
		parameters.add(idCliente);

		int rowsAffected = this.dbConnector.executeUpdate(query, parameters);

		if (rowsAffected == 0) {
			throw new SQLException("Nenhum cliente encontrado com ID " + idCliente);
		}
	}

	public void updateCpf(int idCliente, String novoCpf) throws DomainException, SQLException {
		
		if (novoCpf.length() != 11) {
			throw new DomainException("CPF inválido, deve conter 11 dígitos.");
		}

		try {
			Long.parseLong(novoCpf);
		} catch (NumberFormatException e) {
			throw new DomainException("CPF inválido, deve conter 11 dígitos numéricos.");
		}

		String query = "UPDATE Cliente SET cpf = ? WHERE id = ?";
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(novoCpf);
		parameters.add(idCliente);

		try {
			int rowsAffected = this.dbConnector.executeUpdate(query, parameters);

			if (rowsAffected == 0) {
				throw new SQLException("Nenhum cliente encontrado com ID " + idCliente);
			}
		} catch (SQLException e) {
			if (e.getSQLState().equals("23505")) {
				throw new DomainException("Já existe um cliente cadastrado com este CPF.");
			}
			throw e;
		}
	}

	public void updateDataNascimento(int idCliente, String novaDataStr) throws DomainException, SQLException {
		
		if (novaDataStr == null || novaDataStr.trim().isEmpty()) {
			throw new DomainException("A data não pode ser vazia.");
		}

		try {
			LocalDate.parse(novaDataStr);
		} catch (Exception e) {
			throw new DomainException("Formato de data inválido. Use YYYY-MM-DD.");
		}

		String query = "UPDATE Cliente SET data_nasc = ? WHERE id = ?";
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(Date.valueOf(novaDataStr));
		parameters.add(idCliente);

		int rowsAffected = this.dbConnector.executeUpdate(query, parameters);

		if (rowsAffected == 0) {
			throw new SQLException("Nenhum cliente encontrado com ID " + idCliente);
		}
	}
}
