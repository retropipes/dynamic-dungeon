package net.dynamicdungeon.screens;

import net.dynamicdungeon.Creature;
import net.dynamicdungeon.Item;
import net.dynamicdungeon.world.Tile;

public class LookScreen extends TargetBasedScreen {
    public LookScreen(final Creature player, final String caption, final int sx, final int sy) {
	super(player, caption, sx, sy);
    }

    @Override
    public void enterWorldCoordinate(final int x, final int y, final int screenX, final int screenY) {
	final Creature creature = this.player.creature(x, y, this.player.z);
	if (creature != null) {
	    this.caption = creature.name() + creature.details();
	    return;
	}
	final Item item = this.player.item(x, y, this.player.z);
	if (item != null) {
	    this.caption = this.player.nameOf(item) + item.details();
	    return;
	}
	final Tile tile = this.player.tile(x, y, this.player.z);
	this.caption = tile.details();
    }
}
