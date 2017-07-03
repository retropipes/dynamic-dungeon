package net.dynamicdungeon;

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
