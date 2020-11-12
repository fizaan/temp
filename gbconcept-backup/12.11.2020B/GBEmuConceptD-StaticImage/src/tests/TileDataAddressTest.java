package tests;

public class TileDataAddressTest {
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
