package tilt.tools;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import tilt.game.level.components.TileType;

/**
 * That's right, static textures, you mad?
 * 
 * @author Jan Martin
 * @group Joseph Guinto, Sam Luc
 */
public class Textures {

	// These were lost to the ages
	// public static Texture icon16;
	// public static Texture icon32;
	// public static Texture icon48;

	public static Texture ball;
	public static Texture floor;
	public static Texture floorSide;
	public static Texture wall;

	public static Texture boostN;
	public static Texture boostE;
	public static Texture boostW;
	public static Texture boostS;

	public static Texture effect;
	public static Texture sky;

	public static TrueTypeFont font;

	/**
	 * Load all the textures, apply filters if necessary
	 */
	public static void init() {
		try {
			Font awtFont = Font.createFont(Font.TRUETYPE_FONT,
					ResourceLoader.getResourceAsStream("fonts/gretoon.ttf"));
			awtFont = awtFont.deriveFont(36f);
			font = new TrueTypeFont(awtFont, true);

			// icon16 = load("textures/icon16.png", false);
			// icon32 = load("textures/icon32.png", false);
			// icon48 = load("textures/icon48.png", true);

			effect = load("textures/effect.png", false);
			sky = load("textures/sky.jpg", false);
			ball = load("textures/ball.png", true);

			floor = load("textures/grass.png", true);
			floorSide = load("textures/grassSide.png", true);
			wall = load("textures/wall.png", true);

			boostN = load("textures/boostn.png", true);
			boostE = load("textures/booste.png", true);
			boostS = load("textures/boosts.png", true);
			boostW = load("textures/boostw.png", true);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (FontFormatException e) {
			e.printStackTrace();
		}
	}

	private static Texture load(String ref, boolean pixelPerfect)
			throws IOException {
		Texture tex = TextureLoader.getTexture(ref.substring(ref.length() - 3),
				ResourceLoader.getResourceAsStream(ref));

		if (pixelPerfect) {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
					GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
					GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		}

		return tex;
	}

	public static ByteBuffer toBuffer(Texture texture) {
		return ByteBuffer.wrap(texture.getTextureData());
	}

	/**
	 * Texture to be shown on top of the tile
	 * 
	 * @note This probably could have been simplified by just storing textures
	 *       in an array according to the index of the tile type, but its harder
	 *       to read that way and tutors seem to have a hard-on for enums
	 * 
	 * @return A texture
	 */
	public static Texture top(TileType type) {
		switch (type) {
		case WALL:
			return wall;
		case BOOSTN:
			return boostN;
		case BOOSTE:
			return boostE;
		case BOOSTS:
			return boostS;
		case BOOSTW:
			return boostW;
		default:
			return floor;
		}
	}

	/**
	 * Texture to be shown on the side of the tile
	 * 
	 * @return A texture
	 */
	public static Texture side(TileType type) {
		switch (type) {
		case FLOOR:
		case SPAWN:
		case GOAL:
		case BOOSTN:
		case BOOSTE:
		case BOOSTS:
		case BOOSTW:
			return floorSide;
		case WALL:
			return wall;
		default:
			return floor;
		}
	}

}
