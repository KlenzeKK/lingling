package de.klenze_kk.jingling;

import javax.swing.JFrame;
import de.klenze_kk.jingling.Gui.*;

public class Main {
	static JFrame WINDOW;
	public static void main(String[] args) {
		initWin();
		
	}

	private static void initWin() {
		WINDOW = new JFrame("你 行!");
		WINDOW.setSize(800,600);
		WINDOW.setResizable(false);
		WINDOW.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		WINDOW.setLocationRelativeTo(null);
		LogScreen l = new LogScreen();
		
		
		WINDOW.add(l);
		WINDOW.setVisible(true);
	}
	
	
}
