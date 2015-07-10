package net.mtsoftware;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.MemoryImageSource;

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

		// set initial window size to be 600x300
		frame.setSize(600, 300);
		
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

    	int[] bitmapMemory = null;
    	MemoryImageSource bitmap = null;
    	Image image;
		int xOffset = 0;
		int yOffset = 0;
    	
    	void createWeirdGradient(int width, int height, int xOffset, int yOffset) {
    		
    		bitmapMemory = new int[width*height];

    		// we store one pixel per 32-bits, and integer is 32-bit wide
    		int pos = 0;
    		for( int y=0; y<height; y++ ) {
    			for( int x=0; x<width; x++ ) {
    				int blue = (x + xOffset)%256;
    				int green = (y + yOffset)%256;
    				bitmapMemory[pos++] = new Color(0, green, blue).getRGB();
    			}
    		}
    	}

    	void resizeBitmap(int width, int height) {
    	
    		int offset = 0;
    		int scan = width;

    		// render weird gradient
    		createWeirdGradient(width, height, xOffset, yOffset);
    		bitmap = new MemoryImageSource(width, height, bitmapMemory, offset, scan);
    		image = createImage(bitmap);
    		
    		xOffset++; // animate weird gradient with every window resize
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
