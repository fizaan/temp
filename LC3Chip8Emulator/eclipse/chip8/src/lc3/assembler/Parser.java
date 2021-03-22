package lc3.assembler;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class Parser {
	
	private Map<String, Short> opCodeMap;
	private Map<String, Short> regMap;
	private ArrayList<Integer> machineCodeArr;
	private ArrayList<String> data;
	private ArrayList<String> subroutines;
	private ArrayList<ProgramTable> progTable;
	private int displacement;
	private int absoffset;
	
	/*
	 * totalInstructions holds the number of instructions
	 * which the PC can jump to. It doesn't include
	 * data count or function count, number of lines etc.
	 * 
	 * progcounter = CURRENT PC
	 * 
	 * An offset/address is calculated as follows:
	 * where 
	 * totalinstruction = PC + offset/address
	 * offset = totalinstruction - PC.
	 */
	private int totalInstructions,progcounter;
	private int LabelAddress;
	
	public ArrayList<ProgramTable> getProgTable() {
		return progTable;
	}
	
	public int getAbsoluteOffset() { return absoffset; }
	
	/*
	 * Returns the size of the program. i.e. the total
	 * number of instructions which the PC can jump to.
	 */
	public int getSize() { return totalInstructions; }
	
	/*
	 * PC++ everytime evaluate method is called. Evaluate
	 * method is only called if the instruction is a valid
	 * instruction found in opCodeMap.
	 */
	
	public int getCounter() { return progcounter; }
	
	public ArrayList<String> getData() { return data; }
	
	public Parser() throws Exception {
		opCodeMap= new HashMap<>();
		regMap= new HashMap<>();
		machineCodeArr=new ArrayList<Integer>();
		data=new ArrayList<String>();
		subroutines = new ArrayList<String>();
		progTable = new ArrayList<ProgramTable>();
		displacement = 0;
		totalInstructions = 0;
		progcounter = 0;
		Bit.setbits(16);
		
		/*
		 * opCodeMap contains key value pairs.
		 * of instruction (key) mapping to a random 
		 * number (value). E.g. addr is 1, addi is 0.
		 * This makes it easier to lookup in a switch
		 * statement
		 */
		
		regMap.put("r0",Fixed.r0);regMap.put("r1",Fixed.r1);regMap.put("r2",Fixed.r2);
		regMap.put("r3",Fixed.r3);regMap.put("r4",Fixed.r4);regMap.put("r5",Fixed.r5);
		regMap.put("r6",Fixed.r6);regMap.put("r7",Fixed.r7);
		regMap.put("r8",Fixed.r8);regMap.put("r9",Fixed.r9);
		regMap.put("r10",Fixed.r10);
		
		//add
		opCodeMap.put("addi",Fixed.ADDI);opCodeMap.put("addr",Fixed.ADDR);
		
		//trap
		opCodeMap.put("halt", Fixed.HALT);opCodeMap.put("out", Fixed.OUT);
		opCodeMap.put("puts", Fixed.PUTS);opCodeMap.put("in", Fixed.IN);
		opCodeMap.put("getc", Fixed.GETC);opCodeMap.put("putsp", Fixed.PUTSP);
		
		/*
		 * enhancements: outr, movr, swapr, movi, scan
		 * ldprog, run, mode, push, pop, mulr r0  r1  r2,
		 * cmp r0 r1
		 */
		opCodeMap.put("outr", Fixed.OUTR);opCodeMap.put("movr", Fixed.MOVR);
		opCodeMap.put("swapr", Fixed.SWAPR);opCodeMap.put("movi", Fixed.MOVI);
		opCodeMap.put("scan", Fixed.SCAN);opCodeMap.put("ldprog", Fixed.LD_USERPROG);
		opCodeMap.put("run", Fixed.RUN_USERPROG); opCodeMap.put("mode", Fixed.MODE);
		opCodeMap.put("push", Fixed.PUSH); opCodeMap.put("pop", Fixed.POP);
		opCodeMap.put("mulr", Fixed.MULR);opCodeMap.put("cmp", Fixed.CMP);
		
		//and, ret, not, jmp, jssr, br
		opCodeMap.put("andi", Fixed.ANDI);opCodeMap.put("andr", Fixed.ANDR);opCodeMap.put("ret", Fixed.RET);
		opCodeMap.put("not", Fixed.NOT);opCodeMap.put("jmp", Fixed.JMP);opCodeMap.put("jsrr", Fixed.JSRR);
		opCodeMap.put("jsr", Fixed.JSR); opCodeMap.put("br", Fixed.BR); 
		
		
		//load
		opCodeMap.put("lea", Fixed.LEA);opCodeMap.put("ld", Fixed.LD);
		opCodeMap.put("ldi", Fixed.LDI);opCodeMap.put("ldr", Fixed.LDR);
		opCodeMap.put("leahex", Fixed.LEAHEX);opCodeMap.put("ldf", Fixed.LDRF);
		opCodeMap.put("leaf", Fixed.LEAF);
		
		//store
		opCodeMap.put("st", Fixed.ST);opCodeMap.put("sti", Fixed.STI);
		opCodeMap.put("str", Fixed.STR);
		
		//RTI RES
		opCodeMap.put("rti", Fixed.RTI);opCodeMap.put("res", Fixed.RES);
		
		//abs
		opCodeMap.put("abs", Fixed.ABS);
		
		
	}
	
	public Map<String, Short> getRegMap() {
		return regMap;
	}
	
	
	public void evaluate(ArrayList<String> ar) {
		for(String s:ar) {
			String[] instruction = s.split("\\s+");
			String opcodestr = instruction[0];
						
			if(opcodestr.equals(".start")) {
				int startAddress = Integer.parseInt(instruction[1], 16);
				emitByte(startAddress);
			}
			else if(isInstruction(opcodestr))
				try {
					evaluate(opcodestr, instruction);
				} catch (LC3AssemblerException e) {
					e.printStackTrace();
					System.exit(-1);
				}			
		}	
	}
	
	/*
	 * This is the first pass of ArrayList arr
	 */
	public void setSize(ArrayList<String> ar) {
		totalInstructions = 0;
		for(String s:ar) {
			String[] instruction = s.split("\\s+");
			String opcodestr = instruction[0];
			if(isInstruction(opcodestr)) {
				//functions for jumping:
				int findex = s.indexOf("_");
				if(findex > -1) {
					String fname = s.substring(findex+1, s.length());
					subroutines.add(fname.trim());
					subroutines.add(""+totalInstructions);
				}
				
				totalInstructions++;
			}	
			else if(s.startsWith(".data")) {
                /*
                 * .data#LABEL#TYPE#VALUE
                 * 
                 * Do not trim the values!
                 */
                String[] datastr = s.split("#");
                String label = datastr[1];
                String type = datastr[2];
                String val;
                try {
                	val = datastr[3];
                } catch (ArrayIndexOutOfBoundsException ex) {
                	/*
                	 * handles empty string:
                	 * 		.data#s-main#STR##
                	 * 
                	 * normal case:
                	 * 		.data#s-main#STR#TEST123#
                	 * 
                	 * An empty string has a single element, 
                	 * the null character, '\0' . That's still 
                	 * a character, and the string has a length 
                	 * of zero, but it's not the same as a null 
                	 * string, which has no characters at all
                	 * 
                	 * Note: does not handle a null string.
                	 */
                	val = "";
                }
                data.add( label );
                data.add( type );
                data.add( val );
                data.add( ""+displacement );
                
                /*
                 * A 'displacement' represents 2-bytes.
                 * Assembler writes 2-bytes to obj file
                 * while VM reads 2-bytes from obj file
                 */

                if( type.equals("STR") ) {
                   //+1 to account for null terminated. See writeData();
                	displacement += val.length() + 1; 
                } 
                /*
                 * Don't use type.endsWith()
                 */
                else if (type.equals("HEX") || type.equals("WORD"))
                	displacement++;
                else if (type.equals("ARRAY")) {
                	int x = 0;
    				try { x = Integer.parseInt(val); } catch(Exception e) {
    					val=val.substring(2,val.length());
    					try { x = Integer.parseInt(val,16); } catch(Exception e1) {
    						e1.printStackTrace();
    					}
    				}
                	displacement += x;
                } else if (type.equals("FILLINTARRAY")) {
                	String content[] = val.split(",");
                	displacement += content.length + 1;
                } else if (type.equals("RANDINTARRAY")) {
                	int x = 0;
    				try { x = Integer.parseInt(val); } catch(Exception e) {
    					val=val.substring(2,val.length());
    					try { x = Integer.parseInt(val,16); } catch(Exception e1) {
    						e1.printStackTrace();
    					}
    				}
                	displacement += x + 1;
                	/*
                	 * +1 for the 0th index where
                	 * I store the length.
                	 */
                }
            }
		}
	}	
	
	public void writeUserData() {
		if(data.size() == 0) return;
		/*
		 * 0 = label
		 * 1 = type
		 * 2 = value
		 * 3 = displacement
		 */
		for(int i = 0; i < data.size(); i = i + 4) {
			String type = data.get(i+1);
			String val = data.get(i+2);
			if(type.equals("STR")) {
				for(int k = 0;k < val.length(); k++)
					emitByte(val.charAt(k));
				
				/*
				   MUST add a null terminated byte 
				   after each string value ends
				 */
				emitByte('\0');
			}
			else if(type.equals("HEX"))
				emitByte(Integer.parseInt(val,16));	
			else if(type.equals("WORD")) {
				int x=0;
				try { x = Integer.parseInt(val); } catch(Exception e) {
					val=val.substring(2,val.length());
					try { x = Integer.parseInt(val,16); } catch(Exception e1) {
						e1.printStackTrace();
					}
				}
				
				if(x > Fixed.WORD_MAX_VALUE || x < Fixed.WORD_MIN_VALUE )
					if(Fixed.PRINT_WARNING)
						System.out.println(this+ " Warning: " + x + 
							" (" + String.format("%04X",x) +") is not in range of min-max value");
				char c = (char) x;
				c = c < 0 ? Bit.sext(c, Fixed.NEG) : Bit.sext(c, Fixed.POS);
				emitByte(c);
			}
			/*
			 * Uninitialized Array of size 'loop'
			 */
			else if(type.equals("ARRAY")) {
				int loop = 0;
				try { loop = Integer.parseInt(val); } catch(Exception e) {
					val=val.substring(2,val.length());
					try { loop = Integer.parseInt(val,16); } catch(Exception e1) {
						e1.printStackTrace();
					}
				}
				for(short k = 0; k < loop; k++)
					emitByte('\0');
			}
			/*
			 * FILLINTARRAY
			 * index 0 holds the size
			 */
			else if(type.equals("FILLINTARRAY")) {
				String content[] = val.split(",");
				int size = content.length;
				emitByte(size);
				for(String var:content) {
					var=var.trim();
					int x = 0;
					try { x = Integer.parseInt(var); } catch(Exception e) {
						var=var.substring(2,var.length());
						try { x = Integer.parseInt(var,16); } catch(Exception e1) {
							e1.printStackTrace();
						}
					}
					emitByte(x);
				}
			}
			/*
			 * Random Integer Array of size 'loop'
			 * Note: index 0 is the size of
			 * this random array.			 
			 */
			else if(type.equals("RANDINTARRAY")) {
				ArrayList<Integer> rand = new ArrayList<Integer>();
				int loop = 0;
				try { loop = Integer.parseInt(val); } catch(Exception e) {
					val=val.substring(2,val.length());
					try { loop = Integer.parseInt(val,16); } catch(Exception e1) {
						e1.printStackTrace();
					}
				}
				for(int k = 0; k < loop; k++)
					rand.add(k);
				
				rand.trimToSize();
				Collections.shuffle(rand);
				
				//index 0 = size
				emitByte(loop);
				
				for(int random:rand)
					emitByte(random);
			}
		}			
	}
	
	public int getLabelPos(String label) {
	    int i = 0;
	    for(String s:data) {
	        if( s.equals(label) )
	            return i;
	        i++;
	    }    
	   
	    return -1;
	}
	
	/*
	 * Trimmed down version of getting
	 * offset
	 */
	public int getOffset(String label) throws LC3AssemblerException {
		int pos = getFuction(label);
		if(pos == -1)
			throw new LC3AssemblerException("Error: cannot find lable: "+label);
		String address = getSubroutines().get(pos+1);
		int labeladdress = Integer.parseInt(address);
		setLabelAddress(labeladdress);
		int offset = (labeladdress - progcounter) - 1;
		absoffset = offset & 0xffff;
		return offset;
	}
	
	public int getDataOffset(String label) throws LC3AssemblerException {
		ArrayList<String> data = getData();
		int labelindex = getLabelPos(label);
		if(labelindex == -1)
			throw new LC3AssemblerException("Error: cannot find lable: "+label);
		int offset = ((totalInstructions - progcounter) - 1) + Integer.parseInt(data.get(labelindex+3));
		absoffset = offset & 0xffff;
		return offset;
	}
	
	private void setLabelAddress(int labeladdress) {
		this.LabelAddress = labeladdress;
	}
	
	public int getLabelAddress() { return LabelAddress; }

	/*
	 * The main method which evaluates each instruction and
	 * converts it into byte code which is then written to
	 * machineCodeArray...which is then written to obj output
	 * file.
	 */
	public void evaluate(String opcodestr,String[] instruction) throws LC3AssemblerException {
		progcounter++;
		int val = opCodeMap.get(instruction[0]);
		
		switch(val) {
			case Fixed.ADDI:
				Assembler.addandI(0b0001, instruction, this);
			break;
			
			case Fixed.ADDR://enhanced - but no added functionality
				Assembler.addandR(0x00, 0b0001, instruction, this);
			break;
			
			case Fixed.MULR://enhanced - with added functionality
				Assembler.addandR(0x01, 0b0001, instruction, this);
			break;
			
			case Fixed.CMP://enhanced - with added functionality
				Assembler.cmp(0x02, 0b0001, instruction, this);
			break;
			
			case Fixed.ANDI:
				Assembler.addandI(0b0101, instruction, this);	
			break;
			
			case Fixed.ANDR://enhanced - but no added functionality
				Assembler.addandR(0x00, 0b0101, instruction, this);	
			break;
			
			case Fixed.NOT:
				Assembler.not(0b1001, instruction, this);
			break;
			
			//BRANCH
			case Fixed.BR:
				Assembler.br(0b0000, instruction, this);
			break;
			
			//JUMP
			case Fixed.JMP:	
				Assembler.jmp(0b1100, instruction, this);
			break;
			
			case Fixed.JSR:
				Assembler.jsr(0b0100, instruction, this);
			break;
			
			case Fixed.JSRR:	
				Assembler.jmp(0b0100, instruction, this);
			break;
			
			case Fixed.RET:
				Assembler.ret(0b1100, instruction, this);
			break;
			
			//LOAD
			case Fixed.LEAF:
				Assembler.leaFunc(0b1110, instruction, this);
			break;
			
			case Fixed.LEA:
				Assembler.ld_Ldi_lea_leahex_st_sti(0b1110, instruction, this);	
			break;
			
			case Fixed.LEAHEX:
				Assembler.ld_Ldi_lea_leahex_st_sti(0b1110, instruction, this);
			break;
			
			case Fixed.LD:
				Assembler.ld_Ldi_lea_leahex_st_sti(0b0010, instruction, this);	
			break;
			
			case Fixed.LDI:
				Assembler.ld_Ldi_lea_leahex_st_sti(0b1010, instruction, this);	
			break;
			
			case Fixed.LDR: 
				Assembler.ldr_str(0b0110, instruction, this);
			break;
			
			//STORE
			case Fixed.ST:
				Assembler.ld_Ldi_lea_leahex_st_sti(0b0011, instruction, this);
			break;
			
			case Fixed.STI:
				Assembler.ld_Ldi_lea_leahex_st_sti(0b1011, instruction, this);
			break;
			
			case Fixed.STR:
				Assembler.ldr_str(0b0111, instruction, this);
			break;
			
			//trap
			case Fixed.HALT:
				Assembler.trap(0x05, instruction, this);
			break;
			
			case Fixed.OUT:
				Assembler.trap(0x01, instruction, this);
			break;
			
			case Fixed.OUTR://enhanced
				Assembler.outr(0x06, instruction, this);
			break;
			
			case Fixed.MOVR://enhanced
				Assembler.movr(0x07, instruction, this);
			break;
			
			case Fixed.SWAPR://enhanced
				Assembler.swapr(0x08, instruction, this);
			break;
			
			case Fixed.MOVI://enhanced
				Assembler.movi(0x09, instruction, this);
			break;
			
			case Fixed.SCAN://enhanced
				Assembler.trap(0x0A, instruction, this);
			break;
			
			case Fixed.LD_USERPROG://enhanced
				Assembler.trap(0x0B, instruction, this);
			break;
			
			case Fixed.RUN_USERPROG://enhanced
				Assembler.trap(0x0C, instruction, this);
			break;
			
			case Fixed.MODE://enhanced
				Assembler.trap(0x0D, instruction, this);
			break;
			
			case Fixed.PUSH://enhanced
				Assembler.outr(0x0E, instruction, this);
			break;
			
			case Fixed.POP://enhanced
				Assembler.outr(0x0F, instruction, this);
			break;
			
			case Fixed.PUTS:
				Assembler.trap(0x02, instruction, this);
			break;
			
			case Fixed.IN:
				Assembler.trap(0x03, instruction, this);
			break;
			
			case Fixed.GETC:
				Assembler.trap(0x00, instruction, this);
			break;
			
			case Fixed.PUTSP:
				Assembler.trap(0x04, instruction, this);
			break;
			
			case Fixed.RTI:
				Assembler.rti(instruction, this);
			break;
			
			case Fixed.RES:
				/*
				 * I'm using res opcode 0xD (13) for
				 * loading a program who's file string
				 * index starts at reg r0. Reg r1 stores
				 * the location/address WHERE the program will
				 * be loaded to + offset (r3)
				 * 
				 * E.g res r0 r1 r3
				 */
				Assembler.res(instruction, this);
			break;
			
			case Fixed.ABS:
				Assembler.abs(instruction, this);
			break;
				
			default:
				throw new LC3AssemblerException("No opcode found: " + instruction[0]);	
		}
	}
	
	/*
	 * Writes code to the .obj file 2-bytes at a time.
	 * high-byte first followed by low-byte (big-endian). 
	 * The code is first written to an array called machineCodeArr.
	 */
	public void writeMachineCodeToFile(String image) throws IOException {
		DataOutputStream dos = 
				new DataOutputStream(new FileOutputStream(image));
		for(int b:machineCodeArr) 
			dos.writeChar(b);
		dos.flush();
		dos.close();
	}
	
	/*
	 * Read the source code in assembly and put it in an array
	 * for processing.
	 */
	public ArrayList<String> readTextAssemblyFie(File src) throws Exception {
		return Utility.readTextAssemblyFile(src);
	}
	
	/*
	 * opCodeMap contains key value pairs.
	 * of instruction (key) mapping to a random 
	 * number (value). E.g. addr is 1, addi is 0.
	 * This makes it easier to lookup in a switch
	 * statement
	 */
	private boolean isInstruction(String s) {
		return opCodeMap.containsKey(s);
	}
	
	void check(String msg, boolean b) {
		if(!b) {
			System.out.println(msg);
			System.exit(1);
		}
	}
	
	void emitByte(int b) {
		machineCodeArr.add(b);
	}

	public int getFuction(String label) {
		int index = 0;
		int notfound = -1;
		for(String s:subroutines) {
			if(s.equals(label))
				return index;
			index++;
		}	
			
		return notfound;
	}

	public ArrayList<String> getSubroutines() {
		return subroutines;
	}
	
	public void debug() {
		System.out.print("Instruction\t\tHex\tAddr\tLabelAddr\tPC\t\tPCOffset\t\tCode\n\n");
		for(ProgramTable p: progTable)
			System.out.println(p);
	}
	
	public void debugToFile() throws IOException {
		if(!Fixed.TROUBLE_SHOOT) return;
		String[] assembleroptions=System.getenv("assembleroptions").split(",");
		String option2 = assembleroptions[1];
		FileWriter fw = new FileWriter(option2);
		fw.write("Instruction\t\t\tHex\t\tAddr\t\tLabelAddr\t\tPC\t\t\tPCOffset\t\t\tCode\n\n");
		for(ProgramTable p: progTable) {
			fw.write(p.toString());
			fw.write("\n");
		}
		fw.flush();
		fw.close();
	}

}
