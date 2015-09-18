package net.mtsoftware;

import java.awt.*;
import java.awt.image.MemoryImageSource;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;


public class Main {
	
	static boolean isRunning = true;
	static JFrame frame;
	static MainContentPane pane;
	static GameSound sound;
	static GameInput input;
	
	public static void main(String[] args) throws InterruptedException {
		
		// create the main frame window
		frame = new JFrame("Handmade Hero Day 023");
		
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

	static void runGameLoop() throws InterruptedException {
		
		GameState gameState = new GameState();
		
		GameClock clock = new GameClock();
		clock.start();
		
		while(isRunning) {
			clock.updateGameLogicCounter();
			if( input.isKeyDown(GameInput.ACTION_UP) ) {
				pane.incrementYOffset(+1);
				gameState.updatePlayerY(-10); // jump
			}
			if( input.isKeyDown(GameInput.ACTION_DOWN) ) {
				pane.incrementYOffset(-1);
			}
			if( input.isKeyDown(GameInput.ACTION_LEFT) ) {
				pane.incrementXOffset(+1);
			}
			if( input.isKeyDown(GameInput.ACTION_RIGHT) ) {
				pane.incrementXOffset(-1);
			}
			if( input.isKeyDown(GameInput.LEFT_SHOULDER) ) {
				sound.increaseTone();
			}
			if( input.isKeyDown(GameInput.RIGHT_SHOULDER) ) {
				sound.decreaseTone();
			}

			// handle player movement from keyboard
			if( input.isKeyDown(GameInput.MOVE_UP) ) {
				gameState.updatePlayerY(-1);
			}
			if( input.isKeyDown(GameInput.MOVE_DOWN) ) {
				gameState.updatePlayerY(+1);
			}
			if( input.isKeyDown(GameInput.MOVE_LEFT) ) {
				gameState.updatePlayerX(-1);
			}
			if( input.isKeyDown(GameInput.MOVE_RIGHT) ) {
				gameState.updatePlayerX(+1);
			}
			
			pane.animate(gameState);
			clock.updateGraphicsCounter();

			sound.play();

			input.finishFrame();
			clock.updateSoundCounter();

			clock.sleepUntilFrameEnd();
			clock.printPerformanceVariables();
			pane.flip();
			clock.newFrame();
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
    		backBuffer.renderWeirdGradient(g);
	    }
    	
    	public void incrementXOffset(int delta) {
    		xOffset += delta;
    		if( xOffset<0 )
    			xOffset += 256;
    	}
    	
    	public void incrementYOffset(int delta) {
    		yOffset += delta;
    		if( yOffset<0 )
    			yOffset += 256;
    	}

    	public void animate(GameState gameState) {
    		backBuffer.prepareWeirdGradient(this, gameState, getWidth(), getHeight(), xOffset, yOffset);
    	}

    	public void flip() {
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
    	Image image;
    	
    	public BackBuffer() {
    		createWeirdGradient();
    	}
    	
    	public void createWeirdGradient() {
    		
    		// allocate bitmap memory
    		bitmapMemory = new int[width*height];
    	}
    	
    	void animateWeirdGradient(int xOffset, int yOffset) {
    		
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

    	void renderPlayer(GameState gameState) {
    		
    		for( int y=gameState.getPlayerY()-5; y<gameState.getPlayerY()+5; y++ ) {
    			// ensure to draw within window boundaries
    			if( y<0 || y>=height )
    				continue;
    			int pos = y*width;
    			for( int x = gameState.getPlayerX()-5; x<gameState.getPlayerX()+5; x++ ) {
    				if( x>=0 && x<width ) {
    					bitmapMemory[pos+x] = Color.WHITE.getRGB();
    				}
    			}
    		}
    	}
    	
    	public void prepareWeirdGradient(JComponent component, GameState gameState, 
    			int width, int height, int xOffset, int yOffset) {
    		
    		animateWeirdGradient(xOffset, yOffset);
    		renderPlayer(gameState);
    		MemoryImageSource bitmap = new MemoryImageSource(width, height, bitmapMemory, 0, pitch);
    		image = component.createImage(bitmap);
    	}

    	public void renderWeirdGradient(Graphics g) {
    		g.drawImage(image, 0, 0, null);
    	}
    }
}
