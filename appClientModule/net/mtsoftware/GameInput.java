package net.mtsoftware;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * represents game input: mouse, keyboard, XBox controller, etc
 * 
 * note: user input is delayed by up to 1 frame
 * 
 * @author mtutaj
 *
 */
public class GameInput implements KeyListener {

	// controllers
	public final int KEYBOARD_OLD = 0;
	public final int KEYBOARD_NEW = 1;

	// buttons
	public final int UP = 0;
	public final int DOWN = 1;
	public final int LEFT = 2;
	public final int RIGHT = 3;
	public final int LEFT_SHOULDER = 4;
	public final int RIGHT_SHOULDER = 5;
	
	private GameControllerInput[] controllers;
	
	public GameInput() {
		controllers = new GameControllerInput[2];
		controllers[KEYBOARD_OLD] = new GameControllerInput();
		controllers[KEYBOARD_NEW] = new GameControllerInput();
	}
	
	class GameButtonState {
		public int halfTransitionCount;
		public boolean endedDown;
		
		public void set(GameButtonState i) {
			this.halfTransitionCount = i.halfTransitionCount;
			this.endedDown = i.endedDown;
		}
	}
	
	class GameControllerInput {
		
		// we differentiate between analog and digital input to perform
		// analog/digital movement tuning
		public boolean isAnalog;

		// state of controller stick
		public float startX;
		public float startY;
		public float minX;
		public float minY;
		public float maxX;
		public float maxY;
		public float endX;
		public float endY;
		
		public GameButtonState[] button;
		
		public GameControllerInput() {
			 
			button = new GameButtonState[6];
			 
			for( int i=0; i<button.length; i++ ) {
				button[i] = new GameButtonState();
			}
		}
		
		public void set(GameControllerInput i) {
			this.isAnalog = i.isAnalog;
			this.startX = i.startX;
			this.startY = i.startY;
			this.minX = i.minX;
			this.minY = i.minY;
			this.maxX = i.maxX;
			this.maxY = i.maxY;
			this.endX = i.endX;
			this.endY = i.endY;

			for( int j=0; j<button.length; j++ ) {
				button[j].set(i.button[j]);
			}
		}
	}

	boolean isUpKeyDown() {
		return controllers[KEYBOARD_NEW].button[UP].endedDown &&
				controllers[KEYBOARD_NEW].button[UP].halfTransitionCount!=0;
	}
	
	boolean isDownKeyDown() {
		return controllers[KEYBOARD_NEW].button[DOWN].endedDown &&
				controllers[KEYBOARD_NEW].button[DOWN].halfTransitionCount!=0;
	}

	boolean isLeftKeyDown() {
		return controllers[KEYBOARD_NEW].button[LEFT].endedDown &&
				controllers[KEYBOARD_NEW].button[LEFT].halfTransitionCount!=0;
	}
	
	boolean isRightKeyDown() {
		return controllers[KEYBOARD_NEW].button[RIGHT].endedDown &&
				controllers[KEYBOARD_NEW].button[RIGHT].halfTransitionCount!=0;
	}

	void finishFrame() {
		// copy new controller to old controller
		controllers[KEYBOARD_OLD].set(controllers[KEYBOARD_NEW]);
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		
		int button = -1;
		
		switch(event.getKeyCode()) {
			case KeyEvent.VK_UP: button = UP; break;
			case KeyEvent.VK_DOWN: button = DOWN; break;
			case KeyEvent.VK_LEFT: button = LEFT; break;
			case KeyEvent.VK_RIGHT: button = RIGHT; break;
		}

		GameButtonState newState = controllers[KEYBOARD_NEW].button[button];
		GameButtonState oldState = controllers[KEYBOARD_OLD].button[button];
		
		newState.endedDown = true;
		newState.halfTransitionCount = newState.endedDown != oldState.endedDown ? 1 : 0;
	}

	@Override
	public void keyReleased(KeyEvent event) {

		int button = -1;
		
		switch(event.getKeyCode()) {
			case KeyEvent.VK_UP: button = UP; break;
			case KeyEvent.VK_DOWN: button = DOWN; break;
			case KeyEvent.VK_LEFT: button = LEFT; break;
			case KeyEvent.VK_RIGHT: button = RIGHT; break;
		}

		GameButtonState newState = controllers[KEYBOARD_NEW].button[button];
		GameButtonState oldState = controllers[KEYBOARD_OLD].button[button];
		
		newState.endedDown = false;
		newState.halfTransitionCount = newState.endedDown != oldState.endedDown ? 1 : 0;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// we won't use it
	}
}
