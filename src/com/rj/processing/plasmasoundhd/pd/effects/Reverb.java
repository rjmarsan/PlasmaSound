package com.rj.processing.plasmasoundhd.pd.effects;

import java.util.HashMap;

import com.rj.processing.plasmasoundhd.pd.instruments.BinaryParameter;
import com.rj.processing.plasmasoundhd.pd.instruments.Parameter;

public  class Reverb extends Effect {	
	public final static String REVERB_TIME = "revebrtime";
	public final static String REVERB_FEEDBACK = "reverbfeedback";
	public final static String ENABLED = "reverb_onoff";
	
	public Reverb() {
		this.name = "reverb";

		params = new HashMap<String, Parameter>();
		final Parameter REVERBtime = new Parameter(REVERB_TIME, true);
		REVERBtime.setMinMax(0f, 100f);
		REVERBtime.setDefaultNaive(0.2f);
		params.put(REVERB_TIME, REVERBtime );
		final Parameter feedback = new Parameter(REVERB_FEEDBACK, true);
		feedback.setMinMax(0f, 100f);
		feedback.setDefaultNaive(0.7f);
		params.put(REVERB_FEEDBACK, feedback );
		final Parameter enabled = new BinaryParameter(ENABLED, true);
		enabled.setMinMax(0, 1);
		enabled.setDefaultNaive(1);
		params.put(ENABLED, enabled );
		
		this.yenabled = false;

	}
		


}
