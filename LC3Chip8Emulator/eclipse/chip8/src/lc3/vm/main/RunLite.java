package lc3.vm.main;

import java.io.File;

import lc3.vm.lite.EmulatorLite;
import lc3.vm.lite.LiteCpu;
import lc3.vm.util.Vars;

public class RunLite {
	public static void main(String args[]) throws Exception {
		String[] vmoptions=System.getenv("vmtoptions").split(",");
		String option1 = vmoptions[0];
		Vars.TROUBLE_SHOOT = option1.equals("debug") ? true : false;
		EmulatorLite emu = new EmulatorLite();
		LiteCpu cpu = new LiteCpu(emu);
		cpu.loadImage(new File(args[0]));
		emu.setMemoryForLCD(cpu.mem());
		if(args.length>1)
			Vars.PRINT_HEX = args[1].equals("hex") ? true : false;
		cpu.start();
	}

}
