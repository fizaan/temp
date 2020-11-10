package gameboyconcept.lambda;

public class Command {
	
	private int opcode;
	private Operation operation;
	
	public Command(int opcode, Operation operation) {
		this.opcode = opcode;
		this.operation = operation;
	}
	
	public int key() { return opcode; }
	public Operation opeation() { return operation; }

}
