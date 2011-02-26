package com.rj.processing.plasmasound.pd.effects;

import java.util.HashMap;

import android.content.SharedPreferences;

import com.rj.processing.plasmasound.pd.instruments.Parameter;

public  class Filter extends Effect {	
	public final static String FILT = "filt";
	
	public Filter() {
		params = new HashMap<String, Parameter>();
		Parameter filt = new Parameter(FILT, false);
		filt.setMinMax(0f, 20f);
		filt.setDefaultNaive(0.9f);
		params.put(FILT, filt );
		
		this.yenabledlist = new String[] {
				FILT,
		};
		this.yenabled = false;

	}

	


	public void updateSettings(SharedPreferences prefs, String preset) {
		
	}
	


}
