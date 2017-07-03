package net.dynamicdungeon;

import java.util.ArrayList;
import java.util.List;

public class World {
    private final Tile[][][] tiles;
    private final Item[][][] items;
    private final int width;

    public int width() {
	return this.width;
    }

    private final int height;

    public int height() {
	return this.height;
    }

    private final int depth;

    public int depth() {
	return this.depth;
    }

    private final List<Creature> creatures;

    public World(final Tile[][][] tiles) {
	this.tiles = tiles;
	this.width = tiles.length;
	this.height = tiles[0].length;
	this.depth = tiles[0][0].length;
	this.creatures = new ArrayList<>();
	this.items = new Item[this.width][this.height][this.depth];
    }

    public Creature creature(final int x, final int y, final int z) {
	for (final Creature c : this.creatures) {
	    if (c.x == x && c.y == y && c.z == z) {
		return c;
	    }
	}
	return null;
    }

    public Tile tile(final int x, final int y, final int z) {
	if (x < 0 || x >= this.width || y < 0 || y >= this.height || z < 0 || z >= this.depth) {
	    return Tile.BOUNDS;
	} else {
	    return this.tiles[x][y][z];
	}
    }

    public void dig(final int x, final int y, final int z) {
	if (this.tile(x, y, z).isDiggable()) {
	    this.tiles[x][y][z] = Tile.FLOOR;
	}
    }

    public void addAtEmptyLocation(final Creature creature, final int z) {
	int x;
	int y;
	do {
	    x = (int) (Math.random() * this.width);
	    y = (int) (Math.random() * this.height);
	} while (!this.tile(x, y, z).isGround() || this.creature(x, y, z) != null);
	creature.x = x;
	creature.y = y;
	creature.z = z;
	this.creatures.add(creature);
    }

    public void update() {
	final List<Creature> toUpdate = new ArrayList<>(this.creatures);
	for (final Creature creature : toUpdate) {
	    creature.update();
	}
    }

    public void remove(final Creature other) {
	this.creatures.remove(other);
    }

    public void remove(final Item item) {
	for (int x = 0; x < this.width; x++) {
	    for (int y = 0; y < this.height; y++) {
		for (int z = 0; z < this.depth; z++) {
		    if (this.items[x][y][z] == item) {
			this.items[x][y][z] = null;
			return;
		    }
		}
	    }
	}
    }

    public Item item(final int x, final int y, final int z) {
	return this.items[x][y][z];
    }

    public void addAtEmptyLocation(final Item item, final int depth) {
	int x;
	int y;
	do {
	    x = (int) (Math.random() * this.width);
	    y = (int) (Math.random() * this.height);
	} while (!this.tile(x, y, depth).isGround() || this.item(x, y, depth) != null);
	this.items[x][y][depth] = item;
    }

    public void remove(final int x, final int y, final int z) {
	this.items[x][y][z] = null;
    }

    public boolean addAtEmptySpace(final Item item, final int x, final int y, final int z) {
	if (item == null) {
	    return true;
	}
	final List<Point> points = new ArrayList<>();
	final List<Point> checked = new ArrayList<>();
	points.add(new Point(x, y, z));
	while (!points.isEmpty()) {
	    final Point p = points.remove(0);
	    checked.add(p);
	    if (!this.tile(p.x, p.y, p.z).isGround()) {
		continue;
	    }
	    if (this.items[p.x][p.y][p.z] == null) {
		this.items[p.x][p.y][p.z] = item;
		final Creature c = this.creature(p.x, p.y, p.z);
		if (c != null) {
		    c.notify("A %s lands between your feet.", c.nameOf(item));
		}
		return true;
	    } else {
		final List<Point> neighbors = p.neighbors8();
		neighbors.removeAll(checked);
		points.addAll(neighbors);
	    }
	}
	return false;
    }

    public void add(final Creature pet) {
	this.creatures.add(pet);
    }
}
