package lc3.assembler;

public class Fixed {
	public static final short r0 = 0;
	public static final short r1 = 1;
	public static final short r2 = 2;
	public static final short r3 = 3;
	public static final short r4 = 4;
	public static final short r5 = 5;
	public static final short r6 = 6;
	public static final short r7 = 7;
	public static final short r8 = 8;
	public static final short r9 = 9;
	public static final short r10 = 10;
	
	public static final short ADDI = 0;
	public static final short ADDR = 1;
	public static final short HALT = 2;
	public static final short OUT = 3;
	public static final short PUTS = 4;
	public static final short IN = 5;
	public static final short GETC = 6;
	public static final short PUTSP = 7;
	public static final short ANDI = 8;
	public static final short ANDR = 9;
	public static final short RET = 10;
	public static final short NOT = 11;
	public static final short JMP = 12;
	public static final short JSRR = 13;
	public static final short LEA = 14;
	public static final short LD = 15;
	public static final short LDI = 16;
	public static final short LDR = 17;
	public static final short LEAHEX = 18;
	public static final short JSR = 19;
	public static final short ST = 20;
	public static final short STI = 21;
	public static final short STR = 22;
	
	//oh god! Had to make new case for ld function address
	public static final short LDRF = 23;
	public static final short LEAF = 24;
	
	//br
	public static final short BR = 25;
	
	//RTI RES
	public static final short RTI = 26;
	public static final short RES = 27;
	
	//custom
	public static final short OUTR = 28;
	public static final short MOVR = 29;
	public static final short SWAPR = 30;
	public static final short MOVI = 31;
	public static final short SCAN = 32;
	public static final short LD_USERPROG = 33;
	public static final short RUN_USERPROG = 34;
	public static final short MODE = 35;
	public static final short PUSH = 36;
	public static final short POP = 37;
	public static final short MULR = 38;
	public static final short CMP = 39;
	
	/*
	 * "abs" after each:
			br
			jsr
			leaf
			ld
			ldi
			lea
			st
			sti
	 */
	public static final short ABS = 40;
	
	public static final boolean POS = true;
	public static final boolean NEG = false;
	
	public static final int WORD_MAX_VALUE = 32767;
	public static final int WORD_MIN_VALUE = -32768;
	
	public static boolean PRINT_WARNING=true;
	public static boolean TROUBLE_SHOOT = false;
	

}
