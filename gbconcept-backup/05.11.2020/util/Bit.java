package util;

public class Bit {
	
	private static int bitcount;
	public static int b1 = 1;
	public static int b2 = 2;
	public static int b3 = 3;
	public static int b4 = 4;
	public static int b5 = 5;
	public static int b6 = 6;
	public static int b7 = 7;
	public static int b8 = 8;
	public static int b9 = 9;
	public static int b10 = 10;
	public static int b11 = 11;
	public static int b12 = 12;
	public static int b13 = 13;
	public static int b14 = 14;
	public static int b15 = 15;
	public static int b16 = 16;
	public static int b17 = 17;
	public static int b18 = 18;
	public static int b19 = 19;
	public static int b20 = 20;
	public static int b21 = 21;
	public static int b22 = 22;
	public static int b23 = 23;
	public static int b24 = 24;
	public static int b25 = 25;
	public static int b26 = 26;
	public static int b27 = 27;
	public static int b28 = 28;
	public static int b29 = 29;
	public static int b30 = 30;
	public static int b31 = 31;
	public static int b32 = 32;
	
	public static void setbits(int n) {
		bitcount = n;
	}
	
	public static int bits() { return bitcount; }
	
	public static String binStr(int val, boolean sign) {
	    String x = Integer.toBinaryString(val);

	    if(x.length() > bits() ) {
	        System.out.println("Error! Instruction binary is > 16 digits. Should be <=16.");
	        System.exit(1);
	    }
	    
	    if(x.length() == bits() )
	        return x;
	    
	    int diff=Bit.bits()-x.length();
	    for(int i=0;i<diff;i++) 
	        x = sign ? "0" + x : "1" + x; 
	    return x;
	}
	
	public static String hexStr(int val) {
		return Integer.toHexString(val).toUpperCase();
	}

	//Inclusive. so 8-16 will grab the last 9-bits.
	public static char substr(char c, int startindex, int endindex) {
	    int numberofbits = endindex - startindex + 1;
	    return (char) (c >> (bitcount - endindex) &  maskBy(numberofbits));
	}
	
	//Inclusive. so 8-16 will grab the last 9-bits.
	public static int substrInt(int c, int startindex, int endindex) {
	    int numberofbits = endindex - startindex + 1;
	    return (char) (c >> (bitcount - endindex) &  maskBy(numberofbits));
	}
	
	//Inclusive. so 8-16 will grab the last 9-bits.
		public static byte substr(byte c, int startindex, int endindex) {
		    int numberofbits = endindex - startindex + 1;
		    return  (byte) (c >> (bitcount - endindex) &  maskBy(numberofbits));
		}

	private static int maskBy(int n) {
	    if ( n == 1 ) return 1;
	    return (int) (Math.pow(2, n - 1) + maskBy( n - 1));
	}

	public static boolean isSet(char x, int pos) {
	    return substr(x, pos, pos) == 1;
	}
	
	public static boolean isSetInt(int x, int pos) {
	    return substrInt(x, pos, pos) == 1;
	}
	
	public static boolean isSet(byte x, int pos) {
	    return substr(x, pos, pos) == 1;
	}
	
	public static char sext(char x) {
		String str = binStr(x,false);
		return (char) Integer.parseInt(str,2);	
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
	
	public static int combine(int b[]) {
		return (b[0] << 8) | b[1];
	}

}
