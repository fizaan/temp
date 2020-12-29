package cofeegb;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/*
	Thanks to:
	https://blog.rekawek.eu/2017/02/09/coffee-gb/
*/

public class LCDDisplay extends JPanel implements Runnable {

	private static final long serialVersionUID = 1L;
	public static final int DISPLAY_WIDTH = 160;
    public static final int DISPLAY_HEIGHT = 144;
    private final BufferedImage img;
    public static final int[] COLORS = new int[]{0xe6f8da, 0x99c886, 0x437969, 0x051f2a};
    private final int[] rgb;
    private int scale;
    private int i;
    private boolean enabled, doStop;

    public void stop() {
        doStop = true;
    }

    public LCDDisplay(int scale) {
        super();
        GraphicsConfiguration gfxConfig = GraphicsEnvironment.
                getLocalGraphicsEnvironment().getDefaultScreenDevice().
                getDefaultConfiguration();
        img = gfxConfig.createCompatibleImage(DISPLAY_WIDTH, DISPLAY_HEIGHT);
        rgb = new int[DISPLAY_WIDTH * DISPLAY_HEIGHT];
        this.scale = scale;
    }

    public void putDmgPixel(int color) {
        rgb[i++] = COLORS[color];
        i = i % rgb.length;
        if(i==0) disableLcd();
    }

    public void enableLcd() {
        enabled = true;
    }

    public void disableLcd() {
        enabled = false;
        System.out.println("Warning: LCD off");
    }

	@Override
	public void run() {
		 while (!doStop)
			 if(enabled) {
				 img.setRGB(0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT, rgb, 0, DISPLAY_WIDTH);
	             validate();
	             repaint();
			 }


	}

	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        if (enabled) {
            g2d.drawImage(img, 0, 0, DISPLAY_WIDTH * scale, DISPLAY_HEIGHT * scale, null);
        } else {
            g2d.setColor(new Color(COLORS[0]));
            g2d.drawRect(0, 0, DISPLAY_WIDTH * scale, DISPLAY_HEIGHT * scale);
        }
        g2d.dispose();
    }

}
