package tilt.game.entities;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

import tilt.game.level.Level;
import tilt.tools.Math2;
import tilt.tools.Textures;

public class Ball extends Entity {

	// OpenGl GLU object, easy rendering 8-)
	Sphere sphere;

	// Constant ratio of slowdown per tick, also affects bounce back
	float friction = 0.9f;
	// "xAxis angle"
	float tilt;
	// "yAxis" angle
	float pitch;
	// Used for the shrink out effect
	float animation;

	// True if the ball has touched a hole
	public boolean falling;
	// True if the ball has touched a goal
	public boolean finished;

	public Ball(float x, float y, float z, float radius) {
		super(x, y, z);
		// We already have width and height, might as well use it
		width = height = radius;

		sphere = new Sphere();
		sphere.setTextureFlag(true);
	}

	@Override
	public void step(long delta) {
		xs += xa;
		ys += ya;
		zs += za;

		x += xs * (delta / 1000f);
		y += ys * (delta / 1000f);
		z += zs * (delta / 1000f);
	}

	@Override
	public boolean update() {
		if (finished) {
			// Shrink when finished
			width = height -= animation;
			animation += 0.01f;
			// When I can't shrink no more, remove me!
			if (width <= 0) {
				return true;
			}
		}

		// Accelerate downwards
		za = -3f;
		// If the ball hasn't fallen down a hole
		if (!falling) {
			// Do floor collision
			if (z < 0) {
				z = 0;
				zs = -zs * 0.4f;
				if (zs < 4) {
					zs = 0;
				}
			}
		}

		// Dampen movement
		xs *= friction;
		ys *= friction;

		// Tilt ball based on velocity
		tilt += xs;
		pitch += ys;

		// Prevent rotation from getting infinitely large
		tilt %= 360;
		pitch %= 360;

		// Return false because ball does not have to be removed yet
		return false;
	}

	/**
	 * Collision function inherited from entities, takes the current level to be
	 * check for collision with
	 */
	@Override
	public void collision(Level level) {
		// If the ball has fallen far enough, just restart the level
		if (z < -50) {
			level.restart = true;
		}

		// If the ball is falling skip collision
		if (falling) {
			return;
		}

		// Level tile size is a constant of 2, get the relative array positions\

		// Center of the ball
		int bx = (int) (x / 2);
		int by = (int) (y / 2);

		// Cardinal bearing positions
		int byTop = (int) ((y + width) / 2);
		int byBottom = (int) ((y - width) / 2);
		int bxRight = (int) ((x + width) / 2);
		int bxLeft = (int) ((x - width) / 2);

		// Check tile directly above ball
		if (level.tile(bx, byTop).blocks) {
			// Set the position of the ball to be just outside of the tile
			y = byTop * 2 - width;
			// Bounce back!
			ys = -ys * friction;
		}
		// Check tile directly below ball
		if (level.tile(bx, byBottom).blocks) {
			y = (byBottom + 1) * 2 + width;
			ys = -ys * friction;
		}
		// Check tile to the right
		if (level.tile(bxRight, by).blocks) {
			x = bxRight * 2 - width;
			xs = -xs * friction;
		}
		// Check tile to the left
		if (level.tile(bxLeft, by).blocks) {
			x = (bxLeft + 1) * 2 + width;
			xs = -xs * friction;
		}

		// Special case because -0.01 doesn't round to -1
		if (x < 0 || y < 0) {
			// Trigger a hole tile (make the ball fall)
			level.tile(-1, -1).trigger(this);
		} else {
			// Trigger the tile that the middle of the ball is touching
			level.tile(bx, by).trigger(this);
		}

		// Perform circle-circle collision for each point around the tile, to
		// account for cases where the ball is touching the wall partially on a
		// corner

		// Check the 9 tiles surrounding and occupied by the ball
		done: for (int y = by - 1; y <= by + 1; y++) {
			for (int x = bx - 1; x <= bx + 1; x++) {
				// If the tile is a wall...
				if (level.tile(x, y).blocks) {
					// Check its corners
					for (int i = 0; i <= 1; i++) {
						for (int q = 0; q <= 1; q++) {
							double distance = Math2.distance(this.x, this.y,
									(i + x) * 2, (q + y) * 2);
							// If the ball is inside the corner move it 180
							// degrees the other way
							if (distance < width) {
								// Get the angle between the ball and the corner
								double angle = Math2.angle(this.x, this.y,
										(i + x) * 2, (q + y) * 2);
								// Move the ball back
								this.x += (float) (Math.cos(angle) * (width - distance));
								this.y += (float) (Math.sin(angle) * (width - distance));
								// Once you've collided once, don't bother
								// checking again since it shouldn't happen
								// twice
								break done;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Collision function inherited from entities, takes an entity to be check
	 * for collision with
	 */
	@Override
	public void collision(Entity entity) {
		// If the entity is a ball and the ball has already reached the goal,
		// don't do collision
		if (entity instanceof Ball) {
			if (((Ball) entity).finished) {
				return;
			}
		}
		// Get the minimum distance between the balls before a collision has
		// occured
		float dr = width + entity.width;
		// Check the distance between the two balls
		double distance = Math2.distance(x, y, entity.x, entity.y);
		// If the distance is less than the minimum distance, a collision has
		// occured
		if (distance < dr) {
			// Get the angle between the two balls
			double angle = Math2.angle(x, y, entity.x, entity.y);
			// Move THIS back (minimum distance - current distance)
			x += (float) (Math.cos(angle) * (dr - distance));
			y += (float) (Math.sin(angle) * (dr - distance));
		}
	}

	/**
	 * Draw function inherited from entities
	 */
	@Override
	public void draw() {
		// Add the ball texture to the stack
		Textures.ball.bind();
		// Move to where the ball is (remembering that width = radius)
		GL11.glTranslatef(x, y, z + width);
		{
			// Deal with rotation
			GL11.glRotatef(tilt, 0, 1, 0);
			GL11.glRotatef(pitch, 1, 0, 1);
			{
				// Draw sphere with radius width, 18 * 6 faces
				sphere.draw(width, 18, 6);
			}
			// Rotate back to origin
			GL11.glRotatef(pitch, -1, 0, -1);
			GL11.glRotatef(tilt, 0, -1, 0);
		}
		// Move back to origin
		GL11.glTranslatef(-x, -y, -z - width);
	}

}
