/**
 * 
 * @author Peter Brinkmann (peter.brinkmann@gmail.com)
 * 
 * For information on usage and redistribution, and for a DISCLAIMER OF ALL
 * WARRANTIES, see the file, "LICENSE.txt," in this distribution.
 * 
 */

package com.rj.processing.plasmasound.pd;

import java.io.File;
import java.io.IOException;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.core.PdBase;
import org.puredata.core.PdReceiver;
import org.puredata.core.utils.IoUtils;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.rj.processing.plasmasound.PlasmaSound;
import com.rj.processing.plasmasound.R;


public class PDManager {
	


	
	final PlasmaSound p;
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
	
	public class AudioStatListener implements PdReceiver{
		public float audiolevel = 0f;

		@Override
		public void print(String s) {	
			Log.d("PDManager", "recieved print! "+s);

		}

		@Override
		public void receiveBang(String source) {
			Log.d("PDManager", "recieved bang! "+source);
		}

		@Override
		public void receiveFloat(String source, float x) {
			Log.d("PDManager", "recieved float! "+source+" : "+x);
			if (source.equalsIgnoreCase("mainlevel")) {
				audiolevel = x;
			}
		}
		@Override
		public void receiveList(String source, Object... args) {
			Log.d("PDManager", "recieved list! "+source);
		}
		@Override
		public void receiveMessage(String source, String symbol,
				Object... args) {	
			Log.d("PDManager", "recieved message! "+source);
		}
		@Override
		public void receiveSymbol(String source, String symbol) {		
			Log.d("PDManager", "recieved symbol! "+source);
		}
	};
	
	AudioStatListener reciever = new AudioStatListener();

	public PDManager(PlasmaSound p) {
		this.p = p;
	}

	
	public void onResume(final Runnable callback) {
		new Thread(new Runnable() { 
			public void run() {
				if (AudioParameters.suggestSampleRate() < SAMPLE_RATE) {
					toast("required sample rate not available; exiting");
					finish();
					return;
				}
				int nOut = Math.min(AudioParameters.suggestOutputChannels(), 2);
				if (nOut == 0) {
					toast("audio output not available; exiting");
					finish();
					return;
				}
				try {
					PdAudio.initAudio(SAMPLE_RATE, 0, nOut, 1, true);
					PdAudio.startAudio(p);
		//			PdBase.setReceiver(reciever);
		//			PdBase.subscribe("mainlevel");
					} catch (IOException e) {
					Log.e(TAG, e.toString());
				}
				callback.run();
			}}).start();
	}
	
	public void onPause() {
		PdAudio.stopAudio();
	}
	
	public void onDestroy() {
		cleanup();
	}
	
	public void finish() {
		cleanup();
	}

	
	public int openPatch(String patch) {
		File dir = p.getFilesDir();
		File patchFile = new File(dir, patch);
		int out=-1;
		try {
			IoUtils.extractZipResource(p.getResources().openRawResource(R.raw.patch), dir, true);
//			out = PdUtils.openPatch(patchFile.getAbsolutePath());
			out = PdBase.openPatch(patchFile.getAbsolutePath());
		} catch (IOException e) {
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
	

}
