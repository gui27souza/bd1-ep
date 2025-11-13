package main.java.gui;
import main.java.model.Acesso;
import main.java.service.RelatorioService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
public class RelatoriosFrame extends JFrame {
    private MainFrame mainFrame;
    private Acesso acessoAtual;
    private RelatorioService relatorioService;
    public RelatoriosFrame(MainFrame mainFrame, Acesso acessoAtual, RelatorioService relatorioService) {
        this.mainFrame = mainFrame;
        this.acessoAtual = acessoAtual;
        this.relatorioService = relatorioService;
        initComponents();
    }

    private void initComponents() {
        setTitle("Relatórios e Consultas");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                voltar();
            }
        });
        setSize(850, 700);
        setLocationRelativeTo(null);
        setResizable(true);
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 240, 240));
        // Painel de título
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(26, 188, 156));     // BTN_INFO
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel titleLabel = new JLabel("📊 Relatórios e Consultas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        // Painel central com botões de relatórios
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        JLabel infoLabel = new JLabel("Selecione um relatório para visualizar:");
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(infoLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		// Botões dos relatórios
		JButton btn1 = createReportButton("1. 💰 Maiores Gastos", new Color(33, 150, 243));           // BTN_PRIMARY
		JButton btn2 = createReportButton("2. 💸 Maiores Contribuições", new Color(41, 182, 246));   // BTN_SECONDARY
		JButton btn3 = createReportButton("3. 📊 Gastos Detalhados por Categoria", new Color(38, 198, 218)); // BTN_SUCCESS
		JButton btn4 = createReportButton("4. 👥 Divisão de Gastos por Membro", new Color(26, 188, 156));  // BTN_INFO
		JButton btn5 = createReportButton("5. 📈 Estatísticas dos Grupos", new Color(77, 182, 172));     // BTN_LIGHT
		JButton btn6 = createReportButton("6. 📅 Resumo Financeiro por Período", new Color(38, 198, 218)); // BTN_SUCCESS
		JButton btn7 = createReportButton("7. 🔄 Grupos Ativos vs Inativos", new Color(41, 182, 246));  // BTN_SECONDARY		btn1.addActionListener(e -> exibirRelatorio1());
		btn2.addActionListener(e -> exibirRelatorio2());
		btn3.addActionListener(e -> exibirRelatorio3());
		btn4.addActionListener(e -> exibirRelatorio4());
		btn5.addActionListener(e -> exibirRelatorio5());
		btn6.addActionListener(e -> exibirRelatorio6());
		btn7.addActionListener(e -> exibirRelatorio7());		centerPanel.add(btn1);
		centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		centerPanel.add(btn2);
		centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		centerPanel.add(btn3);
		centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		centerPanel.add(btn4);
		centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		centerPanel.add(btn5);
		centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		centerPanel.add(btn6);
		centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		centerPanel.add(btn7);        mainPanel.add(centerPanel, BorderLayout.CENTER);
        // Botão voltar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(240, 240, 240));
        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.setFont(new Font("Arial", Font.BOLD, 13));
        btnVoltar.setBackground(new Color(158, 158, 158));     // BTN_NEUTRAL
        btnVoltar.setForeground(Color.WHITE);
        btnVoltar.setFocusPainted(false);
        btnVoltar.setPreferredSize(new Dimension(100, 40));
        btnVoltar.addActionListener(e -> voltar());
        buttonPanel.add(btnVoltar);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);
    }

    private JButton createReportButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(650, 45));
        button.setMinimumSize(new Dimension(650, 45));
        button.setPreferredSize(new Dimension(650, 45));
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Efeito hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(adjustBrightness(color, 0.9f));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        return button;
    }

    private Color adjustBrightness(Color color, float factor) {
        int r = (int)(color.getRed() * factor);
        int g = (int)(color.getGreen() * factor);
        int b = (int)(color.getBlue() * factor);
        return new Color(Math.max(0, r), Math.max(0, g), Math.max(0, b));
    }

    private void exibirRelatorio1() {
        List<Map<String, Object>> dados = relatorioService.maioresGastos(acessoAtual.getCliente().getId());
        if (dados.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Nenhuma transação foi encontrada.",
                "Sem Resultados",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] colunas = {"Data", "Grupo", "Cliente", "Categoria", "Valor"};
        DefaultTableModel tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (Map<String, Object> row : dados) {
            String dataFormatada = "";
            if (row.get("data_transacao") != null) {
                dataFormatada = dateFormat.format(row.get("data_transacao"));
            }
            double valor = ((Number) row.get("valor")).doubleValue();
            Object[] rowData = {
                dataFormatada,
                row.get("grupo"),
                row.get("cliente"),
                row.get("categoria"),
                String.format("R$ %.2f", valor)
            };
			tableModel.addRow(rowData);
		}
		mostrarTabelaRelatorio(tableModel, "💰 Maiores Gastos");
	}	private void exibirRelatorio2() {
		List<Map<String, Object>> dados = relatorioService.maioresContribuicoes(acessoAtual.getCliente().getId());
		if (dados.isEmpty()) {
			JOptionPane.showMessageDialog(this,
				"Nenhuma contribuição foi encontrada.",
				"Sem Resultados",
				JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		String[] colunas = {"Data", "Grupo", "Cliente", "Categoria", "Valor"};
		DefaultTableModel tableModel = new DefaultTableModel(colunas, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		for (Map<String, Object> row : dados) {
			String dataFormatada = "";
			if (row.get("data_transacao") != null) {
				dataFormatada = dateFormat.format(row.get("data_transacao"));
			}
			double valor = ((Number) row.get("valor")).doubleValue();
			Object[] rowData = {
				dataFormatada,
				row.get("grupo"),
				row.get("cliente"),
				row.get("categoria"),
				String.format("R$ %.2f", valor)
			};
			tableModel.addRow(rowData);
		}
		mostrarTabelaRelatorio(tableModel, "💸 Maiores Contribuições");
	}

	private void exibirRelatorio3() {
		List<Map<String, Object>> dados = relatorioService.gastosDetalhadosPorCategoria(acessoAtual.getCliente().getId());        if (dados.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Nenhuma transação foi encontrada.",
                "Sem Resultados",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] colunas = {"Categoria", "Quantidade", "Total", "% do Total"};
        DefaultTableModel tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (Map<String, Object> row : dados) {
            double total = ((Number) row.get("total")).doubleValue();
            double percentual = ((Number) row.get("percentual")).doubleValue();
            Object[] rowData = {
                row.get("categoria"),
                row.get("quantidade"),
                String.format("R$ %.2f", total),
                String.format("%.1f%%", percentual)
            };
            tableModel.addRow(rowData);
        }
		mostrarTabelaRelatorio(tableModel, "📊 Gastos Detalhados por Categoria");
	}

	private void exibirRelatorio4() {
		List<Map<String, Object>> dados = relatorioService.divisaoGastosPorMembro(acessoAtual.getCliente().getId());        if (dados.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Nenhuma transação foi encontrada.",
                "Sem Resultados",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] colunas = {"Grupo", "Membro", "Transações", "Total Gasto", "Média"};
        DefaultTableModel tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (Map<String, Object> row : dados) {
            double totalGasto = ((Number) row.get("total_gasto")).doubleValue();
            double mediaGasto = ((Number) row.get("media_gasto")).doubleValue();
            Object[] rowData = {
                row.get("grupo"),
                row.get("membro"),
                row.get("transacoes"),
                String.format("R$ %.2f", totalGasto),
                String.format("R$ %.2f", mediaGasto)
            };
            tableModel.addRow(rowData);
        }
		mostrarTabelaRelatorio(tableModel, "👥 Divisão de Gastos por Membro");
	}

	private void exibirRelatorio5() {
		List<Map<String, Object>> dados = relatorioService.estatisticasGrupos(acessoAtual.getCliente().getId());        if (dados.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Nenhum grupo foi encontrado.",
                "Sem Resultados",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] colunas = {"Grupo", "Membros", "Transações", "Total", "Média", "Min", "Max"};
        DefaultTableModel tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (Map<String, Object> row : dados) {
            double valorTotal = ((Number) row.get("valor_total")).doubleValue();
            double valorMedio = ((Number) row.get("valor_medio")).doubleValue();
            double valorMinimo = ((Number) row.get("valor_minimo")).doubleValue();
            double valorMaximo = ((Number) row.get("valor_maximo")).doubleValue();
            Object[] rowData = {
                row.get("nome"),
                row.get("total_membros"),
                row.get("total_transacoes"),
                String.format("R$ %.2f", valorTotal),
                String.format("R$ %.2f", valorMedio),
                String.format("R$ %.2f", valorMinimo),
                String.format("R$ %.2f", valorMaximo)
            };
            tableModel.addRow(rowData);
        }
		mostrarTabelaRelatorio(tableModel, "📈 Estatísticas dos Grupos");
	}

	private void exibirRelatorio6() {
		List<Map<String, Object>> dados = relatorioService.resumoFinanceiroPorPeriodo(acessoAtual.getCliente().getId());        if (dados.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Nenhum dado foi encontrado.",
                "Sem Resultados",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] colunas = {"Período", "Transações", "Total Gastos", "Total Receitas", "Saldo"};
        DefaultTableModel tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (Map<String, Object> row : dados) {
            double totalGastos = ((Number) row.get("total_gastos")).doubleValue();
            double totalReceitas = ((Number) row.get("total_receitas")).doubleValue();
            double saldo = ((Number) row.get("saldo")).doubleValue();
            Object[] rowData = {
                row.get("periodo"),
                row.get("quantidade_transacoes"),
                String.format("R$ %.2f", totalGastos),
                String.format("R$ %.2f", totalReceitas),
                String.format("R$ %.2f", saldo)
            };
            tableModel.addRow(rowData);
        }
		mostrarTabelaRelatorio(tableModel, "📅 Resumo Financeiro por Período");
	}

	private void exibirRelatorio7() {
		List<Map<String, Object>> dados = relatorioService.gruposAtivosVsInativos(acessoAtual.getCliente().getId());        if (dados.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Nenhum grupo foi encontrado.",
                "Sem Resultados",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] colunas = {"Status", "Nome", "Descrição", "Membros", "Transações"};
        DefaultTableModel tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (Map<String, Object> row : dados) {
            Object[] rowData = {
                row.get("status"),
                row.get("nome"),
                row.get("descricao"),
                row.get("membros"),
                row.get("transacoes")
            };
            tableModel.addRow(rowData);
        }
        mostrarTabelaRelatorio(tableModel, "🔄 Grupos Ativos vs Inativos");
    }

    private void mostrarTabelaRelatorio(DefaultTableModel tableModel, String titulo) {
        JDialog dialog = new JDialog(this, titulo, true);
        dialog.setSize(900, 500);
        dialog.setLocationRelativeTo(this);
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);
        // Título
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setForeground(new Color(26, 188, 156));      // BTN_INFO
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        mainPanel.add(lblTitulo, BorderLayout.NORTH);
        // Tabela
        JTable table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        UIHelper.configureTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        // Informação de total
        JLabel lblTotal = new JLabel("Total de registros: " + tableModel.getRowCount());
        lblTotal.setFont(new Font("Arial", Font.BOLD, 12));
        lblTotal.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        mainPanel.add(lblTotal, BorderLayout.SOUTH);
        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
    }

    private void voltar() {
        mainFrame.setVisible(true);
        this.dispose();
    }
}
