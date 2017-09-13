/**
 * Handles the creation of PushBalls.
 */

package net.spideynn.mazepulse.handlers;

import java.util.ArrayList;

import net.spideynn.mazepulse.entities.Player;
import net.spideynn.mazepulse.entities.PushBall;


public class EnemyFactory {
	
	private static ArrayList<PushBall> pushBalls;
	private static Player player;
	
	public static void init(ArrayList<PushBall> pb, Player p) {
		pushBalls = pb;
		player = p;
	}
	
	public static void spawn(double x, double y, double speed) {
		pushBalls.add(new PushBall(x, y, speed, player));
	}
	
}
