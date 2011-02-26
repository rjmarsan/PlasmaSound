package com.rj.processing.plasmasound.visuals;

import android.view.MotionEvent;

import com.rj.processing.plasmasound.PlasmaSound;

public class AudioStats extends Visual{	
	
	public AudioStats(PlasmaSound p) {
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
	public void touchEvent(MotionEvent me, int i, float x, float y, float vx,
			float vy, float size) {
	}
	  
	  

}
