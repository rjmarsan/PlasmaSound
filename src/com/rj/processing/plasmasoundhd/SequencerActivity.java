package com.rj.processing.plasmasoundhd;

import org.json.JSONObject;

import processing.core.PApplet;

import com.rj.processing.mt.Cursor;
import com.rj.processing.plasmasoundhd.sequencer.Sequencer;

public class SequencerActivity extends PlasmaSubFragment {
	public static String TAG = "Sequencer";

	public SequencerActivity(PDActivity p) {
		super(p);
	}




	public Sequencer sequencer;
	
	public boolean loadPresets() { return false; }
	int getMenu() { return com.rj.processing.plasmasound.R.menu.sequencer_menu; }

	
	@Override
	public void setup() {
		super.setup();
		if (sequencer == null)
			sequencer = new Sequencer(p.inst, 16, 10, 120);
		sequencer.start();
	}
	

	@Override
	public void destroy() {
		super.destroy();
		if (sequencer != null) {
			sequencer.stop();
			sequencer = null;
		}
	}
	
	
	@Override
	public void pause() {
		super.onPause();
		if (sequencer != null) sequencer.stop();
	}
	
	@Override
	public void start() {
		super.onStart();
	    if (sequencer != null) sequencer.start();
	}
	
	@Override
	protected void resume() {
		super.onResume();
		updateSequencer();
	}
	
	@Override
	public void presetChanged(JSONObject preset) {
	}
	
	public void updateSequencer() {
		if (sequencer != null && p.inst != null) sequencer.setFromSettings(p.inst.sequencer);
	}

	
	
	@Override
	public void touchAllUp(final Cursor c) {
		
	}
	@Override
	public void touchDown(final Cursor c) {
	}
	@Override
	public void touchMoved(final Cursor c) {
	}
	@Override
	public void touchUp(final Cursor c) {
		selectSpot(c.currentPoint.x, c.currentPoint.y);
	}
	
	
	public void selectSpot(float x, float y) {
		int gridx = (int)(x / p.width * sequencer.grid.length);
		if (gridx < sequencer.grid.length && gridx >= 0) {
			int gridy = (int)( (p.height-y) / p.height * sequencer.grid[gridx].length);
			if (gridy < sequencer.grid[gridx].length && gridy >= 0) {
				float value = sequencer.grid[gridx][gridy];
				if (value <= 0) {
					value = 1;
				} else {
					value = 0;
				}
				sequencer.grid[gridx][gridy] = value;
			}
		}
	}
	
	
	
	
	@Override
	public void draw() {
		if (sequencer == null) return;
		Sequencer sequencer = this.sequencer;
		p.background(0);
		if (! p.pdready) return;
		updateSequencer();
		sequencer.instrument = p.inst;
		//p.resetMatrix();
		p.rectMode(PApplet.CORNER);
		p.ellipseMode(PApplet.CORNER);

		float[][] grid = sequencer.grid;
		for (int i=0; i<grid.length; i++) {
			float barwidth = p.width/grid.length;
			float barheight = p.height/grid[i].length;

			
			if (sequencer.currentRow == i) {
				p.fill(50);
				p.noStroke();
				p.rect(i*barwidth, 0, barwidth, p.height);
			}
			
			for (int j=0; j<grid[i].length; j++) {
				if (grid[i][j] <= 0) {
					p.fill(100, 30);
					p.stroke(170);
				}  else {
					p.fill(200,30,30, 100);
					p.stroke(170);
				}
				
				p.rect(i*barwidth, (grid[i].length - j - 1)*barheight, barwidth, barheight);

			}

			
		}
		
	}
	
	
	
	

}
