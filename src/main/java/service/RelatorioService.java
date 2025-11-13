package main.java.service;

import main.java.db.DBConnector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelatorioService {

	DBConnector dbConnector;

	public RelatorioService(DBConnector dbConnector) {
		this.dbConnector = dbConnector;
	}

	// ========== CONSULTAS COM SELECT ANINHADO ==========

	/**
	 * Consulta 1: Clientes que possuem transações acima da média de valor (apenas dos grupos do cliente)
	 */
	public List<Map<String, Object>> clientesAcimaDaMedia(int idClienteLogado) {
		
		List<Map<String, Object>> resultados = new ArrayList<>();
		
		String query = """
			SELECT c.id, c.nome, COUNT(t.id) as total_transacoes, SUM(t.valor) as total_gasto
			FROM Cliente c
			JOIN Transacao t ON c.id = t.id_cliente
			WHERE t.id_grupo IN (
				SELECT id_grupo FROM MembroGrupo WHERE id_cliente = ?
			)
			GROUP BY c.id, c.nome
			HAVING SUM(t.valor) > (
				SELECT AVG(total_por_cliente)
				FROM (
					SELECT SUM(valor) as total_por_cliente
					FROM Transacao
					WHERE id_grupo IN (
						SELECT id_grupo FROM MembroGrupo WHERE id_cliente = ?
					)
					GROUP BY id_cliente
				) AS medias
			)
			ORDER BY total_gasto DESC
		""";
		
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idClienteLogado);
		parameters.add(idClienteLogado);
		
		try (ResultSet rs = dbConnector.executeQuery(query, parameters)) {
			while (rs.next()) {
				Map<String, Object> row = new HashMap<>();
				row.put("id", rs.getInt("id"));
				row.put("nome", rs.getString("nome"));
				row.put("total_transacoes", rs.getInt("total_transacoes"));
				row.put("total_gasto", rs.getFloat("total_gasto"));
				resultados.add(row);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao executar consulta: " + e.getMessage());
		}
		
		return resultados;
	}

	/**
	 * Consulta 2: Grupos que possuem mais membros que a média (apenas grupos do cliente)
	 */
	public List<Map<String, Object>> gruposComMaisMembros(int idClienteLogado) {
		
		List<Map<String, Object>> resultados = new ArrayList<>();
		
		String query = """
			SELECT g.id, g.nome, g.descricao, COUNT(mg.id_cliente) as total_membros
			FROM Grupo g
			JOIN MembroGrupo mg ON g.id = mg.id_grupo
			WHERE g.id IN (
				SELECT id_grupo FROM MembroGrupo WHERE id_cliente = ?
			)
			GROUP BY g.id, g.nome, g.descricao
			HAVING COUNT(mg.id_cliente) > (
				SELECT AVG(membros_por_grupo)
				FROM (
					SELECT COUNT(id_cliente) as membros_por_grupo
					FROM MembroGrupo
					WHERE id_grupo IN (
						SELECT id_grupo FROM MembroGrupo WHERE id_cliente = ?
					)
					GROUP BY id_grupo
				) AS medias
			)
			ORDER BY total_membros DESC
		""";
		
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idClienteLogado);
		parameters.add(idClienteLogado);
		
		try (ResultSet rs = dbConnector.executeQuery(query, parameters)) {
			while (rs.next()) {
				Map<String, Object> row = new HashMap<>();
				row.put("id", rs.getInt("id"));
				row.put("nome", rs.getString("nome"));
				row.put("descricao", rs.getString("descricao"));
				row.put("total_membros", rs.getInt("total_membros"));
				resultados.add(row);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao executar consulta: " + e.getMessage());
		}
		
		return resultados;
	}

	// ========== CONSULTAS COM FUNÇÕES DE GRUPO ==========

	/**
	 * Consulta 3: Total de transações e soma de valores por categoria (apenas dos grupos do cliente)
	 */
	public List<Map<String, Object>> totalPorCategoria(int idClienteLogado) {
		
		List<Map<String, Object>> resultados = new ArrayList<>();
		
		String query = """
			SELECT 
				cat.id,
				cat.nome,
				COUNT(t.id) as total_transacoes,
				COALESCE(SUM(t.valor), 0) as valor_total,
				COALESCE(AVG(t.valor), 0) as valor_medio,
				COALESCE(MIN(t.valor), 0) as valor_minimo,
				COALESCE(MAX(t.valor), 0) as valor_maximo
			FROM Categoria cat
			LEFT JOIN Transacao t ON cat.id = t.id_categoria 
				AND t.id_grupo IN (
					SELECT id_grupo FROM MembroGrupo WHERE id_cliente = ?
				)
			GROUP BY cat.id, cat.nome
			HAVING COUNT(t.id) > 0
			ORDER BY valor_total DESC
		""";
		
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idClienteLogado);
		
		try (ResultSet rs = dbConnector.executeQuery(query, parameters)) {
			while (rs.next()) {
				Map<String, Object> row = new HashMap<>();
				row.put("id", rs.getInt("id"));
				row.put("nome", rs.getString("nome"));
				row.put("total_transacoes", rs.getInt("total_transacoes"));
				row.put("valor_total", rs.getFloat("valor_total"));
				row.put("valor_medio", rs.getFloat("valor_medio"));
				row.put("valor_minimo", rs.getFloat("valor_minimo"));
				row.put("valor_maximo", rs.getFloat("valor_maximo"));
				resultados.add(row);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao executar consulta: " + e.getMessage());
		}
		
		return resultados;
	}

	/**
	 * Consulta 4: Estatísticas de grupos (total de membros, transações e valores) - apenas grupos do cliente
	 */
	public List<Map<String, Object>> estatisticasGrupos(int idClienteLogado) {
		
		List<Map<String, Object>> resultados = new ArrayList<>();
		
		String query = """
			SELECT 
				g.id,
				g.nome,
				COUNT(DISTINCT mg.id_cliente) as total_membros,
				COUNT(t.id) as total_transacoes,
				COALESCE(SUM(t.valor), 0) as valor_total,
				COALESCE(AVG(t.valor), 0) as valor_medio
			FROM Grupo g
			LEFT JOIN MembroGrupo mg ON g.id = mg.id_grupo
			LEFT JOIN Transacao t ON g.id = t.id_grupo
			WHERE g.id IN (
				SELECT id_grupo FROM MembroGrupo WHERE id_cliente = ?
			)
			GROUP BY g.id, g.nome
			ORDER BY total_membros DESC, valor_total DESC
		""";
		
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idClienteLogado);
		
		try (ResultSet rs = dbConnector.executeQuery(query, parameters)) {
			while (rs.next()) {
				Map<String, Object> row = new HashMap<>();
				row.put("id", rs.getInt("id"));
				row.put("nome", rs.getString("nome"));
				row.put("total_membros", rs.getInt("total_membros"));
				row.put("total_transacoes", rs.getInt("total_transacoes"));
				row.put("valor_total", rs.getFloat("valor_total"));
				row.put("valor_medio", rs.getFloat("valor_medio"));
				resultados.add(row);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao executar consulta: " + e.getMessage());
		}
		
		return resultados;
	}

	// ========== CONSULTAS COM OPERADORES DE CONJUNTO ==========

	/**
	 * Consulta 5: Clientes que são administradores UNION com clientes que são apenas membros (apenas dos grupos do cliente)
	 */
	public List<Map<String, Object>> clientesAdminVsMembros(int idClienteLogado) {
		
		List<Map<String, Object>> resultados = new ArrayList<>();
		
		String query = """
			SELECT 'ADMIN' as tipo, c.id, c.nome, COUNT(mg.id_grupo) as total_grupos
			FROM Cliente c
			JOIN MembroGrupo mg ON c.id = mg.id_cliente
			WHERE mg.role = 'admin'
				AND mg.id_grupo IN (
					SELECT id_grupo FROM MembroGrupo WHERE id_cliente = ?
				)
			GROUP BY c.id, c.nome
			UNION
			SELECT 'MEMBRO' as tipo, c.id, c.nome, COUNT(mg.id_grupo) as total_grupos
			FROM Cliente c
			JOIN MembroGrupo mg ON c.id = mg.id_cliente
			WHERE mg.role = 'membro'
				AND mg.id_grupo IN (
					SELECT id_grupo FROM MembroGrupo WHERE id_cliente = ?
				)
			GROUP BY c.id, c.nome
			ORDER BY tipo, total_grupos DESC
		""";
		
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idClienteLogado);
		parameters.add(idClienteLogado);
		
		try (ResultSet rs = dbConnector.executeQuery(query, parameters)) {
			while (rs.next()) {
				Map<String, Object> row = new HashMap<>();
				row.put("tipo", rs.getString("tipo"));
				row.put("id", rs.getInt("id"));
				row.put("nome", rs.getString("nome"));
				row.put("total_grupos", rs.getInt("total_grupos"));
				resultados.add(row);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao executar consulta: " + e.getMessage());
		}
		
		return resultados;
	}

	/**
	 * Consulta 6: Clientes com transações PIX INTERSECT com clientes com transações de Cartão (apenas dos grupos do cliente)
	 */
	public List<Map<String, Object>> clientesPixECartao(int idClienteLogado) {
		
		List<Map<String, Object>> resultados = new ArrayList<>();
		
		String query = """
			SELECT c.id, c.nome, c.cpf
			FROM Cliente c
			JOIN Transacao t ON c.id = t.id_cliente
			JOIN Pix p ON t.id = p.id_transacao
			WHERE t.id_grupo IN (
				SELECT id_grupo FROM MembroGrupo WHERE id_cliente = ?
			)
			INTERSECT
			SELECT c.id, c.nome, c.cpf
			FROM Cliente c
			JOIN Transacao t ON c.id = t.id_cliente
			JOIN Cartao ca ON t.id = ca.id_transacao
			WHERE t.id_grupo IN (
				SELECT id_grupo FROM MembroGrupo WHERE id_cliente = ?
			)
			ORDER BY nome
		""";
		
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idClienteLogado);
		parameters.add(idClienteLogado);
		
		try (ResultSet rs = dbConnector.executeQuery(query, parameters)) {
			while (rs.next()) {
				Map<String, Object> row = new HashMap<>();
				row.put("id", rs.getInt("id"));
				row.put("nome", rs.getString("nome"));
				row.put("cpf", rs.getString("cpf"));
				resultados.add(row);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao executar consulta: " + e.getMessage());
		}
		
		return resultados;
	}
}
