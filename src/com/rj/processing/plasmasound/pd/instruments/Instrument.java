package com.rj.processing.plasmasound.pd.instruments;

import org.puredata.core.PdBase;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.MotionEvent;

import com.rj.processing.plasmasound.pd.PDManager;

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
	
	
	
	
	private int patch;
	private String patchName;
	
	public float midiMin = 0;
	public float midiMax = 127;
	public boolean quantize = false;
	
	public float maxVol = 1;
	public float maxFilt = 1;
	public boolean vol_y = true;
	public boolean filt_y = false;
	
	public Instrument(PDManager p) {
		this.p = p;
	}
	
	public void setPatch(String patch) {
		patchName = patch;
		initInstrument();
	}
	
	public void initInstrument() {
		patch = p.openPatch(patchName);
	}
	
	public void touchUp(MotionEvent me, int index, float x, float y) {
		setVolume(0, index);
	}
	public void touchMove(MotionEvent me, int index, float x, float y) {
		setPitch(x, index);
		if (vol_y)
			setVolume(1-y, index);
		else
			setVolume(1, index);
		if (filt_y)
			setFilter(1-y, index);
		else
			setFilter(1, index);
	}
	public void touchDown(MotionEvent me, int index, float x, float y) {
		setVolume(1);
		setPitch(x, index);
		if (vol_y)
			setVolume(1-y, index);
		else
			setVolume(1, index);
		if (filt_y)
			setFilter(1-y, index);
		else
			setFilter(1, index);
	}
	public void allUp() {
		setVolume(0);
		for (int i=0;i<4;i++) {
			setVolume(0, i);
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
		sendMessage("amp", amp);
	}
	public void setVolume(float amp, int index) {
		amp = amp*maxVol;
		sendMessage("amp", amp, index);
	}
	
	
	public void setWaveform(float waveform) {
		sendMessage("inssel", waveform);
	}
	
	public void setFilter(float filter, int index) {
		filter = filter*20f;
		filter = filter*maxFilt;
		sendMessage("filt", filter, index);
	}
	
	
	public void setParam1(float amp) {
		sendMessage("param1", amp);
	}
	public void setParam1(float amp, int index) {
		sendMessage("param1", amp, index);
	}
	public void setParam2(float amp) {
		sendMessage("param2", amp);
	}
	public void setParam3(float amp) {
		sendMessage("param3", amp);
	}
	public void setParam3(float amp, int index) {
		sendMessage("param3", amp, index);
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
			
			float prefDelayTime = prefs.getInt(preset+DELAY_TIME, 10);
			float prefDelayFeedback = prefs.getInt(preset+DELAY_FEEDBACK, 40);
			setParam1(prefDelayTime);
			setParam2(prefDelayFeedback);
			
			
			String s_waveform = prefs.getString(preset+WAVEFORM, "1.0");
			Float waveform = Float.parseFloat(s_waveform);
			setWaveform(waveform);
			
			quantize = prefs.getBoolean(preset+QUANTIZE, false);
			
			vol_y = prefs.getBoolean(preset+VOLUME_Y, true);
			filt_y = prefs.getBoolean(preset+FILTER_Y, false);
			
			maxVol = prefs.getInt(preset+VOLUME, 80)/100f;
			maxFilt = prefs.getInt(preset+FILTER, 80)/100f;
		
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	
	
	public void cleanup() {
//		PdUtils.closePatch(patch);
		PdBase.closePatch(patch);
	}


}
