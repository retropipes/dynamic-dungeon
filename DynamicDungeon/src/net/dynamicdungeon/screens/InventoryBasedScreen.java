package net.dynamicdungeon.screens;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import net.dynamicdungeon.Creature;
import net.dynamicdungeon.Item;
import net.dynamicdungeon.panels.GuiPanel;
import net.dynamicdungeon.panels.MessagePanel;

public abstract class InventoryBasedScreen implements Screen {
    protected Creature player;
    private final String letters;

    protected abstract String getVerb();

    protected abstract boolean isAcceptable(Item item);

    protected abstract Screen use(Item item);

    public InventoryBasedScreen(final Creature player) {
	this.player = player;
	this.letters = "abcdefghijklmnopqrstuvwxyz";
    }

    @Override
    public void displayOutput(final GuiPanel terminal, MessagePanel messages) {
	final ArrayList<String> lines = this.getList();
	for (final String line : lines) {
	    messages.write(line);
	}
	messages.write("What would you like to " + this.getVerb() + "?");
    }

    private ArrayList<String> getList() {
	final ArrayList<String> lines = new ArrayList<>();
	final Item[] inventory = this.player.inventory().getItems();
	for (int i = 0; i < inventory.length; i++) {
	    final Item item = inventory[i];
	    if (item == null || !this.isAcceptable(item)) {
		continue;
	    }
	    String line = this.letters.charAt(i) + " " + this.player.nameOf(item);
	    if (item == this.player.weapon() || item == this.player.armor()) {
		line += " (equipped)";
	    }
	    lines.add(line);
	}
	return lines;
    }

    @Override
    public Screen respondToUserInput(final KeyEvent key) {
	final char c = key.getKeyChar();
	final Item[] items = this.player.inventory().getItems();
	if (this.letters.indexOf(c) > -1 && items.length > this.letters.indexOf(c)
		&& items[this.letters.indexOf(c)] != null && this.isAcceptable(items[this.letters.indexOf(c)])) {
	    return this.use(items[this.letters.indexOf(c)]);
	} else if (key.getKeyCode() == KeyEvent.VK_ESCAPE) {
	    return null;
	} else {
	    return this;
	}
    }
}
