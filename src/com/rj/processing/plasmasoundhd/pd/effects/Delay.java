package com.rj.processing.plasmasoundhd.pd.effects;

import java.util.HashMap;

import com.rj.processing.plasmasoundhd.pd.instruments.BinaryParameter;
import com.rj.processing.plasmasoundhd.pd.instruments.PSND;
import com.rj.processing.plasmasoundhd.pd.instruments.Parameter;

public  class Delay extends Effect {	
	
	public Delay() {
		this.name = "delay";
		params = new HashMap<String, Parameter>();
		
		final Parameter delaytime = new Parameter(PSND.DELAY_TIME, true);
		delaytime.setMinMax(0f, 10f);
		delaytime.setDefaultNaive(0.3f);
		params.put(PSND.DELAY_TIME, delaytime );
		
		final Parameter feedback = new Parameter(PSND.DELAY_FEEDBACK, true);
		feedback.setMinMax(0f, 100f);
		feedback.setDefaultNaive(0.3f);
		params.put(PSND.DELAY_FEEDBACK, feedback );
		
		
		final Parameter enabled = new BinaryParameter(PSND.DELAY_ENABLED, true);
		enabled.setMinMax(0, 1);
		enabled.setDefaultNaive(1);
		params.put(PSND.DELAY_ENABLED, enabled );

		
		this.yenabled = false;

	}
	


}
