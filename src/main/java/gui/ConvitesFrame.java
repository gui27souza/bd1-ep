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
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ConvitesFrame extends JFrame {
    
    private MainFrame mainFrame;
    private Acesso acessoAtual;
    
    private ConviteService conviteService;
    private GrupoService grupoService;
    private ClienteService clienteService;
    
    private JPanel centerPanel;
    
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
        
        // configuração da janela
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
        
        // painel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 240, 240));
        
        // título
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(38, 198, 218));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel titleLabel = new JLabel("Convites");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // área de conteúdo
        centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        exibirInstrucoes();
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // botões de ação
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
        btnEnviarConvite.setBackground(new Color(77, 182, 172));
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
        
        // listeners
        btnVerConvites.addActionListener(e -> verConvitesRecebidos());
        btnEnviarConvite.addActionListener(e -> enviarConvite());
        btnVoltar.addActionListener(e -> voltar());
        
        buttonPanel.add(btnVerConvites);
        buttonPanel.add(btnEnviarConvite);
        buttonPanel.add(btnVoltar);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }
    
    private void exibirInstrucoes() {
        // preparar painel
        centerPanel.removeAll();
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        
        // mensagem de instrução
        JLabel instrucaoLabel = new JLabel("<html><center>" +
            "<h2>Gerenciar Convites</h2>" +
            "<p>Visualize convites recebidos ou envie novos convites para membros.</p>" +
            "</center></html>");
        instrucaoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // status de convites
        JLabel convitesInfoLabel = new JLabel();
        convitesInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        convitesInfoLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        try {
            ConviteService.ConvitesStatus status = conviteService.getConvitesStatus(acessoAtual.getCliente().getId());
            
            if (status.limite == -1) {
                convitesInfoLabel.setText("Convites Ilimitados neste mês");
                convitesInfoLabel.setForeground(new Color(76, 175, 80));
            } else if (status.limite == 0) {
                convitesInfoLabel.setText("Seu plano não permite enviar convites");
                convitesInfoLabel.setForeground(Color.RED);
            } else {
                String texto = String.format("Convites: %d enviados / %d disponíveis (%d restantes)", 
                    status.enviados, status.limite, status.disponiveis);
                convitesInfoLabel.setText(texto);
                convitesInfoLabel.setForeground(status.disponiveis > 0 ? new Color(33, 150, 243) : Color.RED);
            }
        } catch (Exception e) {
            convitesInfoLabel.setText("Erro ao carregar status de convites");
            convitesInfoLabel.setForeground(Color.RED);
        }
        
        JPanel centerContentPanel = new JPanel();
        centerContentPanel.setLayout(new BoxLayout(centerContentPanel, BoxLayout.Y_AXIS));
        centerContentPanel.setBackground(Color.WHITE);
        centerContentPanel.add(instrucaoLabel);
        centerContentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerContentPanel.add(convitesInfoLabel);
        
        centerPanel.add(centerContentPanel);
        centerPanel.revalidate();
        centerPanel.repaint();
    }
    
    private void verConvitesRecebidos() {
        try {
            
            // buscar convites pendentes
            ArrayList<ConviteService.ConviteInfo> convites = conviteService.listarConvitesPendentes(
                acessoAtual.getCliente().getId()
            );
            
            if (convites.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Você não tem convites pendentes.", 
                    "Convites", 
                    JOptionPane.INFORMATION_MESSAGE);
                exibirInstrucoes();
                return;
            }
            
            exibirConvites(convites);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao buscar convites: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exibirConvites(ArrayList<ConviteService.ConviteInfo> convites) {
        
        // preparar painel
        centerPanel.removeAll();
        centerPanel.setLayout(new BorderLayout(10, 10));
        centerPanel.setBackground(Color.WHITE);
        
        // título
        JLabel titleLabel = new JLabel("Convites Recebidos");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        centerPanel.add(titleLabel, BorderLayout.NORTH);
        
        // lista de convites
        DefaultListModel<String> listModel = new DefaultListModel<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (ConviteService.ConviteInfo conv : convites) {
            String dataFormatada = "";
            if (conv.dataCriacao != null) {
                dataFormatada = dateFormat.format(conv.dataCriacao);
            }
            listModel.addElement(String.format("[%s] Grupo: %s - Convidado por: %s", 
                dataFormatada, conv.nomeGrupo, conv.nomeRemetente));
        }
        
        JList<String> convitesList = new JList<>(listModel);
        convitesList.setFont(new Font("Arial", Font.PLAIN, 14));
        convitesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        convitesList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JScrollPane scrollPane = new JScrollPane(convitesList);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        ));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // botões de ação
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(Color.WHITE);
        
        JButton btnAceitar = new JButton("Aceitar");
        btnAceitar.setBackground(new Color(26, 188, 156));
        btnAceitar.setForeground(Color.WHITE);
        btnAceitar.setFocusPainted(false);
        btnAceitar.setFont(new Font("Arial", Font.BOLD, 13));
        
        JButton btnRecusar = new JButton("Recusar");
        btnRecusar.setBackground(new Color(244, 67, 54));
        btnRecusar.setForeground(Color.WHITE);
        btnRecusar.setFocusPainted(false);
        btnRecusar.setFont(new Font("Arial", Font.BOLD, 13));
        
        btnAceitar.addActionListener(e -> {
            
            // validação
            int idx = convitesList.getSelectedIndex();
            if (idx == -1) {
                JOptionPane.showMessageDialog(this, "Selecione um convite.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                conviteService.aceitarConvite(convites.get(idx).id, acessoAtual.getCliente().getId());
                ArrayList<Grupo> gruposAtualizados = grupoService.getGrupos(acessoAtual.getCliente());
                acessoAtual.setGrupos(gruposAtualizados);
                
                JOptionPane.showMessageDialog(this, 
                    "Convite aceito! Você agora é membro do grupo.", 
                    "Sucesso", 
                    JOptionPane.INFORMATION_MESSAGE);
                exibirInstrucoes();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Erro: " + ex.getMessage(), 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnRecusar.addActionListener(e -> {
            
            // validação
            int idx = convitesList.getSelectedIndex();
            if (idx == -1) {
                JOptionPane.showMessageDialog(this, "Selecione um convite.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                conviteService.recusarConvite(convites.get(idx).id, acessoAtual.getCliente().getId());
                
                JOptionPane.showMessageDialog(this, 
                    "Convite recusado.", 
                    "Sucesso", 
                    JOptionPane.INFORMATION_MESSAGE);
                exibirInstrucoes();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Erro: " + ex.getMessage(), 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnPanel.add(btnAceitar);
        btnPanel.add(btnRecusar);
        
        centerPanel.add(btnPanel, BorderLayout.SOUTH);
        centerPanel.revalidate();
        centerPanel.repaint();
    }
    
    private void enviarConvite() {
        
        // validar grupos
        ArrayList<Grupo> grupos = acessoAtual.getGrupos();
        
        if (grupos.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Você não pertence a nenhum grupo ainda.", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // seleção do grupo
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
        
        // buscar grupo selecionado
        Grupo grupoSelecionado = null;
        for (Grupo g : grupos) {
            if (g.getNome().equals(escolha)) {
                grupoSelecionado = g;
                break;
            }
        }
        
        if (grupoSelecionado == null) return;
        
        // solicitar cpf do destinatário
        String cpf = JOptionPane.showInputDialog(this, 
            "Digite o CPF do cliente (11 dígitos):", 
            "CPF do Destinatário", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (cpf == null || cpf.trim().isEmpty()) return;
        
        // enviar convite
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
