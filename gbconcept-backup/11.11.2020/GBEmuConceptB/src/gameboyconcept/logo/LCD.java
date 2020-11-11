package gameboyconcept.logo;

import static tests.TileDataAddressTest.spriteAddress;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JViewport;

import gameboyconcept.GBBus;
import gameboyconcept.GBCpuBootRom;
import gameboyconcept.gblogger.Logit;

public class LCD extends Canvas {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int FRAME_RATE = 1 / 60 * 1000;
	public static final int SLOW_FRAME_RATE = 20;
	public static final int LEN =  256;
	public static final int HEI =  256;
	private BufferedImage canvas;
	private List<Pixel> pixels = new ArrayList<>();
	public int tilemapStart, tilemapEnd;
	private GBBus bus;
	private JViewport viewport;
	private int xPos, yPos, hor, ver, 
		lyScaneline, pixelCount, vpX, vpY;
	
	public JViewport getVP() { return viewport; }
	private void setVP(JViewport vp) { viewport = vp; }
	
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
	
	public int getTileMapIndex() {
		return tilemapStart++;
	}
	
	public int getLYScaneline() { return lyScaneline; }

	public void setTilemapStart(int tilemapStart) {
		this.tilemapStart = tilemapStart;
	}

	public int getTilemapEnd() {
		return tilemapEnd;
	}

	public void setTilemapEnd(int tilemapEnd) {
		this.tilemapEnd = tilemapEnd;
	}
	
	public LCD(GBBus bus) {
		canvas = new BufferedImage(LCD.LEN,LCD.HEI,BufferedImage.TYPE_INT_ARGB);
		this.bus = bus;
	}
	
	public void turnOnLCD() {
		JViewport viewport = new JViewport();
		viewport.setSize(159, 143);
		viewport.add(this);
		Point point = new Point(0,0);
		viewport.setViewPosition(point);
		this.setVP(viewport);
		
		JFrame jp = new JFrame();
        jp.getContentPane().add(this.getVP(), BorderLayout.CENTER);
        jp.setSize(new Dimension(159,143));
        jp.setVisible(true);
	}
	
	public void setTileMap(int mFF40) {
		check(mFF40);
		/*
		 * TILE MAP/NUMBER:
		 * Bit 3 (bit 5)
		 * 0: 9800 to 9BFF
		 * 1: 9C00 to 9FFF
		 * 
		 * TILE DATA:
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
		//g.drawLine(vpX+2, vpY, 159, vpY);
		//g.drawLine(vpX+2, vpY, vpX+2, 143);
		//g.drawLine(159, vpY, 159, 143);
		//g.drawLine(vpX+2, 143, 159, 143);
	}
	
	private void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(canvas, null, null);
	}
	
	private void check(int mFF40) {
		if(mFF40 == 0xFF40)
			Logit.error(this, "Cannot be correct: " + 
					Integer.toHexString(mFF40).toUpperCase());
	}
	
	public void reset() {
		xPos = 0;
		yPos = 0;
		hor = 0;
		ver = 0;
		lyScaneline = 0;
		pixelCount = 0;
		pixels.clear();
		setTileMap(bus.cpuRUnsigned8(0xFF40));
	}
	
	public void printTile(int tileMapIndex, int mFF40) {
		check(mFF40);
		int maxRowPixels = canvas.getWidth() == 256 ? 2048 : 1280;
		int dataAddr = spriteAddress((mFF40 & 0b00010000) >> 4, bus.cpuRUnsigned8(tileMapIndex));
		Pixel[] palettes;
		int inc = 8;
		Palette palette = new Palette();
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
			palette.setXY(xPos, yPos);
			palette.init();
			palettes = palette.getPixels();
			for(Pixel pix: palettes)
				pixels.add(pix);
			
			xPos += inc;
			pixelCount += inc;
			
			if(xPos % 8 == 0) {
				xPos = hor;
				yPos++;
			}
			
			/*
			 * 1 tile completed
			 * 8 x 8 = 64p
			 * p = pixels	
			 */
			if(pixelCount % 64 == 0) {
				hor = hor + inc;
				xPos = hor;
				yPos = ver;
			}
			
			if(pixelCount % maxRowPixels == 0) {
				/*
				 * 1 row of 32 tiles 
				 * (scanline) completed
				 */
				xPos = 0;
				hor = 0;
				ver = ver + inc;
				yPos = ver;
				lyScaneline += inc;
				/*
				 * Is it correct to 
				 * say scanline is 
				 * increment by 8 
				 * pixels? :
				 * lyScaneline += inc;
				 */
			}
		}
	}

}
