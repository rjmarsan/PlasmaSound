package com.rj.processing.plasmasoundhd.pd.instruments;

import java.util.ArrayList;

import org.json.JSONObject;
import org.puredata.core.PdBase;

import android.content.SharedPreferences;
import android.view.MotionEvent;

import com.rj.processing.mt.Cursor;
import com.rj.processing.plasmasoundhd.pd.PDManager;
import com.rj.processing.plasmasoundhd.pd.effects.ASDR;
import com.rj.processing.plasmasoundhd.pd.effects.Delay;
import com.rj.processing.plasmasoundhd.pd.effects.Effect;
import com.rj.processing.plasmasoundhd.pd.effects.Filter;
import com.rj.processing.plasmasoundhd.pd.effects.Reverb;
import com.rj.processing.plasmasoundhd.pd.effects.Tremolo;
import com.rj.processing.plasmasoundhd.pd.effects.Vibrato;
import com.rj.processing.plasmasoundhd.pd.effects.Volume;

public class Instrument {
	private static final String MIDI_MIN = "midimin";
	private static final String MIDI_MAX = "midimax";
	private static final String WAVEFORM = "waveform";
	private static final String QUANTIZE = "quantize_note_list";
	private static final String QUAT_CONTINUOUS = "continuous";
	private static final String QUAT_QUANTIZE = "quantize";
	private static final String QUAT_SLIDE = "slide";
	private static final String VOLUME = "volume";
	private static final String VOLUME_Y = "volume_y";
	private static final String FILTER = "filter";
	private static final String FILTER_Y = "filter_y";
	final PDManager p;
	
	
	
	private static final int MAX_INDEX = 8;
	
	
	final private ArrayList<Effect> effects = new ArrayList<Effect>();
	final private Volume volume;
	
	
	private int patch;
	private String patchName;
	
	public float midiMin = 0;
	public float midiMax = 127;
	public float waveform = 1;
	public static int NCONTINUOUS = 0;
	public static int NQUANTIZE = 1;
	public static int NSLIDE = 2;
	public int quantize = NCONTINUOUS;

	public float maxVol = 1;
	public float maxFilt = 1;
	public boolean vol_y = true;
	public boolean filt_y = false;
	
	public boolean ready = false;
	
	public Instrument(final PDManager p) {
		this.p = p;
		volume = new Volume();
		effects.add(volume);
		effects.add(new Vibrato());
		effects.add(new ASDR());
		effects.add(new Tremolo());
		effects.add(new Delay());
		effects.add(new Reverb());
		effects.add(new Filter());
	}
	
	public void setPatch(final String patch) {
		patchName = patch;
//		new Thread(new Runnable() { public void run() {
			initInstrument();
			ready = true;
//		}}).start();
	}
	
	public void initInstrument() {
		patch = p.openPatch(patchName);
	}
	
	public void touchUp(final MotionEvent me, int index, float x, final float width, float y, final float height, final Cursor c) {
//		Log.d("Instrument", "TOUCH UP!!!!!! : "+index);
		x=x/width;
		y=y/height;
		index ++;
		if (ready && index <= MAX_INDEX) {
			for (final Effect e : effects) {
				e.touchUp(me, index, x, 0, c);
			}
		}
	}
	public void touchMove(final MotionEvent me, int index, float x, final float width, float y, final float height, final Cursor c) {
		x=x/width;
		y=y/height;
		index ++;
		if (ready && index <= MAX_INDEX) {
			setPitch(x, index, c, width);
			for (final Effect e : effects) {
				e.touchMove(me, index, x, y, c);
			}
		}
	}
	public void touchDown(final MotionEvent me, int index, float x, final float width, float y, final float height, final Cursor c) {
//		Log.d("Instrument", "TOUCH DOWN!!!!!!: "+index);
		x=x/width;
		y=y/height;
		index ++;
		
		if (ready && index <= MAX_INDEX) {
			setVolume(1);
			setPitch(x, index, c, width);
			for (final Effect e : effects) {
				e.touchDown(me, index, x, y, c);
			}
		}
	}
	public void allUp() {
		if (ready) {
			//setVolume(0);
			//no.
			for (int index=1; index<=MAX_INDEX; index++) {
				for (final Effect e : effects) {
					e.touchUp(null, index, 0, 0, null);
				}
			}
		}
	}
	
	
	public void setMidiMin(final float val) {
		this.midiMin = val;
	}
	public void setMidiMax(final float val) {
		this.midiMax = val;
	}
	
	private void sendMessage(final String s,final  float val) {
		PdBase.sendFloat(s, val);
	}
	private void sendMessage(final String s,final  float val,final  int index) {
		PdBase.sendFloat(s+index, val);
	}
	
	public void setPitch(final float val) {
		float pitch = midiMin + ((val+(1/(2*midiMax-2*midiMin))) * (midiMax-midiMin));
		if (quantize != NCONTINUOUS)
			pitch = (float)Math.floor(pitch);
		sendMessage("pitch", pitch);
	}
	public void setPitch(final float val,final int index,final Cursor c, final float width) {
		float pitch = midiMin + (val * (midiMax-midiMin));
		if (quantize != NCONTINUOUS) {
			if (quantize == NQUANTIZE || isCursorSnapped(c, width)) {
				pitch = (float)Math.round(pitch); //too close! round!
			} 
		}
		sendMessage("pitch", pitch, index);
	}
	public boolean isCursorSnapped(final Cursor c, final float width) {
		final float spacing = (midiMax-midiMin)/width;
		final int firstClosestX = Math.round((c.firstPoint.x) * spacing);
		final int lastClosestX = Math.round((c.currentPoint.x) * spacing);
		if (firstClosestX == lastClosestX) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void setVolume(final float amp) {
		volume.setVolume(amp);
	}
	
	
	public void setWaveform(final float waveform) {
		this.waveform = waveform;
		sendMessage("inssel", waveform);
	}
	
	
	public void updateSettings(final SharedPreferences prefs) {
		updateSettings(prefs, "");
	}
	public void updateSettings(final SharedPreferences prefs, final String preset) {
		try {
			final float prefMidiMin = prefs.getInt(preset+MIDI_MIN, 70);
			final float prefMidiMax = prefs.getInt(preset+MIDI_MAX, 86);
			setMidiMin(prefMidiMin);
			setMidiMax(prefMidiMax);			
			
			final String s_waveform = prefs.getString(preset+WAVEFORM, "1.0");
			final Float waveform = Float.parseFloat(s_waveform);
			setWaveform(waveform);
			
			String quantval = prefs.getString(preset+QUANTIZE, QUAT_CONTINUOUS);
			if (quantval.equalsIgnoreCase(QUAT_QUANTIZE)) {
				quantize = NQUANTIZE;
			} else if (quantval.equalsIgnoreCase(QUAT_SLIDE)) {
				quantize = NSLIDE;
			}
			
			vol_y = prefs.getBoolean(preset+VOLUME_Y, true);
			filt_y = prefs.getBoolean(preset+FILTER_Y, false);
			
			maxVol = prefs.getInt(preset+VOLUME, 80)/100f;
			maxFilt = prefs.getInt(preset+FILTER, 80)/100f;
			
			for (final Effect e : effects) {
				e.updateSettings(prefs, preset);
			}
		
		} catch (final Exception e) { e.printStackTrace(); }
	}
	
	public void updateSettingsFromJSON(JSONObject prefs) {
		try {
			final float prefMidiMin = prefs.has(MIDI_MIN) ? prefs.getInt(MIDI_MIN) : 70;
			final float prefMidiMax = prefs.has(MIDI_MAX) ? prefs.getInt(MIDI_MAX) : 86;
			setMidiMin(prefMidiMin);
			setMidiMax(prefMidiMax);			
			
			final String s_waveform = prefs.has(WAVEFORM) ? prefs.getString(WAVEFORM) : "1.0";
			final Float waveform = Float.parseFloat(s_waveform);
			setWaveform(waveform);
			
			String quantval = prefs.has(QUANTIZE) ?  prefs.getString(QUANTIZE) : QUAT_CONTINUOUS;
			if (quantval.equalsIgnoreCase(QUAT_QUANTIZE)) {
				quantize = NQUANTIZE;
			} else if (quantval.equalsIgnoreCase(QUAT_SLIDE)) {
				quantize = NSLIDE;
			}
			
			vol_y = prefs.has(VOLUME_Y) ? prefs.getBoolean(VOLUME_Y):  true;
			filt_y = prefs.has(FILTER_Y) ?  prefs.getBoolean(FILTER_Y) : false;
			
			maxVol = prefs.has(VOLUME) ?  prefs.getInt(VOLUME)/100f : 80f/100f;
			maxFilt = prefs.has(FILTER) ? prefs.getInt(FILTER)/100f : 80f/100f;
			
			for (final Effect e : effects) {
				e.updateSettingsFromJSON(prefs);
			}
		
		} catch (final Exception e) { e.printStackTrace(); }
	}
	
	public JSONObject saveSettingsToJSON(JSONObject prefs) {
		try {
			prefs.put(MIDI_MIN, this.midiMin);
			prefs.put(MIDI_MAX, this.midiMax);
	
			prefs.put(WAVEFORM, this.waveform);

			prefs.put(QUANTIZE, this.quantize);
			
			prefs.put(VOLUME_Y, this.vol_y);
			prefs.put(FILTER_Y, this.filt_y);

			prefs.put(VOLUME, this.maxVol);
			prefs.put(FILTER, this.maxFilt);
			
			for (final Effect e : effects) {
				e.saveSettingsToJSON(prefs);
			}	
			
			return prefs;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
	
	
	public void cleanup() {
//		PdUtils.closePatch(patch);
		PdBase.closePatch(patch);
	}


}
