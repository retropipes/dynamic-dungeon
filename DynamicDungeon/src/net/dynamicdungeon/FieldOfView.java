package net.dynamicdungeon;

import net.dynamicdungeon.world.Tile;
import net.dynamicdungeon.world.World;

public class FieldOfView {
    private final World world;
    private int depth;
    private boolean[][] visible;
    private final Tile[][][] tiles;

    public FieldOfView(final World theWorld) {
	this.world = theWorld;
	this.visible = new boolean[theWorld.width()][theWorld.height()];
	this.tiles = new Tile[theWorld.width()][theWorld.height()][theWorld.depth()];
	for (var x = 0; x < theWorld.width(); x++) {
	    for (var y = 0; y < theWorld.height(); y++) {
		for (var z = 0; z < theWorld.depth(); z++) {
		    this.tiles[x][y][z] = Tile.DARKNESS;
		}
	    }
	}
    }

    public boolean isVisible(final int x, final int y, final int z) {
	return z == this.depth && x >= 0 && y >= 0 && x < this.visible.length && y < this.visible[0].length
		&& this.visible[x][y];
    }

    public Tile tile(final int x, final int y, final int z) {
	return this.tiles[x][y][z];
    }

    public void update(final int wx, final int wy, final int wz, final int r) {
	this.depth = wz;
	this.visible = new boolean[this.world.width()][this.world.height()];
	for (var x = -r; x < r; x++) {
	    for (var y = -r; y < r; y++) {
		if (x * x + y * y > r * r) {
		    continue;
		}
		if (wx + x < 0 || wx + x >= this.world.width() || wy + y < 0 || wy + y >= this.world.height()) {
		    continue;
		}
		for (final Point p : new Line(wx, wy, wx + x, wy + y)) {
		    final var tile = this.world.tile(p.x, p.y, wz);
		    this.visible[p.x][p.y] = true;
		    this.tiles[p.x][p.y][wz] = tile;
		    if (!tile.isGround()) {
			break;
		    }
		}
	    }
	}
    }
}
