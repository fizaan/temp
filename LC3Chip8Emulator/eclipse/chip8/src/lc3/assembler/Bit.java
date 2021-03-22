package lc3.assembler;

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
	
	public static void setbits(int n) {
		bitcount = n;
	}
	
	private static int bits() { return bitcount; }
	
	public static String binStr(int val, boolean sign) throws LC3AssemblerException {
	    String x = Integer.toBinaryString(val);

	    if(x.length() > bits() )
	    	throw new LC3AssemblerException("Binary is > 16 digits. Should be <=16.");
	    
	    if(x.length() == bits() )
	        return x;
	    
	    int diff=16-x.length();
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

	private static int maskBy(int n) {
	    if ( n == 1 ) return 1;
	    return (int) (Math.pow(2, n - 1) + maskBy( n - 1));
	}

	public static boolean isSet(char x, int pos) {
	    return substr(x, pos, pos) == 1;
	}
	
	public static char sext(char x, boolean sign) {
		String str = null;
		try {
			str = binStr(x,sign);
		} catch (LC3AssemblerException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return (char) Integer.parseInt(str,2);	
	}

}
