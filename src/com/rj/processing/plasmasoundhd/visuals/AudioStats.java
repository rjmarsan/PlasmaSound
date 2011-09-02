package com.rj.processing.plasmasoundhd.visuals;

import org.json.JSONException;

import processing.core.PApplet;
import processing.core.PFont;
import android.view.MotionEvent;

import com.rj.processing.plasmasoundhd.PlasmaActivity;
import com.rj.processing.plasmasoundhd.pd.instruments.JSONPresets;

public class AudioStats extends Visual{	
	PlasmaActivity pa;
	PFont font;
	
	public AudioStats(final PApplet c, PlasmaActivity p) {
		super(c);
		this.pa = p;
		font = c.loadFont("AmericanTypewriter-24.vlw");
		c.textFont(font);
	}

	@Override
	public void drawVis() {
		
		/** Not yet! **/
		p.pushStyle();
		p.rectMode(PApplet.CORNER);
		float level = pa.getPD().getVolumeLevel();
		if (level < 95) {
			p.fill(120, 100);
			p.stroke(120, 100);
		} else {
			p.fill(200, 20, 20, 100);
			p.stroke(200, 20, 20, 100);
		}
		p.rect(width-40, 45, 30, level*1.5f);
		
		p.fill(200, 140);
		p.stroke(200, 140);
		p.textAlign(PApplet.RIGHT, PApplet.TOP);
		if (JSONPresets.getPresets().getCurrent() != null) {
			try {
				String name = JSONPresets.getPresets().getCurrent().getString("name");
				p.text("Preset: "+name, width-10, 10);
				
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		
		p.popStyle();
		
		
		
		
	}

	@Override
	public void touchEvent(final MotionEvent me, final int i, final float x, final float y, final float vx,
			final float vy, final float size) {
	}
	  
	  

}
