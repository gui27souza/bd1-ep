package main.java.gui;

import main.java.model.Acesso;
import main.java.service.*;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    
    // serviços
    private Acesso acessoAtual;
    private GrupoService grupoService;
    private TransacaoService transacaoService;
    private ClienteService clienteService;
    private CadastroService cadastroService;
    private RelatorioService relatorioService;
    private ConviteService conviteService;
    private PlanoService planoService;
    
    public MainFrame(Acesso acessoAtual, GrupoService grupoService, TransacaoService transacaoService,
                     ClienteService clienteService, CadastroService cadastroService, 
                     RelatorioService relatorioService, ConviteService conviteService, 
                     PlanoService planoService) {
        this.acessoAtual = acessoAtual;
        this.grupoService = grupoService;
        this.transacaoService = transacaoService;
        this.clienteService = clienteService;
        this.cadastroService = cadastroService;
        this.relatorioService = relatorioService;
        this.conviteService = conviteService;
        this.planoService = planoService;
        
        initComponents();
    }
    
    private void initComponents() {
        // configuração da janela
        setTitle("ClientApp - Sistema de Gestão Financeira");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 550);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // painel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(new Color(240, 240, 240));
        
        // cabeçalho
        JLabel welcomeLabel = new JLabel("Bem vindo, " + acessoAtual.getCliente().getNome() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));
        welcomeLabel.setForeground(new Color(33, 150, 243));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(welcomeLabel);
        
        // subtítulo
        JLabel subtitleLabel = new JLabel("Selecione uma opção abaixo:");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(subtitleLabel);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // botões do menu
        JButton btnGrupos = createMenuButton("Gerenciar Grupos", new Color(33, 150, 243));
        JButton btnTransacoes = createMenuButton("Ver Transações", new Color(41, 182, 246));
        JButton btnConvites = createMenuButton("Ver Convites", new Color(38, 198, 218));
        JButton btnCadastro = createMenuButton("Ver Meus Dados", new Color(77, 182, 172));
        JButton btnRelatorios = createMenuButton("Relatórios e Consultas", new Color(26, 188, 156));
        JButton btnSair = createMenuButton("Sair", new Color(158, 158, 158));
        
        // listeners
        btnGrupos.addActionListener(e -> abrirGrupos());
        btnTransacoes.addActionListener(e -> abrirTransacoes());
        btnConvites.addActionListener(e -> abrirConvites());
        btnCadastro.addActionListener(e -> abrirCadastro());
        btnRelatorios.addActionListener(e -> abrirRelatorios());
        btnSair.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Deseja realmente sair?", 
                "Confirmar", 
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        
        // montagem do layout
        mainPanel.add(btnGrupos);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        mainPanel.add(btnTransacoes);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        mainPanel.add(btnConvites);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        mainPanel.add(btnCadastro);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        mainPanel.add(btnRelatorios);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        mainPanel.add(btnSair);
        
        add(mainPanel);
    }
    
    private JButton createMenuButton(String text, Color color) {
        JButton button = new JButton(text);
        
        // configuração visual
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(400, 45));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // efeito hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private void abrirGrupos() {
        GruposFrame gruposFrame = new GruposFrame(this, acessoAtual, grupoService, 
                                                   clienteService, transacaoService);
        gruposFrame.setVisible(true);
        this.setVisible(false);
    }
    
    private void abrirTransacoes() {
        TransacoesFrame transacoesFrame = new TransacoesFrame(this, acessoAtual, transacaoService, grupoService);
        transacoesFrame.setVisible(true);
        this.setVisible(false);
    }
    
    private void abrirConvites() {
        ConvitesFrame convitesFrame = new ConvitesFrame(this, acessoAtual, conviteService, grupoService, clienteService);
        convitesFrame.setVisible(true);
        this.setVisible(false);
    }
    
    private void abrirCadastro() {
        CadastroFrame cadastroFrame = new CadastroFrame(this, acessoAtual, clienteService, cadastroService, planoService);
        cadastroFrame.setVisible(true);
        this.setVisible(false);
    }
    
    private void abrirRelatorios() {
        RelatoriosFrame relatoriosFrame = new RelatoriosFrame(this, acessoAtual, relatorioService);
        relatoriosFrame.setVisible(true);
        this.setVisible(false);
    }
}
