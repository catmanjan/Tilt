package tilt.game.level.components.tiles;

import tilt.game.level.components.TileType;

public class InverseGate5 extends Gate5 {

	// Once is >5 toggle states
	long time;
	// Up/down effect
	float animation;
	// Has been triggered?
	boolean triggered;

	public InverseGate5() {
		super();
		type = TileType.FLOOR;
		depth = 2;
		height = 0;
		blocks = false;
	}
}
