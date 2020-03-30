package de.klenze_kk.jingling;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.klenze_kk.jingling.Gui.*;

public class Main {
	static JFrame WINDOW;
	static JPanel PANEL;
	public static void main(String[] args) {
		initWin();
		
	}

	private static void initWin() {
		WINDOW = new JFrame("你 行!");
		WINDOW.setSize(800,600);
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
	
}
