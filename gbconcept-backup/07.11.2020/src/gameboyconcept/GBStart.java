package gameboyconcept;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import javax.swing.JFrame;
import gameboyconcept.logo.LCD;

public class GBStart {

	public static void main(String[] args) throws Exception {
		String path = System.getProperty("user.dir") +
				"\\gbbootrom\\gbbootrom.bin";
		GBBus bus = new GBBus();
		Register reg = new Register();
		GBCpuBootRom cpu = new GBCpuBootRom(bus, reg);
		cpu.loadLogo();
		cpu.loadGBBootRom(new File(path));
		int len = 256;
		int hei = 256;
		LCD lcd = new LCD(len, 256, cpu, bus);
		JFrame jp = new JFrame();
        jp.getContentPane().add(lcd, BorderLayout.CENTER);
        jp.setSize(new Dimension(len,hei));
        jp.setVisible(true);
	}

}
