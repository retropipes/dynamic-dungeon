package net.dynamicdungeon;

public class Inventory {
    private final Item[] items;

    public Item[] getItems() {
	return this.items;
    }

    public Item get(final int i) {
	return this.items[i];
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
}
