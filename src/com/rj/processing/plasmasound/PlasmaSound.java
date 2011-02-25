package com.rj.processing.plasmasound;

import processing.core.PApplet;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.rj.processing.mt.MTCallback;
import com.rj.processing.mt.MTManager;
import com.rj.processing.plasmasound.pd.PDManager;
import com.rj.processing.plasmasound.pd.instruments.Instrument;
import com.rj.processing.plasmasound.visuals.AudioStats;
import com.rj.processing.plasmasound.visuals.Grid;
import com.rj.processing.plasmasound.visuals.PlasmaFluid;

public class PlasmaSound extends PApplet implements MTCallback {

public static final String SHARED_PREFERENCES_AUDIO = "shared_prefs_audio";



public MTManager mtManager;

public Visualization vis;
public PDManager pdman;
public Instrument inst;


boolean touchupdated = false;
boolean pdready = false;
Runnable readyrunnable = new Runnable() {
	public void run() {
		pdready = true;
	}
};

public int sketchWidth() { return this.screenWidth; }
public int sketchHeight() { return this.screenHeight; }
public String sketchRenderer() { return PApplet.OPENGL; }


View loadingview;

public void onCreate(Bundle savedinstance) {
	super.onCreate(savedinstance);
	loadingview = this.getLayoutInflater().inflate(com.rj.processing.plasmasound.R.layout.loadingscreen, null);
	this.addContentView(loadingview, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
}




public void setup() {
	hint(DISABLE_DEPTH_TEST);
	hint(DISABLE_OPENGL_ERROR_REPORT);
	hint(PApplet.DISABLE_ACCURATE_TEXTURES);
	hint(PApplet.DISABLE_DEPTH_MASK);
	hint(PApplet.DISABLE_DEPTH_SORT);
    frameRate(60);

    mtManager = new MTManager(this);
    
    //VISUALS CODE
    vis = new Visualization(this);
    vis.addVisual(new PlasmaFluid(this)); 
    vis.addVisual(new Grid(this)); 
    vis.addVisual(new AudioStats(this)); 
    
    asyncSetup.execute(new Void[0]);
    debug();
}
AsyncTask<Void,Void,Void> asyncSetup = new AsyncTask<Void,Void,Void>() {
	@Override
	protected Void doInBackground(Void... params) {
		Log.v("PlasmaSoundSetup", "creating pd");
	    //PD Stuff
	    pdman = new PDManager(PlasmaSound.this);
		Log.v("PlasmaSoundSetup", "launching pd");
	    pdready = false;
	    pdman.onResume();
	    
		Log.v("PlasmaSoundSetup", "Starting instrument");
	    //Make the Instrument
	    inst = new Instrument(pdman);
		Log.v("PlasmaSoundSetup", "setting instrument patch");
	    inst.setPatch("simplesine4.pd");
	    inst.setMidiMin(70);
	    inst.setMidiMax(87);
	    
		Log.v("PlasmaSoundSetup", "Reading settings");
		readSettings();	    
		Log.v("PlasmaSoundSetup", "Done!");
		return null;
	}
	@Override
	protected void onPostExecute(Void params) {
		pdready = true;
		loadingview.setVisibility(View.GONE);
		loadingview = null;

	}
};



public void debug() {
	  // Place this inside your setup() method
	  DisplayMetrics dm = new DisplayMetrics();
	  getWindowManager().getDefaultDisplay().getMetrics(dm);
	  float density = dm.density; 
	  int densityDpi = dm.densityDpi;
	  println("density is " + density); 
	  println("densityDpi is " + densityDpi);
	  
	  println("HEY! the screen size is "+width+"x"+height);
}


//mt version
public boolean surfaceTouchEvent(MotionEvent me) {
	if (mtManager != null) mtManager.surfaceTouchEvent(me);
	
	if (pdready)
		instTouchFix(me);
	return super.surfaceTouchEvent(me);
}

public void touchEvent(MotionEvent me, int i, float x, float y, float vx,
		float vy, float size) {
	if (pdready)
		instTouchEvent(me, i, x, y, vx, vy, size);
	vis.touchEvent(me,i,x,y,vx,vy,size);
	
}

public void instTouchEvent(MotionEvent me, int i, float x, float y, float vx,
		float vy, float size) {
	int index = me.getPointerId(i) + 1;//1 indexed.  0 is tougher.. actually I just forgot.
	//me.getPointerId(index)
	if (me.getAction() == me.ACTION_DOWN) {
		if (inst!=null) inst.touchDown(me, index, x/width, y/height);
	}
	else if (me.getAction() == me.ACTION_MOVE) {
		if (inst!=null) inst.touchMove(me, index, x/width, y/height);
	}
	else if (me.getAction() == me.ACTION_UP) {
		if (inst!=null) inst.touchUp(me, index, x/width, y/height);
	}
	
	if (me.getPointerCount() == 1 && me.getAction() == me.ACTION_UP) {//if the final finger is lifted...
		if (inst!=null) inst.allUp();
	}	

}

public void instTouchFix(MotionEvent me) {
//	Log.d("PlasmaTheremin", "Pointer Count: "+me.getPointerCount());
	for (int i1 = 0; i1 < me.getPointerCount()+2; i1++) {
		int pointerId = me.getPointerId(i1);
		int index1 = me.findPointerIndex(pointerId);
//		Log.d("PlasmaTheremin", "pointer id: "+pointerId+" index:"+index1);
		if (index1 < 0) {
			if (inst!=null) inst.touchUp(me, i1+1, 0, 0);
		}
		else if (pointerId != index1) {
			if (inst!=null) inst.touchUp(me, i1, 0, 0);
		}
	}
}



public void draw() {
    background(0);

    vis.drawVisuals();
    
    if (this.frameCount % 100 == 0) println(this.frameRate+"");

}




@Override
protected void onResume() {
	super.onResume();
    pdready = false;
	if (pdman != null) pdman.onResume(readyrunnable);
	readSettings();
}

@Override
protected void onPause() {
	super.onPause();
	if (pdman != null) pdman.onPause();
}

@Override
public void onDestroy() {
	if (pdman != null) pdman.cleanup();
	super.onDestroy();
}



@Override
public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(com.rj.processing.plasmasound.R.menu.main_menu, menu);
    return true;
}

/**
 * its 1:42, and where is Jake?
 * Rj is goofy as fuck.
 */

@Override
public boolean onMenuItemSelected(int featureId, MenuItem item) {
    switch (item.getItemId()) {
    case com.rj.processing.plasmasound.R.id.instrument_settings:
        instrumentSettings();
        return true;
    default:
        return super.onOptionsItemSelected(item);
    }
}

public void instrumentSettings() {
	Intent i = new Intent(this, PlasmaThereminAudioSettings.class);
	this.startActivity(i);
}

@Override
public void onActivityResult(int i, int j, Intent res) {
	super.onActivityResult(i, j, res);
	readSettings();
}

    public void readSettings() {
        SharedPreferences mPrefs = PlasmaSound.this.getSharedPreferences(SHARED_PREFERENCES_AUDIO, 0);
    	if (inst!=null) inst.updateSettings(mPrefs);
    }
    
    public void savePreset(String name) {
        SharedPreferences mPrefs = PlasmaSound.this.getSharedPreferences(SHARED_PREFERENCES_AUDIO, 0);
        //something
    }
    

}
