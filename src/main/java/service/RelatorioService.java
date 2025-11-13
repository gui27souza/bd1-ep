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

	// ========== RELATÓRIOS FINANCEIROS ÚTEIS ==========
	
	// ========== CONSULTAS COM SELECT ANINHADO ==========

	/**
	 * Relatório 1: Maiores Gastos
	 * Mostra as 10 transações mais caras dos grupos do cliente (gastos em valor absoluto)
	 * Usa: SELECT aninhado para filtrar grupos do cliente
	 */
	public List<Map<String, Object>> maioresGastos(int idClienteLogado) {
		
		List<Map<String, Object>> resultados = new ArrayList<>();
		
		String query = """
			SELECT 
				t.data_transacao,
				g.nome as grupo,
				c.nome as cliente,
				cat.nome as categoria,
				t.valor
			FROM Transacao t
			JOIN Cliente c ON t.id_cliente = c.id
			JOIN Grupo g ON t.id_grupo = g.id
			JOIN Categoria cat ON t.id_categoria = cat.id
			WHERE t.id_grupo IN (
				SELECT id_grupo FROM MembroGrupo WHERE id_cliente = ?
			)
			AND t.valor < 0
			ORDER BY t.valor ASC
			LIMIT 10
		""";
		
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idClienteLogado);
		
		try (ResultSet rs = dbConnector.executeQuery(query, parameters)) {
			while (rs.next()) {
				Map<String, Object> row = new HashMap<>();
				row.put("data_transacao", rs.getTimestamp("data_transacao"));
				row.put("grupo", rs.getString("grupo"));
				row.put("cliente", rs.getString("cliente"));
				row.put("categoria", rs.getString("categoria"));
				row.put("valor", rs.getFloat("valor"));
				resultados.add(row);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao executar consulta: " + e.getMessage());
		}
		
		return resultados;
	}

	/**
	 * Relatório 2: Gastos Detalhados por Categoria
	 * Mostra detalhamento de gastos por categoria com subconsulta para percentual
	 * Usa: SELECT aninhado para calcular total geral
	 */
	public List<Map<String, Object>> gastosDetalhadosPorCategoria(int idClienteLogado) {
		
		List<Map<String, Object>> resultados = new ArrayList<>();
		
		String query = """
			SELECT 
				cat.nome as categoria,
				COUNT(t.id) as quantidade,
				COALESCE(SUM(ABS(t.valor)), 0) as total,
				COALESCE(AVG(ABS(t.valor)), 0) as media,
				ROUND(
					COALESCE(SUM(ABS(t.valor)), 0) * 100.0 / NULLIF(
						(SELECT SUM(ABS(valor)) FROM Transacao 
						 WHERE id_grupo IN (SELECT id_grupo FROM MembroGrupo WHERE id_cliente = ?)
						 AND valor < 0),
						0
					), 2
				) as percentual
			FROM Categoria cat
			LEFT JOIN Transacao t ON cat.id = t.id_categoria 
				AND t.id_grupo IN (
					SELECT id_grupo FROM MembroGrupo WHERE id_cliente = ?
				)
				AND t.valor < 0
			GROUP BY cat.id, cat.nome
			HAVING COUNT(t.id) > 0
			ORDER BY total DESC
		""";
		
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idClienteLogado);
		parameters.add(idClienteLogado);
		
		try (ResultSet rs = dbConnector.executeQuery(query, parameters)) {
			while (rs.next()) {
				Map<String, Object> row = new HashMap<>();
				row.put("categoria", rs.getString("categoria"));
				row.put("quantidade", rs.getInt("quantidade"));
				row.put("total", rs.getFloat("total"));
				row.put("media", rs.getFloat("media"));
				row.put("percentual", rs.getFloat("percentual"));
				resultados.add(row);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao executar consulta: " + e.getMessage());
		}
		
		return resultados;
	}

	// ========== CONSULTAS COM FUNÇÕES DE GRUPO ==========

	/**
	 * Relatório 3: Divisão de Gastos por Membro
	 * Mostra quanto cada membro gastou em cada grupo
	 * Usa: COUNT, SUM, AVG (funções de grupo)
	 */
	public List<Map<String, Object>> divisaoGastosPorMembro(int idClienteLogado) {
		
		List<Map<String, Object>> resultados = new ArrayList<>();
		
		String query = """
			SELECT 
				g.nome as grupo,
				c.nome as membro,
				COUNT(t.id) as transacoes,
				COALESCE(SUM(t.valor), 0) as total_gasto,
				COALESCE(AVG(t.valor), 0) as media_gasto
			FROM Grupo g
			JOIN MembroGrupo mg ON g.id = mg.id_grupo
			JOIN Cliente c ON mg.id_cliente = c.id
			LEFT JOIN Transacao t ON t.id_cliente = c.id AND t.id_grupo = g.id
			WHERE g.id IN (
				SELECT id_grupo FROM MembroGrupo WHERE id_cliente = ?
			)
			GROUP BY g.id, g.nome, c.id, c.nome
			ORDER BY g.nome, total_gasto DESC
		""";
		
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idClienteLogado);
		
		try (ResultSet rs = dbConnector.executeQuery(query, parameters)) {
			while (rs.next()) {
				Map<String, Object> row = new HashMap<>();
				row.put("grupo", rs.getString("grupo"));
				row.put("membro", rs.getString("membro"));
				row.put("transacoes", rs.getInt("transacoes"));
				row.put("total_gasto", rs.getFloat("total_gasto"));
				row.put("media_gasto", rs.getFloat("media_gasto"));
				resultados.add(row);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao executar consulta: " + e.getMessage());
		}
		
		return resultados;
	}

	/**
	 * Relatório 4: Estatísticas Gerais dos Grupos
	 * Mostra estatísticas completas de cada grupo
	 * Usa: COUNT, SUM, AVG, MIN, MAX (funções de grupo)
	 */
	public List<Map<String, Object>> estatisticasGrupos(int idClienteLogado) {
		
		List<Map<String, Object>> resultados = new ArrayList<>();
		
		String query = """
			SELECT 
				g.nome,
				COUNT(DISTINCT mg.id_cliente) as total_membros,
				COUNT(t.id) as total_transacoes,
				COALESCE(SUM(t.valor), 0) as valor_total,
				COALESCE(AVG(t.valor), 0) as valor_medio,
				COALESCE(MIN(t.valor), 0) as valor_minimo,
				COALESCE(MAX(t.valor), 0) as valor_maximo
			FROM Grupo g
			JOIN MembroGrupo mg ON g.id = mg.id_grupo
			LEFT JOIN Transacao t ON g.id = t.id_grupo
			WHERE g.id IN (
				SELECT id_grupo FROM MembroGrupo WHERE id_cliente = ?
			)
			GROUP BY g.id, g.nome
			ORDER BY valor_total DESC
		""";
		
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idClienteLogado);
		
		try (ResultSet rs = dbConnector.executeQuery(query, parameters)) {
			while (rs.next()) {
				Map<String, Object> row = new HashMap<>();
				row.put("nome", rs.getString("nome"));
				row.put("total_membros", rs.getInt("total_membros"));
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

	// ========== CONSULTAS COM OPERADORES DE CONJUNTO ==========

	/**
	 * Relatório 5: Resumo Financeiro por Período
	 * Compara gastos do último mês vs mês anterior usando UNION
	 * Usa: UNION para combinar dados de diferentes períodos
	 */
	public List<Map<String, Object>> resumoFinanceiroPorPeriodo(int idClienteLogado) {
		
		List<Map<String, Object>> resultados = new ArrayList<>();
		
		String query = """
			SELECT 
				'Último Mês' as periodo,
				g.nome as grupo,
				COUNT(t.id) as quantidade,
				COALESCE(SUM(t.valor), 0) as total
			FROM Grupo g
			LEFT JOIN Transacao t ON g.id = t.id_grupo 
				AND t.id IN (
					SELECT id FROM Transacao 
					WHERE id_grupo IN (SELECT id_grupo FROM MembroGrupo WHERE id_cliente = ?)
				)
			WHERE g.id IN (SELECT id_grupo FROM MembroGrupo WHERE id_cliente = ?)
			GROUP BY g.id, g.nome
			
			UNION ALL
			
			SELECT 
				'Total Geral' as periodo,
				'Todos os Grupos' as grupo,
				COUNT(t.id) as quantidade,
				COALESCE(SUM(t.valor), 0) as total
			FROM Transacao t
			WHERE t.id_grupo IN (
				SELECT id_grupo FROM MembroGrupo WHERE id_cliente = ?
			)
			
			ORDER BY periodo DESC, total DESC
		""";
		
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idClienteLogado);
		parameters.add(idClienteLogado);
		parameters.add(idClienteLogado);
		
		try (ResultSet rs = dbConnector.executeQuery(query, parameters)) {
			while (rs.next()) {
				Map<String, Object> row = new HashMap<>();
				row.put("periodo", rs.getString("periodo"));
				row.put("grupo", rs.getString("grupo"));
				row.put("quantidade", rs.getInt("quantidade"));
				row.put("total", rs.getFloat("total"));
				resultados.add(row);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao executar consulta: " + e.getMessage());
		}
		
		return resultados;
	}

	/**
	 * Relatório 6: Grupos Ativos vs Inativos
	 * Compara grupos com e sem transações recentes
	 * Usa: EXCEPT para encontrar grupos sem atividade
	 */
	public List<Map<String, Object>> gruposAtivosVsInativos(int idClienteLogado) {
		
		List<Map<String, Object>> resultados = new ArrayList<>();
		
		String query = """
			SELECT 
				'ATIVO' as status,
				g.id,
				g.nome,
				g.descricao,
				COUNT(DISTINCT mg.id_cliente) as membros,
				COUNT(t.id) as transacoes
			FROM Grupo g
			JOIN MembroGrupo mg ON g.id = mg.id_grupo
			LEFT JOIN Transacao t ON g.id = t.id_grupo
			WHERE g.id IN (
				SELECT DISTINCT t2.id_grupo 
				FROM Transacao t2
				WHERE t2.id_grupo IN (
					SELECT id_grupo FROM MembroGrupo WHERE id_cliente = ?
				)
			)
			GROUP BY g.id, g.nome, g.descricao
			
			UNION ALL
			
			SELECT 
				'INATIVO' as status,
				g.id,
				g.nome,
				g.descricao,
				COUNT(DISTINCT mg.id_cliente) as membros,
				0 as transacoes
			FROM Grupo g
			JOIN MembroGrupo mg ON g.id = mg.id_grupo
			WHERE g.id IN (
				SELECT id_grupo FROM MembroGrupo WHERE id_cliente = ?
			)
			AND g.id NOT IN (
				SELECT DISTINCT t2.id_grupo 
				FROM Transacao t2
				WHERE t2.id_grupo IN (
					SELECT id_grupo FROM MembroGrupo WHERE id_cliente = ?
				)
			)
			GROUP BY g.id, g.nome, g.descricao
			
			ORDER BY status, transacoes DESC
		""";
		
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idClienteLogado);
		parameters.add(idClienteLogado);
		parameters.add(idClienteLogado);
		
		try (ResultSet rs = dbConnector.executeQuery(query, parameters)) {
			while (rs.next()) {
				Map<String, Object> row = new HashMap<>();
				row.put("status", rs.getString("status"));
				row.put("id", rs.getInt("id"));
				row.put("nome", rs.getString("nome"));
				row.put("descricao", rs.getString("descricao"));
				row.put("membros", rs.getInt("membros"));
				row.put("transacoes", rs.getInt("transacoes"));
				resultados.add(row);
			}
		} catch (SQLException e) {
			System.err.println("Erro ao executar consulta: " + e.getMessage());
		}
		
		return resultados;
	}
}
