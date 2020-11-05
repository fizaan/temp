package gameboyconcept.logo;

import static tests.TileDataAddressTest.spriteAddress;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import gameboyconcept.GBBus;
import gameboyconcept.gblogger.Logit;
import util.Bit;

public class GBNintendoLogo extends Canvas {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BufferedImage canvas;
	private List<Pixel> pixels = new ArrayList<>();
	private int tilemapStart, tilemapEnd;
	
	
	/*
	public static void main(String args[]) throws Exception {
		int length = 160; 
		int height = 144;
		GBNintendoLogo gb = new GBNintendoLogo(length,height);
		String binFilename = "logoRaw.bin"; 
		File file =  new File(
				"C:\\Users\\Alifa\\Desktop\\lc3-backup\\eclipse\\NesTestRuns-A\\logs\\binary\\" +
						binFilename);
		gb.generatePalette(file);
		JFrame jp = new JFrame();
        jp.getContentPane().add(gb, BorderLayout.CENTER);
        jp.setSize(new Dimension(length,height));
        jp.setVisible(true);
	}*/
	
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
		
		Bit.setbits(8);
		Logit.info(this, "value at FF40: " + Bit.binStr(mFF40, true));
	}
	
	public void paint(Graphics g) {
		for(Pixel p:pixels) 
			canvas.setRGB(p.getX(), p.getY(), p.getColor().getRGB());
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
		
		/*	This is where
		 * I am getting stuck.
		 * 12.5 tiles in one row??
		 * 
		 * 12.5 x 64 = 800
		 */
		int maxRowPixels = 2048;
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
