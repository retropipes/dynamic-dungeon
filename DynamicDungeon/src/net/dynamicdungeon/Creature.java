package net.dynamicdungeon;

import java.util.ArrayList;
import java.util.List;

public class Creature {
    private final World world;
    public int x;
    public int y;
    public int z;
    private final Tile tile;

    public Tile tile() {
	return this.tile;
    }

    private CreatureAi ai;

    public void setCreatureAi(final CreatureAi ai) {
	this.ai = ai;
    }

    private int maxHp;

    public int maxHp() {
	return this.maxHp;
    }

    public void modifyMaxHp(final int amount) {
	this.maxHp += amount;
    }

    private int hp;

    public int hp() {
	return this.hp;
    }

    private int attackValue;

    public void modifyAttackValue(final int value) {
	this.attackValue += value;
    }

    public int attackValue() {
	return this.attackValue + (this.weapon == null ? 0 : this.weapon.attackValue())
		+ (this.armor == null ? 0 : this.armor.attackValue());
    }

    private int defenseValue;

    public void modifyDefenseValue(final int value) {
	this.defenseValue += value;
    }

    public int defenseValue() {
	return this.defenseValue + (this.weapon == null ? 0 : this.weapon.defenseValue())
		+ (this.armor == null ? 0 : this.armor.defenseValue());
    }

    private int visionRadius;

    public void modifyVisionRadius(final int value) {
	this.visionRadius += value;
    }

    public int visionRadius() {
	return this.visionRadius;
    }

    private final String name;

    public String name() {
	return this.name;
    }

    private final Inventory inventory;

    public Inventory inventory() {
	return this.inventory;
    }

    private int maxFood;

    public int maxFood() {
	return this.maxFood;
    }

    private int food;

    public int food() {
	return this.food;
    }

    private Item weapon;

    public Item weapon() {
	return this.weapon;
    }

    private Item armor;

    public Item armor() {
	return this.armor;
    }

    private int xp;

    public int xp() {
	return this.xp;
    }

    public void modifyXp(final int amount) {
	this.xp += amount;
	this.notify("You %s %d xp.", amount < 0 ? "lose" : "gain", amount);
	while (this.xp > (int) (Math.pow(this.level, 1.75) * 25)) {
	    this.level++;
	    this.doAction("advance to level %d", this.level);
	    this.ai.onGainLevel();
	    this.modifyHp(this.level * 2, "Died from having a negative level?");
	}
    }

    private int level;

    public int level() {
	return this.level;
    }

    private int regenHpCooldown;
    private int regenHpPer1000;

    public void modifyRegenHpPer1000(final int amount) {
	this.regenHpPer1000 += amount;
    }

    private final List<Effect> effects;

    public List<Effect> effects() {
	return this.effects;
    }

    private int maxMana;

    public int maxMana() {
	return this.maxMana;
    }

    public void modifyMaxMana(final int amount) {
	this.maxMana += amount;
    }

    private int mana;

    public int mana() {
	return this.mana;
    }

    public void modifyMana(final int amount) {
	this.mana = Math.max(0, Math.min(this.mana + amount, this.maxMana));
    }

    private int regenManaCooldown;
    private int regenManaPer1000;

    public void modifyRegenManaPer1000(final int amount) {
	this.regenManaPer1000 += amount;
    }

    private String causeOfDeath;

    public String causeOfDeath() {
	return this.causeOfDeath;
    }

    public Creature(final World world, final Tile tile, final String name, final int maxHp, final int attack,
	    final int defense) {
	this.world = world;
	this.tile = tile;
	this.maxHp = maxHp;
	this.hp = maxHp;
	this.attackValue = attack;
	this.defenseValue = defense;
	this.visionRadius = 9;
	this.name = name;
	this.inventory = new Inventory(20);
	this.maxFood = 1000;
	this.food = this.maxFood / 3 * 2;
	this.level = 1;
	this.regenHpPer1000 = 10;
	this.effects = new ArrayList<>();
	this.maxMana = 5;
	this.mana = this.maxMana;
	this.regenManaPer1000 = 20;
    }

    public void moveBy(final int mx, final int my, final int mz) {
	if (mx == 0 && my == 0 && mz == 0) {
	    return;
	}
	final Tile tile = this.world.tile(this.x + mx, this.y + my, this.z + mz);
	if (mz == -1) {
	    if (tile == Tile.STAIRS_DOWN) {
		this.doAction("walk up the stairs to level %d", this.z + mz + 1);
	    } else {
		this.doAction("try to go up but are stopped by the cave ceiling");
		return;
	    }
	} else if (mz == 1) {
	    if (tile == Tile.STAIRS_UP) {
		this.doAction("walk down the stairs to level %d", this.z + mz + 1);
	    } else {
		this.doAction("try to go down but are stopped by the cave floor");
		return;
	    }
	}
	final Creature other = this.world.creature(this.x + mx, this.y + my, this.z + mz);
	this.modifyFood(-1);
	if (other == null) {
	    this.ai.onEnter(this.x + mx, this.y + my, this.z + mz, tile);
	} else {
	    this.meleeAttack(other);
	}
    }

    public void meleeAttack(final Creature other) {
	this.commonAttack(other, this.attackValue(), "attack the %s for %d damage", other.name);
    }

    private void throwAttack(final Item item, final Creature other) {
	this.commonAttack(other, this.attackValue / 2 + item.thrownAttackValue(), "throw a %s at the %s for %d damage",
		this.nameOf(item), other.name);
	other.addEffect(item.quaffEffect());
    }

    public void rangedWeaponAttack(final Creature other) {
	this.commonAttack(other, this.attackValue / 2 + this.weapon.rangedAttackValue(),
		"fire a %s at the %s for %d damage", this.nameOf(this.weapon), other.name);
    }

    private void commonAttack(final Creature other, final int attack, final String action, final Object... params) {
	this.modifyFood(-2);
	int amount = Math.max(0, attack - other.defenseValue());
	amount = (int) (Math.random() * amount) + 1;
	final Object[] params2 = new Object[params.length + 1];
	for (int i = 0; i < params.length; i++) {
	    params2[i] = params[i];
	}
	params2[params2.length - 1] = amount;
	this.doAction(action, params2);
	other.modifyHp(-amount, "Killed by a " + this.name);
	if (other.hp < 1) {
	    this.gainXp(other);
	}
    }

    public void gainXp(final Creature other) {
	final int amount = other.maxHp + other.attackValue() + other.defenseValue() - this.level;
	if (amount > 0) {
	    this.modifyXp(amount);
	}
    }

    public void modifyHp(final int amount, final String causeOfDeath) {
	this.hp += amount;
	this.causeOfDeath = causeOfDeath;
	if (this.hp > this.maxHp) {
	    this.hp = this.maxHp;
	} else if (this.hp < 1) {
	    this.doAction("die");
	    this.leaveCorpse();
	    this.world.remove(this);
	}
    }

    private void leaveCorpse() {
	final Item corpse = new Item(Tile.CORPSE, this.name + " corpse", null);
	corpse.modifyFoodValue(this.maxHp * 5);
	this.world.addAtEmptySpace(corpse, this.x, this.y, this.z);
	for (final Item item : this.inventory.getItems()) {
	    if (item != null) {
		this.drop(item);
	    }
	}
    }

    public void dig(final int wx, final int wy, final int wz) {
	this.modifyFood(-10);
	this.world.dig(wx, wy, wz);
	this.doAction("dig");
    }

    public void update() {
	this.modifyFood(-1);
	this.regenerateHealth();
	this.regenerateMana();
	this.updateEffects();
	this.ai.onUpdate();
    }

    private void updateEffects() {
	final List<Effect> done = new ArrayList<>();
	for (final Effect effect : this.effects) {
	    effect.update(this);
	    if (effect.isDone()) {
		effect.end(this);
		done.add(effect);
	    }
	}
	this.effects.removeAll(done);
    }

    private void regenerateHealth() {
	this.regenHpCooldown -= this.regenHpPer1000;
	if (this.regenHpCooldown < 0) {
	    if (this.hp < this.maxHp) {
		this.modifyHp(1, "Died from regenerating health?");
		this.modifyFood(-1);
	    }
	    this.regenHpCooldown += 1000;
	}
    }

    private void regenerateMana() {
	this.regenManaCooldown -= this.regenManaPer1000;
	if (this.regenManaCooldown < 0) {
	    if (this.mana < this.maxMana) {
		this.modifyMana(1);
		this.modifyFood(-1);
	    }
	    this.regenManaCooldown += 1000;
	}
    }

    public boolean canEnter(final int wx, final int wy, final int wz) {
	return this.world.tile(wx, wy, wz).isGround() && this.world.creature(wx, wy, wz) == null;
    }

    public void notify(final String message, final Object... params) {
	this.ai.onNotify(String.format(message, params));
    }

    public void doAction(final String message, final Object... params) {
	for (final Creature other : this.getCreaturesWhoSeeMe()) {
	    if (other == this) {
		other.notify("You " + message + ".", params);
	    } else {
		other.notify(String.format("The %s %s.", this.name, this.makeSecondPerson(message)), params);
	    }
	}
    }

    public void doAction(final Item item, final String message, final Object... params) {
	if (this.hp < 1) {
	    return;
	}
	for (final Creature other : this.getCreaturesWhoSeeMe()) {
	    if (other == this) {
		other.notify("You " + message + ".", params);
	    } else {
		other.notify(String.format("The %s %s.", this.name, this.makeSecondPerson(message)), params);
	    }
	    other.learnName(item);
	}
    }

    private List<Creature> getCreaturesWhoSeeMe() {
	final List<Creature> others = new ArrayList<>();
	final int r = 9;
	for (int ox = -r; ox < r + 1; ox++) {
	    for (int oy = -r; oy < r + 1; oy++) {
		if (ox * ox + oy * oy > r * r) {
		    continue;
		}
		final Creature other = this.world.creature(this.x + ox, this.y + oy, this.z);
		if (other == null) {
		    continue;
		}
		others.add(other);
	    }
	}
	return others;
    }

    private String makeSecondPerson(final String text) {
	final String[] words = text.split(" ");
	words[0] = words[0] + "s";
	final StringBuilder builder = new StringBuilder();
	for (final String word : words) {
	    builder.append(" ");
	    builder.append(word);
	}
	return builder.toString().trim();
    }

    public boolean canSee(final int wx, final int wy, final int wz) {
	return this.detectCreatures > 0 && this.world.creature(wx, wy, wz) != null || this.ai.canSee(wx, wy, wz);
    }

    public Tile realTile(final int wx, final int wy, final int wz) {
	return this.world.tile(wx, wy, wz);
    }

    public Tile tile(final int wx, final int wy, final int wz) {
	if (this.canSee(wx, wy, wz)) {
	    return this.world.tile(wx, wy, wz);
	} else {
	    return this.ai.rememberedTile(wx, wy, wz);
	}
    }

    public Creature creature(final int wx, final int wy, final int wz) {
	if (this.canSee(wx, wy, wz)) {
	    return this.world.creature(wx, wy, wz);
	} else {
	    return null;
	}
    }

    public void pickup() {
	final Item item = this.world.item(this.x, this.y, this.z);
	if (this.inventory.isFull() || item == null) {
	    this.doAction("grab at the ground");
	} else {
	    this.doAction("pickup a %s", this.nameOf(item));
	    this.world.remove(this.x, this.y, this.z);
	    this.inventory.add(item);
	}
    }

    public void drop(final Item item) {
	if (this.world.addAtEmptySpace(item, this.x, this.y, this.z)) {
	    this.doAction("drop a " + this.nameOf(item));
	    this.inventory.remove(item);
	    this.unequip(item);
	} else {
	    this.notify("There's nowhere to drop the %s.", this.nameOf(item));
	}
    }

    public void modifyFood(final int amount) {
	this.food += amount;
	if (this.food > this.maxFood) {
	    this.maxFood = (this.maxFood + this.food) / 2;
	    this.food = this.maxFood;
	    this.notify("You can't belive your stomach can hold that much!");
	    this.modifyHp(-1, "Killed by overeating.");
	} else if (this.food < 1 && this.isPlayer()) {
	    this.modifyHp(-1000, "Starved to death.");
	}
    }

    public boolean isPlayer() {
	return this.tile == Tile.PLAYER;
    }

    public void eat(final Item item) {
	this.doAction("eat a " + this.nameOf(item));
	this.consume(item);
    }

    public void quaff(final Item item) {
	this.doAction("quaff a " + this.nameOf(item));
	this.consume(item);
    }

    private void consume(final Item item) {
	if (item.foodValue() < 0) {
	    this.notify("Gross!");
	}
	this.addEffect(item.quaffEffect());
	this.modifyFood(item.foodValue());
	this.getRidOf(item);
    }

    private void addEffect(final Effect effect) {
	if (effect == null) {
	    return;
	}
	effect.start(this);
	this.effects.add(effect);
    }

    private void getRidOf(final Item item) {
	this.inventory.remove(item);
	this.unequip(item);
    }

    private void putAt(final Item item, final int wx, final int wy, final int wz) {
	this.inventory.remove(item);
	this.unequip(item);
	this.world.addAtEmptySpace(item, wx, wy, wz);
    }

    public void unequip(final Item item) {
	if (item == null) {
	    return;
	}
	if (item == this.armor) {
	    if (this.hp > 0) {
		this.doAction("remove a " + this.nameOf(item));
	    }
	    this.armor = null;
	} else if (item == this.weapon) {
	    if (this.hp > 0) {
		this.doAction("put away a " + this.nameOf(item));
	    }
	    this.weapon = null;
	}
    }

    public void equip(final Item item) {
	if (!this.inventory.contains(item)) {
	    if (this.inventory.isFull()) {
		this.notify("Can't equip %s since you're holding too much stuff.", this.nameOf(item));
		return;
	    } else {
		this.world.remove(item);
		this.inventory.add(item);
	    }
	}
	if (item.attackValue() == 0 && item.rangedAttackValue() == 0 && item.defenseValue() == 0) {
	    return;
	}
	if (item.attackValue() + item.rangedAttackValue() >= item.defenseValue()) {
	    this.unequip(this.weapon);
	    this.doAction("wield a " + this.nameOf(item));
	    this.weapon = item;
	} else {
	    this.unequip(this.armor);
	    this.doAction("put on a " + this.nameOf(item));
	    this.armor = item;
	}
    }

    public Item item(final int wx, final int wy, final int wz) {
	if (this.canSee(wx, wy, wz)) {
	    return this.world.item(wx, wy, wz);
	} else {
	    return null;
	}
    }

    public String details() {
	return String.format("  level:%d  attack:%d  defense:%d  hp:%d", this.level, this.attackValue(),
		this.defenseValue(), this.hp);
    }

    public void throwItem(final Item item, int wx, int wy, final int wz) {
	Point end = new Point(this.x, this.y, 0);
	for (final Point p : new Line(this.x, this.y, wx, wy)) {
	    if (!this.realTile(p.x, p.y, this.z).isGround()) {
		break;
	    }
	    end = p;
	}
	wx = end.x;
	wy = end.y;
	final Creature c = this.creature(wx, wy, wz);
	if (c != null) {
	    this.throwAttack(item, c);
	} else {
	    this.doAction("throw a %s", this.nameOf(item));
	}
	if (item.quaffEffect() != null && c != null) {
	    this.getRidOf(item);
	} else {
	    this.putAt(item, wx, wy, wz);
	}
    }

    public void summon(final Creature other) {
	this.world.add(other);
    }

    private int detectCreatures;

    public void modifyDetectCreatures(final int amount) {
	this.detectCreatures += amount;
    }

    public void castSpell(final Spell spell, final int x2, final int y2) {
	final Creature other = this.creature(x2, y2, this.z);
	if (spell.manaCost() > this.mana) {
	    this.doAction("point and mumble but nothing happens");
	    return;
	} else if (other == null) {
	    this.doAction("point and mumble at nothing");
	    return;
	}
	other.addEffect(spell.effect());
	this.modifyMana(-spell.manaCost());
    }

    public String nameOf(final Item item) {
	return this.ai.getName(item);
    }

    public void learnName(final Item item) {
	this.notify("The " + item.appearance() + " is a " + item.name() + "!");
	this.ai.setName(item, item.name());
    }
}
