package com.rj.processing.plasmasound;

import java.util.ArrayList;

import android.view.MotionEvent;

import com.rj.processing.plasmasound.visuals.Visual;

public class Visualization {
	final PlasmaSound p;
	
	ArrayList<Visual> visuals;

	public Visualization(PlasmaSound p) {
		this.p = p;
		visuals = new ArrayList<Visual>();
	}
	
	public void addVisual(Visual vis) {
		visuals.add(vis);
	}
	
	public void removeVisuals(Visual vis) {
		visuals.remove(vis);
	}


	public void drawVisuals() {
		p.pushStyle();
		p.colorMode(p.RGB, 255);
		
		for (Visual v : visuals) {
			v.drawVis();
		}
		
		p.popStyle();		
	}

	public void touchEvent(MotionEvent me, int i, float x, float y, float vx,
			float vy, float size) {
		for (Visual v : visuals) {
			v.touchEvent(me, i, x, y, vx, vy, size);
		}
	}


}
