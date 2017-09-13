/**
 * Key input handler class.
 */

package net.spideynn.mazepulse.handlers;

import java.awt.event.KeyEvent;

public class Keys {
	
	public static final int NUM_KEYS = 3;
	
	public static boolean keyState[] = new boolean[NUM_KEYS];
	public static boolean prevKeyState[] = new boolean[NUM_KEYS];
	
	public static int ENTER = 0;
	public static int ESCAPE = 1;
	public static int R = 2;
	
	public static void keySet(int i, boolean b) {
		if(i == KeyEvent.VK_ENTER) keyState[ENTER] = b;
		if(i == KeyEvent.VK_ESCAPE) keyState[ESCAPE] = b;
		if(i == KeyEvent.VK_R) keyState[R] = b;
	}
	
	public static void update() {
		for(int i = 0; i < NUM_KEYS; i++) {
			prevKeyState[i] = keyState[i];
		}
	}
	
	public static boolean isPressed(int i) {
		return keyState[i] && !prevKeyState[i];
	}
	
}
