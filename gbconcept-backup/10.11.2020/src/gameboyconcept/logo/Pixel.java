package gameboyconcept.logo;

import java.awt.Color;

public class Pixel {
	private int x;
	private int y;
	private Color c;
	
	public Pixel(int x, int y, Color c) {
		this.x = x;
		this.y = y;
		this.c = c;
	}
	
	public int getX() { return x; }
	public int getY() { return y; }
	public Color getColor() { return c; }

}
