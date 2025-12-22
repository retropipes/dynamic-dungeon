package net.dynamicdungeon.screens;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import net.dynamicdungeon.Creature;
import net.dynamicdungeon.Item;
import net.dynamicdungeon.panels.GuiPanel;
import net.dynamicdungeon.panels.MessagePanel;

public abstract class InventoryBasedScreen implements Screen {
    protected Creature player;
    private final String letters;

    public InventoryBasedScreen(final Creature thePlayer) {
	this.player = thePlayer;
	this.letters = "abcdefghijklmnopqrstuvwxyz";
    }

    @Override
    public void displayOutput(final GuiPanel terminal, final MessagePanel messages) {
	final var lines = this.getList();
	for (final String line : lines) {
	    messages.write(line);
	}
	messages.write("What would you like to " + this.getVerb() + "?");
    }

    private ArrayList<String> getList() {
	final var lines = new ArrayList<String>();
	final var inventory = this.player.inventory().getItems();
	for (var i = 0; i < inventory.length; i++) {
	    final var item = inventory[i];
	    if (item == null || !this.isAcceptable(item)) {
		continue;
	    }
	    final var line = new StringBuilder().append(this.letters.charAt(i)).append(" ")
		    .append(this.player.nameOf(item));
	    if (item == this.player.weapon() || item == this.player.armor()) {
		line.append(" (equipped)");
	    }
	    lines.add(line.toString());
	}
	return lines;
    }

    protected abstract String getVerb();

    protected abstract boolean isAcceptable(Item item);

    @Override
    public Screen respondToUserInput(final KeyEvent key, final MouseEvent mouse) {
	if (key == null) {
	    return this;
	}
	final var c = key.getKeyChar();
	final var items = this.player.inventory().getItems();
	if (this.letters.indexOf(c) > -1 && items.length > this.letters.indexOf(c)
		&& items[this.letters.indexOf(c)] != null && this.isAcceptable(items[this.letters.indexOf(c)])) {
	    return this.use(items[this.letters.indexOf(c)]);
	}
	if (key.getKeyCode() == KeyEvent.VK_ESCAPE) {
	    return null;
	}
	return this;
    }

    protected abstract Screen use(Item item);
}
