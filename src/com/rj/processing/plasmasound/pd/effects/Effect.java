package com.rj.processing.plasmasound.pd.effects;

import java.util.HashMap;

import org.puredata.core.PdBase;

import com.rj.processing.plasmasound.pd.instruments.Parameter;

import android.content.SharedPreferences;
import android.view.MotionEvent;

public abstract class Effect {	
	private static final int MAX_INDEX = 4;
	
	protected HashMap<String, Parameter> params;
	
	boolean yenabled = true;
	String[] yenabledlist = {};
	boolean enabled = true;
	
	public Effect() {
		params = new HashMap<String, Parameter>();
	}

	
	public void initEffect() {
	}
	
	public void touchUp(MotionEvent me, int index, float x, float y) {
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
	}
	public void allUp() {
	}
	
	
	
	public void updateSettings(SharedPreferences prefs) {
		updateSettings(prefs, "");
	}
	public abstract void updateSettings(SharedPreferences prefs, String preset);
	


}
