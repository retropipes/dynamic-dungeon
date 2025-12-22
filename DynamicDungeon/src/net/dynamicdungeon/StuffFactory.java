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

    public StuffFactory(final World theWorld) {
	this.world = theWorld;
    }

    public World getWorld() {
	return this.world;
    }

    public Creature newBat(final int depth) {
	final var bat = new Creature(this.world, Tile.BAT, "bat", 15, 5, 0);
	this.world.addAtEmptyLocation(bat, depth);
	bat.setCreatureAi(new BatAi(bat));
	return bat;
    }

    public Item newBlueMagesSpellbook(final int depth) {
	final var item = new Item(Tile.SPELL, "blue mage's spellbook", null);
	item.addWrittenSpell("blood to mana", 1,
		new Effect(1, 0, 0, 0, 0, null, item, null, 0, 0, 0, 0, true, false, false, false, null));
	item.addWrittenSpell("blink", 6,
		new Effect(1, 0, 0, 0, 0, null, item, null, 0, 0, 0, 0, false, true, false, false, null));
	item.addWrittenSpell("summon bats", 11,
		new Effect(1, 0, 0, 0, 0, null, item, null, 0, 0, 0, 0, false, false, true, false, null));
	item.addWrittenSpell("detect creatures", 16,
		new Effect(75, 0, 0, 0, 0, null, item, null, 0, 0, 0, 0, false, false, false, true, null));
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newBow(final int depth) {
	final var item = new Item(Tile.BOW, "bow", null);
	item.modifyAttackValue(1);
	item.modifyRangedAttackValue(5);
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newBread(final int depth) {
	final var item = new Item(Tile.BREAD, "bread", null);
	item.modifyFoodValue(400);
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newDagger(final int depth) {
	final var item = new Item(Tile.DAGGER, "dagger", null);
	item.modifyAttackValue(5);
	item.modifyThrownAttackValue(5);
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newEdibleWeapon(final int depth) {
	final var item = new Item(Tile.MAGIC_WEAPON, "baguette", null);
	item.modifyAttackValue(3);
	item.modifyFoodValue(100);
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newFruit(final int depth) {
	final var item = new Item(Tile.APPLE, "apple", null);
	item.modifyFoodValue(100);
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Creature newFungus(final int depth) {
	final var fungus = new Creature(this.world, Tile.FUNGUS, "fungus", 10, 0, 0);
	this.world.addAtEmptyLocation(fungus, depth);
	fungus.setCreatureAi(new FungusAi(fungus, this));
	return fungus;
    }

    public Creature newGoblin(final int depth, final Creature player) {
	final var goblin = new Creature(this.world, Tile.GOBLIN, "goblin", 66, 15, 5);
	goblin.setCreatureAi(new GoblinAi(goblin, player));
	goblin.equip(this.randomWeapon(depth));
	goblin.equip(this.randomArmor(depth));
	this.world.addAtEmptyLocation(goblin, depth);
	return goblin;
    }

    public Item newHeavyArmor(final int depth) {
	final var item = new Item(Tile.HEAVY_ARMOR, "platemail", null);
	item.modifyDefenseValue(6);
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newLightArmor(final int depth) {
	final var item = new Item(Tile.LIGHT_ARMOR, "tunic", null);
	item.modifyDefenseValue(2);
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newMediumArmor(final int depth) {
	final var item = new Item(Tile.ARMOR, "chainmail", null);
	item.modifyDefenseValue(4);
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Creature newPlayer(final List<String> messages, final FieldOfView fov) {
	final var player = new Creature(this.world, Tile.PLAYER, "player", 100, 20, 5);
	this.world.addAtEmptyLocation(player, 0);
	player.setCreatureAi(new PlayerAi(player, messages, fov));
	return player;
    }

    public Item newPotionOfArcher(final int depth) {
	final var item = new Item(Tile.POTION, "archers potion", "potion");
	item.setQuaffEffect(new Effect(20, 0, 0, 0, 0, null, item, "look more alert", 3, 0, 0, 0, false, false, false,
		false, "look less alert"));
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newPotionOfExperience(final int depth) {
	final var item = new Item(Tile.POTION, "experience potion", "potion");
	item.setQuaffEffect(new Effect(20, 0, 0, 0, 0, null, item, "look more experienced", 0, 5, 0, 0, false, false,
		false, false, null));
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newPotionOfHealth(final int depth) {
	final var item = new Item(Tile.POTION, "health potion", "potion");
	item.setQuaffEffect(new Effect(1, 0, 0, 15, 0, "Killed by a health potion?", item, "look healthier", 0, 0, 0, 0,
		false, false, false, false, null));
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newPotionOfMana(final int depth) {
	final var item = new Item(Tile.POTION, "mana potion", "potion");
	item.setQuaffEffect(
		new Effect(1, 0, 0, 0, 10, null, item, "look restored", 0, 0, 0, 0, false, false, false, false, null));
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newPotionOfPoison(final int depth) {
	final var item = new Item(Tile.POTION, "poison potion", "potion");
	item.setQuaffEffect(new Effect(20, 0, 0, -1, 0, "Died of poison.", item, "look sick", 0, 0, 0, 0, false, false,
		false, false, null));
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newPotionOfSlowHealth(final int depth) {
	final var item = new Item(Tile.POTION, "slow health potion", "potion");
	item.setQuaffEffect(new Effect(100, 0, 0, 1, 0, "Killed by a slow health potion?", item, "look a little better",
		0, 0, 0, 0, false, false, false, false, null));
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newPotionOfWarrior(final int depth) {
	final var item = new Item(Tile.POTION, "warrior's potion", "potion");
	item.setQuaffEffect(new Effect(20, 5, 5, 0, 0, null, item, "look stronger", 0, 0, 0, 0, false, false, false,
		false, "look less strong"));
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newRock(final int depth) {
	final var rock = new Item(Tile.ROCK, "rock", null);
	rock.modifyThrownAttackValue(5);
	this.world.addAtEmptyLocation(rock, depth);
	return rock;
    }

    public Item newStaff(final int depth) {
	final var item = new Item(Tile.STAFF, "staff", null);
	item.modifyAttackValue(5);
	item.modifyDefenseValue(3);
	item.modifyThrownAttackValue(3);
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newSword(final int depth) {
	final var item = new Item(Tile.SWORD, "sword", null);
	item.modifyAttackValue(10);
	item.modifyThrownAttackValue(3);
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newVictoryItem(final int depth) {
	final var item = new Item(Tile.VICTORY, "amulet", null);
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Item newWhiteMagesSpellbook(final int depth) {
	final var item = new Item(Tile.SPELL, "white mage's spellbook", null);
	item.addWrittenSpell("minor heal", 4, new Effect(1, 0, 0, 20, 0, "Killed by a minor heal spell?", item,
		"look healthier", 0, 0, 0, 0, false, false, false, false, null));
	item.addWrittenSpell("major heal", 8, new Effect(1, 0, 0, 50, 0, "Killed by a major heal spell?", item,
		"look healthier", 0, 0, 0, 0, false, false, false, false, null));
	item.addWrittenSpell("slow heal", 12, new Effect(50, 0, 0, 2, 0, "Killed by a slow heal spell?", item, null, 0,
		0, 0, 0, false, false, false, false, null));
	item.addWrittenSpell("inner strength", 16, new Effect(50, 2, 2, 0, 0, "Killed by inner strength spell?", item,
		"seem to glow with inner strength", 1, 0, 10, -10, false, false, false, false, null));
	this.world.addAtEmptyLocation(item, depth);
	return item;
    }

    public Creature newZombie(final int depth, final Creature player) {
	final var zombie = new Creature(this.world, Tile.ZOMBIE, "zombie", 50, 10, 10);
	this.world.addAtEmptyLocation(zombie, depth);
	zombie.setCreatureAi(new ZombieAi(zombie, player));
	return zombie;
    }

    public Item randomArmor(final int depth) {
	return switch ((int) (Math.random() * 3)) {
	case 0 -> this.newLightArmor(depth);
	case 1 -> this.newMediumArmor(depth);
	default -> this.newHeavyArmor(depth);
	};
    }

    public Item randomPotion(final int depth) {
	return switch ((int) (Math.random() * 9)) {
	case 0 -> this.newPotionOfHealth(depth);
	case 1 -> this.newPotionOfHealth(depth);
	case 2 -> this.newPotionOfMana(depth);
	case 3 -> this.newPotionOfMana(depth);
	case 4 -> this.newPotionOfSlowHealth(depth);
	case 5 -> this.newPotionOfPoison(depth);
	case 6 -> this.newPotionOfWarrior(depth);
	case 7 -> this.newPotionOfArcher(depth);
	default -> this.newPotionOfExperience(depth);
	};
    }

    public Item randomSpellBook(final int depth) {
	return switch ((int) (Math.random() * 2)) {
	case 0 -> this.newWhiteMagesSpellbook(depth);
	default -> this.newBlueMagesSpellbook(depth);
	};
    }

    public Item randomWeapon(final int depth) {
	return switch ((int) (Math.random() * 3)) {
	case 0 -> this.newDagger(depth);
	case 1 -> this.newSword(depth);
	case 2 -> this.newBow(depth);
	default -> this.newStaff(depth);
	};
    }
}
