package com.rj.processing.plasmasoundhd;

import java.util.ArrayList;

import processing.core.PApplet;
import android.view.MotionEvent;

import com.rj.processing.mt.Cursor;
import com.rj.processing.plasmasounddonate.R;
import com.rj.processing.plasmasoundhd.visuals.Visual;

public class Visualization {
	final PApplet p;
	
	ArrayList<Visual> visuals;

	public Visualization(final PApplet p) {
		this.p = p;
		visuals = new ArrayList<Visual>();
	}
	
	public void addVisual(final Visual vis) {
		visuals.add(vis);
	}
	
	public void removeVisuals(final Visual vis) {
		visuals.remove(vis);
	}


	public void drawVisuals() {
		p.pushStyle();
		p.colorMode(PApplet.RGB, 255);
		
		for (final Visual v : visuals) {
			v.drawVis();
		}
		
		p.popStyle();		
	}

	public void touchEvent(final MotionEvent me, final int i, final float x, final float y, final float vx,
			final float vy, final float size, final Cursor c) {
		for (final Visual v : visuals) {
			v.touchEvent(me, i, x, y, vx, vy, size);
		}
	}


}
