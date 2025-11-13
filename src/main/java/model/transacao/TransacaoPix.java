package main.java.model.transacao;

import java.sql.Timestamp;

public class TransacaoPix extends Transacao{
	String chave;

	public TransacaoPix(
		int id, int id_cliente, int id_grupo, float valor, CategoriaTransacao categoria, String descricao, Timestamp dataTransacao) {
		super(id, id_cliente, id_grupo, valor, categoria, descricao, dataTransacao);
	}

	public TransacaoPix(
		int id_cliente, int id_grupo, float valor, CategoriaTransacao categoria, String descricao, Timestamp dataTransacao) {
		super(id_cliente, id_grupo, valor, categoria, descricao, dataTransacao);
	}

	public String getChave() { return chave; }
	public void setChave(String chave) { this.chave = chave; }
}
