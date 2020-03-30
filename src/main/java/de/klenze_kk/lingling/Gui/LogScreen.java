package de.klenze_kk.jingling.Gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class LogScreen extends JPanel implements ActionListener{
	
	private JLabel username, passwort;
	private JTextField userText, passText;
	private JButton logIn;
	
	public LogScreen() {
		this.setLayout(new GridLayout(5,5));
		username = new JLabel("Nutzername: ");
		username.setVisible(true);
		
		userText = new JTextField();
		userText.setVisible(true);
		
		passwort = new JLabel("Passwort: ");
		passwort.setVisible(true);
		
		passText = new JTextField();
		passText.setVisible(true);
		
		logIn = new JButton("Log-In");
		logIn.addActionListener(this);
		logIn.setVisible(true);
		
		this.add(username);
		this.add(userText);
		this.add(passwort);
		this.add(passText);
		this.add(logIn);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	
		
	}
	
	
	
	
}
