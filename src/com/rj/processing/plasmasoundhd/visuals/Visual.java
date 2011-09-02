package com.rj.processing.plasmasoundhd.visuals;

import processing.core.PApplet;
import android.view.MotionEvent;

public abstract class Visual {
	final PApplet p;
	final float width;
	final float height;
	

	public Visual(final PApplet p) {
		this.p = p;
		this.width = p.width;
		this.height = p.height;
	}
		
	public abstract void drawVis();
	
	
	
	public abstract void touchEvent(MotionEvent me, int i, float x, float y, float vx,
			float vy, float size);

}
