package main.java.gui;

import main.java.db.DBConnector;
import main.java.exceptions.DomainException;
import main.java.model.Acesso;
import main.java.service.*;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    
    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private DBConnector dbConnector;
    private CadastroService cadastroService;
    private ClienteService clienteService;
    private GrupoService grupoService;
    
    public LoginFrame(DBConnector dbConnector, CadastroService cadastroService, 
                     ClienteService clienteService, GrupoService grupoService) {
        this.dbConnector = dbConnector;
        this.cadastroService = cadastroService;
        this.clienteService = clienteService;
        this.grupoService = grupoService;
        
        initComponents();
        
        // Garantir visibilidade em todos os sistemas
        UIHelper.ensureVisibility(this);
    }
    
    private void initComponents() {
        setTitle("ClientApp - Bem vindo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Título
        JLabel titleLabel = new JLabel("ClientApp");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(33, 150, 243));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);
        
        JLabel subtitleLabel = new JLabel("Sistema de Gestão Financeira de Grupos");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitleLabel.setForeground(Color.GRAY);
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 8, 20, 8);
        mainPanel.add(subtitleLabel, gbc);
        
        gbc.insets = new Insets(8, 8, 8, 8);
        
        // Email
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lblEmail = new JLabel("E-mail:");
        lblEmail.setFont(new Font("Arial", Font.BOLD, 14));
        lblEmail.setForeground(Color.BLACK);
        mainPanel.add(lblEmail, gbc);
        
        gbc.gridx = 1;
        txtEmail = new JTextField(20);
        txtEmail.setFont(new Font("Arial", Font.PLAIN, 14));
        txtEmail.setPreferredSize(new Dimension(250, 30));
        txtEmail.setBackground(Color.WHITE);
        txtEmail.setForeground(Color.BLACK);
        mainPanel.add(txtEmail, gbc);
        
        // Senha
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setFont(new Font("Arial", Font.BOLD, 14));
        lblSenha.setForeground(Color.BLACK);
        mainPanel.add(lblSenha, gbc);
        
        gbc.gridx = 1;
        txtSenha = new JPasswordField(20);
        txtSenha.setFont(new Font("Arial", Font.PLAIN, 14));
        txtSenha.setPreferredSize(new Dimension(250, 30));
        txtSenha.setBackground(Color.WHITE);
        txtSenha.setForeground(Color.BLACK);
        mainPanel.add(txtSenha, gbc);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(new Color(240, 240, 240));
        
        JButton btnLogin = UIHelper.createButton("Entrar", UIHelper.BLUE, 140, 40);
        JButton btnCadastro = UIHelper.createButton("Criar Conta", UIHelper.GREEN, 140, 40);
        
        btnLogin.addActionListener(e -> fazerLogin());
        btnCadastro.addActionListener(e -> fazerCadastro());
        
        // Enter para fazer login
        txtSenha.addActionListener(e -> fazerLogin());
        
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnCadastro);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 8, 8);
        mainPanel.add(buttonPanel, gbc);
        
        // Usar setContentPane em vez de add direto
        setContentPane(mainPanel);
    }
    
    private void fazerLogin() {
        String email = txtEmail.getText().trim();
        String senha = new String(txtSenha.getPassword());
        
        if (email.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Preencha todos os campos!", 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            Acesso acesso = cadastroService.verificarCredenciais(email, senha);
            
            if (acesso == null) {
                JOptionPane.showMessageDialog(this, 
                    "E-mail ou senha incorretos!", 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            abrirTelaPrincipal(acesso);
            
        } catch (DomainException | SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao fazer login: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void fazerCadastro() {
        CadastroDialog dialog = new CadastroDialog(this, cadastroService, clienteService);
        dialog.setVisible(true);
    }
    
    private void abrirTelaPrincipal(Acesso acesso) {
        TransacaoService transacaoService = new TransacaoService(dbConnector);
        RelatorioService relatorioService = new RelatorioService(dbConnector);
        ConviteService conviteService = new ConviteService(dbConnector, clienteService);
        PlanoService planoService = new PlanoService(dbConnector);
        
        MainFrame mainFrame = new MainFrame(acesso, grupoService, transacaoService, 
            clienteService, cadastroService, relatorioService, conviteService, planoService);
        mainFrame.setVisible(true);
        this.dispose();
    }
}
