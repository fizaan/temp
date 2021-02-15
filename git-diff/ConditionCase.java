package lc3.compiler.assemble.conditions;

import lc3.compiler.assemble.AssemblyText;
import lc3.compiler.exception.LC3CompileException;
import lc3.compiler.parser.Function;
import lc3.compiler.parser.Instruction;
import lc3.compiler.parser.Parser;
import lc3.compiler.utilities.Fixed;

public class ConditionCase {
	
	public void checkConditionOperator(AssemblyText asm, 
			Instruction instr, Function func) 
			throws LC3CompileException {
		switch(instr.getConditionType()) {
			case Fixed.LT:
				conditionASM(asm, instr, func);
				if(instr.getInstructionType() == Fixed.DOLOOP)
					/*
					 *  we don't reverse 
					 *  condition if it's
					 *  a do-while loop
					 */
					branch(asm, instr, "<", func);
				else
					/*
					 * we only reverse 
					 * condition if it's
					 * a while loop or if 
					 * statement
					 */
					branch(asm, instr, ">=", func);
			break;
				
			case Fixed.LTE:
				conditionASM(asm, instr, func);
				if(instr.getInstructionType() == Fixed.DOLOOP)
					branch(asm, instr, "<=", func);
				else
					branch(asm, instr, ">", func);
				
			break;
			
			case Fixed.GT:	
				conditionASM(asm, instr, func);
				if(instr.getInstructionType() == Fixed.DOLOOP)
					branch(asm, instr, ">", func);
				else
					branch(asm, instr, "<=", func);
			break;
			
			case Fixed.GTE:
				conditionASM(asm, instr, func);
				if(instr.getInstructionType() == Fixed.DOLOOP)
					branch(asm, instr, ">=", func);
				else
					branch(asm, instr, "<", func);
			break;
			
			case Fixed.NOT_E:
				conditionASM(asm, instr, func);
				if(instr.getInstructionType() == Fixed.DOLOOP)
					branch(asm, instr, "!=", func);
				else
					branch(asm, instr, "==", func);
			break;
			
			case Fixed.EE:
				conditionASM(asm, instr, func);
				if(instr.getInstructionType() == Fixed.DOLOOP)
					branch(asm, instr, "==", func);
				else
					branch(asm, instr, "!=", func);
			break;
			
			default:
				throw new LC3CompileException("error: investigate");
		}
	}

	private void conditionASM(AssemblyText asm, 
			Instruction instr, Function func) 
			throws LC3CompileException {
		asm.insertNoInstruction(instr);
		
		switch(instr.getSymbol()) {
			case "param0":
				switch(instr.getVType()) {
				case Fixed.NUM:
					if(Parser.isNumeric(instr.getValue())) 
						handleR0R1NumericTypeCondition(asm, instr, "r0");
					else
						handleR0R1SymbolTypeCondition(asm, instr, "r0");
				return;
				
				default:
					throw new LC3CompileException("error: investigate");
			}
			
			case "param1":
				switch(instr.getVType()) {
				case Fixed.NUM:
					if(Parser.isNumeric(instr.getValue())) 
						handleR0R1NumericTypeCondition(asm, instr, "r0");
					else
						handleR0R1SymbolTypeCondition(asm, instr, "r0");
				return;
				
				default:
					throw new LC3CompileException("error: investigate");
			}
		
		}
		
		switch(instr.getVType()) {
			case Fixed.NUM:
				if(Parser.isNumeric(instr.getValue())) 
					handleNumericTypeCondition(asm, instr);
				else
					handleSymbolTypeCondition(asm, instr);
			break;
			
			default:
				throw new LC3CompileException("error: investigate");
		}
	}
	
	private void branch(AssemblyText asm, 
	Instruction instr, String operator, Function func)  
			throws LC3CompileException {
		switch(instr.getInstructionType()) {
			case Fixed.IF:
				new Branch().handleIF(asm, instr, operator, func);
			break;
			
			case Fixed.WHILE:
				new Branch().handleWhile(asm, instr, operator, func);
			break;
			
			case Fixed.DOLOOP:
				new Branch().handleDoLoop(asm, instr, operator, func);
			break;
			
			default:
				throw new LC3CompileException("error: investigate");
		}
	}
	
	/*
	 * if x < 5
	 * while x < 5
	 * loop x < 5
	 */
	private void handleNumericTypeCondition(AssemblyText asm, Instruction instr) {
		asm.asmInsert(";Loop begins for symbol case: x < y",instr.isRecursive());
		asm.asmInsert("ld r0 " + instr.getSymbol() + "-" + instr.getFuncName()
			+ " _" + instr.getRandomLabel(),instr.isRecursive());
		asm.asmInsert("movi r1 " + instr.getValue(),instr.isRecursive());
		asm.asmInsert("cmp r0 r1",instr.isRecursive());
		
	}
	
	/* The symbol here is y.
	 * if x < y
	 * while x < y
	 * loop x < y
	 */
	private void handleSymbolTypeCondition(AssemblyText asm, Instruction instr) {
		asm.asmInsert(";Loop begins for symbol case: x < y",instr.isRecursive());
		asm.asmInsert("ld r0 " + instr.getSymbol() + "-" + instr.getFuncName() 
			+ " _" + instr.getRandomLabel(),instr.isRecursive());
		asm.asmInsert("ld r1 " + instr.getValue() + "-" + instr.getFuncName(),instr.isRecursive());
		asm.asmInsert("cmp r0 r1",instr.isRecursive());
	}
	
	/*
	 * if param0/1 < 5
	 * while param0/1 < 5
	 * loop param0/1 < 5
	 */
	private void handleR0R1NumericTypeCondition(AssemblyText asm, 
			Instruction instr, String reg) {
		asm.asmInsert("movi r1 " + instr.getValue(),instr.isRecursive());
		asm.asmInsert(";dummy for: while-loop big fix",instr.isRecursive());
		asm.asmInsert("movr r0 r0" + " _" + instr.getRandomLabel(),instr.isRecursive());
		asm.asmInsert("cmp " + reg + " r1",instr.isRecursive());
		
	}
	
	/* The symbol here is y.
	 * if param0/1 < y
	 * while param0/1 < y
	 * loop param0/1 < y
	 */
	private void handleR0R1SymbolTypeCondition(AssemblyText asm, 
			Instruction instr, String reg) {
		asm.asmInsert(";Loop begins for symbol case: r0/r0 < y",instr.isRecursive());
		asm.asmInsert("ld r1 " + instr.getValue() + "-" + instr.getFuncName()
			+ " _" + instr.getRandomLabel(),instr.isRecursive());
		asm.asmInsert(";dummy for: while-loop big fix",instr.isRecursive());
		asm.asmInsert("movr r0 r0" + " _" + instr.getRandomLabel(),instr.isRecursive());
		asm.asmInsert("cmp " + reg + " r1",instr.isRecursive());
	}
	
}
