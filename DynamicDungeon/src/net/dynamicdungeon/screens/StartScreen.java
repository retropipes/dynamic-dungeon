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
	messages.write("Press [/] or [?] for help");
	messages.write("-- press [enter] to start --");
    }

    @Override
    public Screen respondToUserInput(final KeyEvent key, final MouseEvent mouse) {
	if (key != null) {
	    return key.getKeyCode() == KeyEvent.VK_ENTER ? new PlayScreen(true) : this;
	}
	return this;
    }
}
