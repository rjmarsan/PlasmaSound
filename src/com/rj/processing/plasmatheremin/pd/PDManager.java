/**
 * 
 * @author Peter Brinkmann (peter.brinkmann@gmail.com)
 * 
 * For information on usage and redistribution, and for a DISCLAIMER OF ALL
 * WARRANTIES, see the file, "LICENSE.txt," in this distribution.
 * 
 */

package com.rj.processing.plasmatheremin.pd;

import java.io.File;
import java.io.IOException;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;
import org.puredata.core.utils.PdUtils;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.rj.processing.plasmatheremin.PlasmaTheremin;
import com.rj.processing.plasmatheremin.R;


public class PDManager {
	
	final PlasmaTheremin p;
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

	public PDManager(PlasmaTheremin p) {
		this.p = p;
	}

	
	public void onResume() {
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
		} catch (IOException e) {
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
		cleanup();
	}

	
	public String openPatch(String patch) {
		File dir = p.getFilesDir();
		File patchFile = new File(dir, patch);
		String out=null;
		try {
			IoUtils.extractZipResource(p.getResources().openRawResource(R.raw.patch), dir, true);
			out = PdUtils.openPatch(patchFile.getAbsolutePath());
		} catch (IOException e) {
			Log.e(TAG, e.toString() + "; exiting now");
			finish();
		}
		return out;
	}
		

	public void cleanup() {
		// make sure to release all resources
		PdAudio.stopAudio();
		PdBase.release();
	}

}
