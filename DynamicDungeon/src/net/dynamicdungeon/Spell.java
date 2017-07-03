package net.dynamicdungeon;

public class Spell {
    private final String name;

    public String name() {
	return this.name;
    }

    private final int manaCost;

    public int manaCost() {
	return this.manaCost;
    }

    private final Effect effect;

    public Effect effect() {
	return new Effect(this.effect);
    }

    public boolean requiresTarget() {
	return true;
    }

    public Spell(final String name, final int manaCost, final Effect effect) {
	this.name = name;
	this.manaCost = manaCost;
	this.effect = effect;
    }
}
