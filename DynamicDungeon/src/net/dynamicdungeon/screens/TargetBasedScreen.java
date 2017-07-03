package net.dynamicdungeon.screens;

import java.awt.event.KeyEvent;

import net.dynamicdungeon.Creature;
import net.dynamicdungeon.panels.GuiPanel;
import net.dynamicdungeon.panels.MessagePanel;

public abstract class TargetBasedScreen implements Screen {
    protected Creature player;
    protected String caption;
    private final int sx;
    private final int sy;
    private int x;
    private int y;

    public TargetBasedScreen(final Creature player, final String caption, final int sx, final int sy) {
	this.player = player;
	this.caption = caption;
	this.sx = sx;
	this.sy = sy;
    }

    @Override
    public void displayOutput(final GuiPanel terminal, MessagePanel messages) {
	messages.write(this.caption);
    }

    @Override
    public Screen respondToUserInput(final KeyEvent key) {
	final int px = this.x;
	final int py = this.y;
	switch (key.getKeyCode()) {
	case KeyEvent.VK_LEFT:
	case KeyEvent.VK_H:
	    this.x--;
	    break;
	case KeyEvent.VK_RIGHT:
	case KeyEvent.VK_L:
	    this.x++;
	    break;
	case KeyEvent.VK_UP:
	case KeyEvent.VK_J:
	    this.y--;
	    break;
	case KeyEvent.VK_DOWN:
	case KeyEvent.VK_K:
	    this.y++;
	    break;
	case KeyEvent.VK_Y:
	    this.x--;
	    this.y--;
	    break;
	case KeyEvent.VK_U:
	    this.x++;
	    this.y--;
	    break;
	case KeyEvent.VK_B:
	    this.x--;
	    this.y++;
	    break;
	case KeyEvent.VK_N:
	    this.x++;
	    this.y++;
	    break;
	case KeyEvent.VK_ENTER:
	    this.selectWorldCoordinate(this.player.x + this.x, this.player.y + this.y, this.sx + this.x,
		    this.sy + this.y);
	    return null;
	case KeyEvent.VK_ESCAPE:
	    return null;
	}
	if (!this.isAcceptable(this.player.x + this.x, this.player.y + this.y)) {
	    this.x = px;
	    this.y = py;
	}
	this.enterWorldCoordinate(this.player.x + this.x, this.player.y + this.y, this.sx + this.x, this.sy + this.y);
	return this;
    }

    public boolean isAcceptable(final int x, final int y) {
	return true;
    }

    public void enterWorldCoordinate(final int x, final int y, final int screenX, final int screenY) {
    }

    public void selectWorldCoordinate(final int x, final int y, final int screenX, final int screenY) {
    }
}
