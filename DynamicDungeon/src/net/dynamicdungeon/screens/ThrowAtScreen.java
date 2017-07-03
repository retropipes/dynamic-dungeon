package net.dynamicdungeon.screens;

import net.dynamicdungeon.Creature;
import net.dynamicdungeon.Item;
import net.dynamicdungeon.Line;
import net.dynamicdungeon.Point;

public class ThrowAtScreen extends TargetBasedScreen {
    private final Item item;

    public ThrowAtScreen(final Creature player, final int sx, final int sy, final Item item) {
	super(player, "Throw " + player.nameOf(item) + " at?", sx, sy);
	this.item = item;
    }

    @Override
    public boolean isAcceptable(final int x, final int y) {
	if (!this.player.canSee(x, y, this.player.z)) {
	    return false;
	}
	for (final Point p : new Line(this.player.x, this.player.y, x, y)) {
	    if (!this.player.realTile(p.x, p.y, this.player.z).isGround()) {
		return false;
	    }
	}
	return true;
    }

    @Override
    public void selectWorldCoordinate(final int x, final int y, final int screenX, final int screenY) {
	this.player.throwItem(this.item, x, y, this.player.z);
    }
}
