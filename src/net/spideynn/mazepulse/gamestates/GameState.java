package net.spideynn.mazepulse.gamestates;

import java.awt.Graphics2D;

import net.spideynn.mazepulse.handlers.GameStateManager;


public abstract class GameState {
	
	protected GameStateManager gsm;
	
	public GameState(GameStateManager gsm) {
		this.gsm = gsm;
	}
	
	public abstract void init();
	public abstract void update();
	public abstract void draw(Graphics2D g);
	public abstract void handleInput();
	
}
