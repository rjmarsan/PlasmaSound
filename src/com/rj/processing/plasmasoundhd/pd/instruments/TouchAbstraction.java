package com.rj.processing.plasmasoundhd.pd.instruments;

import java.util.HashMap;
import java.util.LinkedList;

import com.rj.processing.mt.Point;
import com.rj.processing.plasmasoundhd.pd.Note;

public class TouchAbstraction {
    
//    public static final Point NULL_POINT = new Point(-10000,-10000);
//    public static final int SEQUENCER_OFFSET = -100000;
//    public static final int MOTION_OFFSET    = -200000;
//    public static final int MIDI_OFFSET      = -300000;
//    public static Note sequencerToNoteIndex(int index) {
//        return new Note(NULL_POINT, SEQUENCER_OFFSET + index);
//    }
//    public static Note motionToNoteIndex(int index) {
//        return new Note(NULL_POINT, MOTION_OFFSET + index);
//    }
//    public static Note midiToNoteIndex(int index) {
//        return new Note(NULL_POINT, MIDI_OFFSET + index);
//    }
    
    
	public int max;
	
	public HashMap<Note, Integer> notemap = new HashMap<Note, Integer>();
	public LinkedList<Integer> recentIndexes = new LinkedList<Integer>();
	Note lastcur;
	int lastindex;
	
	public TouchAbstraction(int max) {
		this.max = max;
		for (int i=1; i<=max; i++) {
		    recentIndexes.add(i);
		}
	}
	
	
	public boolean isInRange(Note c, Note c2) {
	    if (1==1) return false; //TODO FIX THIS
		if (Note.distance(c, c2) < 30f) {
			if (Math.abs(c.lastUpdate - c2.lastUpdate) < 1000L)
				return true;
		}
		return false;
	}
	
	public int findOpenIndex() {
	    synchronized (recentIndexes) {
    	    for (int i : recentIndexes) {
    	        if (!notemap.values().contains(i)) {
    	            return i;
    	        }
    	    }
    	    //crap. none of them are gone.
    	    //return the least recently used index.
    	    return recentIndexes.peekFirst();
	    }
	}
	public void useIndex(int index) {
	    synchronized (recentIndexes) {
	        recentIndexes.remove(new Integer(index));
	        recentIndexes.addLast(index);
	    }
	}
	
	public int add(Note c) {
		if (lastcur != null && isInRange(lastcur, c)) {
			notemap.put(c, lastindex);
			return lastindex;
		}
		int index = findOpenIndex();
		notemap.put(c, index);
		useIndex(index);
		return index;
	}
	public int move(Note c) {
		if (!notemap.containsKey(c)) {
			//Log.d("TouchAbstraction", "Can't find "+c+" curid;"+c.curId);
			//first let's search the Notes to see if we have one already with the same curId
			for (Note c2 : notemap.keySet()) {
				if (c2.id == c.id) {
					//Log.d("TouchAbstraction", "Found a replacement: "+c2+" curid;"+c2.curId);
					notemap.put(c, notemap.get(c2));
					return notemap.get(c);
				}
			}
			
			
	        int index = findOpenIndex();
	        notemap.put(c, index);
	        useIndex(index);
	        return index;
		}
		useIndex(notemap.get(c));
		return notemap.get(c);
	}
	public int remove(Note c) {
		if (!notemap.containsKey(c)) return -1;
		int num = notemap.get(c);
		notemap.remove(c);
		lastcur = c;
		lastindex = num;
		return num;
	}
	
	public void allUp() {
		notemap.clear();
	}
	

	
	
}
