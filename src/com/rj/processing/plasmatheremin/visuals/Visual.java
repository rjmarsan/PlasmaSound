package com.rj.processing.plasmatheremin.visuals;

import msafluid.MSAFluidSolver2D;
import processing.core.PApplet;
import processing.core.PImage;
import android.view.MotionEvent;

import com.rj.processing.plasmatheremin.PlasmaTheremin;

public abstract class Visual {
	final PlasmaTheremin p;
	final float width;
	final float height;
	

	public Visual(PlasmaTheremin p) {
		this.p = p;
		this.width = p.width;
		this.height = p.height;
	}
		
	public abstract void drawVis();
	
	
	
	public abstract void touchEvent(MotionEvent me, int i, float x, float y, float vx,
			float vy, float size);

}
