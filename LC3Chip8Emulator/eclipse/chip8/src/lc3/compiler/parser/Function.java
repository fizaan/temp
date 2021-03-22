package lc3.compiler.parser;

import java.util.ArrayList;
import lc3.compiler.exception.LC3CompileException;
import lc3.compiler.utilities.Fixed;

public class Function {
	private String symbol;
	private ArrayList<Instruction> instr;
	private int currentIndex, currentChild;
	private ArrayList<Function> children;
	private boolean returns;
	
	public Function() {
		instr = new ArrayList<>();
		currentIndex = 0;
		currentChild = 0;
		symbol = null;
		children = new ArrayList<>();
		returns = false;
	}
	
	public void setReturns(boolean ret) {
		returns = ret;
	}
	
	public boolean returns() { return returns;}
	
	public Function nextChild() {
		if(children.size()!=0 && 
				currentChild < children.size())
			return children.get(currentChild++);
		else
			return null;
	}
	
	private void setParent(String parent) {
	}
	
	public void addChild(Function child) {
		child.setParent(symbol);
		children.add(child);
	}
	
	public ArrayList<Function>getChildren() {
		return children;
	}
	
	public boolean equals(String s) {
		return symbol.equals(s);
	}
	
	public byte getVType() {
		return instr.get(currentIndex).getVType();
	}
	
	public byte getVTypeSymbol(String sym) {
		for(Instruction cmd: instr)
			if(cmd.getSymbol().equals(sym))
				return cmd.getVType();
		return 0x0;
	}
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public ArrayList<Instruction> getInstructions() {
		return instr;
	}
	
	public void addInstruction(Instruction cmd) throws LC3CompileException {
		if(exists(cmd))
			if(cmd.getInstructionType()==Fixed.ASM)
				;
			else if(cmd.getInstructionType()==Fixed.LABEL)
				;
			else if(cmd.getInstructionType()==Fixed.JMP)
				;
			else
				throw new LC3CompileException("Duplicate Instruction: "+cmd);
		instr.add(cmd);
	}
	
	/*
	 * also adds function
	 * to this function.
	 */
	public void addModifyToInstruction(Instruction cmd) throws LC3CompileException {
		instr.add(cmd);
	}
	
	public boolean exists(Instruction s) {
		currentIndex = 0;
		for(Instruction k:instr) 
			if(s.equals(k))
				return true;
			else
				currentIndex++;
		return false;
	}
	
	public boolean exists(String s) {
		currentIndex = 0;
		for(Instruction k:instr) 
			if(k.equals(s))
				return true;
			else
				currentIndex++;
		return false;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Function: " + symbol + "\n");
		for(Instruction cmd: instr) 
			sb.append(cmd);
		return sb.toString();
	}
	
	public Instruction getCurrentInstruction() {
		return instr.get(currentIndex);
	}
	
	public Instruction getLastInstruction() {
		if (instr.size() == 0) 
			return null;
		return instr.get(instr.size()-1);
	}
	
	public Instruction getInsruction(int type) throws LC3CompileException {
		for(Instruction i: instr)
			if(i.getInstructionType() == type)
				return i;
		throw new LC3CompileException("Instruction not found");
	}
 }
