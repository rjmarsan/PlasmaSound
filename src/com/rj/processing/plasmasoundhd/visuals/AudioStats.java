package com.rj.processing.plasmasoundhd.visuals;

import android.view.MotionEvent;

import com.rj.processing.plasmasoundhd.PlasmaSound;

public class AudioStats extends Visual{	
	
	public AudioStats(final PlasmaSound p) {
		super(p);
	}

	@Override
	public void drawVis() {
		
		/** Not yet! **/
//		p.pushStyle();
//		p.rectMode(PApplet.CORNER);
//		p.fill(100);
//		p.stroke(150);
//		p.rect(width/2, height/2, 20, p.pdman.getVolumeLevel()+1);
//		p.popStyle();
	}

	@Override
	public void touchEvent(final MotionEvent me, final int i, final float x, final float y, final float vx,
			final float vy, final float size) {
	}
	  
	  

}
