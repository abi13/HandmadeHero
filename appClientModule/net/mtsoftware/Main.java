package net.mtsoftware;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;


public class Main {
	
	public static void main(String[] args) {
		
		// create the main frame window
		JFrame frame = new JFrame("Handmade Hero Day 004");
		
		// exit the application when Close button is clicked
		// Note: the default is JFrame.HIDE_ON_CLOSE — Hide the frame, but keep 
		// the application running. This can be frustrating, because it looks like 
		// you have "killed" the program, but it keeps on running, and you see no frame.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// center the main frame window
		frame.setLocationRelativeTo(null);

		// set initial window size to be 300x100
		frame.setSize(300, 100);
		
		// set custom content pane
		MainContentPane mainContentPane = new MainContentPane();
		frame.setContentPane(mainContentPane);

		// added handling of events when main content pane is resized / moved/ shown/ hidden
		frame.addComponentListener(mainContentPane);

		// show it
		frame.setVisible(true);
	}

	/* (non-Java-doc)
	 * @see java.lang.Object#Object()
	 */
	public Main() {
		super();
	}
	
    static class MainContentPane extends JComponent implements ComponentListener {
    	
		private static final long serialVersionUID = 1L;

		Color color = Color.blue;
    	int[] bitmapMemory = null;
    	MemoryImageSource bitmap = null;
    	Image image;
    	
    	void resizeBitmap(int width, int height) {
    		
    		int offset = 0;
    		int scan = width;
    		
    		// we store one pixel per 32-bits, and integer is 32-bit wide
    		bitmapMemory = new int[width*height];
    		Arrays.fill(bitmapMemory, color.getRGB());
    		bitmap = new MemoryImageSource(width, height, bitmapMemory, offset, scan);
    		image = createImage(bitmap);
    	}
    	
    	public void paintComponent(Graphics g) {
    		g.drawImage(image,  0, 0, null);
    		
	    }
		
    	@Override
		public void componentHidden(ComponentEvent e) {
		}

    	@Override
		public void componentMoved(ComponentEvent e) {
		}
		
		@Override
		public void componentResized(ComponentEvent e) {
			System.out.println("resized width="+getWidth()+", height="+getHeight());
			resizeBitmap(getWidth(), getHeight());
		}

		@Override
		public void componentShown(ComponentEvent e) {
		}
	}
}
