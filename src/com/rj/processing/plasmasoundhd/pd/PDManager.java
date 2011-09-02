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

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.core.PdBase;
import org.puredata.core.PdReceiver;
import org.puredata.core.utils.IoUtils;

import processing.core.PApplet;
import android.util.Log;
import android.widget.Toast;

import com.rj.processing.plasmasoundhd.PlasmaSound;
import com.rj.processing.plasmasoundhd.R;


public class PDManager {
	


	
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
	
	public class AudioStatListener implements PdReceiver{
		public float audiolevel = 0f;

		@Override
		public void print(final String s) {	
			Log.d("PDManager", "recieved print! "+s);

		}

		@Override
		public void receiveBang(final String source) {
			Log.d("PDManager", "recieved bang! "+source);
		}

		@Override
		public void receiveFloat(final String source, final float x) {
			Log.d("PDManager", "recieved float! "+source+" : "+x);
			if (source.equalsIgnoreCase("mainlevel")) {
				audiolevel = x;
			}
		}
		@Override
		public void receiveList(final String source, final Object... args) {
			Log.d("PDManager", "recieved list! "+source);
		}
		@Override
		public void receiveMessage(final String source, final String symbol,
				final Object... args) {	
			Log.d("PDManager", "recieved message! "+source);
		}
		@Override
		public void receiveSymbol(final String source, final String symbol) {		
			Log.d("PDManager", "recieved symbol! "+source);
		}
	};
	
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
//			PdBase.setReceiver(reciever);
//			PdBase.subscribe("mainlevel");
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
	

}
