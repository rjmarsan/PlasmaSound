package com.rj.processing.plasmasoundhd.pd.effects;

import java.util.HashMap;

import android.view.MotionEvent;

import com.rj.processing.mt.Cursor;
import com.rj.processing.plasmasoundhd.pd.instruments.Parameter;

public  class Volume extends Effect {	
	public final static String AMP_GLOBAL = "ampglob";
	public final static String AMP = "amp";
	
	public Parameter amp; 
	
	public Volume() {
		params = new HashMap<String, Parameter>();
		final Parameter ampglobal = new Parameter(AMP_GLOBAL, true);
		ampglobal.setMinMax(0f, 1f);
		ampglobal.setDefault(0.9f);
		params.put(AMP_GLOBAL, ampglobal );
		amp = new Parameter(AMP, false);
		amp.setMinMax(0f, 1f);
		amp.setDefault(0.9f);
		params.put(AMP, amp );
		
		this.yenabledlist = new String[] {
				AMP,
		};
		this.yenabled = false;

	}

	
	public void setVolume(final float val) {
		params.get(AMP_GLOBAL).pushValue(val);
	}


	public void touchUp(final MotionEvent me, final int index, final float x, final float y, final Cursor c) {
		amp.pushValue(0, index);
	}


}
