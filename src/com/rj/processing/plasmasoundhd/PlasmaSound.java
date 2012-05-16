package com.rj.processing.plasmasoundhd;

import processing.core.PApplet;
import android.util.Log;
import android.view.MenuItem;

import com.rj.processing.mt.Cursor;
import com.rj.processing.plasmasoundhd.visuals.AudioStats;
import com.rj.processing.plasmasoundhd.visuals.CameraVis;
import com.rj.processing.plasmasoundhd.visuals.Grid;
import com.rj.processing.plasmasoundhd.visuals.PlasmaFluid;


public class PlasmaSound extends PlasmaSubFragment {
	public static String TAG = "PlasmaSound";
	
	public PlasmaSound() {
		//ewww
	}
	public PlasmaSound(PDActivity p) {
		super(p);
		// TODO Auto-generated constructor stub
	}
	


	public Visualization vis;
	boolean settingup = false;

	@Override
	int getMenu() { return com.rj.processing.plasmasound.R.menu.main_menu; }

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
	    //VISUALS CODE
	    vis = new Visualization(p);
	    vis.addVisual(new PlasmaFluid(p, p)); 
	    vis.addVisual(new Grid(p, p)); 
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
		
	}
	@Override
	public void touchDown(final Cursor c) {
		if (p.inst!=null) p.inst.touchDown(null, c.curId, c.currentPoint.x, p.width, c.currentPoint.y, p.height, c);
		if (vis!=null) vis.touchEvent(null, c.curId, c.currentPoint.x, c.currentPoint.y, c.velX, c.velY, 0f, c);
		
	}
	@Override
	public void touchMoved(final Cursor c) {
		if (p.inst!=null) p.inst.touchMove(null, c.curId, c.currentPoint.x, p.width, c.currentPoint.y, p.height, c);
		if (vis!=null) vis.touchEvent(null, c.curId, c.currentPoint.x, c.currentPoint.y, c.velX, c.velY, 0f, c);
	
	}
	@Override
	public void touchUp(final Cursor c) {
		if (p.inst!=null) p.inst.touchUp(null, c.curId, c.currentPoint.x, p.width, c.currentPoint.y, p.height, c);
		if (vis!=null) vis.touchEvent(null, c.curId, c.currentPoint.x, c.currentPoint.y, c.velX, c.velY, 0f, c);
	}
	
	
	
	@Override
	public void draw() {
		if (p.pdready && !settingup) {
		    p.background(0);
		
		    vis.drawVisuals();
		    
		    if (p.frameCount % 1000 == 0) PApplet.println(p.frameRate+"");
		}
	
	}
	
	
	
	

}
