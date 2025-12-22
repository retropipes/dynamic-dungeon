package net.dynamicdungeon.screens;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import net.dynamicdungeon.panels.GuiPanel;
import net.dynamicdungeon.panels.MessagePanel;

public interface Screen {
    void displayOutput(GuiPanel terminal, MessagePanel messages);

    Screen respondToUserInput(KeyEvent key, MouseEvent mouse);
}
