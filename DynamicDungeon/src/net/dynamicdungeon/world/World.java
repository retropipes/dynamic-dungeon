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
    private final List<Creature> creatures;

    public World() {
	// Create an empty World, to be populated later
	this.creatures = new ArrayList<>();
    }

    public World(final Tile[][][] theTiles) {
	this.tiles = theTiles;
	this.width = theTiles.length;
	this.height = theTiles[0].length;
	this.depth = theTiles[0][0].length;
	this.creatures = new ArrayList<>();
	this.items = new Item[this.width][this.height][this.depth];
    }

    public void add(final Creature pet) {
	this.creatures.add(pet);
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

    public void addAtEmptyLocation(final Item item, final int theDepth) {
	int x;
	int y;
	do {
	    x = (int) (Math.random() * this.width);
	    y = (int) (Math.random() * this.height);
	} while (!this.tile(x, y, theDepth).isGround() || this.item(x, y, theDepth) != null);
	this.items[x][y][theDepth] = item;
    }

    public boolean addAtEmptySpace(final Item item, final int x, final int y, final int z) {
	if (item == null) {
	    return true;
	}
	final List<Point> points = new ArrayList<>();
	final List<Point> checked = new ArrayList<>();
	points.add(new Point(x, y, z));
	while (!points.isEmpty()) {
	    final var p = points.remove(0);
	    checked.add(p);
	    if (!this.tile(p.x, p.y, p.z).isGround()) {
		continue;
	    }
	    if (this.items[p.x][p.y][p.z] == null) {
		this.items[p.x][p.y][p.z] = item;
		final var c = this.creature(p.x, p.y, p.z);
		if (c != null) {
		    c.notify("A %s lands between your feet.", c.nameOf(item));
		}
		return true;
	    }
	    final var neighbors = p.neighbors8();
	    neighbors.removeAll(checked);
	    points.addAll(neighbors);
	}
	return false;
    }

    public Creature creature(final int x, final int y, final int z) {
	for (final Creature c : this.creatures) {
	    if (c.x == x && c.y == y && c.z == z) {
		return c;
	    }
	}
	return null;
    }

    public int depth() {
	return this.depth;
    }

    public void dig(final int x, final int y, final int z) {
	if (this.tile(x, y, z).isDiggable()) {
	    this.tiles[x][y][z] = Tile.FLOOR;
	}
    }

    public int height() {
	return this.height;
    }

    public Item item(final int x, final int y, final int z) {
	return this.items[x][y][z];
    }

    private void loadCreatures(final XMLFileReader reader) throws IOException {
	reader.readOpeningGroup("creatures");
	final var cSize = reader.readCustomInt("size");
	for (var c = 0; c < cSize; c++) {
	    final var cr = new Creature(this);
	    cr.loadCreature(reader);
	    this.creatures.add(cr);
	}
	reader.readClosingGroup("creatures");
    }

    private void loadItems(final XMLFileReader reader) throws IOException {
	reader.readOpeningGroup("items");
	this.items = new Item[this.width][this.height][this.depth];
	for (var z = 0; z < this.depth; z++) {
	    for (var y = 0; y < this.height; y++) {
		for (var x = 0; x < this.width; x++) {
		    final var exists = reader.readCustomBoolean("exists");
		    if (exists) {
			final var i = new Item();
			i.loadItem(reader);
			this.items[x][y][z] = i;
		    }
		}
	    }
	}
	reader.readClosingGroup("items");
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
	for (var z = 0; z < this.depth; z++) {
	    for (var y = 0; y < this.height; y++) {
		final var row = reader.readCustomString("row");
		for (var x = 0; x < this.width; x++) {
		    this.tiles[x][y][z] = Tile.getFromSymbol(row.charAt(x));
		}
	    }
	}
	reader.readClosingGroup("rows");
	reader.readClosingGroup("tiles");
    }

    public void loadWorld(final XMLFileReader reader) throws IOException {
	reader.readOpeningGroup("world");
	this.loadTiles(reader);
	this.loadItems(reader);
	this.loadCreatures(reader);
	reader.readClosingGroup("world");
    }

    public void remove(final Creature other) {
	this.creatures.remove(other);
    }

    public void remove(final int x, final int y, final int z) {
	this.items[x][y][z] = null;
    }

    public void remove(final Item item) {
	for (var x = 0; x < this.width; x++) {
	    for (var y = 0; y < this.height; y++) {
		for (var z = 0; z < this.depth; z++) {
		    if (this.items[x][y][z] == item) {
			this.items[x][y][z] = null;
			return;
		    }
		}
	    }
	}
    }

    private void saveCreatures(final XMLFileWriter writer) throws IOException {
	writer.writeOpeningGroup("creatures");
	final var cSize = this.creatures.size();
	writer.writeCustomInt(cSize, "size");
	for (var c = 0; c < cSize; c++) {
	    final var cr = this.creatures.get(c);
	    cr.saveCreature(writer);
	}
	writer.writeClosingGroup("rows");
	writer.writeClosingGroup("creatures");
    }

    private void saveItems(final XMLFileWriter writer) throws IOException {
	writer.writeOpeningGroup("items");
	for (var z = 0; z < this.depth; z++) {
	    for (var y = 0; y < this.height; y++) {
		for (var x = 0; x < this.width; x++) {
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

    private void saveTiles(final XMLFileWriter writer) throws IOException {
	writer.writeOpeningGroup("tiles");
	writer.writeOpeningGroup("size");
	writer.writeCustomInt(this.width, "width");
	writer.writeCustomInt(this.height, "height");
	writer.writeCustomInt(this.depth, "depth");
	writer.writeClosingGroup("size");
	writer.writeOpeningGroup("rows");
	for (var z = 0; z < this.depth; z++) {
	    for (var y = 0; y < this.height; y++) {
		final var row = new StringBuilder();
		for (var x = 0; x < this.width; x++) {
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

    public void saveWorld(final XMLFileWriter writer) throws IOException {
	writer.writeOpeningGroup("world");
	this.saveTiles(writer);
	this.saveItems(writer);
	this.saveCreatures(writer);
	writer.writeClosingGroup("world");
    }

    public Tile tile(final int x, final int y, final int z) {
	if (x < 0 || x >= this.width || y < 0 || y >= this.height || z < 0 || z >= this.depth) {
	    return Tile.BOUNDS;
	}
	return this.tiles[x][y][z];
    }

    public void update() {
	final List<Creature> toUpdate = new ArrayList<>(this.creatures);
	for (final Creature creature : toUpdate) {
	    creature.update();
	}
    }

    public int width() {
	return this.width;
    }
}
