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
		JFrame frame = new JFrame("Handmade Hero Day 005");
		
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

		int xOffset = 0;
		int yOffset = 0;
    	BackBuffer backBuffer = new BackBuffer();
    	
    	public void paintComponent(Graphics g) {
    		// display offscreen buffer in window
    		backBuffer.renderWeirdGradient(this, g, getWidth(), getHeight(), xOffset, yOffset);
	    }
		
    	@Override
		public void componentHidden(ComponentEvent e) {
		}

    	@Override
		public void componentMoved(ComponentEvent e) {
		}
		
		@Override
		public void componentResized(ComponentEvent e) {
    		// animate weird gradient with every window resize
    		xOffset++; 
    		yOffset++;
		}

		@Override
		public void componentShown(ComponentEvent e) {
		}
	}
    
    // fixed size back buffer 1280x760 pixels
    static class BackBuffer {
    	int[] bitmapMemory = null;
    	final int width = 1280;
    	final int height = 760;
    	int bytesPerPixel = 4;
    	int pitch = width;

    	public BackBuffer() {
    		createWeirdGradient();
    	}
    	
    	public void createWeirdGradient() {
    		
    		// allocate bitmap memory
    		bitmapMemory = new int[width*height];
    	}
    	
    	public void animateWeirdGradient(int xOffset, int yOffset) {
    		
    		// we store one pixel per 32-bits, and integer is 32-bit wide
    		int pos = 0;
    		for( int y=0; y<height; y++ ) {
    			for( int x=0; x<width; x++ ) {
    				int blue = (x+xOffset)%256;
    				int green = (y+yOffset)%256;
    				bitmapMemory[pos++] = new Color(0, green, blue).getRGB();
    			}
    		}
    	}

    	public void renderWeirdGradient(JComponent component, Graphics g, 
    			int width, int height, int xOffset, int yOffset) {
    		
    		animateWeirdGradient(xOffset, yOffset);
    		MemoryImageSource bitmap = new MemoryImageSource(width, height, bitmapMemory, 0, pitch);
    		Image image = component.createImage(bitmap);
    		g.drawImage(image,  0, 0, null);
    	}
    }
}
