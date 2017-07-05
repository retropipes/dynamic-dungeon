package net.dynamicdungeon.world;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Tile extends BufferedImage {
    public static final Tile FLOOR = new Tile("/assets/images/terrain/floor.png", "A dirt and rock cave floor.");
    public static final Tile WALL = new Tile("/assets/images/terrain/wall.png", "A dirt and rock cave wall.");
    public static final Tile BOUNDS = new Tile("/assets/images/terrain/bounds.png", "Beyond the edge of the world.");
    public static final Tile DARKNESS = new Tile("/assets/images/terrain/darkness.png", "Not visible right now.");
    public static final Tile STAIRS_DOWN = new Tile("/assets/images/terrain/down.png",
	    "A stone staircase that goes down.");
    public static final Tile STAIRS_UP = new Tile("/assets/images/terrain/up.png",
	    "A stone staircase that goes up.");
    public static final Tile FUNGUS = new Tile("/assets/images/monsters/fungus.png", "A fungus.");
    public static final Tile BAT = new Tile("/assets/images/monsters/bat.png", "A bat.");
    public static final Tile ZOMBIE = new Tile("/assets/images/monsters/zombie.png", "A zombie.");
    public static final Tile GOBLIN = new Tile("/assets/images/monsters/goblin.png", "A goblin.");
    public static final Tile UNKNOWN = new Tile("(unknown)");
    public static final Tile CORPSE = new Tile("/assets/images/items/corpse.png", "It's dead.");
    public static final Tile ROCK = new Tile("/assets/images/items/rock.png", "A rock.");
    public static final Tile VICTORY = new Tile("/assets/images/items/victory.png", "The long-lost amulet!");
    public static final Tile DAGGER = new Tile("/assets/images/items/dagger.png", "A dagger of some sort!");
    public static final Tile SWORD = new Tile("/assets/images/items/sword.png", "A sword of some sort!");
    public static final Tile STAFF = new Tile("/assets/images/items/staff.png", "A staff of some sort!");
    public static final Tile BOW = new Tile("/assets/images/items/bow.png", "A bow of some sort!");
    public static final Tile MAGIC_WEAPON = new Tile("/assets/images/items/staff.png", "An enchanted weapon of some sort!");
    public static final Tile LIGHT_ARMOR = new Tile("/assets/images/items/lightarmor.png", "Some light armor of some sort!");
    public static final Tile ARMOR = new Tile("/assets/images/items/armor.png", "Some armor of some sort!");
    public static final Tile HEAVY_ARMOR = new Tile("/assets/images/items/heavyarmor.png", "Some heavy armor of some sort!");
    public static final Tile SPELL = new Tile("/assets/images/items/spell.png", "A spell book of some sort!");
    public static final Tile POTION = new Tile("/assets/images/items/potion.png", "A potion of some sort!");
    public static final Tile APPLE = new Tile("/assets/images/items/apple.png", "An apple!");
    public static final Tile BREAD = new Tile("/assets/images/items/bread.png", "Some bread!");
    public static Tile PLAYER = new Tile("/assets/images/characters/player.png", "This is you!");
    private String description;

    Tile(final String description) {
	super(32, 32, BufferedImage.TYPE_INT_ARGB);
	this.description = description;
    }

    Tile(final String assetPath, final String description) {
	super(32, 32, BufferedImage.TYPE_INT_ARGB);
	this.description = description;
	try {
	    BufferedImage data = ImageIO.read(Tile.class.getResource(assetPath));
	    this.getGraphics().drawImage(data, 0, 0, null);
	} catch (IOException ioe) {
	    System.err.println("Image loading failed for: " + assetPath);
	}
    }

    public String details() {
	return this.description;
    }

    public boolean isGround() {
	return this != WALL && this != BOUNDS;
    }

    public boolean isDiggable() {
	return this == Tile.WALL;
    }
}
