package net.dynamicdungeon.screens;

import net.dynamicdungeon.Creature;
import net.dynamicdungeon.Spell;

public class CastSpellScreen extends TargetBasedScreen {
    private final Spell spell;

    public CastSpellScreen(final Creature thePlayer, final String theCaption, final int sx, final int sy,
	    final Spell theSpell) {
	super(thePlayer, theCaption, sx, sy);
	this.spell = theSpell;
    }

    @Override
    public void selectWorldCoordinate(final int x, final int y, final int screenX, final int screenY) {
	this.player.castSpell(this.spell, x, y);
    }
}
