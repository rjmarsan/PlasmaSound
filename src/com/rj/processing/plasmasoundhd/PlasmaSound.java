package com.rj.processing.plasmasoundhd;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.rj.processing.mt.Cursor;
import com.rj.processing.plasmasoundhd.visuals.AudioStats;
import com.rj.processing.plasmasoundhd.visuals.Grid;
import com.rj.processing.plasmasoundhd.visuals.PlasmaFluid;


public class PlasmaSound extends PDActivity {
	
	public Visualization vis;

	
	
	
	@Override
	public void setup() {
		super.setup();
		
	    //VISUALS CODE
	    vis = new Visualization(this);
	    vis.addVisual(new PlasmaFluid(this)); 
	    vis.addVisual(new Grid(this, this)); 
	    vis.addVisual(new AudioStats(this, this)); 
	}
	
	

	@Override
	public void touchAllUp(final Cursor c) {
		if (inst!=null) inst.allUp();
		
	}
	@Override
	public void touchDown(final Cursor c) {
		if (inst!=null) inst.touchDown(null, c.curId, c.currentPoint.x, width, c.currentPoint.y, height, c);
		if (vis!=null) vis.touchEvent(null, c.curId, c.currentPoint.x, c.currentPoint.y, c.velX, c.velY, 0f, c);
		
	}
	@Override
	public void touchMoved(final Cursor c) {
		if (inst!=null) inst.touchMove(null, c.curId, c.currentPoint.x, width, c.currentPoint.y, height, c);
		if (vis!=null) vis.touchEvent(null, c.curId, c.currentPoint.x, c.currentPoint.y, c.velX, c.velY, 0f, c);
	
	}
	@Override
	public void touchUp(final Cursor c) {
		if (inst!=null) inst.touchUp(null, c.curId, c.currentPoint.x, width, c.currentPoint.y, height, c);
		if (vis!=null) vis.touchEvent(null, c.curId, c.currentPoint.x, c.currentPoint.y, c.velX, c.velY, 0f, c);
	}
	
	
	
	@Override
	public void draw() {
		if (pdready) {
		    background(0);
		
		    vis.drawVisuals();
		    
	//	    if (this.frameCount % 100 == 0) println(this.frameRate+"");
		}
	
	}
	
	
	
	

}
