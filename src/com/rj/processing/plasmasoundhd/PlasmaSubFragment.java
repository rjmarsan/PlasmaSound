package com.rj.processing.plasmasoundhd;

import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.view.Menu;
import android.view.MenuInflater;

import com.rj.processing.mt.Cursor;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PlasmaSubFragment extends Fragment {	
	public boolean loadPresets() { return false; }
	int getMenu() { return com.rj.processing.plasmasound.R.menu.sequencer_menu; }

	PDActivity p;
	
	public PlasmaSubFragment() {
		//eek! I wish fragments weren't lame and required this.
	}
	
	public PlasmaSubFragment(PDActivity p) {
		this.p = p;
		setHasOptionsMenu(true);
	}
	
	public void setup() {
		
	}
	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    inflater.inflate(getMenu(), menu);
	}
	
	public void destroy() {	
	}
	
	public void background() {	
	}

	
	public void pause() {
	}
	
	public void start() {
	}
	
	protected void resume() {
	}
	
	public void presetChanged(JSONObject preset) {
	}
	
	
	public void touchAllUp(final Cursor c) {
		
	}
	public void touchDown(final Cursor c) {
	}
	public void touchMoved(final Cursor c) {
	}
	public void touchUp(final Cursor c) {
	}	
	
	
	public void draw() {
		
	}
	
	
	
	
	
	

}
