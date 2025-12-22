package net.dynamicdungeon;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Line implements Iterable<Point> {
    private final List<Point> points;

    public Line(final int ix0, final int iy0, final int x1, final int y1) {
	this.points = new ArrayList<>();
	var x0 = ix0;
	var y0 = iy0;
	final var dx = Math.abs(x1 - x0);
	final var dy = Math.abs(y1 - y0);
	final var sx = x0 < x1 ? 1 : -1;
	final var sy = y0 < y1 ? 1 : -1;
	var err = dx - dy;
	while (true) {
	    this.points.add(new Point(x0, y0, 0));
	    if (x0 == x1 && y0 == y1) {
		break;
	    }
	    final var e2 = err * 2;
	    if (e2 > -dx) {
		err -= dy;
		x0 += sx;
	    }
	    if (e2 < dx) {
		err += dx;
		y0 += sy;
	    }
	}
    }

    public List<Point> getPoints() {
	return this.points;
    }

    @Override
    public Iterator<Point> iterator() {
	return this.points.iterator();
    }
}
