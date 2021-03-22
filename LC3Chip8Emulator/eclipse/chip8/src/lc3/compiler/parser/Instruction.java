package lc3.compiler.parser;

public class Instruction {
	private String symbol,value, funcName, randomLabel;
	private byte vType, modifyOperatorType, conditionType;
	private byte instructionType;
	private Instruction els;
	private Instruction endif;
	private Instruction whil, endwhile;
	private Instruction valueInstruction;
	private boolean recursive;
	private boolean printRaw;
	private int strLen;

	public int getStrLen() {
		return strLen;
	}

	public void setStrLen(int strLen) {
		this.strLen = strLen;
	}

	public void setEls(Instruction els) {
		this.els = els;
	}

	public boolean isPrintRaw() {
		return printRaw;
	}

	public void setPrintRaw(boolean printRaw) {
		this.printRaw = printRaw;
	}

	public boolean isRecursive() {
		return recursive;
	}

	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}

	public Instruction getValueInstruction() {
		return valueInstruction;
	}

	public void setValueInstruction(Instruction valueInstruction) {
		this.valueInstruction = valueInstruction;
	}

	public Instruction getWhil() {
		return whil;
	}

	public void setWhil(Instruction whil) {
		this.whil = whil;
	}

	public Instruction getEndwhile() {
		return endwhile;
	}

	public void setEndwhile(Instruction endwhile) {
		this.endwhile = endwhile;
	}

	public Instruction getEndif() {
		return endif;
	}

	public void setEndif(Instruction endif) {
		this.endif = endif;
	}

	public Instruction getElse() {
		return els;
	}

	public void setElse(Instruction els) {
		this.els = els;
	}

	public byte getConditionType() {
		return conditionType;
	}

	public void setConditionType(byte conditionType) {
		this.conditionType = conditionType;
	}
	
	public byte getModifyOperatorType() {
		return modifyOperatorType;
	}

	public void setModifyOperatorType(byte modifyOperatorType) {
		this.modifyOperatorType = modifyOperatorType;
	}
	
	public byte getInstructionType() {
		return instructionType;
	}

	public void setInstructionType(byte instructionType) {
		this.instructionType = instructionType;
	}

	public String getRandomLabel() {
		return randomLabel;
	}

	public void setRandomLabel(String randomLabel) {
		this.randomLabel = randomLabel;
	}
	
	public String getFuncName() {
		return funcName;
	}
	
	public void setFuncName(Function func) {
		this.funcName = func.getSymbol();
	}
	
	public void setFuncName(String name) {
		this.funcName = name;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public void setValue(String value) {
		this.value = value;
		if(value.indexOf("\"")>-1)
			setStrLen(value.length()-2);
		
		if(strLen < 0)
			strLen = 0;
	}

	public void setVType(byte type) {
		this.vType = type;
	}
	
	public String getSymbol() {
		return symbol;
	}

	public String getValue() {
		return value;
	}

	public byte getVType() {
		return vType;
	}
	
	public String toString() {
		return symbol + ", " + value + ", " + 
				(int) vType + ", " + 
					instructionType + ", " + funcName + "\n";
	}

	public boolean equals(Instruction other) {
		return symbol.equals(other.getSymbol());
	}
	
	public boolean equals(String s) {
		return symbol.equals(s);
	}
}
