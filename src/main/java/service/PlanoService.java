package main.java.service;

import main.java.db.DBConnector;
import main.java.exceptions.DomainException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PlanoService {

	DBConnector dbConnector;

	public PlanoService(DBConnector dbConnector) {
		this.dbConnector = dbConnector;
	}

	public ArrayList<PlanoInfo> listarPlanos() throws SQLException {
		
		String query = "SELECT id, nome, valor, qtd_convites FROM Plano ORDER BY valor";
		
		ArrayList<PlanoInfo> planos = new ArrayList<>();

		try (ResultSet rs = dbConnector.executeQuery(query)) {
			while (rs.next()) {
				PlanoInfo plano = new PlanoInfo(
					rs.getInt("id"),
					rs.getString("nome"),
					rs.getFloat("valor"),
					rs.getInt("qtd_convites")
				);
				planos.add(plano);
			}
		}

		return planos;
	}

	public PlanoInfo buscarPlano(int idPlano) throws SQLException, DomainException {
		
		String query = "SELECT id, nome, valor, qtd_convites FROM Plano WHERE id = ?";
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idPlano);

		try (ResultSet rs = dbConnector.executeQuery(query, parameters)) {
			if (rs.next()) {
				return new PlanoInfo(
					rs.getInt("id"),
					rs.getString("nome"),
					rs.getFloat("valor"),
					rs.getInt("qtd_convites")
				);
			}
			throw new DomainException("Plano não encontrado.");
		}
	}

	public PlanoInfo getPlanoAtual(int idCliente) throws SQLException, DomainException {
		
		String query = """
			SELECT p.id, p.nome, p.valor, p.qtd_convites
			FROM Plano p
			JOIN Cliente c ON c.id_plano = p.id
			WHERE c.id = ?
		""";
		
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idCliente);

		try (ResultSet rs = dbConnector.executeQuery(query, parameters)) {
			if (rs.next()) {
				return new PlanoInfo(
					rs.getInt("id"),
					rs.getString("nome"),
					rs.getFloat("valor"),
					rs.getInt("qtd_convites")
				);
			}
			throw new DomainException("Plano não encontrado para o cliente.");
		}
	}

	public void atualizarPlanoCliente(int idCliente, int novoIdPlano) throws SQLException, DomainException {

		buscarPlano(novoIdPlano);

		String query = "UPDATE Cliente SET id_plano = ? WHERE id = ?";
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(novoIdPlano);
		parameters.add(idCliente);

		int rowsAffected = dbConnector.executeUpdate(query, parameters);

		if (rowsAffected == 0) {
			throw new SQLException("Nenhum cliente encontrado com ID " + idCliente);
		}
	}

	public static class PlanoInfo {
		public final int id;
		public final String nome;
		public final float valor;
		public final int qtdConvites;

		public PlanoInfo(int id, String nome, float valor, int qtdConvites) {
			this.id = id;
			this.nome = nome;
			this.valor = valor;
			this.qtdConvites = qtdConvites;
		}
	}
}
