package gameboyconcept;

import gameboyconcept.gblogger.Logit;
import util.Bit;

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
	
	private void set() {
		Bit.setbits(Bit.b8);
	}

	public void setColours(int c) {
		set();
		this.colour3 = Bit.substrInt(c, Bit.b1, Bit.b2);
		this.colour2 = Bit.substrInt(c, Bit.b3, Bit.b4);
		this.colour1 = Bit.substrInt(c, Bit.b5, Bit.b6);
		this.colour0 = Bit.substrInt(c, Bit.b7, Bit.b8);
	}
}
