package main.java.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Classe utilitária para garantir consistência visual cross-platform
 */
public class UIHelper {
    
    // Cores padrão
    public static final Color BACKGROUND = new Color(240, 240, 240);
    public static final Color TEXT_BLACK = Color.BLACK;
    public static final Color TEXT_WHITE = Color.WHITE;
    public static final Color TEXT_GRAY = new Color(100, 100, 100);
    public static final Color FIELD_BACKGROUND = Color.WHITE;
    
    // Cores dos botões
    public static final Color BLUE = new Color(33, 150, 243);
    public static final Color GREEN = new Color(76, 175, 80);
    public static final Color ORANGE = new Color(255, 152, 0);
    public static final Color PURPLE = new Color(156, 39, 176);
    public static final Color PINK = new Color(233, 30, 99);
    public static final Color GRAY = new Color(158, 158, 158);
    
    /**
     * Configura um label com cores explícitas para cross-platform
     */
    public static JLabel createLabel(String text, int fontSize, int style) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", style, fontSize));
        label.setForeground(TEXT_BLACK);
        return label;
    }
    
    /**
     * Cria um label de título colorido
     */
    public static JLabel createTitleLabel(String text, Color color, int fontSize) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, fontSize));
        label.setForeground(color);
        return label;
    }
    
    /**
     * Configura um campo de texto com cores explícitas
     */
    public static void configureTextField(JTextField field) {
        field.setBackground(FIELD_BACKGROUND);
        field.setForeground(TEXT_BLACK);
        field.setCaretColor(TEXT_BLACK);
    }
    
    /**
     * Configura um campo de senha com cores explícitas
     */
    public static void configurePasswordField(JPasswordField field) {
        field.setBackground(FIELD_BACKGROUND);
        field.setForeground(TEXT_BLACK);
        field.setCaretColor(TEXT_BLACK);
    }
    
    /**
     * Configura uma área de texto com cores explícitas
     */
    public static void configureTextArea(JTextArea area) {
        area.setBackground(FIELD_BACKGROUND);
        area.setForeground(TEXT_BLACK);
        area.setCaretColor(TEXT_BLACK);
    }
    
    /**
     * Configura uma lista com cores explícitas
     */
    public static void configureList(JList<?> list) {
        list.setBackground(FIELD_BACKGROUND);
        list.setForeground(TEXT_BLACK);
        list.setSelectionBackground(BLUE);
        list.setSelectionForeground(TEXT_WHITE);
    }
    
    /**
     * Configura uma tabela com cores explícitas
     */
    public static void configureTable(JTable table) {
        table.setBackground(FIELD_BACKGROUND);
        table.setForeground(TEXT_BLACK);
        table.setSelectionBackground(BLUE);
        table.setSelectionForeground(TEXT_WHITE);
        table.setGridColor(new Color(200, 200, 200));
        table.getTableHeader().setBackground(new Color(220, 220, 220));
        table.getTableHeader().setForeground(TEXT_BLACK);
    }
    
    /**
     * Cria um botão com cores cross-platform consistentes
     */
    public static JButton createButton(String text, Color backgroundColor, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(TEXT_WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        
        // Definir tamanhos mínimo, preferido e máximo
        Dimension size = new Dimension(width, height);
        button.setPreferredSize(size);
        button.setMinimumSize(size);
        button.setMaximumSize(size);
        
        // Garantir que o botão seja visível
        button.setVisible(true);
        
        // Efeito hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(adjustBrightness(backgroundColor, 0.9f));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
        
        return button;
    }
    
    /**
     * Cria um painel com background padrão
     */
    public static JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND);
        return panel;
    }
    
    /**
     * Ajusta o brilho de uma cor
     */
    private static Color adjustBrightness(Color color, float factor) {
        int r = (int)(color.getRed() * factor);
        int g = (int)(color.getGreen() * factor);
        int b = (int)(color.getBlue() * factor);
        return new Color(Math.max(0, r), Math.max(0, g), Math.max(0, b));
    }
    
    /**
     * Configura recursivamente todos os componentes de um container
     * para garantir cores explícitas
     */
    public static void ensureVisibility(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                // Só alterar se for cor padrão do sistema
                Color fg = label.getForeground();
                if (fg == null || isSystemColor(fg)) {
                    label.setForeground(TEXT_BLACK);
                }
            } else if (comp instanceof JButton) {
                // Não alterar botões - eles já têm cores customizadas
                // Apenas garantir que sejam opacos
                JButton btn = (JButton) comp;
                if (btn.getBackground() != null) {
                    btn.setOpaque(true);
                    btn.setBorderPainted(false);
                }
            } else if (comp instanceof JTextField) {
                configureTextField((JTextField) comp);
            } else if (comp instanceof JPasswordField) {
                configurePasswordField((JPasswordField) comp);
            } else if (comp instanceof JTextArea) {
                configureTextArea((JTextArea) comp);
            } else if (comp instanceof JList) {
                configureList((JList<?>) comp);
            } else if (comp instanceof JTable) {
                configureTable((JTable) comp);
            } else if (comp instanceof Container) {
                ensureVisibility((Container) comp);
            }
        }
    }
    
    /**
     * Verifica se uma cor é uma cor padrão do sistema
     */
    private static boolean isSystemColor(Color color) {
        return color.equals(UIManager.getColor("Label.foreground")) ||
               color.equals(UIManager.getColor("Button.foreground")) ||
               color.equals(UIManager.getColor("TextField.foreground"));
    }
}
