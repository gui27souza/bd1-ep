package main.java.clientapp;

import main.java.db.DBConnector;
import main.java.gui.LoginFrame;
import main.java.service.CadastroService;
import main.java.service.ClienteService;
import main.java.service.GrupoService;

import javax.swing.*;
import java.sql.SQLException;

public class Main {

	public static void main(String[] args) {
		
		// Configurar look and feel cross-platform (Metal) para consistência entre sistemas
		try {
			// Usar Metal Look and Feel (padrão Java, funciona igual em todos os SOs)
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			
			// Configurações adicionais para garantir visibilidade dos componentes
			UIManager.put("Button.background", new java.awt.Color(238, 238, 238));
			UIManager.put("Panel.background", new java.awt.Color(240, 240, 240));
			UIManager.put("OptionPane.background", new java.awt.Color(240, 240, 240));
			UIManager.put("TextField.background", java.awt.Color.WHITE);
			UIManager.put("PasswordField.background", java.awt.Color.WHITE);
			UIManager.put("TextArea.background", java.awt.Color.WHITE);
			UIManager.put("List.background", java.awt.Color.WHITE);
			UIManager.put("Table.background", java.awt.Color.WHITE);
			
		} catch (Exception e) {
			System.err.println("Erro ao configurar Look and Feel: " + e.getMessage());
		}
		
		// Iniciar interface gráfica
		SwingUtilities.invokeLater(() -> {
			try {
				DBConnector dbConnector = new DBConnector();
				ClienteService clienteService = new ClienteService(dbConnector);
				GrupoService grupoService = new GrupoService(dbConnector);
				CadastroService cadastroService = new CadastroService(dbConnector, clienteService, grupoService);
				
				LoginFrame loginFrame = new LoginFrame(dbConnector, cadastroService, clienteService, grupoService);
				loginFrame.setVisible(true);
				
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, 
					"Erro ao conectar ao banco de dados:\n" + e.getMessage(), 
					"Erro Fatal", 
					JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
		});
	}
}
