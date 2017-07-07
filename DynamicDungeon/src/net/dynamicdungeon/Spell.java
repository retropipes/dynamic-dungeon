package net.dynamicdungeon;

import java.io.IOException;

import net.dynamicdungeon.fileio.XMLFileReader;
import net.dynamicdungeon.fileio.XMLFileWriter;

public class Spell {
    private String name;

    public String name() {
	return this.name;
    }

    private int manaCost;

    public int manaCost() {
	return this.manaCost;
    }

    private Effect effect;

    public Effect effect() {
	return new Effect(this.effect);
    }

    public boolean requiresTarget() {
	return true;
    }

    public Spell() {
	// Create an empty spell to be populated later
    }

    public Spell(final String name, final int manaCost, final Effect effect) {
	this.name = name;
	this.manaCost = manaCost;
	this.effect = effect;
    }

    public void loadSpell(final XMLFileReader reader) throws IOException {
	reader.readOpeningGroup("spell");
	this.name = reader.readCustomString("name");
	this.manaCost = reader.readCustomInt("manaCost");
	// effect
	reader.readClosingGroup("spell");
    }

    public void saveSpell(final XMLFileWriter writer) throws IOException {
	writer.writeOpeningGroup("spell");
	writer.writeCustomString(this.name, "name");
	writer.writeCustomInt(this.manaCost, "manaCost");
	// effect
	writer.writeClosingGroup("spell");
    }
}
