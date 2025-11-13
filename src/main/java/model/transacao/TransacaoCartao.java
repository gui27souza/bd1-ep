package main.java.model.transacao;

public class TransacaoCartao extends Transacao {
	String bandeira;
	String digitosFinais;

	public TransacaoCartao(
		int id, int id_cliente, int id_grupo, float valor, CategoriaTransacao categoria, String descricao) {
		super(id, id_cliente, id_grupo, valor, categoria, descricao);
	}

	public TransacaoCartao(
		int id_cliente, int id_grupo, float valor, CategoriaTransacao categoria, String descricao) {
		super(id_cliente, id_grupo, valor, categoria, descricao);
	}

	public String getBandeira() { return bandeira; }
	public void setBandeira(String bandeira) { this.bandeira = bandeira; }
	public String getDigitosFinais() { return digitosFinais; }
	public void setDigitosFinais(String digitosFinais) { this.digitosFinais = digitosFinais; }
}
