package de.klenze_kk.lingling;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.klenze_kk.lingling.Gui.*;

public class Main {
	
	private static JFrame WINDOW;
	private static JPanel panel;
	private static DatabaseManager DATABASE_MANAGER;
	
	
	public static void main(String[] args) {
		initWin();
		DATABASE_MANAGER = new DatabaseManager(null, (short) 0, null, null, null);
		handleError("DU BLÖDIAN DEIN PROGRAM IST KAPUTT", true);
	}

	private static void initWin() {
		WINDOW = new JFrame("你 行!");
		WINDOW.setSize(800, 600);
		WINDOW.setResizable(false);
		WINDOW.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		WINDOW.setLocationRelativeTo(null);
		panel = new LogScreen();

		WINDOW.add(panel);
		WINDOW.setVisible(true);
	}

	public static synchronized void setJPanel(JPanel p) {
		panel.setVisible(false);
		WINDOW.remove(panel);
		panel = p;
		panel.setVisible(true);
		WINDOW.add(panel);
	}
	
	  
	   public static DatabaseManager getDatabaseManager() {
			return DATABASE_MANAGER;
		}
	   
	   public static void handleError(String message, boolean closeProgram) {
		   JFrame jf = new JFrame("Error");
		   jf.setSize(500, 100);
		   jf.setDefaultCloseOperation(closeProgram ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);
		   if(closeProgram) WINDOW.setVisible(false); 
		   jf.setResizable(false);
		   
		   JLabel jl = new JLabel((closeProgram ? "Program crashed: " : "Error occurred: ")+message,SwingConstants.CENTER);
		   jf.add(jl);
		   jf.setLocationRelativeTo(null);
		   jl.setVisible(true); 
		   jf.setVisible(true);  
		   
	   }
	   
}
