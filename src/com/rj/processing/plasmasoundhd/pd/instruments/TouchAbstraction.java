package com.rj.processing.plasmasoundhd.pd.instruments;

import java.util.HashMap;

import com.rj.processing.mt.Cursor;
import com.rj.processing.mt.Point;

public class TouchAbstraction {

	public int max;
	public int current = 0;
	
	public HashMap<Cursor, Integer>cursors = new HashMap<Cursor, Integer>();
	Cursor lastcur;
	int lastindex;
	
	public TouchAbstraction(int max) {
		this.max = max;
	}
	
	
	public boolean isInRange(Cursor c, Cursor c2) {
		if (Point.distance(c.currentPoint, c2.currentPoint) < 30f) {
			if (Math.abs(c.currentPoint.time - c2.currentPoint.time) < 1000L)
				return true;
		}
		return false;
	}
	
	public int add(Cursor c) {
		if (lastcur != null && isInRange(lastcur, c)) {
			cursors.put(c, lastindex);
			return lastindex;
		}
		current = (current + 1) % max;
		cursors.put(c, current);
		return current;
	}
	public int move(Cursor c) {
		if (!cursors.containsKey(c)) {
			current = (current + 1) % max;
			cursors.put(c, current);
			return current;
		}
		return cursors.get(c);
	}
	public int remove(Cursor c) {
		if (!cursors.containsKey(c)) return -1;
		int num = cursors.get(c);
		cursors.remove(c);
		lastcur = c;
		lastindex = num;
		return num;
	}
	
	
	
}
