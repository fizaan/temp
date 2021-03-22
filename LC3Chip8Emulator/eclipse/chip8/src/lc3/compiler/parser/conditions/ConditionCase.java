package lc3.compiler.parser.conditions;

import lc3.compiler.exception.LC3CompileException;
import lc3.compiler.parser.Function;
import lc3.compiler.parser.Instruction;
import lc3.compiler.parser.Parser;
import lc3.compiler.utilities.Fixed;
import lc3.compiler.utilities.RandomLabelGenerator;

public class ConditionCase {
	
	public void condition(Parser p, Instruction instr, Function func, String str) throws LC3CompileException {
		p.notKW_Nxt_Exception(new String[] { "param0", "param1" });
		instr.setSymbol(p.nextToken());
		instr.setRandomLabel(new RandomLabelGenerator().generateLabel());
		checkConditionOperator(p, instr, func, str);
		str = p.nextToken();
		p.notKW_Nxt();
		
		/*
		 * Why am I calling
		 * p.commonCase...?
		 * Not sure. Something
		 * to look into maybe.
		 * All I know is we need
		 * it.
		 */
		p.commonCaseDeclaration(instr,func,str);
	}
	
	public void checkConditionOperator(Parser p, Instruction instr, Function function, String str) 
			throws LC3CompileException {
		switch(p.peekNextToken()) {
			case "<":
				instr.setConditionType(Fixed.LT);
				
			break;
			
			case "<=":
				instr.setConditionType(Fixed.LTE);
				
			break;
			
			case ">":
				instr.setConditionType(Fixed.GT);
				
			break;
			
			case ">=":
				instr.setConditionType(Fixed.GTE);
				
			break;
			
			case "!=":
				instr.setConditionType(Fixed.NOT_E);
				
			break;
			
			case "==":
				instr.setConditionType(Fixed.EE);
				
			break;
			
			default:
				throw new 
				LC3CompileException("expecting < <= > >= !=");
		}

	}
}
