package com.rj.processing.plasmasoundhd.pd.effects;

import java.util.HashMap;

import com.rj.processing.plasmasoundhd.pd.instruments.Parameter;

public  class Filter extends Effect {	
	public final static String FILT = "filt";
	
	public Filter() {
		params = new HashMap<String, Parameter>();
		final Parameter filt = new Parameter(FILT, false);
		filt.setMinMax(0f, 20f);
		filt.setDefaultNaive(0.9f);
		params.put(FILT, filt );
		
		this.yenabledlist = new String[] {
				FILT,
		};
		this.yenabled = false;

	}

	


	


}
