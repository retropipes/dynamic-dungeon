package net.dynamicdungeon;

import java.util.List;

import net.dynamicdungeon.ai.BatAi;
import net.dynamicdungeon.ai.FungusAi;
import net.dynamicdungeon.ai.GoblinAi;
import net.dynamicdungeon.ai.PlayerAi;
import net.dynamicdungeon.ai.ZombieAi;
import net.dynamicdungeon.world.Tile;
import net.dynamicdungeon.world.World;

public class StuffFactory {
    private final World world;

    public StuffFactory(final World world) {
	this.world = world;
    }

    public Creature newPlayer(final List<String> messages, final FieldOfView fov) {
	final Creature player = new Creature(this.world, Tile.PLAYER, "player", 100, 20, 5);
	this.world.addAtEmptyLocation(player, 0);
	new PlayerAi(player, messages, fov);
	return player;
    }

    public Creature newFungus(final int depth) {
	final Creature fungus = new Creature(this.world, Tile.FUNGUS, "fungus", 10, 0, 0);
	this.world.addAtEmptyLocation(fungus, depth);
	new FungusAi(fungus, this);
	return fungus;
    }

    public Creature newBat(final int depth) {
	final Creature bat = new Creature(this.world, Tile.BAT, "bat", 15, 5, 0);
	this.world.addAtEmptyLocation(bat, depth);
	new BatAi(bat);
	return bat;
    }

    public Creature newZombie(final int depth, final Creature player) {
	final Creature zombie = new Creature(this.world, Tile.ZOMBIE, "zombie", 50, 10, 10);
	this.world.addAtEmptyLocation(zombie, depth);
	new ZombieAi(zombie, player);
	return zombie;
    }

    public Creature newGoblin(final int depth, final Creature player) {
	final Creature goblin = new Creature(this.world, Tile.GOBLIN, "goblin", 66, 15, 5);
	new GoblinAi(goblin, player);
	goblin.equip(this.randomWeapon(depth));
	goblin.equip(this.randomArmor(depth));
	this.world.addAtEmptyLocation(goblin, depth);
	return goblin;
    }

    public Item newRock(final int depth) {
	final Item rock = new Item(Tile.ROCK, "rock", null);
	rock.modifyThrownAttackValue(5);
	this.world.addAtEmptyLocation(rock, depth);
	return rock;
    }

    public Item newVictoryItem(final int depth) {
	final Item item = new Item(Tile.VICTORY, "amulet", null);
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newBread(final int depth) {
	final Item item = new Item(Tile.BREAD, "bread", null);
	item.modifyFoodValue(400);
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newFruit(final int depth) {
	final Item item = new Item(Tile.APPLE, "apple", null);
	item.modifyFoodValue(100);
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newDagger(final int depth) {
	final Item item = new Item(Tile.DAGGER, "dagger", null);
	item.modifyAttackValue(5);
	item.modifyThrownAttackValue(5);
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newSword(final int depth) {
	final Item item = new Item(Tile.SWORD, "sword", null);
	item.modifyAttackValue(10);
	item.modifyThrownAttackValue(3);
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newStaff(final int depth) {
	final Item item = new Item(Tile.STAFF, "staff", null);
	item.modifyAttackValue(5);
	item.modifyDefenseValue(3);
	item.modifyThrownAttackValue(3);
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newBow(final int depth) {
	final Item item = new Item(Tile.BOW, "bow", null);
	item.modifyAttackValue(1);
	item.modifyRangedAttackValue(5);
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newEdibleWeapon(final int depth) {
	final Item item = new Item(Tile.MAGIC_WEAPON, "baguette", null);
	item.modifyAttackValue(3);
	item.modifyFoodValue(100);
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newLightArmor(final int depth) {
	final Item item = new Item(Tile.LIGHT_ARMOR, "tunic", null);
	item.modifyDefenseValue(2);
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newMediumArmor(final int depth) {
	final Item item = new Item(Tile.ARMOR, "chainmail", null);
	item.modifyDefenseValue(4);
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newHeavyArmor(final int depth) {
	final Item item = new Item(Tile.HEAVY_ARMOR, "platemail", null);
	item.modifyDefenseValue(6);
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item randomWeapon(final int depth) {
	switch ((int) (Math.random() * 3)) {
	case 0:
	    return this.newDagger(depth);
	case 1:
	    return this.newSword(depth);
	case 2:
	    return this.newBow(depth);
	default:
	    return this.newStaff(depth);
	}
    }

    public Item randomArmor(final int depth) {
	switch ((int) (Math.random() * 3)) {
	case 0:
	    return this.newLightArmor(depth);
	case 1:
	    return this.newMediumArmor(depth);
	default:
	    return this.newHeavyArmor(depth);
	}
    }

    public Item newPotionOfHealth(final int depth) {
	final Item item = new Item(Tile.POTION, "health potion", "potion");
	item.setQuaffEffect(new Effect(1) {
	    @Override
	    public void start(final Creature creature) {
		if (creature.hp() == creature.maxHp()) {
		    return;
		}
		creature.modifyHp(15, "Killed by a health potion?");
		creature.doAction(item, "look healthier");
	    }
	});
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newPotionOfMana(final int depth) {
	final Item item = new Item(Tile.POTION, "mana potion", "potion");
	item.setQuaffEffect(new Effect(1) {
	    @Override
	    public void start(final Creature creature) {
		if (creature.mana() == creature.maxMana()) {
		    return;
		}
		creature.modifyMana(10);
		creature.doAction(item, "look restored");
	    }
	});
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newPotionOfSlowHealth(final int depth) {
	final Item item = new Item(Tile.POTION, "slow health potion", "potion");
	item.setQuaffEffect(new Effect(100) {
	    @Override
	    public void start(final Creature creature) {
		creature.doAction(item, "look a little better");
	    }

	    @Override
	    public void update(final Creature creature) {
		super.update(creature);
		creature.modifyHp(1, "Killed by a slow health potion?");
	    }
	});
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newPotionOfPoison(final int depth) {
	final Item item = new Item(Tile.POTION, "poison potion", "potion");
	item.setQuaffEffect(new Effect(20) {
	    @Override
	    public void start(final Creature creature) {
		creature.doAction(item, "look sick");
	    }

	    @Override
	    public void update(final Creature creature) {
		super.update(creature);
		creature.modifyHp(-1, "Died of poison.");
	    }
	});
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newPotionOfWarrior(final int depth) {
	final Item item = new Item(Tile.POTION, "warrior's potion", "potion");
	item.setQuaffEffect(new Effect(20) {
	    @Override
	    public void start(final Creature creature) {
		creature.modifyAttackValue(5);
		creature.modifyDefenseValue(5);
		creature.doAction(item, "look stronger");
	    }

	    @Override
	    public void end(final Creature creature) {
		creature.modifyAttackValue(-5);
		creature.modifyDefenseValue(-5);
		creature.doAction("look less strong");
	    }
	});
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newPotionOfArcher(final int depth) {
	final Item item = new Item(Tile.POTION, "archers potion", "potion");
	item.setQuaffEffect(new Effect(20) {
	    @Override
	    public void start(final Creature creature) {
		creature.modifyVisionRadius(3);
		creature.doAction(item, "look more alert");
	    }

	    @Override
	    public void end(final Creature creature) {
		creature.modifyVisionRadius(-3);
		creature.doAction("look less alert");
	    }
	});
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newPotionOfExperience(final int depth) {
	final Item item = new Item(Tile.POTION, "experience potion", "potion");
	item.setQuaffEffect(new Effect(20) {
	    @Override
	    public void start(final Creature creature) {
		creature.doAction(item, "look more experienced");
		creature.modifyXp(creature.level() * 5);
	    }
	});
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item randomPotion(final int depth) {
	switch ((int) (Math.random() * 9)) {
	case 0:
	    return this.newPotionOfHealth(depth);
	case 1:
	    return this.newPotionOfHealth(depth);
	case 2:
	    return this.newPotionOfMana(depth);
	case 3:
	    return this.newPotionOfMana(depth);
	case 4:
	    return this.newPotionOfSlowHealth(depth);
	case 5:
	    return this.newPotionOfPoison(depth);
	case 6:
	    return this.newPotionOfWarrior(depth);
	case 7:
	    return this.newPotionOfArcher(depth);
	default:
	    return this.newPotionOfExperience(depth);
	}
    }

    public Item newWhiteMagesSpellbook(final int depth) {
	final Item item = new Item(Tile.SPELL, "white mage's spellbook", null);
	item.addWrittenSpell("minor heal", 4, new Effect(1) {
	    @Override
	    public void start(final Creature creature) {
		if (creature.hp() == creature.maxHp()) {
		    return;
		}
		creature.modifyHp(20, "Killed by a minor heal spell?");
		creature.doAction("look healthier");
	    }
	});
	item.addWrittenSpell("major heal", 8, new Effect(1) {
	    @Override
	    public void start(final Creature creature) {
		if (creature.hp() == creature.maxHp()) {
		    return;
		}
		creature.modifyHp(50, "Killed by a major heal spell?");
		creature.doAction("look healthier");
	    }
	});
	item.addWrittenSpell("slow heal", 12, new Effect(50) {
	    @Override
	    public void update(final Creature creature) {
		super.update(creature);
		creature.modifyHp(2, "Killed by a slow heal spell?");
	    }
	});
	item.addWrittenSpell("inner strength", 16, new Effect(50) {
	    @Override
	    public void start(final Creature creature) {
		creature.modifyAttackValue(2);
		creature.modifyDefenseValue(2);
		creature.modifyVisionRadius(1);
		creature.modifyRegenHpPer1000(10);
		creature.modifyRegenManaPer1000(-10);
		creature.doAction("seem to glow with inner strength");
	    }

	    @Override
	    public void update(final Creature creature) {
		super.update(creature);
		if (Math.random() < 0.25) {
		    creature.modifyHp(1, "Killed by inner strength spell?");
		}
	    }

	    @Override
	    public void end(final Creature creature) {
		creature.modifyAttackValue(-2);
		creature.modifyDefenseValue(-2);
		creature.modifyVisionRadius(-1);
		creature.modifyRegenHpPer1000(-10);
		creature.modifyRegenManaPer1000(10);
	    }
	});
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newBlueMagesSpellbook(final int depth) {
	final Item item = new Item(Tile.SPELL, "blue mage's spellbook", null);
	item.addWrittenSpell("blood to mana", 1, new Effect(1) {
	    @Override
	    public void start(final Creature creature) {
		final int amount = Math.min(creature.hp() - 1, creature.maxMana() - creature.mana());
		creature.modifyHp(-amount, "Killed by a blood to mana spell.");
		creature.modifyMana(amount);
	    }
	});
	item.addWrittenSpell("blink", 6, new Effect(1) {
	    @Override
	    public void start(final Creature creature) {
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
	});
	item.addWrittenSpell("summon bats", 11, new Effect(1) {
	    @Override
	    public void start(final Creature creature) {
		for (int ox = -1; ox < 2; ox++) {
		    for (int oy = -1; oy < 2; oy++) {
			final int nx = creature.x + ox;
			final int ny = creature.y + oy;
			if (ox == 0 && oy == 0 || creature.creature(nx, ny, creature.z) != null) {
			    continue;
			}
			final Creature bat = StuffFactory.this.newBat(0);
			if (!bat.canEnter(nx, ny, creature.z)) {
			    StuffFactory.this.world.remove(bat);
			    continue;
			}
			bat.x = nx;
			bat.y = ny;
			bat.z = creature.z;
			creature.summon(bat);
		    }
		}
	    }
	});
	item.addWrittenSpell("detect creatures", 16, new Effect(75) {
	    @Override
	    public void start(final Creature creature) {
		creature.doAction("look far off into the distance");
		creature.modifyDetectCreatures(1);
	    }

	    @Override
	    public void end(final Creature creature) {
		creature.modifyDetectCreatures(-1);
	    }
	});
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item randomSpellBook(final int depth) {
	switch ((int) (Math.random() * 2)) {
	case 0:
	    return this.newWhiteMagesSpellbook(depth);
	default:
	    return this.newBlueMagesSpellbook(depth);
	}
    }
}
