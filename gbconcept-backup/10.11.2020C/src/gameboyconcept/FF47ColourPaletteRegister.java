package gameboyconcept;

import gameboyconcept.gblogger.Logit;

public class FF47ColourPaletteRegister {
	private int colour3,
		colour2, colour1,
		colour0;
	
	public static final char ADDR = 0xFF47;
	
	public int getColour(int c) {
		switch(c) {
			case 0:
				return colour0;
			case 1:
				return colour1;
			case 2:
				return colour2;
			case 3:
				return colour3;
			default:
				Logit.error(this, "Invalid colour");
				return -1;
		}
	}
}
