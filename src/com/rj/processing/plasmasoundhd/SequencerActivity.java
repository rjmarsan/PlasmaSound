package com.rj.processing.plasmasoundhd;

import java.util.HashMap;

import org.json.JSONObject;

import processing.core.PApplet;
import processing.core.PFont;
import android.graphics.Point;

import com.rj.processing.mt.Cursor;
import com.rj.processing.plasmasoundhd.sequencer.JSONSequencerPresets;
import com.rj.processing.plasmasoundhd.sequencer.Sequencer;
import com.rj.processing.plasmasoundhd.visuals.AudioStats;

public class SequencerActivity extends PlasmaSubFragment {
	public static String TAG = "Sequencer";

	public SequencerActivity() {
		//ewww
	}
	public SequencerActivity(PDActivity p) {
		super(p);
	}

	


	AudioStats stats;
	public Sequencer sequencer;
	public HashMap<Cursor,PointAndStuff> selectedPoints = new HashMap<Cursor,PointAndStuff>();
	private static class PointAndStuff { Point p; boolean modified; boolean startednow;}
	public boolean loadPresets() { return false; }
	int getMenu() { return com.rj.processing.plasmasounddonate.R.menu.sequencer_menu; }

	PFont font;
	
	@Override
	public void setup() {
		super.setup();
	    stats = new AudioStats(p, p); 
		if (sequencer == null) {
			sequencer = new Sequencer(p.inst, 16, 10, 120);
			updateSequencer();
			JSONSequencerPresets.getPresets().loadDefault(p, sequencer);
		}
		font = p.createFont("americantypewriter.ttf", 28);
		p.textFont(font);
		
		p.textMode(PApplet.MODEL);
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
	public void background() {
		super.background();
		if (sequencer != null) {
			sequencer.stop();
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
	
	private void updateSequencer() {
		if (sequencer != null && p.inst != null) sequencer.setFromSettings(p.inst.sequencer, true);
	}
	
	
	public void clear() {
		sequencer.clear();
	}

	
	
	@Override
	public void touchAllUp(final Cursor c) {
		selectedPoints.clear();
	}
	@Override
	public void touchDown(final Cursor c) {
		Point spot = getSpot(c.currentPoint.x, c.currentPoint.y);
		if (spot != null) {
			PointAndStuff pointnstuff = new PointAndStuff();
			pointnstuff.p = spot;
			pointnstuff.modified = false;
			
			
			int width = sequencer.grid.length;
			int height = sequencer.grid[0].length;
			if (spot.x >= 0 && spot.x < width && spot.y >= 0 && spot.y < height) {
				float value = sequencer.grid[spot.x][spot.y];
				if (value == Sequencer.OFF) {
					value = 1;
					pointnstuff.startednow = true;
				}
				sequencer.setSpot(spot.x, spot.y, value);
				//sequencer.grid[spot.x][spot.y] = value;
			}

			
			
			selectedPoints.put(c, pointnstuff);	
			
		}
	}
	@Override
	public void touchMoved(final Cursor c) {
		Point spot = getSpot(c.currentPoint.x, c.currentPoint.y);
		if (spot == null) return;
		if (selectedPoints.containsKey(c)) {
			if (outsideRange(c)) {
				selectedPoints.get(c).modified = true;
				float xdiff = c.currentPoint.x - c.points.get(c.points.size()-2).x;
				float ydiff = c.currentPoint.y - c.points.get(c.points.size()-2).y;
				ydiff = ydiff / p.height  * -5;
				addToSpot(selectedPoints.get(c).p, ydiff);
			}
		}
	}
	@Override
	public void touchUp(final Cursor c) {
		Point spot = getSpot(c.currentPoint.x, c.currentPoint.y);
		if (spot == null) return;
		if (selectedPoints.containsKey(c) && spot.equals(selectedPoints.get(c).p) && 
				!selectedPoints.get(c).modified && !selectedPoints.get(c).startednow) {
			int width = sequencer.grid.length;
			int height = sequencer.grid[0].length;
			if (spot.x >= 0 && spot.x < width && spot.y >= 0 && spot.y < height) {
				float value = sequencer.grid[spot.x][spot.y];
				if (value != Sequencer.OFF) {
					value = Sequencer.OFF;
				}
				sequencer.setSpot(spot.x, spot.y, value);
				//sequencer.grid[spot.x][spot.y] = value;
			}
		}
		selectedPoints.remove(c);
	}
	
	private boolean outsideRange(Cursor c) {
		if (com.rj.processing.mt.Point.distanceSquared(c.firstPoint, c.currentPoint) > 50) return true;
		return false;
	}
	
	public void addToSpot(Point p, float valueDiff) {
		int x = p.x;
		int y = p.y;
		int width = sequencer.grid.length;
		int height = sequencer.grid[0].length;
		if (x >= 0 && x < width && y >= 0 && y < height) {
			float val = sequencer.grid[x][y];
			val += valueDiff;
			val = Math.min(1,Math.max(0.001f,val));
			sequencer.grid[x][y] = val;
		}
	}
	
	
	public Point getSpot(float x, float y) {
		int gridx = (int)(x / p.width * sequencer.grid.length);
		if (gridx < sequencer.grid.length && gridx >= 0) {
			int gridy = (int)( (p.height-y) / p.height * sequencer.grid[gridx].length);
			if (gridy < sequencer.grid[gridx].length && gridy >= 0) {
				return new Point(gridx,gridy);
			}
		}
		return null;
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
		float barwidth = p.width/grid.length;
		
		/** draw the names of the notes **/
		float barheight = p.height/grid[0].length;
		p.pushStyle();
		p.textAlign(PApplet.CENTER, PApplet.CENTER);
		for (int i=0; i<grid[0].length; i++) {
			p.fill(100);
			p.noStroke();
			p.textSize(barheight/3.5f);
			p.text(Utils.midiNoteToName((int)(sequencer.getNote(i))), p.width-barwidth/2, p.height-(barheight/2 + barheight*i));
		}
		p.popStyle();

		
		for (int i=0; i<grid.length; i++) {
			
			if (sequencer.currentRow == i) {
				p.fill(50);
				p.noStroke();
				p.rect(i*barwidth, 0, barwidth, p.height);
			}
			
			for (int j=0; j<grid[i].length; j++) {
				if (grid[i][j] == Sequencer.OFF) {
					p.fill(100, 30);
					p.stroke(170);
				}  else {
					p.fill(200,60,60,80);
					p.noStroke();
					p.rect(i*barwidth, (grid[i].length - j - 1)*barheight + (barheight-barheight*grid[i][j]), barwidth, barheight*grid[i][j]);
					p.fill(200,30,30, 50);
					p.stroke(170);
				}
				
				p.rect(i*barwidth, (grid[i].length - j - 1)*barheight, barwidth, barheight);

			}

			
		}
		
		
		stats.drawVis();
		
	}
	
	
	
	
	
	
	

}
