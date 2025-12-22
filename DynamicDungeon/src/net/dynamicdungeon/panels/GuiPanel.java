package net.dynamicdungeon.panels;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Stack;

import javax.swing.JPanel;

import net.dynamicdungeon.constants.Constants;
import net.dynamicdungeon.world.Tile;

/**
 *
 * @author Eric Ahnell
 */
public class GuiPanel extends JPanel {
    private static class GuiPanelState {
	public int cursorX;
	public int cursorY;
	public Tile[][][] tiles;

	public GuiPanelState() {
	}

	public GuiPanelState(final GuiPanelState state) {
	    this.cursorX = state.cursorX;
	    this.cursorY = state.cursorY;
	    this.tiles = new Tile[state.tiles.length][state.tiles[0].length][state.tiles[0][0].length];
	    for (var x = 0; x < this.tiles.length; x++) {
		for (var y = 0; y < this.tiles[0].length; y++) {
		    for (var z = 0; z < this.tiles[0][0].length; z++) {
			this.tiles[x][y][z] = state.tiles[x][y][z];
		    }
		}
	    }
	}
    }

    private static final long serialVersionUID = 2L;
    private final int widthInTiles;
    private final int heightInTiles;
    private final int tileWidth = Constants.TILE_SIZE_IN_PIXELS;
    private final int tileHeight = Constants.TILE_SIZE_IN_PIXELS;
    private final int depthInTiles = Constants.SCREEN_DEPTH_IN_TILES;
    private GuiPanelState state;
    private final Stack<GuiPanelState> stateStack;

    /**
     * Class constructor. Default size is 30x18.
     */
    public GuiPanel() {
	this(Constants.SCREEN_WIDTH_IN_TILES, Constants.SCREEN_HEIGHT_IN_TILES);
    }

    public GuiPanel(final GuiPanel source) {
	this.widthInTiles = source.widthInTiles;
	this.heightInTiles = source.heightInTiles;
	this.setPreferredSize(source.getPreferredSize());
	this.state = new GuiPanelState(source.state);
	this.stateStack = new Stack<>();
	this.clear();
    }

    /**
     * Class constructor specifying the width and height in tiles.
     *
     * @param width
     * @param height
     */
    public GuiPanel(final int width, final int height) {
	if (width < 1) {
	    throw new IllegalArgumentException("width " + width + " must be greater than 0.");
	}
	if (height < 1) {
	    throw new IllegalArgumentException("height " + height + " must be greater than 0.");
	}
	this.widthInTiles = width;
	this.heightInTiles = height;
	this.setPreferredSize(new Dimension(this.tileWidth * this.widthInTiles, this.tileHeight * this.heightInTiles));
	this.state = new GuiPanelState();
	this.state.tiles = new Tile[this.widthInTiles][this.heightInTiles][this.depthInTiles];
	this.stateStack = new Stack<>();
	this.clear();
    }

    /**
     * Clear the entire screen to whatever the default tile is.
     *
     * @return this for convenient chaining of method calls
     */
    public GuiPanel clear() {
	this.clear(Tile.BOUNDS, 0, 0, 0, this.widthInTiles, this.heightInTiles);
	this.clear(null, 0, 0, 1, this.widthInTiles, this.heightInTiles);
	return this.clear(null, 0, 0, 2, this.widthInTiles, this.heightInTiles);
    }

    /**
     * Clear the entire screen with the specified tile.
     *
     * @param tile the tile to write
     * @return this for convenient chaining of method calls
     */
    public GuiPanel clear(final Tile tile, final int z) {
	return this.clear(tile, 0, 0, this.widthInTiles, this.heightInTiles, z);
    }

    /**
     * Clear the section of the screen with the specified tile.
     *
     * @param tile   the tile to write
     * @param x      the distance from the left to begin writing from
     * @param y      the distance from the top to begin writing from
     * @param width  the height of the section to clear
     * @param height the width of the section to clear
     * @return this for convenient chaining of method calls
     */
    public GuiPanel clear(final Tile tile, final int x, final int y, final int z, final int width, final int height) {
	if (x < 0 || x >= this.widthInTiles) {
	    throw new IllegalArgumentException("x " + x + " must be within range [0," + this.widthInTiles + ").");
	}
	if (y < 0 || y >= this.heightInTiles) {
	    throw new IllegalArgumentException("y " + y + " must be within range [0," + this.heightInTiles + ").");
	}
	if (width < 1) {
	    throw new IllegalArgumentException("width " + width + " must be greater than 0.");
	}
	if (height < 1) {
	    throw new IllegalArgumentException("height " + height + " must be greater than 0.");
	}
	if (x + width > this.widthInTiles) {
	    throw new IllegalArgumentException(
		    "x + width " + (x + width) + " must be less than " + (this.widthInTiles + 1) + ".");
	}
	if (y + height > this.heightInTiles) {
	    throw new IllegalArgumentException(
		    "y + height " + (y + height) + " must be less than " + (this.heightInTiles + 1) + ".");
	}
	for (var xo = x; xo < x + width; xo++) {
	    for (var yo = y; yo < y + height; yo++) {
		this.write(tile, xo, yo, z);
	    }
	}
	return this;
    }

    /**
     * Gets the distance from the left new tiles will be written to.
     *
     * @return
     */
    public int getCursorX() {
	return this.state.cursorX;
    }

    /**
     * Gets the distance from the top new tiles will be written to.
     *
     * @return
     */
    public int getCursorY() {
	return this.state.cursorY;
    }

    /**
     * Gets the height in tiles.
     *
     * @return
     */
    public int getHeightInTiles() {
	return this.heightInTiles;
    }

    /**
     * Gets the height, in pixels, of a tile.
     *
     * @return
     */
    public int getTileHeight() {
	return this.tileHeight;
    }

    /**
     * Gets the width, in pixels, of a tile.
     *
     * @return
     */
    public int getTileWidth() {
	return this.tileWidth;
    }

    /**
     * Gets the width in tiles.
     *
     * @return
     */
    public int getWidthInTiles() {
	return this.widthInTiles;
    }

    /**
     *
     * @param g
     */
    @Override
    public void paintComponent(final Graphics g) {
	for (var x = 0; x < this.widthInTiles; x++) {
	    for (var y = 0; y < this.heightInTiles; y++) {
		for (var z = 0; z < this.depthInTiles; z++) {
		    final BufferedImage img = this.state.tiles[x][y][z];
		    if (img != null) {
			g.drawImage(img, x * this.tileWidth, y * this.tileHeight, null);
		    }
		}
	    }
	}
    }

    public void popState() {
	if (this.stateStack.isEmpty()) {
	    throw new IllegalArgumentException("no state to pop. Try calling pushState() first.");
	}
	this.state = this.stateStack.pop();
    }

    public void pushState() {
	this.stateStack.push(new GuiPanelState(this.state));
    }

    /**
     * Sets the x and y position of where new tiles will be written to. The origin
     * (0,0) is the upper left corner. The x should be equal to or greater than 0
     * and less than the the width in tiles. The y should be equal to or greater
     * than 0 and less than the the height in tiles.
     *
     * @param x the distance from the left new tiles should be written to
     * @param y the distance from the top new tiles should be written to
     */
    public void setCursorPosition(final int x, final int y) {
	this.setCursorX(x);
	this.setCursorY(y);
    }

    /**
     * Sets the distance from the left new tiles will be written to. This should be
     * equal to or greater than 0 and less than the the width in tiles.
     *
     * @param cursorX the distance from the left new tiles should be written to
     */
    public void setCursorX(final int cursorX) {
	if (cursorX < 0 || cursorX >= this.widthInTiles) {
	    throw new IllegalArgumentException(
		    "cursorX " + cursorX + " must be within range [0," + this.widthInTiles + ").");
	}
	this.state.cursorX = cursorX;
    }

    /**
     * Sets the distance from the top new tiles will be written to. This should be
     * equal to or greater than 0 and less than the the height in tiles.
     *
     * @param cursorY the distance from the top new tiles should be written to
     */
    public void setCursorY(final int cursorY) {
	if (cursorY < 0 || cursorY >= this.heightInTiles) {
	    throw new IllegalArgumentException(
		    "cursorY " + cursorY + " must be within range [0," + this.heightInTiles + ").");
	}
	this.state.cursorY = cursorY;
    }

    /**
     * Write a tile to the cursor's position. This updates the cursor's position.
     *
     * @param tile the tile to write
     * @return this for convenient chaining of method calls
     */
    public GuiPanel write(final Tile tile, final int z) {
	return this.write(tile, this.state.cursorX, this.state.cursorY, z);
    }

    /**
     * Write a tile to the specified position. This updates the cursor's position.
     *
     * @param tile the tile to write
     * @param x    the distance from the left to begin writing from
     * @param y    the distance from the top to begin writing from
     * @return this for convenient chaining of method calls
     */
    public GuiPanel write(final Tile tile, final int x, final int y, final int z) {
	if (x < 0 || x >= this.widthInTiles) {
	    throw new IllegalArgumentException("x " + x + " must be within range [0," + this.widthInTiles + ")");
	}
	if (y < 0 || y >= this.heightInTiles) {
	    throw new IllegalArgumentException("y " + y + " must be within range [0," + this.heightInTiles + ")");
	}
	this.state.tiles[x][y][z] = tile;
	this.state.cursorX = x + 1;
	this.state.cursorY = y;
	return this;
    }
}