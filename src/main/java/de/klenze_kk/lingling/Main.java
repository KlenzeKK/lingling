package de.klenze_kk.lingling;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.klenze_kk.lingling.Gui.*;
import de.klenze_kk.lingling.logic.*;

public class Main {

	private static JFrame WINDOW;
	private static JPanel panel;
	private static DatabaseManager DATABASE_MANAGER;
	private static User USER;
        public static final VocabularyManager VOCABULARY = new VocabularyManager();

	public static void main(String[] args) {
		initWin();
		DATABASE_MANAGER = new DatabaseManager(null, (short) 0, null, null, null);
	}
        

	public static void  setJFrameSize(int x, int y) {
		WINDOW.setSize(x, y);
	}
	
	private static void initWin() {
		WINDOW = new JFrame("你 行!");
		WINDOW.setSize(800, 601);
		WINDOW.setResizable(false);
		WINDOW.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		WINDOW.setLocationRelativeTo(null);
		panel = new LogScreen2();

		WINDOW.add(panel);
		WINDOW.setVisible(true);
	}

	public static synchronized void setJPanel(JPanel p) {
		panel.setVisible(false);
		WINDOW.remove(panel);
		panel = p;
		WINDOW.add(panel);
		panel.setVisible(true);
	}
        
    public static JPanel getPanel(){
        return panel;
    }

	public static DatabaseManager getDatabaseManager() {
		return DATABASE_MANAGER;
	}

	public static synchronized void handleError(String message, String fensterName, boolean closeProgram) {
		JFrame jf = new JFrame(fensterName);
		jf.setSize(500, 100);
                jf.setBackground(Color.white);
		jf.setDefaultCloseOperation(closeProgram ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);
		if (closeProgram)
			WINDOW.setVisible(false);
		jf.setResizable(false);

		JLabel jl = new JLabel((closeProgram ? "Program crashed: " : "Error occurred: ") + message,
				SwingConstants.CENTER);
		jf.add(jl);
		jf.setLocationRelativeTo(null);
		jl.setVisible(true);
		jf.setVisible(true);

	}
	
	public static void handleError(String message, boolean closeProgram) {
		handleError(message, "Error", closeProgram);
	}

	public static synchronized User getUser() {
		return USER;
	}

	public static synchronized void setUser(User user) {
		if(USER!=null) throw new IllegalStateException("User bereits belegt");
		USER = user;
	}
        public static void logOut(){
            USER =null;
            VOCABULARY.logOut();
            setJPanel(new LogScreen2());
        }

}
