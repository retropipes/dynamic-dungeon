package net.dynamicdungeon.screens;

import java.awt.event.KeyEvent;

import net.dynamicdungeon.Sound;
import net.dynamicdungeon.panels.GuiPanel;
import net.dynamicdungeon.panels.MessagePanel;

public class WinScreen implements Screen {
    @Override
    public void displayOutput(final GuiPanel terminal, MessagePanel messages) {
	Sound.play("win");
	messages.clear();
	messages.write("You won.");
	messages.write("-- press [enter] to restart --");
    }

    @Override
    public Screen respondToUserInput(final KeyEvent key) {
	return key.getKeyCode() == KeyEvent.VK_ENTER ? new PlayScreen() : this;
    }
}
