package net.dynamicdungeon.ai;

import java.util.List;

import net.dynamicdungeon.Creature;
import net.dynamicdungeon.FieldOfView;
import net.dynamicdungeon.world.Tile;

public class PlayerAi extends CreatureAi {
    private final List<String> messages;
    private final FieldOfView fov;

    public PlayerAi(final Creature theCreature, final List<String> theMessages, final FieldOfView theFOV) {
	super(theCreature);
	this.messages = theMessages;
	this.fov = theFOV;
    }

    @Override
    public boolean canSee(final int wx, final int wy, final int wz) {
	return this.fov.isVisible(wx, wy, wz);
    }

    @Override
    public void onEnter(final int x, final int y, final int z, final Tile tile) {
	if (tile.isGround()) {
	    this.creature.x = x;
	    this.creature.y = y;
	    this.creature.z = z;
	    final var item = this.creature.item(this.creature.x, this.creature.y, this.creature.z);
	    if (item != null) {
		this.creature.notify("There's a " + this.creature.nameOf(item) + " here.");
	    }
	} else if (tile.isDiggable()) {
	    this.creature.dig(x, y, z);
	}
    }

    @Override
    public void onGainLevel() {
    }

    @Override
    public void onNotify(final String message) {
	this.messages.add(message);
    }

    @Override
    public Tile rememberedTile(final int wx, final int wy, final int wz) {
	return this.fov.tile(wx, wy, wz);
    }
}
