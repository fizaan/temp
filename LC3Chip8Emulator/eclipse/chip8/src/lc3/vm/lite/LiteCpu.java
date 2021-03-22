package lc3.vm.lite;

import static lc3.vm.util.Vars.MAX_MEM;
import static lc3.vm.util.Vars.SIZE;
import static lc3.vm.util.Vars.COND;
import static lc3.vm.util.Vars.POS;
import static lc3.vm.util.Vars.NEG;
import static lc3.vm.util.Vars.ZER;
import static lc3.vm.util.Vars.input;
import static lc3.vm.util.Vars.TROUBLE_SHOOT;
import static lc3.vm.util.Vars.PRINT_HEX;
import static lc3.vm.util.Vars.running;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import lc3.vm.exceptions.LC3VMException;
import lc3.vm.lite.Disassembler;
import lc3.vm.util.Vars;
import lc3.vm.util.bit.Bit;

public class LiteCpu {
	
	/*****************
	 * See LC3-ISA.txt
	 * for reference
	 */
	private int[] mem;
	private int[] reg;
	private int pc;
	private static int abs;
	private EmulatorLite emu;
	private char startAddress,endAddress;
	
	public LiteCpu(EmulatorLite emu) {
		mem = new int[MAX_MEM];
		reg = new int[SIZE];
		pc = 0x3000;
		this.emu = emu;
	}
	
	public int[] mem() { return mem; }
	
	public static int abs() { return abs; }
	
	public void start() throws LC3VMException, IOException {
		Disassembler dis = new Disassembler();
		Bit.setbits(16);
		while(running) {
			switch(mem[pc] >> 12) {
				case Vars.OP_BR:
				case Vars.OP_JSR:
				case Vars.OP_LD:
				case Vars.OP_LDI:
				case Vars.OP_LEA:
				case Vars.OP_ST:
				case Vars.OP_STI:
					abs = mem[pc+1];
				break;
			}
			
			if(TROUBLE_SHOOT) {
				int curpc = pc;
				dis.disassemble(mem[pc], reg, curpc);
				Bit.sleep(200);
			}
			execute(mem[pc++]);
		}
	}
	
	public void loadImage(File file) throws IOException, LC3VMException {
		if(file.getName().equals("q"))
			System.exit(0);
		if(!file.getName().endsWith(".obj"))
			if(!file.getName().endsWith(".bin"))
				throw new LC3VMException("Object file not valid: " + file.getName());
		InputStream input = new FileInputStream(file);  
	    DataInputStream inst = new DataInputStream(input);
	    if(input.available() % 2 != 0) {
	    	inst.close();
	    	throw new LC3VMException("Error: Image file must have even # of bytes\n" +
	    			"Loaded file is: "+input.available()+" bytes");
	    }
	    
		reg[2] = (int) (file.length() / 2);
	    int i = 0;
	    char seek = 0x0;
	    if (endAddress == 0x0)
	    	startAddress = 0x3000;
	    else
	    	startAddress = endAddress;
	    
	    seek=startAddress;
	    while((inst.available())>0) {
	    		if(i++==0) {
	    			//readChar reads two bytes at a time.
	    			char addr = inst.readChar();
	    			//if it's not == 0x3000 throw exception.
	    			if(addr != 0x3000)
	    				if(!file.getName().endsWith(".bin")) {
	    					/*
	    					 * .bin files = chip8 rom
	    					 * Ignore error
	    					 */
		    				inst.close();
		    				throw new LC3VMException("Invalid start address");
	    				}
	    				else
	    					mem[seek++] = addr;
	    			//we don't want the first char since it's the address (in this VM).
	    			//unless it is a chip8 rom
	    			continue; 
	    		}
	    		
	    		//exit if we reached max capacity.
	    		if(seek > 0xFDFF) {
	    			inst.close();
	    			throw new LC3VMException("Error: no more space for user program.");
	    		}
	    		
	    		mem[seek++] = inst.readChar();	
	    }
	    endAddress = seek;
	    inst.close();
	    input.close();
	}
	
	private void setCC(int sum) throws LC3VMException {
		if(sum == 0)
			reg[COND] = ZER;
		else if ((sum & 0xffff) >> 15 == 1)
			reg[COND] = NEG;
		else
			reg[COND] = POS;
	}
	
	private void incrementPC() { pc++; }
	
	public String resLdPrg(int r0) {
		StringBuilder sb = new StringBuilder();
    	int address = reg[r0];
		for(int i = address;;i++) {
			char val = (char) mem[i];
			if(val==0)
				break;
			sb.append(val);
		}
		
		return sb.toString();
	}
	
	private void loadProgAtAddress(File file, int address) throws LC3VMException, IOException {
		InputStream input = new FileInputStream(file);  
	    DataInputStream inst = new DataInputStream(input);
		reg[2] = (int) file.length();
		while((inst.available())>0)
			mem[address++] = inst.readByte();
		inst.close();
	}
	
	public void execute(int instr) throws LC3VMException, IOException {
		int opcode = instr >> 12;
		int r0,r1,r2;
		r0 = (instr >> 9) & 0x7; 
		r1 = (instr >> 6) & 0x7; 
		r2 = instr & 0x7; 
		int imm5 = instr & 0x1f;
		int imm5trap = (instr >> 4) & 0x1f;
		int off6 = instr & 0x3f;
		int resLoadCondition = (instr >> 3) & 0x7;
		int trap4 = instr & 0xf;
		int addr2 = (instr >> 3) & 0x3;
		boolean imm5set = ((instr >> 5) & 1) == 1;
		boolean longflagset = ((instr >> 11) & 1) == 1;
		
		/*
		 * Must sign-extend!
		 * E.g -3 is FFFD
		 * but imm5Trap = 11101 (1D) = 29 
		 */
		imm5trap = (imm5trap >> 4) == 1 ? imm5trap |= 0xfffffff0 : imm5trap;
		imm5 = (imm5 >> 4) == 1 ? imm5 |= 0xfffffff0 : imm5;
		off6 = (off6 >> 5) == 1 ? off6 |= 0xfffffff0 : off6;
		
		imm5trap &= 0xffff;
		imm5 &= 0xffff;
		off6 &= 0xffff;
		
		switch(opcode) {
			case 0:	// BR
				incrementPC();
				if ((r0 & reg[COND]) != 0)
					pc = (pc + abs) & 0xffff;
			break;
			
			case 1:	{ // ADDR/ADDI/MULR/CMP
				int sum = 0;
				if(imm5set) {
					sum = reg[r1] + imm5;
					setCC(sum);
					reg[r0] = sum & 0xffff;
				} 
				else
					switch(addr2) {
						case 0:	// normal
							sum = reg[r1] + reg[r2];
							setCC(sum);
							reg[r0] = sum & 0xffff;
							//For chip8 bitshifting
							if(sum > 0xffff) reg[COND] = POS;
						break;
						
						case 1:	// mulr
							sum = reg[r1] * reg[r2];
							setCC(sum);
							reg[r0] = sum & 0xffff;
						break;
							
						case 2:	// cmp
							sum = reg[r0] - reg[r1];
							setCC(sum);
						break;
						
						default:
							throw new LC3VMException("No case found");
					}
				
			}
			break;	
				
			case 2:	// LD
				incrementPC();
				reg[r0] = mem[(pc + abs) & 0xffff];
			break;	
				
			case 3:	// ST
				incrementPC();
				mem[(pc + abs) & 0xffff] = reg[r0];
				memIO((pc + abs) & 0xffff, reg[r0]);
			break;	
				
			case 4:	// JSR/JSRR/JSSR
				incrementPC();
				reg[7] = pc;
				pc = longflagset ? (pc + abs) & 0xffff : reg[r1];
			break;	
				
			case 5:	// ANDR/ANDI
				int sum = 0;
				if(imm5set) {
					sum = reg[r1] & imm5;
					setCC(sum);
					reg[r0] = sum & 0xffff;
				} else {
					sum = reg[r1] & reg[r2];
					setCC(sum);
					reg[r0] = sum & 0xffff;
				}
			break;	
				
			case 6:	// LDR
				reg[r0] = mem[reg[r1] + off6];
			break;	
				
			case 7:	// STR
				mem[reg[r1] + off6] = reg[r0];
				memIO(reg[r1] + off6, reg[r0]);
			break;	
				
			case 8:	// RTI (UNUSED)
			break;	
				
			case 9:	// NOT
				reg[r0] = ~reg[r1] & 0xffff;
			break;	
				
			case 10: // LDI
				incrementPC();
				reg[r0] = mem[mem[pc + abs]];
			break;	
				
			case 11: // STI
				incrementPC();
				mem[mem[pc + abs]] = reg[r0];
			break;	
				
			case 12: // RET
				pc = reg[r1];
			break;	
				
			case 13: { // RES
				String file = resLdPrg(r0);
				String lc3filepath=System.getenv("chip8roms");
				File userprog = new File(lc3filepath+"/"+file);
				switch(resLoadCondition) {
					case 0:	//IN
						loadProgAtAddress(userprog,reg[r1] + reg[r2]);
					break;
					
					case 1:	//OUT
						DataOutputStream dos = 
						new DataOutputStream(new FileOutputStream(userprog));
						for(int i = 0; i < reg[r2]; i++) {
							int addr = reg[r1] + i;
							dos.writeByte(mem[addr]);
						}	
						dos.close();
					break;
						
					case 2:	//VRAM
						emu.setVramStartAddressAndSize(reg[r0], reg[r1], reg[r2]);	
					break;
						
					case 3:	//SCALE
						emu.setScale(reg[r0]);
					break;
						
					case 4:	//RAND
						int x = (int) Math.floor(Math.random() * LCDDisplayLite.DISPLAY_WIDTH);
						int y = (int) Math.floor(Math.random() * LCDDisplayLite.DISPLAY_HEIGHT);
						reg[r0] = x;
						reg[r1] = y;
					break;
					
					case 5:	//RANDB
						x = (int) Math.floor(Math.random() * 255);
						reg[r0] = x;
					break;
					
					default:
						throw new LC3VMException("No RES condition");
				}
			}
			break;
				
			case 14: // LEA
				incrementPC();
				reg[r0] = (pc + abs) & 0xffff;
			break;	
				
			case 15: { // TRAP
				switch(trap4) {
					case 0:	// TRAP_GETC
						String str = input.next();
				        reg[0] = str.charAt(0);
					break;
					
					case 1:	// TRAP_OUT
						if(!TROUBLE_SHOOT)
							System.out.printf("%c",reg[0]);
					break;
						
					case 2:	// TRAP_PUTS
					case 4: // TRAP_PUTSP
						if(!TROUBLE_SHOOT) {
							StringBuilder sb = new StringBuilder();
					    	int address = reg[0];
							for(int i = address;;i++) {
								char val = (char) mem[i];
								if(val==0)
									break;
								
								sb.append(val);
							}
							
							System.out.print(sb.toString());
						}
					break;
						
					case 3: // TRAP_IN
						System.out.println("Enter a character:");
						str = input.next();
				        reg[0] = str.charAt(0);
					break;
						
					case 5: // TRAP_HALT
						if(!PRINT_HEX)
							System.out.print("HALT"); 
						running = false;
					break;
						
					case 6: // TRAP_OUTR
						int value = -1;
						char c = (char) reg[r0];
						if(PRINT_HEX) {
							System.out.printf("%02X",(int)c);
							return;
						}
						value = c;
						if((c >> 15) == 1) {
							c = (char)~c;
							c++;
							value = c;
							System.out.print("-"+value);
						}
						else
							System.out.print(value);
						System.out.print(" ");
					break;
						
					case 7: // TRAP_MOVR
						if(r0 != r1) {
							reg[r0] = reg[r1];
							//r.setCC(srcreg);
						}
					break;
						
					case 8: // TRAP_SWAPR
						int temp = reg[r0];
						reg[r0] = reg[r1];
						reg[r1] = temp;
						
					break;
						
					case 9: // TRAP_MOVI (uses imm5trap)
						reg[r0] =  imm5trap;
						//r.setCC(srcreg);
					break;
						
					case 10: // TRAP_SCAN
						str = input.next();
				    	int address = reg[0];
						for(int i = 0;i<str.length();i++)
							mem[address++] = str.charAt(i);
						//NULL byte to terminate
						mem[address] = '\0';
					break;
						
					case 11: // TRAP_LOAD
						StringBuilder sb = new StringBuilder();
				    	address = reg[0];
						for(int i = address;;i++) {
							char val = (char) mem[i];
							if(val==0)
								break;
							
							sb.append(val);
						}
						String lc3filepath=System.getenv("chip8roms");
						File userprog = new File(lc3filepath+"/"+sb.toString());
						try {
							loadImage(userprog);
						} catch (Exception e) {
							e.printStackTrace();
							System.exit(-1);
						}
					break;
						
					case 12: // TRAP_RUN
					case 13: // TRAP_MODE
						throw new LC3VMException("Not implemented");
						
					case 14: // TRAP_PUSH
						int val = reg[r0];
						address = reg[6];
						mem[address] = val;
						reg[6] = ++address;
					break;
					
					case 15: // TRAP_POP
						address = reg[6];
						address--;
						val = mem[address];
						reg[r0] = val;
						reg[6] = address;
					break;
					
					default:
						throw new LC3VMException("No trap4 condition");
				}
			}
			break;	
			
			default: // No opcode
				throw new LC3VMException("No opcode: "+
						String.format("%02X", opcode));
		}
	}
	
	/* Jan 29 2021 11:45AM
	 * My enhancement for memory mapped IO
	 * functions OR, XOR.
	 */
	private void memIO(int address, int val) {
		if(address == Vars.MR_BITWISE) {
			if(val == Vars.MR_OR) {
				mem[Vars.MR_DATA1] |= mem[Vars.MR_DATA2];
				mem[Vars.MR_DATA1] &= 0xffff;
				mem[address] = 0;
			}
			else if(val == Vars.MR_XOR) {
				mem[Vars.MR_DATA1] ^= mem[Vars.MR_DATA2];
				mem[Vars.MR_DATA1] &= 0xffff;
				mem[address] = 0;
			}
		}
		else if(address == Vars.MR_SLEEP_ADDR) {
			Bit.sleep(val);
			mem[address] = 0;
		}
		else if (address == Vars.MR_LCDINIT) 
			if(val == Vars.MR_LCDINITVAL) {
				mem[address] = 0;
				System.setProperty("apple.awt.application.name", "Chip-8 Display");
		        try {
		        	if(!emu.isRunning())
		        		emu.run();
		        	else {
		        		try {
		        			throw new LC3VMException("Emulator already running");
		        		} catch(LC3VMException e) {
		        			e.printStackTrace();
		        			System.exit(1);
		        		}
		        	}
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
			} else {
				try {
	    			throw new LC3VMException("Incorrect value for MR_LCDINITVAL: " + val);
	    		} catch(LC3VMException e) {
	    			e.printStackTrace();
	    			System.exit(1);
	    		}
			}
	}

}
