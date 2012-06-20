package com.rj.processing.plasmasoundhd.pd.instruments;

import java.util.HashMap;
import java.util.LinkedList;

import com.rj.processing.mt.Cursor;
import com.rj.processing.mt.Point;

public class TouchAbstraction {
    
    public static final Point NULL_POINT = new Point(-10000,-10000);
    public static final int SEQUENCER_OFFSET = -100000;
    public static final int MOTION_OFFSET    = -200000;
    public static final int MIDI_OFFSET      = -300000;
    public static Cursor sequencerToCursorIndex(int index) {
        return new Cursor(NULL_POINT, SEQUENCER_OFFSET + index);
    }
    public static Cursor motionToCursorIndex(int index) {
        return new Cursor(NULL_POINT, MOTION_OFFSET + index);
    }
    public static Cursor midiToCursorIndex(int index) {
        return new Cursor(NULL_POINT, MIDI_OFFSET + index);
    }
    
    
	public int max;
	
	public HashMap<Cursor, Integer>cursors = new HashMap<Cursor, Integer>();
	public LinkedList<Integer> recentIndexes = new LinkedList<Integer>();
	Cursor lastcur;
	int lastindex;
	
	public TouchAbstraction(int max) {
		this.max = max;
		for (int i=1; i<=max; i++) {
		    recentIndexes.add(i);
		}
	}
	
	
	public boolean isInRange(Cursor c, Cursor c2) {
		if (Point.distance(c.currentPoint, c2.currentPoint) < 30f) {
			if (Math.abs(c.currentPoint.time - c2.currentPoint.time) < 1000L)
				return true;
		}
		return false;
	}
	
	public int findOpenIndex() {
	    for (int i : recentIndexes) {
	        if (!cursors.values().contains(i)) {
	            return i;
	        }
	    }
	    //crap. none of them are gone.
	    //return the least recently used index.
	    return recentIndexes.peekFirst();
	}
	public void useIndex(int index) {
	    recentIndexes.remove(new Integer(index));
	    recentIndexes.addLast(index);
	}
	
	public int add(Cursor c) {
		if (lastcur != null && isInRange(lastcur, c)) {
			cursors.put(c, lastindex);
			return lastindex;
		}
		int index = findOpenIndex();
		cursors.put(c, index);
		useIndex(index);
		return index;
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
			
			
	        int index = findOpenIndex();
	        cursors.put(c, index);
	        useIndex(index);
	        return index;
		}
		useIndex(cursors.get(c));
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
