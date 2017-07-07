package net.dynamicdungeon.world;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import net.dynamicdungeon.constants.Constants;

public class Tile extends BufferedImage {
    public static final Tile FLOOR = new Tile("/assets/images/terrain/floor.png", "A dirt and rock cave floor.", 'F');
    public static final Tile WALL = new Tile("/assets/images/terrain/wall.png", "A dirt and rock cave wall.", 'X');
    public static final Tile BOUNDS = new Tile("/assets/images/terrain/bounds.png", "Beyond the edge of the world.",
	    '\u25a9');
    public static final Tile DARKNESS = new Tile("/assets/images/terrain/darkness.png", "Not visible right now.",
	    '\u2588');
    public static final Tile STAIRS_DOWN = new Tile("/assets/images/terrain/down.png",
	    "A stone staircase that goes down.", '\u2193');
    public static final Tile STAIRS_UP = new Tile("/assets/images/terrain/up.png", "A stone staircase that goes up.",
	    '\u2191');
    public static final Tile FUNGUS = new Tile("/assets/images/monsters/fungus.png", "A fungus.", 'f');
    public static final Tile BAT = new Tile("/assets/images/monsters/bat.png", "A bat.", 'b');
    public static final Tile ZOMBIE = new Tile("/assets/images/monsters/zombie.png", "A zombie.", 'z');
    public static final Tile GOBLIN = new Tile("/assets/images/monsters/goblin.png", "A goblin.", 'g');
    public static final Tile UNKNOWN = new Tile("(unknown)");
    public static final Tile CORPSE = new Tile("/assets/images/items/corpse.png", "It's dead.", '\u24b8');
    public static final Tile ROCK = new Tile("/assets/images/items/rock.png", "A rock.", '\u26ab');
    public static final Tile VICTORY = new Tile("/assets/images/items/victory.png", "The long-lost amulet!", 'v');
    public static final Tile DAGGER = new Tile("/assets/images/items/dagger.png", "A dagger of some sort!", 'w');
    public static final Tile SWORD = new Tile("/assets/images/items/sword.png", "A sword of some sort!", 'W');
    public static final Tile STAFF = new Tile("/assets/images/items/staff.png", "A staff of some sort!", 's');
    public static final Tile BOW = new Tile("/assets/images/items/bow.png", "A bow of some sort!", 'B');
    public static final Tile MAGIC_WEAPON = new Tile("/assets/images/items/staff.png",
	    "An enchanted weapon of some sort!", 'e');
    public static final Tile LIGHT_ARMOR = new Tile("/assets/images/items/lightarmor.png",
	    "Some light armor of some sort!", 'a');
    public static final Tile ARMOR = new Tile("/assets/images/items/armor.png", "Some armor of some sort!", 'A');
    public static final Tile HEAVY_ARMOR = new Tile("/assets/images/items/heavyarmor.png",
	    "Some heavy armor of some sort!", 'H');
    public static final Tile SPELL = new Tile("/assets/images/items/spell.png", "A spell book of some sort!", 'S');
    public static final Tile POTION = new Tile("/assets/images/items/potion.png", "A potion of some sort!", 'p');
    public static final Tile APPLE = new Tile("/assets/images/items/apple.png", "An apple!", '1');
    public static final Tile BREAD = new Tile("/assets/images/items/bread.png", "Some bread!", '2');
    public static Tile PLAYER = new Tile("/assets/images/characters/player.png", "This is you!", 'P');
    private static Hashtable<Character, Tile> symbolMap;
    private final String description;
    private final char stateSymbol;

    Tile(final String description) {
	super(Constants.TILE_SIZE_IN_PIXELS, Constants.TILE_SIZE_IN_PIXELS, BufferedImage.TYPE_INT_ARGB);
	this.description = description;
	this.stateSymbol = ' ';
    }

    Tile(final String assetPath, final String description, final char symbol) {
	super(Constants.TILE_SIZE_IN_PIXELS, Constants.TILE_SIZE_IN_PIXELS, BufferedImage.TYPE_INT_ARGB);
	this.description = description;
	this.stateSymbol = symbol;
	try {
	    final BufferedImage data = ImageIO.read(Tile.class.getResource(assetPath));
	    this.getGraphics().drawImage(data, 0, 0, null);
	} catch (final IOException ioe) {
	    System.err.println("Image loading failed for: " + assetPath);
	}
    }

    public char getStateSymbol() {
	return this.stateSymbol;
    }

    public static Tile getFromSymbol(char symbol) {
	if (Tile.symbolMap == null) {
	    Tile.symbolMap = new Hashtable<>();
	    Tile.symbolMap.put('F', Tile.FLOOR);
	    Tile.symbolMap.put('X', Tile.WALL);
	    Tile.symbolMap.put('\u25a9', Tile.BOUNDS);
	    Tile.symbolMap.put('\u2588', Tile.DARKNESS);
	    Tile.symbolMap.put('\u2193', Tile.STAIRS_DOWN);
	    Tile.symbolMap.put('\u2191', Tile.STAIRS_UP);
	    Tile.symbolMap.put('f', Tile.FUNGUS);
	    Tile.symbolMap.put('b', Tile.BAT);
	    Tile.symbolMap.put('z', Tile.ZOMBIE);
	    Tile.symbolMap.put('g', Tile.GOBLIN);
	    Tile.symbolMap.put(' ', Tile.UNKNOWN);
	    Tile.symbolMap.put('\u24b8', Tile.CORPSE);
	    Tile.symbolMap.put('\u26ab', Tile.ROCK);
	    Tile.symbolMap.put('v', Tile.VICTORY);
	    Tile.symbolMap.put('w', Tile.DAGGER);
	    Tile.symbolMap.put('W', Tile.SWORD);
	    Tile.symbolMap.put('e', Tile.STAFF);
	    Tile.symbolMap.put('a', Tile.LIGHT_ARMOR);
	    Tile.symbolMap.put('A', Tile.ARMOR);
	    Tile.symbolMap.put('H', Tile.HEAVY_ARMOR);
	    Tile.symbolMap.put('S', Tile.SPELL);
	    Tile.symbolMap.put('p', Tile.POTION);
	    Tile.symbolMap.put('1', Tile.APPLE);
	    Tile.symbolMap.put('2', Tile.BREAD);
	    Tile.symbolMap.put('P', Tile.PLAYER);
	}
	return Tile.symbolMap.get(symbol);
    }

    public String details() {
	return this.description;
    }

    public boolean isGround() {
	return this != Tile.WALL && this != Tile.BOUNDS;
    }

    public boolean isDiggable() {
	return this == Tile.WALL;
    }
}
