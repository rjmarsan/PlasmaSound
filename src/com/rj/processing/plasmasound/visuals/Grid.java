package com.rj.processing.plasmasound.visuals;

import processing.core.PApplet;
import android.view.MotionEvent;

import com.rj.processing.mt.Cursor;
import com.rj.processing.plasmasound.PlasmaSound;
import com.rj.processing.plasmasound.pd.instruments.Instrument;

public class Grid extends Visual{
	private final int num_lines = 10;
	private final float crosshair_size = 100;
	
	
	public Grid(final PlasmaSound p) {
		super(p);
	}

	@Override
	public void drawVis() {
		p.rectMode(PApplet.CORNER);

		float midiMax = 86;
		float midiMin = 70;
		if (p.inst != null && p.inst.ready) {
			midiMax = p.inst.midiMax;
			midiMin = p.inst.midiMin;
		}
		final float num_lines = midiMax-midiMin;
		final float spacing = width/(num_lines);
		for (int i=0;i<num_lines;i++) {
			final int space = (int) ((i+midiMin) % 12);
			if (space == 0) {
				p.stroke(200, 100);
				p.fill(100,100);
				//p.line(spacing*i, 0, spacing*i, height);
				p.rect(spacing*i, 0, 2, height);

			} else if (space == 1 || space == 3 || space == 6 || space == 8 || space == 10) {//a black note! 
				p.stroke(100, 100);
				p.line(spacing * i, 0, spacing * i, height);
			} else {
				p.stroke(200, 100);
				p.line(spacing * i, 0, spacing * i, height);
			}

		}
		synchronized (p.mtManager.cursors) {
			p.stroke(255,0,0,180);
			int quantize = Instrument.NCONTINUOUS;
			if (p.inst != null && p.inst.ready) {
				quantize = p.inst.quantize;
			}
			if (quantize == Instrument.NCONTINUOUS) {
				for (final Cursor c : p.mtManager.cursors) {
					if (c != null && c.currentPoint != null) {
						p.line(c.currentPoint.x-crosshair_size, c.currentPoint.y, c.currentPoint.x+crosshair_size, c.currentPoint.y);
						p.line(c.currentPoint.x, c.currentPoint.y-crosshair_size, c.currentPoint.x, c.currentPoint.y+crosshair_size);
					}
				}
			}
			else {
				p.fill(255,0,0,50);
				for (final Cursor c : p.mtManager.cursors) {
					if (c != null && c.currentPoint != null) {
						if (quantize == Instrument.NQUANTIZE || p.inst.isCursorSnapped(c,width)) {
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

	@Override
	public void touchEvent(final MotionEvent me, final int i, final float x, final float y, final float vx,
			final float vy, final float size) {
	}
	  
	  

}
