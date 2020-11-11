package interfaces;

public interface PPUBusInterface {
	
	/*
	 * Comm with cpuBus (R/W)
	 */
	public int cpuR(int addr, boolean rOlny);
	public void cpuWr(int addr, int data);
	
	/*
	 * Comm with ppuBus (R/W)
	 * R/W is with Cartridge
	 */
	public int ppuR(int addr, boolean rOlny);
	public void ppuWr(int addr, int data);
}
