package com.rj.processing.plasmasoundhd.pd.effects;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.MotionEvent;

import com.rj.processing.mt.Cursor;
import com.rj.processing.plasmasoundhd.pd.instruments.Parameter;

public abstract class Effect {
	protected static final int MAX_INDEX = 8;

	protected HashMap<String, Parameter> params;

	boolean yenabled = false;
	String[] yenabledlist = {};
	boolean enabled = true;

	public Effect() {
		params = new HashMap<String, Parameter>();
	}

	public void initEffect() {
	}

	public void touchUp(final MotionEvent me, final int index, final float x,
			final float y, final Cursor c) {
		if (yenabled && enabled && index <= MAX_INDEX) {
			for (final String effect : yenabledlist) {
				final Parameter p = params.get(effect);
				p.pushValueNaive(0, index);
			}
		}
	}

	public void touchMove(final MotionEvent me, final int index, final float x,
			final float y, final Cursor c) {
		if (yenabled && enabled && index <= MAX_INDEX) {
			for (final String effect : yenabledlist) {
				final Parameter p = params.get(effect);
				p.pushValueNaive(1 - y, index);
			}
		}
	}

	public void touchDown(final MotionEvent me, final int index, final float x,
			final float y, final Cursor c) {
		for (final Parameter param : params.values()) {
			// if (!param.isGlobal())
			param.pushDefaultNaive(index);
		}
		if (yenabled && enabled && index <= MAX_INDEX) {
			for (final String effect : yenabledlist) {
				final Parameter p = params.get(effect);
				p.pushValueNaive(1 - y, index);
			}
		}
	}

	public void allUp() {
	}

	public void updateSettings(final SharedPreferences prefs) {
		updateSettings(prefs, "");
	}

	public void updateSettings(final SharedPreferences prefs,
			final String preset) {
		final ArrayList<String> yList = new ArrayList<String>();
		for (final Parameter p : params.values()) {
			if (prefs.getBoolean(p.getName() + "_y", false)) {
				// Log.d("EffectsSettings", "Adding :"+p.getName()+
				// " to ylist");
				yList.add(p.getName());
			}

			float newval = -1;
			try {	
				newval = (prefs.getInt(p.getName(), -1)) / 100f;
			} catch (ClassCastException e) {
				newval = (Float.parseFloat(prefs.getString(p.getName(), "0.0"))) / 100f;
			}
			// Log.d("EffectsSettings", "Value for :"+p.getName()+ " : " +
			// newval);
			if (newval >= 0)
				p.setDefaultNaive(newval);
		}
		this.yenabledlist = new String[yList.size()];
		for (int i = 0; i < yenabledlist.length; i++) {
			yenabledlist[i] = yList.get(i);
		}
		if (yList.size() > 0) {
			this.yenabled = true;
		}
	}

	public void updateSettingsFromJSON(final JSONObject prefs,
			boolean savetoshared, Editor edit) {
		final ArrayList<String> yList = new ArrayList<String>();
		try {
			for (final Parameter p : params.values()) {
				if (prefs.has(p.getName() + "_y")
						&& prefs.getBoolean(p.getName() + "_y")) {
//					Log.d("EffectsSettings", "Adding :" + p.getName()
//							+ " to ylist");
					yList.add(p.getName());
					if (savetoshared)
						edit.putBoolean(p.getName() + "_y", true);
				}
				final float newval = prefs.has(p.getName()) ? (float)prefs.getDouble(p.getName()) / 100f : -1;
				//Log.d("EffectsSettings", "Value for :" + p.getName() + " : "
				//		+ newval);
				if (newval >= 0)
					p.setDefaultNaive(newval);
				if (savetoshared && prefs.has(p.getName()))
					edit.putInt(p.getName(), (int)prefs.getDouble(p.getName()));
			}
		} catch (JSONException j) {
			j.printStackTrace();
		}
		this.yenabledlist = new String[yList.size()];
		for (int i = 0; i < yenabledlist.length; i++) {
			yenabledlist[i] = yList.get(i);
		}
		if (yList.size() > 0) {
			this.yenabled = true;
		}
	}

	public JSONObject saveSettingsToJSON(final JSONObject prefs) {
		try {
			for (final Parameter p : params.values()) {
				prefs.put(p.getName(), p.getDefaultValueNaive() * 100f);
				prefs.put(p.getName() + "_y", false);
			}
			if (this.yenabled) {
				for (String name : yenabledlist) {
					prefs.put(name + "_y", true);
				}
			}
		} catch (JSONException j) {
			j.printStackTrace();
		}
		return prefs;
	}

}
