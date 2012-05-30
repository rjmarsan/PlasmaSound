package com.rj.processing.plasmasoundhd.pd.effects;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences.Editor;

import com.rj.processing.plasmasoundhd.pd.instruments.NonPDParameter;
import com.rj.processing.plasmasoundhd.pd.instruments.PSND;
import com.rj.processing.plasmasoundhd.pd.instruments.Parameter;

public  class MotionStuff extends Effect {	
	
	public Parameter lownote; 
	public Parameter steps; 
	public Parameter bpm; 
	public Parameter notes; 
	public Parameter scale; 
	public Parameter syncopated;
	public Parameter sensitivity;
	public Parameter threshold;
	
	public MotionStuff() {
		params = new HashMap<String, Parameter>();
		
		lownote = new NonPDParameter(PSND.MOTION_LOWNOTE, false);
		lownote.setMinMax(0, 100);
		lownote.setDefault(53);
		params.put(PSND.MOTION_LOWNOTE, lownote );

		steps = new NonPDParameter(PSND.MOTION_STEPS, false);
		steps.setMinMax(1, 100);
		steps.setDefault(8);
		params.put(PSND.MOTION_STEPS, steps );

		bpm = new NonPDParameter(PSND.MOTION_BPM, false);
		bpm.setMinMax(0, 100);
		bpm.setDefault(120);
		params.put(PSND.MOTION_BPM, bpm );

		notes = new NonPDParameter(PSND.MOTION_NOTES, true);
		notes.setMinMax(1, 100);
		notes.setDefault(5);
		params.put(PSND.MOTION_NOTES, notes );

		scale = new NonPDParameter(PSND.MOTION_SCALE, true);
		scale.setMinMax(0, 10);
		scale.setDefault(1);
		params.put(PSND.MOTION_SCALE, scale );
		

		syncopated = new NonPDParameter(PSND.MOTION_SYNCOPATED, false);
		syncopated.setMinMax(0, 100);
		syncopated.setDefault(0);
		params.put(PSND.MOTION_SYNCOPATED, syncopated );

		
		sensitivity = new NonPDParameter(PSND.MOTION_SENSITIVITY, false);
		sensitivity.setMinMax(0, 100);
		sensitivity.setDefault(40);
		params.put(PSND.MOTION_SENSITIVITY, sensitivity );
		
		threshold = new NonPDParameter(PSND.MOTION_THRESHOLD, false);
		threshold.setMinMax(0, 100);
		threshold.setDefault(55);
		params.put(PSND.MOTION_THRESHOLD, threshold );

		
		this.yenabledlist = new String[] {
		};
		this.yenabled = false;
  
	}
	
	@Override
	protected void saveJSONParameterToPrefs(final JSONObject prefs, Editor edit, final Parameter p) throws JSONException {
		if (p.getName().equals(scale.getName())) 
			edit.putString(p.getName(), (int)prefs.getDouble(p.getName())+".0");
		else
			super.saveJSONParameterToPrefs(prefs, edit, p);
	}

	

}
