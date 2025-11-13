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
        
        JButton btnPorCategoria = new JButton("Ver por Categoria");
        btnPorCategoria.setFont(new Font("Arial", Font.BOLD, 14));
        btnPorCategoria.setBackground(new Color(255, 152, 0));
        btnPorCategoria.setForeground(Color.WHITE);
        btnPorCategoria.setFocusPainted(false);
        btnPorCategoria.setBorderPainted(false);
        btnPorCategoria.setOpaque(true);
        btnPorCategoria.setPreferredSize(new Dimension(190, 45));
        
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
        btnPorCategoria.addActionListener(e -> verTransacoesPorCategoria());
        btnVoltar.addActionListener(e -> voltar());
        
        buttonPanel.add(btnPorGrupo);
        buttonPanel.add(btnTodas);
        buttonPanel.add(btnPorCategoria);
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
    
    private void exibirTransacoes(ArrayList<Transacao> transacoes, String titulo) {
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
        String[] colunas = {"Data", "Valor", "Descrição", "Categoria", "Tipo"};
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
            
            Object[] row = {
                dataFormatada,
                String.format("R$ %.2f", t.getValor()),
                t.getDescricao() != null ? t.getDescricao() : "-",
                t.getCategoria().getNome(),
                t.getClass().getSimpleName().replace("Transacao", "")
            };
            model.addRow(row);
        }
        
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(76, 175, 80));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(200, 230, 201));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Informação de total
        JLabel lblTotal = new JLabel(String.format("Total de transações: %d", transacoes.size()));
        lblTotal.setFont(new Font("Arial", Font.BOLD, 12));
        lblTotal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.add(lblTotal, BorderLayout.SOUTH);
        
        centerPanel.revalidate();
        centerPanel.repaint();
    }
    
    private void voltar() {
        mainFrame.setVisible(true);
        this.dispose();
    }
}
