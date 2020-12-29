import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.JFrame;

/*
 * You idiot!
 * You never save your work. Had
 * to redo this all over again.
 * 
 * Thanks to:
 * stackoverflow.com/questions/24958366
 */


public class MovingAnimation2 {
	
	private BufferedImage img;
	private Canvas canvas;
	
	public MovingAnimation2(int width, int height) {
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(width, height));
        canvas.setMaximumSize(new Dimension(width, height));
        canvas.setMinimumSize(new Dimension(width, height));
    }
	
	private Canvas getCanvas() { return canvas; }

	public void begin() {
		 for(;;){
	            try {
	                Thread.sleep(500);
	            } catch (InterruptedException e) {
	            	System.out.println(e.getMessage());
	            }
	            myDoPaint();
	        }
		
	}
	
	public void myDoPaint() {
    	Random r = new Random();
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
            	int red=r.nextInt(256);
        		int green=r.nextInt(256);
        		int blue=r.nextInt(256);
        		Color c=new Color(red,green,blue);
            	int color = c.getRGB();
            	img.setRGB(x, y, color);
            }
        }
        
        Graphics2D g2 = (Graphics2D) canvas.getGraphics();
        g2.drawImage(img, null, null);
    }
	
	
	public static void main(String[] args) {
    	MovingAnimation2 animatedCanvas = new MovingAnimation2(256,256);
        JFrame jframe = new JFrame();
        jframe.getContentPane().add(animatedCanvas.getCanvas(), BorderLayout.CENTER);
        jframe.setSize(new Dimension(256,256));
        jframe.setVisible(true);
        animatedCanvas.begin();
    }

}
