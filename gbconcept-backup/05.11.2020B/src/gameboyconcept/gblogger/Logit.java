package gameboyconcept.gblogger;

import java.io.FileWriter;
import java.io.IOException;
import util.Bit;

public class Logit {
	
	public static final boolean INFO = false;
	public static final boolean STEP = false;
	public static final boolean WARN = true;
	public static final boolean ERROR = true;
	public static final boolean SLEEP = false;
	public static final int SLEEP_TIME = 100;
	
	public static short loop = 100;
	
	public static FileWriter VRAMWR, VRAMWR_NO_ZERO,
			VRAMR, ROMWR, BOOTROM, LOGO_RAW, TILEMAP_RAW,
			ROMR, STATE, STACK, VRAMCOMPLETE;
	
	static {
		try {
			VRAMWR = new FileWriter(System.getProperty("user.dir") +
					"\\logs\\vramWr.log");
			VRAMR = new FileWriter(System.getProperty("user.dir") +
					"\\logs\\vramR.log");
			ROMWR = new FileWriter(System.getProperty("user.dir") +
					"\\logs\\romWr.log");
			ROMR = new FileWriter(System.getProperty("user.dir") +
					"\\logs\\romR.log");
			STATE = new FileWriter(System.getProperty("user.dir") +
					"\\logs\\cpustate.log");
			STACK = new FileWriter(System.getProperty("user.dir") +
					"\\logs\\stack.log");
			VRAMWR_NO_ZERO = new FileWriter(System.getProperty("user.dir") +
					"\\logs\\vramWrNonZero.log");
			BOOTROM = new FileWriter(System.getProperty("user.dir") +
					"\\logs\\bootrom.log");
			LOGO_RAW = new FileWriter(System.getProperty("user.dir") +
					"\\logs\\binary\\logoRaw.bin");
			TILEMAP_RAW = new FileWriter(System.getProperty("user.dir") +
					"\\logs\\binary\\tileMapRaw.bin");
			VRAMCOMPLETE = new FileWriter(System.getProperty("user.dir") +
					"\\logs\\binary\\vramcomplete.bin");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void wrSTACK(String s) {
		try {
			STACK.write(s);
			STACK.write("\n");
			STACK.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void wrCPUSTATE(String s) {
		try {
			STATE.write(s);
			STATE.write("\n");
			STATE.flush();
			
			if(SLEEP) 
				sleep();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void wrVRAM(int data, int addr) {
		try {
			VRAMWR.write("Writing " + Bit.hexStr(data) +
					" to address " + Bit.hexStr(addr));
			VRAMWR.write("\n");
			VRAMWR.flush();
			
			if(data != 0x0)
				info(Logit.class,"Write V-RAM addr and data: " + 
					Bit.hexStr(addr) + ", " +
					Bit.hexStr(data));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void wrNonZeroVRAM(int data, int addr) {
		try {
			VRAMWR_NO_ZERO.write("Writing " + Bit.hexStr(data) +
					" to address " + Bit.hexStr(addr));
			VRAMWR_NO_ZERO.write("\n");
			VRAMWR_NO_ZERO.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void rVRAM(int data, int addr) {
		try {
			VRAMR.write("Read " + Bit.hexStr(data) +
					" from address " + Bit.hexStr(addr));
			VRAMR.write("\n");
			VRAMR.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void wrROM(int data, int addr) {
		try {
			ROMWR.write("Writing " + Bit.hexStr(data) +
					" to address " + Bit.hexStr(addr));
			ROMWR.write("\n");
			ROMWR.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void wrBootROM(int data, int addr) {
		try {
			BOOTROM.write("Writing " + Bit.hexStr(data) +
					" to address " + Bit.hexStr(addr));
			BOOTROM.write("\n");
			BOOTROM.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void rROM(int data, int addr) {
		try {
			ROMR.write("Read " + Bit.hexStr(data) +
					" from address " + Bit.hexStr(addr));
			ROMR.write("\n");
			ROMR.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void close( ) {
		try {
			VRAMR.close();
			VRAMWR.close();
			ROMWR.close();
			ROMR.close();
			STATE.close();
			STACK.close();
			VRAMWR_NO_ZERO.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	

	public static void info(Object obj, String s) {
		System.out.print(INFO ? obj + ": " + s + "\n" : "");
		if(SLEEP) sleep();
	}
	
	public static void step(String s) {
		if(!STEP) return; 
		System.out.print(STEP ? s + "\n" : "");
	}
	
	public static void warn(Object obj, String s) {
		System.out.print(WARN ? obj + ": " + s + "\n" : "");
		if(SLEEP) sleep();
	}
	
	public static void error(Object obj, String s) {
		System.out.print(ERROR ? obj + ": " + s + "\nExiting..." : "");
		System.exit(1);
	}
	
	public static void main(String args[]) {
		info(Logit.class, "This is info");
		warn(Logit.class, "This is warn");
		error(Logit.class, "This is error");
		
	}
	
	public static void sleep() {
		Thread.currentThread();
		try {
			Thread.sleep(SLEEP_TIME);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
