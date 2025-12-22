package net.dynamicdungeon;

import java.io.IOException;

import net.dynamicdungeon.fileio.XMLFileReader;
import net.dynamicdungeon.fileio.XMLFileWriter;

public class Spell {
    private String name;
    private int manaCost;
    private Effect effect;

    public Spell() {
	// Create an empty spell to be populated later
    }

    public Spell(final String theName, final int theManaCost, final Effect theEffect) {
	this.name = theName;
	this.manaCost = theManaCost;
	this.effect = theEffect;
    }

    public Effect effect() {
	return new Effect(this.effect);
    }

    public void loadSpell(final XMLFileReader reader) throws IOException {
	reader.readOpeningGroup("spell");
	this.name = reader.readCustomString("name");
	this.manaCost = reader.readCustomInt("manaCost");
	final var ef = new Effect((Item) null);
	ef.loadEffect(reader);
	this.effect = ef;
	reader.readClosingGroup("spell");
    }

    public int manaCost() {
	return this.manaCost;
    }

    public String name() {
	return this.name;
    }

    public boolean requiresTarget() {
	return true;
    }

    public void saveSpell(final XMLFileWriter writer) throws IOException {
	writer.writeOpeningGroup("spell");
	writer.writeCustomString(this.name, "name");
	writer.writeCustomInt(this.manaCost, "manaCost");
	this.effect.saveEffect(writer);
	writer.writeClosingGroup("spell");
    }
}
