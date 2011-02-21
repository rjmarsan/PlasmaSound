package com.rj.processing.plasmasound.visuals;

import msafluid.MSAFluidSolver2D;
import processing.core.PApplet;
import processing.core.PImage;
import android.view.MotionEvent;

import com.rj.processing.plasmasound.PlasmaSound;

public abstract class Visual {
	final PlasmaSound p;
	final float width;
	final float height;
	

	public Visual(PlasmaSound p) {
		this.p = p;
		this.width = p.width;
		this.height = p.height;
	}
		
	public abstract void drawVis();
	
	
	
	public abstract void touchEvent(MotionEvent me, int i, float x, float y, float vx,
			float vy, float size);

}
