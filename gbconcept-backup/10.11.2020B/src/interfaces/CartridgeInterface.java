package interfaces;

public interface CartridgeInterface {
	/*
	 * Comm with cpuBus (R/W)
	 */
	public boolean cpuR(int addr, boolean rOlny);
	public boolean cpuWr(int addr, int data);
	
	/*
	 * Comm with ppuBus (R/W)
	 * R/W is with Cartridge
	 */
	public boolean ppuR(int addr, boolean rOlny);
	public boolean ppuWr(int addr, int data);
	
	public byte getData();
}
