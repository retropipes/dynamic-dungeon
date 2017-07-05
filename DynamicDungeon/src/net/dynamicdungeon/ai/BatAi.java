package net.dynamicdungeon.ai;

import net.dynamicdungeon.Creature;

public class BatAi extends CreatureAi {
    public BatAi(final Creature creature) {
	super(creature);
    }

    @Override
    public void onUpdate() {
	this.wander();
	this.wander();
    }
}
