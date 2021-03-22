package lc3.assembler;

public class ProgramTable {
	private int address, labelAddress, pc, pcRelativeAddr;
	private String binstr, hexstr, label;
	private String[] assemblyCode;
	
	public int getLabelAddress() {
		return labelAddress;
	}
	
	public void setAddress(int address) {
		this.address = address;
	}
	
	public void setLabelAddress(int address) {
		this.labelAddress = address;
	}
	public int getPc() {
		return pc;
	}
	public void setPc(int pc) {
		this.pc = pc;
	}
	public int getPcRelativeAddr() {
		return pcRelativeAddr;
	}
	public void setPcRelativeAddr(int pcRelativeAddr) {
		this.pcRelativeAddr = pcRelativeAddr;
	}
	public String getAssemblyCode() {
		String str="";
		for(String s:assemblyCode)
			str += s + " ";
		return str;
			
	}
	public void setAssemblyCode(String[] assemblyCode) {
		this.assemblyCode = assemblyCode;
	}
	public String getBinstr(int val, boolean sign) { 
		return binstr;
	}
	
	public void setBinstr(int val, boolean sign) {
		String x = Integer.toBinaryString(val);

	    if(x.length() > 16 ) {
	        System.out.println("Error! Instruction binary is > 16 digits. Should be <=16.");
	        System.exit(1);
	    }
	    
	    if(x.length() == 16 ) {
	    	binstr = x;
	    	return;
	    }
	    
	    int diff=16-x.length();
	    for(int i=0;i<diff;i++) 
	        x = sign ? "0" + x : "1" + x; 
		
	    binstr = x;
	}
	
	public String getHexstr() {
		return hexstr;
	}
	
	public void setHexstr(int val) {
		hexstr = String.format("%04X",val);
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String toString() {
		return binstr + "\t\t" +
				hexstr + "\t\t" +
				String.format("%04X",(address+0x3000)) + "\t\t" +
				labelAddress + "\t\t\t" +
				pc + "\t\t\t" +
				pcRelativeAddr + "\t\t\t" +
				getAssemblyCode();
	}
	

}
