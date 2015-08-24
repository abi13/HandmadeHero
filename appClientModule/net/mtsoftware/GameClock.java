package net.mtsoftware;

/**
 * used in a game loop, computes performance variables
 * frame-per-second, and percentage of time used for logic, graphics and sound updates;
 * 
 * within game loop, for every frame, we do 3 things:
 * 1) update game logic
 * 2) update graphics
 * 3) update sound
 * 
 * therefore we need to keep 3 counters
 * 
 * @author mtutaj
 *
 */
public class GameClock {

	public static final int MONITOR_REFRESH_HZ = 60;
	public static final int GAME_UPDATE_HZ = MONITOR_REFRESH_HZ/2;
	public static final long NANOSECONDS_ELAPSED_PER_FRAME = 1000000000 / GAME_UPDATE_HZ;
	
	long lastCounter; // time counter in ns at the end of previous game frame
	long counter1; // time counter in ns at the end of update-game-logic phase
	long counter2; // time counter in ns at the end of update-graphics phase
	long counter3; // time counter in ns at the end of update-sound phase
	
	void updateGameLogicCounter() {
		counter1 = System.nanoTime();
	}

	void updateGraphicsCounter() {
		counter2 = System.nanoTime();
	}

	void updateSoundCounter() {
		counter3 = System.nanoTime();
	}

	void newFrame() throws InterruptedException {
		// compute nr of ns to sleep until the end of the current frame
		long nsToSleep = NANOSECONDS_ELAPSED_PER_FRAME - counter3 + lastCounter;
		if( nsToSleep>0 ) {
			System.out.println("sleep "+nsToSleep/1000000+"ms");
			Thread.sleep(nsToSleep/1000000, (int)(nsToSleep%1000000));
		}

		lastCounter = counter3;
	}
	
	void start() {
		lastCounter = System.nanoTime();
	}
	
	void printPerformanceVariables() {
		long elapsedFrameTime = counter3 - lastCounter;
		System.out.println("fps "+1000000000l/elapsedFrameTime+
				" logic "+(100*(counter1-lastCounter)/elapsedFrameTime)+"%"+
				" graph "+(100*(counter2-counter1)/elapsedFrameTime)+"%"+
				" sound "+(100*(counter3-counter2)/elapsedFrameTime)+"%");
				
	}
}
