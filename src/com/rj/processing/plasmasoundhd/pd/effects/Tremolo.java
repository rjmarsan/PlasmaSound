package com.rj.processing.plasmasoundhd.pd.effects;

import java.util.HashMap;

import com.rj.processing.plasmasoundhd.pd.instruments.Parameter;

public  class Tremolo extends Effect {	
	public final static String SPEED = "tremolospeed";
	public final static String DEPTH = "tremolodepth";
	
	public Tremolo() {
		params = new HashMap<String, Parameter>();
		final Parameter tremspeed = new Parameter(SPEED, false);
		tremspeed.setMinMax(-20f, 70);
		tremspeed.setDefaultNaive(0.4f);
		params.put(SPEED, tremspeed );
		final Parameter tremdepth = new Parameter(DEPTH, false);
		tremdepth.setMinMax(0f, 100f);
		tremdepth.setDefault(0f);
		//params.put(DEPTH, tremdepth );
		
		this.yenabledlist = new String[] {
				SPEED,
		//		DEPTH,
		};
		this.yenabled = false;

	}




}
