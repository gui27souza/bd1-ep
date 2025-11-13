package main.java.gui;

import javax.swing.*;
import java.awt.*;

// Classe com cores e funções básicas para UI
public class UIHelper {
    
    // Cores padrão
    public static final Color BACKGROUND = new Color(240, 240, 240);
    public static final Color TEXT_BLACK = Color.BLACK;
    public static final Color TEXT_WHITE = Color.WHITE;
    public static final Color TEXT_GRAY = new Color(100, 100, 100);
    public static final Color FIELD_BACKGROUND = Color.WHITE;
    
    // Paleta de gradiente Azul → Verde-Água
    public static final Color BTN_PRIMARY = new Color(33, 150, 243);
    public static final Color BTN_SECONDARY = new Color(41, 182, 246);
    public static final Color BTN_SUCCESS = new Color(38, 198, 218);
    public static final Color BTN_INFO = new Color(26, 188, 156);
    public static final Color BTN_LIGHT = new Color(77, 182, 172);
    public static final Color BTN_DANGER = new Color(244, 67, 54);
    public static final Color BTN_NEUTRAL = new Color(158, 158, 158);
    
    // Cria um botão customizado
    public static JButton createButton(String text, Color backgroundColor, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(TEXT_WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        Dimension size = new Dimension(width, height);
        button.setPreferredSize(size);
        button.setMinimumSize(size);
        button.setMaximumSize(size);
        
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
    
    // Cria um painel com cor padrão
    public static JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND);
        return panel;
    }
    
    // Ajusta o brilho de uma cor
    public static Color adjustBrightness(Color color, float factor) {
        int r = (int)(color.getRed() * factor);
        int g = (int)(color.getGreen() * factor);
        int b = (int)(color.getBlue() * factor);
        return new Color(Math.max(0, r), Math.max(0, g), Math.max(0, b));
    }
    
    // Configura lista com cores
    public static void configureList(JList<?> list) {
        list.setBackground(FIELD_BACKGROUND);
        list.setForeground(TEXT_BLACK);
        list.setSelectionBackground(BTN_PRIMARY);
        list.setSelectionForeground(TEXT_WHITE);
    }
    
    // Configura tabela com cores
    public static void configureTable(JTable table) {
        table.setBackground(FIELD_BACKGROUND);
        table.setForeground(TEXT_BLACK);
        table.setSelectionBackground(BTN_PRIMARY);
        table.setSelectionForeground(TEXT_WHITE);
        table.setGridColor(new Color(200, 200, 200));
        table.getTableHeader().setBackground(new Color(220, 220, 220));
        table.getTableHeader().setForeground(TEXT_BLACK);
    }
}
