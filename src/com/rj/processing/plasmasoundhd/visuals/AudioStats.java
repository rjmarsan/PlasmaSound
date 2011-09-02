package com.rj.processing.plasmasoundhd.visuals;

import processing.core.PApplet;
import android.view.MotionEvent;

import com.rj.processing.plasmasoundhd.PlasmaActivity;

public class AudioStats extends Visual{	
	PlasmaActivity pa;
	public AudioStats(final PApplet c, PlasmaActivity p) {
		super(c);
		this.pa = p;
	}

	@Override
	public void drawVis() {
		
		/** Not yet! **/
		p.pushStyle();
		p.rectMode(PApplet.CORNER);
		float level = pa.getPD().getVolumeLevel();
		if (level < 90) {
			p.fill(120, 100);
			p.stroke(120, 100);
		} else {
			p.fill(200, 20, 20, 100);
			p.stroke(200, 20, 20, 100);
		}
		p.rect(width-40, 20, 30, level*1.5f);
		p.popStyle();
	}

	@Override
	public void touchEvent(final MotionEvent me, final int i, final float x, final float y, final float vx,
			final float vy, final float size) {
	}
	  
	  

}
