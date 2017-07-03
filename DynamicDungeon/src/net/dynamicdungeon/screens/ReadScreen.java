package net.dynamicdungeon.screens;

import net.dynamicdungeon.Creature;
import net.dynamicdungeon.Item;

public class ReadScreen extends InventoryBasedScreen {
    private final int sx;
    private final int sy;

    public ReadScreen(final Creature player, final int sx, final int sy) {
	super(player);
	this.sx = sx;
	this.sy = sy;
    }

    @Override
    protected String getVerb() {
	return "read";
    }

    @Override
    protected boolean isAcceptable(final Item item) {
	return !item.writtenSpells().isEmpty();
    }

    @Override
    protected Screen use(final Item item) {
	return new ReadSpellScreen(this.player, this.sx, this.sy, item);
    }
}
