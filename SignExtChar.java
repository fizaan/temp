
/*
 * out:
 * 	1: a025
	2: 25
	3: ffe5
	4: ffea
	5: fff6
 */

public class SignExtChar {
	
	
	static char sext(char x, int bit_count) {
		if (((x >>> (bit_count - 1)) & 1) == 1) {
		        x |= (0xFFFF << bit_count);
		    }
		return x;
	}

	public static void main(String[] args) {
		char x = 0b1010000000100101;
		System.out.printf("1: %x\n",(int) x);
		char offset = (char) (x & 0b111111);
		System.out.printf("2: %x\n",(int) offset);
		char sextended = sext(offset,6);
			/*
			 * offset = 100101
			 * sign extended = 1111111111100101 = FFE5
			 *
			 * 			1111111111111111 << 6
			 * 		=	1111111111000000
			 * 			1111111111000000 | 100101
			 * 		=	1111111111100101
			 * 		= 	FFE5
			 */
		System.out.printf("3: %x\n",(int) sextended);

		char random = 5;
		random = (char) (5 + sextended);
		System.out.printf("4: %x\n",(int) random);
		
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
				RO = FFF0 (-16)
				
				RO + 6 = FFF6 (-10)
		 *
		 */
		
		char imm5binary = 0b10000;
		char imm5sext = sext(imm5binary, 5);
		char r0 = (char) (imm5sext + 6);
		System.out.printf("5: %x\n",(int) r0);
		

	}

}
