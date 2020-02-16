/*
 ============================================================================
 Name        : SignExt.c
 Author      : faiz
 Version     :
 Copyright   : Your copyright notice
 Description : Hello World in C, Ansi-style
 ============================================================================
 */

#include "main.h"

/*
 * Output:
 * 		1: a025
		2: 25
		3: ffe5
		4: ffea
		5: fff0
		6: fff6
 */


uint16_t sext(uint16_t x, uint16_t bit_count) {
	if ((x >> (bit_count - 1)) & 1) {
	        x |= (0xFFFF << bit_count);
	    }
	return x;
}

int main(void) {

	uint16_t x = 0b1010000000100101;
	printf("1: %x\n",x);
	uint16_t offset = x & 0b111111;
	printf("2: %x\n",offset);
	uint16_t sextended = sext(offset,6);
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
	printf("3: %x\n",sextended);

	uint16_t random = 5;
	random = 5 + sextended;
	printf("4: %x\n",random);

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
	uint16_t imm5 = 0b10000;
	uint16_t signed_offset = sext(imm5,5);

	//RO = FFF0 (which is decimal -16)
	printf("5: %x\n",signed_offset);

	//RO = RO + 6
	uint16_t r0 = signed_offset + 6;

	//RO = FFF6 (which is decimal -10)
	printf("6: %x\n",r0);

	//BUT! The program knows FFF0 and FFF6 are negative number. How????

	return EXIT_SUCCESS;

}



