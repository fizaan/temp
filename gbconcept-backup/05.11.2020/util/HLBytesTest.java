package util;

public class HLBytesTest {

	public static void main(String[] args) {
	int x = -7;
	System.out.println(Integer.toHexString(x));
	int hlbytes[] = Bit.hlbytes(x);
	System.out.println(Integer.toHexString(hlbytes[0]));
	System.out.println(Integer.toHexString(hlbytes[1]));
	System.out.println(Integer.toHexString(Bit.combine(hlbytes)));
	
	System.out.println();
	
	x = 277;
	System.out.println(Integer.toHexString(x));
	hlbytes = Bit.hlbytes(x);
	System.out.println(Integer.toHexString(hlbytes[0]));
	System.out.println(Integer.toHexString(hlbytes[1]));
	System.out.println(Integer.toHexString(Bit.combine(hlbytes)));

	
	System.out.println();
	
	x = -32608;
	System.out.println(Integer.toHexString(x));
	hlbytes = Bit.hlbytes(x);
	System.out.println(Integer.toHexString(hlbytes[0]));
	System.out.println(Integer.toHexString(hlbytes[1]));
	System.out.println(Integer.toHexString(Bit.combine(hlbytes)));
	System.out.println(String.format("%04x", Bit.combine(hlbytes)));
	
	}

}
