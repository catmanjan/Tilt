package tilt.game.entities;

import tilt.game.level.Level;

public abstract class Entity {

	public float x;
	public float y;
	public float z;

	public float xs;
	public float ys;
	public float zs;

	public float xa;
	public float ya;
	public float za;

	public float width;
	public float height;

	public Entity(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Update the position of the entity
	 * 
	 * @return true if entity needs to be removed from list
	 */
	public abstract boolean update();

	public void step(long delta) {
		xs += xa;
		ys += ya;
		zs += za;

		x += xs * (delta / 1000f);
		y += ys * (delta / 1000f);
		z += zs * (delta / 1000f);
	}

	public abstract void collision(Level level);

	public abstract void collision(Entity entity);

	public abstract void draw();

}
