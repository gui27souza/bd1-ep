package main.java.service;

import main.java.model.Acesso;
import main.java.model.Cliente;
import main.java.model.Grupo;
import main.java.model.transacao.Transacao;
import main.java.util.menu.MenuUtil;

import java.sql.SQLException;
import java.util.ArrayList;

public class ClientAppService {

	GrupoService grupoService;
	TransacaoService transacaoService;
	ClienteService clienteService;
	CadastroService cadastroService;
	RelatorioService relatorioService;
	ConviteService conviteService;
	PlanoService planoService;

	Acesso acessoAtual;

	public ClientAppService(Acesso acessoAtual, GrupoService grupoService, TransacaoService transacaoService, ClienteService clienteService, CadastroService cadastroService, RelatorioService relatorioService, ConviteService conviteService, PlanoService planoService) {
		this.acessoAtual = acessoAtual;
		this.grupoService = grupoService;
		this.transacaoService = transacaoService;
		this.clienteService = clienteService;
		this.cadastroService = cadastroService;
		this.relatorioService = relatorioService;
		this.conviteService = conviteService;
		this.planoService = planoService;
	}

	public void menu() {

		String header =
			"\n================================" +
			"\n========== ClienteApp ==========" +
			"\n\nBem vindo " + this.acessoAtual.getCliente().getNome() + "!\n"
		;
		String[] menuOptions = {
			"Gerenciar grupos",
			"Ver transações",
			"Ver convites",
			"Ver/Editar cadastro",
			"Relatórios e Consultas"
		};

		while (true) {

			int opt = MenuUtil.printOptions(menuOptions, header, true);

			switch (opt) {

		case 0:
			menuGrupos();
		break;

		case 1:
			menuTransacoes();
		break;			case 2:
				menuConvites();
			break;

			case 3:
				menuCadastro();
			break;

			case 4:
				menuRelatorios();
			break;
			}
		}
	}

	public void menuGrupos() {

		String header = "\n==== Gerenciar Grupos ====";

		String[] menuOptions = {
			"Ver meus grupos",
			"Criar novo grupo",
			"Retornar ao menu anterior"
		};

		while (true) {

			int opt = MenuUtil.printOptions(menuOptions, header, true);

			switch (opt) {

			case 0:
				verMeusGrupos();
			break;

			case 1:
				criarGrupo();
			break;

			case 2:
				return;
			}
		}
	}

	private void verMeusGrupos() {
		
		try {
			ArrayList<Grupo> grupos = this.grupoService.getGrupos(this.acessoAtual.getCliente());
			
			if (grupos.isEmpty()) {
				System.out.println("\nVocê não pertence a nenhum grupo ainda.");
				System.out.println("Crie um novo grupo ou aguarde um convite!\n");
				MenuUtil.readStringInput("Pressione ENTER para continuar...");
				return;
			}

			System.out.println("\n========== Meus Grupos ==========\n");
			for (Grupo grupo : grupos) {
				System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
				System.out.println("Nome: " + grupo.getNome());
				System.out.println("Descrição: " + grupo.getDescricao());
				System.out.println("Status: " + grupo.getStatus());
				System.out.println("Data de Criação: " + grupo.getDataCriacao());
				System.out.println();
			}
			System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
			
			MenuUtil.readStringInput("Pressione ENTER para continuar...");
			
		} catch (Exception e) {
			System.out.println("\nErro ao buscar grupos: " + e.getMessage());
			MenuUtil.readStringInput("Pressione ENTER para continuar...");
		}
	}

	private void criarGrupo() {
		
		System.out.println("\n==== Criar Novo Grupo ====\n");
		
		String nome = MenuUtil.readStringInput("Nome do grupo: ");
		
		if (nome.trim().isEmpty()) {
			System.out.println("\nOperação cancelada - nome não pode ser vazio.");
			MenuUtil.readStringInput("Pressione ENTER para continuar...");
			return;
		}
		
		String descricao = MenuUtil.readStringInput("Descrição do grupo (opcional): ");
		
		try {
			Grupo novoGrupo = this.grupoService.criarGrupoComAdmin(
				nome, 
				descricao, 
				this.acessoAtual.getCliente().getId()
			);
			
			// Atualiza a lista de grupos do acesso atual
			ArrayList<Grupo> gruposAtualizados = this.grupoService.getGrupos(this.acessoAtual.getCliente());
			this.acessoAtual.setGrupos(gruposAtualizados);
			
			System.out.println("\n✓ Grupo criado com sucesso!");
			System.out.println("Nome: " + novoGrupo.getNome());
			System.out.println("Você é o administrador deste grupo.");
			System.out.println("\nAgora você pode enviar convites para outros membros!");
			
			MenuUtil.readStringInput("\nPressione ENTER para continuar...");
			
		} catch (Exception e) {
			System.out.println("\nErro ao criar grupo: " + e.getMessage());
			MenuUtil.readStringInput("Pressione ENTER para continuar...");
		}
	}

	public void menuTransacoes() {

		String header = "\n==== Transações ====";

		String[] menuOptions = {
			"Escolher grupo para ver transações",
			"Ver todas as transações",
			"Retornar ao menu anterior"
		};

		while (true) {

			int opt = MenuUtil.printOptions(menuOptions, header, true);

			switch (opt) {

				case 0:
					verTransacoesPorGrupo();
				break;

				case 1:
					verTodasTransacoes();
				break;

				case 2:
					return;
			}

		}

	}

	private void verTransacoesPorGrupo() {
		
		ArrayList<Grupo> grupos = this.acessoAtual.getGrupos();
		
		if (grupos.isEmpty()) {
			System.out.println("\nVocê não pertence a nenhum grupo ainda.\n");
			MenuUtil.readStringInput("Pressione ENTER para continuar...");
			return;
		}
		
		Grupo grupoEscolhido = this.grupoService.menuGrupos(grupos);
		
		if (grupoEscolhido == null) {
			return;
		}
		
		try {
			ArrayList<Transacao> transacoes = this.transacaoService.getTransacoesPorGrupo(grupoEscolhido.getId());
			this.transacaoService.imprimirTransacoes(transacoes);
			MenuUtil.readStringInput("Pressione ENTER para continuar...");
		} catch (SQLException e) {
			System.out.println("\nErro ao buscar transações: " + e.getMessage() + "\n");
			MenuUtil.readStringInput("Pressione ENTER para continuar...");
		}
	}

	private void verTodasTransacoes() {
		
		try {
			ArrayList<Transacao> transacoes = this.transacaoService.getTodasTransacoes(
				this.acessoAtual.getCliente().getId()
			);
			this.transacaoService.imprimirTransacoes(transacoes);
			MenuUtil.readStringInput("Pressione ENTER para continuar...");
		} catch (SQLException e) {
			System.out.println("\nErro ao buscar transações: " + e.getMessage() + "\n");
			MenuUtil.readStringInput("Pressione ENTER para continuar...");
		}
	}


	public void menuConvites() {
		
		String header = "\n==== Convites ====";

		String[] menuOptions = {
			"Ver convites recebidos",
			"Enviar convite",
			"Retornar ao menu anterior"
		};

		while (true) {

			int opt = MenuUtil.printOptions(menuOptions, header, true);

			switch (opt) {

			case 0:
				verConvitesRecebidos();
			break;

			case 1:
				enviarConvite();
			break;

			case 2:
				return;
			}
		}
	}

	private void verConvitesRecebidos() {
		
		try {
			ArrayList<ConviteService.ConviteInfo> convites = this.conviteService.listarConvitesPendentes(
				this.acessoAtual.getCliente().getId()
			);

			if (convites.isEmpty()) {
				System.out.println("\nVocê não tem convites pendentes.\n");
				MenuUtil.readStringInput("Pressione ENTER para continuar...");
				return;
			}

			System.out.println("\n========== Convites Recebidos ==========\n");
			for (int i = 0; i < convites.size(); i++) {
				ConviteService.ConviteInfo conv = convites.get(i);
				System.out.printf("%d) Grupo: %s\n", (i + 1), conv.nomeGrupo);
				System.out.printf("   Descrição: %s\n", conv.descricaoGrupo);
				System.out.printf("   Convidado por: %s\n\n", conv.nomeRemetente);
			}

			int escolha = MenuUtil.readIntInput("Escolha um convite (ou 0 para voltar): ");

			if (escolha == 0 || escolha > convites.size()) {
				return;
			}

			ConviteService.ConviteInfo conviteSelecionado = convites.get(escolha - 1);

			String[] opcoesResposta = {"Aceitar", "Recusar", "Voltar"};
			int resposta = MenuUtil.printOptions(opcoesResposta, "\n==== Responder Convite ====", false);

			if (resposta == 0) {
				this.conviteService.aceitarConvite(conviteSelecionado.id, this.acessoAtual.getCliente().getId());
				
				// Atualiza a lista de grupos do acesso atual
				ArrayList<Grupo> gruposAtualizados = this.grupoService.getGrupos(this.acessoAtual.getCliente());
				this.acessoAtual.setGrupos(gruposAtualizados);
				
				System.out.println("\nConvite aceito com sucesso! Você agora é membro do grupo.");
				MenuUtil.readStringInput("Pressione ENTER para continuar...");
			} else if (resposta == 1) {
				this.conviteService.recusarConvite(conviteSelecionado.id, this.acessoAtual.getCliente().getId());
				System.out.println("\nConvite recusado.");
				MenuUtil.readStringInput("Pressione ENTER para continuar...");
			}

		} catch (Exception e) {
			System.out.println("\nErro ao processar convites: " + e.getMessage());
			MenuUtil.readStringInput("Pressione ENTER para continuar...");
		}
	}

	private void enviarConvite() {
		
		ArrayList<Grupo> grupos = this.acessoAtual.getGrupos();
		
		if (grupos.isEmpty()) {
			System.out.println("\nVocê não pertence a nenhum grupo ainda.\n");
			MenuUtil.readStringInput("Pressione ENTER para continuar...");
			return;
		}

		System.out.println("\n==== Enviar Convite ====");
		System.out.println("Escolha o grupo para enviar o convite:\n");

		Grupo grupoEscolhido = this.grupoService.menuGrupos(grupos);
		
		if (grupoEscolhido == null) {
			return;
		}

		System.out.println("\nDigite o CPF do cliente que deseja convidar:");
		String cpf = MenuUtil.readStringInput("CPF (11 dígitos): ");

		try {
			Cliente destinatario = this.clienteService.findByCpf(cpf);
			
			if (destinatario == null) {
				System.out.println("\nCliente não encontrado com este CPF.");
				MenuUtil.readStringInput("Pressione ENTER para continuar...");
				return;
			}

			this.conviteService.enviarConvite(
				this.acessoAtual.getCliente().getId(),
				destinatario.getId(),
				grupoEscolhido.getId()
			);

			System.out.println("\nConvite enviado com sucesso para " + destinatario.getNome() + "!");
			MenuUtil.readStringInput("Pressione ENTER para continuar...");

		} catch (Exception e) {
			System.out.println("\nErro ao enviar convite: " + e.getMessage());
			MenuUtil.readStringInput("Pressione ENTER para continuar...");
		}
	}

	public void menuCadastro() {

		String header = "\n==== Meu Cadastro ====";

		String[] menuOptions = {
			"Ver meus dados",
			"Editar nome",
			"Editar e-mail",
			"Editar CPF",
			"Editar data de nascimento",
			"Trocar plano",
			"Retornar ao menu anterior"
		};

		while (true) {

			int opt = MenuUtil.printOptions(menuOptions, header, true);

			switch (opt) {

			case 0:
				verDadosCliente();
			break;

			case 1:
				editarNome();
			break;

			case 2:
				editarEmail();
			break;

			case 3:
				editarCpf();
			break;

			case 4:
				editarDataNascimento();
			break;

			case 5:
				trocarPlano();
			break;

			case 6:
				return;
			}
		}
	}	private void verDadosCliente() {
		Cliente cliente = this.acessoAtual.getCliente();
		System.out.println("\n========== Meus Dados ==========");
		System.out.println("ID: " + cliente.getId());
		System.out.println("Nome: " + cliente.getNome());
		System.out.println("E-mail: " + this.acessoAtual.getEmail());
		System.out.println("CPF: " + cliente.getCpf());
		System.out.println("Data de Nascimento: " + cliente.getDataNascimento());
		System.out.println("ID Plano: " + cliente.getIdPlano());
		System.out.println("================================\n");
		MenuUtil.readStringInput("Pressione ENTER para continuar...");
	}

	private void editarNome() {
		Cliente cliente = this.acessoAtual.getCliente();
		System.out.println("\n==== Editar Nome ====");
		System.out.println("Nome atual: " + cliente.getNome());
		
		String novoNome = MenuUtil.readStringInput("Novo nome (ou ENTER para cancelar): ");
		
		if (novoNome.trim().isEmpty()) {
			System.out.println("Operação cancelada.");
			MenuUtil.readStringInput("Pressione ENTER para continuar...");
			return;
		}

		try {
			this.clienteService.updateNome(cliente.getId(), novoNome);
			cliente.setNome(novoNome);
			System.out.println("\nNome atualizado com sucesso!");
		} catch (Exception e) {
			System.out.println("\nErro ao atualizar nome: " + e.getMessage());
		}
		
		MenuUtil.readStringInput("Pressione ENTER para continuar...");
	}

	private void editarEmail() {
		System.out.println("\n==== Editar E-mail ====");
		System.out.println("E-mail atual: " + this.acessoAtual.getEmail());
		
		String novoEmail = MenuUtil.readStringInput("Novo e-mail (ou ENTER para cancelar): ");
		
		if (novoEmail.trim().isEmpty()) {
			System.out.println("Operação cancelada.");
			MenuUtil.readStringInput("Pressione ENTER para continuar...");
			return;
		}

		try {
			this.cadastroService.updateEmail(this.acessoAtual.getId(), novoEmail);
			this.acessoAtual.setEmail(novoEmail);
			System.out.println("\nE-mail atualizado com sucesso!");
		} catch (Exception e) {
			System.out.println("\nErro ao atualizar e-mail: " + e.getMessage());
		}
		
		MenuUtil.readStringInput("Pressione ENTER para continuar...");
	}

	private void editarCpf() {
		Cliente cliente = this.acessoAtual.getCliente();
		System.out.println("\n==== Editar CPF ====");
		System.out.println("CPF atual: " + cliente.getCpf());
		
		String novoCpf = MenuUtil.readStringInput("Novo CPF (11 dígitos, ou ENTER para cancelar): ");
		
		if (novoCpf.trim().isEmpty()) {
			System.out.println("Operação cancelada.");
			MenuUtil.readStringInput("Pressione ENTER para continuar...");
			return;
		}

		try {
			this.clienteService.updateCpf(cliente.getId(), novoCpf);
			cliente.setCpf(novoCpf);
			System.out.println("\nCPF atualizado com sucesso!");
		} catch (Exception e) {
			System.out.println("\nErro ao atualizar CPF: " + e.getMessage());
		}
		
		MenuUtil.readStringInput("Pressione ENTER para continuar...");
	}

	private void editarDataNascimento() {
		Cliente cliente = this.acessoAtual.getCliente();
		System.out.println("\n==== Editar Data de Nascimento ====");
		System.out.println("Data atual: " + cliente.getDataNascimento());
		
		String novaDataStr = MenuUtil.readStringInput("Nova data (YYYY-MM-DD, ou ENTER para cancelar): ");
		
		if (novaDataStr.trim().isEmpty()) {
			System.out.println("Operação cancelada.");
			MenuUtil.readStringInput("Pressione ENTER para continuar...");
			return;
		}

		try {
			this.clienteService.updateDataNascimento(cliente.getId(), novaDataStr);
			java.sql.Date novaData = java.sql.Date.valueOf(java.time.LocalDate.parse(novaDataStr));
			cliente.setDataNascimento(novaData);
			System.out.println("\nData de nascimento atualizada com sucesso!");
		} catch (Exception e) {
			System.out.println("\nErro ao atualizar data: " + e.getMessage());
		}
		
		MenuUtil.readStringInput("Pressione ENTER para continuar...");
	}

	private void trocarPlano() {
		
		try {
			Cliente cliente = this.acessoAtual.getCliente();
			
			// Buscar plano atual
			PlanoService.PlanoInfo planoAtual = this.planoService.buscarPlano(cliente.getIdPlano());
			
			System.out.println("\n==== Trocar Plano ====");
			System.out.println("\nPlano Atual:");
			System.out.printf("  %s - R$ %.2f/mês\n", planoAtual.nome.toUpperCase(), planoAtual.valor);
			System.out.printf("  Convites disponíveis: %d\n", planoAtual.qtdConvites);
			
			// Listar todos os planos
			ArrayList<PlanoService.PlanoInfo> planos = this.planoService.listarPlanos();
			
			System.out.println("\n========== Planos Disponíveis ==========\n");
			for (int i = 0; i < planos.size(); i++) {
				PlanoService.PlanoInfo plano = planos.get(i);
				String marcador = (plano.id == planoAtual.id) ? " [ATUAL]" : "";
				System.out.printf("%d) %s - R$ %.2f/mês%s\n", 
					(i + 1), 
					plano.nome.toUpperCase(), 
					plano.valor,
					marcador
				);
				System.out.printf("   Convites: %d por mês\n\n", plano.qtdConvites);
			}
			
			int escolha = MenuUtil.readIntInput("Escolha um plano (ou 0 para cancelar): ");
			
			if (escolha == 0 || escolha > planos.size()) {
				System.out.println("Operação cancelada.");
				MenuUtil.readStringInput("Pressione ENTER para continuar...");
				return;
			}
			
			PlanoService.PlanoInfo novoPlano = planos.get(escolha - 1);
			
			if (novoPlano.id == planoAtual.id) {
				System.out.println("\nVocê já está neste plano.");
				MenuUtil.readStringInput("Pressione ENTER para continuar...");
				return;
			}
			
			// Confirmação
			System.out.printf("\nConfirmar troca de plano de %s para %s?\n", 
				planoAtual.nome.toUpperCase(), 
				novoPlano.nome.toUpperCase()
			);
			String confirmacao = MenuUtil.readStringInput("Digite 'SIM' para confirmar: ");
			
			if (!confirmacao.equalsIgnoreCase("SIM")) {
				System.out.println("\nOperação cancelada.");
				MenuUtil.readStringInput("Pressione ENTER para continuar...");
				return;
			}
			
			// Atualizar plano
			this.planoService.atualizarPlanoCliente(cliente.getId(), novoPlano.id);
			cliente.setIdPlano(novoPlano.id);
			
			System.out.println("\n✓ Plano atualizado com sucesso!");
			System.out.printf("Você agora tem o plano %s com %d convites disponíveis.\n", 
				novoPlano.nome.toUpperCase(),
				novoPlano.qtdConvites
			);
			MenuUtil.readStringInput("\nPressione ENTER para continuar...");
			
		} catch (Exception e) {
			System.out.println("\nErro ao trocar plano: " + e.getMessage());
			MenuUtil.readStringInput("Pressione ENTER para continuar...");
		}
	}

	public void menuRelatorios() {

		String header = "\n==== Relatórios e Consultas ====";

		String[] menuOptions = {
			"Clientes com transações acima da média",
			"Grupos com mais membros que a média",
			"Total de transações por categoria",
			"Estatísticas dos grupos",
			"Clientes administradores vs membros",
			"Clientes que usam PIX e Cartão",
			"Retornar ao menu anterior"
		};

		while (true) {

			int opt = MenuUtil.printOptions(menuOptions, header, true);

			switch (opt) {

			case 0:
				this.relatorioService.clientesAcimaDaMedia(this.acessoAtual.getCliente().getId());
			break;

			case 1:
				this.relatorioService.gruposComMaisMembros(this.acessoAtual.getCliente().getId());
			break;

			case 2:
				this.relatorioService.totalPorCategoria(this.acessoAtual.getCliente().getId());
			break;

			case 3:
				this.relatorioService.estatisticasGrupos(this.acessoAtual.getCliente().getId());
			break;

			case 4:
				this.relatorioService.clientesAdminVsMembros(this.acessoAtual.getCliente().getId());
			break;

			case 5:
				this.relatorioService.clientesPixECartao(this.acessoAtual.getCliente().getId());
			break;

			case 6:
				return;
			}
		}
	}
}
