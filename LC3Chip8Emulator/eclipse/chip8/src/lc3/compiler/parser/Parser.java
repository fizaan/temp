package lc3.compiler.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;
import lc3.compiler.utilities.Fixed;
import lc3.compiler.utilities.Keyword;
import lc3.compiler.utilities.RandomLabelGenerator;
import lc3.compiler.exception.LC3CompileException;
import lc3.compiler.parser.conditions.ConditionCase;
import lc3.compiler.utilities.Utility;

public class Parser {
	private ArrayList<String> srcCode;
	private ArrayList<Function> functions;
	private boolean hasMainMethod;
	private int count;
	private Stack<String> stack;
	private Stack<Instruction> conditionStack; 
	
	
	public Parser() {
		srcCode = new ArrayList<String>();
		functions = new ArrayList<Function>();
		count = 0;
		hasMainMethod = false;
		stack = new Stack<>();
		conditionStack = new Stack<>();
	}
	
	public void loadSrcCode(File file) throws Exception {
		srcCode = Utility.readSourceCode(file);
		
		/*
		 * Number of passes:
		 * parseFunctions();
		 * parseInstructions()
		 * 
		 * note: the ordering of 
		 * execution call matters.
		 */
		parseFunctions();
		putMainMethodOnTop();
		parseInstructions();
		checkReturnsForAllFunc();
	}
	
	private void putMainMethodOnTop() {
		/*
		 * If main is already on top
		 * ignore and return
		 */
		Function temp = functions.get(0);
		if(temp.equals("main"))
			return;
		
		/*
		 * Else...swap with top most
		 * function.
		 */
		for(int i = 0; i <functions.size(); i++) {
			Function func = functions.get(i);
			if(func.equals("main")) {
				//swap
				functions.set(0, func);
				functions.set(i, temp);
			}
		}
	}
	
	private void checkReturnsForAllFunc( ) throws LC3CompileException {
		if(functions.size()==1) return;
		for(int i = 1; i < functions.size(); i++) {
			if(functions.get(i).equals("global"))
				continue;
			if(!functions.get(i).returns()) 
				throw new LC3CompileException("missing return for: " + 
						functions.get(i).getSymbol());
		}
	}
	
	public void parseFunctions() throws LC3CompileException {
		Function func;
		while(hasMoreTokens()) {
			switch(nextToken()) {
				case "function":
					notKW_Nxt();
					func = new Function();
					String str = nextToken();
					switch(str) {
						case "main":
							hasMainMethod = true;
							checkDuplicate(str);
							func.setSymbol(str);
							functions.add(func);
						break;
						
						case "halt":
							throw new LC3CompileException("reserved: " + str);
						
						default:
							checkDuplicate(str);
							func.setSymbol(str);
							functions.add(func);
						break;
					}//end inner switch
				break;
				
				case "global":
					str = "global";
					func = new Function();
					checkDuplicate(str);
					func.setSymbol(str);
					functions.add(func);
				break;
				
				default:
						//do nothing.
						//..only parsing
						//..functions
						//..and globals
				break;
			} //end outer switch
		} //end while
		
		if (functions.isEmpty())
			throw new LC3CompileException("No functions found");
		
		if (!hasMainMethod)
			throw new LC3CompileException("No main function");
		
		reset();
	}
	
	private Function getFunction(String func) throws LC3CompileException {
		for(Function f: functions)
			if(f.equals(func))
				return f;
		throw new LC3CompileException("Function not found: " + func);
	}
	
	private void checkDuplicate(String str) throws LC3CompileException {
		if (functions.isEmpty()) return;
		for(Function f:functions)
			if(f.equals(str))
				throw new LC3CompileException("Function defined already: " + str);
	}
	
	
	public void parseInstructions() throws LC3CompileException {
		Instruction instr = null;
		Function function = null;
		boolean recursive = false;
		while(hasMoreTokens()) {
			instr = new Instruction();
			String str = nextToken();
			switch(str) {
				case "rec-start":
					recursive = true;
				break;
				
				case "rec-end":
					recursive = false;
				break;
				case "global":
					function = getFunction(str);
				break;
				
				case "function":
					function = getFunction(nextToken());
				break;
				
				case "(":
				case "{":
				case "[":
					stack.push(str);
				break;
				
				case ")":
				case "}":
				case "]":
					stack.pop();
				break;
				
				case "letr":
					instr.setRecursive(recursive);
					instr.setInstructionType(Fixed.LET_X_GETPARAM);
					new RegisterParse().letRDeclaration(this, instr,function,str);
				break;
				
				case "let":
				case "public":
					instr.setRecursive(recursive);
					instr.setInstructionType(Fixed.NEW_DECL);
					letDeclaration(instr,function, str);
				break;
				
				case "set":
					instr.setRecursive(recursive);
					instr.setInstructionType(Fixed.SET);
					new RegisterParse().setDeclaration(this, instr,function, str);
				break;
				
				case "if":
					instr.setRecursive(recursive);
					instr.setInstructionType(Fixed.IF);
					new ConditionCase().condition(this, instr, function, str);
					stack.push("if");
					stack.push("endif");
					conditionStack.push(instr);
				break;
				
				case "else":
					instr.setSymbol("ELSE");
					instr.setRecursive(recursive);
					instr.setInstructionType(Fixed.ELSE);
					instr.setRandomLabel(new RandomLabelGenerator().generateLabel());
					function.addModifyToInstruction(instr);
					conditionStack.pop().setElse(instr);
					conditionStack.push(instr); //for jump to endif
					stack.pop();
				break;
				
				case "endif":
					instr.setSymbol("ENDIF");
					instr.setRecursive(recursive);
					instr.setInstructionType(Fixed.ENDIF);
					instr.setRandomLabel(new RandomLabelGenerator().generateLabel());
					function.addModifyToInstruction(instr);
					conditionStack.pop().setEndif(instr);
					stack.pop();
				break;
				
				case "while":
					instr.setRecursive(recursive);
					instr.setInstructionType(Fixed.WHILE);
					instr.setRandomLabel(new RandomLabelGenerator().generateLabel());
					new ConditionCase().condition(this, instr, function, str);
					stack.push("endwhile");
					conditionStack.push(instr);
				break;
				
				case "endwhile":
					instr.setRecursive(recursive);
					instr.setInstructionType(Fixed.ENDWHILE);
					instr.setRandomLabel(new RandomLabelGenerator().generateLabel());
					instr.setSymbol(str);
					instr.setValue("");
					function.addModifyToInstruction(instr);
					Instruction whil = conditionStack.pop();
					whil.setEndwhile(instr);
					instr.setWhil(whil);
					stack.pop();
				break; 
				
				case "do":
					instr.setSymbol("DO-LOOP");
					instr.setRecursive(recursive);
					instr.setInstructionType(Fixed.DO);
					instr.setRandomLabel(new RandomLabelGenerator().generateLabel());
					function.addModifyToInstruction(instr);
	                conditionStack.push(instr);
	                stack.push("loop");
				break;
				
				case "loop":
					instr.setRecursive(recursive);
					instr.setInstructionType(Fixed.DOLOOP);
	                new ConditionCase().condition(this, instr, function, str);
	                instr.setRandomLabel(conditionStack.pop().getRandomLabel());
	                stack.pop();
				break;
				
				case "param0":
					instr.setRecursive(recursive);
					instr.setInstructionType(Fixed.PAR0);
					new RegisterParse().modifyParamCase(this, instr, function, str);
				break;
				
				case "param1":
					instr.setRecursive(recursive);
					instr.setInstructionType(Fixed.PAR1);
					new RegisterParse().modifyParamCase(this, instr, function, str);
				break;
				
				case "print":
					instr.setRecursive(recursive);
					instr.setInstructionType(Fixed.SYS_PRN);
					system(instr,function, str);
				break;	
				
				case "println":
					instr.setRecursive(recursive);
					instr.setInstructionType(Fixed.SYS_PRNLN);
					system(instr,function, str);
				break;
				
				case "printc":
					instr.setRecursive(recursive);
					instr.setInstructionType(Fixed.SYS_PRN);
					instr.setPrintRaw(true);
					system(instr,function, str);
				break;
				
				case "strlen":
					instr.setRecursive(recursive);
					instr.setInstructionType(Fixed.STRLEN);
					strlen(instr,function, str);
				break;
				
				case "lea":
					instr.setRecursive(recursive);
					addressOfValueAt(instr, function, Fixed.ADDRESS_OF_VARIABLE);
				break;
				
				case "ld":
					instr.setRecursive(recursive);
					addressOfValueAt(instr, function, Fixed.VALUE_AT_ADDRESS);
				break;
				
				case "st":
					instr.setRecursive(recursive);
					addressOfValueAt(instr, function, Fixed.INDEX);
				break;
				
				case "getpublic":
					instr.setRecursive(recursive);
					addressOfValueAt(instr, function, Fixed.GETPUBLIC);
				break;
				
				case "setpublic":
					instr.setRecursive(recursive);
					addressOfValueAt(instr, function, Fixed.SETPUBLIC);
				break;
				
				case "randintarray":
					instr.setRecursive(recursive);
					instr.setInstructionType(Fixed.RANDINTARR);
					randomArray(instr, function);
				break;
				
				case "uninitarray":
					instr.setRecursive(recursive);
					instr.setInstructionType(Fixed.UNINITARR);
					randomArray(instr, function);
				break;
				
				case "<asm>":
					stack.push(str);
					instr.setRecursive(recursive);
					instr.setInstructionType(Fixed.ASM);
					asm(instr, function);
				break;
				
				case "<fillintarray>":
					stack.push(str);
					instr.setRecursive(recursive);
					instr.setInstructionType(Fixed.FIA);
					fillintarray(instr, function);
				break;
				
				case "label":
					instr.setRecursive(recursive);
					instr.setInstructionType(Fixed.LABEL);
					label(instr, function);
				break;
				
				case "jump":
					instr.setRecursive(recursive);
					instr.setInstructionType(Fixed.JMP);
					label(instr, function);
				break;
				
				case "return":
					instr.setRecursive(recursive);
					instr.setInstructionType(Fixed.RETURN);
					if(function.equals("main") || function.equals("global"))
						throw new LC3CompileException("'return' found, remove it: " + 
									function.getSymbol());
					else
						ret(instr, function, str);
				break;
				
				case "halt":
					throw new LC3CompileException("reserved: " + str);
					
				default:
					instr.setRecursive(recursive);
					defaultCaseParseInstr(instr, function, str);			
				break;
			} //end switch
		}// end while	
			
		if(!stack.empty()) 
			throw new LC3CompileException("Uneven tokens: ");
			
		reset();
	}

	private void randomArray(Instruction instr, Function func) 
			throws LC3CompileException {
		notKW_Nxt();
		String symbol = nextToken();
		if(isNumeric(symbol)||symbol.indexOf("\"")>-1)
			throw new LC3CompileException("Should be symbol:" + symbol);
		notKW_Nxt();
		String val = nextToken();
		if(!isNumeric(val)) {
			checkValidSymbol(val, func);
			Instruction temp = func.getCurrentInstruction();
			instr.setSymbol(symbol);
			instr.setValue(temp.getValue()); //value at y
		}
		else {
			instr.setVType(Fixed.NUM);
			instr.setSymbol(symbol);
			instr.setValue(val);
		}
		
		instr.setFuncName(func);
		func.addInstruction(instr);
	}
	
	private void asm(Instruction instr, Function func) 
			throws LC3CompileException {
		String token = nextToken();
		StringBuilder val = new StringBuilder();
		while(!token.equals("</asm>")) {
			val.append(token);
			val.append(" ");
			token = nextToken();
		}
		stack.pop();
		instr.setSymbol("ASM");
		instr.setValue(val.toString());
		instr.setVType(Fixed.ASM);
		instr.setFuncName(func);
		func.addInstruction(instr);
	}
	
	private void fillintarray(Instruction instr, Function func) 
		throws LC3CompileException {
			String symbol = nextToken();
			if(isNumeric(symbol)||symbol.indexOf("\"")>-1)
				throw new LC3CompileException("Should be symbol:" + symbol);
			//notKW_Nxt();
			String token = nextToken();
			StringBuilder val = new StringBuilder();
			while(!token.equals("</fillintarray>")) {
				if(!token.startsWith("//")) {
					val.append(token);
					val.append(" ");
				}
				token = nextToken();
			}
			stack.pop();
			instr.setSymbol(symbol);
			instr.setValue(val.toString());
			instr.setVType(Fixed.FIA);
			instr.setFuncName(func);
			func.addInstruction(instr);
		
	}
	
	private void label(Instruction instr, Function func) 
			throws LC3CompileException {
		notKW_Nxt();
		String val = nextToken();
		instr.setSymbol("LABEL");
		instr.setValue(val);
		instr.setVType(Fixed.LABEL);
		instr.setFuncName(func);
		func.addInstruction(instr);
	}
	
	/* void strlen():
	 * puts the length of str into x.
	 * let str = "ABC"
	   let x = 0
	   strlen x str	
	 */
	private void strlen(Instruction instr, Function func, String str) 
			throws LC3CompileException {
		notKW_Nxt();
		String symbol = nextToken();
		if(isNumeric(symbol)||symbol.indexOf("\"")>-1)
			throw new LC3CompileException("Should be symbol:" + symbol);
		checkValidSymbol(symbol, func);
		notKW_Nxt();
		String val = nextToken();
		if(isNumeric(val))
			throw new LC3CompileException("strlen only applies " +
				"to String values: " + val);
		checkValidSymbol(val, func);
		Instruction curInstr = func.getCurrentInstruction();
		instr.setSymbol(symbol);
		instr.setValue(val);
		instr.setValueInstruction(curInstr);
		instr.setFuncName(func);
		func.addModifyToInstruction(instr);
	}
	
	/* void addressOfValueAt():
	 * if type == ADDRESS_OF_VARIABLE
	 * puts the address of y into x.
	 * So, x becomes a pointer variable...
	 * I think?
	 * let y = ...
	   let x = 0
	   
	   Code example:
	   		addrof x y
	   
	   if type == VALUE_AT_ADDRESS
	 * puts the VALUE found at address 
	 * (stored in variable y), into x.
	 * So, x gets the value stored in address y
	 * I think?
	 * let y = ...
	   let x = 0
	   Code example:
	   		valat x y
	 */
	private void addressOfValueAt(Instruction instr, Function func, byte type) 
			throws LC3CompileException {
		notKW_Nxt();
		String symbol = nextToken();
		if(isNumeric(symbol)||symbol.indexOf("\"")>-1)
			throw new LC3CompileException("Should be symbol:" + symbol);
		if(type == Fixed.SETPUBLIC)
			checkIsGlobalSymbol(instr, symbol);
		else
			checkValidSymbol(symbol, func);
		notKW_Nxt();
		String val = nextToken();
		if(isNumeric(val)||symbol.indexOf("\"")>-1)
			throw new LC3CompileException("Should be symbol:" + symbol);
		if(type == Fixed.GETPUBLIC)
			checkIsGlobalSymbol(instr, val);
		else
			checkValidSymbol(val, func);
		instr.setSymbol(symbol);
		instr.setValue(val);
		instr.setFuncName(func);
		instr.setInstructionType(type);
		func.addModifyToInstruction(instr);
	}

	private void defaultCaseParseInstr(Instruction instr, Function function, String str) 
			throws LC3CompileException {
		if(!Keyword.isKeyword(str)) {
			/*
			 * str is either a function call
			 * or a modifier i.e:
			 * x = y
			 * or...
			 * methodB
			 */
			try {
				/*
				 * if function, add it as a child
				 * to this function.
				 * Warning: need to handle case
				 * if function not found!
				 */
				instr.setInstructionType(Fixed.FUNC);
				function(instr, function, str);
			} catch (LC3CompileException lce) {
				modifyParseCase(instr, function, str);
			}	
		}

	}
	
	private void modifyParseCase(Instruction instr, Function function, String str) 
			throws LC3CompileException {
		/*
		 * here...which means
		 * this is a modify:
		 * x = 5
		 * x = "hi"
		 * x = y
		 * ..etc
		 * x + 1 (addition)
		 */
		instr.setInstructionType(Fixed.MODIFY);
		switch(peekNextToken()) {
			case "=":
				instr.setModifyOperatorType(Fixed.EQU);
				modifyEqual(instr,function, str);
			break;
			
			case "+":
				instr.setModifyOperatorType(Fixed.ADD);
				modifyAddition(instr, function, str);
			break;
			
			case "-":
				instr.setModifyOperatorType(Fixed.SUB);
				modifySubtraction(instr, function, str);
			break;
			
			case "*":
				instr.setModifyOperatorType(Fixed.MUL);
				modifyMultiply(instr, function, str);
			break;
			
			case "/":
				instr.setModifyOperatorType(Fixed.DIV);
				modifyDivide(instr, function, str);
			break;
			
			case "&":
				instr.setModifyOperatorType(Fixed.AND);
				modifyAnd(instr, function, str);
			break;
			
			default:
				throw new 
				LC3CompileException("expecting =/+/-/");
			//break;
		}
	}
	
	private Function globalFunction() {
		for(Function func: functions)
			if(func.equals("global"))
				return func;
		return null;
	}
	
	private void checkValidSymbol(String s, Function func) throws LC3CompileException {
		Function global = globalFunction();
		if(global!= null && global.exists(s))
			return;
		
		switch(s) {
			case "param0":
			case "param1":
				return;
		}
		
		if(s.indexOf("\"")==-1 && !isNumeric(s)) 
			if(!func.exists(s))
				throw new 
					LC3CompileException("Invalid symbol: " + s);
	}
	
	private void letDeclaration(Instruction instr, Function func, String str) throws LC3CompileException {
		notKW_Nxt();
		instr.setSymbol(nextToken());
		expectingNxt("=");
		notKW_Nxt();
		instr.setModifyOperatorType(Fixed.EQU);
		commonCaseDeclaration(instr,func,str);
	}
	
	public void commonCaseDeclaration(Instruction instr, Function func, String str) throws LC3CompileException {
		String val = nextToken();
		checkValidSymbol(val,func);
		
		/*
		 * If val is not a number and it
		 * is not a string either, then
		 * it is a symbol:
		 * 		= y
		 * 		
		 */
		if(!isNumeric(val) && val.indexOf("\"") == -1) {
			Instruction temp = func.getCurrentInstruction();
			instr.setValue(temp.getValue()); //value at y
			
			/*
			 * set param0 = k
			 * Not entirely sure why I
			 * do this. Maybe cause 'set' is
			 * not a variable declaration
			 * but rather setting params
			 * r0 and r1? I don't know.
			 * 
			 * All I know is that I need
			 * to do this otherwise it 
			 * doesn't work.
			 */
			if(instr.getInstructionType() == Fixed.SET ||
					instr.getInstructionType() == Fixed.IF ||
					instr.getInstructionType() == Fixed.WHILE ||
					instr.getInstructionType() == Fixed.DOLOOP )
				instr.setValue(val); // = k
			
			instr.setVType(temp.getVType());
		}
		/*
		 * else if number:
		 * 		x = 5
		 * 		set param0 = 5
		 */
		else if(isNumeric(val)) {
			instr.setValue(val);
			instr.setVType(Fixed.NUM);
		}
		/*
		 * else..it is a string
		 * 		x = "abc"
		 */
		else {
			instr.setValue(val);
			instr.setVType(Fixed.STR);
		}
		
		instr.setFuncName(func);
		if(instr.getModifyOperatorType() == Fixed.EQU)
			func.addInstruction(instr);
		else
			func.addModifyToInstruction(instr);
	}
	
	private void function(Instruction instr, Function func, String str) 
			throws LC3CompileException {
		instr.setSymbol(func.getSymbol());
		Function child = getFunction(str);
		func.addChild(child);
		func.addModifyToInstruction(instr);
	}
	
	private void ret(Instruction instr, Function func, String str) 
			throws LC3CompileException {
		instr.setSymbol(str);
		func.addInstruction(instr);
		func.setReturns(true);
	}
	
	private void modifyEqual(Instruction instr, Function func, String str) 
			throws LC3CompileException {
		
		if(func.exists(str)) {
			expectingNxt("=");
			instr.setModifyOperatorType(Fixed.EQU);
			String val = peekNextToken();
			switch(val) {
				/*
				 * modify:
				 * x = getparam0
				 * y = getparam1/2/3/4/5/6/7/8/9/10
				 * 
				 * reg8 = PC
				 * reg9 = CC
				 * reg10 = IR (Instruction Register)
				 */
				case "getparam0":
				case "getparam1":
				case "getparam2":
				case "getparam3":
				case "getparam4":
				case "getparam5":
				case "getparam6":
				case "getparam7":
				case "getparam8":
				case "getparam9":
				case "getparam10":
					new RegisterParse().modifyGetParam(this, instr, func, str);
					return;
			}
			
			val = nextToken();
			if(isNumeric(val)) {
				/*
				 * x = 4
				 */
				if(func.getCurrentInstruction().getVType() == Fixed.NUM) {
					instr.setSymbol(str);
					instr.setValue(val);
					instr.setVType(Fixed.NUM);
					instr.setFuncName(func.getCurrentInstruction().getFuncName());
					func.addModifyToInstruction(instr);
				}
				else if(func.getCurrentInstruction().getVType() == Fixed.LET_X_GETPARAM)
					throw new LC3CompileException("This should not happen, investigate");
				else
					throw new LC3CompileException("mismatch types");
			}
			else if(val.indexOf("\"") > -1) {
				throw new LC3CompileException("Not implemented: " + instr);
			}
			else if(val.indexOf("param") > -1)
				throw new LC3CompileException("Not implemented: " + instr);
			else {
				/* val is a symbol:
				 * x = y (y, the val, is a symbol)
				 * check if the types match
				 */
				checkValidSymbol(val, func);
				
				byte typeA = func.getVType();
				byte typeB = func.getVTypeSymbol(str);
				if( typeA == typeB )	{
					instr.setSymbol(str);
					instr.setValue(func.getCurrentInstruction().getSymbol());
					instr.setVType(typeA);
					instr.setValueInstruction(func.getCurrentInstruction());
					instr.setFuncName(func.getCurrentInstruction().getFuncName());
					
					func.addModifyToInstruction(instr);
				}
				else
					throw new LC3CompileException("Mismatch type: " + str);
			}
			
			
		}
		/*else {
			Function glbl=checkIsGlobalSymbol(instr, str);
			expectingNxt("=");
			notKW_Nxt();
			String val = nextToken();
			checkValidSymbol(val, func);
			instr.setSymbol(str);
			instr.setValue(val);
			instr.setVType(Fixed.NUM);
			instr.setModifyOperatorType(Fixed.EQU);
			instr.setFuncName(glbl);
			instr.setValueInstruction(func.getCurrentInstruction());
			func.addModifyToInstruction(instr);
		}*/
	}
	
	private Function checkIsGlobalSymbol(Instruction instr, String str) 
		throws LC3CompileException {
		/*
		 * global case modify
		 * set the function name to global
		 */
		Function glbl = globalFunction();
		if(glbl!=null && glbl.exists(str)) {
			return glbl;
		}
		else 
			throw new LC3CompileException("Invalid symbol: " + str);
	}
	
	/*
	 * x + 1 i.e. x = x + 1
	 * x + y i.e. x = x + y
	 */
	public void modifyAddition(Instruction instr, Function func, String str) 
			throws LC3CompileException {
		nextToken();
		notKW_Nxt();
		instr.setSymbol(str);
		instr.setValue(nextToken());
		instr.setVType(Fixed.NUM);
		instr.setModifyOperatorType(Fixed.ADD);
		
		if(func.exists(str)) 
			instr.setFuncName(func.getCurrentInstruction().getFuncName());
		
		if(!Parser.isNumeric(instr.getValue())) {
			checkValidSymbol(instr.getValue(), func);
			instr.setValueInstruction(func.getCurrentInstruction());
		}
		
		func.addModifyToInstruction(instr);
	}
	
	/*
	 * x & 1 i.e. x = x & 1
	 * x & y i.e. x = x & y
	 */
	public void modifyAnd(Instruction instr, Function func, String str) 
			throws LC3CompileException {
		nextToken();
		notKW_Nxt();
		instr.setSymbol(str);
		instr.setValue(nextToken());
		instr.setVType(Fixed.NUM);
		instr.setModifyOperatorType(Fixed.AND);
		
		if(func.exists(str)) 
			instr.setFuncName(func.getCurrentInstruction().getFuncName());
		
		if(!Parser.isNumeric(instr.getValue())) {
			checkValidSymbol(instr.getValue(), func);
			instr.setValueInstruction(func.getCurrentInstruction());
		}
		
		func.addModifyToInstruction(instr);
	}
	
	/*
	 * x - 1 i.e. x = x - 1
	 * x - y i.e. x = x - y
	 */
	public void modifySubtraction(Instruction instr, Function func, String str) 
			throws LC3CompileException {
		nextToken();
		notKW_Nxt();
		instr.setSymbol(str);
		instr.setValue(nextToken());
		instr.setVType(Fixed.NUM);
		instr.setModifyOperatorType(Fixed.SUB);
		
		if(func.exists(str)) 
			instr.setFuncName(func.getCurrentInstruction().getFuncName());
		
		if(!Parser.isNumeric(instr.getValue())) {
			checkValidSymbol(instr.getValue(), func);
			instr.setValueInstruction(func.getCurrentInstruction());
		}
		
		func.addModifyToInstruction(instr);
	}
	
	/*
	 * x * 1 i.e. x = x * 1
	 * x * y i.e. x = x * y
	 */
	public void modifyMultiply(Instruction instr, Function func, String str) 
			throws LC3CompileException {
		nextToken();
		notKW_Nxt();
		instr.setSymbol(str);
		instr.setValue(nextToken());
		instr.setVType(Fixed.NUM);
		instr.setModifyOperatorType(Fixed.MUL);
		
		if(func.exists(str)) 
			instr.setFuncName(func.getCurrentInstruction().getFuncName());
		
		if(!Parser.isNumeric(instr.getValue())) {
			checkValidSymbol(instr.getValue(), func);
			instr.setValueInstruction(func.getCurrentInstruction());
		}
		
		func.addModifyToInstruction(instr);
	}
	
	/*
	 * x * 1 i.e. x = x * 1
	 * x * y i.e. x = x * y
	 */
	public void modifyDivide(Instruction instr, Function func, String str) 
			throws LC3CompileException {
		nextToken();
		notKW_Nxt();
		instr.setSymbol(str);
		instr.setValue(nextToken());
		instr.setVType(Fixed.NUM);
		instr.setModifyOperatorType(Fixed.DIV);
		
		if(func.exists(str)) 
			instr.setFuncName(func.getCurrentInstruction().getFuncName());
		
		if(!Parser.isNumeric(instr.getValue())) {
			checkValidSymbol(instr.getValue(), func);
			instr.setValueInstruction(func.getCurrentInstruction());
		}
		
		func.addModifyToInstruction(instr);
	}
	
	private void system(Instruction instr, Function func, String str) throws LC3CompileException {
		notKW_Nxt_Exception(new String[] { "param0", "param1" });
		String val = nextToken();
		
		/*
		 * Check if global variable
		 */
		Function glblfunc = globalFunction();
		if(glblfunc!= null && globalFunction().exists(val)) {
			instr.setSymbol(str);
			instr.setValue(val);
			instr.setVType(Fixed.NUM);
			instr.setFuncName(glblfunc);
			instr.setValueInstruction(instr);
			func.addModifyToInstruction(instr);
			return;
		}
		
		
		instr.setSymbol(str);
		instr.setValue(val);
		
		if(val.indexOf("\"")>-1) {
			//dynamic string
			if(str.equals("print"))
				instr.setVType(Fixed.SYS_PRN_STR);
			else if(str.equals("println"))
				instr.setVType(Fixed.SYS_PRNLN_STR);
		}
		else if(isNumeric(val))
			//dynamic int
			instr.setVType(Fixed.NUM);
		else if(val.equals("param0"))
			instr.setVType(Fixed.PAR0);
		else if(val.equals("param1"))
			instr.setVType(Fixed.PAR1);
		else { 
			
			/*
			 * It is a symbol:
			 * 
			 * print x
			 * println x
			 * 
			 * Now, x is a symbol.
			 * Find out if it's a string
			 * or number.
			 */
			checkValidSymbol(val, func);
			byte b = func.getVType();
			instr.setVType(b);
			instr.setValueInstruction(func.getCurrentInstruction());
		}
		instr.setFuncName(func);
		func.addModifyToInstruction(instr);
	}
	
	public boolean hasMoreTokens() {
		return count < srcCode.size();
	}
	
	public String nextToken() throws LC3CompileException {
		if (count < srcCode.size())
			return srcCode.get(count++);
		else
			throw new LC3CompileException("No more tokens");
	}
	
	public String peekNextToken() throws LC3CompileException {
		if (count < srcCode.size())
			return srcCode.get(count);
		else
			throw new LC3CompileException("No more tokens");
	}
	
	public String prevToken() throws LC3CompileException {
		if (count > 0)
			return srcCode.get(--count);
		else
			throw new LC3CompileException("0th token reached");
	}
	
	public void reset() {
		count = 0;
	}
	
	public static boolean isNumeric(String s) {
		try {
			Integer.parseInt(s);
			return true;
		}
		catch (NumberFormatException nfe) {
			try {
				if(s.startsWith("0x")) {
					s=s.substring(2,s.length());
					Integer.parseInt(s,16);
					return true;
				}
				else
					return false;
			}
			catch (NumberFormatException nfe1) {
				return false;
			}
		}
	}
	
	public void expectingNxt(String s) throws LC3CompileException {
		if (!s.equals(nextToken())) 
			throw new LC3CompileException("Expecting: " + s);
	}
	
	private boolean isOneOf(String token, String[] s) {
		for(String temp: s)
			if(token.equals(temp))
				return true;
		return false;
	}
	
	public void notKW_Nxt() throws LC3CompileException {
		String str = nextToken();
		if(Keyword.isKeyword(str))
			throw new LC3CompileException("keyword found: " + str);
		count--;
	}
	
	public void notKW_Nxt_Exception(String[] exceptions) throws LC3CompileException {
		String str = nextToken();
		if(isOneOf(str, exceptions)) {
			count--;
			return;
		}	
		if(Keyword.isKeyword(str))
			throw new LC3CompileException("keyword found: " + str);
		count--;
	}
	
	public void printFunctions() {
		for(Function f: functions)
			System.out.println(f);
	}
	
	public ArrayList<Function> getFunctionList() {
		return functions;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		while(hasMoreTokens())
			try {
				sb.append(nextToken() + "\n");
			} catch (LC3CompileException e) {
				e.printStackTrace();
			}
		reset();
		return sb.toString();
	}
}
