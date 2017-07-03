package net.dynamicdungeon;

import java.util.ArrayList;
import java.util.List;

public class LevelUpController {
    private static LevelUpOption[] options = new LevelUpOption[] { new LevelUpOption("Increased hit points") {
	@Override
	public void invoke(final Creature creature) {
	    creature.modifyMaxHp(10);
	    creature.modifyHp(10, "Died from increaced hp level-up bonus?");
	    creature.doAction("look a lot healthier");
	}
    }, new LevelUpOption("Increased mana") {
	@Override
	public void invoke(final Creature creature) {
	    creature.modifyMaxMana(5);
	    creature.modifyMana(5);
	    creature.doAction("look more magical");
	}
    }, new LevelUpOption("Increased attack value") {
	@Override
	public void invoke(final Creature creature) {
	    creature.modifyAttackValue(2);
	    creature.doAction("look stronger");
	}
    }, new LevelUpOption("Increased defense value") {
	@Override
	public void invoke(final Creature creature) {
	    creature.modifyDefenseValue(1);
	    creature.doAction("look a little tougher");
	}
    }, new LevelUpOption("Increased vision") {
	@Override
	public void invoke(final Creature creature) {
	    creature.modifyVisionRadius(1);
	    creature.doAction("look a little more aware");
	}
    }, new LevelUpOption("Increased hp regeneration") {
	@Override
	public void invoke(final Creature creature) {
	    creature.modifyRegenHpPer1000(10);
	    creature.doAction("look a little less bruised");
	}
    }, new LevelUpOption("Increased mana regeneration") {
	@Override
	public void invoke(final Creature creature) {
	    creature.modifyRegenManaPer1000(10);
	    creature.doAction("look a little less tired");
	}
    } };

    public void autoLevelUp(final Creature creature) {
	LevelUpController.options[(int) (Math.random() * LevelUpController.options.length)].invoke(creature);
    }

    public List<String> getLevelUpOptions() {
	final List<String> names = new ArrayList<>();
	for (final LevelUpOption option : LevelUpController.options) {
	    names.add(option.name());
	}
	return names;
    }

    public LevelUpOption getLevelUpOption(final String name) {
	for (final LevelUpOption option : LevelUpController.options) {
	    if (option.name().equals(name)) {
		return option;
	    }
	}
	return null;
    }
}
