package com.rj.processing.plasmasound.pd.effects;

import java.util.HashMap;

import com.rj.processing.plasmasound.pd.instruments.Parameter;

public  class Reverb extends Effect {	
	public final static String REVERB_TIME = "revebrtime";
	public final static String REVERB_FEEDBACK = "reverbfeedback";
	
	public Reverb() {
		params = new HashMap<String, Parameter>();
		final Parameter REVERBtime = new Parameter(REVERB_TIME, true);
		REVERBtime.setMinMax(0f, 100f);
		REVERBtime.setDefaultNaive(0.2f);
		params.put(REVERB_TIME, REVERBtime );
		final Parameter feedback = new Parameter(REVERB_FEEDBACK, true);
		feedback.setMinMax(0f, 100f);
		feedback.setDefaultNaive(0.7f);
		params.put(REVERB_FEEDBACK, feedback );
		
		
		this.yenabled = false;

	}
	
	public void setREVERB(final float value) {
		
	}

	
	public void setVolume(final float val) {
		params.get(REVERB_TIME).pushValue(val);
	}

	


}
