package net.dynamicdungeon.screens;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;

import net.dynamicdungeon.Creature;
import net.dynamicdungeon.LevelUpController;
import net.dynamicdungeon.panels.GuiPanel;
import net.dynamicdungeon.panels.MessagePanel;

public class LevelUpScreen implements Screen {
    private final LevelUpController controller;
    private final Creature player;
    private int picks;

    public LevelUpScreen(final Creature player, final int picks) {
	this.controller = new LevelUpController();
	this.player = player;
	this.picks = picks;
    }

    @Override
    public void displayOutput(final GuiPanel terminal, MessagePanel messages) {
	final List<String> options = this.controller.getLevelUpOptions();
	messages.clear();
	messages.write("Choose a level up bonus:");
	for (int i = 0; i < options.size(); i++) {
	    messages.write(String.format("[%d] %s", i + 1, options.get(i)));
	}
    }

    @Override
    public Screen respondToUserInput(final KeyEvent key, MouseEvent mouse) {
	if (key != null) {
	    final List<String> options = this.controller.getLevelUpOptions();
	    String chars = "";
	    for (int i = 0; i < options.size(); i++) {
		chars = chars + Integer.toString(i + 1);
	    }
	    final int i = chars.indexOf(key.getKeyChar());
	    if (i < 0) {
		return this;
	    }
	    this.controller.getLevelUpOption(options.get(i)).invoke(this.player);
	    if (--this.picks < 1) {
		return null;
	    } else {
		return this;
	    }
	} else {
	    return this;
	}
    }
}
