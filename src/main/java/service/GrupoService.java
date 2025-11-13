package main.java.service;

import main.java.db.DBConnector;
import main.java.exceptions.DomainException;
import main.java.model.Cliente;
import main.java.model.Grupo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class GrupoService {

	DBConnector dbConnector;

	public GrupoService(DBConnector dbConnector) {
		this.dbConnector = dbConnector;
	}

	public Grupo createGrupo(String nome, String descricao) throws DomainException, SQLException {

		if (nome == null || nome.trim().isEmpty()) {
			throw new DomainException("Grupo não pode ter o nome vazio!");
		}

		if (descricao == null || descricao.trim().isEmpty()) {
			descricao = "Grupo sem descrição.";
		}

		String query =  "INSERT INTO grupo (nome, descricao) VALUES (?, ?) RETURNING id, status, data_criacao";
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

	/**
	 * Adiciona um membro ao grupo com um papel específico (admin ou membro)
	 */
	public void adicionarMembroAoGrupo(int idCliente, int idGrupo, String papel) throws DomainException, SQLException {
		
		if (!papel.equals("admin") && !papel.equals("membro")) {
			throw new DomainException("Papel inválido. Use 'admin' ou 'membro'.");
		}

		String query = "INSERT INTO MembroGrupo (id_cliente, id_grupo, role) VALUES (?, ?, ?)";
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idCliente);
		parameters.add(idGrupo);
		parameters.add(papel);

		try {
			int rowsAffected = this.dbConnector.executeUpdate(query, parameters);

			if (rowsAffected == 0) {
				throw new SQLException("Falha ao adicionar membro ao grupo.");
			}
		} catch (SQLException e) {
			if (e.getSQLState().equals("23505")) {
				throw new DomainException("Cliente já é membro deste grupo.");
			}
			throw e;
		}
	}

	/**
	 * Cria um grupo e automaticamente adiciona o criador como administrador
	 */
	public Grupo criarGrupoComAdmin(String nome, String descricao, int idCriador) throws DomainException, SQLException {
		
		// Criar o grupo
		Grupo novoGrupo = createGrupo(nome, descricao);
		
		// Adicionar o criador como admin
		adicionarMembroAoGrupo(idCriador, novoGrupo.getId(), "admin");
		
		return novoGrupo;
	}

	/**
	 * Retorna lista de membros (clientes) de um grupo
	 */
	public ArrayList<Cliente> getMembros(int idGrupo) throws SQLException {
		String query = """
				SELECT c.* FROM Cliente c
				INNER JOIN MembroGrupo mg ON c.id = mg.id_cliente
				WHERE mg.id_grupo = ?
				ORDER BY c.nome
		""";
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idGrupo);

		ArrayList<Cliente> membros = new ArrayList<>();

		try (ResultSet resultSet = this.dbConnector.executeQuery(query, parameters)) {
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String nome = resultSet.getString("nome");
				String cpf = resultSet.getString("cpf");
				java.sql.Date dataNascimento = resultSet.getDate("data_nasc");
				int idPlano = resultSet.getInt("id_plano");

				Cliente cliente = new Cliente(id, nome, cpf, dataNascimento, idPlano);
				membros.add(cliente);
			}
		}

		return membros;
	}

	/**
	 * Calcula o saldo do grupo (soma de todas as transações)
	 * Valores negativos representam gastos, positivos representam ganhos
	 */
	public float getSaldoGrupo(int idGrupo) throws SQLException {
		String query = """
				SELECT COALESCE(SUM(valor), 0) as saldo
				FROM Transacao
				WHERE id_grupo = ?
		""";
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idGrupo);

		try (ResultSet resultSet = this.dbConnector.executeQuery(query, parameters)) {
			if (resultSet.next()) {
				return resultSet.getFloat("saldo");
			}
			return 0;
		}
	}
	
	/**
	 * Verifica se um cliente é administrador de um grupo
	 */
	public boolean isAdmin(int idCliente, int idGrupo) throws SQLException {
		String query = "SELECT role FROM MembroGrupo WHERE id_cliente = ? AND id_grupo = ?";
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idCliente);
		parameters.add(idGrupo);

		try (ResultSet resultSet = this.dbConnector.executeQuery(query, parameters)) {
			if (resultSet.next()) {
				String role = resultSet.getString("role");
				return "admin".equals(role);
			}
			return false;
		}
	}
	
	/**
	 * Altera o status de um grupo (ativo, inativo, arquivado)
	 * Apenas administradores podem fazer esta operação
	 */
	public void alterarStatusGrupo(int idGrupo, String novoStatus, int idCliente) throws DomainException, SQLException {
		
		// Verificar se o cliente é admin do grupo
		if (!isAdmin(idCliente, idGrupo)) {
			throw new DomainException("Apenas administradores podem alterar o status do grupo.");
		}
		
		// Validar status
		if (!novoStatus.equals("ativo") && !novoStatus.equals("inativo") && !novoStatus.equals("arquivado")) {
			throw new DomainException("Status inválido. Use 'ativo', 'inativo' ou 'arquivado'.");
		}
		
		String query = "UPDATE Grupo SET status = ? WHERE id = ?";
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(novoStatus);
		parameters.add(idGrupo);

		int rowsAffected = this.dbConnector.executeUpdate(query, parameters);

		if (rowsAffected == 0) {
			throw new SQLException("Falha ao atualizar status do grupo.");
		}
	}
	
	/**
	 * Deleta um grupo (marca como inativo)
	 * Apenas administradores podem deletar
	 */
	public void deletarGrupo(int idGrupo, int idCliente) throws DomainException, SQLException {
		alterarStatusGrupo(idGrupo, "inativo", idCliente);
	}
	
	/**
	 * Arquiva um grupo
	 * Apenas administradores podem arquivar
	 */
	public void arquivarGrupo(int idGrupo, int idCliente) throws DomainException, SQLException {
		alterarStatusGrupo(idGrupo, "arquivado", idCliente);
	}
	
	/**
	 * Desarquiva um grupo (volta para ativo)
	 * Apenas administradores podem desarquivar
	 */
	public void desarquivarGrupo(int idGrupo, int idCliente) throws DomainException, SQLException {
		alterarStatusGrupo(idGrupo, "ativo", idCliente);
	}
}
