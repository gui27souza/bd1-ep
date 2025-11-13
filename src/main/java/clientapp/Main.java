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
		
		// Configurar look and feel do sistema
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// Usar look and feel padrão se falhar
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
