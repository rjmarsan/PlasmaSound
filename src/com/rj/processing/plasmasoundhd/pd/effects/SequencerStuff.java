package com.rj.processing.plasmasoundhd.pd.effects;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences.Editor;

import com.rj.processing.plasmasoundhd.pd.instruments.NonPDParameter;
import com.rj.processing.plasmasoundhd.pd.instruments.PSND;
import com.rj.processing.plasmasoundhd.pd.instruments.Parameter;

public  class SequencerStuff extends Effect {	
	
	public Parameter lownote; 
	public Parameter steps; 
	public Parameter bpm; 
	public Parameter notes; 
	public Parameter scale; 
	public Parameter syncopated;
	
	public SequencerStuff() {
		params = new HashMap<String, Parameter>();
		
		lownote = new NonPDParameter(PSND.SEQUENCER_LOWNOTE, false);
		lownote.setMinMax(0, 100);
		lownote.setDefault(53);
		params.put(PSND.SEQUENCER_LOWNOTE, lownote );

		steps = new NonPDParameter(PSND.SEQUENCER_STEPS, false);
		steps.setMinMax(1, 100);
		steps.setDefault(8);
		params.put(PSND.SEQUENCER_STEPS, steps );

		bpm = new NonPDParameter(PSND.SEQUENCER_BPM, false);
		bpm.setMinMax(0, 100);
		bpm.setDefault(120);
		params.put(PSND.SEQUENCER_BPM, bpm );

		notes = new NonPDParameter(PSND.SEQUENCER_NOTES, true);
		notes.setMinMax(1, 100);
		notes.setDefault(5);
		params.put(PSND.SEQUENCER_NOTES, notes );

		scale = new NonPDParameter(PSND.SEQUENCER_SCALE, true);
		scale.setMinMax(0, 10);
		scale.setDefault(1);
		params.put(PSND.SEQUENCER_SCALE, scale );
		

		syncopated = new NonPDParameter(PSND.SEQUENCER_SYNCOPATED, false);
		syncopated.setMinMax(0, 100);
		syncopated.setDefault(0);
		params.put(PSND.SEQUENCER_SYNCOPATED, syncopated );

		
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
