package main.java.service;

import main.java.db.DBConnector;
import main.java.util.menu.MenuUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RelatorioService {

	DBConnector dbConnector;

	public RelatorioService(DBConnector dbConnector) {
		this.dbConnector = dbConnector;
	}

	// ========== CONSULTAS COM SELECT ANINHADO ==========

	/**
	 * Consulta 1: Clientes que possuem transações acima da média de valor (apenas dos grupos do cliente)
	 */
	public void clientesAcimaDaMedia(int idClienteLogado) {
		
		System.out.println("\n========== Clientes com Transações Acima da Média (Meus Grupos) ==========\n");
		
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
			
			boolean temResultados = false;
			
			while (rs.next()) {
				temResultados = true;
				int id = rs.getInt("id");
				String nome = rs.getString("nome");
				int totalTransacoes = rs.getInt("total_transacoes");
				float totalGasto = rs.getFloat("total_gasto");
				
				System.out.printf("ID: %d | Nome: %s\n", id, nome);
				System.out.printf("Total de Transações: %d | Total Gasto: R$ %.2f\n\n", totalTransacoes, totalGasto);
			}
			
			if (!temResultados) {
				System.out.println("Nenhum resultado encontrado.\n");
			}
			
		} catch (SQLException e) {
			System.out.println("Erro ao executar consulta: " + e.getMessage());
		}
		
		MenuUtil.readStringInput("Pressione ENTER para continuar...");
	}

	/**
	 * Consulta 2: Grupos que possuem mais membros que a média (apenas grupos do cliente)
	 */
	public void gruposComMaisMembros(int idClienteLogado) {
		
		System.out.println("\n========== Meus Grupos com Mais Membros que a Média ==========\n");
		
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
			
			boolean temResultados = false;
			
			while (rs.next()) {
				temResultados = true;
				int id = rs.getInt("id");
				String nome = rs.getString("nome");
				String descricao = rs.getString("descricao");
				int totalMembros = rs.getInt("total_membros");
				
				System.out.printf("ID: %d | Nome: %s\n", id, nome);
				System.out.printf("Descrição: %s\n", descricao);
				System.out.printf("Total de Membros: %d\n\n", totalMembros);
			}
			
			if (!temResultados) {
				System.out.println("Nenhum resultado encontrado.\n");
			}
			
		} catch (SQLException e) {
			System.out.println("Erro ao executar consulta: " + e.getMessage());
		}
		
		MenuUtil.readStringInput("Pressione ENTER para continuar...");
	}

	// ========== CONSULTAS COM FUNÇÕES DE GRUPO ==========

	/**
	 * Consulta 3: Total de transações e soma de valores por categoria (apenas dos grupos do cliente)
	 */
	public void totalPorCategoria(int idClienteLogado) {
		
		System.out.println("\n========== Total de Transações por Categoria (Meus Grupos) ==========\n");
		
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
			
			boolean temResultados = false;
			
			while (rs.next()) {
				temResultados = true;
				int id = rs.getInt("id");
				String nome = rs.getString("nome");
				int totalTransacoes = rs.getInt("total_transacoes");
				float valorTotal = rs.getFloat("valor_total");
				float valorMedio = rs.getFloat("valor_medio");
				float valorMinimo = rs.getFloat("valor_minimo");
				float valorMaximo = rs.getFloat("valor_maximo");
				
				System.out.printf("Categoria: %s (ID: %d)\n", nome, id);
				System.out.printf("Total de Transações: %d\n", totalTransacoes);
				System.out.printf("Valor Total: R$ %.2f\n", valorTotal);
				System.out.printf("Valor Médio: R$ %.2f\n", valorMedio);
				System.out.printf("Valor Mínimo: R$ %.2f | Valor Máximo: R$ %.2f\n\n", valorMinimo, valorMaximo);
			}
			
			if (!temResultados) {
				System.out.println("Nenhum resultado encontrado.\n");
			}
			
		} catch (SQLException e) {
			System.out.println("Erro ao executar consulta: " + e.getMessage());
		}
		
		MenuUtil.readStringInput("Pressione ENTER para continuar...");
	}

	/**
	 * Consulta 4: Estatísticas de grupos (total de membros, transações e valores) - apenas grupos do cliente
	 */
	public void estatisticasGrupos(int idClienteLogado) {
		
		System.out.println("\n========== Estatísticas dos Meus Grupos ==========\n");
		
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
			
			boolean temResultados = false;
			
			while (rs.next()) {
				temResultados = true;
				int id = rs.getInt("id");
				String nome = rs.getString("nome");
				int totalMembros = rs.getInt("total_membros");
				int totalTransacoes = rs.getInt("total_transacoes");
				float valorTotal = rs.getFloat("valor_total");
				float valorMedio = rs.getFloat("valor_medio");
				
				System.out.printf("Grupo: %s (ID: %d)\n", nome, id);
				System.out.printf("Membros: %d | Transações: %d\n", totalMembros, totalTransacoes);
				System.out.printf("Valor Total: R$ %.2f | Valor Médio: R$ %.2f\n\n", valorTotal, valorMedio);
			}
			
			if (!temResultados) {
				System.out.println("Nenhum resultado encontrado.\n");
			}
			
		} catch (SQLException e) {
			System.out.println("Erro ao executar consulta: " + e.getMessage());
		}
		
		MenuUtil.readStringInput("Pressione ENTER para continuar...");
	}

	// ========== CONSULTAS COM OPERADORES DE CONJUNTO ==========

	/**
	 * Consulta 5: Clientes que são administradores UNION com clientes que são apenas membros (apenas dos grupos do cliente)
	 */
	public void clientesAdminVsMembros(int idClienteLogado) {
		
		System.out.println("\n========== Clientes: Administradores vs Membros (Meus Grupos) ==========\n");
		
		String query = """
			SELECT 'ADMIN' as tipo, c.id, c.nome, COUNT(mg.id_grupo) as total_grupos
			FROM Cliente c
			JOIN MembroGrupo mg ON c.id = mg.id_cliente
			WHERE mg.papel = 'admin'
				AND mg.id_grupo IN (
					SELECT id_grupo FROM MembroGrupo WHERE id_cliente = ?
				)
			GROUP BY c.id, c.nome
			UNION
			SELECT 'MEMBRO' as tipo, c.id, c.nome, COUNT(mg.id_grupo) as total_grupos
			FROM Cliente c
			JOIN MembroGrupo mg ON c.id = mg.id_cliente
			WHERE mg.papel = 'membro'
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
			
			boolean temResultados = false;
			String tipoAtual = "";
			
			while (rs.next()) {
				temResultados = true;
				String tipo = rs.getString("tipo");
				int id = rs.getInt("id");
				String nome = rs.getString("nome");
				int totalGrupos = rs.getInt("total_grupos");
				
				if (!tipo.equals(tipoAtual)) {
					System.out.println("\n--- " + tipo + "S ---\n");
					tipoAtual = tipo;
				}
				
				System.out.printf("ID: %d | Nome: %s | Grupos: %d\n", id, nome, totalGrupos);
			}
			
			if (!temResultados) {
				System.out.println("Nenhum resultado encontrado.\n");
			}
			
			System.out.println();
			
		} catch (SQLException e) {
			System.out.println("Erro ao executar consulta: " + e.getMessage());
		}
		
		MenuUtil.readStringInput("Pressione ENTER para continuar...");
	}

	/**
	 * Consulta 6: Clientes com transações PIX INTERSECT com clientes com transações de Cartão (apenas dos grupos do cliente)
	 */
	public void clientesPixECartao(int idClienteLogado) {
		
		System.out.println("\n========== Clientes que Usam PIX e Cartão (Meus Grupos) ==========\n");
		
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
			
			boolean temResultados = false;
			
			while (rs.next()) {
				temResultados = true;
				int id = rs.getInt("id");
				String nome = rs.getString("nome");
				String cpf = rs.getString("cpf");
				
				System.out.printf("ID: %d | Nome: %s | CPF: %s\n", id, nome, cpf);
			}
			
			if (!temResultados) {
				System.out.println("Nenhum cliente usa tanto PIX quanto Cartão.\n");
			}
			
			System.out.println();
			
		} catch (SQLException e) {
			System.out.println("Erro ao executar consulta: " + e.getMessage());
		}
		
		MenuUtil.readStringInput("Pressione ENTER para continuar...");
	}
}
