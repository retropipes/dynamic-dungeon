package net.dynamicdungeon.screens;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import net.dynamicdungeon.Creature;
import net.dynamicdungeon.LevelUpController;
import net.dynamicdungeon.panels.GuiPanel;
import net.dynamicdungeon.panels.MessagePanel;

public class LevelUpScreen implements Screen {
    private final LevelUpController controller;
    private final Creature player;
    private int picks;

    public LevelUpScreen(final Creature thePlayer, final int thePicks) {
	this.controller = new LevelUpController();
	this.player = thePlayer;
	this.picks = thePicks;
    }

    @Override
    public void displayOutput(final GuiPanel terminal, final MessagePanel messages) {
	final var options = this.controller.getLevelUpOptions();
	messages.clear();
	messages.write("Choose a level up bonus:");
	for (var i = 0; i < options.size(); i++) {
	    messages.write(String.format("[%d] %s", i + 1, options.get(i)));
	}
    }

    @Override
    public Screen respondToUserInput(final KeyEvent key, final MouseEvent mouse) {
	if (key == null) {
	    return this;
	}
	final var options = this.controller.getLevelUpOptions();
	final var chars = new StringBuilder();
	for (var i = 0; i < options.size(); i++) {
	    chars.append(Integer.toString(i + 1));
	}
	final var i = chars.toString().indexOf(key.getKeyChar());
	if (i < 0) {
	    return this;
	}
	this.controller.getLevelUpOption(options.get(i)).invoke(this.player);
	if (--this.picks < 1) {
	    return null;
	}
	return this;
    }
}
