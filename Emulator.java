package cofeegb;

/*
	Thanks to:
	https://blog.rekawek.eu/2017/02/09/coffee-gb/
*/

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Emulator {
	private JFrame mainWindow;
	private Random random;
	private final LCDDisplay display;
	public static final int SCALE = 2;

	public Emulator() {
		display = new LCDDisplay(SCALE);
		display.enableLcd();
		random = new Random();
	}

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
        display.setPreferredSize(new Dimension(160 * SCALE, 144 * SCALE));
        mainWindow = new JFrame("Coffee GB");
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setContentPane(display);
        mainWindow.setResizable(false);
        mainWindow.setVisible(true);
        mainWindow.pack();
        new Thread(display).start();
    }

	private void stopGui() {
        display.stop();
        mainWindow.dispose();
    }

	private static void printInfo() {
		Thread th[] = new Thread[Thread.activeCount()];
		Thread.enumerate(th);
		for(Thread t:th) System.out.println(t);

	}

	public void spitter() {
		if(random.nextBoolean()) {
			int col = random.nextInt(3);
			//System.out.println(col);
			display.putDmgPixel(col);
		}

	}
}
