package lc3.compiler.parser;

import lc3.compiler.exception.LC3CompileException;
import lc3.compiler.utilities.Fixed;

public class RegisterParse {
	
	/*
	 * set param0 = x
	 * set param1 = y
	 * set param0 = 2
	 * set param0 = 1
	 */
	public void setDeclaration(Parser p, Instruction instr, Function func, String str) throws LC3CompileException {
		str = p.nextToken();
		switch(str) {
			case "param0":
			case "param1":
				instr.setInstructionType(Fixed.SET);
				modifyParamCase(p, instr, func, str);
			break;
			
			default:
				throw new LC3CompileException("Expecting param0/1");
		}
	}
	
	public void modifyParamCase(Parser p, Instruction instr, Function function, String str) 
			throws LC3CompileException {
		/*
		 * param0 = 1
		 * param0 + 1
		 * param0 + y
		 */
		//instr.setInstructionType(Fixed.MODIFY);
		switch(p.peekNextToken()) {
			case "=":
				instr.setModifyOperatorType(Fixed.EQU);
				modifyParamEqual(p, instr, function, str);
			break;
			
			case "+":
				instr.setModifyOperatorType(Fixed.ADD);
				p.modifyAddition(instr, function, str);
			break;
			
			case "-":
				instr.setModifyOperatorType(Fixed.SUB);
				p.modifySubtraction(instr, function, str);
			break;
			
			case "*":
				instr.setModifyOperatorType(Fixed.MUL);
				p.modifyMultiply(instr, function, str);
			break;
			
			default:
				throw new 
				LC3CompileException("expecting =/+/-/");
		}
	}
	
	public void modifyParamEqual(Parser p, Instruction instr, Function func, String str) 
			throws LC3CompileException {
		p.nextToken();
		p.notKW_Nxt();
		instr.setSymbol(str);
		instr.setValue(p.nextToken());
		instr.setVType(Fixed.NUM);
		instr.setFuncName(func);
		func.addModifyToInstruction(instr);
	}
	
	/*
	 * 		void letRDeclaration()
	 * 		letr x = getparam0
	 * 		letr y = getparam1
	 * 	
	 * 		x gets value stored in reg r0
	 * 		y gets value stored in reg r1
	 */
	public void letRDeclaration(Parser p, Instruction instr, Function func, String str) throws LC3CompileException {
		p.notKW_Nxt();
		instr.setSymbol(p.nextToken());
		p.expectingNxt("=");
		String val = p.nextToken();
		switch(val) {
			case "getparam0":
			case "getparam1":
				instr.setVType(Fixed.NUM);
				instr.setValue(val);
				instr.setFuncName(func);
				func.addInstruction(instr);
			break;
			
			default:
				throw new LC3CompileException("Expecting getparam0/1");
		}
	}
	
	public void modifyGetParam(Parser p, Instruction instr, Function func, String str) 
			throws LC3CompileException {
		/* getting the params
		 * x = getparam0
		 * y = getparam1
		 */
		instr.setSymbol(str);
		instr.setValue(p.nextToken());
		instr.setVType(Fixed.NUM);
		instr.setFuncName(func);
		func.addModifyToInstruction(instr);
	}
}
