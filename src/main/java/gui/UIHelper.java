package main.java.gui;

import javax.swing.*;
import java.awt.*;

public class UIHelper {
    
    // Cores gerais
    public static final Color BACKGROUND = new Color(240, 240, 240);
    public static final Color TEXT_BLACK = Color.BLACK;
    public static final Color TEXT_WHITE = Color.WHITE;
    public static final Color TEXT_GRAY = new Color(100, 100, 100);
    public static final Color FIELD_BACKGROUND = Color.WHITE;
    
    // Cores de botões
    public static final Color BTN_PRIMARY = new Color(33, 150, 243);
    public static final Color BTN_SECONDARY = new Color(41, 182, 246);
    public static final Color BTN_SUCCESS = new Color(38, 198, 218);
    public static final Color BTN_INFO = new Color(26, 188, 156);
    public static final Color BTN_LIGHT = new Color(77, 182, 172);
    public static final Color BTN_DANGER = new Color(244, 67, 54);
    public static final Color BTN_NEUTRAL = new Color(158, 158, 158);
    
    public static JButton createButton(String text, Color backgroundColor, int width, int height) {
        JButton button = new JButton(text);
        
        // Configuração visual básica
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(TEXT_WHITE);
        
        // Configuração de aparência
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Configuração de tamanho
        Dimension size = new Dimension(width, height);
        button.setPreferredSize(size);
        button.setMinimumSize(size);
        button.setMaximumSize(size);
        
        // Adicionar efeito hover
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
    
    public static JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND);
        return panel;
    }
    
    public static Color adjustBrightness(Color color, float factor) {
        int r = (int)(color.getRed() * factor);
        int g = (int)(color.getGreen() * factor);
        int b = (int)(color.getBlue() * factor);
        
        return new Color(Math.max(0, r), Math.max(0, g), Math.max(0, b));
    }
    
    public static void configureList(JList<?> list) {
        // Cores básicas
        list.setBackground(FIELD_BACKGROUND);
        list.setForeground(TEXT_BLACK);
        
        // Cores de seleção
        list.setSelectionBackground(BTN_PRIMARY);
        list.setSelectionForeground(TEXT_WHITE);
    }
    
    public static void configureTable(JTable table) {
        // Cores básicas
        table.setBackground(FIELD_BACKGROUND);
        table.setForeground(TEXT_BLACK);
        
        // Cores de seleção e grid
        table.setSelectionBackground(BTN_PRIMARY);
        table.setSelectionForeground(TEXT_WHITE);
        table.setGridColor(new Color(200, 200, 200));
        
        // Configuração do cabeçalho
        table.getTableHeader().setBackground(new Color(220, 220, 220));
        table.getTableHeader().setForeground(TEXT_BLACK);
    }
}
