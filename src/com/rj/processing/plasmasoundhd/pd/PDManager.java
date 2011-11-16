/**
 * 
 * @author Peter Brinkmann (peter.brinkmann@gmail.com)
 * 
 * For information on usage and redistribution, and for a DISCLAIMER OF ALL
 * WARRANTIES, see the file, "LICENSE.txt," in this distribution.
 * 
 */

package com.rj.processing.plasmasoundhd.pd;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import processing.core.PApplet;
import android.util.Log;
import android.widget.Toast;

import com.rj.processing.plasmasounddonate.R;


public class PDManager {
	


	public boolean recording = false;
	String recording_filename = "";
	final PApplet p;
	private static final int SAMPLE_RATE = 44100;
	private static final String TAG = "Plasma Theremin";

	private Toast toast = null;
	
	private void toast(final String msg) {
		p.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (toast == null) {
					toast = Toast.makeText(p.getApplicationContext(), "", Toast.LENGTH_SHORT);
				}
				toast.setText(TAG + ": " + msg);
				toast.show();
			}
		});
	}
	
	
	AudioStatListener reciever = new AudioStatListener();

	public PDManager(final PApplet p) {
		this.p = p;
	}

	public void onResume(final Runnable callback) {
		new Thread(new Runnable() { 
			public void run() {
				onResume();
				callback.run();
			}}).start();
	}
	public void onResume() {
		if (AudioParameters.suggestSampleRate() < SAMPLE_RATE) {
			toast("required sample rate not available; exiting");
			finish();
			return;
		}
		final int nOut = Math.min(AudioParameters.suggestOutputChannels(), 2);
		if (nOut == 0) {
			toast("audio output not available; exiting");
			finish();
			return;
		}
		try {
			PdAudio.initAudio(SAMPLE_RATE, 0, nOut, 1, true);
			PdAudio.startAudio(p);
			PdBase.setReceiver(reciever);
			PdBase.subscribe("mainlevel");
			} catch (final IOException e) {
			Log.e(TAG, e.toString());
		}
	}
	
	public void onPause() {
		PdAudio.stopAudio();
	}
	
	public void onDestroy() {
		cleanup();
	}
	
	public void finish() {
		Log.d(TAG, "Finishing for some reason");
		cleanup();
	}

	
	public int openPatch(final String patch) {
		final File dir = p.getFilesDir();
		final File patchFile = new File(dir, patch);
		int out=-1;
		try {
			IoUtils.extractZipResource(p.getResources().openRawResource(R.raw.patch), dir, true);
//			out = PdUtils.openPatch(patchFile.getAbsolutePath());
			out = PdBase.openPatch(patchFile.getAbsolutePath());
		} catch (final IOException e) {
			e.printStackTrace();
			Log.e(TAG, e.toString() + "; exiting now");
			finish();
		}
		return out;
	}
	
	public float getVolumeLevel() {
		return reciever.audiolevel;
	}

		

	public void cleanup() {
		// make sure to release all resources
		PdAudio.stopAudio();
		PdBase.release();
	}
	
	private static final char[] ILLEGAL_CHARACTERS = { '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':' };
	public String recordOnOff(File parentdir, String name, boolean addTimestamp) {
		if (recording) {
			String filename = recording_filename;
			endRecord();
			return filename;
		} else {
			String filename = name;
			if (addTimestamp) {
				Date d = new Date();
				filename += "-"+d.toString();
			}
			filename = filename.replace(' ','_');
			filename = filename.replace('/','_');
			for (char c : ILLEGAL_CHARACTERS) {
				filename = filename.replace(c, '.');
			}
			filename += ".wav";
			filename = parentdir.getAbsolutePath()+"/" + filename;
//			filename = parentdir.getAbsolutePath()+"ughfuckinghell.wav";
			startRecord(filename);
			return null;
		}
	}
	
	public void startRecord(String filename) {
		Log.d("PdManager", "Starting recording at : "+filename);
		recording = true;
		recording_filename = filename;
		// /PdBase.sendFloat("record_onoff", 1);
		PdBase.sendSymbol("record_filename", filename);
		PdBase.sendBang("record_start");
	}
	public void endRecord() {
		Log.d("PdManager", "Ending recording at : "+recording_filename);
		PdBase.sendBang("record_stop");
		//PdBase.sendFloat("record_onoff", 1);
		recording = false;
		recording_filename = null;
	}

}
