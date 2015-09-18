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
	static public final int KEYBOARD = 0;
	static public final int CONTROLLER_NEW = 1;
	static public final int CONTROLLER_OLD = 1;

	// buttons
	static public final int MOVE_UP = 0;
	static public final int MOVE_DOWN = 1;
	static public final int MOVE_LEFT = 2;
	static public final int MOVE_RIGHT = 3;
	static public final int ACTION_UP = 4;
	static public final int ACTION_DOWN = 5;
	static public final int ACTION_LEFT = 6;
	static public final int ACTION_RIGHT = 7;
	static public final int LEFT_SHOULDER = 8;
	static public final int RIGHT_SHOULDER = 9;
	static public final int BUTTON_COUNT = 10;
	
	private GameControllerInput[] controllers;
	
	public GameInput() {
		controllers = new GameControllerInput[3];
		controllers[KEYBOARD] = new GameControllerInput();
		controllers[CONTROLLER_OLD] = new GameControllerInput();
		controllers[CONTROLLER_NEW] = new GameControllerInput();
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
		public float stickAverageX;
		public float stickAverageY;
		
		public GameButtonState[] button;
		
		public GameControllerInput() {
			 
			button = new GameButtonState[BUTTON_COUNT];
			 
			for( int i=0; i<button.length; i++ ) {
				button[i] = new GameButtonState();
			}
		}
		
		public void set(GameControllerInput i) {
			this.isAnalog = i.isAnalog;
			this.stickAverageX = i.stickAverageX;
			this.stickAverageY = i.stickAverageY;

			for( int j=0; j<button.length; j++ ) {
				button[j].set(i.button[j]);
			}
		}
	}

	boolean isKeyDown(int button) {
		return controllers[KEYBOARD].button[button].endedDown &&
				controllers[KEYBOARD].button[button].halfTransitionCount!=0;
	}

	void finishFrame() {
		// copy new controller to old controller
		controllers[CONTROLLER_OLD].set(controllers[CONTROLLER_NEW]);
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		
		int button = -1;
		
		switch(event.getKeyCode()) {
			case KeyEvent.VK_W: button = MOVE_UP; break;
			case KeyEvent.VK_S: button = MOVE_DOWN; break;
			case KeyEvent.VK_A: button = MOVE_LEFT; break;
			case KeyEvent.VK_D: button = MOVE_RIGHT; break;
			case KeyEvent.VK_UP: button = ACTION_UP; break;
			case KeyEvent.VK_DOWN: button = ACTION_DOWN; break;
			case KeyEvent.VK_LEFT: button = ACTION_LEFT; break;
			case KeyEvent.VK_RIGHT: button = ACTION_RIGHT; break;
			case KeyEvent.VK_Q: button = LEFT_SHOULDER; break;
			case KeyEvent.VK_E: button = RIGHT_SHOULDER; break;
			case KeyEvent.VK_L: toggleInputRecording(); break;
		}

		GameButtonState newState = controllers[KEYBOARD].button[button];
		
		newState.endedDown = true;
		newState.halfTransitionCount++;
		// for digital button (game controller):
		//newState.halfTransitionCount = newState.endedDown != oldState.endedDown ? 1 : 0;
	}

	@Override
	public void keyReleased(KeyEvent event) {

		int button = -1;
		
		switch(event.getKeyCode()) {
			case KeyEvent.VK_W: button = MOVE_UP; break;
			case KeyEvent.VK_S: button = MOVE_DOWN; break;
			case KeyEvent.VK_A: button = MOVE_LEFT; break;
			case KeyEvent.VK_D: button = MOVE_RIGHT; break;
			case KeyEvent.VK_UP: button = ACTION_UP; break;
			case KeyEvent.VK_DOWN: button = ACTION_DOWN; break;
			case KeyEvent.VK_LEFT: button = ACTION_LEFT; break;
			case KeyEvent.VK_RIGHT: button = ACTION_RIGHT; break;
			case KeyEvent.VK_Q: button = LEFT_SHOULDER; break;
			case KeyEvent.VK_E: button = RIGHT_SHOULDER; break;
			case KeyEvent.VK_L: toggleInputRecording(); break;
		}

		GameButtonState newState = controllers[KEYBOARD].button[button];
		
		newState.endedDown = false;
		newState.halfTransitionCount++;
		
		// for digital button (game controller):
		//newState.halfTransitionCount = newState.endedDown != oldState.endedDown ? 1 : 0;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// we won't use it
	}
	
	public void onControllerEvent(GameState gameState) {
		int Controller_StickAverageX = 0; // placeholder
		int Controller_StickAverageY = 0; // placeholder
		gameState.updatePlayerX((int)(4.0f*Controller_StickAverageX));
		gameState.updatePlayerX((int)(4.0f*Controller_StickAverageY));
	}


	// input recording and playback
	private boolean inputRecording = false;
	private boolean inputPlaying = false;
	
	
	
	public boolean isInputRecording() {
		return inputRecording;
	}
	
	public void toggleInputRecording() {
		inputRecording = !inputRecording;
		
		// if stopped recording, then autoplay the recording
		if( !inputRecording ) {
			inputPlaying = true;
		}
	}
	
	public boolean isInputPlaying() {
		return inputPlaying;
	}
	
	public void toggleInputPlaying() {
		inputPlaying = !inputPlaying;
	}

	public void recordInput() {
		
	}
	
	public void playBackInput() {
		
	}
}
