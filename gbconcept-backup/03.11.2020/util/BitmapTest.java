package util;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

public class BitmapTest {
	public static void main(String args[]) {
		int pixSize = 1;
		Bitmap bmap = new Bitmap(pixSize);
		JFrame jp = new JFrame();
        jp.getContentPane().add(bmap, BorderLayout.CENTER);
        jp.setSize(new Dimension(Bitmap.SIZE,Bitmap.SIZE));
        jp.setVisible(true);
	}

}
