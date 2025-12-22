package net.dynamicdungeon.ai;

import net.dynamicdungeon.Creature;

public class BatAi extends CreatureAi {
    public BatAi(final Creature theCreature) {
	super(theCreature);
    }

    @Override
    public void onUpdate() {
	this.wander();
	this.wander();
    }
}
