package util;

public class Bit {
	public static String hexStr(int x) {
		return Integer.toHexString(x).toUpperCase();
	}
	
	public static int[] hlbytes(int data) {
		/*
		 * https://stackoverflow.com/questions/6090561/
		 * how-to-use-high-and-low-bytes/6090641
		 */
		int[] b = new int[2];
		b[0] = (data >> 8) & 0xFF;
		b[1] = data & 0xFF;
		return b;
	}
}
