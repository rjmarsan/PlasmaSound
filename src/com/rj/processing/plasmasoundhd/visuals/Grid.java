package com.rj.processing.plasmasoundhd.visuals;

import processing.core.PApplet;
import android.view.MotionEvent;

import com.rj.processing.mt.Cursor;
import com.rj.processing.plasmasoundhd.PlasmaActivity;
import com.rj.processing.plasmasoundhd.PlasmaSound;
import com.rj.processing.plasmasoundhd.pd.Note;
import com.rj.processing.plasmasoundhd.pd.NoteInputManager;
import com.rj.processing.plasmasoundhd.pd.instruments.Instrument;

public class Grid extends Visual{
	private final float crosshair_size = 100;
	private final static int NUM_DASHES = 9;
	PlasmaActivity pp;
	PlasmaSound keyboard;
	NoteInputManager noteman;
	//PImage dashed;
//	PGraphics cache;
//	PImage cacheImage;
	float midiMax;
	float midiMin;
	
	
	public Grid(final PApplet p, PlasmaActivity pp, PlasmaSound keyboard) {
		super(p);
		this.pp = pp;
		this.keyboard = keyboard;
		//dashed = p.loadImage("dashed.png");
	}
	
	public void setNoteManager(NoteInputManager noteman) {
	    this.noteman = noteman;
	}

	@Override
	public void drawVis() {
		float midiMax = pp.getInst().midiMax;
		float midiMin = pp.getInst().midiMin;
		
		drawVis(p);
		this.midiMin = midiMin;
		this.midiMax = midiMax;
		
		
		final float num_lines = midiMax-midiMin;
		final float spacing = p.width/(num_lines);

		if (noteman == null) return;
		synchronized (noteman.currentNotes) {
			p.stroke(255,0,0,180);
            p.fill(255,0,0,50);
			int quantize = Instrument.NCONTINUOUS;
			if (pp.getInst() != null && pp.getInst().ready) {
				quantize = pp.getInst().quantize;
			}
            for (final Note note : noteman.currentNotes) {
                if (note == null || !note.path.equals(Note.PRIMARY_NOTE)) continue;
                final float val = (note.notevalue-midiMin)/(midiMax-midiMin);
				if (note.source.equals(keyboard) && 
				        (quantize == Instrument.NCONTINUOUS || 
				         (quantize == Instrument.NSLIDE && 
				         !keyboard.isCursorSnapped((Cursor)note.data,p.width,pp.getInst())) )) {
                    final float x = val*p.width;
                    final float y = note.controlvalue*p.height;
                    p.line(x-crosshair_size, y, x+crosshair_size, y);
                    p.line(x, y-crosshair_size, x, y+crosshair_size);
				} else {
                    final float x = val*p.width+spacing/2;
                    final int s = (int) (x/spacing);
                    p.rect(spacing*s-spacing/2f, 0, spacing, p.height);
				}
			}
			
		}

	}
	
	public void drawVis(PApplet p) {
		p.rectMode(PApplet.CORNER);

		float midiMax = 86;
		float midiMin = 70;
		if (pp.getInst() != null && pp.getInst().ready) {
			midiMax = pp.getInst().midiMax;
			midiMin = pp.getInst().midiMin;
		}
		final float num_lines = midiMax-midiMin;
		final float spacing = p.width/(num_lines);
		for (int i=0;i<num_lines;i++) {
			final int space = (int) ((i+midiMin) % 12);
			if (space == 0) {
				p.fill(200,100);
				p.noStroke();
				//p.line(spacing*i, 0, spacing*i, height);
				p.rect(spacing*i, 0, 3, p.height);

			} else if (space == 1 || space == 3 || space == 6 || space == 8 || space == 10) {//a black note! 
				p.stroke(100, 100);
				if (num_lines < 30) { //don't bother drawing if there's too much stuff on screen
					for (int q = 0; q<NUM_DASHES; q++) {
						p.line(spacing * i, q*(p.height/NUM_DASHES)-20, spacing * i, (q+1)*(p.height/NUM_DASHES));
						//p.image(dashed, spacing * i, 0);
					}
				} else if (num_lines < 50) {
					p.line(spacing * i, 0, spacing * i, p.height);
				}
			} else {
				p.stroke(183, 100);
				p.line(spacing * i, 0, spacing * i, p.height);
			}

		}
	}

	@Override
	public void touchEvent(final MotionEvent me, final int i, final float x, final float y, final float vx,
			final float vy, final float size) {
	}
	  
	  

}
