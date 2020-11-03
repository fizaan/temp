package util;

public class BitIntTest {

	public static void main(String[] args) {
		int x = 0x9fff;
		System.out.println(x);
		Bit.setbits(16);
		int highByte = Bit.substrInt(x, 1, 8);
		int lowByte = Bit.substrInt(x, 9, 16);
		System.out.println(highByte);
		System.out.println(lowByte);
		System.out.println(highByte << 8 | lowByte );
		
		x = 0b11 << 6;
		System.out.println(Integer.toBinaryString(x));
		System.out.println(x);

	}

}
