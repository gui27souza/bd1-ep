package main.java.gui;

import main.java.model.Acesso;
import main.java.model.Grupo;
import main.java.model.transacao.Transacao;
import main.java.service.GrupoService;
import main.java.service.TransacaoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class TransacoesFrame extends JFrame {
    
    private MainFrame mainFrame;
    private Acesso acessoAtual;
    private TransacaoService transacaoService;
    private GrupoService grupoService;
    
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
        titlePanel.setBackground(new Color(76, 175, 80));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("💰 Transações");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton btnPorGrupo = new JButton("Ver por Grupo");
        btnPorGrupo.setFont(new Font("Arial", Font.BOLD, 14));
        btnPorGrupo.setBackground(new Color(33, 150, 243));
        btnPorGrupo.setForeground(Color.WHITE);
        btnPorGrupo.setFocusPainted(false);
        btnPorGrupo.setBorderPainted(false);
        btnPorGrupo.setOpaque(true);
        btnPorGrupo.setPreferredSize(new Dimension(170, 45));
        
        JButton btnTodas = new JButton("Ver Todas");
        btnTodas.setFont(new Font("Arial", Font.BOLD, 14));
        btnTodas.setBackground(new Color(76, 175, 80));
        btnTodas.setForeground(Color.WHITE);
        btnTodas.setFocusPainted(false);
        btnTodas.setBorderPainted(false);
        btnTodas.setOpaque(true);
        btnTodas.setPreferredSize(new Dimension(170, 45));
        
        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.setFont(new Font("Arial", Font.BOLD, 14));
        btnVoltar.setBackground(new Color(158, 158, 158));
        btnVoltar.setForeground(Color.WHITE);
        btnVoltar.setFocusPainted(false);
        btnVoltar.setBorderPainted(false);
        btnVoltar.setOpaque(true);
        btnVoltar.setPreferredSize(new Dimension(120, 45));
        
        btnPorGrupo.addActionListener(e -> verTransacoesPorGrupo());
        btnTodas.addActionListener(e -> verTodasTransacoes());
        btnVoltar.addActionListener(e -> voltar());
        
        buttonPanel.add(btnPorGrupo);
        buttonPanel.add(btnTodas);
        buttonPanel.add(btnVoltar);
        
        // Painel central com instruções
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        JLabel instrucaoLabel = new JLabel("<html><center>" +
            "<h2>Escolha uma opção</h2>" +
            "<p>Selecione <b>Ver por Grupo</b> para visualizar transações de um grupo específico</p>" +
            "<p>ou <b>Ver Todas</b> para visualizar todas as suas transações.</p>" +
            "</center></html>");
        instrucaoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(instrucaoLabel);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
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
            exibirTransacoes(transacoes, "Todas as Minhas Transações");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao buscar transações: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exibirTransacoes(ArrayList<Transacao> transacoes, String titulo) {
        JDialog dialog = new JDialog(this, titulo, true);
        dialog.setSize(900, 500);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        if (transacoes.isEmpty()) {
            JLabel msgLabel = new JLabel("Nenhuma transação encontrada.");
            msgLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            msgLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(msgLabel, BorderLayout.CENTER);
        } else {
            // Criar tabela
            String[] colunas = {"ID", "Valor", "Descrição", "Categoria", "Tipo"};
            DefaultTableModel model = new DefaultTableModel(colunas, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            for (Transacao t : transacoes) {
                model.addRow(new Object[]{
                    t.getId(),
                    String.format("R$ %.2f", t.getValor()),
                    t.getDescricao() != null ? t.getDescricao() : "",
                    t.getCategoria().getNome(),
                    t.getClass().getSimpleName().replace("Transacao", "")
                });
            }
            
            JTable table = new JTable(model);
            table.setFont(new Font("Arial", Font.PLAIN, 12));
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
            table.setRowHeight(25);
            
            JScrollPane scrollPane = new JScrollPane(table);
            panel.add(scrollPane, BorderLayout.CENTER);
        }
        
        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.add(btnFechar);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void voltar() {
        mainFrame.setVisible(true);
        this.dispose();
    }
}
