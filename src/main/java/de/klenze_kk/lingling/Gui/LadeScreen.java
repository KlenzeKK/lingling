package de.klenze_kk.lingling.Gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class LadeScreen extends JPanel {
	BufferedImage[] loadingCircle = new BufferedImage[25];
	int rotation = 0;

	public LadeScreen() {
		try {
			for (int i = 2; i < loadingCircle.length; i++) loadingCircle[i-1] = ImageIO.read(new File("source/loading/loading" + (i) + ".png"));
			loadingCircle[24] = ImageIO.read(new File("source/loading/Loading1.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		Timer animation = new Timer();
		animation.scheduleAtFixedRate(new TimerTask() {

			@Override

			public void run() {
					if (rotation % 24 == 0) rotation = 1;
					 else 
						rotation++;	 
			}
		}, 0, 40);

	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.black);
		g.fillRect(0, 0, 800, 600);
		g.drawImage(loadingCircle[rotation], 650, 450, 100, 100, null);
		g.setColor(Color.white);
		g.drawString("laedt...", 690, 550);
		repaint();
	}

		

			
		}

