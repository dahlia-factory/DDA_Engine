import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class DunGen {
	public static final int mapHeight = 128;
	public static final int mapWidth = 128;

	private static int[][] initMap() {
		int[][] map = new int[mapHeight][mapWidth];

		// fill maps with 1 for pre-generation
		for (int i = 0; i < mapHeight; i++) {
			for (int j = 0; j < mapWidth; j++) {
				map[i][j] = 1;
			}
		}

		return map;
	}

	// print the dungeon layout in ASCII using stringbuilder
	public static void printDungeonASCII(int[][] grid) {
		StringBuilder row = new StringBuilder(mapWidth * 2);
		for (int y = 0; y < mapHeight; y++) {
			row.setLength(0);
			for (int x = 0; x < mapWidth; x++) {
				if (grid[y][x] == 1) {
					row.append("██"); 
				} else {
					row.append("  "); 
				}
			}
			System.out.println(row.toString());
		}
	}


	private static int[][] generateMap() {
		// initialize a map
		int[][] map = initMap();

		// initial setup:
		// starting coordinate

		int posX = mapWidth / 2;
		int posY = mapHeight / 2;

		int dX = 0;
		int dY = 0;

		// a d512 between
		// int generating = (int)Math.floor(Math.random()) % 512;
		int generating = 0;

		// set the starting tile to zero
		map[posY][posX] = 0;

		ThreadLocalRandom rand = ThreadLocalRandom.current();

		while (true) {

			// if the d512 = 512, stop generation
			generating = rand.nextInt(256) + 1;

			if (generating == 256) {
				break;
			}

			// a d4 determines which way the hallway leads:
			// n == 1 (dx = 0, dy = -1)
			// e == 2 (dx = 1, dy = 0)
			// s == 3 (dx = 0, dy = 1)
			// w == 4 (dx = -1, dy = 0)
			int direction = rand.nextInt(4) + 1;

			if (direction == 1) {
				dX = 0;
				dY = -1;
			}

			else if (direction == 2) {
				dX = 1;
				dY = 0;
			}

			else if (direction == 3) {
				dX = 0;
				dY = 1;
			}

			else if (direction == 4) {
				dX = -1;
				dY = 0;
			}

			// a d6 determines the length of the corridor
			// Increased slightly to make hallways long enough to separate rooms cleanly
			int corridorLen = rand.nextInt(12) + 4; 
			// loop over distance
			for (int i = 0; i < corridorLen; i++) {
				// calculate hypothetical next tile
				// nextX = posX + dx, nextY = posY + dy
				int nextX = posX + dX;
				int nextY = posY + dY;

				// evaluate margin: if nextX and Y are within 1 and height-2:
				if (nextX >= 1 && nextX <= (mapWidth - 2) && nextY >= 1 && nextY <= (mapHeight - 2)) {
					// build the next bit of hallway
					posX = nextX;
					posY = nextY;

					map[posY][posX] = 0;
				}

				// if we went off the edge, we should probably stop that
				else {
					break;
				}
			}

			// Lowered chance (1 in 5) to restrict room saturation
			int roomChance = rand.nextInt(5); 

			if (roomChance == 1) {
				// roomWidth and roomHeight: randoms between 1 and 10
				int roomWidth = rand.nextInt(12) + 6;
				int roomHeight = rand.nextInt(12) + 6;

				int offsetX = roomWidth / 2;
				int offsetY = roomHeight / 2;

				// safely carve room tiles
				for (int localY = 0; localY < roomHeight; localY++) {
					for (int localX = 0; localX < roomWidth; localX++) {
						int targetX = (posX - offsetX) + localX;
						int targetY = (posY - offsetY) + localY;

						if (targetX >= 1 && targetX <= mapWidth - 2 && targetY >= 1 && targetY <= mapHeight - 2) {
							map[targetY][targetX] = 0;
						}
						// if OOB, nothing happens...
					}
				}

			}

		}
		return map;
	}

	public static void main(String[] args) {
		int[][] map = generateMap();
		printDungeonASCII(map);
	}
}
