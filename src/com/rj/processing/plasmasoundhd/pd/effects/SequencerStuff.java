package com.rj.processing.plasmasoundhd.pd.effects;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences.Editor;

import com.rj.processing.plasmasoundhd.pd.instruments.NonPDParameter;
import com.rj.processing.plasmasoundhd.pd.instruments.Parameter;

public  class SequencerStuff extends Effect {	
	public final static String LOWNOTE = "sequence_lownote";
	public final static String STEPS = "sequence_steps";
	public final static String BPM = "sequence_bpm";
	public final static String NOTES = "sequence_notes";
	public final static String SCALE = "sequence_scale";
	public final static String SYNCOPATED = "sequence_syncopated";
	
	public Parameter lownote; 
	public Parameter steps; 
	public Parameter bpm; 
	public Parameter notes; 
	public Parameter scale; 
	public Parameter syncopated;
	
	public SequencerStuff() {
		params = new HashMap<String, Parameter>();
		
		lownote = new NonPDParameter(LOWNOTE, false);
		lownote.setMinMax(0, 100);
		lownote.setDefault(53);
		params.put(LOWNOTE, lownote );

		steps = new NonPDParameter(STEPS, false);
		steps.setMinMax(1, 100);
		steps.setDefault(8);
		params.put(STEPS, steps );

		bpm = new NonPDParameter(BPM, false);
		bpm.setMinMax(0, 100);
		bpm.setDefault(120);
		params.put(BPM, bpm );

		notes = new NonPDParameter(NOTES, true);
		notes.setMinMax(1, 100);
		notes.setDefault(5);
		params.put(NOTES, notes );

		scale = new NonPDParameter(SCALE, true);
		scale.setMinMax(0, 10);
		scale.setDefault(1);
		params.put(SCALE, scale );
		

		syncopated = new NonPDParameter(SYNCOPATED, false);
		syncopated.setMinMax(0, 100);
		syncopated.setDefault(0);
		params.put(SYNCOPATED, syncopated );

		
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
