package main.java.model.transacao;

public class TransacaoPix extends Transacao{
	String chave;

	public TransacaoPix(
		int id, int id_cliente, int id_grupo, float valor, CategoriaTransacao categoria, String descricao) {
		super(id, id_cliente, id_grupo, valor, categoria, descricao);
	}

	public TransacaoPix(
		int id_cliente, int id_grupo, float valor, CategoriaTransacao categoria, String descricao) {
		super(id_cliente, id_grupo, valor, categoria, descricao);
	}

	public String getChave() { return chave; }
	public void setChave(String chave) { this.chave = chave; }
}
