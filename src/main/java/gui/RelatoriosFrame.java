package main.java.gui;

import main.java.model.Acesso;
import main.java.service.RelatorioService;

import javax.swing.*;
import java.awt.*;

public class RelatoriosFrame extends JFrame {
    
    private MainFrame mainFrame;
    private Acesso acessoAtual;
    private RelatorioService relatorioService;
    
    public RelatoriosFrame(MainFrame mainFrame, Acesso acessoAtual, RelatorioService relatorioService) {
        this.mainFrame = mainFrame;
        this.acessoAtual = acessoAtual;
        this.relatorioService = relatorioService;
        
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Relatórios e Consultas");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                voltar();
            }
        });
        setSize(850, 700);
        setLocationRelativeTo(null);
        setResizable(true);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 240, 240));
        
        // Painel de título
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(233, 30, 99));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("📊 Relatórios e Consultas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Painel central com botões de relatórios
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel infoLabel = new JLabel("Selecione um relatório para visualizar:");
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(infoLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Botões dos relatórios
        JButton btn1 = createReportButton("1. Clientes Acima da Média de Transações", new Color(33, 150, 243));
        JButton btn2 = createReportButton("2. Grupos com Mais Membros", new Color(76, 175, 80));
        JButton btn3 = createReportButton("3. Total por Categoria de Transação", new Color(255, 152, 0));
        JButton btn4 = createReportButton("4. Estatísticas dos Grupos", new Color(156, 39, 176));
        JButton btn5 = createReportButton("5. Clientes Admin vs Membros (UNION)", new Color(0, 150, 136));
        JButton btn6 = createReportButton("6. Clientes com PIX e Cartão (INTERSECT)", new Color(233, 30, 99));
        
        btn1.addActionListener(e -> exibirRelatorio1());
        btn2.addActionListener(e -> exibirRelatorio2());
        btn3.addActionListener(e -> exibirRelatorio3());
        btn4.addActionListener(e -> exibirRelatorio4());
        btn5.addActionListener(e -> exibirRelatorio5());
        btn6.addActionListener(e -> exibirRelatorio6());
        
        centerPanel.add(btn1);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(btn2);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(btn3);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(btn4);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(btn5);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(btn6);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Botão voltar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(240, 240, 240));
        
        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.setFont(new Font("Arial", Font.BOLD, 13));
        btnVoltar.setBackground(new Color(158, 158, 158));
        btnVoltar.setForeground(Color.WHITE);
        btnVoltar.setFocusPainted(false);
        btnVoltar.setPreferredSize(new Dimension(100, 40));
        btnVoltar.addActionListener(e -> voltar());
        
        buttonPanel.add(btnVoltar);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JButton createReportButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(650, 45));
        button.setMinimumSize(new Dimension(650, 45));
        button.setPreferredSize(new Dimension(650, 45));
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        return button;
    }
    
    private void exibirRelatorio1() {
        relatorioService.clientesAcimaDaMedia(acessoAtual.getCliente().getId());
        
        JOptionPane.showMessageDialog(this,
            "Relatório exibido no console.",
            "Clientes Acima da Média",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void exibirRelatorio2() {
        relatorioService.gruposComMaisMembros(acessoAtual.getCliente().getId());
        
        JOptionPane.showMessageDialog(this,
            "Relatório exibido no console.",
            "Grupos com Mais Membros",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void exibirRelatorio3() {
        relatorioService.totalPorCategoria(acessoAtual.getCliente().getId());
        
        JOptionPane.showMessageDialog(this,
            "Relatório exibido no console.",
            "Total por Categoria",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void exibirRelatorio4() {
        relatorioService.estatisticasGrupos(acessoAtual.getCliente().getId());
        
        JOptionPane.showMessageDialog(this,
            "Relatório exibido no console.",
            "Estatísticas dos Grupos",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void exibirRelatorio5() {
        relatorioService.clientesAdminVsMembros(acessoAtual.getCliente().getId());
        
        JOptionPane.showMessageDialog(this,
            "Relatório exibido no console.",
            "Clientes Admin vs Membros",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void exibirRelatorio6() {
        relatorioService.clientesPixECartao(acessoAtual.getCliente().getId());
        
        JOptionPane.showMessageDialog(this,
            "Relatório exibido no console.",
            "Clientes com PIX e Cartão",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void voltar() {
        mainFrame.setVisible(true);
        this.dispose();
    }
}
