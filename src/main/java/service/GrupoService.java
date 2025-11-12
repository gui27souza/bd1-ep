package main.java.service;

import main.java.db.DBConnector;
import main.java.exceptions.DomainException;
import main.java.model.Cliente;
import main.java.model.Grupo;
import main.java.util.menu.MenuUtilGrupo;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class GrupoService {

	DBConnector dbConnector;

	public GrupoService(DBConnector dbConnector) {
		this.dbConnector = dbConnector;
	}

	public Grupo menuGrupos(ArrayList<Grupo> grupos) {

		Grupo grupo = MenuUtilGrupo.chooseGrupoFromLista(grupos);

		return grupo;
	}

	public Grupo createGrupo(String nome, String descricao) throws DomainException, SQLException {

		if (nome == null || nome.trim().isEmpty()) {
			throw new DomainException("Grupo não pode ter o nome vazio!");
		}

		if (descricao == null || descricao.trim().isEmpty()) {
			descricao = "Grupo sem descrição.";
		}

		String query =  "INSERT INTO grupo (nome, status, descricao) VALUES (?, ?, ?) RETURNING id, status, data_criacao";
		ArrayList<Object> parameters =  new ArrayList<>();
		parameters.add(nome);
		parameters.add(descricao);

		Grupo novoGrupo;

		try (ResultSet resultSet = this.dbConnector.executeQuery(query, parameters)) {

			if (resultSet.next()) {

				int id = resultSet.getInt("id");
				String status = resultSet.getString("status");
				Date dataCriacao = resultSet.getDate("data_criacao");

				novoGrupo = new Grupo(id, nome, status, dataCriacao, descricao);

				return novoGrupo;

			}  else {
				throw new SQLException("Falha ao obter o ID do grupo após a inserção.");
			}
		}
	}

	public ArrayList<Grupo> getGrupos(Cliente cliente) throws DomainException, SQLException {

		if (cliente == null) {
			throw new DomainException("Cliente não pode ser nulo!");
		}

		String query = """
				SELECT * FROM GRUPO WHERE id IN (
					SELECT id_grupo FROM MEMBROGRUPO WHERE id_cliente = ?
				)
		""";
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(cliente.getId());

		Grupo grupoAux;
		ArrayList<Grupo> listaGrupos = new ArrayList<>();

		try (ResultSet resultSet = this.dbConnector.executeQuery(query, parameters)) {

			while (resultSet.next()) {

				int id = resultSet.getInt("id");
				String nome = resultSet.getString("nome");
				String status = resultSet.getString("status");
				Date dataCriacao = resultSet.getDate("data_criacao");
				String descricao = resultSet.getString("descricao");

				grupoAux = new Grupo(id, nome, status, dataCriacao, descricao);

				listaGrupos.add(grupoAux);
			}

			return listaGrupos;
		}
	}
}
