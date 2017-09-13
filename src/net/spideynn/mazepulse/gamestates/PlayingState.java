/**
 * The playing screen.
 * All level information is obtained from LevelData.
 */

package net.spideynn.mazepulse.gamestates;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import net.spideynn.mazepulse.entities.Bouncer;
import net.spideynn.mazepulse.entities.Bubble;
import net.spideynn.mazepulse.entities.Cursor;
import net.spideynn.mazepulse.entities.GameButton;
import net.spideynn.mazepulse.entities.Goal;
import net.spideynn.mazepulse.entities.HitBall;
import net.spideynn.mazepulse.entities.Hole;
import net.spideynn.mazepulse.entities.Particle;
import net.spideynn.mazepulse.entities.Player;
import net.spideynn.mazepulse.entities.PushBall;
import net.spideynn.mazepulse.entities.Spawner;
import net.spideynn.mazepulse.handlers.EnemyFactory;
import net.spideynn.mazepulse.handlers.GameData;
import net.spideynn.mazepulse.handlers.GameStateManager;
import net.spideynn.mazepulse.handlers.ImageLoader;
import net.spideynn.mazepulse.handlers.JukeBox;
import net.spideynn.mazepulse.handlers.Keys;
import net.spideynn.mazepulse.handlers.LevelData;
import net.spideynn.mazepulse.handlers.Mouse;
import net.spideynn.mazepulse.handlers.ParticleFactory;
import net.spideynn.mazepulse.main.Game;
import net.spideynn.mazepulse.main.GamePanel;


public class PlayingState extends GameState {
	
	// background image
	private BufferedImage bg;
	
	private Player player;
	private Cursor cursor;
	private Goal goal;
	
	// time limit
	private int timer;
	private int timeLimit;
	
	// maximum number of hits allowed
	private int hitLimit;
	
	// various game objects
	private ArrayList<HitBall> hitBalls;
	private ArrayList<Bouncer> bouncers;
	private ArrayList<Hole> holes;
	private ArrayList<Spawner> spawners;
	private ArrayList<PushBall> pushBalls;
	private ArrayList<Particle> particles;
	private ArrayList<Bubble> bubbles;
	
	// random bubble timer
	private int bubbleTimer;
	
	// map dimensions
	private int levelWidth;
	private int levelHeight;
	
	// fading
	private int fadeInTimer;
	private int fadeInDelay;
	private int fadeOutTimer;
	private int fadeOutDelay;
	private int alpha;
	
	// events
	private int eventCount;
	private boolean eventFail;
	private boolean eventFinish;
	
	// buttons
	private GameButton resetButton;
	private GameButton backButton;
	
	// other
	private int nextState;
	
	public PlayingState(GameStateManager gsm) {
		super(gsm);
		init();
	}
	
	public void init() {
		
		// set up lists
		hitBalls = new ArrayList<>();
		bouncers = LevelData.getBouncers();
		holes = LevelData.getHoles();
		spawners = LevelData.getSpawners();
		pushBalls = new ArrayList<>();
		particles = new ArrayList<>();
		bubbles = new ArrayList<>();
		
		// set up player
		player = new Player();
		LevelData.setPlayer(player);
		
		// set up factories
		EnemyFactory.init(pushBalls, player);
		ParticleFactory.init(particles);
		
		// set up goal
		goal = new Goal();
		LevelData.setGoal(goal);
		
		// use custom cursor
		Game.setCursor(Game.INVISIBLE);
		cursor = new Cursor();
		
		// set up hits
		hitLimit = LevelData.getLimit();
		for(int i = 0; i < hitLimit; i++) {
			hitBalls.add(new HitBall(14 + i * 16, 14));
		}
		
		// level dimensions
		levelWidth = 640;
		levelHeight = 480;
		
		// set up bg image
		bg = ImageLoader.BG;
		
		// fading
		fadeInTimer = 0;
		fadeInDelay = 60;
		fadeOutTimer = -1;
		fadeOutDelay = 60;
		
		// events
		eventCount = 0;
		eventFail = false;
		eventFinish = false;
		
		// other
		bubbleTimer = 0;
		timeLimit = LevelData.getTime();
		timer = 0;
		
		// music
		JukeBox.stop("menumusic");
		if(!JukeBox.isPlaying("bgmusic1")) {
			JukeBox.loop("bgmusic1");
		}
		
		// buttons
		resetButton = new GameButton(10, 450);
		resetButton.setType(GameButton.LEFT);
		resetButton.setFont(LevelData.SC_FONT.deriveFont(Font.BOLD, 24f));
		resetButton.setText("reset");
		backButton = new GameButton(580, 450);
		backButton.setType(GameButton.LEFT);
		backButton.setFont(LevelData.SC_FONT.deriveFont(Font.BOLD, 24f));
		backButton.setText("quit");
		
	}
	
	private void setFade() {
		if(fadeInTimer >= 0) {
			fadeInTimer++;
			alpha = (int) (255.0 * fadeInTimer / fadeInDelay);
			if(fadeInTimer == fadeInDelay) {
				fadeInTimer = -1;
			}
		}
		if(fadeOutTimer >= 0) {
			fadeOutTimer++;
			alpha = (int) (255.0 * fadeOutTimer / fadeOutDelay);
			if(fadeOutTimer == fadeOutDelay) {
				gsm.setState(nextState);
			}
		}
		if(alpha < 0) alpha = 0;
		if(alpha > 255) alpha = 255;
	}
	
	private void drawFade(Graphics2D g) {
		if(fadeInTimer >= 0) {
			g.setColor(new Color(255, 255, 255, 255 - alpha));
			g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		}
		if(fadeOutTimer >= 0) {
			g.setColor(new Color(255, 255, 255, alpha));
			g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		}
	}
	
	public void update() {
		
		// check if reached hit limit
		if(player.isDead()) {
			eventFail = true;
		}
		
		// check if reached goal
		if(goal.containsCircle(player)) {
			player.setPosition(goal);
			player.setVector(0, 0);
			player.reachedGoal();
			eventFinish = true;
		}
		
		// play events
		if(eventFail) {
			eventFail();
		}
		if(eventFinish) {
			eventFinish();
		}
		
		// check timer
		if(timeLimit != -1 && !eventFinish) {
			timer++;
			if(timer > timeLimit) timer = timeLimit;
			if(timer == timeLimit) {
				player.setDead();
				eventFail = true;
			}
		}
		
		// check game input
		handleInput();
		
		// update player
		player.update();
		player.fixBounds(levelWidth, levelHeight);
		
		// check if mouse hits player
		if(fadeInTimer == -1 &&
			player.checkHit(player.getx() - Mouse.x, player.gety() - Mouse.y, 5)) {
			ParticleFactory.createSmallWave(
				14 + (hitLimit - player.getNumHits()) * 16,
				14,
				8
			);
		}
		
		// check num hits left
		for(int i = hitBalls.size(); i > hitLimit - player.getNumHits(); i--) {
			hitBalls.remove(i - 1);
		}
		
		// check if player hits bouncers
		for (Bouncer b : bouncers) {
			b.update();
			if (b.intersectsCircle(player)) {
				player.reflect(b.getx(), b.gety(), b.getWidth(), b.getImpulse());
				break;
			}
		}
		
		// update spawner
		spawners.forEach(Spawner::update);
		
		// update push balls
		for(int i = 0; i < pushBalls.size(); i++) {
			if(pushBalls.get(i).update()) {
				pushBalls.remove(i);
				i--;
			}
		}
		
		// update holes
		for (Hole h : holes) {

			h.update();

			// player fell into hole
			if (h.containsCircle(player)) {
				player.setPosition(h);
				player.setVector(0, 0);
				player.fellInHole();
				eventFail = true;
				break;
			}

		}
		
		// update particles
		for(int i = 0; i < particles.size(); i++) {
			if(particles.get(i).update()) {
				particles.remove(i);
				i--;
			}
		}
		
		// update cursor
		cursor.update();
		
		// update goal
		goal.update();
		
		// check bubbles
		bubbleTimer++;
		if(bubbleTimer == 60) {
			bubbles.add(new Bubble(Math.random() * 540 - 100, Math.random() * 100 + 480));
			bubbleTimer = 0;
		}
		for(int i = 0; i < bubbles.size(); i++) {
			if(bubbles.get(i).update()) {
				bubbles.remove(i);
				i--;
			}
		}
		
		// fading
		setFade();
		
	}
	
	public void draw(Graphics2D g) {
		
		// draw bg
		g.drawImage(bg, 0, 0, null);
		
		// draw bubbles
		for (Bubble bubble : bubbles) {
			bubble.draw(g);
		}
		
		// draw bouncers
		for (Bouncer bouncer : bouncers) {
			bouncer.draw(g);
		}
		
		// draw holes
		for (Hole hole : holes) {
			hole.draw(g);
		}
		
		// draw spawner
		for (Spawner spawner : spawners) {
			spawner.draw(g);
		}
		
		// draw push balls
		for (PushBall pushBall : pushBalls) {
			pushBall.draw(g);
		}
		
		// draw goal
		goal.draw(g);
		
		// draw player
		player.draw(g);
		
		// draw cursor
		cursor.draw(g);
		
		// draw particles
		for (Particle particle : particles) {
			particle.draw(g);
		}
		
		// draw hud
		for (HitBall hitBall : hitBalls) {
			hitBall.draw(g);
		}
		g.setColor(Color.WHITE);
		//g.drawString("(" + Mouse.x + ", " + Mouse.y + ")", 480, 430);
		g.setFont(LevelData.SC_FONT.deriveFont(Font.BOLD, 24f));
		g.drawString(Integer.toString(GameData.getCurrentScore()), 600, 22);
		if(timeLimit != -1) {
			int sec = (timeLimit - timer) / 60;
			int mil = (int) ((timeLimit - timer) % 60 * 100.0 / 60);
			String s = sec + ":";
			if(mil < 10) s += "0" + mil;
			else s += mil;
			g.drawString(s, 10, 42);
		}
		
		// draw buttons
		resetButton.draw(g);
		backButton.draw(g);
		
		// draw fade
		drawFade(g);
		
	}
	
	public void handleInput() {
		
		// restart level
		if(Keys.isPressed(Keys.R)) {
			reset();
		}
		
		// go back to menu
		if(Keys.isPressed(Keys.ESCAPE) && fadeOutTimer == -1) {
			nextState = GameStateManager.LEVEL_SELECT_STATE;
			fadeInTimer = -1;
			fadeOutTimer = 0;
		}
		
		// button
		if(resetButton.isHovering(Mouse.x, Mouse.y)) {
			resetButton.setHover(true);
		}
		else {
			resetButton.setHover(false);
		}
		if(Mouse.isPressed() && resetButton.isHovered()) {
			reset();
		}
		if(backButton.isHovering(Mouse.x, Mouse.y)) {
			backButton.setHover(true);
		}
		else {
			backButton.setHover(false);
		}
		if(Mouse.isPressed() && backButton.isHovered() && fadeOutTimer == -1) {
			fadeInTimer = -1;
			fadeOutTimer = 0;
			nextState = GameStateManager.LEVEL_SELECT_STATE;
		}
		
	}
	
	private void reset() {
		if(eventFinish) return;
		GameData.addScore(-1);
		init();
	}
	
	private void eventFail() {
		
		eventCount++;
		
		// restart level
		if(eventCount == 60) {
			reset();
		}
		
	}
	
	private void eventFinish() {
		
		eventCount++;
		
		if(eventCount == 1) {
			for (HitBall hitBall : hitBalls) {
				hitBall.setVector(5, 0);
			}
		}
		
		if(eventCount > 30 && hitBalls.size() > 0) {
			for(int i = 0; i < hitBalls.size(); i++) {
				HitBall hb = hitBalls.get(i);
				hb.update();
				if(hb.getx() > 600) {
					GameData.addScore(1);
					hitBalls.remove(i);
					i--;
					ParticleFactory.createSmallWave(hb.getx(), hb.gety(), hb.getWidth());
				}
			}
			eventCount--;
		}
		
		if(eventCount >= 60 && fadeOutTimer == -1) {
			
			fadeInTimer = -1;
			fadeOutTimer = 0;
			
			// finished level
			if((LevelData.getLevelIndex() + 1) % 4 == 0) {
				nextState = GameStateManager.LEVEL_SELECT_STATE;
				GameData.setFinalScore(LevelData.getLevelIndex() / 4);
				GameData.save();
			}
			
			// next part of level
			else {
				nextState = GameStateManager.LEVEL_INFO_STATE;
				LevelData.nextLevel();
			}
			
		}
		
	}
	
}
