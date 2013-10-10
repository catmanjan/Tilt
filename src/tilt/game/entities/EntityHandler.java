package tilt.game.entities;

import java.util.ArrayList;
import java.util.Iterator;

import tilt.game.level.Level;

@SuppressWarnings("serial")
public class EntityHandler extends ArrayList<Entity> {

	public void draw() {
		for (Entity e : this) {
			e.draw();
		}
	}

	public void update(long delta) {
		Iterator<Entity> iterator = iterator();
		while (iterator.hasNext()) {
			Entity e = iterator.next();
			e.step(delta);
			if (e.update()) {
				iterator.remove();
			}
		}
	}

	public void applyForce(float xs, float ys) {
		for (Entity e : this) {
			if (e.z <= 0) {
				e.xa = xs;
				e.ya = ys;
			}
		}
	}

	public void collision(Level level) {
		for (Entity e : this) {
			for (Entity p : this) {
				if (p != e) {
					e.collision(p);
				}
			}
			e.collision(level);
		}
	}

}
