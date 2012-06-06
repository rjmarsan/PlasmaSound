package com.rj.processing.plasmasoundhd;

import processing.core.PApplet;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.rj.processing.mt.Cursor;
import com.rj.processing.mt.MTManager;
import com.rj.processing.mt.Point;
import com.rj.processing.mt.TouchListener;
import com.rj.processing.plasmasoundhd.R;
import com.rj.processing.plasmasoundhd.waveform.Waveform;

public class WaveformEditor extends PApplet implements TouchListener {
	public MTManager mtManager;
	
	private Waveform waveform;
	private Waveform originalWave;
	
	
	
	private boolean redraw = true;
	
	
	public int sketchWidth() { return this.screenWidth; }
	public int sketchHeight() { return this.screenHeight; }
	public String sketchRenderer() { return PApplet.OPENGL; }
	  public boolean keepTitlebar() { return Launcher.getUIType() != Launcher.PHONE; }
	
	public void onCreate(final Bundle savedinstance) {
		super.onCreate(savedinstance);
		setupActionBar();
	}
	private void setupActionBar() {
//		ActionBar actionBar = getActionBar();
//		if (actionBar == null) return;
//		System.out.println("Action bar: "+actionBar);
	}
	
	
	
	@Override
	public void setup() {
		hint(DISABLE_DEPTH_TEST);
		hint(DISABLE_OPENGL_ERROR_REPORT);
		hint(PApplet.DISABLE_ACCURATE_TEXTURES);
		hint(PApplet.DISABLE_DEPTH_MASK);
		hint(PApplet.DISABLE_DEPTH_SORT);
	    frameRate(60);
	
	    
	    waveform = new Waveform(2050);
	    for (int i = 0; i<2050; i++) {
	    	waveform.points[i] = (float)Math.sin(i/1000f);
	    }
	    checkIfWaveformTooBig();
	    
	    
	    
	    mtManager = new MTManager();
	    mtManager.addTouchListener(this);
	    debug();
	}
	
	private void checkIfWaveformTooBig() {
		if (waveform.points.length > screenWidth) {
			originalWave = waveform;
			waveform = new Waveform(screenWidth);
			float divisor = (float)originalWave.points.length / (float)waveform.points.length;
			for (int i = 0; i < screenWidth; i++) {
				float val = originalWave.points[(int)(i*divisor)];
				waveform.points[i] = val;
			}
		} else {
			originalWave = null;
		}
	}
	
	
	public void debug() {
		  // Place this inside your setup() method
		  final DisplayMetrics dm = new DisplayMetrics();
		  getWindowManager().getDefaultDisplay().getMetrics(dm);
		  final float density = dm.density; 
		  final int densityDpi = dm.densityDpi;
		  println("density is " + density); 
		  println("densityDpi is " + densityDpi);
		  
		  println("HEY! the screen size is "+width+"x"+height);
	}
	
	
	//mt version
	public boolean surfaceTouchEvent(final MotionEvent me) {
		if (mtManager != null) mtManager.surfaceTouchEvent(me);
		return super.surfaceTouchEvent(me);
	}
	

	@Override
	public void touchAllUp(final Cursor c) {
		
	}
	@Override
	public void touchDown(final Cursor c) {
		//System.out.println("down");
		editWaveformFromCursor(c);
	}
	@Override
	public void touchMoved(final Cursor c) {
		//System.out.println("mov");
		editWaveformFromCursor(c);
	}
	@Override
	public void touchUp(final Cursor c) {
		//System.out.println("upz");
		editWaveformFromCursor(c);
	}
	
	private void editWaveformFromCursor(Cursor c) {
		if (waveform == null || c == null) return;
		if (c.points.size() <= 1) {
			changeWaveformFromPoint(c.currentPoint.x, c.currentPoint.y);
		} else {
			Point curPt = c.points.get(c.points.size()-1);
			Point lastPt = c.points.get(c.points.size()-2);
			interpBetweenTwoPoints(curPt, lastPt);
		}
		redraw = true;
	}
	
	private void interpBetweenTwoPoints(Point curPt, Point lastPt) {
		
		//System.out.println("Interp from ("+curX+","+curY+"), to ("+lastX+","+lastY+")");

		
		//don't interpolate if the points are that close
		if (Math.abs(curPt.x - lastPt.x) < 2) {
			changeWaveformFromPoint(curPt.x, curPt.y);
			changeWaveformFromPoint(lastPt.x, lastPt.y);
			return;
		}
		
		float x1 = curPt.x, y1 = curPt.y;
		float x2 = lastPt.x, y2 = lastPt.y;
		if (curPt.x < lastPt.x) {
			x1 = lastPt.x; y1 = lastPt.y; 
			x2 = curPt.x; y2 = curPt.y;
		}
		float diff = (x1-x2);
		for (float xoff = 0; xoff <= (int)diff; xoff++ ) {
			float interp = (xoff/diff);
			float y = y2*interp + y1*(1-interp);
			changeWaveformFromPoint((float)Math.floor(x1)-xoff, y);
		}
	}
	
	private void changeWaveformFromPoint(float px, float py) {
		float x = px;
		float y = (py/(screenHeight/-2))+1;
		float width = screenWidth/waveform.points.length;
		int position = (int)(x/width);
		//System.out.println("changing ("+px+","+py+") at["+position+"] to "+y+"");
		if (position < waveform.points.length)
			waveform.points[position] = y;
		else
			System.out.println("WTF got a position out of bounds");
	}
	
	
	@Override
	public void draw() {
//		if (!redraw) return;
		PApplet p = this;
		background(0);
		p.stroke(128);
		p.line(0, screenHeight/2, screenWidth, screenHeight/2);
		if (waveform != null && waveform.points != null) {
			float barwidth = screenWidth / waveform.points.length;
			float num = 0;
			for (float point : waveform.points) {
				p.fill(200,30,30,100);
				p.noStroke();
				p.rect(num*barwidth, screenHeight/2, barwidth, (screenHeight/2)*-point);
				num++;
			}
		}
		redraw = false;
	}
	
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
	    final MenuInflater inflater = getMenuInflater();
	    inflater.inflate(com.rj.processing.plasmasoundhd.R.menu.main_menu, menu);
	    return true;
	}
	
	/**
	 * its 1:42, and where is Jake?
	 * Rj is goofy as fuck.
	 */
	
	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
	    switch (item.getItemId()) {
	    case com.rj.processing.plasmasoundhd.R.id.instrument_settings:
	        return true;
	    case com.rj.processing.plasmasoundhd.R.id.effects_settings:
	        return true;
	    case com.rj.processing.plasmasoundhd.R.id.save_settings:
	        return true;
	    case com.rj.processing.plasmasoundhd.R.id.load_settings:
	        return true;

	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	



}
