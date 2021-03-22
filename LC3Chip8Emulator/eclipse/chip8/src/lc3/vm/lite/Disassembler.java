package lc3.vm.lite;

import java.io.FileWriter;
import java.io.IOException;
import lc3.vm.exceptions.LC3VMException;
import lc3.vm.util.Vars;
import lc3.vm.util.bit.Bit;

public class Disassembler {
	
	private StringBuilder sb;
	private FileWriter fw;
	
	public Disassembler() {
		sb = new StringBuilder();
		try {
			if(Vars.TROUBLE_SHOOT) {
				String[] vmoptions=System.getenv("vmtoptions").split(",");
				String option2 = vmoptions[1];
				fw = new FileWriter(option2);
			}
		} catch(IOException io) {
			io.printStackTrace();
			System.exit(-1);
		}
	}
	
	private void reg(int r, int[] reg) {
		sb.append(" r");
		sb.append(r);
		sb.append("[");
		sb.append(String.format("%04X",reg[r]));
		sb.append("] ");
	}
		
	
	public void close() throws IOException {
		fw.close();
	}
	
	public void disassemble(int instr, int[] reg, int pc) throws LC3VMException, IOException {
		int opcode = instr >> 12;
		int r0,r1,r2;
		r0 = (instr >> 9) & 0x7; 
		r1 = (instr >> 6) & 0x7; 
		r2 = instr & 0x7; 
		int imm5 = instr & 0x1f;
		/*
		 * imm5trap = bits 8-4 (8-12)
		 * Used in movi
		 */
		int imm5trap = (instr >> 4) & 0x1f;
		int off6 = instr & 0x3f;
		int resLoadCondition = (instr >> 3) & 0x7;	//bits 11-13 inclusive
		/*
		 * trap4 = last 4 bits
		 * Used as a trap condition
		 */
		int trap4 = instr & 0xf;
		
		/*
		 * addr2 = bit 4-3 (12-13)
		 */
		int addr2 = (instr >> 3) & 0x3;
		boolean imm5set = ((instr >> 5) & 1) == 1;
		boolean longflagset = ((instr >> 11) & 1) == 1;
		
		//imm5trap = (imm5trap >> 4) == 1 ? imm5trap * -1 : imm5trap;
		//imm5 = (imm5 >> 4) == 1 ? imm5 * -1 : imm5;
		//off6 = (imm5trap >> 5) == 1 ? off6 * -1 : off6;
		
		sb.append(String.format("%04X/%d %s: %04X %02X ",pc,pc,Bit.binStr(instr, true), instr, opcode));
		sb.append("\t");
		switch(opcode) {
			case Vars.OP_ADD:
				if(imm5set) {
					sb.append("Addi");
					reg(r0,reg);
					reg(r1,reg);
					sb.append(" ");
					sb.append(imm5 >> 4 == 1 ? "-" : "+");
					sb.append(imm5);
				} else {
					switch(addr2) {
						case Vars.ADDR_NORMAL:
							sb.append("addr");
							reg(r0,reg);
							reg(r1,reg);
							reg(r2,reg);
						break;
						case Vars.ADDR_MULR:
							sb.append("mulr");
							reg(r0,reg);
							reg(r1,reg);
							reg(r2,reg);
						break;
						case Vars.ADDR_CMP:
							sb.append("cmp");
							reg(r0,reg);
							reg(r1,reg);
						break;
					}
				}
			break;
			case Vars.OP_AND:
				if(imm5set) {
					sb.append("andi");
					reg(r0,reg);
					reg(r1,reg);
					sb.append(" ");
					sb.append(imm5 >> 4 == 1 ? "-" : "+");
					sb.append(imm5);
				} else {
					sb.append("andr");
					reg(r0,reg);
					reg(r1,reg);
					sb.append(" r");
					sb.append(r2);
				}
			break;
			case Vars.OP_BR:
				sb.append("br");
				sb.append(" ");
				sb.append(nzpSimplify(r0));
				sb.append(" ");
				sb.append(pc);
				sb.append(" + ");
				sb.append(LiteCpu.abs());
				sb.append(" (" + LiteCpu.abs() + ")");
				sb.append(" ");
				sb.append(branchCondition(reg, r0) ? "JUMP" : "");
			break;
			case Vars.OP_JMP:
				if(r1==7) {
					sb.append("ret");
					sb.append(" ");
				} else {
					sb.append("jmp");
					reg(r1,reg);
				}
			break;
			case Vars.OP_JSR:
				sb.append(longflagset ? "jsr" : "jsrr");
				sb.append(" ");
				sb.append(pc);
				sb.append(" + ");
				sb.append(LiteCpu.abs());
				sb.append(" (" + LiteCpu.abs() + ")");
				sb.append(" ");
			break;
			case Vars.OP_LD:
				sb.append("ld");
				reg(r0,reg);
				sb.append(" ");
				sb.append(pc);
				sb.append(" + ");
				sb.append(LiteCpu.abs());
				sb.append(" (" + LiteCpu.abs() + ")");
			break;
			case Vars.OP_LDI:
				sb.append("ldi");
				reg(r0,reg);
				sb.append(" ");
				sb.append(pc);
				sb.append("(" + String.format("%04X",pc) + ")");
				sb.append(" + ");
				sb.append(LiteCpu.abs());
				sb.append("(" + LiteCpu.abs() + ")");
			break;
			case Vars.OP_LDR:
				sb.append("ldr");
				reg(r0,reg);
				reg(r1,reg);
				sb.append(" ");
				sb.append(off6 >> 5 == 1 ? " - " : " + ");
				sb.append(off6);
			break;
			case Vars.OP_LEA:
				sb.append("lea");
				reg(r0,reg);
				sb.append(" ");
				sb.append(pc);
				sb.append("(" + String.format("%04X",pc) + ")");
				sb.append(" + ");
				sb.append(LiteCpu.abs());
				sb.append("(" + String.format("%04X",LiteCpu.abs()) + ")");
			break;
			case Vars.OP_NOT:
				sb.append("not");
				reg(r0,reg);
				reg(r1,reg);
				sb.append(" ");
			break;
			case Vars.OP_RTI:
				sb.append("rti");
			break;
			case Vars.OP_ST:
				sb.append("st");
				reg(r0,reg);
				sb.append(" ");
				sb.append(pc);
				sb.append(" + ");
				sb.append(LiteCpu.abs());
				sb.append(" (" + LiteCpu.abs() + ")");
			break;
			case Vars.OP_STI:
				sb.append("sti");
				reg(r0,reg);
				sb.append(" ");
				sb.append(pc);
				sb.append(" + ");
				sb.append(LiteCpu.abs());
				sb.append(" (" + LiteCpu.abs() + ")");
			break;
			case Vars.OP_STR:
				sb.append("str");
				reg(r0,reg);
				reg(r1,reg);
				sb.append(" ");
				sb.append(off6 >> 5 == 1 ? " - " : " + ");
				sb.append(off6);
				
			break;
			case Vars.OP_TRAP: {
				switch(trap4) {
					case Vars.TRAP_IN:
						sb.append("input");
					break;
					case Vars.TRAP_GETC:
						sb.append("get");
					break;
					case Vars.TRAP_HALT:
						sb.append("HALT");
					break;
					case Vars.TRAP_OUT:
						sb.append("out");
					break;
					case Vars.TRAP_PUTS:
						sb.append("puts");
					break;
					case Vars.TRAP_PUTSP:
						sb.append("putsp");
					break;
					case Vars.TRAP_OUTR:
						sb.append("outr");
						reg(r0,reg);
						sb.append(" ");
					break;
					case Vars.TRAP_MOVR:
						sb.append("movr");
						reg(r0,reg);
						reg(r1,reg);
						sb.append(" ");
					break;
					case Vars.TRAP_SWAPR:
						sb.append("swapr");
						reg(r0,reg);
						reg(r1,reg);
						sb.append(" ");
					break;
					case Vars.TRAP_MOVI:
						sb.append("movi");
						reg(r0,reg);
						sb.append(" ");
						sb.append(imm5trap);
					break;
					case Vars.TRAP_PUSH:
						sb.append("push");
						reg(r0,reg);
					break;
					case Vars.TRAP_POP:
						sb.append("pop");
						reg(r0,reg);
					break;
					case Vars.TRAP_SCAN:
						sb.append("scan");
					break;
					case Vars.TRAP_LOAD:
						sb.append("ldprg");
					break;
					case Vars.TRAP_RUN:
						sb.append("run");
					break;
					case Vars.TRAP_MODE:
						sb.append("mode");
					default:
						throw new LC3VMException("Invalid Trap code: " + Integer.toHexString(trap4) );
				}
			}
			break;
			case Vars.OP_RES:
				sb.append("res");
				reg(r0,reg);
				reg(r1,reg);
				switch(resLoadCondition) {
					case 0:
						sb.append("IN");
					break;
					
					case 1:
						sb.append("OUT");
					break;
					default:
						throw new LC3VMException("No case for RES (LOAD)");
				}
			break;
			
			default:
				sb.append("???");
			break;
		}
		
		//sb.append("\t");
		//sb.append(String.format("%04X",(int)reg.getPC()));
		sb.trimToSize();
		fw.write(sb.toString());
		fw.write("\n");
		fw.flush();
		sb.setLength(0);
	
	}
		
	/*
	 * nzpSimplify:
	 * Added Fri Feb 05 2021
	 * 11:14AM
	 */
	private static String nzpSimplify(int nzp) throws LC3VMException {
		switch(nzp) {
			case 1:
				return ">";
			case 2:
				return "==";
			case 3:
				return ">=";
			case 4:
				return "<";
			case 5:
				return "!=";
			case 6:
				return "<=";
			case 7:
				return "<>";
			default:
				return "???";
				//throw new LC3VMException("NZP undetermined " + String.format("%d", nzp));
		}
					
	}
	
	private static boolean branchCondition(int[] reg, int num) {
		return (num & reg[Vars.COND]) != 0;
	}

}
