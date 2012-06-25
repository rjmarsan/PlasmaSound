package com.rj.processing.plasmasoundhd;

import processing.core.PApplet;
import android.util.Log;
import android.view.MenuItem;

import com.rj.processing.mt.Cursor;
import com.rj.processing.plasmasoundhd.pd.Note;
import com.rj.processing.plasmasoundhd.pd.NoteInputManager;
import com.rj.processing.plasmasoundhd.pd.NoteInputSource;
import com.rj.processing.plasmasoundhd.pd.instruments.Instrument;
import com.rj.processing.plasmasoundhd.visuals.AudioStats;
import com.rj.processing.plasmasoundhd.visuals.CameraVis;
import com.rj.processing.plasmasoundhd.visuals.Grid;
import com.rj.processing.plasmasoundhd.visuals.PlasmaFluid;


public class PlasmaSound extends PlasmaSubFragment implements NoteInputSource {
	public static String TAG = "PlasmaSound";
	
	private NoteInputManager noteManager;
	
	public PlasmaSound() {
		//ewww
	}
	public PlasmaSound(PDActivity p) {
		super(p);
	    grid = new Grid(p, p, this);
	}
	


	public Visualization vis;
	boolean settingup = false;
	Grid grid;

	@Override
	int getMenu() { return com.rj.processing.plasmasoundhd.R.menu.main_menu; }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d("PlasmaSound", "onOptionsItemSelected called with "+item.getTitle().toString());
		return super.onContextItemSelected(item);
	}	
	
	
	@Override
	public void setup() {
		super.setup();
		if (vis != null)  {
			Log.d(TAG, "For some reason we're calling setup twice?");
			Thread.dumpStack();
		}
		settingup = true;
		p.noteManager.addInputSource(this);
	    //VISUALS CODE
	    vis = new Visualization(p);
	    vis.addVisual(new PlasmaFluid(p, p)); 
	    vis.addVisual(grid); 
	    vis.addVisual(new AudioStats(p, p)); 
	    vis.addVisual(new CameraVis(p, p));
	    settingup = false;
	}
	
	@Override
	public void destroy() {
		super.destroy();
		vis = null;
	}

	@Override
	public void touchAllUp(final Cursor c) {
		if (p.inst!=null) p.inst.allUp();
		if (noteManager != null) noteManager.clear(this);
		
	}
	@Override
	public void touchDown(final Cursor c) {
	    if (p.inst != null) {
    	    float pitch = getPitch(c, c.currentPoint.x, p.width, p.inst);
    	    Note note = new Note(c.curId, Note.PRIMARY_NOTE, pitch, 1-c.currentPoint.y/p.height, this);
    	    note.data = c;
            if (noteManager != null) noteManager.noteOn(note);
	    }
		if (vis!=null) vis.touchEvent(null, c.curId, c.currentPoint.x, c.currentPoint.y, c.velX, c.velY, 0f, c);
		
	}
	@Override
	public void touchMoved(final Cursor c) {
       if (p.inst != null) {
            float pitch = getPitch(c, c.currentPoint.x, p.width, p.inst);
            Note note = new Note(c.curId, Note.PRIMARY_NOTE, pitch, 1-c.currentPoint.y/p.height, this);
            note.data = c;
            if (noteManager != null) noteManager.noteUpdated(note);
            //if (p.inst!=null) p.inst.touchMove(null, c.curId, c.currentPoint.x, p.width, c.currentPoint.y, p.height, c);
        }
		if (vis!=null) vis.touchEvent(null, c.curId, c.currentPoint.x, c.currentPoint.y, c.velX, c.velY, 0f, c);
	
	}
	@Override
	public void touchUp(final Cursor c) {
        if (p.inst != null) {
            float pitch = getPitch(c, c.currentPoint.x, p.width, p.inst);
            Note note = new Note(c.curId, Note.PRIMARY_NOTE, pitch, 1-c.currentPoint.y/p.height, this);
            note.data = c;
            if (noteManager != null) noteManager.noteOff(note);  //p.inst.noteOff(note);
        }
		if (vis!=null) vis.touchEvent(null, c.curId, c.currentPoint.x, c.currentPoint.y, c.velX, c.velY, 0f, c);
	}
	
	
	

    public float getPitch(Cursor c, float x, float width, Instrument inst) {
        float val = x/width;
        float pitch = inst.midiMin + (val * (inst.midiMax - inst.midiMin));
        if (inst.quantize != Instrument.NCONTINUOUS) {
            if (inst.quantize == Instrument.NQUANTIZE || isCursorSnapped(c, width, inst)) {
                pitch = (float) Math.round(pitch); // too close! round!
            }
        }
        return pitch;
    }

    public boolean isCursorSnapped(final Cursor c, final float width, Instrument inst) {
        if (c == null)
            return false;
        final float spacing = (inst.midiMax - inst.midiMin) / width;
        final int firstClosestX = Math.round((c.firstPoint.x) * spacing);
        final int lastClosestX = Math.round((c.currentPoint.x) * spacing);
        if (firstClosestX == lastClosestX) {
            return true;
        } else {
            return false;
        }
    }
	
	
	@Override
	public void draw() {
		if (p.pdready && !settingup) {
		    p.background(0);
		
		    if (vis != null) vis.drawVisuals();
		    
		    if (p.frameCount % 1000 == 0) PApplet.println(p.frameRate+"");
		}
	
	}
    @Override
    public void setManager(NoteInputManager manager) {
        this.noteManager = manager;
        grid.setNoteManager(manager);
    }
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public boolean isConnected() {
        // TODO Auto-generated method stub
        return false;
    }

	
	

}
