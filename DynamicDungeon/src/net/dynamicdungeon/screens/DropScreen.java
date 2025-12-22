package net.dynamicdungeon.screens;

import net.dynamicdungeon.Creature;
import net.dynamicdungeon.Item;

public class DropScreen extends InventoryBasedScreen {
    public DropScreen(final Creature thePlayer) {
	super(thePlayer);
    }

    @Override
    protected String getVerb() {
	return "drop";
    }

    @Override
    protected boolean isAcceptable(final Item item) {
	return true;
    }

    @Override
    protected Screen use(final Item item) {
	this.player.drop(item);
	return null;
    }
}
