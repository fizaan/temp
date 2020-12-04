import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import javax.swing.JFrame;


public class LineByLinePrint {
	
	/*
	 * This works. Why?
	 */
	
	private BufferedImage img;
	private Canvas canvas;
	private int x;
	private int y;
	
	public LineByLinePrint(int width, int height) {
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(width, height));
        canvas.setMaximumSize(new Dimension(width, height));
        canvas.setMinimumSize(new Dimension(width, height));
        x = y = 0;
    }
	
	private Canvas getCanvas() { return canvas; }

	private void begin() throws IOException {
		 for(;;){
			    getUserInput();
	            myDoPaint();
	        }
	}
	
	private void getUserInput() throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		reader.read();
	}
	
	private void myDoPaint() {
    	Random r = new Random();
    	int red=r.nextInt(256);
		int green=r.nextInt(256);
		int blue=r.nextInt(256);
		Color c=new Color(red,green,blue);
    	int color = c.getRGB();
    	img.setRGB(x++, y, color);
    	if(x >= img.getWidth()) {
    		x = 0;
    		y++;
    	}
    	if(y >= img.getHeight()) {
    		y = 0;
    	}	
    	
        Graphics2D g2 = (Graphics2D) canvas.getGraphics();
        g2.drawImage(img, null, null);
    }
	
	
	public static void main(String[] args) throws IOException {
		LineByLinePrint animatedCanvas = new LineByLinePrint(256,256);
        JFrame jframe = new JFrame();
        jframe.getContentPane().add(animatedCanvas.getCanvas(), BorderLayout.CENTER);
        jframe.setSize(new Dimension(256,256));
        jframe.setVisible(true);
        System.out.println("Keep pressing enter to starting printing..");
        animatedCanvas.begin();
    }


}
