package com.rj.processing.plasmasoundhd.pd.instruments;

import java.util.HashMap;

import android.util.Log;

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
		cursors.put(c, current+1);
		return current+1;
	}
	public int move(Cursor c) {
		if (!cursors.containsKey(c)) {
			//Log.d("TouchAbstraction", "Can't find "+c+" curid;"+c.curId);
			//first let's search the cursors to see if we have one already with the same curId
			for (Cursor c2 : cursors.keySet()) {
				if (c2.curId == c.curId) {
					//Log.d("TouchAbstraction", "Found a replacement: "+c2+" curid;"+c2.curId);
					cursors.put(c, cursors.get(c2));
					return cursors.get(c);
				}
			}
			
			
			current = (current + 1) % max;
			cursors.put(c, current+1);
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
	
	public void allUp() {
		cursors.clear();
	}
	
	
	
}
