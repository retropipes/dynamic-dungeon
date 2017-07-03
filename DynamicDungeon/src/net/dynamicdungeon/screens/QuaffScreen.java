package net.dynamicdungeon.screens;

import net.dynamicdungeon.Creature;
import net.dynamicdungeon.Item;

public class QuaffScreen extends InventoryBasedScreen {
    public QuaffScreen(final Creature player) {
	super(player);
    }

    @Override
    protected String getVerb() {
	return "quaff";
    }

    @Override
    protected boolean isAcceptable(final Item item) {
	return item.quaffEffect() != null;
    }

    @Override
    protected Screen use(final Item item) {
	this.player.quaff(item);
	return null;
    }
}
