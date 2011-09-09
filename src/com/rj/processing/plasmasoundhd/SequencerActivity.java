package com.rj.processing.plasmasoundhd;

import org.json.JSONObject;

import processing.core.PApplet;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.rj.processing.mt.Cursor;
import com.rj.processing.mt.MTManager;
import com.rj.processing.mt.TouchListener;
import com.rj.processing.plasmasoundhd.sequencer.Sequencer;

public class SequencerActivity extends PDActivity {
	
	public Sequencer sequencer;
	
	public boolean loadPresets() { return false; }
	int getMenu() { return com.rj.processing.plasmasoundhd.R.menu.sequencer_menu; }

	
	@Override
	public void setup() {
		super.setup();
		if (sequencer == null)
			sequencer = new Sequencer(inst, 16, 10, 120);
		sequencer.start();
	}
	
	
	@Override
	public void onPause() {
		super.onPause();
		if (sequencer != null) sequencer.stop();
	}
	
	@Override
	public void onStart() {
		super.onStart();
	    if (sequencer != null) sequencer.start();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateSequencer();
	}
	
	@Override
	public void presetChanged(JSONObject preset) {
	}
	
	public void updateSequencer() {
		if (sequencer != null && inst != null) sequencer.setFromSettings(inst.sequencer);
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
		int gridx = (int)(x / screenWidth * sequencer.grid.length);
		if (gridx < sequencer.grid.length && gridx >= 0) {
			int gridy = (int)( (screenHeight-y) / screenHeight * sequencer.grid[gridx].length);
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
		PApplet p = this;
		background(0);
		if (! pdready) return;
		updateSequencer();
		sequencer.instrument = inst;
		float[][] grid = sequencer.grid;
		for (int i=0; i<grid.length; i++) {
			float barwidth = screenWidth/grid.length;
			float barheight = screenHeight/grid[i].length;
			
			
			if (sequencer.currentRow == i) {
				p.fill(50);
				p.noStroke();
				p.rect(i*barwidth, 0, barwidth, screenHeight);
			}
			
			for (int j=0; j<grid[i].length; j++) {
				if (grid[i][j] <= 0) {
					p.fill(100, 100);
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
