package interfaces;

public interface CPUBusInterface {
	
	/*
	 * Comm with cpuBus (R/W)
	 */
	public int cpuR(int addr, boolean rOlny);
	public void cpuWr(int addr, int data);

}
