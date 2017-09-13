/**
 * Player must reach goal to advance
 * to the next level.
 */

package net.spideynn.mazepulse.entities;

import java.awt.Color;
import java.awt.Graphics2D;

import net.spideynn.mazepulse.handlers.ParticleFactory;


public class Goal extends GameObject {
	
	private int tick;
	
	public Goal() {
		width = height = 100; // default: 85
		color = new Color(255, 128, 128, 64);
		colorBorder = new Color(color.getRed(), color.getGreen(), color.getBlue());
		tick = 0;
	}
	
	public void update() {
		tick++;
		if(tick == 60) {
			tick = 0;
			ParticleFactory.createWave(x, y, width, color);
		}
	}
	
	public void draw(Graphics2D g) {
		drawCircleNoBorder(g);
	}
	
}
