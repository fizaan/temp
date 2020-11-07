package gameboyconcept.logo;

import java.awt.Color;

public class Palette {
	private int hByte, lByte;
	private int[] hBits, lBits;
	public static final int COL_WHITE = 0;
	public static final int COL_LIGHTGREY = 1;
	public static final int COL_DARK_GREY = 2;
	public static final int COL_BLACK = 3;
	private Pixel[] pixels;
	private int x,y;
	
	public Palette() {
		hBits = new int[8];
		lBits = new int[8];
		pixels = new Pixel[8];
	}
	
	public void setXY(int x, int y) {
		this.x  = x;
		this.y = y;
	}
	
	public Pixel[] getPixels() { return pixels; }
	
	public void init() {
		hBits[0] = (hByte & 0b10000000) >> 7;
		hBits[1] = (hByte & 0b01000000) >> 6;
		hBits[2] = (hByte & 0b00100000) >> 5;
		hBits[3] = (hByte & 0b00010000) >> 4;
		hBits[4] = (hByte & 0b00001000) >> 3;
		hBits[5] = (hByte & 0b00000100) >> 2;
		hBits[6] = (hByte & 0b00000010) >> 1;
		hBits[7] = hByte & 0b00000001;
		
		lBits[0] = (lByte & 0b10000000) >> 7;
		lBits[1] = (lByte & 0b01000000) >> 6;
		lBits[2] = (lByte & 0b00100000) >> 5;
		lBits[3] = (lByte & 0b00010000) >> 4;
		lBits[4] = (lByte & 0b00001000) >> 3;
		lBits[5] = (lByte & 0b00000100) >> 2;
		lBits[6] = (lByte & 0b00000010) >> 1;
		lBits[7] = lByte & 0b00000001;
		
		for(int i = 0; i < 8; i++) {
			int shade = hBits[i] << 1 | lBits[i];
			pixels[i] = new Pixel(x++, y, getColourClassic(shade));
		}
	}
	
	public void setHB(int b) { hByte = b; }
	public void setLB(int b) { lByte = b; }
	
	public void print() {
		for(int i:hBits)
			System.out.print(i);
		System.out.println();
		for(int i:lBits)
			System.out.print(i);
	}
	
	public static void main(String args[]) {
		//test it:
		int a = 0xB9;
		int b = 0x3C;
		Palette p = new Palette();
		p.setHB(a);
		p.setLB(b);
		p.init();
		p.print();
	}
	
	private Color getColourClassic(int x) {
		switch(x) {
			case COL_WHITE:
				return new Color(155, 188, 15);
			
			case COL_LIGHTGREY:
				return new Color(139, 172, 15);
				
			case COL_DARK_GREY:
				return new Color(48, 98, 48);
				
			case COL_BLACK:
				return new Color(15,56,15);
		}
		
		return null;
	}
	
	private Color getColourBW(int x) {
		switch(x) {
			case COL_WHITE:
				return new Color(255,255,255);
			
			case COL_LIGHTGREY:
				return new Color(192, 192, 192);
				
			case COL_DARK_GREY:
				return new Color(96, 96, 96);
				
			case COL_BLACK:
				return new Color(0,0,0);
		}
		
		return null;
	}

}
