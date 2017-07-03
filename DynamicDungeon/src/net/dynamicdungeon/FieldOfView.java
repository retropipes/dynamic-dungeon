package net.dynamicdungeon;

public class FieldOfView {
    private final World world;
    private int depth;
    private boolean[][] visible;

    public boolean isVisible(final int x, final int y, final int z) {
	return z == this.depth && x >= 0 && y >= 0 && x < this.visible.length && y < this.visible[0].length
		&& this.visible[x][y];
    }

    private final Tile[][][] tiles;

    public Tile tile(final int x, final int y, final int z) {
	return this.tiles[x][y][z];
    }

    public FieldOfView(final World world) {
	this.world = world;
	this.visible = new boolean[world.width()][world.height()];
	this.tiles = new Tile[world.width()][world.height()][world.depth()];
	for (int x = 0; x < world.width(); x++) {
	    for (int y = 0; y < world.height(); y++) {
		for (int z = 0; z < world.depth(); z++) {
		    this.tiles[x][y][z] = Tile.DARKNESS;
		}
	    }
	}
    }

    public void update(final int wx, final int wy, final int wz, final int r) {
	this.depth = wz;
	this.visible = new boolean[this.world.width()][this.world.height()];
	for (int x = -r; x < r; x++) {
	    for (int y = -r; y < r; y++) {
		if (x * x + y * y > r * r) {
		    continue;
		}
		if (wx + x < 0 || wx + x >= this.world.width() || wy + y < 0 || wy + y >= this.world.height()) {
		    continue;
		}
		for (final Point p : new Line(wx, wy, wx + x, wy + y)) {
		    final Tile tile = this.world.tile(p.x, p.y, wz);
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
