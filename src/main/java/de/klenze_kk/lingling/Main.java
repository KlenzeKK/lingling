package de.klenze_kk.jingling;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.klenze_kk.jingling.Gui.*;

public class Main {
	private static final Logger LOGGER = Logger.getLogger("[Lingling-System]");
	private static JFrame WINDOW;
	private static JPanel PANEL;
	private static JPanel currentDisplay;
	
	public static void main(String[] args) {
		initWin();

	}

	private static void initWin() {
		WINDOW = new JFrame("你 行!");
		WINDOW.setSize(800, 600);
		WINDOW.setResizable(false);
		WINDOW.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		WINDOW.setLocationRelativeTo(null);
		PANEL = new LogScreen();

		WINDOW.add(PANEL);
		WINDOW.setVisible(true);
	}

	public static void setJPanel(JPanel p) {
		WINDOW.remove(PANEL);
		PANEL = p;
		WINDOW.add(PANEL);
	}
	
	 public static void log(Level logLevel, String message) {
	        synchronized (LOGGER) {
	            LOGGER.log(logLevel, message);
	        }
	    }
	 
	   public static void log(Level logLevel, String message, Throwable error) {
	        synchronized (LOGGER) {
	            LOGGER.log(logLevel, message, error);
	        }
	    }

}
