package gameboyconcept;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import java.util.List;

import gameboyconcept.lambda.Command;
import gameboyconcept.lambda.Operation;

public final class OpcodesInit {
	private static Command[] cmds;
	public static List<Command> commands;
	
	static {
		cmds = new Command[300];
		addCmd(0x31, (cpu) -> cpu.H31());
		addCmd(0xAF, (cpu) -> cpu.HAF());
		addCmd(0x21, (cpu) -> cpu.H21());
		addCmd(0x32, (cpu) -> cpu.H32());
		addCmd(0x7C, (cpu) -> cpu.determineA());
		addCmd(0x20, (cpu) -> cpu.H20());
		addCmd(0xCB, (cpu) -> cpu.cbExt());
		addCmd(0x0E, (cpu) -> cpu.H0E());
		addCmd(0x3E, (cpu) -> cpu.H3E());
		addCmd(0xE2, (cpu) -> cpu.HE2());
		addCmd(0x0C, (cpu) -> cpu.H0C());
		addCmd(0x77, (cpu) -> cpu.H77());
		addCmd(0xE0, (cpu) -> cpu.HE0());
		addCmd(0x11, (cpu) -> cpu.determineB());
		addCmd(0x1A, (cpu) -> cpu.H1A());
		addCmd(0xCD, (cpu) -> cpu.HCD());
		addCmd(0x13, (cpu) -> cpu.H13());
		addCmd(0x7B, (cpu) -> cpu.H7B());
		addCmd(0xFE, (cpu) -> cpu.HFE());
		addCmd(0x06, (cpu) -> cpu.H06());
		addCmd(0x22, (cpu) -> cpu.H22());
		addCmd(0x23, (cpu) -> cpu.H23());
		addCmd(0x05, (cpu) -> cpu.H05());
		addCmd(0xEA, (cpu) -> cpu.HEA());
		addCmd(0x3D, (cpu) -> cpu.H3D());
		addCmd(0x28, (cpu) -> cpu.H28());
		addCmd(0x0D, (cpu) -> cpu.H0D());
		addCmd(0x2E, (cpu) -> cpu.H2E());
		addCmd(0x18, (cpu) -> cpu.H18());
		addCmd(0x67, (cpu) -> cpu.H67());
		addCmd(0x57, (cpu) -> cpu.H57());
		addCmd(0x04, (cpu) -> cpu.H04());
		addCmd(0x1E, (cpu) -> cpu.H1E());
		addCmd(0xF0, (cpu) -> cpu.HF0());
		addCmd(0x1D, (cpu) -> cpu.H1D());
		addCmd(0x15, (cpu) -> cpu.H15());
		addCmd(0x90, (cpu) -> cpu.H90());
		addCmd(0x16, (cpu) -> cpu.H16());
		addCmd(0x4F, (cpu) -> cpu.H4F());
		addCmd(0xC5, (cpu) -> cpu.HC5());
		addCmd(0xC1, (cpu) -> cpu.HC1());
		addCmd(0xC9, (cpu) -> cpu.HC9());
		addCmd(0xBE, (cpu) -> cpu.HBE());
		addCmd(0x7D, (cpu) -> cpu.H7D());
		addCmd(0x78, (cpu) -> cpu.H78());
		addCmd(0x86, (cpu) -> cpu.H86());
		addCmd(0x17, (cpu) -> cpu.H17());
		commands = unmodifiableList(asList(cmds));
	}
	
	private static void addCmd(int opcode, Operation operation) {
		cmds[opcode] = new Command(opcode, operation);
	}
}
