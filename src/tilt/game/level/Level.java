package tilt.game.level;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import tilt.game.level.components.Tile;
import tilt.game.level.components.TileType;
import tilt.game.level.components.tiles.Hole;

/**
 * Stores information regarding level including name, tiles (and their indices)
 * 
 * Draws the board
 * 
 * Retrieve specific tiles give map (x, y) coordinates
 * 
 * @author Jan Martin
 * @group Joseph Guinto, Sam Luc
 * 
 */
public class Level {

	// Guess what "name" is
	public String name;
	// Having a 2d array of anything but primitives makes me want to kill myself
	public Tile[][] map;
	// True if the game needs to restart the current level
	public boolean restart;

	/**
	 * Update tiles
	 */
	public void update(long delta) {
		for (int y = 0; y < map.length; y++) {
			for (int x = 0; x < map[0].length; x++) {
				map[y][x].update(delta);
			}
		}
	}

	/**
	 * Draw the board (tiles)
	 */
	public void draw() {
		// Center the fucker
		GL11.glTranslatef(-map[0].length, -map.length, 0);
		for (int y = 0; y < map.length; y++) {
			for (int x = 0; x < map[0].length; x++) {
				if (map[y][x] != null) {
					map[y][x].draw();
				}
				GL11.glTranslatef(2, 0, 0);
			}
			GL11.glTranslatef(-map[0].length * 2, 2, 0);
		}
		GL11.glTranslatef(0, -map.length * 2, 0);
	}

	/**
	 * The positions of all the spawn tiles in the current level
	 * 
	 * @note this returns cartesian coordinates, not the index of the spawns, so
	 *       it is already centered on the spawn tile
	 * 
	 * @return an unsorted list of (x, y) coordinates, corresponding to where
	 *         ball/s should start
	 */
	public float[][] spawns() {
		// I think using an arraylist here is justified, is there a more
		// primitive list type with dynamic sizing in Java?
		ArrayList<float[]> spawnList = new ArrayList<float[]>();

		for (int y = 0; y < map.length; y++) {
			for (int x = 0; x < map[0].length; x++) {
				if (type(x, y) == TileType.SPAWN) {
					float[] position = { x * 2 + 1, y * 2 + 1 };
					spawnList.add(position);
				}
			}
		}

		// Maybe this arraylist wasn't such a good idea...
		float[][] spawns = new float[spawnList.size()][2];
		for (int i = 0; i < spawns.length; i++) {
			spawns[i] = spawnList.get(i);
		}
		// Yeah I know you can use that Apache Commons native casting stuff, but
		// I don't like Apache...

		return spawns;
	}

	/**
	 * Syntactic sugar
	 * 
	 * @return map[0].length
	 */
	public int width() {
		return map[0].length;
	}

	/**
	 * Syntactic sugar
	 * 
	 * @return map.length
	 */
	public int height() {
		return map.length;
	}

	/**
	 * Returns the Tile at coordinates (x, y).
	 * 
	 * Returns a Hole if out of bounds.
	 * 
	 * @param x
	 *            xCoordinate in the array
	 * @param y
	 *            yCoordinate in the array
	 * @return Tile object at coordinate
	 */
	public Tile tile(int x, int y) {
		if (x < 0 || y < 0 || x > map[0].length - 1 || y > map.length - 1) {
			return new Hole();
		}
		return map[y][x];
	}

	/**
	 * Returns the tile Type coordinates (x, y).
	 * 
	 * Returns Type.TILE if out of bounds.
	 * 
	 * @note This only exists to reduce the amount of object we're throwing
	 *       around, because they are baaad. This could be made better by just
	 *       using an integer to represent tile type.
	 * 
	 * @param x
	 *            xCoordinate in the array
	 * @param y
	 *            yCoordinate in the array
	 * @return Type enum of tile at coordinate
	 */
	public TileType type(int x, int y) {
		return tile(x, y).type;
	}

}
