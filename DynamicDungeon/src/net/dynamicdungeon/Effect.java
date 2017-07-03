package net.dynamicdungeon;

public class Effect {
    protected int duration;

    public boolean isDone() {
	return this.duration < 1;
    }

    public Effect(final int duration) {
	this.duration = duration;
    }

    public Effect(final Effect other) {
	this.duration = other.duration;
    }

    public void update(final Creature creature) {
	this.duration--;
    }

    public void start(final Creature creature) {
    }

    public void end(final Creature creature) {
    }
}
