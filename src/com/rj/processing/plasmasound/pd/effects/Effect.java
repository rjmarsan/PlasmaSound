package com.rj.processing.plasmasound.pd.effects;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.MotionEvent;

import com.rj.processing.mt.Cursor;
import com.rj.processing.plasmasound.pd.instruments.Parameter;

public abstract class Effect {	
	private static final int MAX_INDEX = 4;
	
	protected HashMap<String, Parameter> params;
	
	boolean yenabled = false;
	String[] yenabledlist = {};
	boolean enabled = true;
	
	public Effect() {
		params = new HashMap<String, Parameter>();
	}

	
	public void initEffect() {
	}
	
	public void touchUp(final MotionEvent me, final int index, final float x, final float y, final Cursor c) {
		if (yenabled && enabled && index <= MAX_INDEX) {
			for (final String effect : yenabledlist) {
				final Parameter p = params.get(effect);
				p.pushValueNaive(0, index);
			}
		}
	}
	public void touchMove(final MotionEvent me, final int index, final float x, final float y, final Cursor c) {
		if (yenabled && enabled && index <= MAX_INDEX) {
			for (final String effect : yenabledlist) {
				final Parameter p = params.get(effect);
				p.pushValueNaive(1-y, index);
			}
		}
	}
	public void touchDown(final MotionEvent me, final int index, final float x, final float y, final Cursor c) {
		for (final Parameter param : params.values()) {
			param.pushDefaultNaive(index);
		}
		if (yenabled && enabled && index <= MAX_INDEX) {
			for (final String effect : yenabledlist) {
				final Parameter p = params.get(effect);
				p.pushValueNaive(1-y, index);
			}
		}
	}
	public void allUp() {
	}
	
	
	
	public void updateSettings(final SharedPreferences prefs) {
		updateSettings(prefs, "");
	}
	public void updateSettings(final SharedPreferences prefs, final String preset) {
		final ArrayList<String> yList = new ArrayList<String>();
		for (final Parameter p : params.values()) {
			if (prefs.getBoolean(p.getName()+"_y", false)) {
				Log.d("EffectsSettings", "Adding :"+p.getName()+ " to ylist");
				yList.add(p.getName());
			}
			final float newval = (prefs.getInt(p.getName(), -1))/100f;
			Log.d("EffectsSettings", "Value for :"+p.getName()+ " : " + newval);
			if (newval >= 0)
				p.setDefaultNaive(newval);
		}
		this.yenabledlist = new String[yList.size()];
		for (int i=0; i<yenabledlist.length;i++) {
			yenabledlist[i] = yList.get(i);
		}
		if (yList.size() > 0) {
			this.yenabled = true;
		}
	}
	


}
