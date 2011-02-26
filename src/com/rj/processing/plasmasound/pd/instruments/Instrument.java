package com.rj.processing.plasmasound.pd.instruments;

import java.util.ArrayList;

import org.puredata.core.PdBase;

import android.content.SharedPreferences;
import android.view.MotionEvent;

import com.rj.processing.plasmasound.pd.PDManager;
import com.rj.processing.plasmasound.pd.effects.Delay;
import com.rj.processing.plasmasound.pd.effects.Effect;
import com.rj.processing.plasmasound.pd.effects.Filter;
import com.rj.processing.plasmasound.pd.effects.Vibrato;
import com.rj.processing.plasmasound.pd.effects.Volume;

public class Instrument {
	private static final String MIDI_MIN = "midimin";
	private static final String MIDI_MAX = "midimax";
	private static final String DELAY_TIME = "delay_time";
	private static final String DELAY_FEEDBACK = "delay_feedback";
	private static final String WAVEFORM = "waveform";
	private static final String QUANTIZE = "quantize_note";
	private static final String VOLUME = "volume";
	private static final String VOLUME_Y = "volume_y";
	private static final String FILTER = "filter";
	private static final String FILTER_Y = "filter_y";
	final PDManager p;
	
	
	private static final float WAVEFORM_SINE = 1.0f;
	private static final float WAVEFORM_TRIANGLE = 2.0f;
	private static final float WAVEFORM_SQUARE = 3.0f;
	private static final float WAVEFORM_SAW = 4.0f;
	
	private static final int MAX_INDEX = 4;
	
	
	private ArrayList<Effect> effects = new ArrayList<Effect>();
	private Volume volume;
	
	
	private int patch;
	private String patchName;
	
	public float midiMin = 0;
	public float midiMax = 127;
	public boolean quantize = false;
	
	public float maxVol = 1;
	public float maxFilt = 1;
	public boolean vol_y = true;
	public boolean filt_y = false;
	
	public boolean ready = false;
	
	public Instrument(PDManager p) {
		this.p = p;
		volume = new Volume();
		effects.add(volume);
		effects.add(new Vibrato());
		effects.add(new Delay());
		effects.add(new Filter());
	}
	
	public void setPatch(String patch) {
		patchName = patch;
//		new Thread(new Runnable() { public void run() {
			initInstrument();
			ready = true;
//		}}).start();
	}
	
	public void initInstrument() {
		patch = p.openPatch(patchName);
	}
	
	public void touchUp(MotionEvent me, int index, float x, float y) {
		if (ready && index <= MAX_INDEX) {
			for (Effect e : effects) {
				e.touchUp(me, index, x, 0);
			}
		}
	}
	public void touchMove(MotionEvent me, int index, float x, float y) {
		if (ready && index <= MAX_INDEX) {
			setPitch(x, index);
			for (Effect e : effects) {
				e.touchMove(me, index, x, y);
			}
		}
	}
	public void touchDown(MotionEvent me, int index, float x, float y) {
		if (ready && index <= MAX_INDEX) {
			setVolume(1);
			setPitch(x, index);
			for (Effect e : effects) {
				e.touchDown(me, index, x, y);
			}
		}
	}
	public void allUp() {
		if (ready) {
			setVolume(0);
			for (int index=1; index<=MAX_INDEX; index++) {
				for (Effect e : effects) {
					e.touchUp(null, index, 0, 0);
				}
			}
		}
	}
	
	
	public void setMidiMin(float val) {
		this.midiMin = val;
	}
	public void setMidiMax(float val) {
		this.midiMax = val;
	}
	
	private void sendMessage(String s, float val) {
		PdBase.sendFloat(s, val);
	}
	private void sendMessage(String s, float val, int index) {
		PdBase.sendFloat(s+index, val);
	}
	
	public void setPitch(float val) {
		float pitch = midiMin + (val * (midiMax-midiMin));
		if (quantize)
			pitch = (float)Math.floor(pitch);
		sendMessage("pitch", pitch);
	}
	public void setPitch(float val, int index) {
		float pitch = midiMin + (val * (midiMax-midiMin));
		if (quantize)
			pitch = (float)Math.floor(pitch);
		sendMessage("pitch", pitch, index);
	}
	
	public void setVolume(float amp) {
		volume.setVolume(amp);
	}
	
	
	public void setWaveform(float waveform) {
		sendMessage("inssel", waveform);
	}
	
	
	public void updateSettings(SharedPreferences prefs) {
		updateSettings(prefs, "");
	}
	public void updateSettings(SharedPreferences prefs, String preset) {
		try {
			float prefMidiMin = prefs.getInt(preset+MIDI_MIN, 70);
			float prefMidiMax = prefs.getInt(preset+MIDI_MAX, 86);
			setMidiMin(prefMidiMin);
			setMidiMax(prefMidiMax);			
			
			String s_waveform = prefs.getString(preset+WAVEFORM, "1.0");
			Float waveform = Float.parseFloat(s_waveform);
			setWaveform(waveform);
			
			quantize = prefs.getBoolean(preset+QUANTIZE, false);
			
			vol_y = prefs.getBoolean(preset+VOLUME_Y, true);
			filt_y = prefs.getBoolean(preset+FILTER_Y, false);
			
			maxVol = prefs.getInt(preset+VOLUME, 80)/100f;
			maxFilt = prefs.getInt(preset+FILTER, 80)/100f;
			
			for (Effect e : effects) {
				e.updateSettings(prefs, preset);
			}
		
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	
	
	public void cleanup() {
//		PdUtils.closePatch(patch);
		PdBase.closePatch(patch);
	}


}
