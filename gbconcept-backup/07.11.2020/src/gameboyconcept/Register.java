package gameboyconcept;

import gameboyconcept.gblogger.Logit;

/*
 * 	The carry flag is set if there's 
 * 	an overflow from the 7th bit or
 * 	bit 15th for 2-byte added.

	The half carry flag is set if there's 
	an overflow from the 3rd into the 4th bit.
	or 7th to 8th bit for 2-byte adds.
	
	Search on stackoverflow:
	GBZ80: How does LD HL,(SP+e) affect H and C flags?
	
	For subtraction:
	Borrow: when bit 7 needs to borrow or
	when bit 15 needs to borrow for 2-byte sub.
	Halfborrow: when bit 3 borrows from bit 4
	of when bit 7 needs to borrow from bit 8. 
 */

public class Register {
	private int A,B,C,D,E,F,H,L;
	private int SP, PC, opcode;
	private char[] flags;
	public static final short Z = 0;
	public static final short N = 1;
	public static final short HC = 2;
	public static final short CC = 3;
	
	public void setZ(boolean set) { flags[Z] = set ? '1' : '0'; }
	public void setN(boolean set) { flags[N] = set ? '1' : '0'; }
	public void setHC(boolean set) { flags[HC] = set ? '1' : '0'; }
	public void setCC(boolean set) { flags[CC] = set ? '1' : '0'; }
	public boolean isSetCC() { return flags[CC] == '1'; }
	public void setOpcode(int op) { opcode = op; }
    public int getOpcode() { return opcode; }
	
	public Register() {
		flags = new char[8];
		flags[0] = '0';
		flags[1] = '0';
		flags[2] = '0';
		flags[3] = '0';
		flags[4] = '0';
		flags[5] = '0';
		flags[6] = '0';
		flags[7] = '0';
	}
	
	public void incrementPC() {
		PC++;
	}
	
	public int getAF() {
		return A << 8 | F;
	}
	
	public int getHL() {
		return H << 8 | L;
	}
	
	public int getDE() {
		return D << 8 | E;
	}
	
	public int getBC() {
		return B << 8 | C;
	}
	
	public int getA() {
		return A;
	}
	public void setA(int a) {
		A = a;
	}
	public int getB() {
		return B;
	}
	public void setB(int b) {
		B = b;
	}
	public int getC() {
		return C;
	}
	public void setC(int c) {
		C = c;
	}
	public int getD() {
		return D;
	}
	public void setD(int d) {
		D = d;
	}
	public int getE() {
		return E;
	}
	public void setE(int e) {
		E = e;
	}
	public int getF() {
		F = Integer.parseInt(String.valueOf(flags),2);
		return F;
	}
	public void setF(int f) {
		F = f;
	}
	public int getH() {
		return H;
	}
	public void setH(int h) {
		H = h;
	}
	public int getL() {
		return L;
	}
	public void setL(int l) {
		L = l;
	}
	public int getSP() {
		return SP;
	}
	public void setSP(int sP) {
		SP = sP;
	}
	public int getPC() {
		return PC;
	}
	public void setPC(int pC) {
		PC = pC;
	}
	
	public int addBytes(int byteA, int byteB) {
		setZ(((byteA + byteB) & 0xFF) == 0x0);
		setN(false);
		setHC(((byteA & 0xF) + (byteB & 0xF))  > 0xF);
		setCC(((byteA + byteB) & 0xFF) > 0xFF);
		getF();
		return (byteA + byteB) & 0xFF;
	}
	
	public int incrementReg(int reg) {
		setZ(((reg + 1) & 0xFF) == 0x0);
		setN(false);
		setHC(((reg & 0xF) + (1 & 0xF))  > 0xF);
		getF();
		return (reg + 1) & 0xFF;
	}
	
	public int decrementReg(int reg) {
		setZ(((reg - 1) & 0xFF) == 0x0);
		setN(true);
		setHC(((reg & 0xF) - (1 & 0xF))  > 0xF);
		getF();
		return (reg - 1) & 0xFF;
	}
	
	public int subBytes(int byteA, int byteB) {
		setZ(((byteA - byteB) & 0xFF) == 0x0);
		setN(true);
		setHC(((byteA & 0xF) < (byteB & 0xF)));
		setCC(byteA < byteB);
		getF();
		return (byteA - byteB) & 0xFF;
	}
	
	public void HFE(int byteA, int byteB) {
		setZ(((byteA - byteB) & 0xFF) == 0x0);
		setN(true);
		setHC(((byteA & 0xF) > (byteB & 0xF)));
		setCC(byteA > byteB);
		getF();
	}
	
	public int xor(int byteA, int byteB) {
		setZ(((byteA ^ byteB) & 0xFF) == 0x0);
		setN(false);
		setHC(false);
		setCC(false);
		getF();
		
		return (byteA ^ byteB) & 0xFF;
	}
	
	public static void printRes(int res) {
		System.out.println("0x" + Integer.toHexString(res).toUpperCase() +
				", " + Integer.toBinaryString(res) + ", " +
				res + "\n");
	}
	
	public static void check(int byteA) {
		if(byteA < 0)
			return;
		if ( (byteA & 0x0100) == 0x0100)
			Logit.error(Register.class, Integer.toHexString(byteA).toUpperCase() + 
					" is larger than byte");
		
	}
	
	public String state() {
		return String.format("Opcode=%04x, A=%04x, BC=%04x, DE=%04x, HL=%04x, SP=%04x, PC=%04x, %s", opcode, A, getBC(), getDE(), getHL(), getSP(), getPC(), flagsStr());
	}
	
    public String flagsStr() {
        StringBuilder result = new StringBuilder();
        result.append(flags[Z] == '1' ? 'Z' : '-');
        result.append(flags[N] == '1' ? 'N' : '-');
        result.append(flags[HC] == '1' ? 'H' : '-');
        result.append(flags[CC] == '1' ? 'C' : '-');
        result.append("----");
        return result.toString();
    }

}
