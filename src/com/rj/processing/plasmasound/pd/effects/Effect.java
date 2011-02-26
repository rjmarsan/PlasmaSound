package com.rj.processing.plasmasound.pd.effects;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.MotionEvent;

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
	
	public void touchUp(MotionEvent me, int index, float x, float y) {
		if (yenabled && enabled && index <= MAX_INDEX) {
			for (String effect : yenabledlist) {
				Parameter p = params.get(effect);
				p.pushValueNaive(0, index);
			}
		}
	}
	public void touchMove(MotionEvent me, int index, float x, float y) {
		if (yenabled && enabled && index <= MAX_INDEX) {
			for (String effect : yenabledlist) {
				Parameter p = params.get(effect);
				p.pushValueNaive(1-y, index);
			}
		}
	}
	public void touchDown(MotionEvent me, int index, float x, float y) {
		for (Parameter param : params.values()) {
			param.pushDefaultNaive(index);
		}
		if (yenabled && enabled && index <= MAX_INDEX) {
			for (String effect : yenabledlist) {
				Parameter p = params.get(effect);
				p.pushValueNaive(1-y, index);
			}
		}
	}
	public void allUp() {
	}
	
	
	
	public void updateSettings(SharedPreferences prefs) {
		updateSettings(prefs, "");
	}
	public void updateSettings(SharedPreferences prefs, String preset) {
		ArrayList<String> yList = new ArrayList<String>();
		for (Parameter p : params.values()) {
			if (prefs.getBoolean(p.getName()+"_y", false)) {
				Log.d("EffectsSettings", "Adding :"+p.getName()+ " to ylist");
				yList.add(p.getName());
			}
			float newval = (float)(prefs.getInt(p.getName(), -1))/100f;
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
