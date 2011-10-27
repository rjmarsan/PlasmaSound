package com.rj.processing.plasmasoundhd.pd.effects;

import java.util.HashMap;

import android.view.MotionEvent;

import com.rj.processing.mt.Cursor;
import com.rj.processing.plasmasoundhd.pd.instruments.Parameter;

public  class ASDR extends Effect {	
	public final static String ATTACK = "attack";
	public final static String SUSTAIN = "sustain";
	public final static String DECAY = "decay";
	public final static String RELEASE = "release";
	
	public Parameter attack; 
	public Parameter sustain; 
	public Parameter decay; 
	public Parameter release; 
	
	public ASDR() {
		this.name = "asdr";

		params = new HashMap<String, Parameter>();
		attack = new Parameter(ATTACK, false);
		attack.setMinMax(0f, 100);
		attack.setDefault(100);
		params.put(ATTACK, attack );

		sustain = new Parameter(SUSTAIN, false);
		sustain.setMinMax(0f, 100);
		sustain.setDefault(100);
		params.put(SUSTAIN, sustain );

		decay = new Parameter(DECAY, false);
		decay.setMinMax(0f, 100);
		decay.setDefault(100);
		params.put(DECAY, decay );

		release = new Parameter(RELEASE, false);
		release.setMinMax(0f, 100);
		release.setDefault(100);
		params.put(RELEASE, release );

		this.yenabledlist = new String[] {
//				ATTACK,
//				SUSTAIN,
//				DECAY,
//				RELEASE
		};
		this.yenabled = false;
  
	}

	

}
