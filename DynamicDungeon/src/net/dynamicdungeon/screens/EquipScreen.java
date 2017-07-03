package net.dynamicdungeon.screens;

import net.dynamicdungeon.Creature;
import net.dynamicdungeon.Item;

public class EquipScreen extends InventoryBasedScreen {
    public EquipScreen(final Creature player) {
	super(player);
    }

    @Override
    protected String getVerb() {
	return "wear or wield";
    }

    @Override
    protected boolean isAcceptable(final Item item) {
	return item.attackValue() > 0 || item.defenseValue() > 0;
    }

    @Override
    protected Screen use(final Item item) {
	this.player.equip(item);
	return null;
    }
}
