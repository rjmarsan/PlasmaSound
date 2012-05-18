package com.rj.processing.plasmasoundhd.sequencer;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.SystemClock;
import android.util.Log;

import com.rj.processing.plasmasoundhd.PDActivity;
import com.rj.processing.plasmasoundhd.pd.effects.MotionStuff;
import com.rj.processing.plasmasoundhd.pd.effects.SequencerStuff;
import com.rj.processing.plasmasoundhd.pd.instruments.Instrument;

public class CameraPatterns {
	public static final int MAJOR = 0;
	public static final int MINOR = 1;
	public static final int PENTATONIC = 2;
	public static final int WHOLE = 3;
	public static final int HALF = 4;
	
	
	public Instrument instrument;
	public volatile float[][] grid;
	public float bpm;
	public float syncopated;
	public int key = 0; /** c = 0, c# = 1... **/
	public int mode = MAJOR; /** MAJOR or MINOR, etc **/
	public SequenceThread sequenceThread;
	public int currentRow = -1;
	
	
	int[] majorscale = {0, 4, 7};
	int[] minorscale = {0, 3, 7};
	int[] pentatonic = {0, 3, 5, 7, 10};
	int[] wholenotes = {0, 2, 4, 5, 7, 9, 11};
	int[] halfnotes = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
	
	
	public class SequenceThread extends Thread {
		public boolean sequenceKeepRunning = true;
		
		@Override
		public void run() {
			try {
				long lastpause = SystemClock.uptimeMillis();
				int count = 0;
				
				while(sequenceKeepRunning && grid != null) {
					Log.d("Sequencer", "Starting loop with sequence thread. sequenceKeepRunning: "+sequenceKeepRunning + " grid:"+grid+ " grid.length:"+grid.length);
					try {
						for (int i=0; i<grid.length; i++) {
							float[][] grid = CameraPatterns.this.grid;
							currentRow = i;
							int countInternal = count;
		
							for (int j=0; j<grid[i].length; j++) {
								if (sequenceKeepRunning && grid[i][j] != Sequencer.OFF) {
									countInternal = (countInternal + 1)%Instrument.MAX_INDEX;
									sendNoteOn(i,j, grid[i][j], countInternal);
								}
							}
							
							try {
								float bpm = CameraPatterns.this.bpm;
								float syncopation = CameraPatterns.this.syncopated;
								if (instrument != null) {
									bpm = instrument.motion.bpm.getDefaultValue();
									syncopation = instrument.motion.syncopated.getDefaultValue();
								}
								long waittime = (long) (1/bpm * 1000 /*milliseconds*/ * 60 /*seconds*/);
								if (currentRow % 2 == 0) {
									waittime = (long)(waittime + waittime * (syncopation/100f));
								} else {
									waittime = (long)(waittime - waittime * (syncopation/100f));
								}
								long waitedtime = SystemClock.uptimeMillis() - lastpause;
								if (sequenceKeepRunning && waittime - waitedtime > 0) Thread.sleep(waittime - waitedtime);
								lastpause = SystemClock.uptimeMillis();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
							
							countInternal = count; //reset the count so we can turn Sequencer.OFF those sequencers
							for (int j=0; j<grid[i].length; j++) {
								if (sequenceKeepRunning && grid[i][j] != Sequencer.OFF) {
									countInternal = (countInternal + 1)%Instrument.MAX_INDEX;
									sendNoteOff(i,j, grid[i][j], countInternal);
								}
							}
							 
							count = countInternal;
		
						}
					} catch (Exception e) {
						try {
							e.printStackTrace();
							float bpm = CameraPatterns.this.bpm;
							if (instrument != null) bpm = instrument.motion.bpm.getDefaultValue();
							long waittime = (long) (1/bpm * 1000 /*milliseconds*/ * 60 /*seconds*/);
							if (sequenceKeepRunning) Thread.sleep(waittime);
						} catch (InterruptedException ee) {
							ee.printStackTrace();
						}
	
					}
				}
				if (instrument != null) instrument.allUp();
				currentRow = -1;
			} catch (Exception e) {
				e.printStackTrace();
				//uber exception tracking
			}
			Log.d("Sequencer", "Done with sequence thread. sequenceKeepRunning: "+sequenceKeepRunning + " grid:"+grid);

		}
		
		
		
		private void sendNoteOn(int i, int j, float val, int index) {
			if (instrument == null) return;
			
			float note = getNote(j);

			
			float midiMin = instrument.midiMin;
			float midiMax  = instrument.midiMax;
			instrument.setMidiMin(0);
			instrument.setMidiMax(127);
			
			
			//Log.d("Sequencer", "NOTE ON: "+index);
			instrument.touchDown(null, index+1, note, 127, 1-val, 1, null);
			instrument.touchMove(null, index+1, note, 127, 1-val, 1, null);
			
			
			instrument.setMidiMin(midiMin);
			instrument.setMidiMax(midiMax);

		}
		
		private void sendNoteOff(int i, int j, float val, int index) {
			if (instrument == null) return;
			float note = getNote(j);
			//Log.d("Sequencer", "NOTE Sequencer.OFF: "+index);
			instrument.touchUp(null, index+1, note, 127, 0.72f, 1, null);
		}
		
	}
	
	public float getNote(int column) {
		if (instrument == null) return -1;
		
		int[] scale = pentatonic;
		int scaletype = (int)instrument.motion.scale.getDefaultValue();
		if (scaletype == MAJOR)
			scale = majorscale;
		else if (scaletype == MINOR)
			scale = minorscale;
		else if (scaletype == PENTATONIC)
			scale = pentatonic;
		else if (scaletype == WHOLE)
			scale = wholenotes;
		else if (scaletype == HALF)
			scale = halfnotes;
			
		int octaves = column / scale.length;
		int value = column % scale.length;
		int baseNote = (int)instrument.motion.lownote.getDefaultValue();
		
		return baseNote + 12*octaves + scale[value];
	}

	
	public CameraPatterns(Instrument instrument, int width, int height, float bpm) {
		grid = new float[width][];
		for (int i=0; i<width; i++) {
			grid[i] = new float[height];
			Arrays.fill(grid[i], Sequencer.OFF);
		}
		this.instrument = instrument;
		this.bpm = bpm;
	}
	
	public void setFromSettings(MotionStuff s, boolean run) {
		int width = (int)s.steps.getDefaultValue();
		int height = (int)s.notes.getDefaultValue();
		float bpm = s.bpm.getDefaultValue();
		float syncopated = s.syncopated.getDefaultValue();
		setTempo(bpm);
		setSyncopation(syncopated);
		if (grid.length != width || grid[0].length != height) {
			boolean restart = false;
			if (sequenceThread != null && sequenceThread.sequenceKeepRunning == true) {
				restart = true;
				stop();
			}
			
			float[][] grid = copyGrid(width, height, true);
			this.grid = grid;
			if (restart && run) start();
		}
	}
	public float[][] copyGrid(boolean preserve) {
		return copyGrid(grid.length, grid[0].length, preserve);
	}
	public float[][] copyGrid(int width, int height, boolean preserve) {
		float[][] grid = new float[width][];
		for (int i=0; i<width; i++) {
			grid[i] = new float[height];
			Arrays.fill(grid[i], Sequencer.OFF);
			if (preserve) {
				for (int j=0; j<height; j++) {
					if (i < this.grid.length && j < this.grid[i].length) {
						grid[i][j] = this.grid[i][j];
					}
				}
			}
		}
		return grid;
	}
	
	public void clear() {
		float[][] grid = copyGrid(false);
		this.grid = grid;
	}
	
	public void setSpot(int x, int y, float value) {
		float[][] grid = copyGrid(true);
		grid[x][y] = value;
		this.grid = grid;
	}
	
	
	public JSONObject sequenceToJSON(JSONObject sequence) throws JSONException {
		try {
//			sequence.put("bpm", instrument.motion.bpm.getDefaultValue());
//			sequence.put("syncopated", instrument.motion.syncopated.getDefaultValue());
//			sequence.put("lownote", instrument.motion.lownote.getDefaultValue());
//			sequence.put("notes", instrument.motion.notes.getDefaultValue());
//			sequence.put("steps", instrument.motion.steps.getDefaultValue());
//			sequence.put("scale", (int)instrument.motion.scale.getDefaultValue());
			
			instrument.motion.saveSettingsToJSON(sequence);

			JSONArray array = new JSONArray();
			for (int i=0; i<grid.length; i++) {
				JSONArray subarray = new JSONArray();
				for (int j=0; j<grid[i].length; j++) {
					subarray.put(grid[i][j]);
				}
				array.put(subarray);
			}
			sequence.put("array", array);
			return sequence;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public void loadSequence(Context context, JSONObject sequence) {
		MotionStuff stuff = this.instrument.motion;
			try {
//				stuff.bpm.setDefault((float)sequence.getDouble("bpm"));
//				stuff.syncopated.setDefault((float)sequence.getDouble("syncopated"));
//				stuff.lownote.setDefault((float)sequence.getDouble("lownote"));
//				stuff.notes.setDefault((float)sequence.getDouble("notes"));
//				stuff.steps.setDefault((float)sequence.getDouble("steps"));
//				stuff.scale.setDefault(sequence.getInt("scale"));
				SharedPreferences prefs = context.getSharedPreferences(PDActivity.SHARED_PREFERENCES_AUDIO, 0);
				Editor edit = prefs.edit();
				stuff.updateSettingsFromJSON(sequence, true, edit);
				edit.commit();
				setFromSettings(stuff, true);
				JSONArray array = sequence.getJSONArray("array");
				for (int i=0; i<grid.length; i++) {
					JSONArray subarray = array.getJSONArray(i);
					for (int j=0; j<grid[i].length; j++) {
						grid[i][j] = (float)subarray.getDouble(j);
					}
					array.put(subarray);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
	} 
	
	
	
	public synchronized void start() {
		Log.d("Sequencer", "Starting sequencer...");
		if (sequenceThread != null) sequenceThread.sequenceKeepRunning = false;
		sequenceThread = new SequenceThread();
		sequenceThread.sequenceKeepRunning = true;
		sequenceThread.start();
	}
	
	public synchronized void stop() {
		Log.d("Sequencer", "Stopping sequencer...");
		if (sequenceThread != null) {
			sequenceThread.sequenceKeepRunning = false;
			sequenceThread = null;
		}
	}
	
	public void setTempo(float bpm) {
		this.bpm = bpm;
	}
	
	public void setSyncopation(float syncopated) {
		this.syncopated = syncopated;
	}
	
	public void setMode(int mode) {
		this.mode = mode;
	}

}
