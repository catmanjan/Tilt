package tilt.game.level.components;

import org.lwjgl.opengl.GL11;

import tilt.game.entities.Ball;
import tilt.tools.Textures;

public class Tile {

	protected float depth = 2;
	protected float height = 0;
	public TileType type;
	public boolean blocks;

	public Tile(TileType type, float depth, float height) {
		this.type = type;
		this.depth = depth;
		this.height = height;
	}

	/**
	 * Called when the center of the ball (x, y) touches this tile
	 */
	public void trigger(Ball ball) {

	}

	/**
	 * Called every tick
	 */
	public void update(long delta) {

	}

	/**
	 * Draw the main part of the tile, the only reason this is abstracted from
	 * the draw method is to remove redundancy in the Goal class
	 */
	protected void drawBody() {
		Textures.top(type).bind();

		GL11.glNormal3f(0, 1, 0);
		GL11.glBegin(GL11.GL_QUADS);
		{
			// Top face
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex3f(0, 0, height);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(2, 0, height);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(2, 2, height);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(0, 2, height);
		}
		GL11.glEnd();

		Textures.side(type).bind();

		GL11.glBegin(GL11.GL_QUADS);
		{
			// North face
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(0, 2, height);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(2, 2, height);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(2, 2, -depth);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex3f(0, 2, -depth);

			// West face
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(0, 2, -depth);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex3f(0, 0, -depth);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(0, 0, height);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(0, 2, height);
		}
		GL11.glEnd();

		// Render on the other side
		GL11.glFrontFace(GL11.GL_CW);

		GL11.glBegin(GL11.GL_QUADS);
		{
			// East face
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(2, 2, -depth);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex3f(2, 0, -depth);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(2, 0, height);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(2, 2, height);

			// South face
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(0, 0, height);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(2, 0, height);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(2, 0, -depth);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex3f(0, 0, -depth);
		}
		GL11.glEnd();

		// Return to normal rendering side
		GL11.glFrontFace(GL11.GL_CCW);
	}

	/**
	 * Called by Level, draws the tile
	 */
	public void draw() {
		drawBody();
	}

	@Override
	public String toString() {
		return type.name();
	}

}
