package com.rj.processing.plasmasoundhd.pd.effects;

import java.util.HashMap;

import com.rj.processing.plasmasoundhd.pd.instruments.Parameter;

public  class Delay extends Effect {	
	public final static String DELAY_TIME = "delaytime";
	public final static String DELAY_FEEDBACK = "delayfeedback";
	
	public Delay() {
		params = new HashMap<String, Parameter>();
		final Parameter delaytime = new Parameter(DELAY_TIME, true);
		delaytime.setMinMax(0f, 10f);
		delaytime.setDefaultNaive(0.3f);
		params.put(DELAY_TIME, delaytime );
		final Parameter feedback = new Parameter(DELAY_FEEDBACK, true);
		feedback.setMinMax(0f, 100f);
		feedback.setDefaultNaive(0.3f);
		params.put(DELAY_FEEDBACK, feedback );
		
		
		this.yenabled = false;

	}
	


}
