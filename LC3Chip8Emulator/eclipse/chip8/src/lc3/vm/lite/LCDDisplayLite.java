package lc3.vm.lite;

import javax.swing.*;
import lc3.vm.util.bit.Bit;

import java.awt.*;

public class LCDDisplayLite extends JPanel implements Runnable {
	
	private static final long serialVersionUID = 1L;
	public static int DISPLAY_WIDTH;
    public static int DISPLAY_HEIGHT;
    public static int DISPLAY_SCALE;
    public static final int[] COLORS = new int[]{0xe6f8da, 0x99c886, 0x437969, 0x051f2a};
    private boolean doStop, isRunning;
    private int[] vram;
	private int[] mem;
	private int vramStart;
    
    public void stop() {
        doStop = true;
    }
    
    public LCDDisplayLite() {
        super();
        this.isRunning = false;
        vram = new int[DISPLAY_WIDTH * DISPLAY_HEIGHT];
    }

	@Override
	public void run() {
		isRunning = true;
		 while (!doStop) {
             validate();
             repaint();
             Bit.sleep(60);
		 }
	}
	
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        refresh(g2d);
        
        g2d.setColor(Color.BLUE);
        for(int i=0; i < DISPLAY_WIDTH*DISPLAY_HEIGHT; i++) {
            int x = (i % DISPLAY_WIDTH) * DISPLAY_SCALE;
            int y = (int) Math.floor(i / DISPLAY_WIDTH) * DISPLAY_SCALE;

            if(mem[i+vramStart] == 1) 
            	g2d.fillRect(x, y, DISPLAY_SCALE, DISPLAY_SCALE);
        }
        g2d.dispose();
    }
	
	private void refresh(Graphics2D g2d) {
		g2d.setColor(Color.YELLOW);
	    g2d.fillRect(0, 0, DISPLAY_WIDTH * DISPLAY_SCALE, DISPLAY_HEIGHT * DISPLAY_SCALE);
	}
	
	public boolean lcdSetPixel(double x, double y) {
	    if(x > DISPLAY_WIDTH)
	        x -= DISPLAY_WIDTH;
	    else if(x < 0)
	        x += DISPLAY_WIDTH;

	    if(y > DISPLAY_HEIGHT)
	        y -= DISPLAY_HEIGHT;
	    else if(y<0)
	        y +=  DISPLAY_HEIGHT;

	    vram[(int) (x + (y * DISPLAY_WIDTH))] ^= 1;
	    return vram[(int) (x + (y * DISPLAY_WIDTH))] != 1;
	}
	
	public void lcdTestRender() {
	    lcdSetPixel(Math.floor(Math.random() * 64), 
	        Math.floor(Math.random() * 32));
	    lcdSetPixel(Math.floor(Math.random() * 64), 
	        Math.floor(Math.random() * 32));
	}
	
	/*
	   func: lcdDrawRectangle
	   draws a shape at pos x, y
	   of lenght len and height hei
	*/ 
	private void lcdDrawRectangle(double x, double y, int len, int hei) {
	    for(int i=0; i < len; i++)
	        for(int j=0; j < hei; j++)
	            lcdSetPixel(x+i,y+j);
	}
	
	public void lcdTest() {
	    /*
	        Taken from:
	        austinmorlan.com/posts/chip8_emulator/
	        Although he had a typo:
	        instead of (7,6) it should be
	        (7,3). I emailed him about it:
	        mail@austinmorlan.com
	    */
	    lcdDrawRectangle(1,1,10,4);
	    lcdDrawRectangle(6,6,8,2);
	    lcdDrawRectangle(7,3,3,4);
	}
	
	public void clearVRam() {
		vram = new int[DISPLAY_WIDTH * DISPLAY_HEIGHT];
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setMemory(int[] mem) {
		this.mem = mem;
		
	}

	public void setVramStartAddressAndSize(int addr, int w, int h) {
		this.vramStart = addr;
		DISPLAY_WIDTH = w;
		DISPLAY_HEIGHT = h;
	}

	public void setScale(int c) {
		DISPLAY_SCALE = c;
	}

}
