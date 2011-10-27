package com.rj.processing.plasmasoundhd.visuals;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import android.view.MotionEvent;

import com.rj.processing.mt.Cursor;
import com.rj.processing.plasmasoundhd.PlasmaActivity;
import com.rj.processing.plasmasoundhd.pd.instruments.Instrument;

public class Grid extends Visual{
	private final float crosshair_size = 100;
	private final static int NUM_DASHES = 9;
	PlasmaActivity pp;
	//PImage dashed;
	PGraphics cache;
	PImage cacheImage;
	float midiMax;
	float midiMin;
	
	
	public Grid(final PApplet p, PlasmaActivity pp) {
		super(p);
		this.pp = pp;
		//dashed = p.loadImage("dashed.png");
	}

	@Override
	public void drawVis() {
		float midiMax = pp.getInst().midiMax;
		float midiMin = pp.getInst().midiMin;
		if (cacheImage == null || this.midiMin != midiMin || this.midiMax != midiMax) {
			cache = p.createGraphics((int)width, (int)height, PApplet.P2D);
			//p.println("NEW IMAGE NEW GIRD NEW GRID NEW GRID NEW GRID ");
			cache.beginDraw();
			//cache.background(0, 0, 0, 0);
			drawVis(cache);
			cache.endDraw();
			if (cacheImage != null) cacheImage.delete();
			cacheImage = cache.get();
			this.midiMin = midiMin;
			this.midiMax = midiMax;
		}
		p.image(cacheImage, 0, 0);
		
		
		final float num_lines = midiMax-midiMin;
		final float spacing = width/(num_lines);

		synchronized (pp.getMTManager().cursors) {
			p.stroke(255,0,0,180);
			int quantize = Instrument.NCONTINUOUS;
			if (pp.getInst() != null && pp.getInst().ready) {
				quantize = pp.getInst().quantize;
			}
			if (quantize == Instrument.NCONTINUOUS) {
				for (final Cursor c : pp.getMTManager().cursors) {
					if (c != null && c.currentPoint != null) {
						p.line(c.currentPoint.x-crosshair_size, c.currentPoint.y, c.currentPoint.x+crosshair_size, c.currentPoint.y);
						p.line(c.currentPoint.x, c.currentPoint.y-crosshair_size, c.currentPoint.x, c.currentPoint.y+crosshair_size);
					}
				}
			}
			else {
				p.fill(255,0,0,50);
				for (final Cursor c : pp.getMTManager().cursors) {
					if (c != null && c.currentPoint != null) {
						if (quantize == Instrument.NQUANTIZE || pp.getInst().isCursorSnapped(c,width)) {
							final float x = c.currentPoint.x+spacing/2;
							final int s = (int) (x/spacing);
							p.rect(spacing*s-spacing/2f, 0, spacing, p.height);
						} else {
							p.line(c.currentPoint.x-crosshair_size, c.currentPoint.y, c.currentPoint.x+crosshair_size, c.currentPoint.y);
							p.line(c.currentPoint.x, c.currentPoint.y-crosshair_size, c.currentPoint.x, c.currentPoint.y+crosshair_size);
						}
					}
				}
			}
		}

	}
	
	public void drawVis(PGraphics p) {
		p.rectMode(PApplet.CORNER);

		float midiMax = 86;
		float midiMin = 70;
		if (pp.getInst() != null && pp.getInst().ready) {
			midiMax = pp.getInst().midiMax;
			midiMin = pp.getInst().midiMin;
		}
		final float num_lines = midiMax-midiMin;
		final float spacing = width/(num_lines);
		for (int i=0;i<num_lines;i++) {
			final int space = (int) ((i+midiMin) % 12);
			if (space == 0) {
				p.fill(200,100);
				p.noStroke();
				//p.line(spacing*i, 0, spacing*i, height);
				p.rect(spacing*i, 0, 3, height);

			} else if (space == 1 || space == 3 || space == 6 || space == 8 || space == 10) {//a black note! 
				p.stroke(100, 100);
				for (int q = 0; q<NUM_DASHES; q++) {
					p.line(spacing * i, q*(height/NUM_DASHES)-20, spacing * i, (q+1)*(height/NUM_DASHES));
					//p.image(dashed, spacing * i, 0);
				}
			} else {
				p.stroke(183, 100);
				p.line(spacing * i, 0, spacing * i, height);
			}

		}
	}

	@Override
	public void touchEvent(final MotionEvent me, final int i, final float x, final float y, final float vx,
			final float vy, final float size) {
	}
	  
	  

}
