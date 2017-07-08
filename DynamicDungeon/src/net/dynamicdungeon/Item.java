package net.dynamicdungeon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.dynamicdungeon.fileio.XMLFileReader;
import net.dynamicdungeon.fileio.XMLFileWriter;
import net.dynamicdungeon.world.Tile;

public class Item {
    private Tile tile;

    public Tile tile() {
	return this.tile;
    }

    private String name;

    public String name() {
	return this.name;
    }

    private String appearance;

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

    public Item() {
	// Create an empty item to be populated later
	this.writtenSpells = new ArrayList<>();
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

    public void loadItem(final XMLFileReader reader) throws IOException {
	reader.readOpeningGroup("item");
	this.tile = Tile.getFromSymbol(reader.readCustomString("tile").charAt(0));
	this.name = reader.readCustomString("name");
	this.appearance = reader.readCustomString("appearance");
	this.foodValue = reader.readCustomInt("food");
	this.attackValue = reader.readCustomInt("attack");
	this.defenseValue = reader.readCustomInt("defense");
	this.thrownAttackValue = reader.readCustomInt("thrown");
	this.rangedAttackValue = reader.readCustomInt("ranged");
	boolean effectExists = reader.readCustomBoolean("effectExists");
	if (effectExists) {
	    Effect ef = new Effect(this);
	    ef.loadEffect(reader);
	    this.quaffEffect = ef;
	}
	reader.readOpeningGroup("spells");
	int sSize = reader.readCustomInt("count");
	for (int s = 0; s < sSize; s++) {
	    Spell sp = new Spell();
	    sp.loadSpell(reader);
	    this.writtenSpells.add(sp);
	}
	reader.readClosingGroup("spells");
	reader.readClosingGroup("item");
    }

    public void saveItem(final XMLFileWriter writer) throws IOException {
	writer.writeOpeningGroup("item");
	writer.writeCustomString(Character.toString(this.tile.getStateSymbol()), "tile");
	writer.writeCustomString(this.name, "name");
	writer.writeCustomString(this.appearance, "appearance");
	writer.writeCustomInt(this.foodValue, "food");
	writer.writeCustomInt(this.attackValue, "attack");
	writer.writeCustomInt(this.defenseValue, "defense");
	writer.writeCustomInt(this.thrownAttackValue, "thrown");
	writer.writeCustomInt(this.rangedAttackValue, "ranged");
	boolean effectExists = (this.quaffEffect != null);
	writer.writeCustomBoolean(effectExists, "effectExists");
	if (effectExists) {
	    this.quaffEffect.saveEffect(writer);
	}
	writer.writeOpeningGroup("spells");
	int sSize = this.writtenSpells.size();
	writer.writeCustomInt(sSize, "count");
	for (int s = 0; s < sSize; s++) {
	    Spell sp = this.writtenSpells.get(s);
	    sp.saveSpell(writer);
	}
	writer.writeClosingGroup("spells");
	writer.writeClosingGroup("item");
    }
}
