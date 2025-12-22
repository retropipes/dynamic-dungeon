package net.dynamicdungeon.ai;

import net.dynamicdungeon.Creature;

public class ZombieAi extends CreatureAi {
    private final Creature player;

    public ZombieAi(final Creature theCreature, final Creature thePlayer) {
	super(theCreature);
	this.player = thePlayer;
    }

    @Override
    public void onUpdate() {
	if (Math.random() < 0.2) {
	    return;
	}
	if (this.creature.canSee(this.player.x, this.player.y, this.player.z)) {
	    this.hunt(this.player);
	} else {
	    this.wander();
	}
    }
}
