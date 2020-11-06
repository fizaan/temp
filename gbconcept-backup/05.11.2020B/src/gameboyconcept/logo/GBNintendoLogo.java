package gameboyconcept.logo;

import static tests.TileDataAddressTest.spriteAddress;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import gameboyconcept.GBBus;

public class GBNintendoLogo extends Canvas {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BufferedImage canvas;
	private List<Pixel> pixels = new ArrayList<>();
	private int tilemapStart, tilemapEnd;
	
	public static void init(GBBus bus, int len, int hei) {
		GBNintendoLogo gb = new GBNintendoLogo(len,hei);
		gb.setTileMap(bus.cpuRUnsigned8(0xFF40));
		gb.generatePalette(bus, bus.cpuRUnsigned8(0xFF40));
		JFrame jp = new JFrame();
        jp.getContentPane().add(gb, BorderLayout.CENTER);
        jp.setSize(new Dimension(len,hei));
        jp.setVisible(true);
	}
	
	public GBNintendoLogo(int len, int hei) {
		canvas = new BufferedImage(len,hei,BufferedImage.TYPE_INT_ARGB);
	}
	
	public void setTileMapExplicit(int start, int end) {
		tilemapStart = start;
		tilemapEnd = end;
	}
	
	public void setTileMap(int mFF40) {
		/*
		 * Bit 3 (bit 5)
		 * 0: 9800 to 9BFF
		 * 1: 9C00 to 9FFF
		 * 
		 * Bit 4 (bit 4)
		 * 0: 8800 to 97FF
		 * 1: 8000 to 8FFF
		 */
		
		if ( ((mFF40 & 0b00001000) >> 3) == 1) {
			tilemapStart = 0x9C00;
			tilemapEnd = 0xA000;
		}
		else {
			tilemapStart = 0x9800;
			tilemapEnd = 0x9C00;
		}
	}
	
	public void paint(Graphics g) {
		for(Pixel p:pixels) 
			try {
				canvas.setRGB(p.getX(), p.getY(), p.getColor().getRGB());
			}
			catch(Exception e) {
				/*
				 * If a pixel cannot be drawn
				 * skip it and move on to next.
				 */
				System.out.println("Cannot draw pixel: " + p.getX() + ", " +
					p.getY());
			}
		draw(g);
	}
	
	private void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(canvas, null, null);
	}
	
	public void generatePalette(GBBus bus, int mFF40)  {
		int hor = 0;
		int ver = 0;
		int x = 0;
		int y = 0;
		int inc = 8;
		int pixelCount = 0;
		
		/*
		 * maxRowPixels
		 * Each tile has 64 pixels
		 * if 256 x 256 
		 * 		maxRowPixels = 2048 (32 * 64)
		 * else if 160 x 144
		 * 		maxRowPixels = 1280 (20 * 64)
		 */
		int maxRowPixels = canvas.getWidth() == 256 ? 2048 : 1280;
		Pixel[] palettes;
		Palette palette = new Palette();
		
		for(int i = tilemapStart; i < tilemapEnd; i++) {
	    	int dataAddr = spriteAddress((mFF40 & 0b00010000) >> 4, bus.cpuRUnsigned8(i));
	    	/*
			 * Read range 0 to F
			 * e.g.
			 * 08000: 08000 to 0x800F
			 * 
			 * 08000, 08002, 08004, 08006,
			 * 08008, 0800A, 0800C, 0800D,
			 */
			for(int k = 0; k < 16; k = k + 2) {
				int hByte = bus.cpuRUnsigned8(dataAddr + k);
				int lByte = bus.cpuRUnsigned8(dataAddr + (k + 1));
				palette.setHB(hByte);
				palette.setLB(lByte);
				palette.setXY(x, y);
				palette.init();
				palettes = palette.getPixels();
				for(Pixel pix: palettes)
					pixels.add(pix);
				
				x += inc;
				pixelCount += inc;
				
				if(x % 8 == 0) {
					x = hor;
					y++;
				}
				
				/*
				 * 1 tile completed
				 * 8 x 8 = 64p
				 * p means pixels	
				 */
				if(pixelCount % 64 == 0) {
					hor = hor + inc;
					x = hor;
					y = ver;
				}
				
				if(pixelCount % maxRowPixels == 0) {
					x = 0;
					hor = 0;
					ver = ver + inc;
					y = ver;
				}
			}
			
			System.out.println();
	    }
	}
	    

}
