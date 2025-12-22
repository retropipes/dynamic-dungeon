package net.dynamicdungeon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Point {
    public int x;
    public int y;
    public int z;

    public Point(final int ix, final int iy, final int iz) {
	this.x = ix;
	this.y = iy;
	this.z = iz;
    }

    @Override
    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null || !(obj instanceof final Point other) || (this.x != other.x) || (this.y != other.y)) {
	    return false;
	}
	if (this.z != other.z) {
	    return false;
	}
	return true;
    }

    @Override
    public int hashCode() {
	return Objects.hash(this.x, this.y, this.z);
    }

    public List<Point> neighbors8() {
	final List<Point> points = new ArrayList<>();
	for (var ox = -1; ox < 2; ox++) {
	    for (var oy = -1; oy < 2; oy++) {
		if (ox == 0 && oy == 0) {
		    continue;
		}
		points.add(new Point(this.x + ox, this.y + oy, this.z));
	    }
	}
	Collections.shuffle(points);
	return points;
    }
}
