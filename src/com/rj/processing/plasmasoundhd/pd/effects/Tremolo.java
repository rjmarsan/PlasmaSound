package com.rj.processing.plasmasoundhd.pd.effects;

import java.util.HashMap;

import com.rj.processing.plasmasoundhd.pd.instruments.BinaryParameter;
import com.rj.processing.plasmasoundhd.pd.instruments.PSND;
import com.rj.processing.plasmasoundhd.pd.instruments.Parameter;

public  class Tremolo extends Effect {	
	
	public Tremolo() {
		this.name = "tremolo";

		params = new HashMap<String, Parameter>();
		final Parameter tremspeed = new Parameter(PSND.TREMOLO_SPEED, false);
		tremspeed.setMinMax(-20f, 70);
		tremspeed.setDefaultNaive(0.4f);
		params.put(PSND.TREMOLO_SPEED, tremspeed );
		final Parameter tremdepth = new Parameter(PSND.TREMOLO_DEPTH, false);
		tremdepth.setMinMax(0f, 100f);
		tremdepth.setDefault(0f);
		params.put(PSND.TREMOLO_DEPTH, tremdepth );
		final Parameter enabled = new BinaryParameter(PSND.TREMOLO_ENABLED, true);
		enabled.setMinMax(0, 1);
		enabled.setDefaultNaive(1);
		params.put(PSND.TREMOLO_ENABLED, enabled );
		final Parameter tremwaveform = new Parameter(PSND.TREMOLO_WAVEFORM, false);
		tremwaveform.setMinMax(0f, 100f);
		tremwaveform.setDefault(1f);
		params.put(PSND.TREMOLO_WAVEFORM, tremwaveform );
		
		this.yenabledlist = new String[] {
				PSND.TREMOLO_SPEED,
				PSND.TREMOLO_DEPTH,
		};
		this.yenabled = false;

	}




}
