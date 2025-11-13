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
        carregarGrupos();
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
        titlePanel.setBackground(new Color(33, 150, 243));
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
        
        JLabel lblGrupos = new JLabel("Seus Grupos:");
        lblGrupos.setFont(new Font("Arial", Font.BOLD, 14));
        lblGrupos.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        leftPanel.add(lblGrupos, BorderLayout.NORTH);
        
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
        
        JButton btnCriarGrupo = UIHelper.createButton("Criar Novo Grupo", UIHelper.GREEN, 180, 45);
        JButton btnVoltar = UIHelper.createButton("Voltar", UIHelper.GRAY, 120, 45);
        
        btnCriarGrupo.addActionListener(e -> criarNovoGrupo());
        btnVoltar.addActionListener(e -> voltar());
        
        buttonPanel.add(btnCriarGrupo);
        buttonPanel.add(btnVoltar);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        UIHelper.ensureVisibility(this);
    }
    
    private void carregarGrupos() {
        listModel.clear();
        grupos = acessoAtual.getGrupos();
        
        if (grupos.isEmpty()) {
            listModel.addElement("📭 Você não pertence a nenhum grupo ainda.");
        } else {
            for (Grupo grupo : grupos) {
                String status = grupo.getStatus().equals("ativo") ? "✓" : "✗";
                listModel.addElement(status + " " + grupo.getNome() + " - " + grupo.getStatus().toUpperCase());
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
        lblNome.setForeground(UIHelper.BLUE);
        
        JLabel lblDescricao = new JLabel(
            (grupo.getDescricao() != null && !grupo.getDescricao().isEmpty()) 
                ? grupo.getDescricao() 
                : "Sem descrição"
        );
        lblDescricao.setFont(new Font("Arial", Font.PLAIN, 12));
        lblDescricao.setForeground(Color.GRAY);
        
        JLabel lblStatus = new JLabel("Status: " + grupo.getStatus().toUpperCase());
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 12));
        lblStatus.setForeground(grupo.getStatus().equals("ativo") ? UIHelper.GREEN : Color.RED);
        
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
        
        detailsPanel.revalidate();
        detailsPanel.repaint();
    }
    
    private JPanel criarPainelIntegrantes(Grupo grupo) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        try {
            ArrayList<Cliente> membros = grupoService.getMembros(grupo.getId());
            
            if (membros.isEmpty()) {
                JLabel lblVazio = new JLabel("<html><div style='text-align: center; color: gray;'>" +
                    "<p>Nenhum membro encontrado.</p></div></html>", SwingConstants.CENTER);
                panel.add(lblVazio, BorderLayout.CENTER);
            } else {
                DefaultListModel<String> membrosModel = new DefaultListModel<>();
                for (Cliente membro : membros) {
                    String cpfMasked = membro.getCpf().substring(0, 3) + ".***.***-" + 
                                      membro.getCpf().substring(membro.getCpf().length() - 2);
                    membrosModel.addElement("👤 " + membro.getNome() + " (" + cpfMasked + ")");
                }
                
                JList<String> membrosList = new JList<>(membrosModel);
                membrosList.setFont(new Font("Arial", Font.PLAIN, 13));
                membrosList.setBackground(Color.WHITE);
                membrosList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                UIHelper.configureList(membrosList);
                
                JScrollPane scrollPane = new JScrollPane(membrosList);
                scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                
                JLabel lblTotal = new JLabel("Total: " + membros.size() + " membro(s)");
                lblTotal.setFont(new Font("Arial", Font.BOLD, 12));
                lblTotal.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                
                panel.add(lblTotal, BorderLayout.NORTH);
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
                
                carregarGrupos();
                
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
    
    private void voltar() {
        mainFrame.setVisible(true);
        this.dispose();
    }
}
