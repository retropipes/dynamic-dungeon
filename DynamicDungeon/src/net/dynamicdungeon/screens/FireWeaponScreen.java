package net.dynamicdungeon.screens;

import net.dynamicdungeon.Creature;
import net.dynamicdungeon.Line;
import net.dynamicdungeon.Point;

public class FireWeaponScreen extends TargetBasedScreen {
    public FireWeaponScreen(final Creature player, final int sx, final int sy) {
	super(player, "Fire " + player.nameOf(player.weapon()) + " at?", sx, sy);
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
	final Creature other = this.player.creature(x, y, this.player.z);
	if (other == null) {
	    this.player.notify("There's no one there to fire at.");
	} else {
	    this.player.rangedWeaponAttack(other);
	}
    }
}
