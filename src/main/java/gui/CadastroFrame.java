package main.java.gui;
import main.java.model.Acesso;
import main.java.service.CadastroService;
import main.java.service.ClienteService;
import main.java.service.PlanoService;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
public class CadastroFrame extends JFrame {
    private MainFrame mainFrame;
    private Acesso acessoAtual;
    private ClienteService clienteService;
    private CadastroService cadastroService;
    private PlanoService planoService;
    public CadastroFrame(MainFrame mainFrame, Acesso acessoAtual, ClienteService clienteService,
                        CadastroService cadastroService, PlanoService planoService) {
        this.mainFrame = mainFrame;
        this.acessoAtual = acessoAtual;
        this.clienteService = clienteService;
        this.cadastroService = cadastroService;
        this.planoService = planoService;
        initComponents();
    }

    private void initComponents() {
        setTitle("Meu Cadastro");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                voltar();
            }
        });
        setSize(800, 650);
        setLocationRelativeTo(null);
        setResizable(true);
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 240, 240));
        // Painel de título
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(77, 182, 172));     // BTN_LIGHT
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel titleLabel = new JLabel("👤 Meu Cadastro");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        // Painel central com opções
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        // Botões de ações
        JButton btnVerDados = createActionButton("Ver Meus Dados", new Color(33, 150, 243));      // BTN_PRIMARY
        JButton btnEditarNome = createActionButton("Editar Nome", new Color(41, 182, 246));       // BTN_SECONDARY
        JButton btnEditarEmail = createActionButton("Editar E-mail", new Color(38, 198, 218));    // BTN_SUCCESS
        JButton btnEditarCpf = createActionButton("Editar CPF", new Color(26, 188, 156));         // BTN_INFO
        JButton btnEditarData = createActionButton("Editar Data de Nascimento", new Color(77, 182, 172)); // BTN_LIGHT
        JButton btnTrocarPlano = createActionButton("Trocar Plano", new Color(33, 150, 243));     // BTN_PRIMARY
        btnVerDados.addActionListener(e -> verDados());
        btnEditarNome.addActionListener(e -> editarNome());
        btnEditarEmail.addActionListener(e -> editarEmail());
        btnEditarCpf.addActionListener(e -> editarCpf());
        btnEditarData.addActionListener(e -> editarDataNascimento());
        btnTrocarPlano.addActionListener(e -> trocarPlano());
        centerPanel.add(btnVerDados);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(btnEditarNome);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(btnEditarEmail);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(btnEditarCpf);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(btnEditarData);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(btnTrocarPlano);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        // Botão voltar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(240, 240, 240));
        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.setFont(new Font("Arial", Font.BOLD, 13));
        btnVoltar.setBackground(new Color(158, 158, 158));     // BTN_NEUTRAL
        btnVoltar.setForeground(Color.WHITE);
        btnVoltar.setFocusPainted(false);
        btnVoltar.setPreferredSize(new Dimension(100, 40));
        btnVoltar.addActionListener(e -> voltar());
        buttonPanel.add(btnVoltar);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(400, 40));
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Efeito hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(adjustBrightness(color, 0.9f));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        return button;
    }

    private Color adjustBrightness(Color color, float factor) {
        int r = (int)(color.getRed() * factor);
        int g = (int)(color.getGreen() * factor);
        int b = (int)(color.getBlue() * factor);
        return new Color(Math.max(0, r), Math.max(0, g), Math.max(0, b));
    }

    private void verDados() {
        try {
            // Buscar plano atual
            PlanoService.PlanoInfo plano = planoService.getPlanoAtual(acessoAtual.getCliente().getId());
            // Criar dialog customizado
            JDialog dialog = new JDialog(this, "Meus Dados", true);
            dialog.setSize(550, 450);
            dialog.setLocationRelativeTo(this);
            JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            mainPanel.setBackground(Color.WHITE);
            // Título
            JLabel titleLabel = new JLabel("👤 Meus Dados Pessoais");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            titleLabel.setForeground(new Color(156, 39, 176));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            mainPanel.add(titleLabel, BorderLayout.NORTH);
            // Painel de informações
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBackground(Color.WHITE);
            infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
            ));
            // Adicionar informações com formatação elegante
            addInfoRow(infoPanel, "Nome:", acessoAtual.getCliente().getNome());
            addInfoRow(infoPanel, "E-mail:", acessoAtual.getEmail());
            addInfoRow(infoPanel, "CPF:", acessoAtual.getCliente().getCpf());
            addInfoRow(infoPanel, "Data de Nascimento:", acessoAtual.getCliente().getDataNascimento().toString());
            infoPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            // Separador
            JSeparator separator = new JSeparator();
            separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            infoPanel.add(separator);
            infoPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            // Informações do plano com destaque
            JLabel planoTitleLabel = new JLabel("💳 Plano Atual");
            planoTitleLabel.setFont(new Font("Arial", Font.BOLD, 14));
            planoTitleLabel.setForeground(new Color(33, 150, 243));
            planoTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            infoPanel.add(planoTitleLabel);
            infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            addInfoRow(infoPanel, "Nome do Plano:", plano.nome);
            addInfoRow(infoPanel, "Valor Mensal:", String.format("R$ %.2f", plano.valor));
            addInfoRow(infoPanel, "Convites Disponíveis:", String.valueOf(plano.qtdConvites));
            mainPanel.add(infoPanel, BorderLayout.CENTER);
            // Botão fechar
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.setBackground(Color.WHITE);
            JButton btnFechar = new JButton("Fechar");
            btnFechar.setFont(new Font("Arial", Font.BOLD, 13));
            btnFechar.setBackground(new Color(33, 150, 243));  // BTN_PRIMARY
            btnFechar.setForeground(Color.WHITE);
            btnFechar.setFocusPainted(false);
            btnFechar.setPreferredSize(new Dimension(120, 35));
            btnFechar.addActionListener(e -> dialog.dispose());
            buttonPanel.add(btnFechar);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setContentPane(mainPanel);
            dialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao buscar dados: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addInfoRow(JPanel panel, String label, String value) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Arial", Font.BOLD, 13));
        lblLabel.setPreferredSize(new Dimension(180, 25));
        lblLabel.setForeground(new Color(80, 80, 80));
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.PLAIN, 13));
        lblValue.setForeground(new Color(50, 50, 50));
        rowPanel.add(lblLabel);
        rowPanel.add(lblValue);
        panel.add(rowPanel);
    }

    private void editarNome() {
        String novoNome = JOptionPane.showInputDialog(this, 
            "Digite o novo nome:", 
            acessoAtual.getCliente().getNome());
        if (novoNome != null && !novoNome.trim().isEmpty()) {
            try {
                clienteService.updateNome(acessoAtual.getCliente().getId(), novoNome.trim());
                acessoAtual.getCliente().setNome(novoNome.trim());
                JOptionPane.showMessageDialog(this, 
                    "Nome atualizado com sucesso!", 
                    "Sucesso", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao atualizar nome: " + e.getMessage(), 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarEmail() {
        String novoEmail = JOptionPane.showInputDialog(this, 
            "Digite o novo e-mail:", 
            acessoAtual.getEmail());
        if (novoEmail != null && !novoEmail.trim().isEmpty()) {
            try {
                cadastroService.updateEmail(acessoAtual.getId(), novoEmail.trim());
                acessoAtual.setEmail(novoEmail.trim());
                JOptionPane.showMessageDialog(this, 
                    "E-mail atualizado com sucesso!", 
                    "Sucesso", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao atualizar e-mail: " + e.getMessage(), 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarCpf() {
        String novoCpf = JOptionPane.showInputDialog(this, 
            "Digite o novo CPF (11 dígitos):", 
            acessoAtual.getCliente().getCpf());
        if (novoCpf != null && !novoCpf.trim().isEmpty()) {
            try {
                clienteService.updateCpf(acessoAtual.getCliente().getId(), novoCpf.trim());
                acessoAtual.getCliente().setCpf(novoCpf.trim());
                JOptionPane.showMessageDialog(this, 
                    "CPF atualizado com sucesso!", 
                    "Sucesso", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao atualizar CPF: " + e.getMessage(), 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarDataNascimento() {
        String novaData = JOptionPane.showInputDialog(this, 
            "Digite a nova data de nascimento (AAAA-MM-DD):", 
            acessoAtual.getCliente().getDataNascimento());
        if (novaData != null && !novaData.trim().isEmpty()) {
            try {
                LocalDate data = LocalDate.parse(novaData.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
                clienteService.updateDataNascimento(acessoAtual.getCliente().getId(), novaData.trim());
                acessoAtual.getCliente().setDataNascimento(java.sql.Date.valueOf(data));
                JOptionPane.showMessageDialog(this, 
                    "Data de nascimento atualizada com sucesso!", 
                    "Sucesso", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, 
                    "Formato de data inválido! Use AAAA-MM-DD", 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao atualizar data: " + e.getMessage(), 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void trocarPlano() {
        try {
            ArrayList<PlanoService.PlanoInfo> planos = planoService.listarPlanos();
            // Criar dialog para escolher plano
            JDialog dialog = new JDialog(this, "Trocar Plano", true);
            dialog.setSize(500, 400);
            dialog.setLocationRelativeTo(this);
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            DefaultListModel<String> listModel = new DefaultListModel<>();
            for (PlanoService.PlanoInfo plano : planos) {
                listModel.addElement(String.format("%s - R$ %.2f - %d convites", 
                    plano.nome, plano.valor, plano.qtdConvites));
            }
            JList<String> planosList = new JList<>(listModel);
            planosList.setFont(new Font("Arial", Font.PLAIN, 13));
            planosList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane scrollPane = new JScrollPane(planosList);
            panel.add(scrollPane, BorderLayout.CENTER);
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            JButton btnSelecionar = new JButton("Selecionar");
            btnSelecionar.setBackground(new Color(33, 150, 243)); // BTN_PRIMARY
            btnSelecionar.setForeground(Color.WHITE);
            btnSelecionar.setFocusPainted(false);
            JButton btnCancelar = new JButton("Cancelar");
            btnCancelar.setBackground(new Color(158, 158, 158));   // BTN_NEUTRAL
            btnCancelar.setForeground(Color.WHITE);
            btnCancelar.setFocusPainted(false);
            btnSelecionar.addActionListener(e -> {
                int idx = planosList.getSelectedIndex();
                if (idx == -1) {
                    JOptionPane.showMessageDialog(dialog, "Selecione um plano.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                PlanoService.PlanoInfo planoSelecionado = planos.get(idx);
                int confirm = JOptionPane.showConfirmDialog(dialog, 
                    String.format("Deseja trocar para o plano %s (R$ %.2f)?", 
                        planoSelecionado.nome, planoSelecionado.valor),
                    "Confirmar", 
                    JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        planoService.atualizarPlanoCliente(acessoAtual.getCliente().getId(), planoSelecionado.id);
                        JOptionPane.showMessageDialog(dialog, 
                            "Plano atualizado com sucesso!", 
                            "Sucesso", 
                            JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Erro ao atualizar plano: " + ex.getMessage(), 
                            "Erro", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            btnCancelar.addActionListener(e -> dialog.dispose());
            btnPanel.add(btnSelecionar);
            btnPanel.add(btnCancelar);
            panel.add(btnPanel, BorderLayout.SOUTH);
            dialog.add(panel);
            dialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao buscar planos: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void voltar() {
        mainFrame.setVisible(true);
        this.dispose();
    }
}
