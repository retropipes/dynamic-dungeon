package net.dynamicdungeon;

import java.io.IOException;

import net.dynamicdungeon.fileio.XMLFileReader;
import net.dynamicdungeon.fileio.XMLFileWriter;

public class Inventory {
    private Item[] items;

    public Item[] getItems() {
	return this.items;
    }

    public Item get(final int i) {
	return this.items[i];
    }

    public Inventory() {
	// Create an empty inventory, to be populated later
    }

    public Inventory(final int max) {
	this.items = new Item[max];
    }

    public void add(final Item item) {
	for (int i = 0; i < this.items.length; i++) {
	    if (this.items[i] == null) {
		this.items[i] = item;
		break;
	    }
	}
    }

    public void remove(final Item item) {
	for (int i = 0; i < this.items.length; i++) {
	    if (this.items[i] == item) {
		this.items[i] = null;
		return;
	    }
	}
    }

    public boolean isFull() {
	int size = 0;
	for (final Item item : this.items) {
	    if (item != null) {
		size++;
	    }
	}
	return size == this.items.length;
    }

    public boolean contains(final Item item) {
	for (final Item i : this.items) {
	    if (i == item) {
		return true;
	    }
	}
	return false;
    }

    public void loadInventory(final XMLFileReader reader) throws IOException {
	reader.readOpeningGroup("inventory");
	int iSize = reader.readCustomInt("size");
	this.items = new Item[iSize];
	for (int i = 0; i < iSize; i++) {
	    boolean exists = reader.readCustomBoolean("exists");
	    if (exists) {
		Item it = new Item();
		it.loadItem(reader);
		this.items[i] = it;
	    }
	}
	reader.readClosingGroup("inventory");
    }

    public void saveInventory(final XMLFileWriter writer) throws IOException {
	writer.writeOpeningGroup("inventory");
	int iSize = this.items.length;
	writer.writeCustomInt(iSize, "size");
	for (int i = 0; i < iSize; i++) {
	    boolean exists = (this.items[i] != null);
	    writer.writeCustomBoolean(exists, "exists");
	    if (exists) {
		this.items[i].saveItem(writer);
	    }
	}
	writer.writeClosingGroup("rows");
	writer.writeClosingGroup("inventory");
    }
}
