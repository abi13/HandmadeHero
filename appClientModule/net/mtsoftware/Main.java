package net.mtsoftware;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.MemoryImageSource;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;


public class Main {
	
	static boolean isRunning = true;
	static JFrame frame;
	static MainContentPane pane;
	static GameSound sound;
	static GameInput input;
	
	public static void main(String[] args) {
		
		// create the main frame window
		frame = new JFrame("Handmade Hero Day 015");
		
		// exit the application when Close button is clicked
		// Note: the default is JFrame.HIDE_ON_CLOSE � Hide the frame, but keep 
		// the application running. This can be frustrating, because it looks like 
		// you have "killed" the program, but it keeps on running, and you see no frame.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// center the main frame window
		frame.setLocationRelativeTo(null);

		// set initial window size to be 600x300
		frame.setSize(600, 300);
		
		// set custom content pane
		pane = new MainContentPane();
		frame.setContentPane(pane);

		// show it
		frame.setVisible(true);
		
		try {
			input = new GameInput();
			sound = new GameSound();
			sound.init();
			frame.addKeyListener(input);
		} catch(LineUnavailableException e) {
			e.printStackTrace();
		}
		
		// our game loop
		runGameLoop();
	}

	static void runGameLoop() {
		
		GameClock clock = new GameClock();
		clock.start();
		
		while(isRunning) {
			clock.updateGameLogicCounter();
			if( input.isUpKeyDown() ) {
				sound.increaseTone();
			}
			if( input.isDownKeyDown() ) {
				sound.decreaseTone();
			}
			if( input.isLeftKeyDown() ) {
				pane.incrementXOffset();
			}
			if( input.isRightKeyDown() ) {
				pane.incrementYOffset();
			}
			
			pane.animate();
			clock.updateGraphicsCounter();

			sound.play();
			clock.updateSoundCounter();

			clock.printPerformanceVariables();
			clock.newFrame();
			input.finishFrame();
		}
	}
	
	/* (non-Java-doc)
	 * @see java.lang.Object#Object()
	 */
	public Main() {
		super();
	}
	
    static class MainContentPane extends JComponent {
    	
		private static final long serialVersionUID = 1L;

		int xOffset = 0;
		int yOffset = 0;
    	BackBuffer backBuffer = new BackBuffer();
    	
    	public MainContentPane() {
    	}
    	
    	public void paintComponent(Graphics g) {
    		// display offscreen buffer in window
    		backBuffer.renderWeirdGradient(this, g, getWidth(), getHeight(), xOffset, yOffset);
	    }
    	
    	public void incrementXOffset() {
    		xOffset++;
    	}
    	
    	public void incrementYOffset() {
    		yOffset++;
    	}

    	public void animate() {
    		paintComponent(getGraphics());
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
