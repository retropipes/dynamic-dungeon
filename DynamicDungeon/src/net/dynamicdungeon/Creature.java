package net.dynamicdungeon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.dynamicdungeon.ai.CreatureAi;
import net.dynamicdungeon.fileio.XMLFileReader;
import net.dynamicdungeon.fileio.XMLFileWriter;
import net.dynamicdungeon.sound.Sound;
import net.dynamicdungeon.world.Tile;
import net.dynamicdungeon.world.World;

public class Creature {
    private static String makeSecondPerson(final String theText) {
	final var words = theText.split(" ");
	words[0] = words[0] + "s";
	final var builder = new StringBuilder();
	for (final String word : words) {
	    builder.append(" ");
	    builder.append(word);
	}
	return builder.toString().trim();
    }

    private final World world;
    public int x;
    public int y;
    public int z;
    private Tile tile;
    private CreatureAi ai;
    private int maxHp;
    private int hp;
    private int attackValue;
    private int defenseValue;
    private int visionRadius;
    private String name;
    private final Inventory inventory;
    private int maxFood;
    private int food;
    private Item weapon;
    private Item armor;
    private int xp;
    private int level;
    private int regenHpCooldown;
    private int regenHpPer1000;
    private int regenManaCooldown;
    private int regenManaPer1000;
    private String causeOfDeath;
    private final List<Effect> effects;
    private int maxMana;
    private int mana;
    private int detectCreatures;

    public Creature(final World theWorld) {
	// Create an empty creature to be populated later
	this.world = theWorld;
	this.inventory = new Inventory(20);
	this.effects = new ArrayList<>();
    }

    public Creature(final World theWorld, final Tile theTile, final String theName, final int theMaxHp,
	    final int attack, final int defense) {
	this.world = theWorld;
	this.tile = theTile;
	this.maxHp = theMaxHp;
	this.hp = theMaxHp;
	this.attackValue = attack;
	this.defenseValue = defense;
	this.visionRadius = 9;
	this.name = theName;
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

    private void addEffect(final Effect effect) {
	if (effect == null) {
	    return;
	}
	effect.start(this);
	this.effects.add(effect);
    }

    public Item armor() {
	return this.armor;
    }

    public int attackValue() {
	return this.attackValue + (this.weapon == null ? 0 : this.weapon.attackValue())
		+ (this.armor == null ? 0 : this.armor.attackValue());
    }

    public boolean canEnter(final int wx, final int wy, final int wz) {
	return this.world.tile(wx, wy, wz).isGround() && this.world.creature(wx, wy, wz) == null;
    }

    public boolean canSee(final int wx, final int wy, final int wz) {
	return this.detectCreatures > 0 && this.world.creature(wx, wy, wz) != null || this.ai.canSee(wx, wy, wz);
    }

    public void castSpell(final Spell spell, final int x2, final int y2) {
	final var other = this.creature(x2, y2, this.z);
	if (spell.manaCost() > this.mana) {
	    if (this.isPlayer()) {
		Sound.play("failed");
	    }
	    this.doAction("point and mumble but nothing happens");
	    return;
	}
	if (other == null) {
	    if (this.isPlayer()) {
		Sound.play("failed");
	    }
	    this.doAction("point and mumble at nothing");
	    return;
	}
	if (this.isPlayer()) {
	    Sound.play("spell");
	}
	other.addEffect(spell.effect());
	this.modifyMana(-spell.manaCost());
    }

    public String causeOfDeath() {
	return this.causeOfDeath;
    }

    private void commonAttack(final Creature other, final int attack, final String action, final Object... params) {
	this.modifyFood(-2);
	var amount = Math.max(0, attack - other.defenseValue());
	amount = (int) (Math.random() * amount) + 1;
	final var params2 = new Object[params.length + 1];
	for (var i = 0; i < params.length; i++) {
	    params2[i] = params[i];
	}
	params2[params2.length - 1] = amount;
	this.doAction(action, params2);
	other.modifyHp(-amount, "Killed by a " + this.name);
	if (other.hp < 1) {
	    this.gainXp(other);
	}
    }

    private void consume(final Item item) {
	if (this.isPlayer()) {
	    Sound.play("consume");
	}
	if (item.foodValue() < 0) {
	    this.notify("Gross!");
	}
	this.addEffect(item.quaffEffect());
	this.modifyFood(item.foodValue());
	this.getRidOf(item);
    }

    public Creature creature(final int wx, final int wy, final int wz) {
	if (this.canSee(wx, wy, wz)) {
	    return this.world.creature(wx, wy, wz);
	}
	return null;
    }

    public int defenseValue() {
	return this.defenseValue + (this.weapon == null ? 0 : this.weapon.defenseValue())
		+ (this.armor == null ? 0 : this.armor.defenseValue());
    }

    public String details() {
	if (this.isPlayer()) {
	    Sound.play("identify");
	}
	return String.format("  level:%d  attack:%d  defense:%d  hp:%d", this.level, this.attackValue(),
		this.defenseValue(), this.hp);
    }

    public void dig(final int wx, final int wy, final int wz) {
	if (this.isPlayer()) {
	    Sound.play("dig");
	}
	this.modifyFood(-10);
	this.world.dig(wx, wy, wz);
	this.doAction("dig");
    }

    public void doAction(final Item item, final String message, final Object... params) {
	if (this.hp < 1) {
	    return;
	}
	for (final Creature other : this.getCreaturesWhoSeeMe()) {
	    if (other == this) {
		other.notify("You " + message + ".", params);
	    } else {
		other.notify(String.format("The %s %s.", this.name, Creature.makeSecondPerson(message)), params);
	    }
	    other.learnName(item);
	}
    }

    public void doAction(final String message, final Object... params) {
	for (final Creature other : this.getCreaturesWhoSeeMe()) {
	    if (other == this) {
		other.notify("You " + message + ".", params);
	    } else {
		other.notify(String.format("The %s %s.", this.name, Creature.makeSecondPerson(message)), params);
	    }
	}
    }

    public void drop(final Item item) {
	if (this.world.addAtEmptySpace(item, this.x, this.y, this.z)) {
	    if (this.isPlayer()) {
		Sound.play("drop");
	    }
	    this.doAction("drop a " + this.nameOf(item));
	    this.inventory.remove(item);
	    this.unequip(item);
	} else {
	    if (this.isPlayer()) {
		Sound.play("failed");
	    }
	    this.notify("There's nowhere to drop the %s.", this.nameOf(item));
	}
    }

    public void eat(final Item item) {
	this.doAction("eat a " + this.nameOf(item));
	this.consume(item);
    }

    public List<Effect> effects() {
	return this.effects;
    }

    public void equip(final Item item) {
	if (!this.inventory.contains(item)) {
	    if (this.inventory.isFull()) {
		this.notify("Can't equip %s since you're holding too much stuff.", this.nameOf(item));
		return;
	    }
	    this.world.remove(item);
	    this.inventory.add(item);
	}
	if (item.attackValue() == 0 && item.rangedAttackValue() == 0 && item.defenseValue() == 0) {
	    return;
	}
	if (this.isPlayer()) {
	    Sound.play("equip");
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

    public int food() {
	return this.food;
    }

    public void gainXp(final Creature other) {
	final var amount = other.maxHp + other.attackValue() + other.defenseValue() - this.level;
	if (amount > 0) {
	    this.modifyXp(amount);
	}
    }

    private List<Creature> getCreaturesWhoSeeMe() {
	final List<Creature> others = new ArrayList<>();
	final var r = 9;
	for (var ox = -r; ox < r + 1; ox++) {
	    for (var oy = -r; oy < r + 1; oy++) {
		if (ox * ox + oy * oy > r * r) {
		    continue;
		}
		final var other = this.world.creature(this.x + ox, this.y + oy, this.z);
		if (other == null) {
		    continue;
		}
		others.add(other);
	    }
	}
	return others;
    }

    private void getRidOf(final Item item) {
	this.inventory.remove(item);
	this.unequip(item);
    }

    public int hp() {
	return this.hp;
    }

    public Inventory inventory() {
	return this.inventory;
    }

    public boolean isPlayer() {
	return this.tile == Tile.PLAYER;
    }

    public Item item(final int wx, final int wy, final int wz) {
	if (this.canSee(wx, wy, wz)) {
	    return this.world.item(wx, wy, wz);
	}
	return null;
    }

    public void learnName(final Item item) {
	this.notify("The " + item.appearance() + " is a " + item.name() + "!");
	this.ai.setName(item, item.name());
    }

    private void leaveCorpse() {
	final var corpse = new Item(Tile.CORPSE, this.name + " corpse", null);
	corpse.modifyFoodValue(this.maxHp * 5);
	this.world.addAtEmptySpace(corpse, this.x, this.y, this.z);
	for (final Item item : this.inventory.getItems()) {
	    if (item != null) {
		this.drop(item);
	    }
	}
    }

    public int level() {
	return this.level;
    }

    public void loadCreature(final XMLFileReader reader) throws IOException {
	reader.readOpeningGroup("creature");
	this.tile = Tile.getFromSymbol(reader.readCustomString("tile").charAt(0));
	this.name = reader.readCustomString("name");
	this.maxHp = reader.readCustomInt("maxHealth");
	this.hp = reader.readCustomInt("health");
	this.attackValue = reader.readCustomInt("attack");
	this.defenseValue = reader.readCustomInt("defense");
	this.visionRadius = reader.readCustomInt("vision");
	this.maxFood = reader.readCustomInt("maxFood");
	this.food = reader.readCustomInt("food");
	this.maxMana = reader.readCustomInt("maxMana");
	this.mana = reader.readCustomInt("mana");
	this.xp = reader.readCustomInt("xp");
	this.level = reader.readCustomInt("level");
	this.regenHpCooldown = reader.readCustomInt("regenHealthCooldown");
	this.regenHpPer1000 = reader.readCustomInt("regenHealthPer1000");
	this.regenManaCooldown = reader.readCustomInt("regenManaCooldown");
	this.regenManaPer1000 = reader.readCustomInt("regenManaPer1000");
	final var hasWeapon = reader.readCustomBoolean("hasWeapon");
	if (hasWeapon) {
	    this.weapon = new Item();
	    reader.readOpeningGroup("weapon");
	    this.weapon.loadItem(reader);
	    reader.readClosingGroup("weapon");
	}
	final var hasArmor = reader.readCustomBoolean("hasArmor");
	if (hasArmor) {
	    this.armor = new Item();
	    reader.readOpeningGroup("armor");
	    this.armor.loadItem(reader);
	    reader.readClosingGroup("armor");
	}
	this.inventory.loadInventory(reader);
	reader.readOpeningGroup("effects");
	final var efSize = reader.readCustomInt("size");
	this.effects.clear();
	for (var e = 0; e < efSize; e++) {
	    final var ef = new Effect((Item) null);
	    ef.loadEffect(reader);
	    this.effects.add(ef);
	}
	reader.readClosingGroup("effects");
	reader.readClosingGroup("creature");
    }

    public int mana() {
	return this.mana;
    }

    public int maxFood() {
	return this.maxFood;
    }

    public int maxHp() {
	return this.maxHp;
    }

    public int maxMana() {
	return this.maxMana;
    }

    public void meleeAttack(final Creature other) {
	if (this.isPlayer()) {
	    Sound.play("hit");
	} else if (other.isPlayer()) {
	    Sound.play("monsterhit");
	}
	this.commonAttack(other, this.attackValue(), "attack the %s for %d damage", other.name);
    }

    public void modifyAttackValue(final int value) {
	this.attackValue += value;
    }

    public void modifyDefenseValue(final int value) {
	this.defenseValue += value;
    }

    public void modifyDetectCreatures(final int amount) {
	this.detectCreatures += amount;
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

    public void modifyHp(final int amount, final String theCauseOfDeath) {
	this.hp += amount;
	this.causeOfDeath = theCauseOfDeath;
	if (this.hp > this.maxHp) {
	    this.hp = this.maxHp;
	} else if (this.hp < 1) {
	    if (!this.isPlayer()) {
		Sound.play("death");
	    }
	    this.doAction("die");
	    this.leaveCorpse();
	    this.world.remove(this);
	}
    }

    public void modifyMana(final int amount) {
	this.mana = Math.max(0, Math.min(this.mana + amount, this.maxMana));
    }

    public void modifyMaxHp(final int amount) {
	this.maxHp += amount;
    }

    public void modifyMaxMana(final int amount) {
	this.maxMana += amount;
    }

    public void modifyRegenHpPer1000(final int amount) {
	this.regenHpPer1000 += amount;
    }

    public void modifyRegenManaPer1000(final int amount) {
	this.regenManaPer1000 += amount;
    }

    public void modifyVisionRadius(final int value) {
	this.visionRadius += value;
    }

    public void modifyXp(final int amount) {
	this.xp += amount;
	this.notify("You %s %d xp.", amount < 0 ? "lose" : "gain", amount);
	while (this.xp > (int) (Math.pow(this.level, 1.75) * 25)) {
	    if (this.isPlayer()) {
		Sound.play("level_up");
	    }
	    this.level++;
	    this.doAction("advance to level %d", this.level);
	    this.ai.onGainLevel();
	    this.modifyHp(this.level * 2, "Died from having a negative level?");
	}
    }

    public void moveBy(final int mx, final int my, final int mz) {
	if (mx == 0 && my == 0 && mz == 0) {
	    return;
	}
	final var newTile = this.world.tile(this.x + mx, this.y + my, this.z + mz);
	if (mz == -1) {
	    if (newTile != Tile.STAIRS_DOWN) {
		if (this.isPlayer()) {
		    Sound.play("failed");
		}
		this.doAction("try to go up but are stopped by the cave ceiling");
		return;
	    }
	    if (this.isPlayer()) {
		Sound.play("up");
	    }
	    this.doAction("walk up the stairs to level %d", this.z + mz + 1);
	} else if (mz == 1) {
	    if (newTile != Tile.STAIRS_UP) {
		if (this.isPlayer()) {
		    Sound.play("failed");
		}
		this.doAction("try to go down but are stopped by the cave floor");
		return;
	    }
	    if (this.isPlayer()) {
		Sound.play("down");
	    }
	    this.doAction("walk down the stairs to level %d", this.z + mz + 1);
	}
	if (this.isPlayer()) {
	    Sound.play("walk");
	}
	final var other = this.world.creature(this.x + mx, this.y + my, this.z + mz);
	this.modifyFood(-1);
	if (other == null) {
	    this.ai.onEnter(this.x + mx, this.y + my, this.z + mz, newTile);
	} else {
	    this.meleeAttack(other);
	}
    }

    public String name() {
	return this.name;
    }

    public String nameOf(final Item item) {
	return this.ai.getName(item);
    }

    public void notify(final String message, final Object... params) {
	this.ai.onNotify(String.format(message, params));
    }

    public void pickup() {
	final var item = this.world.item(this.x, this.y, this.z);
	if (this.inventory.isFull() || item == null) {
	    if (this.isPlayer()) {
		Sound.play("failed");
	    }
	    this.doAction("grab at the ground");
	} else {
	    if (this.isPlayer()) {
		Sound.play("grab");
	    }
	    this.doAction("pickup a %s", this.nameOf(item));
	    this.world.remove(this.x, this.y, this.z);
	    this.inventory.add(item);
	}
    }

    private void putAt(final Item item, final int wx, final int wy, final int wz) {
	this.inventory.remove(item);
	this.unequip(item);
	this.world.addAtEmptySpace(item, wx, wy, wz);
    }

    public void quaff(final Item item) {
	this.doAction("quaff a " + this.nameOf(item));
	this.consume(item);
    }

    public void rangedWeaponAttack(final Creature other) {
	if (this.isPlayer()) {
	    Sound.play("shoot");
	}
	this.commonAttack(other, this.attackValue / 2 + this.weapon.rangedAttackValue(),
		"fire a %s at the %s for %d damage", this.nameOf(this.weapon), other.name);
    }

    public Tile realTile(final int wx, final int wy, final int wz) {
	return this.world.tile(wx, wy, wz);
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

    public void saveCreature(final XMLFileWriter writer) throws IOException {
	writer.writeOpeningGroup("creature");
	writer.writeCustomString(Character.toString(this.tile.getStateSymbol()), "tile");
	writer.writeCustomString(this.name, "name");
	writer.writeCustomInt(this.maxHp, "maxHealth");
	writer.writeCustomInt(this.hp, "health");
	writer.writeCustomInt(this.attackValue, "attack");
	writer.writeCustomInt(this.defenseValue, "defense");
	writer.writeCustomInt(this.visionRadius, "vision");
	writer.writeCustomInt(this.maxFood, "maxFood");
	writer.writeCustomInt(this.food, "food");
	writer.writeCustomInt(this.maxMana, "maxMana");
	writer.writeCustomInt(this.mana, "mana");
	writer.writeCustomInt(this.xp, "xp");
	writer.writeCustomInt(this.level, "level");
	writer.writeCustomInt(this.regenHpCooldown, "regenHealthCooldown");
	writer.writeCustomInt(this.regenHpPer1000, "regenHealthPer1000");
	writer.writeCustomInt(this.regenManaCooldown, "regenManaCooldown");
	writer.writeCustomInt(this.regenManaPer1000, "regenManaPer1000");
	final var hasWeapon = this.weapon != null;
	writer.writeCustomBoolean(hasWeapon, "hasWeapon");
	if (hasWeapon) {
	    writer.writeOpeningGroup("weapon");
	    this.weapon.saveItem(writer);
	    writer.writeClosingGroup("weapon");
	}
	final var hasArmor = this.armor != null;
	writer.writeCustomBoolean(hasArmor, "hasArmor");
	if (hasArmor) {
	    writer.writeOpeningGroup("armor");
	    this.armor.saveItem(writer);
	    writer.writeClosingGroup("armor");
	}
	this.inventory.saveInventory(writer);
	writer.writeOpeningGroup("effects");
	final var efSize = this.effects.size();
	writer.writeCustomInt(efSize, "size");
	for (var e = 0; e < efSize; e++) {
	    final var ef = this.effects.get(e);
	    ef.saveEffect(writer);
	}
	writer.writeClosingGroup("effects");
	writer.writeClosingGroup("creature");
    }

    public void setCreatureAi(final CreatureAi newAi) {
	this.ai = newAi;
    }

    public void summon(final Creature other) {
	this.world.add(other);
    }

    private void throwAttack(final Item item, final Creature other) {
	if (this.isPlayer()) {
	    Sound.play("shoot");
	}
	this.commonAttack(other, this.attackValue / 2 + item.thrownAttackValue(), "throw a %s at the %s for %d damage",
		this.nameOf(item), other.name);
	other.addEffect(item.quaffEffect());
    }

    public void throwItem(final Item item, final int wx, final int wy, final int wz) {
	var end = new Point(this.x, this.y, 0);
	for (final Point p : new Line(this.x, this.y, wx, wy)) {
	    if (!this.realTile(p.x, p.y, this.z).isGround()) {
		break;
	    }
	    end = p;
	}
	final var nwx = end.x;
	final var nwy = end.y;
	final var c = this.creature(nwx, nwy, wz);
	if (c != null) {
	    this.throwAttack(item, c);
	} else {
	    this.doAction("throw a %s", this.nameOf(item));
	}
	if (item.quaffEffect() != null && c != null) {
	    this.getRidOf(item);
	} else {
	    this.putAt(item, nwx, nwy, wz);
	}
    }

    public Tile tile() {
	return this.tile;
    }

    public Tile tile(final int wx, final int wy, final int wz) {
	if (this.canSee(wx, wy, wz)) {
	    return this.world.tile(wx, wy, wz);
	}
	return this.ai.rememberedTile(wx, wy, wz);
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

    public int visionRadius() {
	return this.visionRadius;
    }

    public Item weapon() {
	return this.weapon;
    }

    public int xp() {
	return this.xp;
    }
}
