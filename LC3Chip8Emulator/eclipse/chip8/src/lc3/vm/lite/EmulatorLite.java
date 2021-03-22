package lc3.vm.lite;

import javax.swing.*;

import java.awt.*;

public class EmulatorLite {
	private JFrame mainWindow;
	private final LCDDisplayLite display;
	
	public EmulatorLite() {
		display = new LCDDisplayLite();
	}
	
	public boolean isRunning() {
		return display.isRunning();
	}
	
	public LCDDisplayLite getDisplay() { return display; }
	
	public void run() throws Exception {
        System.setProperty("sun.java2d.opengl", "true");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
        } catch (InstantiationException ex) {
        } catch (IllegalAccessException ex) {
        } catch (UnsupportedLookAndFeelException ex) {
        }
        SwingUtilities.invokeLater(() -> startGui());
        printInfo();
    }
	
	private void startGui() {
        display.setPreferredSize(new Dimension(
        		LCDDisplayLite.DISPLAY_WIDTH * LCDDisplayLite.DISPLAY_SCALE, 
        		LCDDisplayLite.DISPLAY_HEIGHT * LCDDisplayLite.DISPLAY_SCALE));
        mainWindow = new JFrame("Lite VM Chip-8 Display");
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setContentPane(display);
        mainWindow.setResizable(false);
        mainWindow.setVisible(true);
        mainWindow.pack();
        new Thread(display).start();
    }
	
	private static void printInfo() {
		Thread th[] = new Thread[Thread.activeCount()];
		Thread.enumerate(th);
		for(Thread t:th) System.out.println(t);	
	}
	
	public void lcdTestRender() {
		display.lcdTestRender();
	}
	
	public void lcdTest() {
		display.lcdTest();
	}
	
	public void clearVRam() {
		display.clearVRam();
	}

	public void setMemoryForLCD(int[] mem) {
		display.setMemory(mem);
	}
	
	public void setVramStartAddressAndSize(int addr, int w, int h) {
		display.setVramStartAddressAndSize(addr,w,h);
	}

	public void setScale(int c) {
		display.setScale(c);	
	}
}
