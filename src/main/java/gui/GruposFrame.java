package main.java.gui;

import main.java.model.Acesso;
import main.java.model.Cliente;
import main.java.model.Grupo;
import main.java.model.transacao.Transacao;
import main.java.model.transacao.TransacaoPix;
import main.java.model.transacao.TransacaoCartao;
import main.java.service.ClienteService;
import main.java.service.GrupoService;
import main.java.service.TransacaoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class GruposFrame extends JFrame {
    
    private MainFrame mainFrame;
    private Acesso acessoAtual;
    private GrupoService grupoService;
    private ClienteService clienteService;
    private TransacaoService transacaoService;
    private DefaultListModel<String> listModel;
    private JList<String> gruposList;
    private ArrayList<Grupo> grupos;
    private JPanel detailsPanel;
    
    public GruposFrame(MainFrame mainFrame, Acesso acessoAtual, GrupoService grupoService,
                      ClienteService clienteService, TransacaoService transacaoService) {
        this.mainFrame = mainFrame;
        this.acessoAtual = acessoAtual;
        this.grupoService = grupoService;
        this.clienteService = clienteService;
        this.transacaoService = transacaoService;
        
        initComponents();
        carregarGrupos("Todos");
    }
    
    private void initComponents() {
        setTitle("Gerenciar Grupos");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                voltar();
            }
        });
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Painel principal com split
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 240, 240));
        
        // Painel de título
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(33, 150, 243));     // BTN_PRIMARY
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("👥 Meus Grupos");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Split pane para lista e detalhes
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.3);
        
        // Painel esquerdo - Lista de grupos
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBackground(new Color(240, 240, 240));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Painel superior com label e filtro
        JPanel topLeftPanel = new JPanel(new BorderLayout(5, 5));
        topLeftPanel.setBackground(new Color(240, 240, 240));
        
        JLabel lblGrupos = new JLabel("Seus Grupos:");
        lblGrupos.setFont(new Font("Arial", Font.BOLD, 14));
        lblGrupos.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        topLeftPanel.add(lblGrupos, BorderLayout.WEST);
        
        // ComboBox para filtrar por status
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        filterPanel.setBackground(new Color(240, 240, 240));
        
        JLabel lblFiltro = new JLabel("Filtro:");
        lblFiltro.setFont(new Font("Arial", Font.PLAIN, 11));
        filterPanel.add(lblFiltro);
        
        JComboBox<String> cbFiltroStatus = new JComboBox<>(new String[]{
            "Todos", "Ativos", "Arquivados", "Inativos"
        });
        cbFiltroStatus.setFont(new Font("Arial", Font.PLAIN, 11));
        cbFiltroStatus.setPreferredSize(new Dimension(100, 25));
        cbFiltroStatus.addActionListener(e -> carregarGrupos((String) cbFiltroStatus.getSelectedItem()));
        filterPanel.add(cbFiltroStatus);
        
        topLeftPanel.add(filterPanel, BorderLayout.EAST);
        leftPanel.add(topLeftPanel, BorderLayout.NORTH);
        
        listModel = new DefaultListModel<>();
        gruposList = new JList<>(listModel);
        gruposList.setFont(new Font("Arial", Font.PLAIN, 13));
        gruposList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        gruposList.setBackground(Color.WHITE);
        gruposList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Listener para mostrar detalhes ao selecionar
        gruposList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && gruposList.getSelectedIndex() != -1) {
                mostrarDetalhesGrupo();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(gruposList);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        
        splitPane.setLeftComponent(leftPanel);
        
        // Painel direito - Detalhes do grupo
        detailsPanel = new JPanel(new BorderLayout(10, 10));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel lblSelecione = new JLabel("<html><div style='text-align: center; color: gray;'>" +
            "<h2>👈 Selecione um grupo</h2>" +
            "<p>Clique em um grupo da lista para ver seus detalhes,<br>" +
            "integrantes e transações.</p></div></html>", SwingConstants.CENTER);
        detailsPanel.add(lblSelecione, BorderLayout.CENTER);
        
        splitPane.setRightComponent(detailsPanel);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton btnCriarGrupo = UIHelper.createButton("Criar Novo Grupo", new Color(77, 182, 172), 180, 45); // BTN_LIGHT
        JButton btnVoltar = UIHelper.createButton("Voltar", new Color(158, 158, 158), 120, 45);         // BTN_NEUTRAL
        
        btnCriarGrupo.addActionListener(e -> criarNovoGrupo());
        btnVoltar.addActionListener(e -> voltar());
        
        buttonPanel.add(btnCriarGrupo);
        buttonPanel.add(btnVoltar);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        UIHelper.ensureVisibility(this);
    }
    
    private void carregarGrupos(String filtroStatus) {
        listModel.clear();
        grupos = acessoAtual.getGrupos();
        
        // Aplicar filtro
        ArrayList<Grupo> gruposFiltrados = new ArrayList<>();
        for (Grupo g : grupos) {
            if (filtroStatus.equals("Todos")) {
                gruposFiltrados.add(g);
            } else if (filtroStatus.equals("Ativos") && g.getStatus().equals("ativo")) {
                gruposFiltrados.add(g);
            } else if (filtroStatus.equals("Arquivados") && g.getStatus().equals("arquivado")) {
                gruposFiltrados.add(g);
            } else if (filtroStatus.equals("Inativos") && g.getStatus().equals("inativo")) {
                gruposFiltrados.add(g);
            }
        }
        
        // Substituir lista de grupos pela filtrada
        grupos = gruposFiltrados;
        
        if (grupos.isEmpty()) {
            listModel.addElement("Nenhum grupo encontrado");
        } else {
            for (Grupo g : grupos) {
                String statusIcon = switch (g.getStatus()) {
                    case "ativo" -> "✅";
                    case "arquivado" -> "📦";
                    case "inativo" -> "⏸️";
                    default -> "";
                };
                listModel.addElement(statusIcon + " " + g.getNome() + " (" + g.getStatus() + ")");
            }
        }
    }
    
    private void verDetalhesGrupo() {
        int selectedIndex = gruposList.getSelectedIndex();
        
        if (selectedIndex == -1 || grupos.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Selecione um grupo para ver os detalhes.", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        mostrarDetalhesGrupo();
    }
    
    private void mostrarDetalhesGrupo() {
        int selectedIndex = gruposList.getSelectedIndex();
        if (selectedIndex == -1 || grupos.isEmpty()) {
            return;
        }
        
        Grupo grupo = grupos.get(selectedIndex);
        
        // Limpar painel de detalhes
        detailsPanel.removeAll();
        detailsPanel.setLayout(new BorderLayout(10, 10));
        
        // Painel superior com informações do grupo
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel lblNome = new JLabel(grupo.getNome());
        lblNome.setFont(new Font("Arial", Font.BOLD, 18));
        lblNome.setForeground(new Color(33, 150, 243));        // BTN_PRIMARY
        
        JLabel lblDescricao = new JLabel(
            (grupo.getDescricao() != null && !grupo.getDescricao().isEmpty()) 
                ? grupo.getDescricao() 
                : "Sem descrição"
        );
        lblDescricao.setFont(new Font("Arial", Font.PLAIN, 12));
        lblDescricao.setForeground(Color.GRAY);
        
        JLabel lblStatus = new JLabel("Status: " + grupo.getStatus().toUpperCase());
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 12));
        lblStatus.setForeground(grupo.getStatus().equals("ativo") ? new Color(26, 188, 156) : Color.RED); // BTN_INFO
        
        JLabel lblData = new JLabel("Criado em: " + grupo.getDataCriacao());
        lblData.setFont(new Font("Arial", Font.PLAIN, 11));
        lblData.setForeground(Color.GRAY);
        
        // Calcular e exibir saldo do grupo
        try {
            float saldo = grupoService.getSaldoGrupo(grupo.getId());
            JLabel lblSaldo = new JLabel(String.format("Saldo: R$ %.2f", saldo));
            lblSaldo.setFont(new Font("Arial", Font.BOLD, 14));
            lblSaldo.setForeground(saldo >= 0 ? new Color(0, 150, 0) : new Color(220, 20, 60));
            
            infoPanel.add(lblNome);
            infoPanel.add(Box.createVerticalStrut(5));
            infoPanel.add(lblDescricao);
            infoPanel.add(Box.createVerticalStrut(5));
            infoPanel.add(lblStatus);
            infoPanel.add(Box.createVerticalStrut(3));
            infoPanel.add(lblData);
            infoPanel.add(Box.createVerticalStrut(8));
            infoPanel.add(lblSaldo);
        } catch (SQLException e) {
            infoPanel.add(lblNome);
            infoPanel.add(Box.createVerticalStrut(5));
            infoPanel.add(lblDescricao);
            infoPanel.add(Box.createVerticalStrut(5));
            infoPanel.add(lblStatus);
            infoPanel.add(Box.createVerticalStrut(3));
            infoPanel.add(lblData);
        }
        
        detailsPanel.add(infoPanel, BorderLayout.NORTH);
        
        // Tabs para integrantes e transações
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Tab 1: Integrantes
        JPanel integrantesPanel = criarPainelIntegrantes(grupo);
        tabbedPane.addTab("👥 Integrantes", integrantesPanel);
        
        // Tab 2: Transações
        JPanel transacoesPanel = criarPainelTransacoes(grupo);
        tabbedPane.addTab("💰 Transações", transacoesPanel);
        
        detailsPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Painel de botões de administração (se for admin)
        try {
            boolean isAdmin = grupoService.isAdmin(acessoAtual.getCliente().getId(), grupo.getId());
            if (isAdmin) {
                JPanel adminPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
                adminPanel.setBackground(Color.WHITE);
                adminPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                
                if (grupo.getStatus().equals("ativo")) {
                    JButton btnArquivar = UIHelper.createButton("📦 Arquivar", new Color(255, 152, 0), 130, 35);
                    btnArquivar.addActionListener(e -> arquivarGrupo(grupo));
                    
                    JButton btnDeletar = UIHelper.createButton("🗑️ Deletar", new Color(244, 67, 54), 130, 35);
                    btnDeletar.addActionListener(e -> deletarGrupo(grupo));
                    
                    adminPanel.add(btnArquivar);
                    adminPanel.add(btnDeletar);
                    
                } else if (grupo.getStatus().equals("arquivado")) {
                    JButton btnDesarquivar = UIHelper.createButton("✅ Desarquivar", new Color(26, 188, 156), 160, 35); // BTN_INFO
                    btnDesarquivar.addActionListener(e -> desarquivarGrupo(grupo));
                    
                    JButton btnDeletar = UIHelper.createButton("🗑️ Deletar", new Color(244, 67, 54), 130, 35);
                    btnDeletar.addActionListener(e -> deletarGrupo(grupo));
                    
                    adminPanel.add(btnDesarquivar);
                    adminPanel.add(btnDeletar);
                    
                } else if (grupo.getStatus().equals("inativo")) {
                    JLabel lblDeletado = new JLabel("❌ Este grupo foi deletado");
                    lblDeletado.setFont(new Font("Arial", Font.BOLD, 13));
                    lblDeletado.setForeground(new Color(244, 67, 54));
                    adminPanel.add(lblDeletado);
                }
                
                detailsPanel.add(adminPanel, BorderLayout.SOUTH);
            }
        } catch (SQLException e) {
            // Se falhar ao verificar se é admin, não mostra os botões
            e.printStackTrace();
        }
        
        detailsPanel.revalidate();
        detailsPanel.repaint();
    }
    
    private JPanel criarPainelIntegrantes(Grupo grupo) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        try {
            ArrayList<GrupoService.MembroInfo> membros = grupoService.getMembrosComRole(grupo.getId());
            
            if (membros.isEmpty()) {
                JLabel lblVazio = new JLabel("<html><div style='text-align: center; color: gray;'>" +
                    "<p>Nenhum membro encontrado.</p></div></html>", SwingConstants.CENTER);
                panel.add(lblVazio, BorderLayout.CENTER);
            } else {
                DefaultListModel<String> membrosModel = new DefaultListModel<>();
                for (GrupoService.MembroInfo membroInfo : membros) {
                    Cliente membro = membroInfo.cliente;
                    String role = membroInfo.role;
                    String cpfMasked = membro.getCpf().substring(0, 3) + ".***.***-" + 
                                      membro.getCpf().substring(membro.getCpf().length() - 2);
                    
                    String roleIcon = role.equals("admin") ? "👑" : "👤";
                    String roleText = role.equals("admin") ? " [ADMIN]" : "";
                    membrosModel.addElement(roleIcon + " " + membro.getNome() + roleText + " (" + cpfMasked + ")");
                }
                
                JList<String> membrosList = new JList<>(membrosModel);
                membrosList.setFont(new Font("Arial", Font.PLAIN, 13));
                membrosList.setBackground(Color.WHITE);
                membrosList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                UIHelper.configureList(membrosList);
                
                JScrollPane scrollPane = new JScrollPane(membrosList);
                scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                
                // Painel superior com total e botão de remover (se for admin)
                JPanel topPanel = new JPanel(new BorderLayout(10, 5));
                topPanel.setBackground(Color.WHITE);
                
                JLabel lblTotal = new JLabel("Total: " + membros.size() + " membro(s)");
                lblTotal.setFont(new Font("Arial", Font.BOLD, 12));
                lblTotal.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                topPanel.add(lblTotal, BorderLayout.WEST);
                
                // Adicionar botão de remover se for admin e grupo estiver ativo
                boolean isAdmin = grupoService.isAdmin(acessoAtual.getCliente().getId(), grupo.getId());
                if (isAdmin && grupo.getStatus().equals("ativo")) {
                    JButton btnRemover = UIHelper.createButton("➖ Remover Membro", new Color(244, 67, 54), 160, 30);
                    btnRemover.addActionListener(e -> removerMembroDialog(grupo, membros));
                    topPanel.add(btnRemover, BorderLayout.EAST);
                }
                
                panel.add(topPanel, BorderLayout.NORTH);
                panel.add(scrollPane, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            JLabel lblErro = new JLabel("<html><div style='text-align: center; color: red;'>" +
                "<p>Erro ao carregar integrantes: " + e.getMessage() + "</p></div></html>", 
                SwingConstants.CENTER);
            panel.add(lblErro, BorderLayout.CENTER);
        }
        
        return panel;
    }
    
    private JPanel criarPainelTransacoes(Grupo grupo) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        try {
            ArrayList<Transacao> transacoes = transacaoService.getTransacoesPorGrupo(grupo.getId());
            
            if (transacoes.isEmpty()) {
                JLabel lblVazio = new JLabel("<html><div style='text-align: center; color: gray;'>" +
                    "<p>Nenhuma transação registrada neste grupo.</p></div></html>", 
                    SwingConstants.CENTER);
                panel.add(lblVazio, BorderLayout.CENTER);
            } else {
                String[] colunas = {"Data", "Valor", "Descrição", "Categoria"};
                DefaultTableModel tableModel = new DefaultTableModel(colunas, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                
                for (Transacao t : transacoes) {
                    String tipoTransacao = "";
                    if (t instanceof TransacaoPix) {
                        tipoTransacao = "PIX";
                    } else if (t instanceof TransacaoCartao) {
                        tipoTransacao = "Cartão";
                    } else {
                        tipoTransacao = "Outro";
                    }
                    
                    String dataFormatada = "";
                    if (t.getDataTransacao() != null) {
                        dataFormatada = dateFormat.format(t.getDataTransacao());
                    }
                    
                    Object[] row = {
                        dataFormatada,
                        String.format("R$ %.2f", t.getValor()),
                        t.getDescricao(),
                        t.getCategoria().getNome() + " (" + tipoTransacao + ")"
                    };
                    tableModel.addRow(row);
                }
                
                JTable table = new JTable(tableModel);
                table.setFont(new Font("Arial", Font.PLAIN, 12));
                table.setRowHeight(25);
                table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
                UIHelper.configureTable(table);
                
                JScrollPane scrollPane = new JScrollPane(table);
                scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                
                JLabel lblTotal = new JLabel("Total: " + transacoes.size() + " transação(ões)");
                lblTotal.setFont(new Font("Arial", Font.BOLD, 12));
                lblTotal.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                
                panel.add(lblTotal, BorderLayout.NORTH);
                panel.add(scrollPane, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            JLabel lblErro = new JLabel("<html><div style='text-align: center; color: red;'>" +
                "<p>Erro ao carregar transações: " + e.getMessage() + "</p></div></html>", 
                SwingConstants.CENTER);
            panel.add(lblErro, BorderLayout.CENTER);
        }
        
        return panel;
    }
    
    private void criarNovoGrupo() {
        // Painel para criar grupo
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblNome = new JLabel("Nome do grupo:");
        lblNome.setFont(new Font("Arial", Font.BOLD, 12));
        JTextField txtNome = new JTextField();
        
        JLabel lblDescricao = new JLabel("Descrição (opcional):");
        lblDescricao.setFont(new Font("Arial", Font.BOLD, 12));
        JTextField txtDescricao = new JTextField();
        
        panel.add(lblNome);
        panel.add(txtNome);
        panel.add(lblDescricao);
        panel.add(txtDescricao);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Criar Novo Grupo", 
            JOptionPane.OK_CANCEL_OPTION, 
            JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String nome = txtNome.getText().trim();
            String descricao = txtDescricao.getText().trim();
            
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "O nome do grupo é obrigatório!", 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                Grupo novoGrupo = grupoService.criarGrupoComAdmin(
                    nome, 
                    descricao.isEmpty() ? null : descricao,
                    acessoAtual.getCliente().getId()
                );
                
                // Atualiza lista de grupos
                ArrayList<Grupo> gruposAtualizados = grupoService.getGrupos(acessoAtual.getCliente());
                acessoAtual.setGrupos(gruposAtualizados);
                
                carregarGrupos("Todos");
                
                JOptionPane.showMessageDialog(this, 
                    "<html><h3>✓ Grupo criado com sucesso!</h3>" +
                    "<p><b>Nome:</b> " + novoGrupo.getNome() + "</p>" +
                    "<p>Você é o administrador deste grupo.</p>" +
                    "<p>Agora você pode enviar convites para outros membros!</p></html>", 
                    "Sucesso", 
                    JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao criar grupo: " + e.getMessage(), 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void arquivarGrupo(Grupo grupo) {
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("<html><b>Arquivar o grupo '%s'?</b><br><br>" +
                "O grupo ficará marcado como arquivado mas não será deletado.<br>" +
                "Você poderá desarquivá-lo posteriormente.</html>", grupo.getNome()),
            "Confirmar Arquivamento",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                grupoService.arquivarGrupo(grupo.getId(), acessoAtual.getCliente().getId());
                atualizarGrupoELista(grupo);
                
                JOptionPane.showMessageDialog(this,
                    "Grupo arquivado com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Erro ao arquivar grupo: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private void desarquivarGrupo(Grupo grupo) {
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Deseja desarquivar o grupo '%s'?", grupo.getNome()),
            "Confirmar Desarquivamento",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                grupoService.desarquivarGrupo(grupo.getId(), acessoAtual.getCliente().getId());
                atualizarGrupoELista(grupo);
                
                JOptionPane.showMessageDialog(this,
                    "Grupo desarquivado com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Erro ao desarquivar grupo: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private void deletarGrupo(Grupo grupo) {
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("<html><b style='color: red;'>ATENÇÃO: Deletar o grupo '%s'?</b><br><br>" +
                "Esta ação marcará o grupo como INATIVO (deletado).<br>" +
                "O grupo não será removido do banco, mas não poderá ser usado.<br><br>" +
                "<b>Deseja continuar?</b></html>", grupo.getNome()),
            "⚠️ Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                grupoService.deletarGrupo(grupo.getId(), acessoAtual.getCliente().getId());
                atualizarGrupoELista(grupo);
                
                JOptionPane.showMessageDialog(this,
                    "<html><b>Grupo deletado com sucesso!</b><br>" +
                    "O grupo foi marcado como inativo.</html>",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Erro ao deletar grupo: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private void removerMembroDialog(Grupo grupo, ArrayList<GrupoService.MembroInfo> membros) {
        // Filtrar apenas membros não-admin para remover
        ArrayList<GrupoService.MembroInfo> membrosRemoveveis = new ArrayList<>();
        for (GrupoService.MembroInfo membroInfo : membros) {
            // Não pode remover admin nem a si mesmo
            if (!membroInfo.role.equals("admin") && 
                membroInfo.cliente.getId() != acessoAtual.getCliente().getId()) {
                membrosRemoveveis.add(membroInfo);
            }
        }
        
        if (membrosRemoveveis.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Não há membros disponíveis para remoção.\n" +
                "Administradores não podem ser removidos.",
                "Aviso",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Criar dialog de seleção
        JDialog dialog = new JDialog(this, "Remover Membro do Grupo", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(Color.WHITE);
        
        // Título
        JLabel lblTitulo = new JLabel("<html><b>Selecione o membro para remover:</b></html>");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(lblTitulo, BorderLayout.NORTH);
        
        // Lista de membros
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (GrupoService.MembroInfo membroInfo : membrosRemoveveis) {
            Cliente membro = membroInfo.cliente;
            String cpfMasked = membro.getCpf().substring(0, 3) + ".***.***-" + 
                              membro.getCpf().substring(membro.getCpf().length() - 2);
            listModel.addElement("👤 " + membro.getNome() + " (" + cpfMasked + ")");
        }
        
        JList<String> membrosList = new JList<>(listModel);
        membrosList.setFont(new Font("Arial", Font.PLAIN, 13));
        membrosList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        membrosList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JScrollPane scrollPane = new JScrollPane(membrosList);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Painel de botões
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(Color.WHITE);
        
        JButton btnRemover = new JButton("🗑️ Remover");
        btnRemover.setBackground(new Color(244, 67, 54));      // BTN_DANGER
        btnRemover.setForeground(Color.WHITE);
        btnRemover.setFocusPainted(false);
        btnRemover.setFont(new Font("Arial", Font.BOLD, 13));
        btnRemover.setPreferredSize(new Dimension(130, 35));
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(158, 158, 158));   // BTN_NEUTRAL
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 13));
        btnCancelar.setPreferredSize(new Dimension(130, 35));
        
        btnRemover.addActionListener(e -> {
            int selectedIndex = membrosList.getSelectedIndex();
            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(dialog,
                    "Selecione um membro para remover.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            GrupoService.MembroInfo membroSelecionado = membrosRemoveveis.get(selectedIndex);
            
            // Confirmação
            int confirm = JOptionPane.showConfirmDialog(dialog,
                String.format("Tem certeza que deseja remover '%s' do grupo '%s'?",
                    membroSelecionado.cliente.getNome(), grupo.getNome()),
                "Confirmar Remoção",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    grupoService.removerMembro(
                        grupo.getId(),
                        membroSelecionado.cliente.getId(),
                        acessoAtual.getCliente().getId()
                    );
                    
                    JOptionPane.showMessageDialog(dialog,
                        "Membro removido com sucesso!",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    dialog.dispose();
                    
                    // Atualizar visualização do grupo
                    atualizarGrupoELista(grupo);
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                        "Erro ao remover membro: " + ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        btnPanel.add(btnRemover);
        btnPanel.add(btnCancelar);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void atualizarGrupoELista(Grupo grupo) throws Exception {
        // Atualizar a lista de grupos
        ArrayList<Grupo> gruposAtualizados = grupoService.getGrupos(acessoAtual.getCliente());
        acessoAtual.setGrupos(gruposAtualizados);
        carregarGrupos("Todos");
        
        // Reselecionar o grupo para atualizar os detalhes
        for (int i = 0; i < grupos.size(); i++) {
            if (grupos.get(i).getId() == grupo.getId()) {
                gruposList.setSelectedIndex(i);
                break;
            }
        }
    }
    
    private void voltar() {
        mainFrame.setVisible(true);
        this.dispose();
    }
}
