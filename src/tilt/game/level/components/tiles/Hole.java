package tilt.game.level.components.tiles;

import tilt.game.entities.Ball;
import tilt.game.level.components.Tile;
import tilt.game.level.components.TileType;

public class Hole extends Tile {

	public Hole() {
		super(TileType.HOLE, 0, 0);
	}

	@Override
	public void trigger(Ball ball) {
		if (ball.z == 0) {
			ball.falling = true;
			ball.xs *= 0.1f;
			ball.ys *= 0.1f;
		}
	}

	@Override
	public void draw() {
		// Don't draw anything, it's a hole
	}

}
