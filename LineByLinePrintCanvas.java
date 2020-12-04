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


public class LineByLinePrintCanvas extends Canvas {
	
	/**
	 * This doesn't work. Why?
	 */
	
	private static final long serialVersionUID = 1L;
	private BufferedImage img;
	private int x;
	private int y;
	
	public LineByLinePrintCanvas(int width, int height) {
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        x = y = 0;
    }

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
    	
        Graphics2D g2 = (Graphics2D) img.getGraphics();
        g2.drawImage(img, null, null);
    }
	
	
	public static void main(String[] args) throws IOException {
		LineByLinePrintCanvas animatedCanvas = new LineByLinePrintCanvas(256,256);
        JFrame jframe = new JFrame();
        jframe.getContentPane().add(animatedCanvas, BorderLayout.CENTER);
        jframe.setSize(new Dimension(256,256));
        jframe.setVisible(true);
        System.out.println("Keep pressing enter to starting printing..");
        animatedCanvas.begin();
    }

}
