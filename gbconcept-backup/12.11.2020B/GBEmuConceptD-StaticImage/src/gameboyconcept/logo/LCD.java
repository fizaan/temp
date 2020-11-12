package gameboyconcept.logo;

import static tests.TileDataAddressTest.spriteAddress;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JViewport;

import gameboyconcept.GBBus;
import gameboyconcept.GBCpuBootRom;
import gameboyconcept.gblogger.Logit;

public class LCD {
	
	/**
	 * 
	 */
	public static final int FRAME_RATE = 1 / 60 * 1000;
	public static final int SLOW_FRAME_RATE = 20;
	public static final int LEN =  256;
	public static final int HEI =  256;
	private BufferedImage canvas;
	public int tilemapStart, tilemapEnd;
	private JViewport viewport;
	private int vpX, vpY;
	
	public JViewport getVP() { return viewport; }
	
	private void exportImg() throws IOException {
		/*
		 * Only use if created image inside canvas
		 * which you want to export to a file.
		 */
		String file = 
				System.getProperty("user.dir") +
				"\\img\\logo.jpg";
		FileOutputStream fos = 
				new FileOutputStream(file);
		ImageIO.write(canvas, "png", fos);
	}
	
	public int getVpX() {
		return vpX;
	}

	public void setVpX(int vpX) {
		this.vpX = vpX;
	}

	public int getVpY() {
		return vpY;
	}

	public void setVpY(int vpY) {
		this.vpY = vpY;
	}
	
	public void turnOnLCD() {
		init();
	}
	
	public void init() {  
        JFrame frame = new JFrame("GB Static Image");  
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        String img = System.getProperty("user.dir") +
        		"\\img\\logo.jpg";
        JLabel label = new JLabel(new ImageIcon(img));  
        label.setPreferredSize(new Dimension(256, 256));  
  
        viewport = new JViewport();
		viewport.setSize(159, 143);
		viewport.add(label);
		Point point = new Point(0,0);
		viewport.setViewPosition(point);
  
        frame.add(viewport, BorderLayout.CENTER);  
        frame.setSize(159, 143);  
        frame.setVisible(true);  
    }

}
