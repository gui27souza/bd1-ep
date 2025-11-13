package main.java.service;

import main.java.db.DBConnector;
import main.java.exceptions.DomainException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ConviteService {

	DBConnector dbConnector;
	ClienteService clienteService;

	public ConviteService(DBConnector dbConnector, ClienteService clienteService) {
		this.dbConnector = dbConnector;
		this.clienteService = clienteService;
	}

	/**
	 * Envia um convite de um cliente remetente para um cliente destino para entrar em um grupo
	 */
	public void enviarConvite(int idRemetente, int idDestino, int idGrupo) throws DomainException, SQLException {
		
		// Validação 1: Verificar se o remetente tem plano que permite convites
		if (!clientePodeEnviarConvites(idRemetente)) {
			throw new DomainException("Seu plano não permite enviar convites. Faça upgrade para um plano superior.");
		}
		
		// Validação 2: Verificar se o cliente não atingiu o limite de convites do mês
		if (!clienteTemConvitesDisponiveis(idRemetente)) {
			throw new DomainException("Você já atingiu o limite de convites do seu plano neste mês. Aguarde o próximo ciclo ou faça upgrade.");
		}

		// Validação 3: Verificar se o remetente é admin do grupo
		if (!clienteEhAdminDoGrupo(idRemetente, idGrupo)) {
			throw new DomainException("Você precisa ser administrador do grupo para enviar convites.");
		}

		// Validação 4: Verificar se o destino já é membro do grupo
		if (clienteJaEstaNoGrupo(idDestino, idGrupo)) {
			throw new DomainException("Este cliente já é membro do grupo.");
		}

		// Validação 5: Verificar se já existe um convite pendente
		if (existeConvitePendente(idDestino, idGrupo)) {
			throw new DomainException("Já existe um convite pendente para este cliente neste grupo.");
		}

		// Criar o convite
		String query = "INSERT INTO Convite (id_remetente, id_destino, id_grupo, status) VALUES (?, ?, ?, 'pendente')";
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idRemetente);
		parameters.add(idDestino);
		parameters.add(idGrupo);

		int rowsAffected = dbConnector.executeUpdate(query, parameters);

		if (rowsAffected == 0) {
			throw new SQLException("Falha ao criar convite.");
		}
	}

	/**
	 * Verifica se o cliente tem um plano que permite enviar convites (plano_id > 1)
	 */
	private boolean clientePodeEnviarConvites(int idCliente) throws SQLException {
		
		String query = """
			SELECT p.qtd_convites 
			FROM Cliente c 
			JOIN Plano p ON c.id_plano = p.id 
			WHERE c.id = ?
		""";
		
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idCliente);

		try (ResultSet rs = dbConnector.executeQuery(query, parameters)) {
			if (rs.next()) {
				int qtdConvites = rs.getInt("qtd_convites");
				return qtdConvites > 0;
			}
			return false;
		}
	}
	
	/**
	 * Verifica se o cliente ainda tem convites disponíveis no mês atual
	 * Compara quantidade de convites enviados no mês com o limite do plano
	 */
	private boolean clienteTemConvitesDisponiveis(int idCliente) throws SQLException {
		
		String query = """
			SELECT 
				p.qtd_convites as limite,
				COUNT(conv.id) as enviados
			FROM Cliente c
			JOIN Plano p ON c.id_plano = p.id
			LEFT JOIN Convite conv ON conv.id_remetente = c.id 
				AND conv.status IN ('pendente', 'aceito')
				AND DATE_TRUNC('month', conv.data_criacao) = DATE_TRUNC('month', CURRENT_DATE)
			WHERE c.id = ?
			GROUP BY p.qtd_convites
		""";
		
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idCliente);

		try (ResultSet rs = dbConnector.executeQuery(query, parameters)) {
			if (rs.next()) {
				int limite = rs.getInt("limite");
				int enviados = rs.getInt("enviados");
				
				// Se o limite é -1, significa convites ilimitados
				if (limite == -1) {
					return true;
				}
				
				return enviados < limite;
			}
			return false;
		}
	}

	/**
	 * Retorna quantidade de convites disponíveis do cliente no mês
	 */
	public ConvitesStatus getConvitesStatus(int idCliente) throws SQLException {
		
		String query = """
			SELECT 
				p.qtd_convites as limite,
				COUNT(conv.id) as enviados
			FROM Cliente c
			JOIN Plano p ON c.id_plano = p.id
			LEFT JOIN Convite conv ON conv.id_remetente = c.id 
				AND conv.status IN ('pendente', 'aceito')
				AND DATE_TRUNC('month', conv.data_criacao) = DATE_TRUNC('month', CURRENT_DATE)
			WHERE c.id = ?
			GROUP BY p.qtd_convites
		""";
		
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idCliente);

		try (ResultSet rs = dbConnector.executeQuery(query, parameters)) {
			if (rs.next()) {
				int limite = rs.getInt("limite");
				int enviados = rs.getInt("enviados");
				return new ConvitesStatus(limite, enviados);
			}
			return new ConvitesStatus(0, 0);
		}
	}

	/**
	 * Verifica se o cliente é administrador do grupo
	 */
	private boolean clienteEhAdminDoGrupo(int idCliente, int idGrupo) throws SQLException {
		
		String query = "SELECT role FROM MembroGrupo WHERE id_cliente = ? AND id_grupo = ?";
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idCliente);
		parameters.add(idGrupo);

		try (ResultSet rs = dbConnector.executeQuery(query, parameters)) {
			if (rs.next()) {
				String role = rs.getString("role");
				return "admin".equals(role);
			}
			return false;
		}
	}

	/**
	 * Verifica se o cliente já é membro do grupo
	 */
	private boolean clienteJaEstaNoGrupo(int idCliente, int idGrupo) throws SQLException {
		
		String query = "SELECT id FROM MembroGrupo WHERE id_cliente = ? AND id_grupo = ?";
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idCliente);
		parameters.add(idGrupo);

		try (ResultSet rs = dbConnector.executeQuery(query, parameters)) {
			return rs.next();
		}
	}

	/**
	 * Verifica se já existe um convite pendente para o cliente neste grupo
	 */
	private boolean existeConvitePendente(int idDestino, int idGrupo) throws SQLException {
		
		String query = "SELECT id FROM Convite WHERE id_destino = ? AND id_grupo = ? AND status = 'pendente'";
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idDestino);
		parameters.add(idGrupo);

		try (ResultSet rs = dbConnector.executeQuery(query, parameters)) {
			return rs.next();
		}
	}

	/**
	 * Lista todos os convites pendentes recebidos por um cliente
	 */
	public ArrayList<ConviteInfo> listarConvitesPendentes(int idCliente) throws SQLException {
		
		String query = """
			SELECT 
				conv.id,
				conv.id_remetente,
				conv.id_grupo,
				conv.data_criacao,
				c_rem.nome as nome_remetente,
				g.nome as nome_grupo,
				g.descricao as descricao_grupo
			FROM Convite conv
			JOIN Cliente c_rem ON conv.id_remetente = c_rem.id
			JOIN Grupo g ON conv.id_grupo = g.id
			WHERE conv.id_destino = ? AND conv.status = 'pendente'
			ORDER BY conv.data_criacao DESC
		""";

		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idCliente);

		ArrayList<ConviteInfo> convites = new ArrayList<>();

		try (ResultSet rs = dbConnector.executeQuery(query, parameters)) {
			while (rs.next()) {
				ConviteInfo info = new ConviteInfo(
					rs.getInt("id"),
					rs.getInt("id_remetente"),
					rs.getInt("id_grupo"),
					rs.getString("nome_remetente"),
					rs.getString("nome_grupo"),
					rs.getString("descricao_grupo"),
					rs.getTimestamp("data_criacao")
				);
				convites.add(info);
			}
		}

		return convites;
	}

	/**
	 * Aceita um convite e adiciona o cliente ao grupo como membro
	 */
	public void aceitarConvite(int idConvite, int idCliente) throws DomainException, SQLException {
		
		// Buscar informações do convite
		String queryBusca = "SELECT id_destino, id_grupo FROM Convite WHERE id = ? AND status = 'pendente'";
		ArrayList<Object> paramsBusca = new ArrayList<>();
		paramsBusca.add(idConvite);

		int idDestino;
		int idGrupo;

		try (ResultSet rs = dbConnector.executeQuery(queryBusca, paramsBusca)) {
			if (!rs.next()) {
				throw new DomainException("Convite não encontrado ou já foi respondido.");
			}
			
			idDestino = rs.getInt("id_destino");
			idGrupo = rs.getInt("id_grupo");

			// Verificar se o convite é para o cliente correto
			if (idDestino != idCliente) {
				throw new DomainException("Este convite não é para você.");
			}
		}

		// Adicionar cliente ao grupo como membro
		String queryMembro = "INSERT INTO MembroGrupo (id_cliente, id_grupo, role) VALUES (?, ?, 'membro')";
		ArrayList<Object> paramsMembro = new ArrayList<>();
		paramsMembro.add(idCliente);
		paramsMembro.add(idGrupo);

		dbConnector.executeUpdate(queryMembro, paramsMembro);

		// Atualizar status do convite para aceito
		String queryUpdate = "UPDATE Convite SET status = 'aceito' WHERE id = ?";
		ArrayList<Object> paramsUpdate = new ArrayList<>();
		paramsUpdate.add(idConvite);

		dbConnector.executeUpdate(queryUpdate, paramsUpdate);
	}

	/**
	 * Recusa um convite
	 */
	public void recusarConvite(int idConvite, int idCliente) throws DomainException, SQLException {
		
		// Verificar se o convite existe e é para o cliente correto
		String queryBusca = "SELECT id_destino FROM Convite WHERE id = ? AND status = 'pendente'";
		ArrayList<Object> paramsBusca = new ArrayList<>();
		paramsBusca.add(idConvite);

		try (ResultSet rs = dbConnector.executeQuery(queryBusca, paramsBusca)) {
			if (!rs.next()) {
				throw new DomainException("Convite não encontrado ou já foi respondido.");
			}
			
			int idDestino = rs.getInt("id_destino");
			if (idDestino != idCliente) {
				throw new DomainException("Este convite não é para você.");
			}
		}

		// Atualizar status do convite para recusado
		String queryUpdate = "UPDATE Convite SET status = 'recusado' WHERE id = ?";
		ArrayList<Object> paramsUpdate = new ArrayList<>();
		paramsUpdate.add(idConvite);

		dbConnector.executeUpdate(queryUpdate, paramsUpdate);
	}

	/**
	 * Classe interna para armazenar status de convites
	 */
	public static class ConvitesStatus {
		public final int limite;
		public final int enviados;
		public final int disponiveis;
		
		public ConvitesStatus(int limite, int enviados) {
			this.limite = limite;
			this.enviados = enviados;
			this.disponiveis = (limite == -1) ? -1 : Math.max(0, limite - enviados);
		}
	}

	/**
	 * Classe interna para armazenar informações do convite
	 */
	public static class ConviteInfo {
		public final int id;
		public final int idRemetente;
		public final int idGrupo;
		public final String nomeRemetente;
		public final String nomeGrupo;
		public final String descricaoGrupo;
		public final java.sql.Timestamp dataCriacao;

		public ConviteInfo(int id, int idRemetente, int idGrupo, String nomeRemetente, String nomeGrupo, String descricaoGrupo, java.sql.Timestamp dataCriacao) {
			this.id = id;
			this.idRemetente = idRemetente;
			this.idGrupo = idGrupo;
			this.nomeRemetente = nomeRemetente;
			this.nomeGrupo = nomeGrupo;
			this.descricaoGrupo = descricaoGrupo;
			this.dataCriacao = dataCriacao;
		}
	}
}
