package tilt.game.level.components.tiles;

import tilt.game.level.components.Tile;
import tilt.game.level.components.TileType;

public class Gate5 extends Tile {

	// Once is >5 toggle states
	long time;
	// Up/down effect
	float animation;
	// Has been triggered?
	boolean triggered;

	public Gate5() {
		super(TileType.WALL, 0, 2);
		blocks = true;
	}

	@Override
	public void update(long delta) {
		time += delta;
		if (time > 5000 && !triggered) {
			triggered = true;
			animation = 0;
			blocks = !blocks;
		}
		if (triggered) {
			animation = 0.075f;
			if (blocks) {
				height += animation;
				depth -= animation;
				type = TileType.WALL;
				if (height >= 2) {
					height = 2;
					depth = 0;
					triggered = false;
					time = 0;
				}
			} else {
				height -= animation;
				depth += animation;
				if (height <= 0) {
					type = TileType.FLOOR;
					height = 0;
					depth = 2;
					triggered = false;
					time = 0;
				}
			}
		}
	}

}
