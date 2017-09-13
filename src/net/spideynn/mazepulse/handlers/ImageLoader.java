/**
 * Loads all images at game start.
 */

package net.spideynn.mazepulse.handlers;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class ImageLoader {
	
	//public static BufferedImage LOGO = load("/logo.gif");
	public static BufferedImage LOGO = load("/star.png");
	public static BufferedImage BG = load("/menubg.jpg");
	public static BufferedImage STAR = load("/star.png");
	public static BufferedImage STAROUTLINE = load("/staroutline.png");
	
	private static BufferedImage load(String s) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(ImageLoader.class.getResourceAsStream(s));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return image;
	}
	
}
