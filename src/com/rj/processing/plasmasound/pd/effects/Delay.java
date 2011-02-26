package com.rj.processing.plasmasound.pd.effects;

import java.util.HashMap;

import android.content.SharedPreferences;

import com.rj.processing.plasmasound.pd.instruments.Parameter;

public  class Delay extends Effect {	
	public final static String DELAY_TIME = "delaytime";
	public final static String DELAY_FEEDBACK = "delayfeedback";
	
	public Delay() {
		params = new HashMap<String, Parameter>();
		Parameter delaytime = new Parameter(DELAY_TIME, false);
		delaytime.setMinMax(0f, 100f);
		params.put(DELAY_TIME, delaytime );
		Parameter feedback = new Parameter(DELAY_FEEDBACK, false);
		feedback.setMinMax(0f, 100f);
		params.put(DELAY_FEEDBACK, feedback );
		
		this.yenabledlist = new String[] {
				DELAY_FEEDBACK,
				DELAY_TIME
		};

	}
	
	public void setDelay(float value) {
		
	}

	
	public void setVolume(float val) {
		params.get(DELAY_TIME).pushValue(val);
	}

	public void updateSettings(SharedPreferences prefs, String preset) {
		
	}
	


}
