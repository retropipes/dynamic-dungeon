package net.dynamicdungeon.screens;

//import java.awt.FileDialog;
//import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
//import java.io.File;
//import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.dynamicdungeon.Creature;
import net.dynamicdungeon.Effect;
import net.dynamicdungeon.FieldOfView;
import net.dynamicdungeon.Item;
import net.dynamicdungeon.StuffFactory;
import net.dynamicdungeon.constants.Constants;
//import net.dynamicdungeon.fileio.FilenameChecker;
//import net.dynamicdungeon.fileio.XMLFileWriter;
import net.dynamicdungeon.panels.GuiPanel;
import net.dynamicdungeon.panels.MessagePanel;
import net.dynamicdungeon.world.Tile;
import net.dynamicdungeon.world.World;
import net.dynamicdungeon.world.WorldBuilder;

public class PlayScreen implements Screen {
    private World world;
    private Creature player;
    private final int screenWidth;
    private final int screenHeight;
    private final List<String> messages;
    private final FieldOfView fov;
    private Screen subscreen;
    private boolean firstTimeFlag;

    public PlayScreen() {
	this.screenWidth = Constants.SCREEN_WIDTH_IN_TILES;
	this.screenHeight = Constants.SCREEN_HEIGHT_IN_TILES;
	this.messages = new ArrayList<>();
	this.createWorld();
	this.fov = new FieldOfView(this.world);
	final var factory = new StuffFactory(this.world);
	Effect.setStuffFactory(factory);
	this.createCreatures(factory);
	this.createItems(factory);
	this.firstTimeFlag = false;
    }

    public PlayScreen(final boolean first, final World saved) {
	this.screenWidth = Constants.SCREEN_WIDTH_IN_TILES;
	this.screenHeight = Constants.SCREEN_HEIGHT_IN_TILES;
	this.messages = new ArrayList<>();
	if (saved != null) {
	    this.world = saved;
	} else {
	    this.createWorld();
	}
	this.fov = new FieldOfView(this.world);
	final var factory = new StuffFactory(this.world);
	Effect.setStuffFactory(factory);
	this.createCreatures(factory);
	this.createItems(factory);
	this.firstTimeFlag = first;
    }

    private void createCreatures(final StuffFactory factory) {
	this.player = factory.newPlayer(this.messages, this.fov);
	for (var z = 0; z < this.world.depth(); z++) {
	    for (var i = 0; i < 4; i++) {
		factory.newFungus(z);
	    }
	    for (var i = 0; i < 10; i++) {
		factory.newBat(z);
	    }
	    for (var i = 0; i < z * 2 + 1; i++) {
		factory.newZombie(z, this.player);
		factory.newGoblin(z, this.player);
	    }
	}
    }

    private void createItems(final StuffFactory factory) {
	for (var z = 0; z < this.world.depth(); z++) {
	    for (var i = 0; i < this.world.width() * this.world.height() / 50; i++) {
		factory.newRock(z);
	    }
	    factory.newFruit(z);
	    factory.newEdibleWeapon(z);
	    factory.newBread(z);
	    factory.randomArmor(z);
	    factory.randomWeapon(z);
	    factory.randomWeapon(z);
	    for (var i = 0; i < z + 1; i++) {
		factory.randomPotion(z);
		factory.randomSpellBook(z);
	    }
	}
	factory.newVictoryItem(this.world.depth() - 1);
    }

    private void createWorld() {
	this.world = new WorldBuilder(Constants.WORLD_WIDTH_IN_TILES, Constants.WORLD_HEIGHT_IN_TILES,
		Constants.WORLD_DEPTH_IN_TILES).makeCaves().build();
    }

    @Override
    public void displayOutput(final GuiPanel terminal, final MessagePanel theMessages) {
	if (this.firstTimeFlag) {
	    theMessages.clear();
	    this.firstTimeFlag = false;
	}
	final var left = this.getScrollX();
	final var top = this.getScrollY();
	this.displayTiles(terminal, left, top);
	for (final String element : this.messages) {
	    theMessages.write(element);
	}
	this.messages.clear();
	final var stats = String.format(" Health: %3d/%3d   Mana: %d/%d   Food: %d/%d %s   (Press [/] or [?] for help)",
		this.player.hp(), this.player.maxHp(), this.player.mana(), this.player.maxMana(), this.player.food(),
		this.player.maxFood(), this.hunger());
	theMessages.setStats(stats);
	if (this.subscreen != null) {
	    this.subscreen.displayOutput(terminal, theMessages);
	}
    }

    private void displayTiles(final GuiPanel terminal, final int left, final int top) {
	this.fov.update(this.player.x, this.player.y, this.player.z, this.player.visionRadius());
	for (var x = 0; x < this.screenWidth; x++) {
	    for (var y = 0; y < this.screenHeight; y++) {
		final var wx = x + left;
		final var wy = y + top;
		if (this.player.canSee(wx, wy, this.player.z)) {
		    terminal.write(this.world.tile(wx, wy, this.player.z), x, y, 0);
		    final var i = this.world.item(wx, wy, this.player.z);
		    if (i != null) {
			terminal.write(i.tile(), x, y, 1);
		    } else {
			terminal.write(null, x, y, 1);
		    }
		    final var c = this.world.creature(wx, wy, this.player.z);
		    if (c != null) {
			terminal.write(this.world.creature(wx, wy, this.player.z).tile(), x, y, 2);
		    } else {
			terminal.write(null, x, y, 2);
		    }
		} else {
		    terminal.write(this.fov.tile(wx, wy, this.player.z), x, y, 0);
		    terminal.write(null, x, y, 1);
		    terminal.write(null, x, y, 2);
		}
	    }
	}
    }

    public int getScrollX() {
	return Math.max(0, Math.min(this.player.x - this.screenWidth / 2, this.world.width() - this.screenWidth));
    }

    public int getScrollY() {
	return Math.max(0, Math.min(this.player.y - this.screenHeight / 2, this.world.height() - this.screenHeight));
    }

    private String hunger() {
	if (this.player.food() < this.player.maxFood() * 0.10) {
	    return "(Starving)";
	}
	if (this.player.food() < this.player.maxFood() * 0.25) {
	    return "(Hungry)";
	}
	if (this.player.food() > this.player.maxFood() * 0.90) {
	    return "(Stuffed)";
	}
	if (this.player.food() > this.player.maxFood() * 0.75) {
	    return "(Full)";
	}
	return "(Normal)";
    }

    @Override
    public Screen respondToUserInput(final KeyEvent key, final MouseEvent mouse) {
	final var level = this.player.level();
	if (this.subscreen != null) {
	    this.subscreen = this.subscreen.respondToUserInput(key, mouse);
	} else if (key != null) {
	    switch (key.getKeyCode()) {
	    case KeyEvent.VK_LEFT:
	    case KeyEvent.VK_H:
	    case KeyEvent.VK_NUMPAD4:
		this.player.moveBy(-1, 0, 0);
		break;
	    case KeyEvent.VK_RIGHT:
	    case KeyEvent.VK_L:
	    case KeyEvent.VK_NUMPAD6:
		this.player.moveBy(1, 0, 0);
		break;
	    case KeyEvent.VK_UP:
	    case KeyEvent.VK_K:
	    case KeyEvent.VK_NUMPAD8:
		this.player.moveBy(0, -1, 0);
		break;
	    case KeyEvent.VK_DOWN:
	    case KeyEvent.VK_J:
	    case KeyEvent.VK_NUMPAD2:
		this.player.moveBy(0, 1, 0);
		break;
	    case KeyEvent.VK_Y:
	    case KeyEvent.VK_NUMPAD7:
		this.player.moveBy(-1, -1, 0);
		break;
	    case KeyEvent.VK_U:
	    case KeyEvent.VK_NUMPAD9:
		this.player.moveBy(1, -1, 0);
		break;
	    case KeyEvent.VK_B:
	    case KeyEvent.VK_NUMPAD1:
		this.player.moveBy(-1, 1, 0);
		break;
	    case KeyEvent.VK_N:
	    case KeyEvent.VK_NUMPAD3:
		this.player.moveBy(1, 1, 0);
		break;
	    case KeyEvent.VK_D:
		this.subscreen = new DropScreen(this.player);
		break;
	    case KeyEvent.VK_E:
		this.subscreen = new EatScreen(this.player);
		break;
	    case KeyEvent.VK_W:
		this.subscreen = new EquipScreen(this.player);
		break;
	    case KeyEvent.VK_X:
		this.subscreen = new ExamineScreen(this.player);
		break;
	    case KeyEvent.VK_SEMICOLON:
		this.subscreen = new LookScreen(this.player, "Looking", this.player.x - this.getScrollX(),
			this.player.y - this.getScrollY());
		break;
	    case KeyEvent.VK_T:
		this.subscreen = new ThrowScreen(this.player, this.player.x - this.getScrollX(),
			this.player.y - this.getScrollY());
		break;
	    case KeyEvent.VK_F:
		if (this.player.weapon() == null || this.player.weapon().rangedAttackValue() == 0) {
		    this.player.notify("You don't have a ranged weapon equiped.");
		} else {
		    this.subscreen = new FireWeaponScreen(this.player, this.player.x - this.getScrollX(),
			    this.player.y - this.getScrollY());
		}
		break;
	    case KeyEvent.VK_Q:
		this.subscreen = new QuaffScreen(this.player);
		break;
	    case KeyEvent.VK_R:
		this.subscreen = new ReadScreen(this.player, this.player.x - this.getScrollX(),
			this.player.y - this.getScrollY());
		break;
//		case KeyEvent.VK_S:
//		    final FileDialog fd = new FileDialog((Frame) null, "Save Game", FileDialog.SAVE);
//		    String filename, extension, file, dir;
//		    fd.setVisible(true);
//		    file = fd.getFile();
//		    dir = fd.getDirectory();
//		    if (file != null && dir != null) {
//			filename = dir + file;
//			extension = PlayScreen.getExtension(filename);
//			if (extension == null || !extension.equals(Constants.SAVE_FILE_EXTENSION)) {
//			    file += Constants.SAVE_FILE_EXTENSION_PERIOD;
//			    filename = dir + file;
//			}
//			if (FilenameChecker.isFilenameOK(
//				PlayScreen.getNameWithoutExtension(PlayScreen.getFileNameOnly(filename)))) {
//			    try {
//				XMLFileWriter writer = new XMLFileWriter(filename, Constants.SAVE_FILE_DOC_TAG);
//				PlayScreen.this.world.saveWorld(writer);
//			    } catch (IOException ioe) {
//				// Failed
//			    }
//			}
//		    }
//		    break;
	    default:
		break;
	    }
	    switch (key.getKeyChar()) {
	    case 'g':
	    case ',':
		this.player.pickup();
		break;
	    case '[':
	    case '<':
		if (this.userIsTryingToExit()) {
		    return this.userExits();
		}
		this.player.moveBy(0, 0, -1);
		break;
	    case ']':
	    case '>':
		this.player.moveBy(0, 0, 1);
		break;
	    case '/':
	    case '?':
		this.subscreen = new HelpScreen();
		break;
	    default:
		break;
	    }
	}
	if (this.player.level() > level) {
	    this.subscreen = new LevelUpScreen(this.player, this.player.level() - level);
	}
	if (this.subscreen == null) {
	    this.world.update();
	}
	if (this.player.hp() < 1) {
	    return new LoseScreen(this.player);
	}
	return this;
    }

    private Screen userExits() {
	for (final Item item : this.player.inventory().getItems()) {
	    if (item != null && "amulet".equals(item.name())) {
		return new WinScreen();
	    }
	}
	this.player.modifyHp(0, "Died while cowardly fleeing the caves.");
	return new LoseScreen(this.player);
    }
//    private static String getExtension(final String s) {
//	String ext = null;
//	final int i = s.lastIndexOf('.');
//	if ((i > 0) && (i < s.length() - 1)) {
//	    ext = s.substring(i + 1).toLowerCase();
//	}
//	return ext;
//    }
//
//    private static String getNameWithoutExtension(final String s) {
//	String ext = null;
//	final int i = s.lastIndexOf('.');
//	if ((i > 0) && (i < s.length() - 1)) {
//	    ext = s.substring(0, i);
//	} else {
//	    ext = s;
//	}
//	return ext;
//    }
//
//    private static String getFileNameOnly(final String s) {
//	String fno = null;
//	final int i = s.lastIndexOf(File.separatorChar);
//	if ((i > 0) && (i < s.length() - 1)) {
//	    fno = s.substring(i + 1);
//	} else {
//	    fno = s;
//	}
//	return fno;
//    }

    private boolean userIsTryingToExit() {
	return this.player.z == 0 && this.world.tile(this.player.x, this.player.y, this.player.z) == Tile.STAIRS_UP;
    }
}
