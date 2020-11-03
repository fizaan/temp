package util;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import nes.mapper.Header;

/*
 * Special thanks to www.mistapotta.com
 * Oct 18 2020 4:30 PM
 */

@SuppressWarnings("serial")
public class Bitmap extends Canvas {
	
	private BufferedImage canvas;
	private int pixelSize;
	public static final int SIZE = 600;
	private byte[] sprites;
	

	public Bitmap(int pSize) {
		/*
		 * The window
		 */
		canvas = new BufferedImage(SIZE,SIZE,BufferedImage.TYPE_INT_ARGB);
		Bit.setbits(8); //1 byte
		
		/*
		 * Pixel size. Default is size 1
		 * According to MY code.
		 * 
		 * But can pixel x, y coordinates
		 * be float or decimal points?
		 */
		pixelSize = pSize;
		if(pixelSize == 0)
			pixelSize = 1;
 	}
	
	public void paint(Graphics g) {
		/*
    	 * X and Y start positions
    	 * Horizontal bit count and
    	 * Vertical bit count.
    	 * 
    	 * Padding is spacing between 
    	 * tiles
    	 */
    	int pixelX, pixelY, 
    		HORBitCount,
    		padding;
    	
    	HORBitCount = 0;
    	padding = 5;
        
        /*
         * Start at whatever x,y positions.
         */
        pixelX = 5;
        pixelY = 5;
        
        /*
         * Load the cartridge:
         */
        File file = new File("C:/Users/Alifa/Desktop/lc3-backup/eclipse/Nes/nestest.nes");
		Header header = new Header();
		try {
			header.readRom(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*
		 * Get the sprites:
		 * 
		 * sadistech.com/nesromtool/romdoc.html
		 * 
		 */
		sprites = header.getChrRam();
        for(int i = 0; i < sprites.length; i++) {
        	byte bitmapRow = sprites[i];
        	for( int k = 1; k <= Bit.bits(); k++) {
        		if(pixelY >= 600) {
        			HORBitCount += 8 + padding + pixelSize;
        			pixelX = HORBitCount;
        			pixelY = 5;
        		}
        		if(Bit.isSet(bitmapRow, k)) 
        			drawPixel(pixelX, pixelY, pixelSize);
        		pixelX+= pixelSize;
        	}
        	
        	pixelX = HORBitCount + padding + pixelSize;
        	pixelY+= pixelSize;
        }
        draw(g);
        
        
    }
	
	private void drawPixel(int x, int y, int size) {
		/*
		 * Colour of pixel is black:
		 */
		Color c=new Color(0,0,0);
		int color = c.getRGB();
		
		if(size == 0) 
			canvas.setRGB(x, y, color);
		else {
			for(int i = 0; i < size; i++) 
				for (int k = 0; k <size; k++) 
					canvas.setRGB(x+i, y+k, color);
		}
	}
	
	private void draw(Graphics g) {
		/*
		 * paint it.
		 */
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(canvas, null, null);
	}
}

