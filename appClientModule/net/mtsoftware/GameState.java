package net.mtsoftware;

public class GameState {

	private int playerX = 100; // player x position
	private int playerY = 100; // player y position
	
	public int getPlayerX() {
		return playerX;
	}
	
	public void setPlayerX(int playerX) {
		this.playerX = playerX;
	}
	
	public void updatePlayerX(int playerXAdj) {
		this.playerX += playerXAdj;
	}

	public int getPlayerY() {
		return playerY;
	}
	
	public void setPlayerY(int playerY) {
		this.playerY = playerY;
	}

	public void updatePlayerY(int playerYAdj) {
		this.playerY += playerYAdj;
	}
}
