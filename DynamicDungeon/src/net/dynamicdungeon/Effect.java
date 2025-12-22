package net.dynamicdungeon;

import java.io.IOException;

import net.dynamicdungeon.fileio.XMLFileReader;
import net.dynamicdungeon.fileio.XMLFileWriter;

public final class Effect {
    private static StuffFactory FACTORY;

    public static void setStuffFactory(final StuffFactory stuff) {
	Effect.FACTORY = stuff;
    }

    // Fields
    private int duration;
    private int attackModifier;
    private int defenseModifier;
    private int hpModifierOngoing;
    private int manaModifierOngoing;
    private String hpModifierOngoingMessage;
    private String itemMessage;
    private String itemMessageEnd;
    private int visionModifier;
    private int xpModifierOngoing;
    private int regenHpModifier;
    private int regenManaModifier;
    private boolean hpToMana;
    private boolean blink;
    private boolean summonBats;
    private boolean detect;
    private final Item item;

    public Effect(final Effect other) {
	this.duration = other.duration;
	this.attackModifier = other.attackModifier;
	this.defenseModifier = other.defenseModifier;
	this.hpModifierOngoing = other.hpModifierOngoing;
	this.manaModifierOngoing = other.manaModifierOngoing;
	this.hpModifierOngoingMessage = other.hpModifierOngoingMessage;
	this.item = other.item;
	this.itemMessage = other.itemMessage;
	this.visionModifier = other.visionModifier;
	this.xpModifierOngoing = other.xpModifierOngoing;
	this.regenHpModifier = other.regenHpModifier;
	this.regenManaModifier = other.regenManaModifier;
	this.hpToMana = other.hpToMana;
	this.blink = other.blink;
	this.summonBats = other.summonBats;
	this.detect = other.detect;
	this.itemMessageEnd = other.itemMessageEnd;
    }

    public Effect(final int theDuration, final int theAttackModifier, final int theDefenseModifier,
	    final int theHpModifierOngoing, final int theManaModifierOngoing, final String theHpModifierOngoingMessage,
	    final Item theItem, final String theItemMessage, final int theVisionModifier,
	    final int theXpModifierOngoing, final int theRegenHpModifier, final int theRegenManaModifier,
	    final boolean theHpToMana, final boolean isBlink, final boolean summonsBats, final boolean isDetect,
	    final String theItemMessageEnd) {
	this.duration = theDuration;
	this.attackModifier = theAttackModifier;
	this.defenseModifier = theDefenseModifier;
	this.hpModifierOngoing = theHpModifierOngoing;
	this.manaModifierOngoing = theManaModifierOngoing;
	this.hpModifierOngoingMessage = theHpModifierOngoingMessage;
	this.item = theItem;
	this.itemMessage = theItemMessage;
	this.visionModifier = theVisionModifier;
	this.xpModifierOngoing = theXpModifierOngoing;
	this.regenHpModifier = theRegenHpModifier;
	this.regenManaModifier = theRegenManaModifier;
	this.hpToMana = theHpToMana;
	this.blink = isBlink;
	this.summonBats = summonsBats;
	this.detect = isDetect;
	this.itemMessageEnd = theItemMessageEnd;
    }

    public Effect(final Item theItem) {
	// Create an empty effect to be populated later
	this.item = theItem;
    }

    public void end(final Creature creature) {
	if (this.itemMessageEnd != null) {
	    creature.doAction(this.itemMessageEnd);
	}
	if (this.detect) {
	    creature.modifyDetectCreatures(-1);
	}
	if (this.attackModifier != 0) {
	    creature.modifyAttackValue(-this.attackModifier);
	}
	if (this.defenseModifier != 0) {
	    creature.modifyDefenseValue(-this.defenseModifier);
	}
	if (this.visionModifier != 0) {
	    creature.modifyVisionRadius(-this.visionModifier);
	}
	if (this.regenHpModifier != 0) {
	    creature.modifyRegenHpPer1000(-this.regenHpModifier);
	}
	if (this.regenManaModifier != 0) {
	    creature.modifyRegenManaPer1000(-this.regenManaModifier);
	}
    }

    public boolean isDone() {
	return this.duration < 1;
    }

    public void loadEffect(final XMLFileReader reader) throws IOException {
	reader.readOpeningGroup("effect");
	this.duration = reader.readCustomInt("duration");
	this.attackModifier = reader.readCustomInt("attackModifier");
	this.defenseModifier = reader.readCustomInt("defenseModifier");
	this.hpModifierOngoing = reader.readCustomInt("healthModifierOngoing");
	this.manaModifierOngoing = reader.readCustomInt("manaModifierOngoing");
	final var hpModOMPresent = reader.readCustomBoolean("hpModOMPresent");
	if (hpModOMPresent) {
	    this.hpModifierOngoingMessage = reader.readCustomString("healthModifierOngoingMessage");
	}
	final var imPresent = reader.readCustomBoolean("imPresent");
	if (imPresent) {
	    this.itemMessage = reader.readCustomString("itemMessage");
	}
	this.visionModifier = reader.readCustomInt("visionModifier");
	this.regenHpModifier = reader.readCustomInt("regenHealthModifier");
	this.regenManaModifier = reader.readCustomInt("regenManaModifier");
	this.hpToMana = reader.readCustomBoolean("hpToManaFlag");
	this.blink = reader.readCustomBoolean("blinkFlag");
	this.summonBats = reader.readCustomBoolean("summonBatsFlag");
	this.detect = reader.readCustomBoolean("detectFlag");
	final var imePresent = reader.readCustomBoolean("imePresent");
	if (imePresent) {
	    this.itemMessageEnd = reader.readCustomString("itemMessageEnd");
	}
	reader.readClosingGroup("effect");
    }

    public void saveEffect(final XMLFileWriter writer) throws IOException {
	writer.writeOpeningGroup("effect");
	writer.writeCustomInt(this.duration, "duration");
	writer.writeCustomInt(this.attackModifier, "attackModifier");
	writer.writeCustomInt(this.defenseModifier, "defenseModifier");
	writer.writeCustomInt(this.hpModifierOngoing, "healthModifierOngoing");
	writer.writeCustomInt(this.manaModifierOngoing, "manaModifierOngoing");
	final var hpModOMPresent = this.hpModifierOngoingMessage != null;
	writer.writeCustomBoolean(hpModOMPresent, "hpModOMPresent");
	if (hpModOMPresent) {
	    writer.writeCustomString(this.hpModifierOngoingMessage, "healthModifierOngoingMessage");
	}
	final var imPresent = this.itemMessage != null;
	writer.writeCustomBoolean(imPresent, "imPresent");
	if (imPresent) {
	    writer.writeCustomString(this.itemMessage, "itemMessage");
	}
	writer.writeCustomInt(this.visionModifier, "visionModifier");
	writer.writeCustomInt(this.regenHpModifier, "regenHealthModifier");
	writer.writeCustomInt(this.regenManaModifier, "regenManaModifier");
	writer.writeCustomBoolean(this.hpToMana, "hpToManaFlag");
	writer.writeCustomBoolean(this.blink, "blinkFlag");
	writer.writeCustomBoolean(this.summonBats, "summonBatsFlag");
	writer.writeCustomBoolean(this.detect, "detectFlag");
	final var imePresent = this.itemMessageEnd != null;
	writer.writeCustomBoolean(imPresent, "imePresent");
	if (imePresent) {
	    writer.writeCustomString(this.itemMessageEnd, "itemMessageEnd");
	}
	writer.writeClosingGroup("effect");
    }

    public void start(final Creature creature) {
	if (this.item != null && this.itemMessage != null) {
	    creature.doAction(this.item, this.itemMessage);
	}
	if (this.hpToMana && this.hpModifierOngoingMessage != null) {
	    final var amount = Math.min(creature.hp() - 1, creature.maxMana() - creature.mana());
	    creature.modifyHp(-amount, this.hpModifierOngoingMessage);
	    creature.modifyMana(amount);
	}
	if (this.blink) {
	    creature.doAction("fade out");
	    var mx = 0;
	    var my = 0;
	    do {
		mx = (int) (Math.random() * 11) - 5;
		my = (int) (Math.random() * 11) - 5;
	    } while (!creature.canEnter(creature.x + mx, creature.y + my, creature.z)
		    && creature.canSee(creature.x + mx, creature.y + my, creature.z));
	    creature.moveBy(mx, my, 0);
	    creature.doAction("fade in");
	}
	if (this.summonBats) {
	    for (var ox = -1; ox < 2; ox++) {
		for (var oy = -1; oy < 2; oy++) {
		    final var nx = creature.x + ox;
		    final var ny = creature.y + oy;
		    if (ox == 0 && oy == 0 || creature.creature(nx, ny, creature.z) != null) {
			continue;
		    }
		    final var bat = Effect.FACTORY.newBat(0);
		    if (!bat.canEnter(nx, ny, creature.z)) {
			Effect.FACTORY.getWorld().remove(bat);
			continue;
		    }
		    bat.x = nx;
		    bat.y = ny;
		    bat.z = creature.z;
		    creature.summon(bat);
		}
	    }
	}
	if (this.detect) {
	    creature.doAction("look far off into the distance");
	    creature.modifyDetectCreatures(1);
	}
	if (this.attackModifier != 0) {
	    creature.modifyAttackValue(this.attackModifier);
	}
	if (this.defenseModifier != 0) {
	    creature.modifyDefenseValue(this.defenseModifier);
	}
	if (this.visionModifier != 0) {
	    creature.modifyVisionRadius(this.visionModifier);
	}
	if (this.regenHpModifier != 0) {
	    creature.modifyRegenHpPer1000(this.regenHpModifier);
	}
	if (this.regenManaModifier != 0) {
	    creature.modifyRegenManaPer1000(this.regenManaModifier);
	}
    }

    public void update(final Creature creature) {
	this.duration--;
	if (!this.hpToMana) {
	    if (this.hpModifierOngoing != 0 && this.hpModifierOngoingMessage != null) {
		if (this.hpModifierOngoing > 0 && creature.hp() < creature.maxHp()
			|| this.hpModifierOngoing < 0 && creature.hp() > 0) {
		    creature.modifyHp(this.hpModifierOngoing, this.hpModifierOngoingMessage);
		}
	    }
	    if (this.manaModifierOngoing != 0) {
		if (this.manaModifierOngoing > 0 && creature.mana() < creature.maxMana()
			|| this.manaModifierOngoing < 0 && creature.mana() > 0) {
		    creature.modifyMana(this.manaModifierOngoing);
		}
	    }
	}
	if (this.xpModifierOngoing != 0) {
	    creature.modifyXp(creature.level() * this.xpModifierOngoing);
	}
    }
}
