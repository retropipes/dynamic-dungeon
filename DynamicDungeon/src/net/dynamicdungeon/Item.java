package net.dynamicdungeon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.dynamicdungeon.fileio.XMLFileReader;
import net.dynamicdungeon.fileio.XMLFileWriter;
import net.dynamicdungeon.world.Tile;

public class Item {
    private Tile tile;
    private String name;
    private String appearance;
    private int foodValue;
    private int attackValue;
    private int defenseValue;
    private int thrownAttackValue;
    private int rangedAttackValue;
    private Effect quaffEffect;
    private final List<Spell> writtenSpells;

    public Item() {
	// Create an empty item to be populated later
	this.writtenSpells = new ArrayList<>();
    }

    public Item(final Tile theTile, final String theName, final String theAppearance) {
	this.tile = theTile;
	this.name = theName;
	this.appearance = theAppearance == null ? theName : theAppearance;
	this.thrownAttackValue = 1;
	this.writtenSpells = new ArrayList<>();
    }

    public void addWrittenSpell(final String theName, final int manaCost, final Effect effect) {
	this.writtenSpells.add(new Spell(theName, manaCost, effect));
    }

    public String appearance() {
	return this.appearance;
    }

    public int attackValue() {
	return this.attackValue;
    }

    public int defenseValue() {
	return this.defenseValue;
    }

    public String details() {
	final var details = new StringBuilder();
	if (this.attackValue != 0) {
	    details.append("  attack:").append(this.attackValue);
	}
	if (this.thrownAttackValue != 1) {
	    details.append("  thrown:").append(this.thrownAttackValue);
	}
	if (this.rangedAttackValue > 0) {
	    details.append("  ranged:").append(this.rangedAttackValue);
	}
	if (this.defenseValue != 0) {
	    details.append("  defense:").append(this.defenseValue);
	}
	if (this.foodValue != 0) {
	    details.append("  food:").append(this.foodValue);
	}
	return details.toString();
    }

    public int foodValue() {
	return this.foodValue;
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
	final var effectExists = reader.readCustomBoolean("effectExists");
	if (effectExists) {
	    final var ef = new Effect(this);
	    ef.loadEffect(reader);
	    this.quaffEffect = ef;
	}
	reader.readOpeningGroup("spells");
	final var sSize = reader.readCustomInt("count");
	for (var s = 0; s < sSize; s++) {
	    final var sp = new Spell();
	    sp.loadSpell(reader);
	    this.writtenSpells.add(sp);
	}
	reader.readClosingGroup("spells");
	reader.readClosingGroup("item");
    }

    public void modifyAttackValue(final int amount) {
	this.attackValue += amount;
    }

    public void modifyDefenseValue(final int amount) {
	this.defenseValue += amount;
    }

    public void modifyFoodValue(final int amount) {
	this.foodValue += amount;
    }

    public void modifyRangedAttackValue(final int amount) {
	this.rangedAttackValue += amount;
    }

    public void modifyThrownAttackValue(final int amount) {
	this.thrownAttackValue += amount;
    }

    public String name() {
	return this.name;
    }

    public Effect quaffEffect() {
	return this.quaffEffect;
    }

    public int rangedAttackValue() {
	return this.rangedAttackValue;
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
	final var effectExists = this.quaffEffect != null;
	writer.writeCustomBoolean(effectExists, "effectExists");
	if (effectExists) {
	    this.quaffEffect.saveEffect(writer);
	}
	writer.writeOpeningGroup("spells");
	final var sSize = this.writtenSpells.size();
	writer.writeCustomInt(sSize, "count");
	for (var s = 0; s < sSize; s++) {
	    final var sp = this.writtenSpells.get(s);
	    sp.saveSpell(writer);
	}
	writer.writeClosingGroup("spells");
	writer.writeClosingGroup("item");
    }

    public void setQuaffEffect(final Effect effect) {
	this.quaffEffect = effect;
    }

    public int thrownAttackValue() {
	return this.thrownAttackValue;
    }

    public Tile tile() {
	return this.tile;
    }

    public List<Spell> writtenSpells() {
	return this.writtenSpells;
    }
}
