package main.java.service;

import main.java.db.DBConnector;
import main.java.model.transacao.CategoriaTransacao;
import main.java.model.transacao.Transacao;
import main.java.model.transacao.TransacaoPix;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class TransacaoService {

	DBConnector dbConnector;

	public TransacaoService(DBConnector dbConnector) {
		this.dbConnector = dbConnector;
	}

	public ArrayList<Transacao> getTransacoesPorGrupo(int idGrupo) throws SQLException {
		
		String query = """
			SELECT t.id, t.descricao, t.valor, t.data_transacao, t.id_cliente, t.id_grupo, t.id_categoria,
			       c.nome as categoria_nome, c.descricao as categoria_desc
			FROM Transacao t
			JOIN Categoria c ON t.id_categoria = c.id
			WHERE t.id_grupo = ?
			ORDER BY t.data_transacao DESC
		""";
		
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idGrupo);
		
		return executarQueryTransacoes(query, parameters);
	}

	public ArrayList<Transacao> getTodasTransacoes(int idCliente) throws SQLException {
		
		String query = """
			SELECT t.id, t.descricao, t.valor, t.data_transacao, t.id_cliente, t.id_grupo, t.id_categoria,
			       c.nome as categoria_nome, c.descricao as categoria_desc
			FROM Transacao t
			JOIN Categoria c ON t.id_categoria = c.id
			WHERE t.id_cliente = ?
			ORDER BY t.data_transacao DESC
		""";
		
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idCliente);
		
		return executarQueryTransacoes(query, parameters);
	}

	public ArrayList<Transacao> getTransacoesPorCategoria(int idCliente, int idCategoria) throws SQLException {
		
		String query = """
			SELECT t.id, t.descricao, t.valor, t.data_transacao, t.id_cliente, t.id_grupo, t.id_categoria,
			       c.nome as categoria_nome, c.descricao as categoria_desc
			FROM Transacao t
			JOIN Categoria c ON t.id_categoria = c.id
			WHERE t.id_cliente = ? AND t.id_categoria = ?
			ORDER BY t.data_transacao DESC
		""";
		
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idCliente);
		parameters.add(idCategoria);
		
		return executarQueryTransacoes(query, parameters);
	}

	public ArrayList<Transacao> getTransacoesPorPeriodo(int idCliente, java.sql.Date dataInicio, java.sql.Date dataFim) throws SQLException {
		
		String query = """
			SELECT t.id, t.descricao, t.valor, t.data_transacao, t.id_cliente, t.id_grupo, t.id_categoria,
			       c.nome as categoria_nome, c.descricao as categoria_desc
			FROM Transacao t
			JOIN Categoria c ON t.id_categoria = c.id
			WHERE t.id_cliente = ? 
			AND DATE(t.data_transacao) BETWEEN ? AND ?
			ORDER BY t.data_transacao DESC
		""";
		
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idCliente);
		parameters.add(dataInicio);
		parameters.add(dataFim);
		
		return executarQueryTransacoes(query, parameters);
	}

	public ArrayList<CategoriaTransacao> getCategorias() throws SQLException {
		
		String query = "SELECT id, nome, descricao FROM Categoria ORDER BY nome";
		ArrayList<Object> parameters = new ArrayList<>();
		ArrayList<CategoriaTransacao> categorias = new ArrayList<>();
		
		try (ResultSet rs = dbConnector.executeQuery(query, parameters)) {
			while (rs.next()) {
				int id = rs.getInt("id");
				String nome = rs.getString("nome");
				String descricao = rs.getString("descricao");
				categorias.add(new CategoriaTransacao(id, nome, descricao));
			}
		}
		
		return categorias;
	}

	private ArrayList<Transacao> executarQueryTransacoes(String query, ArrayList<Object> parameters) throws SQLException {
		
		ArrayList<Transacao> transacoes = new ArrayList<>();
		
		try (ResultSet rs = dbConnector.executeQuery(query, parameters)) {
			
			while (rs.next()) {
				int id = rs.getInt("id");
				String descricao = rs.getString("descricao");
				float valor = rs.getFloat("valor");
				java.sql.Timestamp dataTransacao = rs.getTimestamp("data_transacao");
				int idCliente = rs.getInt("id_cliente");
				int idGrupo = rs.getInt("id_grupo");
				int idCategoria = rs.getInt("id_categoria");
				String categoriaNome = rs.getString("categoria_nome");
				String categoriaDesc = rs.getString("categoria_desc");
				
				CategoriaTransacao categoria = new CategoriaTransacao(idCategoria, categoriaNome, categoriaDesc);
				
				// Por enquanto cria como TransacaoPix generica
				Transacao transacao = new TransacaoPix(id, idCliente, idGrupo, valor, categoria, descricao, dataTransacao);
				transacoes.add(transacao);
			}
		}
		
		return transacoes;
	}

	public void imprimirTransacoes(ArrayList<Transacao> transacoes) {
		
		if (transacoes.isEmpty()) {
			System.out.println("\nNenhuma transação encontrada.\n");
			return;
		}
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		
		System.out.println("\n========== TRANSAÇÕES ==========");
		for (Transacao t : transacoes) {
			System.out.println("\nID: " + t.getId());
			if (t.getDataTransacao() != null) {
				System.out.println("Data: " + dateFormat.format(t.getDataTransacao()));
			}
			System.out.println("Descrição: " + t.getDescricao());
			System.out.println("Valor: R$ " + String.format("%.2f", t.getValor()));
			System.out.println("Categoria: " + t.getCategoria().getNome());
		}
		System.out.println("\n================================\n");
	}

	public void criarTransacao(Transacao transacao, String tipoTransacao) throws SQLException {
		// Inserir na tabela Transacao e obter o ID gerado
		String query = """
			INSERT INTO Transacao (id_cliente, id_grupo, id_categoria, valor, descricao, data_transacao)
			VALUES (?, ?, ?, ?, ?, ?)
			RETURNING id
		""";
		
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(transacao.getId_cliente());
		parameters.add(transacao.getId_grupo());
		parameters.add(transacao.getCategoria().getId());
		parameters.add(transacao.getValor());
		parameters.add(transacao.getDescricao());
		parameters.add(transacao.getDataTransacao());
		
		int idTransacao = 0;
		try (ResultSet rs = dbConnector.executeQuery(query, parameters)) {
			if (rs.next()) {
				idTransacao = rs.getInt("id");
			}
		}
		
		// Inserir na tabela especializada (Pix ou Cartao)
		if (tipoTransacao.equals("PIX")) {
			String queryPix = "INSERT INTO Pix (id_transacao, chave) VALUES (?, ?)";
			ArrayList<Object> paramsPix = new ArrayList<>();
			paramsPix.add(idTransacao);
			paramsPix.add("chave.generica@pix.com"); // Chave genérica para Opção 1
			dbConnector.executeUpdate(queryPix, paramsPix);
		} else if (tipoTransacao.equals("CARTAO")) {
			String queryCartao = "INSERT INTO Cartao (id_transacao, bandeira, digitos_finais) VALUES (?, ?, ?)";
			ArrayList<Object> paramsCartao = new ArrayList<>();
			paramsCartao.add(idTransacao);
			paramsCartao.add("Visa"); // Bandeira genérica
			paramsCartao.add("0000"); // Dígitos genéricos
			dbConnector.executeUpdate(queryCartao, paramsCartao);
		}
	}

	public void editarTransacao(Transacao transacao) throws SQLException {
		String query = """
			UPDATE Transacao
			SET id_grupo = ?, id_categoria = ?, valor = ?, descricao = ?, data_transacao = ?
			WHERE id = ?
		""";
		
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(transacao.getId_grupo());
		parameters.add(transacao.getCategoria().getId());
		parameters.add(transacao.getValor());
		parameters.add(transacao.getDescricao());
		parameters.add(transacao.getDataTransacao());
		parameters.add(transacao.getId());
		
		dbConnector.executeUpdate(query, parameters);
	}

	public void deletarTransacao(int idTransacao) throws SQLException {
		String query = "DELETE FROM Transacao WHERE id = ?";
		
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idTransacao);
		
		dbConnector.executeUpdate(query, parameters);
	}

	public String getTipoTransacao(int idTransacao) throws SQLException {
		// Verificar se existe em Pix
		String queryPix = "SELECT id_transacao FROM Pix WHERE id_transacao = ?";
		ArrayList<Object> paramsPix = new ArrayList<>();
		paramsPix.add(idTransacao);
		
		ResultSet rsPix = dbConnector.executeQuery(queryPix, paramsPix);
		if (rsPix.next()) {
			rsPix.close();
			return "PIX";
		}
		rsPix.close();
		
		// Verificar se existe em Cartao
		String queryCartao = "SELECT id_transacao FROM Cartao WHERE id_transacao = ?";
		ArrayList<Object> paramsCartao = new ArrayList<>();
		paramsCartao.add(idTransacao);
		
		ResultSet rsCartao = dbConnector.executeQuery(queryCartao, paramsCartao);
		if (rsCartao.next()) {
			rsCartao.close();
			return "CARTAO";
		}
		rsCartao.close();
		
		return "N/A";
	}

	public void editarTransacao(Transacao transacao, String novoTipo) throws SQLException {
		// Primeiro, atualizar a transação base
		String query = """
			UPDATE Transacao
			SET id_grupo = ?, id_categoria = ?, valor = ?, descricao = ?, data_transacao = ?
			WHERE id = ?
		""";
		
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(transacao.getId_grupo());
		parameters.add(transacao.getCategoria().getId());
		parameters.add(transacao.getValor());
		parameters.add(transacao.getDescricao());
		parameters.add(transacao.getDataTransacao());
		parameters.add(transacao.getId());
		
		dbConnector.executeUpdate(query, parameters);
		
		// Se tipo foi fornecido, atualizar especialização
		if (novoTipo != null && !novoTipo.isEmpty()) {
			int idTransacao = transacao.getId();
			
			// Remover de ambas as tabelas
			String deletePix = "DELETE FROM Pix WHERE id_transacao = ?";
			String deleteCartao = "DELETE FROM Cartao WHERE id_transacao = ?";
			
			ArrayList<Object> paramsDel = new ArrayList<>();
			paramsDel.add(idTransacao);
			
			dbConnector.executeUpdate(deletePix, paramsDel);
			dbConnector.executeUpdate(deleteCartao, paramsDel);
			
			// Inserir no tipo correto
			if (novoTipo.equals("PIX")) {
				String insertPix = "INSERT INTO Pix (id_transacao, chave) VALUES (?, ?)";
				ArrayList<Object> paramsIns = new ArrayList<>();
				paramsIns.add(idTransacao);
				paramsIns.add("chave.generica@pix.com");
				dbConnector.executeUpdate(insertPix, paramsIns);
			} else if (novoTipo.equals("CARTAO")) {
				String insertCartao = "INSERT INTO Cartao (id_transacao, bandeira, digitos_finais) VALUES (?, ?, ?)";
				ArrayList<Object> paramsIns = new ArrayList<>();
				paramsIns.add(idTransacao);
				paramsIns.add("Visa");
				paramsIns.add("0000");
				dbConnector.executeUpdate(insertCartao, paramsIns);
			}
		}
	}
}
