package main.java.gui;

import main.java.model.Acesso;
import main.java.model.Cliente;
import main.java.model.Grupo;
import main.java.service.ClienteService;
import main.java.service.ConviteService;
import main.java.service.GrupoService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class ConvitesFrame extends JFrame {
    
    private MainFrame mainFrame;
    private Acesso acessoAtual;
    private ConviteService conviteService;
    private GrupoService grupoService;
    private ClienteService clienteService;
    
    public ConvitesFrame(MainFrame mainFrame, Acesso acessoAtual, ConviteService conviteService,
                        GrupoService grupoService, ClienteService clienteService) {
        this.mainFrame = mainFrame;
        this.acessoAtual = acessoAtual;
        this.conviteService = conviteService;
        this.grupoService = grupoService;
        this.clienteService = clienteService;
        
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Convites");
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
        titlePanel.setBackground(new Color(255, 152, 0));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("📨 Convites");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Painel central com instruções
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        JLabel instrucaoLabel = new JLabel("<html><center>" +
            "<h2>Gerenciar Convites</h2>" +
            "<p>Visualize convites recebidos ou envie novos convites para membros.</p>" +
            "</center></html>");
        instrucaoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(instrucaoLabel);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton btnVerConvites = new JButton("Ver Convites Recebidos");
        btnVerConvites.setFont(new Font("Arial", Font.BOLD, 14));
        btnVerConvites.setBackground(new Color(33, 150, 243));
        btnVerConvites.setForeground(Color.WHITE);
        btnVerConvites.setFocusPainted(false);
        btnVerConvites.setBorderPainted(false);
        btnVerConvites.setOpaque(true);
        btnVerConvites.setPreferredSize(new Dimension(220, 45));
        
        JButton btnEnviarConvite = new JButton("Enviar Convite");
        btnEnviarConvite.setFont(new Font("Arial", Font.BOLD, 14));
        btnEnviarConvite.setBackground(new Color(76, 175, 80));
        btnEnviarConvite.setForeground(Color.WHITE);
        btnEnviarConvite.setFocusPainted(false);
        btnEnviarConvite.setBorderPainted(false);
        btnEnviarConvite.setOpaque(true);
        btnEnviarConvite.setPreferredSize(new Dimension(180, 45));
        
        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.setFont(new Font("Arial", Font.BOLD, 14));
        btnVoltar.setBackground(new Color(158, 158, 158));
        btnVoltar.setForeground(Color.WHITE);
        btnVoltar.setFocusPainted(false);
        btnVoltar.setBorderPainted(false);
        btnVoltar.setOpaque(true);
        btnVoltar.setPreferredSize(new Dimension(120, 45));
        
        btnVerConvites.addActionListener(e -> verConvitesRecebidos());
        btnEnviarConvite.addActionListener(e -> enviarConvite());
        btnVoltar.addActionListener(e -> voltar());
        
        buttonPanel.add(btnVerConvites);
        buttonPanel.add(btnEnviarConvite);
        buttonPanel.add(btnVoltar);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void verConvitesRecebidos() {
        try {
            ArrayList<ConviteService.ConviteInfo> convites = conviteService.listarConvitesPendentes(
                acessoAtual.getCliente().getId()
            );
            
            if (convites.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Você não tem convites pendentes.", 
                    "Convites", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Criar dialog para mostrar convites
            JDialog dialog = new JDialog(this, "Convites Recebidos", true);
            dialog.setSize(600, 400);
            dialog.setLocationRelativeTo(this);
            
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            DefaultListModel<String> listModel = new DefaultListModel<>();
            for (ConviteService.ConviteInfo conv : convites) {
                listModel.addElement(String.format("Grupo: %s - Convidado por: %s", 
                    conv.nomeGrupo, conv.nomeRemetente));
            }
            
            JList<String> convitesList = new JList<>(listModel);
            convitesList.setFont(new Font("Arial", Font.PLAIN, 13));
            convitesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            
            JScrollPane scrollPane = new JScrollPane(convitesList);
            panel.add(scrollPane, BorderLayout.CENTER);
            
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            
            JButton btnAceitar = new JButton("Aceitar");
            btnAceitar.setBackground(new Color(76, 175, 80));
            btnAceitar.setForeground(Color.WHITE);
            btnAceitar.setFocusPainted(false);
            
            JButton btnRecusar = new JButton("Recusar");
            btnRecusar.setBackground(new Color(244, 67, 54));
            btnRecusar.setForeground(Color.WHITE);
            btnRecusar.setFocusPainted(false);
            
            JButton btnFechar = new JButton("Fechar");
            btnFechar.setBackground(new Color(158, 158, 158));
            btnFechar.setForeground(Color.WHITE);
            btnFechar.setFocusPainted(false);
            
            btnAceitar.addActionListener(e -> {
                int idx = convitesList.getSelectedIndex();
                if (idx == -1) {
                    JOptionPane.showMessageDialog(dialog, "Selecione um convite.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                try {
                    conviteService.aceitarConvite(convites.get(idx).id, acessoAtual.getCliente().getId());
                    
                    // Atualiza lista de grupos
                    ArrayList<Grupo> gruposAtualizados = grupoService.getGrupos(acessoAtual.getCliente());
                    acessoAtual.setGrupos(gruposAtualizados);
                    
                    JOptionPane.showMessageDialog(dialog, 
                        "Convite aceito! Você agora é membro do grupo.", 
                        "Sucesso", 
                        JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Erro: " + ex.getMessage(), 
                        "Erro", 
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            btnRecusar.addActionListener(e -> {
                int idx = convitesList.getSelectedIndex();
                if (idx == -1) {
                    JOptionPane.showMessageDialog(dialog, "Selecione um convite.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                try {
                    conviteService.recusarConvite(convites.get(idx).id, acessoAtual.getCliente().getId());
                    JOptionPane.showMessageDialog(dialog, 
                        "Convite recusado.", 
                        "Sucesso", 
                        JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Erro: " + ex.getMessage(), 
                        "Erro", 
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            btnFechar.addActionListener(e -> dialog.dispose());
            
            btnPanel.add(btnAceitar);
            btnPanel.add(btnRecusar);
            btnPanel.add(btnFechar);
            
            panel.add(btnPanel, BorderLayout.SOUTH);
            dialog.add(panel);
            dialog.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao buscar convites: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void enviarConvite() {
        ArrayList<Grupo> grupos = acessoAtual.getGrupos();
        
        if (grupos.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Você não pertence a nenhum grupo ainda.", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Escolher grupo
        String[] nomes = new String[grupos.size()];
        for (int i = 0; i < grupos.size(); i++) {
            nomes[i] = grupos.get(i).getNome();
        }
        
        String escolha = (String) JOptionPane.showInputDialog(
            this,
            "Selecione o grupo para enviar o convite:",
            "Escolher Grupo",
            JOptionPane.QUESTION_MESSAGE,
            null,
            nomes,
            nomes[0]
        );
        
        if (escolha == null) return;
        
        Grupo grupoSelecionado = null;
        for (Grupo g : grupos) {
            if (g.getNome().equals(escolha)) {
                grupoSelecionado = g;
                break;
            }
        }
        
        if (grupoSelecionado == null) return;
        
        // Pedir CPF
        String cpf = JOptionPane.showInputDialog(this, 
            "Digite o CPF do cliente (11 dígitos):", 
            "CPF do Destinatário", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (cpf == null || cpf.trim().isEmpty()) return;
        
        try {
            Cliente destinatario = clienteService.findByCpf(cpf.trim());
            
            if (destinatario == null) {
                JOptionPane.showMessageDialog(this, 
                    "Cliente não encontrado com este CPF.", 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            conviteService.enviarConvite(
                acessoAtual.getCliente().getId(),
                destinatario.getId(),
                grupoSelecionado.getId()
            );
            
            JOptionPane.showMessageDialog(this, 
                "Convite enviado com sucesso para " + destinatario.getNome() + "!", 
                "Sucesso", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao enviar convite: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void voltar() {
        mainFrame.setVisible(true);
        this.dispose();
    }
}
