package tilt.game.level.components.tiles;

import tilt.game.level.components.Tile;
import tilt.game.level.components.TileType;

public class Wall extends Tile {

	public Wall() {
		super(TileType.WALL, 0, 2);
		blocks = true;
	}

}
