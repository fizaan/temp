
/*
 * 	1: a025
	2: 25
	3: 3fffe5
	4: 3fffea
	5: ffea
	6: ea2c
	7: b025
	8: fff0
	9: fff6
	10: 12ff0 (should have printed 2FF0)
	11: 2ff0

 */

public class SignExtInt {
	
	
	static int sext(int x, int bit_count) {
		if (((x >>> (bit_count - 1)) & 1) == 1) {
		        x |= (0xFFFF << bit_count);
		    }
		return x;
	}

	public static void main(String[] args) {
		int x = 0b1010000000100101;
		System.out.printf("1: %x\n",x);
		int offset = x & 0b111111;
		System.out.printf("2: %x\n",offset);
		int sextended = sext(offset,6);
			/*
			 * offset = 100101
			 * extended = 1111111111100101 = FFE5
			 *
			 * 			1111111111111111 << 6
			 * 		=	1111111111000000
			 * 			1111111111000000 | 100101
			 * 		=	1111111111100101
			 * 		= 	FFE5
			 */
		System.out.printf("3: %x\n",sextended);

		int random = 5;
		random = 5 + sextended;
		System.out.printf("4: %x\n",random);
		
		/*
		 * Conversion to char will eliminate the higher bits
		 * e.g 3fffe5 will become ffe5 as shown below:
		 */
		
		char c = (char) random;
		random = (int) c;
		System.out.printf("5: %x\n",random);
		
		random = 0xFFF0EA2C; // FFF0 EA2C
		c = (char) random;
		random = (int) c;
		System.out.printf("6: %x\n",random);
		
		random = 0xB025;
		c = (char) random;
		random = (int) c;
		System.out.printf("7: %x\n",random);
		
		
		/*
		 * Let's assume you have the following 2 instructions starting at 0x3000:
		 * 				ADD	R0, R1, #-16
		 * 				ADD R0, R0, #6
		 * What will the final value in RO?
		 * IMM5 is 5 bits, so to store -16 in 5 bits we get:
		 * 		-16 = 1111111111110000 in two's complement negative number.
		 * 		taking last 5 bits to put within imm5
		 * 		= 10000.
		 *
		 * 		Sign extension of 10000 is:
		 * 		1111111111110000
		 *
		 * 		R0 = 0 + 1111111111110000
				R0 = 0 + 1111 1111 1111 0000
				R0 = 0 + FFF0
				RO = FFF0
		 *
		 */
		
		int imm5 = 0b10000;
		int signed_offset = sext(imm5,5);
		c = (char) signed_offset;
		signed_offset = (int) c;
		
		//RO = FFF0 (which is from signed two's complement is decimal -16)
		System.out.printf("8: %x\n",signed_offset);
		
		//RO = RO + 6
		int r0 = signed_offset + 6;
		
		//RO = FFF6 (which is from signed two's complement is decimal -10)
		System.out.printf("9: %x\n",r0);
		
		//BUT! The program knows FFF0 and FFF6 are negative number. How????
		//only char knows, not int.
		
		int a1 = 0x3000;
		int b1 = 0xFFF0;
		int c1 = a1 + b1;
		System.out.printf("10: %x\n",c1); //10: 12ff0 (error)
		
		c1 = c1 & 0xFFFF;
		System.out.printf("11: %x\n",c1); //11: 2ff0 (correct)
		
 
		
	}

}
