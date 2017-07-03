package net.dynamicdungeon;

public abstract class LevelUpOption {
    private final String name;

    public String name() {
	return this.name;
    }

    public LevelUpOption(final String name) {
	this.name = name;
    }

    public abstract void invoke(Creature creature);
}
