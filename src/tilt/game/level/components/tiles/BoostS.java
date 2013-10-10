package tilt.game.level.components.tiles;

import tilt.game.entities.Ball;
import tilt.game.level.components.Tile;
import tilt.game.level.components.TileType;

public class BoostS extends Tile {

	public BoostS() {
		super(TileType.BOOSTS, 2, 0);
	}

	@Override
	public void trigger(Ball ball) {
		ball.ys -= 2;
	}

}
