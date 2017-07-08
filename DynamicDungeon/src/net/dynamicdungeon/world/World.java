package net.dynamicdungeon.world;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.dynamicdungeon.Creature;
import net.dynamicdungeon.Item;
import net.dynamicdungeon.Point;
import net.dynamicdungeon.fileio.XMLFileReader;
import net.dynamicdungeon.fileio.XMLFileWriter;

public class World {
    private Tile[][][] tiles;
    private Item[][][] items;
    private int width;
    private int height;
    private int depth;

    public int width() {
	return this.width;
    }

    public int height() {
	return this.height;
    }

    public int depth() {
	return this.depth;
    }

    private final List<Creature> creatures;

    public World() {
	// Create an empty World, to be populated later
	this.creatures = new ArrayList<>();
    }

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

    public void loadWorld(final XMLFileReader reader) throws IOException {
	reader.readOpeningGroup("world");
	this.loadTiles(reader);
	this.loadItems(reader);
	this.loadCreatures(reader);
	reader.readClosingGroup("world");
    }

    public void saveWorld(final XMLFileWriter writer) throws IOException {
	writer.writeOpeningGroup("world");
	this.saveTiles(writer);
	this.saveItems(writer);
	this.saveCreatures(writer);
	writer.writeClosingGroup("world");
    }

    private void loadTiles(final XMLFileReader reader) throws IOException {
	reader.readOpeningGroup("tiles");
	reader.readOpeningGroup("size");
	this.width = reader.readCustomInt("width");
	this.height = reader.readCustomInt("height");
	this.depth = reader.readCustomInt("depth");
	reader.readClosingGroup("size");
	reader.readOpeningGroup("rows");
	this.tiles = new Tile[this.width][this.height][this.depth];
	for (int z = 0; z < this.depth; z++) {
	    for (int y = 0; y < this.height; y++) {
		String row = reader.readCustomString("row");
		for (int x = 0; x < this.width; x++) {
		    this.tiles[x][y][z] = Tile.getFromSymbol(row.charAt(x));
		}
	    }
	}
	reader.readClosingGroup("rows");
	reader.readClosingGroup("tiles");
    }

    private void saveTiles(final XMLFileWriter writer) throws IOException {
	writer.writeOpeningGroup("tiles");
	writer.writeOpeningGroup("size");
	writer.writeCustomInt(this.width, "width");
	writer.writeCustomInt(this.height, "height");
	writer.writeCustomInt(this.depth, "depth");
	writer.writeClosingGroup("size");
	writer.writeOpeningGroup("rows");
	for (int z = 0; z < this.depth; z++) {
	    for (int y = 0; y < this.height; y++) {
		StringBuilder row = new StringBuilder();
		for (int x = 0; x < this.width; x++) {
		    if (this.tiles[x][y][z] == null) {
			row.append("0");
		    } else {
			row.append(Character.toString(this.tiles[x][y][z].getStateSymbol()));
		    }
		}
		writer.writeCustomString(row.toString(), "row");
	    }
	}
	writer.writeClosingGroup("rows");
	writer.writeClosingGroup("tiles");
    }

    private void loadItems(final XMLFileReader reader) throws IOException {
	reader.readOpeningGroup("items");
	this.items = new Item[this.width][this.height][this.depth];
	for (int z = 0; z < this.depth; z++) {
	    for (int y = 0; y < this.height; y++) {
		for (int x = 0; x < this.width; x++) {
		    boolean exists = reader.readCustomBoolean("exists");
		    if (exists) {
			Item i = new Item();
			i.loadItem(reader);
			this.items[x][y][z] = i;
		    }
		}
	    }
	}
	reader.readClosingGroup("items");
    }

    private void saveItems(final XMLFileWriter writer) throws IOException {
	writer.writeOpeningGroup("items");
	for (int z = 0; z < this.depth; z++) {
	    for (int y = 0; y < this.height; y++) {
		for (int x = 0; x < this.width; x++) {
		    if (this.items[x][y][z] == null) {
			writer.writeCustomBoolean(false, "exists");
		    } else {
			writer.writeCustomBoolean(true, "exists");
			this.items[x][y][z].saveItem(writer);
		    }
		}
	    }
	}
	writer.writeClosingGroup("items");
    }

    private void loadCreatures(final XMLFileReader reader) throws IOException {
	reader.readOpeningGroup("creatures");
	int cSize = reader.readCustomInt("size");
	for (int c = 0; c < cSize; c++) {
	    Creature cr = new Creature(this);
	    cr.loadCreature(reader);
	    this.creatures.add(cr);
	}
	reader.readClosingGroup("creatures");
    }

    private void saveCreatures(final XMLFileWriter writer) throws IOException {
	writer.writeOpeningGroup("creatures");
	int cSize = this.creatures.size();
	writer.writeCustomInt(cSize, "size");
	for (int c = 0; c < cSize; c++) {
	    Creature cr = this.creatures.get(c);
	    cr.saveCreature(writer);
	}
	writer.writeClosingGroup("rows");
	writer.writeClosingGroup("creatures");
    }
}
