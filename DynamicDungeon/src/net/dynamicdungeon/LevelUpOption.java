package net.dynamicdungeon;

public abstract class LevelUpOption {
    private final String name;

    public LevelUpOption(final String theName) {
	this.name = theName;
    }

    public abstract void invoke(Creature creature);

    public String name() {
	return this.name;
    }
}
