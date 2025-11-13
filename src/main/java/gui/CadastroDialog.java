package main.java.gui;

import main.java.model.Cliente;
import main.java.service.CadastroService;
import main.java.service.ClienteService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CadastroDialog extends JDialog {
    
    private CadastroService cadastroService;
    private ClienteService clienteService;
    
    private JTextField txtNome;
    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private JPasswordField txtConfirmaSenha;
    private JTextField txtCpf;
    private JTextField txtDataNascimento;
    
    public CadastroDialog(JFrame parent, CadastroService cadastroService, ClienteService clienteService) {
        super(parent, "Cadastro de Novo Cliente", true);
        this.cadastroService = cadastroService;
        this.clienteService = clienteService;
        
        initComponents();
    }
    
    private void initComponents() {
        setSize(450, 450);
        setLocationRelativeTo(getParent());
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Título
        JLabel titleLabel = new JLabel("Criar Nova Conta");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(76, 175, 80));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Nome
        gbc.gridy = 1;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Nome completo:"), gbc);
        gbc.gridx = 1;
        txtNome = new JTextField(20);
        mainPanel.add(txtNome, gbc);
        
        // Email
        gbc.gridy = 2;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("E-mail:"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField(20);
        mainPanel.add(txtEmail, gbc);
        
        // Senha
        gbc.gridy = 3;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1;
        txtSenha = new JPasswordField(20);
        mainPanel.add(txtSenha, gbc);
        
        // Confirmar Senha
        gbc.gridy = 4;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Confirmar senha:"), gbc);
        gbc.gridx = 1;
        txtConfirmaSenha = new JPasswordField(20);
        mainPanel.add(txtConfirmaSenha, gbc);
        
        // CPF
        gbc.gridy = 5;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("CPF (11 dígitos):"), gbc);
        gbc.gridx = 1;
        txtCpf = new JTextField(20);
        mainPanel.add(txtCpf, gbc);
        
        // Data de Nascimento
        gbc.gridy = 6;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Data de Nascimento:"), gbc);
        gbc.gridx = 1;
        txtDataNascimento = new JTextField(20);
        JLabel lblFormato = new JLabel("(AAAA-MM-DD)");
        lblFormato.setFont(new Font("Arial", Font.ITALIC, 10));
        lblFormato.setForeground(Color.GRAY);
        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.setBackground(new Color(240, 240, 240));
        datePanel.add(txtDataNascimento, BorderLayout.CENTER);
        datePanel.add(lblFormato, BorderLayout.SOUTH);
        mainPanel.add(datePanel, gbc);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(240, 240, 240));
        
        JButton btnCadastrar = new JButton("Cadastrar");
        btnCadastrar.setBackground(new Color(76, 175, 80));
        btnCadastrar.setForeground(Color.WHITE);
        btnCadastrar.setFocusPainted(false);
        btnCadastrar.setBorderPainted(false);
        btnCadastrar.setOpaque(true);
        btnCadastrar.setPreferredSize(new Dimension(120, 35));
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(244, 67, 54));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorderPainted(false);
        btnCancelar.setOpaque(true);
        btnCancelar.setPreferredSize(new Dimension(120, 35));
        
        btnCadastrar.addActionListener(e -> realizarCadastro());
        btnCancelar.addActionListener(e -> dispose());
        
        buttonPanel.add(btnCadastrar);
        buttonPanel.add(btnCancelar);
        
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel);
    }
    
    private void realizarCadastro() {
        String nome = txtNome.getText().trim();
        String email = txtEmail.getText().trim();
        String senha = new String(txtSenha.getPassword());
        String confirmaSenha = new String(txtConfirmaSenha.getPassword());
        String cpf = txtCpf.getText().trim();
        String dataNascimentoStr = txtDataNascimento.getText().trim();
        
        // Validações
        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || cpf.isEmpty() || dataNascimentoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Todos os campos são obrigatórios!", 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!senha.equals(confirmaSenha)) {
            JOptionPane.showMessageDialog(this, 
                "As senhas não coincidem!", 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (cpf.length() != 11 || !cpf.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, 
                "CPF deve conter exatamente 11 dígitos!", 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        LocalDate dataNascimento;
        try {
            dataNascimento = LocalDate.parse(dataNascimentoStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, 
                "Data de nascimento inválida! Use o formato AAAA-MM-DD", 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Criar cliente
            Cliente novoCliente = clienteService.createCliente(nome, cpf, java.sql.Date.valueOf(dataNascimento));
            
            // Criar credenciais (plano padrão = 1)
            cadastroService.createCredenciais(novoCliente.getId(), email, senha);
            
            JOptionPane.showMessageDialog(this, 
                "Cadastro realizado com sucesso!\nFaça login para acessar o sistema.", 
                "Sucesso", 
                JOptionPane.INFORMATION_MESSAGE);
            
            dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao realizar cadastro: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
