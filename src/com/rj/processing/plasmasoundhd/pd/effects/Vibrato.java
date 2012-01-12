package com.rj.processing.plasmasoundhd.pd.effects;

import java.util.HashMap;

import com.rj.processing.plasmasoundhd.pd.instruments.BinaryParameter;
import com.rj.processing.plasmasoundhd.pd.instruments.PSND;
import com.rj.processing.plasmasoundhd.pd.instruments.Parameter;

public  class Vibrato extends Effect {	
	
	public Vibrato() {
		this.name = "vibrato";

		params = new HashMap<String, Parameter>();
		final Parameter vibspeed = new Parameter(PSND.VIBRATO_SPEED, false);
		vibspeed.setMinMax(0f, 50);
		vibspeed.setDefaultNaive(0.4f);
		params.put(PSND.VIBRATO_SPEED, vibspeed );
		final Parameter vibdepth = new Parameter(PSND.VIBRATO_DEPTH, false);
		vibdepth.setMinMax(0f, 100f);
		vibdepth.setDefault(0f);
		params.put(PSND.VIBRATO_DEPTH, vibdepth );
		
		final Parameter enabled = new BinaryParameter(PSND.VIBRATO_ENABLED, true);
		enabled.setMinMax(0, 1);
		enabled.setDefault(1);
		params.put(PSND.VIBRATO_ENABLED, enabled );

		final Parameter vibwaveform = new Parameter(PSND.VIBRATO_WAVEFORM, false);
		vibwaveform.setMinMax(0f, 100f);
		vibwaveform.setDefault(1f);
		params.put(PSND.VIBRATO_WAVEFORM, vibwaveform );

		
		this.yenabledlist = new String[] {
				PSND.VIBRATO_SPEED,
				PSND.VIBRATO_DEPTH,
		};
		this.yenabled = false;

	}




}
