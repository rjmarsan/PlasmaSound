package com.rj.processing.plasmasound.pd.effects;

import java.util.HashMap;

import android.content.SharedPreferences;

import com.rj.processing.plasmasound.pd.instruments.Parameter;

public  class Volume extends Effect {	
	public final static String AMP_GLOBAL = "ampglob";
	public final static String AMP = "amp";
	
	public Volume() {
		params = new HashMap<String, Parameter>();
		Parameter ampglobal = new Parameter(AMP_GLOBAL, true);
		ampglobal.setMinMax(0f, 1f);
		ampglobal.setDefault(0.9f);
		params.put(AMP_GLOBAL, ampglobal );
		Parameter amp = new Parameter(AMP, false);
		amp.setMinMax(0f, 1f);
		amp.setDefault(0.9f);
		params.put(AMP, amp );
		
		this.yenabledlist = new String[] {
				AMP,
		};
		this.yenabled = false;

	}

	
	public void setVolume(float val) {
		params.get(AMP_GLOBAL).pushValue(val);
	}




}
