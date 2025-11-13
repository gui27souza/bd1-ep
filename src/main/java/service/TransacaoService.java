package main.java.service;

import main.java.db.DBConnector;
import main.java.model.transacao.CategoriaTransacao;
import main.java.model.transacao.Transacao;
import main.java.model.transacao.TransacaoPix;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TransacaoService {

	DBConnector dbConnector;

	public TransacaoService(DBConnector dbConnector) {
		this.dbConnector = dbConnector;
	}

	public ArrayList<Transacao> getTransacoesPorGrupo(int idGrupo) throws SQLException {
		
		String query = """
			SELECT t.id, t.descricao, t.valor, t.id_cliente, t.id_grupo, t.id_categoria,
			       c.nome as categoria_nome, c.descricao as categoria_desc
			FROM Transacao t
			JOIN Categoria c ON t.id_categoria = c.id
			WHERE t.id_grupo = ?
			ORDER BY t.id
		""";
		
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idGrupo);
		
		return executarQueryTransacoes(query, parameters);
	}

	public ArrayList<Transacao> getTodasTransacoes(int idCliente) throws SQLException {
		
		String query = """
			SELECT t.id, t.descricao, t.valor, t.id_cliente, t.id_grupo, t.id_categoria,
			       c.nome as categoria_nome, c.descricao as categoria_desc
			FROM Transacao t
			JOIN Categoria c ON t.id_categoria = c.id
			WHERE t.id_cliente = ?
			ORDER BY t.id
		""";
		
		ArrayList<Object> parameters = new ArrayList<>();
		parameters.add(idCliente);
		
		return executarQueryTransacoes(query, parameters);
	}

	private ArrayList<Transacao> executarQueryTransacoes(String query, ArrayList<Object> parameters) throws SQLException {
		
		ArrayList<Transacao> transacoes = new ArrayList<>();
		
		try (ResultSet rs = dbConnector.executeQuery(query, parameters)) {
			
			while (rs.next()) {
				int id = rs.getInt("id");
				String descricao = rs.getString("descricao");
				float valor = rs.getFloat("valor");
				int idCliente = rs.getInt("id_cliente");
				int idGrupo = rs.getInt("id_grupo");
				int idCategoria = rs.getInt("id_categoria");
				String categoriaNome = rs.getString("categoria_nome");
				String categoriaDesc = rs.getString("categoria_desc");
				
				CategoriaTransacao categoria = new CategoriaTransacao(idCategoria, categoriaNome, categoriaDesc);
				
				// Por enquanto cria como TransacaoPix generica
				Transacao transacao = new TransacaoPix(id, idCliente, idGrupo, valor, categoria, descricao);
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
		
		System.out.println("\n========== TRANSAÇÕES ==========");
		for (Transacao t : transacoes) {
			System.out.println("\nID: " + t.getId());
			System.out.println("Descrição: " + t.getDescricao());
			System.out.println("Valor: R$ " + String.format("%.2f", t.getValor()));
			System.out.println("Categoria: " + t.getCategoria().getNome());
		}
		System.out.println("\n================================\n");
	}
}
