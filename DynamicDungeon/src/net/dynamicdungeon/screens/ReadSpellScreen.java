package net.dynamicdungeon.screens;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import net.dynamicdungeon.Creature;
import net.dynamicdungeon.Item;
import net.dynamicdungeon.Spell;
import net.dynamicdungeon.panels.GuiPanel;
import net.dynamicdungeon.panels.MessagePanel;

public class ReadSpellScreen implements Screen {
    protected Creature player;
    private final String letters;
    private final Item item;
    private final int sx;
    private final int sy;

    public ReadSpellScreen(final Creature thePlayer, final int nsx, final int nsy, final Item theItem) {
	this.player = thePlayer;
	this.letters = "abcdefghijklmnopqrstuvwxyz";
	this.item = theItem;
	this.sx = nsx;
	this.sy = nsy;
    }

    @Override
    public void displayOutput(final GuiPanel terminal, final MessagePanel messages) {
	final var lines = this.getList();
	for (final String line : lines) {
	    messages.write(line);
	}
	messages.write("What would you like to read?");
    }

    private ArrayList<String> getList() {
	final var lines = new ArrayList<String>();
	for (var i = 0; i < this.item.writtenSpells().size(); i++) {
	    final var spell = this.item.writtenSpells().get(i);
	    final var line = this.letters.charAt(i) + " - " + spell.name() + " (" + spell.manaCost() + " mana)";
	    lines.add(line);
	}
	return lines;
    }

    @Override
    public Screen respondToUserInput(final KeyEvent key, final MouseEvent mouse) {
	if (key == null) {
	    return this;
	}
	final var c = key.getKeyChar();
	final var items = this.player.inventory().getItems();
	if (this.letters.indexOf(c) > -1 && items.length > this.letters.indexOf(c)
		&& items[this.letters.indexOf(c)] != null) {
	    return this.use(this.item.writtenSpells().get(this.letters.indexOf(c)));
	}
	if (key.getKeyCode() == KeyEvent.VK_ESCAPE) {
	    return null;
	}
	return this;
    }

    protected Screen use(final Spell spell) {
	if (spell.requiresTarget()) {
	    return new CastSpellScreen(this.player, "", this.sx, this.sy, spell);
	}
	this.player.castSpell(spell, this.player.x, this.player.y);
	return null;
    }
}
