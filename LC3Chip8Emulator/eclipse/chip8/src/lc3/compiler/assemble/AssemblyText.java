package lc3.compiler.assemble;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import lc3.compiler.assemble.conditions.ConditionCase;
import lc3.compiler.exception.LC3CompileException;
import lc3.compiler.parser.Function;
import lc3.compiler.parser.Instruction;
import lc3.compiler.parser.Parser;
import lc3.compiler.utilities.Fixed;
import lc3.compiler.utilities.RandomLabelGenerator;

public class AssemblyText {
	private StringBuilder asm, data, unitarray, 
		fillintarray, rec, division;
	public Stack<String> stack = new Stack<String>();
	
	/* funcLabel:
	 * a very stupid, but effective
	 * way to label functions.
	 */
	private byte funcLabel = 0x0;
	
	public AssemblyText(ArrayList<Function> func) {
		asm = new StringBuilder();
		data = new StringBuilder();
		unitarray = new StringBuilder();
		fillintarray = new StringBuilder();
		rec = new StringBuilder();
		division = new StringBuilder();
		begin();
		asmInsertNL();
		try {
			parse(func);
		}
		catch(LC3CompileException e) {
			e.printStackTrace();
		}
		asmInsertNL();
		asmInsertNL();
		asmInsert(data.toString(),false);
		asmAddStackNewLine();
		asmInsert(unitarray.toString(), false);
		asmInsert(fillintarray.toString(), false);
	}
	
	private void begin() {
		asmInsert(".start 3000",false);
		asmInsert("lea r6 stack",false);
	}
	
	private void asmInsertHalt() {
		asmInsert("halt",false);
		//asmInsert(division.toString(), false);
		asm.append(division.toString());
	}
	
	private void asmAddStackNewLine() {
		asmInsert(".data#newline#HEX#0A",false); 
		asmInsert(".data#stack#ARRAY#100",false);
	}
	
	public void asmInsert(String s, boolean recursive) {
		if(recursive)
			recursiveInsert(s);
		else {
			asm.append(s);
			asmInsertNL();
			
			if(
				s.indexOf("br ") > -1 ||
				s.indexOf("ld ") > -1 ||
				s.indexOf("lea ") > -1 ||
				s.indexOf("leaf ") > -1 ||
				s.indexOf("ldi ") > -1 ||
				s.indexOf("jsr ") > -1 ||
				s.indexOf("st ") > -1 ||
				s.indexOf("sti ") > -1
			) {
				if(s.indexOf("asm")==-1) {
					asm.append("abs");
					asmInsertNL();
				}
			}
		}	
	}
	
	public void recursiveInsert(String s) {
		rec.append(s);
		rec.append("\n");
		if(
				s.indexOf("br ") > -1 ||
				s.indexOf("ld ") > -1 ||
				s.indexOf("lea ") > -1 ||
				s.indexOf("leaf ") > -1 ||
				s.indexOf("ldi ") > -1 ||
				s.indexOf("jsr ") > -1 ||
				s.indexOf("st ") > -1 ||
				s.indexOf("sti ") > -1
			) {
				if(s.indexOf("asm")==-1) {
					rec.append("abs");
					rec.append("\n");
				}
			}
	}
	
	public void asmDataAppend(String s) {
		data.append(s);
		data.append("\n");
	}
	
	private void asmInsertNL() {
		asm.append("\n");
	}
	
	public void parse(ArrayList<Function> func) throws LC3CompileException {
		for(Function f:func) {
			ArrayList<Instruction> instrList = f.getInstructions();
			funcLabel = 0x0;
			for(Instruction instr: instrList) {
				byte instrType = instr.getInstructionType();
				switch(instrType) {
					//let x = 5
					//let x = "hey man"
					//public x = 5
					case Fixed.NEW_DECL:
						declarationData(instr);
					break;
					
					case Fixed.SET:
						new RegisterASM().caseSet(this, instr);
						funcLabel = 0x1;
					break;
					
					//param0 = x
					//param0 = "hey man"
					case Fixed.PAR0:
						new RegisterASM().declareParam(this, instr, "r0", false);
						funcLabel = 0x1;
					break;
					
					//param1 = x
					//param1 = "hey man"
					case Fixed.PAR1:
						new RegisterASM().declareParam(this, instr, "r1", false);
						funcLabel = 0x1;
					break;
					
					//x = 7
					//let y = 0
					//y = getparam0
					//let z = 0
					//z = getparam1
					case Fixed.MODIFY:
						modify(instr);
						funcLabel = 0x1;
					break;
					
					//print x
					//print 5
					//print "hi"
					case Fixed.SYS_PRN:
						dataDynamicDeclare(instr);
						sysPrint(instr, false);
						funcLabel = 0x1;
					break;
					
					//println x
					//println 5
					//println "hi"
					case Fixed.SYS_PRNLN:
						dataDynamicDeclare(instr);
						sysPrint(instr, true);
						funcLabel = 0x1;
					break;
					
					//return
					case Fixed.RETURN:
						insertReturn();
						funcLabel = 0x1;
					break;
					
					//function (jsr funcA)
					case Fixed.FUNC:
						insertCallToFunction(f.nextChild());
						funcLabel = 0x1;
					break;
					
					case Fixed.LET_X_GETPARAM:
						new RegisterASM().letRParamDeclaration(this, instr);
						funcLabel = 0x1;
					break;
					
					case Fixed.IF:
					case Fixed.WHILE:
						new ConditionCase().checkConditionOperator(this, instr, f);
						funcLabel = 0x1;
					break;
					
					case Fixed.ELSE:
						/*
						 * also need to jump out
						 * of if to endif.
						 * 	
						 */
						asmInsert(";To jump out of if:",instr.isRecursive());
						asmInsert(";Branch always:",instr.isRecursive());
						asmInsert("br <> " + instr.getEndif().getRandomLabel(),instr.isRecursive());
						asmInsert(";Dummy for else",instr.isRecursive());
						asmInsert("movr r0 r0 _" + instr.getRandomLabel(),instr.isRecursive());
						funcLabel = 0x1;
					break;
					
					case Fixed.ENDIF:
						asmInsert(";Dummy for: endif",instr.isRecursive());
						asmInsert("movr r0 r0 _" + instr.getRandomLabel(),instr.isRecursive());
						funcLabel = 0x1;
					break;
					
					case Fixed.ENDWHILE:
						/*
						 * loop forever, or goto
						 * after endwhile dummy.
						 */
						asmInsert(";loop always",instr.isRecursive());
						asmInsert("br <> " + instr.getWhil().getRandomLabel(),instr.isRecursive());
						asmInsert(";Dummy for: endwhile",instr.isRecursive());
						asmInsert("movr r0 r0 _" + instr.getRandomLabel(),instr.isRecursive());
						funcLabel = 0x1;
					break;
					
					case Fixed.DO:
						asmInsert(";Dummy for: do-loop",instr.isRecursive());
						asmInsert("movr r0 r0 _" + instr.getRandomLabel(),instr.isRecursive());
						funcLabel = 0x1;
					break;
					
					case Fixed.DOLOOP:
						new ConditionCase().checkConditionOperator(this, instr, f);
						funcLabel = 0x1;
					break;
					
					case Fixed.STRLEN:
						strLen(instr);
						funcLabel = 0x1;
					break;
					
					case Fixed.ADDRESS_OF_VARIABLE:
						addressOf(instr);
						funcLabel = 0x1;
					break;
					
					case Fixed.VALUE_AT_ADDRESS:
						valueAt(instr);
						funcLabel = 0x1;
					break;
					
					case Fixed.INDEX:
						insertAtIndex(instr);
						funcLabel = 0x1;
					break;
					
					case Fixed.GETPUBLIC:
						getPublicVar(instr);
						funcLabel = 0x1;
					break;
					
					case Fixed.SETPUBLIC:
						setPublicVar(instr);
						funcLabel = 0x1;
					break;
					
					case Fixed.RANDINTARR:
						data.append(".data#"+
								instr.getSymbol() + "-" +
								instr.getFuncName() + "#RANDINTARRAY#"+
								instr.getValue());
						data.append("\n");
					break;
					
					case Fixed.UNINITARR:
						unitarray.append(".data#"+
								instr.getSymbol() + "-" +
								instr.getFuncName() + "#ARRAY#"+
								instr.getValue());
						unitarray.append("\n");
					break;	
					
					case Fixed.ASM:
						asmInsert(instr.getValue(),instr.isRecursive());
					break;
					
					case Fixed.LABEL:
						asmInsert("movr r0 r0" + " _" + instr.getValue(),instr.isRecursive());
					break;
					
					case Fixed.JMP:
						asmInsert("leaf r2 " + instr.getValue(),instr.isRecursive());
						asmInsert("jmp r2",instr.isRecursive());
					break;
					
					case Fixed.FIA:
						fillintarray.append(".data#");
						fillintarray.append(instr.getSymbol());
						fillintarray.append("-");
						fillintarray.append(instr.getFuncName());
						fillintarray.append("#FILLINTARRAY#");
						fillintarray.append(instr.getValue());
						fillintarray.append("\n");
					break;
					
					default:
						throw new LC3CompileException("No type for instruction: " + instr);
				}
			}
			
			if(f.getSymbol().equals("main"))
				asmInsertHalt();
		}
	}
	
	/* puts the value of x into
	 * yPublic var 
	   Code example:
	   		setpublic yPublic x
	 */
	private void setPublicVar(Instruction instr) {
		insertNoInstruction(instr);
		asmInsert("ld r0 " +
				instr.getValue() + "-" +
				instr.getFuncName(), instr.isRecursive());
		asmInsert("st r0 " + 
				instr.getSymbol() + "-global",instr.isRecursive());
		
	}

	/* puts the value of yPublic into
	 * x var 
	   Code example:
	   		getpublic x yPublic	
	 */
	private void getPublicVar(Instruction instr) {
		insertNoInstruction(instr);
		asmInsert("ld r0 " + 
				instr.getValue() + "-global",instr.isRecursive());
		asmInsert("st r0 " +
				instr.getSymbol() + "-" +
				instr.getFuncName(), instr.isRecursive());
	}

	/* void insertAtIndex():
	 * puts the value y at address x  
	   Code example:
	   		index x y	
	 */
	private void insertAtIndex(Instruction instr) {
		insertNoInstruction(instr);
		asmInsert("ld r0 " + 
				instr.getValue() + "-" + 
				instr.getFuncName(),instr.isRecursive());
		asmInsert("ld r1 " + 
				instr.getSymbol() + "-" + 
				instr.getFuncName(),instr.isRecursive());
		asmInsert("str r0 r1 0",instr.isRecursive());
		
	}

	/* void strlen():
	 * puts the length of str into x.
	 * let str = "ABC"
	   let x = 0
	   
	   Code example:
	   		strlen x str	
	 */
	private void strLen(Instruction instr) {
		insertNoInstruction(instr);
		asmInsert("ld r1 " + 
				instr.getValue() + "-" + 
				instr.getFuncName() + "-strlen",instr.isRecursive());
		asmInsert("st r1 " +
				instr.getSymbol() + "-" +
				instr.getFuncName(), instr.isRecursive());
	}
	
	/* void addressOf():
	 * puts the address of y into x.
	 * So, x becomes a pointer variable...
	 * I think?
	 * let y = ...
	   let x = 0
	   
	   Code example:
	   		addrof x y
	 */
	private void addressOf(Instruction instr) {
		insertNoInstruction(instr);
		asmInsert("lea r1 " + 
				instr.getValue() + "-" + 
				instr.getFuncName(),instr.isRecursive());
		asmInsert("st r1 " +
				instr.getSymbol() + "-" +
				instr.getFuncName(), instr.isRecursive());
	}
	
	/* void valueAt():
	 * puts the VALUE found at address 
	 * (stored in variable y), into x.
	 * So, x gets the value stored in address y
	 * I think?
	 * let y = ...
	   let x = 0
	   Code example:
	   		valat x y	
	 */
	private void valueAt(Instruction instr) {
		insertNoInstruction(instr);
		asmInsert("ld r0 " + 
				instr.getValue() + "-" + 
				instr.getFuncName(),instr.isRecursive());
		asmInsert("ldr r1 r0 0",instr.isRecursive());
		asmInsert("st r1 " +
				instr.getSymbol() + "-" +
				instr.getFuncName(), instr.isRecursive());
	}
	
	/*
	 * child function:
	 * 
	 */
	private void insertCallToFunction(Function f) {
		if(f == null) return;
		asmInsert("jsr " + f.getSymbol(),false);
	}
	
	private void handleModifyNumeric(Instruction instr) {
		switch(instr.getModifyOperatorType()) {
			case Fixed.EQU:
				asmInsert("movi r2 " + instr.getValue(),instr.isRecursive());
				asmInsert("st r2 " + instr.getSymbol() + "-" + instr.getFuncName() ,instr.isRecursive());
			break;
			
			case Fixed.ADD:
				asmInsert("ld r1 " + instr.getSymbol() + "-" + instr.getFuncName() ,instr.isRecursive());
				asmInsert("movi r2 " + instr.getValue(),instr.isRecursive());
				asmInsert("addr r1 r1 r2",instr.isRecursive());
				asmInsert("st r1 " + instr.getSymbol() + "-" + instr.getFuncName() ,instr.isRecursive());
			break;
			
			case Fixed.AND:
				asmInsert("ld r1 " + instr.getSymbol() + "-" + instr.getFuncName() ,instr.isRecursive());
				asmInsert("movi r2 " + instr.getValue(),instr.isRecursive());
				asmInsert("andr r1 r1 r2",instr.isRecursive());
				asmInsert("st r1 " + instr.getSymbol() + "-" + instr.getFuncName() ,instr.isRecursive());
			break;
			
			case Fixed.SUB:
				asmInsert("ld r1 " + instr.getSymbol() + "-" + instr.getFuncName() ,instr.isRecursive());
				asmInsert("movi r2 " + instr.getValue(),instr.isRecursive());
				asmInsert("not r2 r2",instr.isRecursive());
				asmInsert("addi r2 r2 1",instr.isRecursive());
				asmInsert("addr r1 r1 r2",instr.isRecursive());
				asmInsert("st r1 " + instr.getSymbol() + "-" + instr.getFuncName() ,instr.isRecursive());
			break;
			
			case Fixed.MUL:
				asmInsert("ld r1 " + instr.getSymbol() + "-" + instr.getFuncName() ,instr.isRecursive());
				asmInsert("movi r2 " + instr.getValue(),instr.isRecursive());
				asmInsert("mulr r1 r1 r2",instr.isRecursive());
				asmInsert("st r1 " + instr.getSymbol() + "-" + instr.getFuncName() ,instr.isRecursive());
			break;
			
			case Fixed.DIV:
				String randomLabelA = new RandomLabelGenerator().generateLabel();
				String randomLabelB = new RandomLabelGenerator().generateLabel();
				//clear r0 r2:
				asmInsert("andi r0 r0 0",instr.isRecursive());
				asmInsert("andi r2 r2 0",instr.isRecursive());
				asmInsert("ld r0 " + instr.getSymbol() + "-" + instr.getFuncName() ,instr.isRecursive());
			    asmInsert("push r0",instr.isRecursive());
			    asmInsert("movi r1 -" + instr.getValue(),instr.isRecursive());
			    asmInsert("addr r0 r0 r1 _"+randomLabelA,instr.isRecursive());
			    asmInsert("br 011 "+randomLabelB,instr.isRecursive());
			    asmInsert("movi r1 3",instr.isRecursive());
	    		asmInsert("mulr r1 r1 r2",instr.isRecursive());
	    		asmInsert("not r1 r1",instr.isRecursive());
	    		asmInsert("addi r1 r1 1",instr.isRecursive());
	    		asmInsert("pop r0",instr.isRecursive());
	    		asmInsert("addr r0 r0 r1",instr.isRecursive());
	    		//This goes after HALT
	    		division.append("addi r2 r2 1 _"+randomLabelB+"\n");
	    		division.append("jsr "+randomLabelA+"\n");
	    		division.append("abs"+"\n");
			break;
		}
	}
	
	private void handleModifyVariable(Instruction instr) {
		/*
		 * remember! getValue() is a symbol in this case:
		 * x = y
		 * x + y etc
		 */
		switch(instr.getModifyOperatorType()) {
			case Fixed.EQU:
				asmInsert("ld r2 " + instr.getValue() + "-" + instr.getValueInstruction().getFuncName() ,instr.isRecursive());
				asmInsert("st r2 " + instr.getSymbol() + "-" + instr.getFuncName() ,instr.isRecursive());
			break;
			
			case Fixed.ADD:
				asmInsert("ld r1 " + instr.getSymbol() + "-" + instr.getFuncName(),instr.isRecursive());
				asmInsert("ld r2 " + instr.getValue() + "-" + instr.getValueInstruction().getFuncName(),instr.isRecursive());
				asmInsert("addr r1 r1 r2",instr.isRecursive());
				asmInsert("st r1 " + instr.getSymbol() + "-" + instr.getFuncName() ,instr.isRecursive());
			break;
			
			case Fixed.AND:
				asmInsert("ld r1 " + instr.getSymbol() + "-" + instr.getFuncName(),instr.isRecursive());
				asmInsert("ld r2 " + instr.getValue() + "-" + instr.getValueInstruction().getFuncName(),instr.isRecursive());
				asmInsert("andr r1 r1 r2",instr.isRecursive());
				asmInsert("st r1 " + instr.getSymbol() + "-" + instr.getFuncName() ,instr.isRecursive());
			break;
			
			case Fixed.SUB:
				asmInsert("ld r1 " + instr.getSymbol() + "-" + instr.getFuncName() ,instr.isRecursive());
				asmInsert("ld r2 " + instr.getValue() + "-" + instr.getValueInstruction().getFuncName(),instr.isRecursive());
				asmInsert("not r2 r2",instr.isRecursive());
				asmInsert("addi r2 r2 1",instr.isRecursive());
				asmInsert("addr r1 r1 r2",instr.isRecursive());
				asmInsert("st r1 " + instr.getSymbol() + "-" + instr.getFuncName(),instr.isRecursive());
			break;
			
			case Fixed.MUL:
				asmInsert("ld r1 " + instr.getSymbol() + "-" + instr.getFuncName() ,instr.isRecursive());
				asmInsert("ld r2 " + instr.getValue() + "-" + instr.getValueInstruction().getFuncName() ,instr.isRecursive());
				asmInsert("mulr r1 r1 r2",instr.isRecursive());
				asmInsert("st r1 " + instr.getSymbol() + "-" + instr.getFuncName() ,instr.isRecursive());
			break;
			
			case Fixed.DIV:
				String randomLabelA = new RandomLabelGenerator().generateLabel();
				String randomLabelB = new RandomLabelGenerator().generateLabel();
				//clear r0 r2:
				asmInsert("andi r0 r0 0",instr.isRecursive());
				asmInsert("andi r2 r2 0",instr.isRecursive());
				asmInsert("ld r0 " + instr.getSymbol() + "-" + instr.getFuncName() ,instr.isRecursive());
			    asmInsert("push r0",instr.isRecursive());
			    asmInsert("ld r1 " + instr.getValue() + "-" + instr.getValueInstruction().getFuncName() ,instr.isRecursive());
			    asmInsert("not r1 r1",instr.isRecursive());
				asmInsert("addi r1 r1 1",instr.isRecursive());
				asmInsert("addr r0 r0 r1 _"+randomLabelA,instr.isRecursive());
				asmInsert("br 011 "+randomLabelB,instr.isRecursive());
			    asmInsert("movi r1 3",instr.isRecursive());
	    		asmInsert("mulr r1 r1 r2",instr.isRecursive());
	    		asmInsert("not r1 r1",instr.isRecursive());
	    		asmInsert("addi r1 r1 1",instr.isRecursive());
	    		asmInsert("pop r0",instr.isRecursive());
	    		asmInsert("addr r0 r0 r1",instr.isRecursive());
	    		//This goes after HALT
	    		division.append("addi r2 r2 1 _"+randomLabelB+"\n");
	    		division.append("jsr "+randomLabelA+"\n");
	    		division.append("abs"+"\n");
			break;
		}
	}
	
	/*
	 * What's "-"?
	 * - See data() method
	 */
	private void modify(Instruction instr) throws LC3CompileException {
		if(instr.getValue().startsWith("get")) {
			new RegisterASM().modifyGetParam(this, instr);
			return;
		}
			
		insertNoInstruction(instr);
		switch(instr.getVType()) {
			case Fixed.NUM:
				if(Parser.isNumeric(instr.getValue())) {
					/*
					 * x = 5
					 * x + 1
					 * x - 1
					 * x * 3
					 * param0 = 1
					 * param0 + 1
					 * param0 - 1
					 * param0 * 5
					 */
					handleModifyNumeric(instr);
				}
				else {
					/*
					 * x = y
					 * x + y
					 * x - y
					 * x * y
					 * param0 = y
					 * param0 + y
					 * param0 - y
					 * param0 * y
					 * 
					 * x is the symbol
					 * y is the value
					 */
					handleModifyVariable(instr);
				}
			break;
			
			case Fixed.STR:
				System.out.println("AssemblyText.java: Not implemented: " + instr);
			break;
			
			case Fixed.LET_X_GETPARAM:
				System.out.println("error: modify()");
			break;
			
			default:
				throw new LC3CompileException("No case found for modify");
			
		}
	}
	
	/*
	 * dataDynamicDeclare():
	 * This is a special case because
	 * getSymbol() is actually either
	 * print or println as set in parser
	 * system() function. In usual
	 * cases like let or modify, the
	 * symbol is a variable...but in
	 * this special case it is a keyword
	 * (print or println)
	 * so it has it's own function.
	 */
	private void dataDynamicDeclare(Instruction instr) {
		switch(instr.getVType()) {
			case Fixed.SYS_PRNLN_STR:
			case Fixed.SYS_PRN_STR:
				RandomLabelGenerator rg = new
						RandomLabelGenerator();
				String label = rg.generateLabel();
				instr.setRandomLabel(label);
				int strBegin = instr.getValue().indexOf("\"");
				String val = instr.getValue().substring(strBegin+1,
						instr.getValue().length()-1);
				asmDataAppend(".data#" + label + "-" + instr.getFuncName() + "#STR#" +
						 val + "#");
			break;
		}
	}
	
	/* private void data(Instruction instr)
	 * 
	 * All function variables are local to each
	 * function which means they may have same
	 * variable names. We need to distinguish them
	 * otherwise two x's or y's will share the same
	 * address.
	 * 
	 * So.. Fuction main's variables will be called
	 * 			x-main, y-main etc
	 * 		Function temp's variables will be called
	 * 			x-temp, y-temp etc.
	 */
	private void declarationData(Instruction instr) {
		switch(instr.getVType()) {
			/*
			 * case: NUM
			 * let x = 5
			 */
			case Fixed.NUM: 
				asmDataAppend(".data#" + 
						instr.getSymbol() + "-" 
						+ instr.getFuncName() + "#WORD#" +
						 instr.getValue() + "#");
			break;
			
			/*
			 * case: STR
			 * let x = "hi there"
			 */
			case Fixed.STR:
				int strBegin = instr.getValue().indexOf("\"");
				String val = instr.getValue().substring(strBegin+1,
							instr.getValue().length()-1);
				asmDataAppend(".data#" + instr.getSymbol() + "-" 
							+ instr.getFuncName() + "#STR#" +
							val + "#");
				asmDataAppend(".data#" + instr.getSymbol() + "-" 
						+ instr.getFuncName() + "-strlen#WORD#" +
						instr.getStrLen() + "#");
			break;	
			
			default:
				System.out.println("error: declarationData");
			break;
		}
	}
	
	/*
	 * void param()
	 * 
	 * function parametres are stored in
	 * registers r0 and r1. I am not
	 * storing the type (int or string).
	 */
	
	private void sysPrint(Instruction instr, boolean newLine) 
			throws LC3CompileException {
		insertNoInstruction(instr);
		switch(instr.getVType()) {
			case Fixed.SYS_PRN_STR:
			case Fixed.SYS_PRNLN_STR:
				/*
				 * case:
				 * print "hi there"
				 * println "hi there"
				 */
				asmInsert("lea r0 " + instr.getRandomLabel() + "-" + instr.getFuncName() ,instr.isRecursive());
				if(instr.isPrintRaw())
					asmInsert("out",instr.isRecursive());
				else
					asmInsert("puts",instr.isRecursive());
			break;
			
			case Fixed.STR:
				/*
				 * case:
				 * print str1, where str1 = "hi there"
				 * println str1, where str1 = "hi there"
				 * 	
				 */
				asmInsert("lea r0 " + instr.getValue() + "-" + instr.getFuncName() ,instr.isRecursive());
				if(instr.isPrintRaw())
					asmInsert("out",instr.isRecursive());
				else
					asmInsert("puts",instr.isRecursive());
			break;
			
			case Fixed.NUM:
				if (Parser.isNumeric(instr.getValue()))
					asmInsert("movi r0 " + instr.getValue() ,instr.isRecursive());
					/*
					 * if case:
					 * print 5
					 * println 5
					 * 	
					 */
				else
					asmInsert("ld r0 " + instr.getValue() + "-" + 
							instr.getValueInstruction().getFuncName() ,instr.isRecursive());
					/*
					 * else case:
					 * print x, where x = 5
					 * println x,  where x = 5
					 */
				if(instr.isPrintRaw())
					asmInsert("out",instr.isRecursive());
				else
					asmInsert("outr r0",instr.isRecursive());
			break;
			
			case Fixed.PAR0:
				if(instr.isPrintRaw())
					asmInsert("out",instr.isRecursive());
				else
					asmInsert("outr r0",instr.isRecursive());
			break;
			
			case Fixed.PAR1:
				if(instr.isPrintRaw())
					asmInsert("out",instr.isRecursive());
				else
					asmInsert("outr r1",instr.isRecursive());
			break;
		
			case Fixed.LET_X_GETPARAM:
			default:
				throw new LC3CompileException("No case for print/println");
		}
		
		if(newLine) {
			asmInsert("ld r0 newline",instr.isRecursive());
			asmInsert("out",instr.isRecursive());
		}
		
		instr.setPrintRaw(false);
	}
	
	public void generateASMFile(File file) throws IOException {
		FileWriter fw = new FileWriter(file);
		fw.write(asm.toString());
		fw.flush();
		fw.close();
	}
	
	public String toString() {
		return asm.toString();
	}
	
	public void insertNoInstruction(Instruction instr) {
		/*
		 * Dummy Instruction 
		 * just to add label 
		 * 
		 * All functions/methods
		 * get a label, except main
		 * 
		 * movr r0 r0 
		 * does nothing.
		 */
		
		if(funcLabel == 0x0) {
			if(instr.getFuncName().equals("main")) {
				asmInsert("/*************************************",instr.isRecursive());
				asmInsert("The function: " + instr.getFuncName(),instr.isRecursive());
				asmInsert("*************************************/",instr.isRecursive());
				return;
			}	
			asmInsertNL();
			asmInsert("/*************************************",instr.isRecursive());
			asmInsert("The function: " + instr.getFuncName(),instr.isRecursive());
			asmInsert("..movr is just a dummy",instr.isRecursive());
			asmInsert("..that does nothing.",instr.isRecursive());
			asmInsert("..I'm using it to point",instr.isRecursive());
			asmInsert("..to the label of the func.",instr.isRecursive());
			asmInsert("*************************************/",instr.isRecursive());
			asmInsert("movr r0 r0" + " _" + instr.getFuncName(),instr.isRecursive());
			
			/*
			 * The following helps with recursion
			 * inadvertently. Meaning we always push
			 * values to the stack (especially the r7
			 * register which holds the return address)
			 * regardless if a function call is recursive 
			 * or not, but having done so, it automatically
			 * helps with recursive calls to a function. 
			 * E.g:
			 * 		function recPrint(x) {
			 * 			if(x > 0) {
			 * 				print x
			 * 				recPrint(x-1)
			 * 				print x
			 *			}
			 * 		}
			 * 
			 * But..there is a bug in it.
			 */
			asmInsert("push r7",instr.isRecursive());
			asmInsert("push r0",instr.isRecursive());
			asmInsert("push r1",instr.isRecursive());
			stack.push("pop r7");
			stack.push("pop r0");
			stack.push("pop r1");
		}
	}
	
	/*
	 * return:
	 */
	private void insertReturn() {
		while(!stack.isEmpty())
			asmInsert(stack.pop(),false);
		/*
		 * recrusive instructions
		 * go here just before
		 * return :)
		 */
		//asmInsert(rec.toString(),false);
		//asmInsert("ret",false);
		asm.append(rec.toString());
		asm.append("ret");
	}

}
