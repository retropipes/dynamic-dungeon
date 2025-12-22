package net.dynamicdungeon.screens;

import net.dynamicdungeon.Creature;
import net.dynamicdungeon.Item;

public class ThrowScreen extends InventoryBasedScreen {
    private final int sx;
    private final int sy;

    public ThrowScreen(final Creature thePlayer, final int nsx, final int nsy) {
	super(thePlayer);
	this.sx = nsx;
	this.sy = nsy;
    }

    @Override
    protected String getVerb() {
	return "throw";
    }

    @Override
    protected boolean isAcceptable(final Item item) {
	return true;
    }

    @Override
    protected Screen use(final Item item) {
	return new ThrowAtScreen(this.player, this.sx, this.sy, item);
    }
}
