package lc3.assembler;

public class Assembler {
	
	//instruction[0] is the opcode e.g jmp or br
	
	public static void addandI(int opcode, String[] instruction, Parser asm) {
		int r0 = asm.getRegMap().get(instruction[1]);
		int r1 = asm.getRegMap().get(instruction[2]);
		int imm5 = Integer.parseInt(instruction[3]);
		//imm5 must be >= -16 and <= 31.
		asm.check("Out of range: " + imm5, (-16 <= imm5) && (imm5 <= 15));
		opcode = opcode << 12;
		r0 = r0 << 9;
		r1 = r1 << 6;
		int modebit = 1 << 5;
		imm5 = imm5 & 0b11111;	//two's complement.
		int byteEmit = opcode | r0 | r1 | modebit | imm5;
		asm.emitByte(byteEmit);
		
		ProgramTable pt = new ProgramTable();
		pt.setAddress(asm.getCounter()-1);
		pt.setLabelAddress(-1);
		pt.setAssemblyCode(instruction);
		pt.setPc(asm.getCounter());
		pt.setBinstr(byteEmit, true);
		pt.setHexstr(byteEmit);
		asm.getProgTable().add(pt);
		
	}
	
	/*
	 * nzpSimplify:
	 * Added Sunday April 19 2020
	 * 1:40PM
	 */
	private static String nzpSimplify(String nzp) throws LC3AssemblerException {
		if (nzp.length() == 3)
			//backward compatibility:
			return nzp;
		
		if(nzp.equals("<"))
			return "100";
		else if (nzp.equals("<="))
			return "110";
		else if (nzp.equals("=="))
			return "010";
		else if (nzp.equals(">"))
			return "001";
		else if (nzp.equals(">="))
			return "011";
		else if (nzp.equals("!="))
			return "101";
		else if (nzp.equals("<>"))
			//branch always
			return "111";
		else 
			throw new LC3AssemblerException("NZP undetermined");
					
	}
	
	public static void abs(String[] instruction,Parser asm) {
		ProgramTable pt = new ProgramTable();
		pt.setAddress(asm.getCounter()-1);
		pt.setLabelAddress(-1);
		pt.setAssemblyCode(instruction);
		pt.setPc(asm.getCounter());
		pt.setBinstr(0, true);
		pt.setHexstr(0);
		asm.getProgTable().add(pt);
		asm.emitByte(asm.getAbsoluteOffset());
	}
	
	//br 111 label
	//br 110 label
	public static void br(int opcode, String[] instruction, Parser asm) throws LC3AssemblerException {
		String nzp = nzpSimplify(instruction[1]);
		int binary = 2;
		int nzpbyte = Integer.parseInt(nzp,binary);
		nzpbyte = nzpbyte << 9;
		opcode = opcode << 12;
		String label = instruction[2];
		ProgramTable pt = new ProgramTable();
		int pcoffset = asm.getOffset(label);
		pt.setAssemblyCode(instruction);
		pt.setPc(asm.getCounter());
		pt.setLabelAddress(asm.getLabelAddress());
		pt.setAddress(asm.getCounter()-1);
		pt.setPcRelativeAddr(pcoffset);
		//LSBCheck(pcoffset,0x1ff,9);
		//pcoffset = pcoffset & 0x1ff;
		//int byteEmit = opcode | nzpbyte | offset;
		int byteEmit = opcode | nzpbyte;
		asm.emitByte(byteEmit);
		pt.setBinstr(byteEmit, true);
		pt.setHexstr(byteEmit);
		asm.getProgTable().add(pt);
	}
	
	public static void jmp(int opcode, String[] instruction, Parser asm) {
		int r0 = asm.getRegMap().get(instruction[1]);
		opcode = opcode << 12;
		r0 = r0 << 6;
		int byteEmit = opcode | r0;
		asm.emitByte(byteEmit);
		ProgramTable pt = new ProgramTable();
		pt.setAddress(asm.getCounter()-1);
		pt.setLabelAddress(-1);
		pt.setAssemblyCode(instruction);
		pt.setPc(asm.getCounter());
		pt.setBinstr(byteEmit, true);
		pt.setHexstr(byteEmit);
		asm.getProgTable().add(pt);
	}
	
	public static void jsr(int opcode, String[] instruction, Parser asm) throws LC3AssemblerException {
		opcode = opcode << 12;
		int bit11 = 0b1 << 11;
		String label = instruction[1];
		ProgramTable pt = new ProgramTable();
		int pcoffset = asm.getOffset(label);
		pt.setAssemblyCode(instruction);
		pt.setPc(asm.getCounter());
		pt.setLabelAddress(asm.getLabelAddress());
		pt.setAddress(asm.getCounter()-1);
		pt.setPcRelativeAddr(pcoffset);
		//LSBCheck(pcoffset,0x7ff,11);
		//pcoffset = pcoffset & 0x7ff;
		//int byteEmit = opcode | bit11 | offset;
		int byteEmit = opcode | bit11;
		asm.emitByte(byteEmit);
		pt.setBinstr(byteEmit, true);
		pt.setHexstr(byteEmit);
		asm.getProgTable().add(pt);
	}
	
	public static void ret(int opcode, String[] instruction, Parser asm) {
		opcode = opcode << 12;
		int r1 = 0b111 << 6;
		int byteEmit = opcode | r1;
		asm.emitByte(byteEmit);
		
		ProgramTable pt = new ProgramTable();
		pt.setAddress(asm.getCounter()-1);
		pt.setLabelAddress(-1);
		pt.setAssemblyCode(instruction);
		pt.setPc(asm.getCounter());
		pt.setBinstr(byteEmit, true);
		pt.setHexstr(byteEmit);
		asm.getProgTable().add(pt);
	}
	
	/*
	 * Enhancement 10April2020:
	 * addandR() is now using
	 * bits 12-13 to determine
	 * if it's a normal addR or
	 * a mulR or cmp
	 * 0000 000 000 002 000
	 * 
	 * if addr3 == 0x01 we do mulR
	 * 
	 * usage:
	 * 		mulr r0 r0 r1
	 */
	public static void addandR(int addr3, int opcode, String[] instruction, Parser asm) {
		int r0 = asm.getRegMap().get(instruction[1]);
		int r1 = asm.getRegMap().get(instruction[2]);
		int r2 = asm.getRegMap().get(instruction[3]);
		opcode = opcode << 12;
		r0 = r0 << 9;
		r1 = r1 << 6;
		addr3 = addr3 << 3;
		int byteEmit = opcode | r0 | r1 | addr3 | r2;
		asm.emitByte(byteEmit);
		
		ProgramTable pt = new ProgramTable();
		pt.setAddress(asm.getCounter()-1);
		pt.setLabelAddress(-1);
		pt.setAssemblyCode(instruction);
		pt.setPc(asm.getCounter());
		pt.setBinstr(byteEmit, true);
		pt.setHexstr(byteEmit);
		asm.getProgTable().add(pt);
	}
	
	/*
	 * Enhancement 11April2020:
	 * addandR() is now using
	 * bits 12-13 to determine
	 * if it's a normal addR or
	 * a mulR or a cmp
	 * 0000 000 000 002 000
	 * 
	 * if addr3 == 0x02 we do cmp
	 * usage:
	 * 		cmp r0 r1
	 * 		
	 * CC is set to N/Z/P depending
	 * on result of comparison
	 * 
	 * Note: the values of r0 or r1
	 * are UNCHANGED
	 */
	public static void cmp(int addr3, int opcode, String[] instruction, Parser asm) {
		int r0 = asm.getRegMap().get(instruction[1]);
		int r1 = asm.getRegMap().get(instruction[2]);
		opcode = opcode << 12;
		r0 = r0 << 9;
		r1 = r1 << 6;
		addr3 = addr3 << 3;
		int byteEmit = opcode | r0 | r1 | addr3;
		asm.emitByte(byteEmit);
		
		ProgramTable pt = new ProgramTable();
		pt.setAddress(asm.getCounter()-1);
		pt.setLabelAddress(-1);
		pt.setAssemblyCode(instruction);
		pt.setPc(asm.getCounter());
		pt.setBinstr(byteEmit, true);
		pt.setHexstr(byteEmit);
		asm.getProgTable().add(pt);
	}
	
	public static void leaFunc(int opcode, String[] instruction, Parser asm) throws LC3AssemblerException {
		int r0 = asm.getRegMap().get(instruction[1]);
		opcode = opcode << 12;
		r0 = r0 << 9;
		String label = instruction[2];
		
		ProgramTable pt = new ProgramTable();
		int pcoffset = asm.getOffset(label);
		pt.setAssemblyCode(instruction);
		pt.setPc(asm.getCounter());
		pt.setLabelAddress(asm.getLabelAddress());
		pt.setAddress(asm.getCounter()-1);
		pt.setPcRelativeAddr(pcoffset);
		//LSBCheck(pcoffset,0x1ff,9);
		//pcoffset = pcoffset & 0x1ff;
		//int byteEmit = opcode | r0 | offset;
		int byteEmit = opcode | r0;
		asm.emitByte(byteEmit);
		pt.setBinstr(byteEmit, true);
		pt.setHexstr(byteEmit);
		asm.getProgTable().add(pt);
	}
	
	/*
	 * Hanles LD, LDI, LEA, ST, STI
	 * 
	 * Warning!
	 * r8,r9,r10 have bugs:
	 * 
	 * E.g st r10 x-main
	 * r10 = 1010
	 * opcode = 0011
	 * 	0011 << 12 = 0011000000000000
		1010 << 9 =  0001010000000000
		------------------------------
             		 0011010000000000
        ------------------------------
        0011010000000000 = 3400
        r0 becomes 010 = 2! (wrong)
        Do not use r8,r9,r10
        
        Try running reg.ltc to find 
        out the results. r10 is 
        interpretted as r2.
	 * 
	 */
	public static void ld_Ldi_lea_leahex_st_sti(int opcode, String[] instruction, Parser asm) throws LC3AssemblerException {
		int r0 = asm.getRegMap().get(instruction[1]);
		opcode = opcode << 12;
		r0 = r0 << 9;
		String label = instruction[2];
		/*
		 * 0 = label
		 * 1 = type
		 * 2 = value
		 * 3 = displacement
		 */
		ProgramTable pt = new ProgramTable();
		int pcoffset = asm.getDataOffset(label);
		pt.setPcRelativeAddr(pcoffset);
		//LSBCheck(pcoffset,0x1ff,9);
		//pcoffset = pcoffset & 0x1ff;
		//int byteEmit = opcode | r0 | offset;
		int byteEmit = opcode | r0;
		asm.emitByte(byteEmit);
		pt.setAddress(asm.getCounter()-1);
		pt.setLabelAddress(-1);
		pt.setAssemblyCode(instruction);
		pt.setPc(asm.getCounter());
		pt.setBinstr(byteEmit, true);
		pt.setHexstr(byteEmit);
		asm.getProgTable().add(pt);
	}
	
	/*
	 * LDR R3 R0, 2  ;R3 <- mem[r0 + 2]
	 * LDR R3 R4 -2	 ;R3 <- mem[r4 - 2]
	 * 
	 * Handles LDR, STR
	 */
	public static void ldr_str(int opcode, String[] instruction, Parser asm) throws LC3AssemblerException {
		int r0 = asm.getRegMap().get(instruction[1]);
		int r1 = asm.getRegMap().get(instruction[2]);
		int offset = Integer.parseInt(instruction[3]);
		LSBCheck(offset,0x3f,6);
		offset = offset & 0x3f;
		opcode = opcode << 12;
		r0 = r0 << 9;
		r1 = r1 << 6;
		int byteEmit = opcode | r0 | r1 | offset;
		asm.emitByte(byteEmit);
		
		ProgramTable pt = new ProgramTable();
		pt.setAddress(asm.getCounter()-1);
		pt.setLabelAddress(-1);
		pt.setAssemblyCode(instruction);
		pt.setPc(asm.getCounter());
		pt.setBinstr(byteEmit, true);
		pt.setHexstr(byteEmit);
		asm.getProgTable().add(pt);
	}
	
	public static void not(int opcode, String[] instruction, Parser asm) {
		int r0 = asm.getRegMap().get(instruction[1]);
		int r1 = asm.getRegMap().get(instruction[2]);
		opcode = opcode << 12;
		r0 = r0 << 9;
		r1 = r1 << 6;
		int byteEmit = opcode | r0 | r1 | 0b111111;
		asm.emitByte(byteEmit);
		
		ProgramTable pt = new ProgramTable();
		pt.setAddress(asm.getCounter()-1);
		pt.setLabelAddress(-1);
		pt.setAssemblyCode(instruction);
		pt.setPc(asm.getCounter());
		pt.setBinstr(byteEmit, true);
		pt.setHexstr(byteEmit);
		asm.getProgTable().add(pt);
	}
	
	public static void trap(int b, String[] instruction, Parser asm) {
		int opcode = 0b1111 << 12;
		int byteEmit = opcode | b;
		asm.emitByte(byteEmit);
		
		ProgramTable pt = new ProgramTable();
		pt.setAddress(asm.getCounter()-1);
		pt.setLabelAddress(-1);
		pt.setAssemblyCode(instruction);
		pt.setPc(asm.getCounter());
		pt.setBinstr(byteEmit, true);
		pt.setHexstr(byteEmit);
		asm.getProgTable().add(pt);
	}
	
	public static void outr(int b, String[] instruction, Parser asm) {
		int opcode = 0b1111 << 12;
		int r0 = asm.getRegMap().get(instruction[1]);
		r0 = r0 << 9;
		int byteEmit = opcode | r0 | b;
		asm.emitByte(byteEmit);
		
		ProgramTable pt = new ProgramTable();
		pt.setAddress(asm.getCounter()-1);
		pt.setLabelAddress(-1);
		pt.setAssemblyCode(instruction);
		pt.setPc(asm.getCounter());
		pt.setBinstr(byteEmit, true);
		pt.setHexstr(byteEmit);
		asm.getProgTable().add(pt);
	}
	
	/* enhancement:
	 * movr r0 1
	 * 1111 000 001 000001
	 */
	public static void movr(int b, String[] instruction, Parser asm) {
		int opcode = 0b1111 << 12;
		int r0 = asm.getRegMap().get(instruction[1]);
		int r1 = asm.getRegMap().get(instruction[2]);
		r0 = r0 << 9;
		r1 = r1 << 6;
		int byteEmit = opcode | r0 | r1 | b;
		asm.emitByte(byteEmit);
		
		ProgramTable pt = new ProgramTable();
		pt.setAddress(asm.getCounter()-1);
		pt.setLabelAddress(-1);
		pt.setAssemblyCode(instruction);
		pt.setPc(asm.getCounter());
		pt.setBinstr(byteEmit, true);
		pt.setHexstr(byteEmit);
		asm.getProgTable().add(pt);
	}
	
	/* enhancement: using 6 bits to store immediate
	 * movi r0 2
	 * 1111 000 000010 101
	 */
	public static void movi(int b, String[] instruction, Parser asm) {
		int opcode = 0b1111 << 12;
		int r0 = asm.getRegMap().get(instruction[1]);
		int imm5 = Integer.parseInt(instruction[2]);
		asm.check("Out of range: " + imm5, (-16 <= imm5) && (imm5 <= 15));
		r0 = r0 << 9;
		imm5 = imm5 & 0b11111;	//two's complement.
		imm5 = imm5 << 4;
		int byteEmit = opcode | r0 | imm5 | b;
		asm.emitByte(byteEmit);
		
		ProgramTable pt = new ProgramTable();
		pt.setAddress(asm.getCounter()-1);
		pt.setLabelAddress(-1);
		pt.setAssemblyCode(instruction);
		pt.setPc(asm.getCounter());
		pt.setBinstr(byteEmit, true);
		pt.setHexstr(byteEmit);
		asm.getProgTable().add(pt);
	}
	
	/* enhancement:
	 * movr r0 1
	 * 1111 000 001 000001
	 */
	public static void swapr(int b, String[] instruction, Parser asm) {
		int opcode = 0b1111 << 12;
		int r0 = asm.getRegMap().get(instruction[1]);
		int r1 = asm.getRegMap().get(instruction[2]);
		r0 = r0 << 9;
		r1 = r1 << 6;
		int byteEmit = opcode | r0 | r1 | b;
		asm.emitByte(byteEmit);
		
		ProgramTable pt = new ProgramTable();
		pt.setAddress(asm.getCounter()-1);
		pt.setLabelAddress(-1);
		pt.setAssemblyCode(instruction);
		pt.setPc(asm.getCounter());
		pt.setBinstr(byteEmit, true);
		pt.setHexstr(byteEmit);
		asm.getProgTable().add(pt);
	}
	
	public static void rti(String[] instruction, Parser asm) {
		int byteEmit = 0b1 << 15;
		asm.emitByte(byteEmit);
		
		ProgramTable pt = new ProgramTable();
		pt.setAddress(asm.getCounter()-1);
		pt.setLabelAddress(-1);
		pt.setAssemblyCode(instruction);
		pt.setPc(asm.getCounter());
		pt.setBinstr(byteEmit, true);
		pt.setHexstr(byteEmit);
		asm.getProgTable().add(pt);
	}
	
	/*
	 * I'm using res opcode 0xD (13) for
	 * loading a program who's file string
	 * index starts at reg r0. Reg r1 stores
	 * the location/address WHERE the program will
	 * be loaded to + offset (r3)
	 * 
	 * E.g 
	 * 		res r0 r1 r3 IN
	 * 		res r0 r1 r3 OUT
	 * 
	 * Where in reads from the file
	 * and OUT writes to a file.
	 * When writing to file:
	 * 		r0 = filename
	 * 		r1 = start index
	 * 		r3 = end index
	 */
	public static void res(String[] instruction, Parser asm) {
		int r0 = asm.getRegMap().get(instruction[1]);
		int r1 = asm.getRegMap().get(instruction[2]);
		int r2 = asm.getRegMap().get(instruction[3]);
		int b = 0; 	//default is read from file
					//bits 11-13 (inclusive)
		String condition = instruction[4];
		switch(condition) {
			case "IN": // RES R0 R1 R3 IN
				b = 0;
			break;
			
			case "OUT": // RES R0 R1 R3 OUT
				b = 1 << 3;
			break;
			
			case "VRAM": // RES R0 R1 R3 VRAM
				b = 2 << 3;
			break;
			
			case "SCALE": // RES R0 R1 R3 SCALE
				b = 3 << 3;
			break;
			
			case "RAND": // RES R0 R1 R3 RAND
				b = 4 << 3;
			break;
			
			case "RANDB": // RES R0 R1 R3 RANDB
				b = 5 << 3;
			break;
			
			default:
				b = 0;
			break;
		}
			
		r0 = r0 << 9;
		r1 = r1 << 6;
		int byteEmit = 0b1101 << 12;
		byteEmit = byteEmit | r0 | r1 | b | r2;
		asm.emitByte(byteEmit);
		
		ProgramTable pt = new ProgramTable();
		pt.setAddress(asm.getCounter()-1);
		pt.setLabelAddress(-1);
		pt.setAssemblyCode(instruction);
		pt.setPc(asm.getCounter());
		pt.setBinstr(byteEmit, true);
		pt.setHexstr(byteEmit);
		asm.getProgTable().add(pt);
	}
	
	//No longer needed
	private static void LSBCheck(int x, int mask, int bits) throws LC3AssemblerException {
		String[] assembleroptions=System.getenv("assembleroptions").split(",");
		String option3 = assembleroptions[2];
		if(option3.equals("noLSBCheck")) return;
		
		/*
		 * if x > 0 then LSB of (x & mask) >> bits - 1
		 * should not be 1
		 */
		if(x > 0)
			if((x & mask) >> (bits - 1) == 1)
				throw new LC3AssemblerException("Positive number will be sign-extended and treated as negative " + 
						x);
		
		if(Math.abs(x) > mask)
			throw new LC3AssemblerException("Cannot fit into offset"+
					String.format("%04X",x));
	}

}
