package com.rj.processing.plasmasoundhd.pd.effects;

import java.util.HashMap;

import com.rj.processing.plasmasoundhd.pd.instruments.BinaryParameter;
import com.rj.processing.plasmasoundhd.pd.instruments.Parameter;

public  class Filter extends Effect {	
	public final static String FILT = "filt";
	public final static String ENABLED = "filter_onoff";
	
	public Filter() {
		this.name = "filter";

		params = new HashMap<String, Parameter>();
		final Parameter filt = new Parameter(FILT, false);
		filt.setMinMax(0f, 20f);
		filt.setDefaultNaive(0.9f);
		params.put(FILT, filt );
		
		
		final Parameter enabled = new BinaryParameter(ENABLED, true);
		enabled.setMinMax(0, 1);
		enabled.setDefaultNaive(1);
		params.put(ENABLED, enabled );

		
		
		this.yenabledlist = new String[] {
				FILT,
		};
		this.yenabled = false;

	}

	


	


}
