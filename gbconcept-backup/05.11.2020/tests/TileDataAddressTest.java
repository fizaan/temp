package tests;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import util.Bit;

public class TileDataAddressTest {

	public static void main(String[] args) throws Exception {
		Bit.setbits(16);
		/*
		 * assuming tiles map x9910 to x9904 has data
		 * has data from 19 to 1.
		 * 
		 * Print All tile addresses for
		 * that range (not their data).
		 *
		for (int i = 19; i > 0; i--)
			System.out.println(Bit.hexStr(spriteAddress(1,i)));
			*/
		
		String path = System.getProperty("user.dir") +
				"\\logs\\binary\\tileMapRaw.bin";
		File file = new File(path);
		InputStream input = new FileInputStream(file);  
	    DataInputStream inst = new DataInputStream(input);
	    while((inst.available())>0)
	    	System.out.printf("%04x\n", spriteAddress(1,inst.readByte()));
	    inst.close();
	}
	
	/*
	 * Why 0x10? What's so special about this?
	 * - Don't ask. It's a 2-byte indentation
	 *   for mapping a to sprite data. It's 
	 *   basically the decimal 16 which is x10.
	 */
	
	public static int spriteAddress(int set, int tileIndex) {
		char address;
		switch(set) {
			case 0:
				/*
				 * x8800 - x97FF 
				 * Indexing -128 to 127
				 */
				address = 0x8800;
				tileIndex += 128;
				address += (tileIndex * 0x10);
			break;
			
			case 1:
				/*
				 * x8000 - x8FFF 
				 * Indexing 0 - 255
				 */
				address = 0x8000;
				address += (tileIndex * 0x10);
			break;
			
			default:
				address = 0;
		}
		
		return address;
		
	}

}
