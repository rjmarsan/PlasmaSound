package com.rj.processing.plasmasoundhd.pd.effects;

import java.util.HashMap;

import android.view.MotionEvent;

import com.rj.processing.mt.Cursor;
import com.rj.processing.plasmasoundhd.pd.instruments.Parameter;

public  class Volume extends Effect {	
	public final static String AMP_GLOBAL = "ampglob";
	public final static String AMP = "amp";
	public final static String ON = "noteon";
	public final static String OFF = "noteoff";
	
	public Parameter amp; 
	public Parameter ampglobal;
	public Parameter on;
	public Parameter off;
	
	public Volume() {
		params = new HashMap<String, Parameter>();
		ampglobal = new Parameter(AMP_GLOBAL, true);
		ampglobal.setMinMax(0f, 1f);
		ampglobal.setDefault(0.9f);
		//params.put(AMP_GLOBAL, ampglobal );
		amp = new Parameter(AMP, false);
		amp.setMinMax(0f, 1f);
		amp.setDefault(0.9f);
		params.put(AMP, amp );
		
		on = new Parameter(ON, false);
		on.setMinMax(0, 1);
		on.setDefault(0);
		
		off = new Parameter(OFF, false);
		off.setMinMax(0, 1);
		off.setDefault(0);

		
	    
		this.yenabledlist = new String[] {
				AMP,
		};
		this.yenabled = false;

	}

	
	public void setVolume(final float val) {
		ampglobal.pushValue(val);
	}


	@Override
	public void touchDown(final MotionEvent me, final int index, final float x, final float y, final Cursor c) {
		super.touchDown(me, index, x, y, c);
		on.pushValue(1,index);
	}
 	
	@Override
	public void touchUp(final MotionEvent me, final int index, final float x, final float y, final Cursor c) {
		//super.touchUp(me, index, x, y, c);
		off.pushValue(1, index);
		//amp.pushValue(0, index);
	}


}
