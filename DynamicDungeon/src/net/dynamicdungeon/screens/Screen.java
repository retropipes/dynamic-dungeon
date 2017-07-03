package net.dynamicdungeon.screens;

import java.awt.event.KeyEvent;

import net.dynamicdungeon.panels.GuiPanel;
import net.dynamicdungeon.panels.MessagePanel;

public interface Screen {
    public void displayOutput(GuiPanel terminal, MessagePanel messages);

    public Screen respondToUserInput(KeyEvent key);
}
