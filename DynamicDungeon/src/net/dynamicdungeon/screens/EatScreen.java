package net.dynamicdungeon.screens;

import net.dynamicdungeon.Creature;
import net.dynamicdungeon.Item;

public class EatScreen extends InventoryBasedScreen {
    public EatScreen(final Creature thePlayer) {
	super(thePlayer);
    }

    @Override
    protected String getVerb() {
	return "eat";
    }

    @Override
    protected boolean isAcceptable(final Item item) {
	return item.foodValue() != 0;
    }

    @Override
    protected Screen use(final Item item) {
	this.player.eat(item);
	return null;
    }
}
