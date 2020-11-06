package gameboyconcept;

import java.io.FileWriter;
import java.io.IOException;

import gameboyconcept.gblogger.Logit;
import interfaces.CPUBusInterface;
import util.Bit;

public class GBBus implements CPUBusInterface {
	
	/*
	 * According to Youtube a GB's memory is
	 * addresseable from 0000 to FFFF which 
	 * is 64KB or 65536 ints of addresses.
	 */
	
	private int[] mem;
	private FF47ColourPaletteRegister dr;
	
	public int cpuRUnsigned8(int addr) {
		return mem[addr] & 0xFF;
	}
	
	@Override
	public int cpuR(int addr, boolean rOlny) {
		
		/*
		 * 0x0104 to 0x0133 contains
		 * the nintendo logo from the
		 * cartridge ROM (not bootstrap
		 * code file).
		 */
		
		if(addr >= 0x0104 && addr < 0x0134) 
			Logit.step("R " + "(" + Bit.hexStr(addr) + ") " +Bit.hexStr(mem[addr]));
		
		/*
		 * 0x00D8 t0 00DF is data for
		 * the (R) trademark. This is 
		 * inside the bootsrap code file.
		 */
		if(addr >= 0x00D8 && addr < 0x00E0) 
			Logit.step("R " + "(" + Bit.hexStr(addr) + ") " +Bit.hexStr(mem[addr]));
		
		if(addr >= 0x0000 && addr < 0x8000) {
			/*
			 * The rom
			 * 0x0000 to 0x7FFF
			 * 32KB
			 */
			Logit.info(this, "CPUint read from ROM: " + 
					Integer.toHexString(addr).toUpperCase() + ", " +
					Integer.toHexString(mem[addr]).toUpperCase());
			Logit.rROM(mem[addr], addr);
			return mem[addr];
		} else if (addr >= 0x8000 && addr < 0xA000) {
			/*
			 * The VRam
			 * 0x8000 to 0x9FFF
			 * 8KB
			 */
			Logit.info(this, "CPUint read from VRAM: " + 
					Integer.toHexString(addr).toUpperCase() + ", " +
					Integer.toHexString(mem[addr]).toUpperCase());
			Logit.rVRAM(mem[addr], addr);
			return mem[addr];
		} else if (addr >= 0xA000 && addr < 0xC000) {
			/*
			 * External Ram
			 * 0xA000 to 0xBFFF
			 * 8KB
			 */
			Logit.info(this, "CPUint read from EX RAM: " + 
					Integer.toHexString(addr).toUpperCase() + ", " +
					Integer.toHexString(mem[addr]).toUpperCase());
			return mem[addr];
		} else if (addr >= 0xC000 && addr < 0xE000) {
			/*
			 * Work Ram
			 * 0xC000 to 0xDFFF
			 * 8KB
			 */
			Logit.info(this, "CPUint read from W-RAM: " + 
					Integer.toHexString(addr).toUpperCase() + ", " +
					Integer.toHexString(mem[addr]).toUpperCase());
			return mem[addr];
		} else if (addr >= 0xE000 && addr < 0xFE00) {
			/*
			 * Copy of Work Ram
			 * except it is smaller
			 * by 512 ints.
			 * 0xE00 to 0xFDFF
			 * 8KB
			 */
			Logit.info(this, "CPUint read from Mirror RAM: " + 
					Integer.toHexString(addr).toUpperCase() + ", " +
					Integer.toHexString(mem[addr]).toUpperCase());
			return mem[addr];
		} else if (addr >= 0xFE00 && addr < 0xFEA0) {
			/*
			 * OAM Ram
			 * 0xFE00 to 0xFE9F
			 */
			Logit.info(this, "CPUint read from OAM: " + 
					Integer.toHexString(addr).toUpperCase() + ", " +
					Integer.toHexString(mem[addr]).toUpperCase());
			return mem[addr];
		} else if (addr >= 0xFEA0 && addr < 0xFF00) {
			/*
			 * Not used.
			 * 0xFEA0 to 0xFEFF
			 */
			Logit.warn(this, "CPUint read from UNUSED RAM area: " + 
					Integer.toHexString(addr).toUpperCase() + ", " +
					Integer.toHexString(mem[addr]).toUpperCase());
			return mem[addr];
		} else if (addr >= 0xFF00 && addr < 0xFF80) {
			/*
			 * Game control!
			 * For buttons, screen, sound.
			 * 0xFF00 to 0xFF7F
			 */
			
			/*
			 * MM Reg 0xFF47
			 */
			if(addr == FF47ColourPaletteRegister.ADDR) {
				
			}
			
			Logit.info(this, "CPUint read from GAME-CTRL: " + 
					Integer.toHexString(addr).toUpperCase() + ", " +
					Integer.toHexString(mem[addr]).toUpperCase());
			return mem[addr];
		} else if (addr >= 0xFF80 && addr < 0xFFFF) {
			/*
			 * High-speed Ram:
			 * FF80 to 0xFFFE
			 */
			Logit.info(this, "CPUint read from H-RAM: " + 
					Integer.toHexString(addr).toUpperCase() + ", " +
					Integer.toHexString(mem[addr]).toUpperCase());
			return mem[addr];
		} else if (addr == 0xFFFF) {
			/*
			 * Interrupt switch
			 */
			Logit.info(this, "CPUint read: Interrupt: " + 
					Integer.toHexString(addr).toUpperCase() + ", " +
					Integer.toHexString(mem[addr]).toUpperCase());
			return mem[addr];
		} else {
			//error
			Logit.error(this, "CPUint read error: " + 
					Integer.toHexString(addr).toUpperCase() + ", " +
					Integer.toHexString(mem[addr]).toUpperCase());
		}
		
		return 0;
	}
	
	public GBBus() {
		mem = new int[0xFFFF];
	}

	@Override
	public void cpuWr(int addr, int data) {
		if(addr >= 0x0000 && addr < 0x8000) {
			/*
			 * The rom
			 * 0x0000 to 0x7FFF
			 * 32KB
			 */
			Logit.info(this, "CPUint write to ROM: " + 
					Integer.toHexString(addr).toUpperCase() + ", " +
					Integer.toHexString(data).toUpperCase());
			Logit.wrROM(data, addr);
			mem[addr] =  data;
		} else if (addr >= 0x8000 && addr < 0xA000) {
			/*
			 * The VRam
			 * 0x8000 to 0x9FFF
			 * 8KB
			 */
			Logit.wrVRAM(data, addr);
			if(data != 0) {
				/*	write to non-zero data to 
					vramWrNonZero.log
				*/
				Logit.step(" Wr " + "(" + Bit.hexStr(addr) + ") " +Bit.hexStr(data));
				Logit.wrNonZeroVRAM(data, addr);
			}
			mem[addr] =  data;
		} else if (addr >= 0xA000 && addr < 0xC000) {
			/*
			 * External Ram
			 * 0xA000 to 0xBFFF
			 * 8KB
			 */
			Logit.info(this, "CPUint write to EX RAM: " + 
					Integer.toHexString(addr).toUpperCase()+ ", " +
					Integer.toHexString(data).toUpperCase());
			mem[addr] =  data;
		} else if (addr >= 0xC000 && addr < 0xE000) {
			/*
			 * Work Ram
			 * 0xC000 to 0xDFFF
			 * 8KB
			 */
			Logit.info(this, "CPUint write to W-RAM: " + 
					Integer.toHexString(addr).toUpperCase()+ ", " +
					Integer.toHexString(data).toUpperCase());
			mem[addr] =  data;
		} else if (addr >= 0xE000 && addr < 0xFE00) {
			/*
			 * Copy of Work Ram
			 * except it is smaller
			 * by 512 ints.
			 * 0xE00 to 0xFDFF
			 * 8KB
			 */
			Logit.info(this, "CPUint write to Mirror RAM: " + 
					Integer.toHexString(addr).toUpperCase()+ ", " +
					Integer.toHexString(data).toUpperCase());
			mem[addr] =  data;
		} else if (addr >= 0xFE00 && addr < 0xFEA0) {
			/*
			 * OAM Ram
			 * 0xFE00 to 0xFE9F
			 */
			Logit.info(this, "CPUint write to OAM: " + 
					Integer.toHexString(addr).toUpperCase()+ ", " +
					Integer.toHexString(data).toUpperCase());
			mem[addr] =  data;
		} else if (addr >= 0xFEA0 && addr < 0xFF00) {
			/*
			 * Not used.
			 * 0xFEA0 to 0xFEFF
			 */
			Logit.warn(this, "CPUint write to UNUSED RAM area: " + 
					Integer.toHexString(addr).toUpperCase()+ ", " +
					Integer.toHexString(data).toUpperCase());
			mem[addr] =  data;
		} else if (addr >= 0xFF00 && addr < 0xFF80) {
			/*
			 * Game control!
			 * For buttons, screen, sound.
			 * 0xFF00 to 0xFF7F
			 */
			
			/*
			 * MM Reg 0xFF47
			 */
			if(addr == FF47ColourPaletteRegister.ADDR) {
				dr = new FF47ColourPaletteRegister();
				Logit.info(this, "Colour3: " + dr.getColour(3));
				Logit.info(this, "Colour2: " + dr.getColour(2));
				Logit.info(this, "Colour1: " + dr.getColour(1));
				Logit.info(this, "Colour0: " + dr.getColour(0));
			}
			
			Logit.info(this, "CPUint write to GAME-CTRL: " + 
					Integer.toHexString(addr).toUpperCase()+ ", " +
					Integer.toHexString(data).toUpperCase());
			mem[addr] =  data;
		} else if (addr >= 0xFF80 && addr < 0xFFFF) {
			/*
			 * High-speed Ram:
			 * FF80 to 0xFFFE
			 */
			Logit.info(this, "CPUint write to H-RAM: " + 
					Integer.toHexString(addr).toUpperCase()+ ", " +
					Integer.toHexString(data).toUpperCase());
			mem[addr] =  data;
		} else if (addr == 0xFFFF) {
			/*
			 * Interrupt switch
			 */
			Logit.info(this, "CPUint write: Interrupt: " + 
					Integer.toHexString(addr).toUpperCase()+ ", " +
					Integer.toHexString(data).toUpperCase());
			mem[addr] =  data;
		} else {
			//error
			Logit.error(this, "CPUint write error: " + 
					Integer.toHexString(addr).toUpperCase()+ ", " +
					Integer.toHexString(data).toUpperCase());
		}
		
	}
	
	public void printLogoData() {
		for(int i = 0x8000; i < 0x81A0; i++) {
			boolean wrap = (i - 0x8000) % 0x10 == 0;
			if(wrap) {
				System.out.println();
				System.out.printf("%04X: ",i);
			}
			int data = cpuR(i, true);
			System.out.printf("%02X", data);
		}
		System.out.println();
	}
	
	public void printRawToFile(FileWriter fw, int x, int y) {
		for(int i = x; i < y; i++)
			try {
				fw.write(cpuR(i, true));
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		try {
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	

}
