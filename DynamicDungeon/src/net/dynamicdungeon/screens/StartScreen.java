package net.dynamicdungeon.screens;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import net.dynamicdungeon.panels.GuiPanel;
import net.dynamicdungeon.panels.MessagePanel;

public class StartScreen implements Screen {
    @Override
    public void displayOutput(final GuiPanel terminal, final MessagePanel messages) {
	messages.clear();
	messages.write("Dynamic Dungeon");
	messages.write("-- press [l] to load a saved game --");
	messages.write("-- press [enter] to start --");
    }

    @Override
    public Screen respondToUserInput(final KeyEvent key, final MouseEvent mouse) {
	if (key != null) {
	    int keyCode = key.getKeyCode();
	    if (keyCode == KeyEvent.VK_ENTER) {
		return new PlayScreen(true, null);
	    }
	    if (keyCode == KeyEvent.VK_L) {
		// TODO: Load state
	    }
	}
	return this;
    }
}
