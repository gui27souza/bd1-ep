package main.java.gui;

import main.java.model.Acesso;
import main.java.model.Grupo;
import main.java.service.GrupoService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class GruposFrame extends JFrame {
    
    private MainFrame mainFrame;
    private Acesso acessoAtual;
    private GrupoService grupoService;
    private DefaultListModel<String> listModel;
    private JList<String> gruposList;
    private ArrayList<Grupo> grupos;
    
    public GruposFrame(MainFrame mainFrame, Acesso acessoAtual, GrupoService grupoService) {
        this.mainFrame = mainFrame;
        this.acessoAtual = acessoAtual;
        this.grupoService = grupoService;
        
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
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Painel principal
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
        
        // Lista de grupos
        listModel = new DefaultListModel<>();
        gruposList = new JList<>(listModel);
        gruposList.setFont(new Font("Arial", Font.PLAIN, 14));
        gruposList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        gruposList.setBackground(Color.WHITE);
        gruposList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(gruposList);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton btnVerDetalhes = new JButton("Ver Detalhes");
        btnVerDetalhes.setFont(new Font("Arial", Font.BOLD, 14));
        btnVerDetalhes.setBackground(new Color(33, 150, 243));
        btnVerDetalhes.setForeground(Color.WHITE);
        btnVerDetalhes.setFocusPainted(false);
        btnVerDetalhes.setBorderPainted(false);
        btnVerDetalhes.setOpaque(true);
        btnVerDetalhes.setPreferredSize(new Dimension(160, 45));
        
        JButton btnCriarGrupo = new JButton("Criar Novo Grupo");
        btnCriarGrupo.setFont(new Font("Arial", Font.BOLD, 14));
        btnCriarGrupo.setBackground(new Color(76, 175, 80));
        btnCriarGrupo.setForeground(Color.WHITE);
        btnCriarGrupo.setFocusPainted(false);
        btnCriarGrupo.setBorderPainted(false);
        btnCriarGrupo.setOpaque(true);
        btnCriarGrupo.setPreferredSize(new Dimension(180, 45));
        
        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.setFont(new Font("Arial", Font.BOLD, 14));
        btnVoltar.setBackground(new Color(158, 158, 158));
        btnVoltar.setForeground(Color.WHITE);
        btnVoltar.setFocusPainted(false);
        btnVoltar.setBorderPainted(false);
        btnVoltar.setOpaque(true);
        btnVoltar.setPreferredSize(new Dimension(120, 45));
        
        btnVerDetalhes.addActionListener(e -> verDetalhesGrupo());
        btnCriarGrupo.addActionListener(e -> criarNovoGrupo());
        btnVoltar.addActionListener(e -> voltar());
        
        buttonPanel.add(btnVerDetalhes);
        buttonPanel.add(btnCriarGrupo);
        buttonPanel.add(btnVoltar);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
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
        
        Grupo grupo = grupos.get(selectedIndex);
        
        String detalhes = String.format(
            "<html>" +
            "<h2>%s</h2>" +
            "<p><b>Descrição:</b> %s</p>" +
            "<p><b>Status:</b> %s</p>" +
            "<p><b>Data de Criação:</b> %s</p>" +
            "</html>",
            grupo.getNome(),
            grupo.getDescricao() != null && !grupo.getDescricao().isEmpty() ? grupo.getDescricao() : "Sem descrição",
            grupo.getStatus().toUpperCase(),
            grupo.getDataCriacao()
        );
        
        JOptionPane.showMessageDialog(this, 
            detalhes, 
            "Detalhes do Grupo", 
            JOptionPane.INFORMATION_MESSAGE);
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
