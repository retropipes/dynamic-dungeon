package net.dynamicdungeon;

import java.util.ArrayList;
import java.util.List;

public class Item {
    private final Tile tile;

    public Tile tile() {
	return this.tile;
    }

    private final String name;

    public String name() {
	return this.name;
    }

    private final String appearance;

    public String appearance() {
	return this.appearance;
    }

    private int foodValue;

    public int foodValue() {
	return this.foodValue;
    }

    public void modifyFoodValue(final int amount) {
	this.foodValue += amount;
    }

    private int attackValue;

    public int attackValue() {
	return this.attackValue;
    }

    public void modifyAttackValue(final int amount) {
	this.attackValue += amount;
    }

    private int defenseValue;

    public int defenseValue() {
	return this.defenseValue;
    }

    public void modifyDefenseValue(final int amount) {
	this.defenseValue += amount;
    }

    private int thrownAttackValue;

    public int thrownAttackValue() {
	return this.thrownAttackValue;
    }

    public void modifyThrownAttackValue(final int amount) {
	this.thrownAttackValue += amount;
    }

    private int rangedAttackValue;

    public int rangedAttackValue() {
	return this.rangedAttackValue;
    }

    public void modifyRangedAttackValue(final int amount) {
	this.rangedAttackValue += amount;
    }

    private Effect quaffEffect;

    public Effect quaffEffect() {
	return this.quaffEffect;
    }

    public void setQuaffEffect(final Effect effect) {
	this.quaffEffect = effect;
    }

    private final List<Spell> writtenSpells;

    public List<Spell> writtenSpells() {
	return this.writtenSpells;
    }

    public void addWrittenSpell(final String name, final int manaCost, final Effect effect) {
	this.writtenSpells.add(new Spell(name, manaCost, effect));
    }

    public Item(final Tile tile, final String name, final String appearance) {
	this.tile = tile;
	this.name = name;
	this.appearance = appearance == null ? name : appearance;
	this.thrownAttackValue = 1;
	this.writtenSpells = new ArrayList<>();
    }

    public String details() {
	String details = "";
	if (this.attackValue != 0) {
	    details += "  attack:" + this.attackValue;
	}
	if (this.thrownAttackValue != 1) {
	    details += "  thrown:" + this.thrownAttackValue;
	}
	if (this.rangedAttackValue > 0) {
	    details += "  ranged:" + this.rangedAttackValue;
	}
	if (this.defenseValue != 0) {
	    details += "  defense:" + this.defenseValue;
	}
	if (this.foodValue != 0) {
	    details += "  food:" + this.foodValue;
	}
	return details;
    }
}
