package lc3.compiler.assemble;

import lc3.compiler.exception.LC3CompileException;
import lc3.compiler.parser.Instruction;
import lc3.compiler.parser.Parser;
import lc3.compiler.utilities.Fixed;

public class RegisterASM {
	
	public void caseSet(AssemblyText asm, Instruction instr) 
			throws LC3CompileException {
		switch(instr.getSymbol()) {
			case "param0":
				declareParam(asm, instr, "r0", true);
			break;
			
			case "param1":
				declareParam(asm, instr, "r1", true);
			break;
			
			default:
				throw new LC3CompileException("expecting param0/1");
		}
	}
	
	public void declareParam(AssemblyText asm, Instruction instr, String reg, boolean newDeclaration) {
		asm.insertNoInstruction(instr);
		switch(instr.getVType()) {
			case Fixed.NUM:
				if (Parser.isNumeric(instr.getValue())) 
					handleRegisterParamModifyNumeric(asm, instr, reg);
					/*
					 * if case:
					 * set param0 = 5
					 * set param1 = 7
					 * param0 = 2
					 * param1 = 5
					 * param0 + 1
					 * param0 - 1
					 * param0 * 2
					 */
				else 
					handleRegisterParamModifyVariable(asm, instr, reg);
					/*
					 * else case:
					 * set param0 = x
					 * set param1 = y
					 * param0 = k
					 * param1 = k
					 * param0 + k
					 * param0 - k
					 * param0 * k
					 */
			break;
			
			/*
			 * case :
			 * set param0 = "hi there"
			 * set param1 = "hey man"
			 */
			case Fixed.STR:
				
				System.out.println("Not implemented: param0 = string");
				/*
				asmInsert("ld "  + reg + " " +  instr.getSymbol() + "-" 
						+ instr.getFuncName() );
				int strBegin = instr.getValue().indexOf("\"");
				String val = instr.getValue().substring(strBegin+1,
							instr.getValue().length()-1);
				 data.append(".data#" + instr.getSymbol() + "-" 
							+ instr.getFuncName() + "#STR#" +
							val + "#");
				 data.append("\n");
				*/
			break;
		}
		
		if(newDeclaration) {
			asm.asmInsert("push " + reg,instr.isRecursive());
			asm.stack.push("pop " + reg);
		}
	}
	
	/*
	 * void param()
	 * 
	 * function parametres are stored in
	 * registers r0 and r1. I am not
	 * storing the type (int or string).
	 */
	
	public void handleRegisterParamModifyNumeric(AssemblyText asm, Instruction instr, String reg) {
		switch(instr.getModifyOperatorType()) {
			case Fixed.EQU:
				asm.asmInsert("movi " + reg + " " + instr.getValue() ,instr.isRecursive());
			break;
			
			case Fixed.ADD:
				asm.asmInsert("addi " + reg + " " + reg + " " + instr.getValue(),instr.isRecursive());
			break;
			
			case Fixed.SUB:
				asm.asmInsert("movi r2 " + instr.getValue(),instr.isRecursive());
				asm.asmInsert("not r2 r2",instr.isRecursive());
				asm.asmInsert("addi r2 r2 1",instr.isRecursive());
				asm.asmInsert("addr " + reg + " " + reg + " r2" ,instr.isRecursive());
				
			break;
			
			case Fixed.MUL:
				asm.asmInsert("movi r2 " + instr.getValue(),instr.isRecursive());
				asm.asmInsert("mulr " + reg + " " + reg + " r2" ,instr.isRecursive());
			break;
		}
	}
	
	public void handleRegisterParamModifyVariable(AssemblyText asm, Instruction instr, String reg) {
		/*
		 * remember! getValue() is a symbol in this case:
		 * param0 = y
		 * param0 * y etc
		 */
		switch(instr.getModifyOperatorType()) {
			case Fixed.EQU:
				asm.asmInsert("ld " + reg + " " + instr.getValue() + "-" + instr.getFuncName() ,instr.isRecursive());
			break;
			
			case Fixed.ADD:
				asm.asmInsert("ld r2 " + instr.getValue() + "-" + instr.getFuncName() ,instr.isRecursive());
				asm.asmInsert("addr " + reg + " " + reg + " r2",instr.isRecursive());
			break;
			
			case Fixed.SUB:
				asm.asmInsert("ld r2 " + instr.getValue() + "-" + instr.getFuncName() ,instr.isRecursive());
				asm.asmInsert("not r2 r2",instr.isRecursive());
				asm.asmInsert("addi r2 r2 1",instr.isRecursive());
				asm.asmInsert("addi " + reg + " " + reg + " " + instr.getValue() ,instr.isRecursive());
			break;
			
			case Fixed.MUL:
				asm.asmInsert("ld r2 " + instr.getValue() + "-" + instr.getFuncName() ,instr.isRecursive());
				asm.asmInsert("mulr " + reg + " " + reg + " r2" ,instr.isRecursive());
			break;
		}
	}
	
	/*
	 * x = getparam0
	 * y = getparam1/2/3/4/5/6/7/8/9/10
	 * st r0 mem[x]
	 * st r1 mem[y]
	 * 
	 * reg8 = PC
	 * reg9 = CC
	 * reg10 = IR (Instruction Register)
	 */
	public void modifyGetParam(AssemblyText asm, Instruction instr) {
		asm.insertNoInstruction(instr);
		switch(instr.getValue()) {
			case "getparam0":
				asm.asmInsert("st r0 " + instr.getSymbol() + "-" 
						+ instr.getFuncName() ,instr.isRecursive());
			break;
			case "getparam1":
				asm.asmInsert("st r1 " + instr.getSymbol() + "-" 
						+ instr.getFuncName() ,instr.isRecursive());
			break;
			
			case "getparam2":
				asm.asmInsert("st r2 " + instr.getSymbol() + "-" 
						+ instr.getFuncName() ,instr.isRecursive());
			break;
			
			case "getparam3":
				asm.asmInsert("st r3 " + instr.getSymbol() + "-" 
						+ instr.getFuncName() ,instr.isRecursive());
			break;
			
			case "getparam4":
				asm.asmInsert("st r4 " + instr.getSymbol() + "-" 
						+ instr.getFuncName() ,instr.isRecursive());
			break;
			
			case "getparam5":
				asm.asmInsert("st r5 " + instr.getSymbol() + "-" 
						+ instr.getFuncName() ,instr.isRecursive());
			break;
			
			case "getparam6":
				asm.asmInsert("st r6 " + instr.getSymbol() + "-" 
						+ instr.getFuncName() ,instr.isRecursive());
			break;
			
			case "getparam7":
				asm.asmInsert("st r7 " + instr.getSymbol() + "-" 
						+ instr.getFuncName() ,instr.isRecursive());
			break;
			
			case "getparam8":
				asm.asmInsert("st r8 " + instr.getSymbol() + "-" 
						+ instr.getFuncName() ,instr.isRecursive());
			break;
			
			case "getparam9":
				asm.asmInsert("st r9 " + instr.getSymbol() + "-" 
						+ instr.getFuncName() ,instr.isRecursive());
			break;
			
			case "getparam10":
				asm.asmInsert("st r10 " + instr.getSymbol() + "-" 
						+ instr.getFuncName() ,instr.isRecursive());
			break;
			
			
		}
	}
	
	/*********************************
	 * letParamDeclaration()
	 * Faulty method I think
	 * 
	 */
	public void letRParamDeclaration(AssemblyText asm, Instruction instr) {
		asm.insertNoInstruction(instr);
			switch(instr.getValue()) {
				case "getparam0":
					asm.asmInsert("st r0 " + instr.getSymbol() + "-" +
							instr.getFuncName(),instr.isRecursive());
				break;
				
				case "getparam1":
					asm.asmInsert("st r1 " + instr.getSymbol() + "-" +
							instr.getFuncName(),instr.isRecursive());
				break;
				
				default:
					System.out.println("error: letRParamDeclaration - store");
				break;
			}
			
			switch(instr.getValue()) {
				case "getparam0":
					asm.asmDataAppend(".data#" + instr.getSymbol() + "-" 
							+ instr.getFuncName() + "#WORD#0#");
				break;
				
				case "getparam1":
					asm.asmDataAppend(".data#" + instr.getSymbol() + "-" 
							+ instr.getFuncName() + "#WORD#0#");
				break;
				
				default:
					System.out.println("error: letParamDeclaration - data");
				break;
			}
	}

}
