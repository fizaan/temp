package lc3.compiler.assemble.conditions;

import lc3.compiler.assemble.AssemblyText;
import lc3.compiler.exception.LC3CompileException;
import lc3.compiler.parser.Function;
import lc3.compiler.parser.Instruction;

public class Branch {
	public void handleIF(AssemblyText asm, 
			Instruction instr, String operator, Function func) 
					throws LC3CompileException {
		
		/*
		 * Get the corresponding endif
		 * and jump to its label.
		 */
		asm.asmInsert("br " + operator + " " + 
				instr.getElse().getRandomLabel(),instr.isRecursive());
	}
	
	public void handleWhile(AssemblyText asm, 
			Instruction instr, String operator, Function func) 
					throws LC3CompileException {
		
		/*
		 * Get the corresponding endwhile
		 * and jump to its label.
		 */
		asm.asmInsert("br " + operator + " " + 
				instr.getEndwhile().getRandomLabel(),instr.isRecursive());
	}
	
	public void handleDoLoop(AssemblyText asm, 
			Instruction instr, String operator, Function func) 
					throws LC3CompileException {
		
		/*
		 * Get the corresponding "do"
		 * instruction and jump to 
		 * its label.
		 */
		asm.asmInsert("br " + operator + " " + 
				instr.getRandomLabel(),instr.isRecursive());
	}

}
