package net.dynamicdungeon.screens;

import net.dynamicdungeon.Creature;

public class LookScreen extends TargetBasedScreen {
    public LookScreen(final Creature thePlayer, final String theCaption, final int sx, final int sy) {
	super(thePlayer, theCaption, sx, sy);
    }

    @Override
    public void enterWorldCoordinate(final int x, final int y, final int screenX, final int screenY) {
	final var creature = this.player.creature(x, y, this.player.z);
	if (creature != null) {
	    this.caption = creature.name() + creature.details();
	    return;
	}
	final var item = this.player.item(x, y, this.player.z);
	if (item != null) {
	    this.caption = this.player.nameOf(item) + item.details();
	    return;
	}
	final var tile = this.player.tile(x, y, this.player.z);
	this.caption = tile.details();
    }
}
