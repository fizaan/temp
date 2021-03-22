package lc3.vm.util;

import java.util.Scanner;

public class Vars {
	
	/* Opcodes */
	public static final int OP_BR = 0; /* branch */
	public static final int OP_ADD = 1;    /* add  */
	public static final int OP_LD = 2;    /* load */
	public static final int OP_ST = 3; /* store */
	public static final int OP_JSR = 4; /* jump register */
	public static final int OP_AND = 5;    /* bitwise and */
	public static final int OP_LDR = 6;    /* load register */
	public static final int OP_STR = 7;    /* store register */
	public static final int OP_RTI = 8;    /* unused */
	public static final int OP_NOT = 9;    /* bitwise not */
	public static final int OP_LDI = 10;    /* load indirect */
	public static final int OP_STI = 11;    /* store indirect */
	public static final int OP_JMP = 12;    /* jump */
	public static final int OP_RES = 13;    /* reserved (unused) */
	public static final int OP_LEA = 14;    /* load effective address */
	public static final int OP_TRAP = 15;    /* execute trap */
	
/* TRAP Codes */
	
	/*
	public static final int TRAP_GETC = 0x20;   get character from keyboard, not echoed onto the terminal 
	public static final int TRAP_OUT = 0x21;   output a character 
	public static final int TRAP_PUTS = 0x22;   output a word string 
	public static final int TRAP_IN = 0x23;     get character from keyboard, echoed onto the terminal 
	public static final int TRAP_PUTSP = 0x24;  output a byte string 
	public static final int TRAP_HALT = 0x25;    halt the program */
	
	//modfied trap codes
	
	public static final int TRAP_GETC = 0x00;   //get character from keyboard, not echoed onto the terminal 
	public static final int TRAP_OUT = 0x01;   //output a character 
	public static final int TRAP_PUTS = 0x02;   //output a word string 
	public static final int TRAP_IN = 0x03;     //get character from keyboard, echoed onto the terminal 
	public static final int TRAP_PUTSP = 0x04;  //output a byte string 
	public static final int TRAP_HALT = 0x05; //halt the program
	
	/*
	 * Custom Traps:
	 */
	
	//print hex value of a variable:
	public static boolean PRINT_HEX;
	
	public static final int TRAP_OUTR = 0x06;
	public static final int TRAP_MOVR = 0x07;
	public static final int TRAP_SWAPR = 0x08;
	public static final int TRAP_MOVI = 0x09;
	public static final int TRAP_SCAN = 0x0A;
	public static final int TRAP_LOAD = 0x0B;
	public static final int TRAP_RUN = 0x0C; 
	//kernel or user mode
	public static final int TRAP_MODE = 0x0D;
	public static final int TRAP_PUSH = 0x0E;
	public static final int TRAP_POP = 0x0F;
	
	//Custom addr: mul
	public static final int ADDR_NORMAL = 0x00;
	public static final int ADDR_MULR = 0x01; 
	public static final int ADDR_CMP = 0x02;
	
	public static boolean running = true;
	public static Scanner input = new Scanner(System.in); 
	
	//troubleshooting only
	public static int GC = 0;
	public static boolean TROUBLE_SHOOT = false;
	
	//FLAGS
	// 1 << 0; //POS = 1
    // 1 << 1; //ZER = 2
    // 1 << 2; //NEG = 4
	public static final byte POS = 0x01;
	public static final byte ZER = 0x02;
	public static final byte NEG = 0x04;
	
	/*
	 * FF07 and FF08
	 * are memory mapped IO for
	 * doing bitwise operations
	 */
	public static final char MR_DATA1 = 0xFF07;
	public static final char MR_DATA2 = 0xFF08;
	public static final char MR_BITWISE = 0xFF09;
	public static final char MR_OR = 0x5A;
	public static final char MR_XOR = 0x5B;
	public static final char MR_SLEEP_ADDR = 0xFF0A;
	public static final char MR_LCDINIT = 0xFF0B;
	public static final char MR_LCDINITVAL = 0x5C;
	
	//MAX_MEM
	public static final int MAX_MEM = 0xFFFF;
	
	//COND
	public static final int COND = 9;
	
	//SIZE
	public static final int SIZE = 13;
	

}
