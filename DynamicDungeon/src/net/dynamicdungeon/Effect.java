package net.dynamicdungeon;

import java.io.IOException;

import net.dynamicdungeon.fileio.XMLFileReader;
import net.dynamicdungeon.fileio.XMLFileWriter;

public final class Effect {
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
    private static StuffFactory FACTORY;

    public Effect(final Item item) {
	// Create an empty effect to be populated later
	this.item = item;
    }

    public Effect(final int duration, final int attackModifier, final int defenseModifier, final int hpModifierOngoing,
	    final int manaModifierOngoing, final String hpModifierOngoingMessage, final Item item,
	    final String itemMessage, final int visionModifier, final int xpModifierOngoing, final int regenHpModifier,
	    final int regenManaModifier, final boolean hpToMana, final boolean blink, final boolean summonBats,
	    final boolean detect, final String itemMessageEnd) {
	this.duration = duration;
	this.attackModifier = attackModifier;
	this.defenseModifier = defenseModifier;
	this.hpModifierOngoing = hpModifierOngoing;
	this.manaModifierOngoing = manaModifierOngoing;
	this.hpModifierOngoingMessage = hpModifierOngoingMessage;
	this.item = item;
	this.itemMessage = itemMessage;
	this.visionModifier = visionModifier;
	this.xpModifierOngoing = xpModifierOngoing;
	this.regenHpModifier = regenHpModifier;
	this.regenManaModifier = regenManaModifier;
	this.hpToMana = hpToMana;
	this.blink = blink;
	this.summonBats = summonBats;
	this.detect = detect;
	this.itemMessageEnd = itemMessageEnd;
    }

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

    public static void setStuffFactory(final StuffFactory stuff) {
	Effect.FACTORY = stuff;
    }

    public boolean isDone() {
	return this.duration < 1;
    }

    public void update(final Creature creature) {
	this.duration--;
	if (!this.hpToMana) {
	    if (this.hpModifierOngoing != 0 && this.hpModifierOngoingMessage != null) {
		if (this.hpModifierOngoing > 0 && creature.hp() < creature.maxHp()) {
		    creature.modifyHp(this.hpModifierOngoing, this.hpModifierOngoingMessage);
		} else if (this.hpModifierOngoing < 0 && creature.hp() > 0) {
		    creature.modifyHp(this.hpModifierOngoing, this.hpModifierOngoingMessage);
		}
	    }
	    if (this.manaModifierOngoing != 0) {
		if (this.manaModifierOngoing > 0 && creature.mana() < creature.maxMana()) {
		    creature.modifyMana(this.manaModifierOngoing);
		} else if (this.manaModifierOngoing < 0 && creature.mana() > 0) {
		    creature.modifyMana(this.manaModifierOngoing);
		}
	    }
	}
	if (this.xpModifierOngoing != 0) {
	    creature.modifyXp(creature.level() * this.xpModifierOngoing);
	}
    }

    public void start(final Creature creature) {
	if (this.item != null && this.itemMessage != null) {
	    creature.doAction(this.item, this.itemMessage);
	}
	if (this.hpToMana && this.hpModifierOngoingMessage != null) {
	    final int amount = Math.min(creature.hp() - 1, creature.maxMana() - creature.mana());
	    creature.modifyHp(-amount, this.hpModifierOngoingMessage);
	    creature.modifyMana(amount);
	}
	if (this.blink) {
	    creature.doAction("fade out");
	    int mx = 0;
	    int my = 0;
	    do {
		mx = (int) (Math.random() * 11) - 5;
		my = (int) (Math.random() * 11) - 5;
	    } while (!creature.canEnter(creature.x + mx, creature.y + my, creature.z)
		    && creature.canSee(creature.x + mx, creature.y + my, creature.z));
	    creature.moveBy(mx, my, 0);
	    creature.doAction("fade in");
	}
	if (this.summonBats) {
	    for (int ox = -1; ox < 2; ox++) {
		for (int oy = -1; oy < 2; oy++) {
		    final int nx = creature.x + ox;
		    final int ny = creature.y + oy;
		    if (ox == 0 && oy == 0 || creature.creature(nx, ny, creature.z) != null) {
			continue;
		    }
		    final Creature bat = Effect.FACTORY.newBat(0);
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

    public void loadEffect(final XMLFileReader reader) throws IOException {
	reader.readOpeningGroup("effect");
	this.duration = reader.readCustomInt("duration");
	this.attackModifier = reader.readCustomInt("attackModifier");
	this.defenseModifier = reader.readCustomInt("defenseModifier");
	this.hpModifierOngoing = reader.readCustomInt("healthModifierOngoing");
	this.manaModifierOngoing = reader.readCustomInt("manaModifierOngoing");
	boolean hpModOMPresent = reader.readCustomBoolean("hpModOMPresent");
	if (hpModOMPresent) {
	    this.hpModifierOngoingMessage = reader.readCustomString("healthModifierOngoingMessage");
	}
	boolean imPresent = reader.readCustomBoolean("imPresent");
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
	boolean imePresent = reader.readCustomBoolean("imePresent");
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
	boolean hpModOMPresent = (this.hpModifierOngoingMessage != null);
	writer.writeCustomBoolean(hpModOMPresent, "hpModOMPresent");
	if (hpModOMPresent) {
	    writer.writeCustomString(this.hpModifierOngoingMessage, "healthModifierOngoingMessage");
	}
	boolean imPresent = (this.itemMessage != null);
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
	boolean imePresent = (this.itemMessageEnd != null);
	writer.writeCustomBoolean(imPresent, "imePresent");
	if (imePresent) {
	    writer.writeCustomString(this.itemMessageEnd, "itemMessageEnd");
	}
	writer.writeClosingGroup("effect");
    }
}
