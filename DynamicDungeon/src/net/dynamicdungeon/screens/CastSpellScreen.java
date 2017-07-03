package net.dynamicdungeon.screens;

import net.dynamicdungeon.Creature;
import net.dynamicdungeon.Spell;

public class CastSpellScreen extends TargetBasedScreen {
    private final Spell spell;

    public CastSpellScreen(final Creature player, final String caption, final int sx, final int sy, final Spell spell) {
	super(player, caption, sx, sy);
	this.spell = spell;
    }

    @Override
    public void selectWorldCoordinate(final int x, final int y, final int screenX, final int screenY) {
	this.player.castSpell(this.spell, x, y);
    }
}
