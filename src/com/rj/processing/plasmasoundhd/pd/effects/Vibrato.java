package com.rj.processing.plasmasoundhd.pd.effects;

import java.util.HashMap;

import com.rj.processing.plasmasoundhd.pd.instruments.BinaryParameter;
import com.rj.processing.plasmasoundhd.pd.instruments.Parameter;

public  class Vibrato extends Effect {	
	public final static String SPEED = "vibspeed";
	public final static String DEPTH = "vibdepth";
	public final static String ENABLED = "vibrato_onoff";
	
	public Vibrato() {
		this.name = "vibrato";

		params = new HashMap<String, Parameter>();
		final Parameter vibspeed = new Parameter(SPEED, false);
		vibspeed.setMinMax(0f, 50);
		vibspeed.setDefaultNaive(0.4f);
		params.put(SPEED, vibspeed );
		final Parameter vibdepth = new Parameter(DEPTH, false);
		vibdepth.setMinMax(0f, 100f);
		vibdepth.setDefault(0f);
		params.put(DEPTH, vibdepth );
		
		final Parameter enabled = new BinaryParameter(ENABLED, true);
		enabled.setMinMax(0, 1);
		enabled.setDefault(1);
		params.put(ENABLED, enabled );

		
		this.yenabledlist = new String[] {
				SPEED,
				DEPTH,
		};
		this.yenabled = false;

	}




}
