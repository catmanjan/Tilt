package tilt.game.level.components.tiles;

import tilt.game.entities.Ball;
import tilt.game.level.components.Tile;
import tilt.game.level.components.TileType;

public class BoostE extends Tile {

	public BoostE() {
		super(TileType.BOOSTE, 2, 0);
	}

	@Override
	public void trigger(Ball ball) {
		ball.xs += 2;
	}

}
