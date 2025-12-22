package net.dynamicdungeon.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.dynamicdungeon.Point;
import net.dynamicdungeon.constants.Constants;

public class WorldBuilder {
    private final int width;
    private final int height;
    private final int depth;
    private Tile[][][] tiles;
    private int[][][] regions;
    private int nextRegion;

    public WorldBuilder(final int theWidth, final int theHeight, final int theDepth) {
	this.width = theWidth;
	this.height = theHeight;
	this.depth = theDepth;
	this.tiles = new Tile[theWidth][theHeight][theDepth];
	this.regions = new int[theWidth][theHeight][theDepth];
	this.nextRegion = 1;
    }

    private WorldBuilder addExitStairs() {
	var x = -1;
	var y = -1;
	do {
	    x = (int) (Math.random() * this.width);
	    y = (int) (Math.random() * this.height);
	} while (this.tiles[x][y][0] != Tile.FLOOR);
	this.tiles[x][y][0] = Tile.STAIRS_UP;
	return this;
    }

    public World build() {
	return new World(this.tiles);
    }

    public WorldBuilder connectRegions() {
	for (var z = 0; z < this.depth - 1; z++) {
	    this.connectRegionsDown(z);
	}
	return this;
    }

    private void connectRegionsDown(final int z) {
	final List<Integer> connected = new ArrayList<>();
	for (var x = 0; x < this.width; x++) {
	    for (var y = 0; y < this.height; y++) {
		final var r = this.regions[x][y][z] * 1000 + this.regions[x][y][z + 1];
		if (this.tiles[x][y][z] == Tile.FLOOR && this.tiles[x][y][z + 1] == Tile.FLOOR
			&& !connected.contains(r)) {
		    connected.add(r);
		    this.connectRegionsDown(z, this.regions[x][y][z], this.regions[x][y][z + 1]);
		}
	    }
	}
    }

    private void connectRegionsDown(final int z, final int r1, final int r2) {
	final var candidates = this.findRegionOverlaps(z, r1, r2);
	var stairs = 0;
	do {
	    final var p = candidates.remove(0);
	    this.tiles[p.x][p.y][z] = Tile.STAIRS_DOWN;
	    this.tiles[p.x][p.y][z + 1] = Tile.STAIRS_UP;
	    stairs++;
	} while (candidates.size() / stairs > Constants.WORLD_CONNECT_DOWN_MIN_SIZE);
    }

    private WorldBuilder createRegions() {
	this.regions = new int[this.width][this.height][this.depth];
	for (var z = 0; z < this.depth; z++) {
	    for (var x = 0; x < this.width; x++) {
		for (var y = 0; y < this.height; y++) {
		    if (this.tiles[x][y][z] != Tile.WALL && this.regions[x][y][z] == 0) {
			final var size = this.fillRegion(this.nextRegion++, x, y, z);
			if (size < Constants.WORLD_REGION_TRIM_SIZE) {
			    this.removeRegion(this.nextRegion - 1, z);
			}
		    }
		}
	    }
	}
	return this;
    }

    private int fillRegion(final int region, final int x, final int y, final int z) {
	var size = 1;
	final var open = new ArrayList<Point>();
	open.add(new Point(x, y, z));
	this.regions[x][y][z] = region;
	while (!open.isEmpty()) {
	    final var p = open.remove(0);
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

    public List<Point> findRegionOverlaps(final int z, final int r1, final int r2) {
	final var candidates = new ArrayList<Point>();
	for (var x = 0; x < this.width; x++) {
	    for (var y = 0; y < this.height; y++) {
		if (this.tiles[x][y][z] == Tile.FLOOR && this.tiles[x][y][z + 1] == Tile.FLOOR
			&& this.regions[x][y][z] == r1 && this.regions[x][y][z + 1] == r2) {
		    candidates.add(new Point(x, y, z));
		}
	    }
	}
	Collections.shuffle(candidates);
	return candidates;
    }

    public WorldBuilder makeCaves() {
	return this.randomizeTiles().smooth(Constants.WORLD_SMOOTHING_PASSES).createRegions().connectRegions()
		.addExitStairs();
    }

    private WorldBuilder randomizeTiles() {
	for (var x = 0; x < this.width; x++) {
	    for (var y = 0; y < this.height; y++) {
		for (var z = 0; z < this.depth; z++) {
		    this.tiles[x][y][z] = Math.random() < 0.5 ? Tile.FLOOR : Tile.WALL;
		}
	    }
	}
	return this;
    }

    private void removeRegion(final int region, final int z) {
	for (var x = 0; x < this.width; x++) {
	    for (var y = 0; y < this.height; y++) {
		if (this.regions[x][y][z] == region) {
		    this.regions[x][y][z] = 0;
		    this.tiles[x][y][z] = Tile.WALL;
		}
	    }
	}
    }

    private WorldBuilder smooth(final int times) {
	final var tiles2 = new Tile[this.width][this.height][this.depth];
	for (var time = 0; time < times; time++) {
	    for (var x = 0; x < this.width; x++) {
		for (var y = 0; y < this.height; y++) {
		    for (var z = 0; z < this.depth; z++) {
			var floors = 0;
			var rocks = 0;
			for (var ox = -1; ox < 2; ox++) {
			    for (var oy = -1; oy < 2; oy++) {
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
}