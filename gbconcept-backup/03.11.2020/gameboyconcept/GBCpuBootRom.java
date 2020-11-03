package gameboyconcept;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cartridgeLogo.Logo;
import gameboyconcept.gblogger.Logit;
import interfaces.CPUBusInterface;
import util.Bit;

public class GBCpuBootRom implements CPUBusInterface{
	
	private GBBus bus;
	private Register reg;
	private int data;
	private boolean cbExt;
	
	public GBCpuBootRom(GBBus bus, Register reg) {
		this.bus = bus;
		this.reg = reg;
		data = 0;
		reg.setPC(0);
	}

	@Override
	public int cpuR(int addr, boolean rOlny) {
		return bus.cpuR(addr, rOlny);
	}

	@Override
	public void cpuWr(int addr, int data) {
		bus.cpuWr(addr, data);
	}
	
	/*
	 * The GB Nintendo logo address
	 * starts at address A8 (168)
	 * in it's boot rom.
	 * 
	 * The CARTRIDGE must have EXACTLY
	 * the same data at it's own ROM
	 * starting at address 104 (260).
	 */
	public void loadLogo() {
		char start = 0x0104;
		for(int i = 0; i < Logo.ninLOGO.length; i++)
			bus.cpuWr(start++, Logo.ninLOGO[i]);
	}
	
	public void loadGBBootRom(File file) throws IOException {
		InputStream input = new FileInputStream(file);  
	    DataInputStream inst = new DataInputStream(input);
	    int seek=reg.getPC();
	    while((inst.available())>0) {
	    	/*
	    	 * Remember byte is SIGNED
	    	 * so use fetchUnsigned() or
	    	 * fetchSigned() depending
	    	 * on whether you need a signed
	    	 * value or unsigned value.
	    	 * 
	    	 * Example, for
	    	 * LD A, n  ;n is unsigned (I think)
	    	 * JR NZ n  ;n is signed (I think)
	    	 */
	    	byte b = inst.readByte();
	    	Logit.wrBootROM(b, seek);
	    	bus.cpuWr(seek++,b);
	    }
	    
	    input.close();
	    inst.close();
	}
	
	public void compute() {
		Bit.setbits(16);
		int line = 1;
		while(true) {
			
			if(reg.getPC() == 0x00c)
				Logit.wrCPUSTATE("---info: VRAM CLEARED---");
			else if(reg.getPC() == 0x001d )
				Logit.wrCPUSTATE("---info: Audio setup: completed---");
			else if(reg.getPC() == 0x0021  ) 
				Logit.wrCPUSTATE("---info: BG Palette: completed---");
			else if(reg.getPC() == 0x0034 ) 
				Logit.wrCPUSTATE("---info: Logo data WR to vram: completed---");
			else if(reg.getPC() == 0x0040 ) {
				Logit.wrCPUSTATE("---info: TM (R) WR to vram: completed---");
				bus.printRawToFile(Logit.LOGO_RAW,0x8000,0x81A0);
			}
			else if(reg.getPC() == 0x0055 ) {
				Logit.wrCPUSTATE("---info: BG TileMap: completed---");
				bus.printRawToFile(Logit.TILEMAP_RAW,0x9900,0x9930);
			}
			else if(reg.getPC() == 0x0062) {
				Logit.wrCPUSTATE("---info: Skipping logo scroll at 0x0062---");
				reg.setPC(0x00e0);
			}
			else if(reg.getPC() == 0x00fa) {
				/*
				 * JR NZ,$fe		; $00fa
				 * if Z flag == 0
				 * 		we are in a loop
				 * 		data match was a success
				 */
				if(reg.getF()==0)
					Logit.wrCPUSTATE("---info: SUCCESS---");
				else
					Logit.wrCPUSTATE("---info: FAILED ---");
				
				Logit.close();
				System.exit(0);
			}
			
			fetchUnsigned8();
			int opcode = data;
			reg.setOpcode(opcode);
			
			if(opcode == 0x0)
				Logit.error(this, "0x0 opcode");
		
			OpcodesInit.commands.get(opcode).opeation().run(this);
			if(opcode != 0xCB)
				Logit.wrCPUSTATE("Line " + line++ + ": Registers: " + reg.state());
		}
	}
	
	/************** Opcodes begin **********/
	
	public void unsupported() {
		Logit.error(this, "Not supported");
	}
	
	public void cbExt() {
		cbExt = true;
	}
	
	public void determineA() {
		if(cbExt) {
			HCB7C();
			cbExt = false;
		}
		else 
			H7C();	
			
	}
	
	public void determineB() {
		if(cbExt) {
			HCB11();
			cbExt = false;
		}	
		else 
			H11();
			
	}
	
	/*
	 * LD SP,nn
	 * LD SP $fffe
	 * 0x31
	 */
	public void H31() {
		Logit.info(this,  
				Integer.toHexString(data).toUpperCase() + ": LD SP,nn");
		fetchUnsigned16();
		reg.setSP(data);
		Logit.wrSTACK("SP Init --> " + Bit.hexStr(data) + " [" + data + "]");
	}
	
	/*
	 * XOR A
	 * 0xAF
	 */
	public void HAF() {
		reg.setA(reg.xor(reg.getA(), reg.getA()));
		Logit.info(this,  
				Integer.toHexString(data).toUpperCase() + ": XOR A");
	}
	
	private int swapBytes(int byteA, int byteB) {
		Logit.info(this, "Swap bytes");
		return byteB << 8 | byteA;
	}
	
	public void fetchUnsigned8() {
		data =  bus.cpuR(reg.getPC(), true) & 0xFF;
		reg.incrementPC();
	}
	
	public void fetchSigned8() {
		data =  bus.cpuR(reg.getPC(), true);
		reg.incrementPC();
	}
	
	private void fetchUnsigned16() {
		int byteA = bus.cpuR(reg.getPC(), true) & 0xFF;
		Logit.info(this,"byteA: " + Integer.toHexString(byteA).toUpperCase());
		reg.incrementPC();
		int byteB = bus.cpuR(reg.getPC(), true) & 0xFF;
		Logit.info(this,"byteB: " + Integer.toHexString(byteB).toUpperCase());
		reg.incrementPC();
		data = swapBytes(byteA, byteB);
		Logit.info(this,"DATA: " + Integer.toHexString(data).toUpperCase());
		Logit.info(this, "2-bytes: " + 
				Integer.toHexString(data).toUpperCase());
	}
	
	/*
	 * LD HL,$9fff
	 * LD HL, nn
	 * 0x21
	 */
	public void H21() {
		Logit.info(this,  
				Integer.toHexString(data).toUpperCase() + ": LD HL, nn");
		fetchUnsigned16();
		int b[] = Bit.hlbytes(data);
		int highByte = b[0];
		int lowByte = b[1];
		reg.setH(highByte);
		reg.setL(lowByte);
		int hl = reg.getHL();
		Logit.info(this,"HL = " + hl);
		Logit.info(this, "ldhlnn H:" + reg.getH());
		Logit.info(this, "ldhlnn L:" + reg.getL());
 	}
	
	/*
	 * LD (HL-),A
	 * LD (HLD), A
	 * LDD (HL), A
	 * 
	 * 0x32
	 */
	public void H32() {
		Logit.info(this,  
				Integer.toHexString(data).toUpperCase() + ": LDD (HL), A");
		int hl = reg.getHL();
		bus.cpuWr(hl, reg.getA());
		hl--;
		int b[] = Bit.hlbytes(hl);
		int highByte = b[0];
		int lowByte = b[1];
		reg.setH(highByte);
		reg.setL(lowByte);
		Logit.info(this,"HL = " + hl);
		Logit.info(this, "lddhla H:" + reg.getH());
		Logit.info(this, "lddhla L:" + reg.getL());
	}
	
	/*
	 * BIT 7,H
	 * 	
	 * 0xCB 0x7C
	 * 
	 * if bit 7 of H is 1
	 * then set z to 0
	 */
	public void HCB7C() {
		Logit.info(this,  
				Integer.toHexString(data).toUpperCase() + ": BIT 7,H");
		Bit.setbits(Bit.b8);
		Logit.info(this, "bit7H valstr = " + Bit.binStr(reg.getH(),true));
		Logit.info(this, "bit7H valorig = " + Integer.toBinaryString(reg.getH()));
		Logit.info(this, "bit7H bit 7 set? " + Bit.isSetInt(reg.getH(), Bit.b1));
		reg.setN(false);
		reg.setHC(true);
		reg.setZ(!Bit.isSetInt(reg.getH(), Bit.b1));
		int flags = reg.getF();
		Logit.info(this, "FLAGS: " + Bit.binStr(flags,true));
		
	}
	
	/* opcode 20:
	 * JR NZ, n
	 * if z == 0
	 * 		pc += n
	 * else
	 * 		resume
	 */
	public void H20() {
		//Need fetch() to get next signed byte
		fetchSigned8();
		Logit.info(this,  
				Integer.toHexString(data).toUpperCase() + ": JR NZ, " + data);
		int flags = reg.getF();
		Logit.info(this, "FLAGS: " + Bit.binStr(flags,true));
		if(!(flags < 0 || ((flags & 0xFF) >> 7) == 1)) {
			reg.setPC(reg.getPC() + data);
			Logit.info(this, "Jumping to instruction at PC: " + reg.getPC());
		} 
		else 
			Logit.info(this, "No jump");
	}
	
	//opcode 0E
	public void H0E() {
		fetchUnsigned8();
		reg.setC(data);
	}
	
	public void H3E() {
		fetchUnsigned8();
		reg.setA(data);
	}
	
	/*
	 * opcode E2
	 * LD ($FF00+C),A
	 */
	public void HE2() {
		bus.cpuWr(0xFF00 + reg.getC(), reg.getA());
	}
	
	//opcode 0C
	public void H0C() {
		reg.setC(reg.incrementReg(reg.getC()));
	}
	
	//opcode 04
	public void H04() {
		reg.setB(reg.incrementReg(reg.getB()));
	}
	
	//opcode 24
	public void H24() {
		reg.setH(reg.incrementReg(reg.getH()));
	}
	
	//opcode 77
	public void H77() {
		bus.cpuWr(reg.getHL(), reg.getA());
	}
	
	//opcode E0
	public void HE0() {
		fetchUnsigned8();
		bus.cpuWr(0xFF00 + data, reg.getA());
	}
	
	//opcode 11
	public void H11() {
		Logit.info(this,  
				Integer.toHexString(data).toUpperCase() + ": LD DE, nn");
		fetchUnsigned16();
		int b[] = Bit.hlbytes(data);
		int highByte = b[0];
		int lowByte = b[1];
		reg.setD(highByte);
		reg.setE(lowByte);
		int de = reg.getDE();
		Logit.info(this,"DE = " + de);
		Logit.info(this, "lddenn D:" + reg.getD());
		Logit.info(this, "lddenn E:" + reg.getE());
 	}
	
	//opcode 1A
	public void H1A() {
		reg.setA(bus.cpuRUnsigned8(reg.getDE()));
	}
	
	/*
	 * opcode CD:
	 * Call nn
	 * Push 2-bytes onto stack
	 * SP--
	 * 
	 */
	public void HCD() {
		fetchUnsigned16();
		int b[] = Bit.hlbytes(reg.getPC());
		int highByte = b[0];
		int lowByte = b[1];
		reg.setSP(reg.getSP() - 1);
		bus.cpuWr(reg.getSP(), highByte);
		Logit.wrSTACK("CD PUSH " + Bit.hexStr(highByte));
		Logit.wrSTACK("SP -1 --> " + Bit.hexStr(reg.getSP()) + " [" + reg.getSP() + "]");
		reg.setSP(reg.getSP() - 1);
		bus.cpuWr(reg.getSP(), lowByte);
		Logit.wrSTACK("CD PUSH " + Bit.hexStr(highByte));
		Logit.wrSTACK("SP -1 --> " + Bit.hexStr(reg.getSP()) + " [" + reg.getSP() + "]");
		reg.setPC(data);
	}
	
	//opcode 13:
	public void H13() {
		int de = reg.getDE();
		de++;
		int b[] = Bit.hlbytes(de);
		int highByte = b[0];
		int lowByte = b[1];
		reg.setD(highByte);
		reg.setE(lowByte);
	}
	
	//opcode 7B:
	public void H7B() {
		reg.setA(reg.getE());
	}
	
	//opcode FE:
	public void HFE() {
		fetchUnsigned8();
		reg.HFE(reg.getA(), data);
	}
	
	//opcode 06:
	public void H06() {
		fetchUnsigned8();
		reg.setB(data);
	}
	
	//opcode 22:
	public void H22() {
		Logit.info(this,  
				Integer.toHexString(data).toUpperCase() + ": LDD (HL++), A");
		int hl = reg.getHL();
		bus.cpuWr(hl, reg.getA());
		hl++;
		int b[] = Bit.hlbytes(hl);
		int highByte = b[0];
		int lowByte = b[1];
		reg.setH(highByte);
		reg.setL(lowByte);
		Logit.info(this,"HL = " + hl);
		Logit.info(this, "lddhla H:" + reg.getH());
		Logit.info(this, "lddhla L:" + reg.getL());
	}
	
	//opcode 23:
	public void H23() {
		int hl = reg.getHL();
		hl++;
		int b[] = Bit.hlbytes(hl);
		int highByte = b[0];
		int lowByte = b[1];
		reg.setH(highByte);
		reg.setL(lowByte);
	}
	
	//opcode 05:
	public void H05() {
		reg.setB(reg.decrementReg(reg.getB()));
	}
	
	//opcode 3D:
	public void H3D() {
		reg.setA(reg.decrementReg(reg.getA()));
	}
	
	//opcode 0D:
	public void H0D() {
		reg.setC(reg.decrementReg(reg.getC()));
	}
	
	//opcode 1D
	public void H1D() {
		reg.setE(reg.decrementReg(reg.getE()));
	}
	
	//opcode 15
	public void H15() {
		reg.setD(reg.decrementReg(reg.getD()));
	}
	
	//opcode EA: 
	public void HEA() {
		fetchUnsigned16();
		bus.cpuWr(data, reg.getA());
	}
	
	/* opcode 28:
	 * JR Z n
	 * if Z == 1
	 * 		pc += n
	 * else
	 * 		resume
	 */
	public void H28() {
		//Need fetch() to get next signed byte
		fetchSigned8();
		Logit.info(this,  
				Integer.toHexString(data).toUpperCase() + ": JR NZ, " + data);
		int flags = reg.getF();
		Logit.info(this, "FLAGS: " + Bit.binStr(flags,true));
		if(flags < 0 || ((flags & 0xFF) >> 7) == 1) {
			reg.setPC(reg.getPC() + data);
			Logit.info(this, "Jumping to instruction at PC: " + reg.getPC());
		} 
		else {
			Logit.info(this, "No jump");
		}
	}
	
	//opcode 2E:
	public void H2E() {
		fetchUnsigned8();
		reg.setL(data);
	}
	
	/*
	 * opcode 18
	 * JR n
	 * 
	 * Jump to pc + n
	 */
	public void H18() {
		fetchSigned8();
		reg.setPC(reg.getPC() + data);
	}
	
	//opcode 67
	public void H67() {
		reg.setH(reg.getA());
	}
	
	//opcode 57
	public void H57() {
		reg.setD(reg.getA());
	}
	
	//opcode 1E
	public void H1E() {
		fetchUnsigned8();
		reg.setE(data);
	}
	
	//opcode F0
	public void HF0() {
		fetchUnsigned8();
		reg.setA(bus.cpuR(0xFF00 + data, true));
	}
	
	//opcode 7C
	public void H7C() {
		reg.setA(reg.getH());
	}
	
	//opcode 90
	public void H90() {
		reg.setA(reg.subBytes(reg.getA(), reg.getB()));
	}
	
	//opcode 16
	public void H16() {
		fetchUnsigned8();
		reg.setD(data);
	}
	
	//opcode 4F
	public void H4F() {
		reg.setC(reg.getA());
	}
	
	/*
	 * opcode C5:
	 * Push BC
	 * SP--
	 */
	public void HC5() {
		int bc = reg.getBC();
		int b[] = Bit.hlbytes(bc);
		int highByte = b[0];
		int lowByte = b[1];
		reg.setSP(reg.getSP() - 1);
		bus.cpuWr(reg.getSP(), highByte);
		Logit.wrSTACK("C5 PUSH " + Bit.hexStr(highByte));
		Logit.wrSTACK("SP -1 --> " + Bit.hexStr(reg.getSP()) + " [" + reg.getSP() + "]");
		reg.setSP(reg.getSP() - 1);
		bus.cpuWr(reg.getSP(), lowByte);
		Logit.wrSTACK("C5 PUSH " + Bit.hexStr(highByte));
		Logit.wrSTACK("SP -1--> " + Bit.hexStr(reg.getSP()) + " [" + reg.getSP() + "]");
	}
	
	/*
	 * opcode CB11:
	 * RL C
	 */
	public void HCB11() {
		int lsb = reg.isSetCC() ? 1 : 0;
		reg.setCC(reg.getC() < 0 || ((reg.getC() & 0xFF) >> 7) == 1);
		reg.setN(false);
		reg.setHC(false);
		int result = ((reg.getC() << 1) & 0xFF) | lsb;
		reg.setZ(result == 0);
		reg.setC(result);
		reg.getF();
	}
	
	/* opcode 17:
	 * RLA
	 */
	public void H17() {
		int lsb = reg.isSetCC() ? 1 : 0;
		reg.setCC(reg.getA() < 0 || ((reg.getA() & 0xFF) >> 7) == 1);
		reg.setN(false);
		reg.setHC(false);
		int result = ((reg.getA() << 1) & 0xFF) | lsb;
		reg.setZ(result == 0);
		reg.setA(result);
		reg.getF();
	}
	
	/*
	 * opcode C1: 
	 * POP BC
	 * SP++
	 */
	public void HC1() {
		int lowByte = bus.cpuR(reg.getSP(), true);
		reg.setC(lowByte);
		reg.setSP(reg.getSP() + 1);
		Logit.wrSTACK("C1 POP " + Bit.hexStr(lowByte));
		Logit.wrSTACK("SP +1 --> " + Bit.hexStr(reg.getSP()) + " [" + reg.getSP() + "]");
		int highByte = bus.cpuR(reg.getSP(), true);
		reg.setB(highByte);
		reg.setSP(reg.getSP() + 1);
		Logit.wrSTACK("C1 POP " + Bit.hexStr(highByte));
		Logit.wrSTACK("SP +1 --> " + Bit.hexStr(reg.getSP()) + " [" + reg.getSP() + "]");
	}
	
	/*
	 * opcode C9: 
	 * RET
	 */
	public void HC9() {
		int lowByte = bus.cpuR(reg.getSP(), true);
		reg.setSP(reg.getSP() + 1);
		Logit.wrSTACK("C9 POP " + Bit.hexStr(lowByte));
		Logit.wrSTACK("SP +1 --> " + Bit.hexStr(reg.getSP()) + " [" + reg.getSP() + "]");
		int highByte = bus.cpuR(reg.getSP(), true);
		reg.setSP(reg.getSP() + 1);
		Logit.wrSTACK("C9 POP " + Bit.hexStr(highByte));
		Logit.wrSTACK("SP +1 --> " + Bit.hexStr(reg.getSP()) + " [" + reg.getSP() + "]");
		reg.setPC(highByte << 8 | lowByte);
		Logit.info(this,"RET: Jumping to " + reg.getPC());
	}
	
	//opcode BE: CP (HL)
	public void HBE() {
		reg.HFE(reg.getA(), bus.cpuR(reg.getHL(), true));
	}
	
	//opcode 7D:
	public void H7D() {
		reg.setA(reg.getL());
	}
	
	//opcode 78:
	public void H78() {
		reg.setA(reg.getB());
	}
	
	//opcode 86: ADD (HL)
	public void H86() {
		reg.addBytes(reg.getA(), bus.cpuR(reg.getHL(), true));
	}
}
