/**
 * The main menu.
 */

package net.spideynn.mazepulse.gamestates;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import net.spideynn.mazepulse.entities.Bubble;
import net.spideynn.mazepulse.entities.GameButton;
import net.spideynn.mazepulse.handlers.GameStateManager;
import net.spideynn.mazepulse.handlers.ImageLoader;
import net.spideynn.mazepulse.handlers.JukeBox;
import net.spideynn.mazepulse.handlers.Mouse;
import net.spideynn.mazepulse.main.Game;
import net.spideynn.mazepulse.main.GamePanel;


public class MenuState extends GameState {
	
	// bg image
	private BufferedImage bg;
	
	// bubbles
	private ArrayList<Bubble> bubbles;
	private int bubbleTimer;
	
	// buttons
	private int currentChoice = 0;
	private GameButton[] options;
	
	// fonts and colors
	private Color titleColor;
	private Font titleFont;
	private Font font;
	private Font font2;
	
	// fading
	private int fadeInTimer;
	private int fadeInDelay;
	private int fadeOutTimer;
	private int fadeOutDelay;
	private int alpha;
	private int nextState;
	
	public MenuState(GameStateManager gsm) {
		super(gsm);
		init();
	}
	
	public void init() {
		
		// load bg image
		bg = ImageLoader.BG;
		
		// load fonts
		try {
			Font scFont = Font.createFont(
					Font.TRUETYPE_FONT,
					getClass().getResourceAsStream("/fonts/SECRCODE.TTF"));
			titleColor = Color.WHITE;
			titleFont = scFont.deriveFont(64f);
			font = scFont.deriveFont(26f);
			font2 = scFont.deriveFont(14f);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		// show mouse cursor
		Game.setCursor(Game.VISIBLE);
		
		// set up buttons
		options = new GameButton[2];
		options[0] = new GameButton(320, 300, 100, 50);
		options[0].setText("s t a r t", font);
		options[1] = new GameButton(320, 350, 100, 50);
		options[1].setText("q u i t", font);
		
		// fade timer
		fadeInTimer = 0;
		fadeInDelay = 60;
		fadeOutTimer = -1;
		fadeOutDelay = 60;
		
		// music
		//if(!JukeBox.isPlaying("menumusic")) {
			//JukeBox.loop("menumusic", 3000, JukeBox.getFrames("menumusic") - 3000);
		//}
		JukeBox.stop("bgmusic1");
		
		// bubbles
		bubbles = new ArrayList<>();
		bubbleTimer = 0;
		
	}
	
	public void update() {
		
		// check keys
		handleInput();
		
		// check buttons for hover
		for(int i = 0; i < options.length; i++) {
			if(currentChoice == i) {
				options[i].setHover(true);
			}
			else {
				options[i].setHover(false);
			}
		}
		
		// update fade
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
		
		// bubbles
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
		
	}
	
	public void draw(Graphics2D g) {
		
		// draw bg
		g.drawImage(bg, 0, 0, null);
		
		// draw bubbles
		for (Bubble bubble : bubbles) {
			bubble.draw(g);
		}
		
		// draw title
		g.setColor(titleColor);
		g.setFont(titleFont);
		g.drawString("M A Z E", 100, 180);
		g.drawString("P U L S E", 100, 230);
		
		// draw menu options
		g.setFont(font);
		g.setColor(Color.WHITE);
		
		// draw buttons
		for (GameButton option : options) {
			option.draw(g);
		}
		
		// other
		g.setFont(font2);
		g.setColor(Color.BLACK);
		g.drawString("2012 &copy; Spideynn.", 10, 470);
		
		// draw fade
		if(fadeInTimer >= 0) {
			g.setColor(new Color(255, 255, 255, 255 - alpha));
			g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		}
		if(fadeOutTimer >= 0) {
			g.setColor(new Color(255, 255, 255, alpha));
			g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		}
		
	}
	
	private void select() {
		
		// already transitioning, cannot select
		if(fadeOutTimer != -1) return;
		
		// go to level select
		if(currentChoice == 0) {
			nextState = GameStateManager.LEVEL_SELECT_STATE;
			fadeInTimer = -1;
			fadeOutTimer = 0;
			JukeBox.play("menuclick");
		}
		
		// quit
		else if(currentChoice == 1) {
			System.exit(0);
		}
	}
	
	public void handleInput() {
		
		// see if button is clicked
		if(Mouse.isPressed()) {
			select();
		}
		
		// check if mouse is hovering over buttons
		boolean hit = false;
		for(int i = 0; i < options.length; i++) {
			if(options[i].isHovering(Mouse.x, Mouse.y)) {
				currentChoice = i;
				hit = true;
				break;
			}
		}
		if(!hit) currentChoice = -1;
		
	}
	
}










