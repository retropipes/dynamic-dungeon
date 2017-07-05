package net.dynamicdungeon.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.dynamicdungeon.Point;

public class WorldBuilder {
    private final int width;
    private final int height;
    private final int depth;
    private Tile[][][] tiles;
    private int[][][] regions;
    private int nextRegion;

    public WorldBuilder(final int width, final int height, final int depth) {
	this.width = width;
	this.height = height;
	this.depth = depth;
	this.tiles = new Tile[width][height][depth];
	this.regions = new int[width][height][depth];
	this.nextRegion = 1;
    }

    public World build() {
	return new World(this.tiles);
    }

    private WorldBuilder randomizeTiles() {
	for (int x = 0; x < this.width; x++) {
	    for (int y = 0; y < this.height; y++) {
		for (int z = 0; z < this.depth; z++) {
		    this.tiles[x][y][z] = Math.random() < 0.5 ? Tile.FLOOR : Tile.WALL;
		}
	    }
	}
	return this;
    }

    private WorldBuilder smooth(final int times) {
	final Tile[][][] tiles2 = new Tile[this.width][this.height][this.depth];
	for (int time = 0; time < times; time++) {
	    for (int x = 0; x < this.width; x++) {
		for (int y = 0; y < this.height; y++) {
		    for (int z = 0; z < this.depth; z++) {
			int floors = 0;
			int rocks = 0;
			for (int ox = -1; ox < 2; ox++) {
			    for (int oy = -1; oy < 2; oy++) {
				if (x + ox < 0 || x + ox >= this.width || y + oy < 0 || y + oy >= this.height) {
				    continue;
				}
				if (this.tiles[x + ox][y + oy][z] == Tile.FLOOR) {
				    floors++;
				} else {
				    rocks++;
				}
			    }
			}
			tiles2[x][y][z] = floors >= rocks ? Tile.FLOOR : Tile.WALL;
		    }
		}
	    }
	    this.tiles = tiles2;
	}
	return this;
    }

    private WorldBuilder createRegions() {
	this.regions = new int[this.width][this.height][this.depth];
	for (int z = 0; z < this.depth; z++) {
	    for (int x = 0; x < this.width; x++) {
		for (int y = 0; y < this.height; y++) {
		    if (this.tiles[x][y][z] != Tile.WALL && this.regions[x][y][z] == 0) {
			final int size = this.fillRegion(this.nextRegion++, x, y, z);
			if (size < 25) {
			    this.removeRegion(this.nextRegion - 1, z);
			}
		    }
		}
	    }
	}
	return this;
    }

    private void removeRegion(final int region, final int z) {
	for (int x = 0; x < this.width; x++) {
	    for (int y = 0; y < this.height; y++) {
		if (this.regions[x][y][z] == region) {
		    this.regions[x][y][z] = 0;
		    this.tiles[x][y][z] = Tile.WALL;
		}
	    }
	}
    }

    private int fillRegion(final int region, final int x, final int y, final int z) {
	int size = 1;
	final ArrayList<Point> open = new ArrayList<>();
	open.add(new Point(x, y, z));
	this.regions[x][y][z] = region;
	while (!open.isEmpty()) {
	    final Point p = open.remove(0);
	    for (final Point neighbor : p.neighbors8()) {
		if (neighbor.x < 0 || neighbor.y < 0 || neighbor.x >= this.width || neighbor.y >= this.height) {
		    continue;
		}
		if (this.regions[neighbor.x][neighbor.y][neighbor.z] > 0
			|| this.tiles[neighbor.x][neighbor.y][neighbor.z] == Tile.WALL) {
		    continue;
		}
		size++;
		this.regions[neighbor.x][neighbor.y][neighbor.z] = region;
		open.add(neighbor);
	    }
	}
	return size;
    }

    public WorldBuilder connectRegions() {
	for (int z = 0; z < this.depth - 1; z++) {
	    this.connectRegionsDown(z);
	}
	return this;
    }

    private void connectRegionsDown(final int z) {
	final List<Integer> connected = new ArrayList<>();
	for (int x = 0; x < this.width; x++) {
	    for (int y = 0; y < this.height; y++) {
		final int r = this.regions[x][y][z] * 1000 + this.regions[x][y][z + 1];
		if (this.tiles[x][y][z] == Tile.FLOOR && this.tiles[x][y][z + 1] == Tile.FLOOR
			&& !connected.contains(r)) {
		    connected.add(r);
		    this.connectRegionsDown(z, this.regions[x][y][z], this.regions[x][y][z + 1]);
		}
	    }
	}
    }

    private void connectRegionsDown(final int z, final int r1, final int r2) {
	final List<Point> candidates = this.findRegionOverlaps(z, r1, r2);
	int stairs = 0;
	do {
	    final Point p = candidates.remove(0);
	    this.tiles[p.x][p.y][z] = Tile.STAIRS_DOWN;
	    this.tiles[p.x][p.y][z + 1] = Tile.STAIRS_UP;
	    stairs++;
	} while (candidates.size() / stairs > 250);
    }

    public List<Point> findRegionOverlaps(final int z, final int r1, final int r2) {
	final ArrayList<Point> candidates = new ArrayList<>();
	for (int x = 0; x < this.width; x++) {
	    for (int y = 0; y < this.height; y++) {
		if (this.tiles[x][y][z] == Tile.FLOOR && this.tiles[x][y][z + 1] == Tile.FLOOR
			&& this.regions[x][y][z] == r1 && this.regions[x][y][z + 1] == r2) {
		    candidates.add(new Point(x, y, z));
		}
	    }
	}
	Collections.shuffle(candidates);
	return candidates;
    }

    private WorldBuilder addExitStairs() {
	int x = -1;
	int y = -1;
	do {
	    x = (int) (Math.random() * this.width);
	    y = (int) (Math.random() * this.height);
	} while (this.tiles[x][y][0] != Tile.FLOOR);
	this.tiles[x][y][0] = Tile.STAIRS_UP;
	return this;
    }

    public WorldBuilder makeCaves() {
	return this.randomizeTiles().smooth(8).createRegions().connectRegions().addExitStairs();
    }
}