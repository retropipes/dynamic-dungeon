package net.dynamicdungeon.screens;

import net.dynamicdungeon.Creature;
import net.dynamicdungeon.Item;

public class ExamineScreen extends InventoryBasedScreen {
    public ExamineScreen(final Creature player) {
	super(player);
    }

    @Override
    protected String getVerb() {
	return "examine";
    }

    @Override
    protected boolean isAcceptable(final Item item) {
	return true;
    }

    @Override
    protected Screen use(final Item item) {
	final String article = "aeiou".contains(this.player.nameOf(item).subSequence(0, 1)) ? "an " : "a ";
	this.player.notify("It's " + article + this.player.nameOf(item) + "." + item.details());
	return null;
    }
}
