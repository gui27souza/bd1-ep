package main.java.gui;

import main.java.model.Acesso;
import main.java.model.Grupo;
import main.java.model.transacao.CategoriaTransacao;
import main.java.model.transacao.Transacao;
import main.java.service.GrupoService;
import main.java.service.TransacaoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class TransacoesFrame extends JFrame {
    
    private MainFrame mainFrame;
    private Acesso acessoAtual;
    private TransacaoService transacaoService;
    private GrupoService grupoService;
    private JPanel centerPanel;
    private ArrayList<Transacao> transacoesAtuais;
    private JTable tabelaTransacoes;
    
    public TransacoesFrame(MainFrame mainFrame, Acesso acessoAtual, 
                          TransacaoService transacaoService, GrupoService grupoService) {
        this.mainFrame = mainFrame;
        this.acessoAtual = acessoAtual;
        this.transacaoService = transacaoService;
        this.grupoService = grupoService;
        
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Ver Transações");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                voltar();
            }
        });
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(true);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 240, 240));
        
        // Painel de título
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(33, 150, 243));     // BTN_PRIMARY
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("💰 Transações");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Painel de botões com GridLayout para acomodar todos os botões
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 15, 10));
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JButton btnPorGrupo = new JButton("Ver por Grupo");
        btnPorGrupo.setFont(new Font("Arial", Font.BOLD, 14));
        btnPorGrupo.setBackground(new Color(33, 150, 243));    // BTN_PRIMARY
        btnPorGrupo.setForeground(Color.WHITE);
        btnPorGrupo.setFocusPainted(false);
        btnPorGrupo.setBorderPainted(false);
        btnPorGrupo.setOpaque(true);
        
        JButton btnTodas = new JButton("Ver Todas");
        btnTodas.setFont(new Font("Arial", Font.BOLD, 14));
        btnTodas.setBackground(new Color(41, 182, 246));       // BTN_SECONDARY
        btnTodas.setForeground(Color.WHITE);
        btnTodas.setFocusPainted(false);
        btnTodas.setBorderPainted(false);
        btnTodas.setOpaque(true);
        
        JButton btnPorCategoria = new JButton("Ver por Categoria");
        btnPorCategoria.setFont(new Font("Arial", Font.BOLD, 14));
        btnPorCategoria.setBackground(new Color(38, 198, 218)); // BTN_SUCCESS
        btnPorCategoria.setForeground(Color.WHITE);
        btnPorCategoria.setFocusPainted(false);
        btnPorCategoria.setBorderPainted(false);
        btnPorCategoria.setOpaque(true);
        
        JButton btnPorPeriodo = new JButton("📅 Ver por Período");
        btnPorPeriodo.setFont(new Font("Arial", Font.BOLD, 14));
        btnPorPeriodo.setBackground(new Color(26, 188, 156));  // BTN_INFO
        btnPorPeriodo.setForeground(Color.WHITE);
        btnPorPeriodo.setFocusPainted(false);
        btnPorPeriodo.setBorderPainted(false);
        btnPorPeriodo.setOpaque(true);
        
        JButton btnNova = new JButton("➕ Nova Transação");
        btnNova.setFont(new Font("Arial", Font.BOLD, 14));
        btnNova.setBackground(new Color(77, 182, 172));        // BTN_LIGHT
        btnNova.setForeground(Color.WHITE);
        btnNova.setFocusPainted(false);
        btnNova.setBorderPainted(false);
        btnNova.setOpaque(true);
        
        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.setFont(new Font("Arial", Font.BOLD, 14));
        btnVoltar.setBackground(new Color(158, 158, 158));     // BTN_NEUTRAL
        btnVoltar.setForeground(Color.WHITE);
        btnVoltar.setFocusPainted(false);
        btnVoltar.setBorderPainted(false);
        btnVoltar.setOpaque(true);
        
        btnPorGrupo.addActionListener(e -> verTransacoesPorGrupo());
        btnTodas.addActionListener(e -> verTodasTransacoes());
        btnPorCategoria.addActionListener(e -> verTransacoesPorCategoria());
        btnPorPeriodo.addActionListener(e -> verTransacoesPorPeriodo());
        btnNova.addActionListener(e -> novaTransacao());
        btnVoltar.addActionListener(e -> voltar());
        
        // Primeira linha: 3 botões de visualização
        buttonPanel.add(btnPorGrupo);
        buttonPanel.add(btnTodas);
        buttonPanel.add(btnPorCategoria);
        
        // Segunda linha: Período, Nova transação e Voltar
        buttonPanel.add(btnPorPeriodo);
        buttonPanel.add(btnNova);
        buttonPanel.add(btnVoltar);
        
        // Painel central com instruções (será substituído pela tabela)
        centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        exibirInstrucoes();
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void exibirInstrucoes() {
        centerPanel.removeAll();
        centerPanel.setLayout(new GridBagLayout());
        
        JLabel instrucaoLabel = new JLabel("<html><center>" +
            "<h2>Escolha uma opção</h2>" +
            "<p>Selecione <b>Ver por Grupo</b> para visualizar transações de um grupo específico,</p>" +
            "<p><b>Ver Todas</b> para visualizar todas as suas transações,</p>" +
            "<p>ou <b>Ver por Categoria</b> para filtrar por categoria.</p>" +
            "</center></html>");
        instrucaoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(instrucaoLabel);
        
        centerPanel.revalidate();
        centerPanel.repaint();
    }
    
    private void verTransacoesPorCategoria() {
        try {
            // Buscar categorias disponíveis
            ArrayList<CategoriaTransacao> categorias = transacaoService.getCategorias();
            
            if (categorias.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Nenhuma categoria disponível.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Criar array de nomes para o dialog
            String[] nomesCategorias = new String[categorias.size()];
            for (int i = 0; i < categorias.size(); i++) {
                nomesCategorias[i] = categorias.get(i).getNome();
            }
            
            // Mostrar dialog de seleção
            String categoriaSelecionada = (String) JOptionPane.showInputDialog(
                this,
                "Selecione a categoria:",
                "Filtrar por Categoria",
                JOptionPane.QUESTION_MESSAGE,
                null,
                nomesCategorias,
                nomesCategorias[0]
            );
            
            if (categoriaSelecionada == null) {
                return; // Usuário cancelou
            }
            
            // Encontrar a categoria selecionada
            CategoriaTransacao categoria = null;
            for (CategoriaTransacao c : categorias) {
                if (c.getNome().equals(categoriaSelecionada)) {
                    categoria = c;
                    break;
                }
            }
            
            if (categoria == null) {
                return;
            }
            
            // Buscar transações da categoria
            ArrayList<Transacao> transacoes = transacaoService.getTransacoesPorCategoria(
                acessoAtual.getCliente().getId(), 
                categoria.getId()
            );
            
            if (transacoes.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Nenhuma transação encontrada para a categoria " + categoria.getNome() + ".",
                    "Informação",
                    JOptionPane.INFORMATION_MESSAGE);
                exibirInstrucoes();
                return;
            }
            
            // Exibir tabela de transações no painel central
            exibirTransacoes(transacoes, "Categoria: " + categoria.getNome());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao buscar transações por categoria: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void verTransacoesPorGrupo() {
        ArrayList<Grupo> grupos = acessoAtual.getGrupos();
        
        if (grupos.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Você não pertence a nenhum grupo ainda.", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Criar lista de nomes de grupos
        String[] nomes = new String[grupos.size()];
        for (int i = 0; i < grupos.size(); i++) {
            nomes[i] = grupos.get(i).getNome();
        }
        
        String escolha = (String) JOptionPane.showInputDialog(
            this,
            "Selecione o grupo:",
            "Escolher Grupo",
            JOptionPane.QUESTION_MESSAGE,
            null,
            nomes,
            nomes[0]
        );
        
        if (escolha == null) return;
        
        // Encontrar grupo selecionado
        Grupo grupoSelecionado = null;
        for (Grupo g : grupos) {
            if (g.getNome().equals(escolha)) {
                grupoSelecionado = g;
                break;
            }
        }
        
        if (grupoSelecionado != null) {
            try {
                ArrayList<Transacao> transacoes = transacaoService.getTransacoesPorGrupo(grupoSelecionado.getId());
                
                if (transacoes.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "Nenhuma transação encontrada para este grupo.",
                        "Informação",
                        JOptionPane.INFORMATION_MESSAGE);
                    exibirInstrucoes();
                    return;
                }
                
                exibirTransacoes(transacoes, "Transações do Grupo: " + grupoSelecionado.getNome());
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao buscar transações: " + e.getMessage(), 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void verTodasTransacoes() {
        try {
            ArrayList<Transacao> transacoes = transacaoService.getTodasTransacoes(acessoAtual.getCliente().getId());
            
            if (transacoes.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Você ainda não possui transações.",
                    "Informação",
                    JOptionPane.INFORMATION_MESSAGE);
                exibirInstrucoes();
                return;
            }
            
            exibirTransacoes(transacoes, "Todas as Minhas Transações");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao buscar transações: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void verTransacoesPorPeriodo() {
        // Dialog para selecionar período
        JDialog dialog = new JDialog(this, "Selecionar Período", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Data Início
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Data Início (DD/MM/AAAA):"), gbc);
        
        gbc.gridx = 1;
        JTextField txtDataInicio = new JTextField(10);
        panel.add(txtDataInicio, gbc);
        
        // Data Fim
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Data Fim (DD/MM/AAAA):"), gbc);
        
        gbc.gridx = 1;
        JTextField txtDataFim = new JTextField(10);
        panel.add(txtDataFim, gbc);
        
        // Info
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JLabel lblInfo = new JLabel("<html><i>Exemplos: 01/01/2025 ou 31/12/2025</i></html>");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 10));
        lblInfo.setForeground(Color.GRAY);
        panel.add(lblInfo, gbc);
        gbc.gridwidth = 1;
        
        // Botões
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        JButton btnBuscar = new JButton("🔍 Buscar");
        btnBuscar.setBackground(new Color(33, 150, 243));      // BTN_PRIMARY
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setPreferredSize(new Dimension(120, 35));
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(158, 158, 158));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setPreferredSize(new Dimension(120, 35));
        
        btnBuscar.addActionListener(e -> {
            try {
                String dataInicioStr = txtDataInicio.getText().trim();
                String dataFimStr = txtDataFim.getText().trim();
                
                if (dataInicioStr.isEmpty() || dataFimStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                        "Preencha ambas as datas.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Converter strings para java.sql.Date
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                sdf.setLenient(false);
                
                java.util.Date dateInicio = sdf.parse(dataInicioStr);
                java.util.Date dateFim = sdf.parse(dataFimStr);
                
                java.sql.Date sqlDataInicio = new java.sql.Date(dateInicio.getTime());
                java.sql.Date sqlDataFim = new java.sql.Date(dateFim.getTime());
                
                // Validar que data início é antes da data fim
                if (sqlDataInicio.after(sqlDataFim)) {
                    JOptionPane.showMessageDialog(dialog,
                        "A data inicial deve ser anterior à data final.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                ArrayList<Transacao> transacoes = transacaoService.getTransacoesPorPeriodo(
                    acessoAtual.getCliente().getId(),
                    sqlDataInicio,
                    sqlDataFim
                );
                
                dialog.dispose();
                
                if (transacoes.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "Nenhuma transação encontrada neste período.",
                        "Informação",
                        JOptionPane.INFORMATION_MESSAGE);
                    exibirInstrucoes();
                    return;
                }
                
                String titulo = String.format("Transações de %s a %s", dataInicioStr, dataFimStr);
                exibirTransacoes(transacoes, titulo);
                
            } catch (java.text.ParseException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Formato de data inválido. Use DD/MM/AAAA",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Erro ao buscar transações: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        btnPanel.add(btnBuscar);
        btnPanel.add(btnCancelar);
        panel.add(btnPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void exibirTransacoes(ArrayList<Transacao> transacoes, String titulo) {
        // Armazenar transações atuais
        this.transacoesAtuais = transacoes;
        
        // Limpar painel central
        centerPanel.removeAll();
        centerPanel.setLayout(new BorderLayout(10, 10));
        centerPanel.setBackground(Color.WHITE);
        
        // Título
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setForeground(new Color(76, 175, 80));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.add(lblTitulo, BorderLayout.NORTH);
        
        // Criar tabela
        String[] colunas = {"ID", "Data", "Valor", "Descrição", "Categoria", "Tipo Pagamento"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        for (Transacao t : transacoes) {
            String dataFormatada = "";
            if (t.getDataTransacao() != null) {
                dataFormatada = dateFormat.format(t.getDataTransacao());
            }
            
            // Buscar tipo de pagamento do banco
            String tipoPagamento = "-";
            try {
                tipoPagamento = transacaoService.getTipoTransacao(t.getId());
            } catch (SQLException e) {
                // Se falhar, mantém "-"
            }
            
            Object[] row = {
                t.getId(),
                dataFormatada,
                String.format("R$ %.2f", t.getValor()),
                t.getDescricao() != null ? t.getDescricao() : "-",
                t.getCategoria().getNome(),
                tipoPagamento
            };
            model.addRow(row);
        }
        
        tabelaTransacoes = new JTable(model);
        tabelaTransacoes.setFont(new Font("Arial", Font.PLAIN, 12));
        tabelaTransacoes.setRowHeight(25);
        tabelaTransacoes.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabelaTransacoes.getTableHeader().setBackground(new Color(33, 150, 243)); // BTN_PRIMARY
        tabelaTransacoes.getTableHeader().setForeground(Color.WHITE);
        tabelaTransacoes.setSelectionBackground(new Color(200, 230, 201));
        
        JScrollPane scrollPane = new JScrollPane(tabelaTransacoes);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Painel inferior com total e botões de ação
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        
        JLabel lblTotal = new JLabel(String.format("Total de transações: %d", transacoes.size()));
        lblTotal.setFont(new Font("Arial", Font.BOLD, 12));
        lblTotal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(lblTotal, BorderLayout.WEST);
        
        // Botões de ação
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actionPanel.setBackground(Color.WHITE);
        
        JButton btnEditar = new JButton("✏️ Editar");
        btnEditar.setFont(new Font("Arial", Font.BOLD, 12));
        btnEditar.setBackground(new Color(38, 198, 218));      // BTN_SUCCESS
        btnEditar.setForeground(Color.WHITE);
        btnEditar.setFocusPainted(false);
        btnEditar.setPreferredSize(new Dimension(110, 35));
        
        JButton btnDeletar = new JButton("🗑️ Deletar");
        btnDeletar.setFont(new Font("Arial", Font.BOLD, 12));
        btnDeletar.setBackground(new Color(244, 67, 54));      // BTN_DANGER
        btnDeletar.setForeground(Color.WHITE);
        btnDeletar.setFocusPainted(false);
        btnDeletar.setPreferredSize(new Dimension(110, 35));
        
        btnEditar.addActionListener(e -> editarTransacao());
        btnDeletar.addActionListener(e -> deletarTransacao());
        
        actionPanel.add(btnEditar);
        actionPanel.add(btnDeletar);
        bottomPanel.add(actionPanel, BorderLayout.EAST);
        
        centerPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        centerPanel.revalidate();
        centerPanel.repaint();
    }
    
    private void novaTransacao() {
        ArrayList<Grupo> grupos = acessoAtual.getGrupos();
        
        if (grupos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Você não pertence a nenhum grupo. Entre em um grupo para criar transações.",
                "Aviso",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Dialog para nova transação
        JDialog dialog = new JDialog(this, "Nova Transação", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Grupo
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Grupo:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> cbGrupo = new JComboBox<>();
        for (Grupo g : grupos) {
            cbGrupo.addItem(g.getNome());
        }
        panel.add(cbGrupo, gbc);
        
        // Categoria
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Categoria:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> cbCategoria = new JComboBox<>();
        ArrayList<CategoriaTransacao> categorias = new ArrayList<>();
        try {
            categorias = transacaoService.getCategorias();
            for (CategoriaTransacao c : categorias) {
                cbCategoria.addItem(c.getNome());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(dialog,
                "Erro ao carregar categorias: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
            dialog.dispose();
            return;
        }
        panel.add(cbCategoria, gbc);
        
        // Tipo de Pagamento
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Tipo de Pagamento:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> cbTipoPagamento = new JComboBox<>(new String[]{"PIX", "CARTAO"});
        panel.add(cbTipoPagamento, gbc);
        
        // Valor
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Valor (R$):"), gbc);
        
        gbc.gridx = 1;
        JTextField txtValor = new JTextField(15);
        panel.add(txtValor, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JLabel lblInfo = new JLabel("<html><i>Dica: Use valores negativos para gastos e positivos para receitas/contribuições</i></html>");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 10));
        lblInfo.setForeground(Color.GRAY);
        panel.add(lblInfo, gbc);
        gbc.gridwidth = 1;
        
        // Descrição
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Descrição:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtDescricao = new JTextField(15);
        panel.add(txtDescricao, gbc);
        
        // Botões
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        JButton btnSalvar = new JButton("💾 Salvar");
        btnSalvar.setBackground(new Color(76, 175, 80));
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFocusPainted(false);
        btnSalvar.setPreferredSize(new Dimension(120, 35));
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(158, 158, 158));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setPreferredSize(new Dimension(120, 35));
        
        final ArrayList<CategoriaTransacao> finalCategorias = categorias;
        
        btnSalvar.addActionListener(e -> {
            try {
                // Validações
                if (txtValor.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Informe o valor.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                float valor = Float.parseFloat(txtValor.getText().trim().replace(",", "."));
                String descricao = txtDescricao.getText().trim();
                
                // Buscar grupo e categoria selecionados
                Grupo grupoSelecionado = grupos.get(cbGrupo.getSelectedIndex());
                CategoriaTransacao categoriaSelecionada = finalCategorias.get(cbCategoria.getSelectedIndex());
                
                // Obter tipo de pagamento selecionado
                String tipoTransacao = (String) cbTipoPagamento.getSelectedItem();
                
                // Criar transação (PIX como padrão)
                Transacao novaTransacao = new main.java.model.transacao.TransacaoPix(
                    acessoAtual.getCliente().getId(),
                    grupoSelecionado.getId(),
                    valor,
                    categoriaSelecionada,
                    descricao,
                    new java.sql.Timestamp(System.currentTimeMillis())
                );
                
                transacaoService.criarTransacao(novaTransacao, tipoTransacao);
                
                JOptionPane.showMessageDialog(dialog,
                    "Transação criada com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
                
                dialog.dispose();
                exibirInstrucoes(); // Volta para instruções
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Valor inválido. Use formato: 10.50 ou -10.50",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Erro ao criar transação: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        btnPanel.add(btnSalvar);
        btnPanel.add(btnCancelar);
        panel.add(btnPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void editarTransacao() {
        if (tabelaTransacoes == null || tabelaTransacoes.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this,
                "Selecione uma transação para editar.",
                "Aviso",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int selectedRow = tabelaTransacoes.getSelectedRow();
        int idTransacao = (int) tabelaTransacoes.getValueAt(selectedRow, 0);
        
        // Buscar transação na lista atual
        Transacao transacao = null;
        for (Transacao t : transacoesAtuais) {
            if (t.getId() == idTransacao) {
                transacao = t;
                break;
            }
        }
        
        if (transacao == null) {
            JOptionPane.showMessageDialog(this,
                "Erro ao localizar transação.",
                "Erro",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        ArrayList<Grupo> grupos = acessoAtual.getGrupos();
        
        // Dialog para editar transação
        JDialog dialog = new JDialog(this, "Editar Transação", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Grupo
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Grupo:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> cbGrupo = new JComboBox<>();
        int grupoIndex = 0;
        for (int i = 0; i < grupos.size(); i++) {
            Grupo g = grupos.get(i);
            cbGrupo.addItem(g.getNome());
            if (g.getId() == transacao.getId_grupo()) {
                grupoIndex = i;
            }
        }
        cbGrupo.setSelectedIndex(grupoIndex);
        panel.add(cbGrupo, gbc);
        
        // Categoria
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Categoria:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> cbCategoria = new JComboBox<>();
        ArrayList<CategoriaTransacao> categorias = new ArrayList<>();
        int categoriaIndex = 0;
        try {
            categorias = transacaoService.getCategorias();
            for (int i = 0; i < categorias.size(); i++) {
                CategoriaTransacao c = categorias.get(i);
                cbCategoria.addItem(c.getNome());
                if (c.getId() == transacao.getCategoria().getId()) {
                    categoriaIndex = i;
                }
            }
            cbCategoria.setSelectedIndex(categoriaIndex);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(dialog,
                "Erro ao carregar categorias: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
            dialog.dispose();
            return;
        }
        panel.add(cbCategoria, gbc);
        
        // Tipo de Pagamento
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Tipo de Pagamento:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> cbTipoPagamento = new JComboBox<>(new String[]{"PIX", "CARTAO"});
        // Definir tipo atual
        try {
            String tipoAtual = transacaoService.getTipoTransacao(transacao.getId());
            if (tipoAtual.equals("CARTAO")) {
                cbTipoPagamento.setSelectedIndex(1);
            } else {
                cbTipoPagamento.setSelectedIndex(0);
            }
        } catch (SQLException ex) {
            cbTipoPagamento.setSelectedIndex(0);
        }
        panel.add(cbTipoPagamento, gbc);
        
        // Valor
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Valor (R$):"), gbc);
        
        gbc.gridx = 1;
        JTextField txtValor = new JTextField(String.valueOf(transacao.getValor()), 15);
        panel.add(txtValor, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JLabel lblInfo = new JLabel("<html><i>Dica: Use valores negativos para gastos e positivos para receitas/contribuições</i></html>");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 10));
        lblInfo.setForeground(Color.GRAY);
        panel.add(lblInfo, gbc);
        gbc.gridwidth = 1;
        
        // Descrição
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Descrição:"), gbc);
        
        gbc.gridx = 1;
        JTextField txtDescricao = new JTextField(transacao.getDescricao() != null ? transacao.getDescricao() : "", 15);
        panel.add(txtDescricao, gbc);
        
        // Botões
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        JButton btnSalvar = new JButton("💾 Salvar");
        btnSalvar.setBackground(new Color(76, 175, 80));
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFocusPainted(false);
        btnSalvar.setPreferredSize(new Dimension(120, 35));
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(158, 158, 158));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setPreferredSize(new Dimension(120, 35));
        
        final ArrayList<CategoriaTransacao> finalCategorias = categorias;
        final Transacao finalTransacao = transacao;
        
        btnSalvar.addActionListener(e -> {
            try {
                // Validações
                if (txtValor.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Informe o valor.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                float valor = Float.parseFloat(txtValor.getText().trim().replace(",", "."));
                String descricao = txtDescricao.getText().trim();
                
                // Buscar grupo e categoria selecionados
                Grupo grupoSelecionado = grupos.get(cbGrupo.getSelectedIndex());
                CategoriaTransacao categoriaSelecionada = finalCategorias.get(cbCategoria.getSelectedIndex());
                String tipoTransacao = (String) cbTipoPagamento.getSelectedItem();
                
                // Atualizar transação
                finalTransacao.setId_grupo(grupoSelecionado.getId());
                finalTransacao.setCategoria(categoriaSelecionada);
                finalTransacao.setValor(valor);
                finalTransacao.setDescricao(descricao);
                
                transacaoService.editarTransacao(finalTransacao, tipoTransacao);
                
                JOptionPane.showMessageDialog(dialog,
                    "Transação atualizada com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
                
                dialog.dispose();
                
                // Recarregar lista
                recarregarTransacoes();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Valor inválido. Use formato: 10.50 ou -10.50",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Erro ao atualizar transação: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        btnPanel.add(btnSalvar);
        btnPanel.add(btnCancelar);
        panel.add(btnPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void deletarTransacao() {
        if (tabelaTransacoes == null || tabelaTransacoes.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this,
                "Selecione uma transação para deletar.",
                "Aviso",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int selectedRow = tabelaTransacoes.getSelectedRow();
        int idTransacao = (int) tabelaTransacoes.getValueAt(selectedRow, 0);
        String descricao = (String) tabelaTransacoes.getValueAt(selectedRow, 3);
        
        int confirmacao = JOptionPane.showConfirmDialog(this,
            "Tem certeza que deseja deletar a transação:\n\"" + descricao + "\"?\n\nEsta ação não pode ser desfeita.",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }
        
        try {
            transacaoService.deletarTransacao(idTransacao);
            
            JOptionPane.showMessageDialog(this,
                "Transação deletada com sucesso!",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Recarregar lista
            recarregarTransacoes();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao deletar transação: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void recarregarTransacoes() {
        // Recarrega a última visualização
        if (transacoesAtuais != null && !transacoesAtuais.isEmpty()) {
            try {
                // Tentar detectar qual tipo de visualização estava ativa
                // Por simplicidade, recarrega todas as transações
                ArrayList<Transacao> transacoes = transacaoService.getTodasTransacoes(acessoAtual.getCliente().getId());
                if (transacoes.isEmpty()) {
                    exibirInstrucoes();
                } else {
                    exibirTransacoes(transacoes, "Todas as Minhas Transações");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Erro ao recarregar transações: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void voltar() {
        mainFrame.setVisible(true);
        this.dispose();
    }
}
