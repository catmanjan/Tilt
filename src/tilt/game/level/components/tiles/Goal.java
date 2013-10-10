package tilt.game.level.components.tiles;

import org.lwjgl.opengl.GL11;

import tilt.game.entities.Ball;
import tilt.game.level.components.Tile;
import tilt.game.level.components.TileType;
import tilt.tools.Textures;

public class Goal extends Tile {

	// Stores ring positions
	private float[] effect;

	public Goal() {
		super(TileType.GOAL, 2, 0);
		effect = new float[3];
		for (int i = 0; i < effect.length; i++) {
			effect[i] = i * (0.3f / effect.length);
		}
	}

	@Override
	public void trigger(Ball ball) {
		ball.finished = true;
		ball.xs = ball.ys = 0;
	}

	@Override
	public void update(long delta) {
		for (int i = 0; i < effect.length; i++) {
			effect[i] += 0.004f;
			effect[i] %= 0.3f;
		}
	}

	public void draw() {
		drawBody();

		// Center for the effect
		GL11.glTranslatef(1, 1, 0);
		Textures.effect.bind();
		for (int i = 0; i < effect.length; i++) {
			GL11.glBegin(GL11.GL_QUADS);
			{
				// Make the pretty effect!
				GL11.glTexCoord2f(0.0f, 0.0f);
				GL11.glVertex3f(2f * -effect[i] - 0.3f, 2f * -effect[i] - 0.3f,
						-height + effect[i] * 7);
				GL11.glTexCoord2f(1.0f, 0.0f);
				GL11.glVertex3f(2.2f * effect[i] + 0.3f,
						2f * -effect[i] - 0.3f, -height + effect[i] * 7);
				GL11.glTexCoord2f(1.0f, 1.0f);
				GL11.glVertex3f(2f * effect[i] + 0.3f, 2f * effect[i] + 0.3f,
						-height + effect[i] * 7);
				GL11.glTexCoord2f(0.0f, 1.0f);
				GL11.glVertex3f(2f * -effect[i] - 0.3f, 2f * effect[i] + 0.3f,
						-height + effect[i] * 7);
			}
			GL11.glEnd();
		}
		GL11.glTranslatef(-1, -1, 0);
	}

}
