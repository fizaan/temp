package gameboyconcept;

import java.io.File;

import gameboyconcept.gblogger.Logit;

public class GBStart {

	public static void main(String[] args) throws Exception {
		String path = System.getProperty("user.dir") +
				"\\gbbootrom\\gbbootrom.bin";
		GBBus bus = new GBBus();
		Register reg = new Register();
		GBCpuBootRom cpu = new GBCpuBootRom(bus, reg);
		cpu.loadLogo();
		cpu.loadGBBootRom(new File(path));
		cpu.compute();
		Logit.close();
		
	}

}
