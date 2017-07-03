package net.dynamicdungeon.screens;

import java.awt.event.KeyEvent;

import net.dynamicdungeon.panels.GuiPanel;
import net.dynamicdungeon.panels.MessagePanel;

public class StartScreen implements Screen {
    @Override
    public void displayOutput(final GuiPanel terminal, MessagePanel messages) {
	messages.clear();
	messages.write("Dynamic Dungeon");
	messages.write("-- press [enter] to start --");
    }

    @Override
    public Screen respondToUserInput(final KeyEvent key) {
	return key.getKeyCode() == KeyEvent.VK_ENTER ? new PlayScreen() : this;
    }
}
