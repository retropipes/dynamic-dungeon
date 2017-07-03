package net.dynamicdungeon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class PathFinder {
    private final ArrayList<Point> open;
    private final ArrayList<Point> closed;
    private final HashMap<Point, Point> parents;
    private final HashMap<Point, Integer> totalCost;

    public PathFinder() {
	this.open = new ArrayList<>();
	this.closed = new ArrayList<>();
	this.parents = new HashMap<>();
	this.totalCost = new HashMap<>();
    }

    private int heuristicCost(final Point from, final Point to) {
	return Math.max(Math.abs(from.x - to.x), Math.abs(from.y - to.y));
    }

    private int costToGetTo(final Point from) {
	return this.parents.get(from) == null ? 0 : 1 + this.costToGetTo(this.parents.get(from));
    }

    private int totalCost(final Point from, final Point to) {
	if (this.totalCost.containsKey(from)) {
	    return this.totalCost.get(from);
	}
	final int cost = this.costToGetTo(from) + this.heuristicCost(from, to);
	this.totalCost.put(from, cost);
	return cost;
    }

    private void reParent(final Point child, final Point parent) {
	this.parents.put(child, parent);
	this.totalCost.remove(child);
    }

    public ArrayList<Point> findPath(final Creature creature, final Point start, final Point end, final int maxTries) {
	this.open.clear();
	this.closed.clear();
	this.parents.clear();
	this.totalCost.clear();
	this.open.add(start);
	for (int tries = 0; tries < maxTries && this.open.size() > 0; tries++) {
	    final Point closest = this.getClosestPoint(end);
	    this.open.remove(closest);
	    this.closed.add(closest);
	    if (closest.equals(end)) {
		return this.createPath(start, closest);
	    } else {
		this.checkNeighbors(creature, end, closest);
	    }
	}
	return null;
    }

    private Point getClosestPoint(final Point end) {
	Point closest = this.open.get(0);
	for (final Point other : this.open) {
	    if (this.totalCost(other, end) < this.totalCost(closest, end)) {
		closest = other;
	    }
	}
	return closest;
    }

    private void checkNeighbors(final Creature creature, final Point end, final Point closest) {
	for (final Point neighbor : closest.neighbors8()) {
	    if (this.closed.contains(neighbor)
		    || !creature.canEnter(neighbor.x, neighbor.y, creature.z) && !neighbor.equals(end)) {
		continue;
	    }
	    if (this.open.contains(neighbor)) {
		this.reParentNeighborIfNecessary(closest, neighbor);
	    } else {
		this.reParentNeighbor(closest, neighbor);
	    }
	}
    }

    private void reParentNeighbor(final Point closest, final Point neighbor) {
	this.reParent(neighbor, closest);
	this.open.add(neighbor);
    }

    private void reParentNeighborIfNecessary(final Point closest, final Point neighbor) {
	final Point originalParent = this.parents.get(neighbor);
	final double currentCost = this.costToGetTo(neighbor);
	this.reParent(neighbor, closest);
	final double reparentCost = this.costToGetTo(neighbor);
	if (reparentCost < currentCost) {
	    this.open.remove(neighbor);
	} else {
	    this.reParent(neighbor, originalParent);
	}
    }

    private ArrayList<Point> createPath(final Point start, Point end) {
	final ArrayList<Point> path = new ArrayList<>();
	while (!end.equals(start)) {
	    path.add(end);
	    end = this.parents.get(end);
	}
	Collections.reverse(path);
	return path;
    }
}
