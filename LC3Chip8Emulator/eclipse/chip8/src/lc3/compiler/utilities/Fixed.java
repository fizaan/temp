package lc3.compiler.utilities;

public class Fixed {
	public static final byte STR = 0x1;
	public static final byte NUM = 0x2;
	public static final byte FUNC = 0x3;
	public static final byte SYS_PRN = 0x4;
	public static final byte SYS_PRNLN = 0x5;
	public static final byte NEW_DECL = 0x6;
	public static final byte MODIFY = 0x7;
	public static final byte SYS_PRN_STR = 0x8;
	public static final byte SYS_PRNLN_STR = 0x9;
	public static final byte RETURN = 0xA;
	
	/*
	 * PAR0 and PAR1
	 * store paramter values
	 * in register r0 and r1
	 * which can be passed between
	 * functions
	 */
	public static final byte PAR0 = 0xB;
	public static final byte PAR1 = 0xC;
	public static final byte SET = 0xD;
	public static final byte LET_X_GETPARAM = 0xE;
	
	/*
	 * add, sub, mul, equ
	 */
	
	public static final byte EQU = 0xF;
	public static final byte ADD = 0x10; 
	public static final byte SUB = 0x11; 
	public static final byte MUL = 0x12; 
	
	/*
	 * condition types:
	 * <,<=,>,>=,!=,==
	 */
	public static final byte LT = 0x13;
	public static final byte LTE = 0x14; 
	public static final byte GT = 0x15; 
	public static final byte GTE = 0x16;
	public static final byte NOT_E = 0x17;
	public static final byte EE = 0x18;
	public static final byte IF = 0x19;
	public static final byte ELSE = 0x20;
	public static final byte ENDIF = 0x21;
	
	//loop
	public static final byte WHILE = 0x22;
	public static final byte ENDWHILE = 0x23;
	public static final byte DO = 0x24;
	public static final byte DOLOOP = 0x25;
	
	//Modify - divide
	public static final byte DIV = 0x26;
	
	//strlen - strlen x
	public static final byte STRLEN = 0x27;
	
	//pointers! YAY fun stuff :)
	public static final byte VALUE_AT_ADDRESS = 0x28;
	public static final byte ADDRESS_OF_VARIABLE = 0x29;
	
	//Array index
	public static final byte INDEX = 0x2A;
	
	//getpublic x xPublic
	//setpublic xPublic x
	//randintarray x 10
	//uninitarray x 10
	public static final byte GETPUBLIC = 0x2B;
	public static final byte SETPUBLIC = 0x2C;
	public static final byte RANDINTARR = 0x2D;
	public static final byte UNINITARR = 0x2E;
	
	/*
	 * AND
	 */
	public static final byte AND = 0x2F; 
	
	/*
	 * Raw assembly,
	 * Label (to jump to) 
	 * Jump
	 * Fill Interger array
	 */
	public static final byte LABEL = 0x30;
	public static final byte ASM = 0x31; 
	public static final byte JMP = 0x32;
	public static final byte FIA = 0x33;
	
	
	

}
