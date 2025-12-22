package net.dynamicdungeon.ai;

import java.util.HashMap;
import java.util.Map;

import net.dynamicdungeon.Creature;
import net.dynamicdungeon.Item;
import net.dynamicdungeon.LevelUpController;
import net.dynamicdungeon.Line;
import net.dynamicdungeon.Path;
import net.dynamicdungeon.Point;
import net.dynamicdungeon.world.Tile;

public class CreatureAi {
    protected Creature creature;
    private final Map<String, String> itemNames;

    public CreatureAi(final Creature theCreature) {
	this.creature = theCreature;
	this.creature.setCreatureAi(this);
	this.itemNames = new HashMap<>();
    }

    protected boolean canPickup() {
	return this.creature.item(this.creature.x, this.creature.y, this.creature.z) != null
		&& !this.creature.inventory().isFull();
    }

    protected boolean canRangedWeaponAttack(final Creature other) {
	return this.creature.weapon() != null && this.creature.weapon().rangedAttackValue() > 0
		&& this.creature.canSee(other.x, other.y, other.z);
    }

    public boolean canSee(final int wx, final int wy, final int wz) {
	if (this.creature.z != wz || (this.creature.x - wx) * (this.creature.x - wx) + (this.creature.y - wy)
		* (this.creature.y - wy) > this.creature.visionRadius() * this.creature.visionRadius()) {
	    return false;
	}
	for (final Point p : new Line(this.creature.x, this.creature.y, wx, wy)) {
	    if (this.creature.realTile(p.x, p.y, wz).isGround() || p.x == wx && p.y == wy) {
		continue;
	    }
	    return false;
	}
	return true;
    }

    protected boolean canThrowAt(final Creature other) {
	return this.creature.canSee(other.x, other.y, other.z) && this.getWeaponToThrow() != null;
    }

    protected boolean canUseBetterEquipment() {
	final var currentWeaponRating = this.creature.weapon() == null ? 0
		: this.creature.weapon().attackValue() + this.creature.weapon().rangedAttackValue();
	final var currentArmorRating = this.creature.armor() == null ? 0 : this.creature.armor().defenseValue();
	for (final Item item : this.creature.inventory().getItems()) {
	    if (item == null) {
		continue;
	    }
	    final var isArmor = item.attackValue() + item.rangedAttackValue() < item.defenseValue();
	    if (item.attackValue() + item.rangedAttackValue() > currentWeaponRating
		    || isArmor && item.defenseValue() > currentArmorRating) {
		return true;
	    }
	}
	return false;
    }

    public String getName(final Item item) {
	final var name = this.itemNames.get(item.name());
	return name == null ? item.appearance() : name;
    }

    protected Item getWeaponToThrow() {
	Item toThrow = null;
	for (final Item item : this.creature.inventory().getItems()) {
	    if (item == null || this.creature.weapon() == item || this.creature.armor() == item) {
		continue;
	    }
	    if (toThrow == null || item.thrownAttackValue() > toThrow.attackValue()) {
		toThrow = item;
	    }
	}
	return toThrow;
    }

    public void hunt(final Creature target) {
	final var points = new Path(this.creature, target.x, target.y).points();
	final var mx = points.get(0).x - this.creature.x;
	final var my = points.get(0).y - this.creature.y;
	this.creature.moveBy(mx, my, 0);
    }

    public void onEnter(final int x, final int y, final int z, final Tile tile) {
	if (tile.isGround()) {
	    this.creature.x = x;
	    this.creature.y = y;
	    this.creature.z = z;
	} else {
	    this.creature.doAction("bump into a wall");
	}
    }

    public void onGainLevel() {
	new LevelUpController().autoLevelUp(this.creature);
    }

    /**
     * @param message
     */
    public void onNotify(final String message) {
    }

    public void onUpdate() {
    }

    /**
     * @param wx
     * @param wy
     * @param wz
     */
    public Tile rememberedTile(final int wx, final int wy, final int wz) {
	return Tile.UNKNOWN;
    }

    public void setName(final Item item, final String name) {
	this.itemNames.put(item.name(), name);
    }

    protected void useBetterEquipment() {
	final var currentWeaponRating = this.creature.weapon() == null ? 0
		: this.creature.weapon().attackValue() + this.creature.weapon().rangedAttackValue();
	final var currentArmorRating = this.creature.armor() == null ? 0 : this.creature.armor().defenseValue();
	for (final Item item : this.creature.inventory().getItems()) {
	    if (item == null) {
		continue;
	    }
	    final var isArmor = item.attackValue() + item.rangedAttackValue() < item.defenseValue();
	    if (item.attackValue() + item.rangedAttackValue() > currentWeaponRating
		    || isArmor && item.defenseValue() > currentArmorRating) {
		this.creature.equip(item);
	    }
	}
    }

    public void wander() {
	final var mx = (int) (Math.random() * 3) - 1;
	final var my = (int) (Math.random() * 3) - 1;
	final var other = this.creature.creature(this.creature.x + mx, this.creature.y + my, this.creature.z);
	if (other != null && other.name().equals(this.creature.name())
		|| !this.creature.tile(this.creature.x + mx, this.creature.y + my, this.creature.z).isGround()) {
	    return;
	}
	this.creature.moveBy(mx, my, 0);
    }
}
