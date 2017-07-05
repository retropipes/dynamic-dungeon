package net.dynamicdungeon.screens;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import net.dynamicdungeon.Creature;
import net.dynamicdungeon.panels.GuiPanel;
import net.dynamicdungeon.panels.MessagePanel;
import net.dynamicdungeon.sound.Sound;

public class LoseScreen implements Screen {
    private final Creature player;

    public LoseScreen(final Creature player) {
	this.player = player;
    }

    @Override
    public void displayOutput(final GuiPanel terminal, final MessagePanel messages) {
	Sound.play("lose");
	messages.clear();
	messages.write("R.I.P.");
	messages.write(this.player.causeOfDeath());
	messages.write("-- press [enter] or click to restart --");
    }

    @Override
    public Screen respondToUserInput(final KeyEvent key, final MouseEvent mouse) {
	if (key != null) {
	    return key.getKeyCode() == KeyEvent.VK_ENTER ? new PlayScreen() : this;
	}
	if (mouse != null) {
	    return new PlayScreen();
	}
	return this;
    }
}
