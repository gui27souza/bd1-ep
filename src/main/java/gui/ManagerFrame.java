package main.java.gui;

import main.java.db.DBConnector;
import main.java.model.Cliente;
import main.java.service.ClienteService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;

/**
 * Interface gráfica para gerenciamento administrativo do banco de dados
 */
public class ManagerFrame extends JFrame {

    private DBConnector dbConnector;
    private ClienteService clienteService;
    
    private JPanel painelPrincipal;
    private JTextArea areaResultados;
    private JComboBox<String> comboTabelas;

    public ManagerFrame(DBConnector dbConnector, ClienteService clienteService) {
        this.dbConnector = dbConnector;
        this.clienteService = clienteService;
        
        initComponents();
    }

    private void initComponents() {
        setTitle("Manager App - Administração do Sistema");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        painelPrincipal.setBackground(Color.WHITE);
        
        // Painel superior com título
        JPanel painelTitulo = new JPanel();
        painelTitulo.setBackground(new Color(33, 150, 243));   // BTN_PRIMARY
        JLabel lblTitulo = new JLabel("Gerenciamento Administrativo");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        painelTitulo.add(lblTitulo);
        
        // Painel de controles
        JPanel painelControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        painelControles.setBackground(Color.WHITE);
        
        // ComboBox de tabelas
        JLabel lblTabela = new JLabel("Tabela:");
        lblTabela.setForeground(Color.BLACK);
        comboTabelas = new JComboBox<>();
        comboTabelas.setPreferredSize(new Dimension(200, 30));
        comboTabelas.setBackground(Color.WHITE);
        carregarNomesTabelas();
        
        // Botões
        JButton btnVerTabela = UIHelper.createButton("Ver Tabela", new Color(33, 150, 243), 120, 35);    // BTN_PRIMARY
        btnVerTabela.addActionListener(e -> visualizarTabela());
        
        JButton btnVerClientes = UIHelper.createButton("Ver Clientes", new Color(41, 182, 246), 120, 35); // BTN_SECONDARY
        btnVerClientes.addActionListener(e -> visualizarClientes());
        
        JButton btnListarTabelas = UIHelper.createButton("Listar Tabelas", new Color(38, 198, 218), 130, 35); // BTN_SUCCESS
        btnListarTabelas.addActionListener(e -> listarTabelas());
        
        JButton btnLimpar = UIHelper.createButton("Limpar", new Color(158, 158, 158), 100, 35);        // BTN_NEUTRAL
        btnLimpar.addActionListener(e -> areaResultados.setText(""));
        
        painelControles.add(lblTabela);
        painelControles.add(comboTabelas);
        painelControles.add(btnVerTabela);
        painelControles.add(btnVerClientes);
        painelControles.add(btnListarTabelas);
        painelControles.add(btnLimpar);
        
        // Área de resultados
        areaResultados = new JTextArea();
        areaResultados.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaResultados.setEditable(false);
        areaResultados.setBackground(Color.WHITE);
        areaResultados.setForeground(Color.BLACK);
        JScrollPane scrollResultados = new JScrollPane(areaResultados);
        scrollResultados.setBorder(BorderFactory.createLineBorder(new Color(158, 158, 158))); // BTN_NEUTRAL
        
        // Montagem
        painelPrincipal.add(painelTitulo, BorderLayout.NORTH);
        painelPrincipal.add(painelControles, BorderLayout.CENTER);
        painelPrincipal.add(scrollResultados, BorderLayout.SOUTH);
        
        // Configurar área de resultados para ocupar maior parte da tela
        scrollResultados.setPreferredSize(new Dimension(900, 500));
        
        setContentPane(painelPrincipal);
    }

    private void carregarNomesTabelas() {
        try {
            ArrayList<String> tabelas = dbConnector.getAvailableTables();
            for (String tabela : tabelas) {
                comboTabelas.addItem(tabela);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao carregar tabelas: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void visualizarTabela() {
        String tabelaSelecionada = (String) comboTabelas.getSelectedItem();
        if (tabelaSelecionada == null || tabelaSelecionada.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Selecione uma tabela primeiro.",
                "Aviso",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            ResultSet rs = dbConnector.queryTable(tabelaSelecionada);
            String resultado = formatarResultSet(rs, tabelaSelecionada);
            areaResultados.setText(resultado);
        } catch (Exception e) {
            areaResultados.setText("Erro ao consultar tabela: " + e.getMessage());
        }
    }

    private void visualizarClientes() {
        try {
            ArrayList<Cliente> clientes = clienteService.findAll();
            
            if (clientes.isEmpty()) {
                areaResultados.setText("Nenhum cliente encontrado no sistema.");
                return;
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("========================================\n");
            sb.append("       LISTA DE CLIENTES CADASTRADOS       \n");
            sb.append("========================================\n\n");
            
            for (Cliente cliente : clientes) {
                sb.append("ID: ").append(cliente.getId()).append("\n");
                sb.append("Nome: ").append(cliente.getNome()).append("\n");
                sb.append("CPF: ").append(cliente.getCpf()).append("\n");
                sb.append("Data Nascimento: ").append(cliente.getDataNascimento()).append("\n");
                sb.append("ID Plano: ").append(cliente.getIdPlano()).append("\n");
                sb.append("----------------------------------------\n");
            }
            
            sb.append("\nTotal: ").append(clientes.size()).append(" cliente(s)\n");
            
            areaResultados.setText(sb.toString());
            
        } catch (Exception e) {
            areaResultados.setText("Erro ao buscar clientes: " + e.getMessage());
        }
    }

    private void listarTabelas() {
        try {
            ArrayList<String> tabelas = dbConnector.getAvailableTables();
            
            StringBuilder sb = new StringBuilder();
            sb.append("========================================\n");
            sb.append("       TABELAS DISPONÍVEIS NO BD       \n");
            sb.append("========================================\n\n");
            
            for (int i = 0; i < tabelas.size(); i++) {
                sb.append(String.format("%2d. %s\n", i + 1, tabelas.get(i)));
            }
            
            sb.append("\nTotal: ").append(tabelas.size()).append(" tabela(s)\n");
            
            areaResultados.setText(sb.toString());
            
        } catch (Exception e) {
            areaResultados.setText("Erro ao listar tabelas: " + e.getMessage());
        }
    }

    private String formatarResultSet(ResultSet rs, String nomeTabela) {
        StringBuilder sb = new StringBuilder();
        
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            // Cabeçalho
            sb.append("========================================\n");
            sb.append("Tabela: ").append(nomeTabela.toUpperCase()).append("\n");
            sb.append("========================================\n\n");
            
            // Nomes das colunas
            ArrayList<String> columnNames = new ArrayList<>();
            ArrayList<Integer> columnWidths = new ArrayList<>();
            
            for (int i = 1; i <= columnCount; i++) {
                String colName = metaData.getColumnName(i);
                columnNames.add(colName);
                columnWidths.add(Math.max(colName.length(), 15));
            }
            
            // Linha de cabeçalho
            for (int i = 0; i < columnCount; i++) {
                sb.append(String.format("%-" + columnWidths.get(i) + "s  ", columnNames.get(i)));
            }
            sb.append("\n");
            
            // Linha separadora
            for (int i = 0; i < columnCount; i++) {
                for (int j = 0; j < columnWidths.get(i) + 2; j++) {
                    sb.append("-");
                }
            }
            sb.append("\n");
            
            // Dados
            int rowCount = 0;
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String value = rs.getString(i);
                    if (value == null) {
                        value = "NULL";
                    }
                    sb.append(String.format("%-" + columnWidths.get(i - 1) + "s  ", value));
                }
                sb.append("\n");
                rowCount++;
            }
            
            sb.append("\n");
            for (int i = 0; i < columnCount; i++) {
                for (int j = 0; j < columnWidths.get(i) + 2; j++) {
                    sb.append("=");
                }
            }
            sb.append("\n");
            sb.append("Total de registros: ").append(rowCount).append("\n");
            
        } catch (Exception e) {
            sb.append("Erro ao formatar resultado: ").append(e.getMessage());
        }
        
        return sb.toString();
    }
}
