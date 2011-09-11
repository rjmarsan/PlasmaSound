package com.rj.processing.plasmasoundhd;

import org.json.JSONObject;

import processing.core.PApplet;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.rj.processing.mt.Cursor;
import com.rj.processing.mt.MTManager;
import com.rj.processing.mt.TouchListener;
import com.rj.processing.plasmasoundhd.pd.PDManager;
import com.rj.processing.plasmasoundhd.pd.instruments.Instrument;
import com.rj.processing.plasmasoundhd.pd.instruments.JSONPresets;

public class PDActivity extends PApplet implements TouchListener, PlasmaActivity, JSONPresets.PresetListener {

	public static final String SHARED_PREFERENCES_AUDIO = "shared_prefs_audio";
	public static final String SHARED_PREFERENCES_APPSTUFF = "appstufffz";
	
	public static final String PATCH_PATH = Launcher.getUIType() == Launcher.GINGERBREAD_PHONE ? "simplesine.small.4.2.pd"  : "simplesine4.2.pd";
	
	
	public MTManager mtManager;
	
	public PDManager pdman;
	public Instrument inst;
	
	PowerManager.WakeLock wl;

	
	boolean pdready = false;
	boolean startingup = true;
	Runnable readyrunnable = new Runnable() {
		public void run() {
			if (startingup == false) {
				pdready = false;
				if (pdman != null) {
					pdman.onResume();
				
				
					pdready = true;
					Log.v("PlasmaSoundReadyRunnable", "Destroying popup!");
				}
				runOnUiThread(new Runnable() { public void run() {loadingview.setVisibility(View.GONE);}});
			}
		}
	};
	
	public int sketchWidth() { return this.screenWidth; }
	public int sketchHeight() { return this.screenHeight; }
	public String sketchRenderer() { return PApplet.OPENGL; }
	public boolean keepTitlebar() { return Launcher.getUIType() != Launcher.GINGERBREAD_PHONE; }
	/** return false to keep presets from being loaded **/
	public boolean loadPresets() { return true; }
	/** override to select a custom menu **/
	int getMenu() { return com.rj.processing.plasmasoundhd.R.menu.main_menu; }
	
	
	View loadingview;
	View preferenceview;
	
	public void onCreate(final Bundle savedinstance) {
		super.onCreate(savedinstance);
		plzBeLandscape();
		loadingview = this.getLayoutInflater().inflate(com.rj.processing.plasmasoundhd.R.layout.loadingscreenmall, null);
		this.addContentView(loadingview, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		if (Launcher.getUIType() != Launcher.GINGERBREAD_PHONE) {
			preferenceview = this.getLayoutInflater().inflate(
							com.rj.processing.plasmasoundhd.R.layout.prefsoverlay,
							null);
			this.addContentView(preferenceview, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}
		hideBoth();

	}
	
	
	
	@Override
	public void setup() {
		hint(DISABLE_DEPTH_TEST);
		hint(DISABLE_OPENGL_ERROR_REPORT);
		hint(PApplet.DISABLE_ACCURATE_TEXTURES);
		hint(PApplet.DISABLE_DEPTH_MASK);
		hint(PApplet.DISABLE_DEPTH_SORT);
	    frameRate(60);
	
	    mtManager = new MTManager();
	    mtManager.addTouchListener(this);
	    	    
	    asyncSetup.execute(new Void[0]);
	    debug();
	}
	
	AsyncTask<Void,Void,Void> asyncSetup = new AsyncTask<Void,Void,Void>() {
		@Override
		protected Void doInBackground(final Void... params) {
			startingup = true;
			Log.v("PlasmaSoundSetup", "creating pd");
		    //PD Stuff
		    pdman = new PDManager(PDActivity.this);
			Log.v("PlasmaSoundSetup", "launching pd");
		    pdready = false;
		    pdman.onResume();
		    
			Log.v("PlasmaSoundSetup", "Starting instrument");
		    //Make the Instrument
		    inst = new Instrument(pdman);
			Log.v("PlasmaSoundSetup", "setting instrument patch");
		    inst.setPatch(PATCH_PATH);
		    inst.setMidiMin(70);
		    inst.setMidiMax(87);
		    
			Log.v("PlasmaSoundSetup", "Reading settings");
			if (loadPresets()) {
				if (JSONPresets.getPresets().loadDefault(PDActivity.this, inst) == null) //if there is no defaults
					readSettings();	    
			} else {
				readSettings();
			}
			//readSettings();	    
			Log.v("PlasmaSoundSetup", "Done!");
			return null;
		}
		
		@Override
		protected void onPostExecute(final Void params) {
			Log.v("PlasmaSoundSetup", "Destroying popup!");
			pdready = true;
			startingup = false;
			loadingview.setVisibility(View.GONE);
		}
	};
	
	
	
	public void debug() {
		  // Place this inside your setup() method
		  final DisplayMetrics dm = new DisplayMetrics();
		  getWindowManager().getDefaultDisplay().getMetrics(dm);
		  final float density = dm.density; 
		  final int densityDpi = dm.densityDpi;
		  println("density is " + density); 
		  println("densityDpi is " + densityDpi);
		  println("HEY! the screen size is "+width+"x"+height);
	}
	
	
	//mt version
	public boolean surfaceTouchEvent(final MotionEvent me) {
		if (mtManager != null) mtManager.surfaceTouchEvent(me);
		return super.surfaceTouchEvent(me);
	}
	

	@Override
	public void touchAllUp(final Cursor c) {
		
	}
	@Override
	public void touchDown(final Cursor c) {
	}
	
	@Override
	public void touchMoved(final Cursor c) {
	}
	
	@Override
	public void touchUp(final Cursor c) {
	}
	
	
	
	@Override
	public void draw() {	
	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "PlasmaSoundHDLock");
		wl.acquire();
		if (loadingview == null)
			loadingview = this.findViewById(com.rj.processing.plasmasoundhd.R.id.loadingview);
		loadingview.setVisibility(View.VISIBLE);
		if (pdready == true) {
		    pdready = false;
			if (pdman != null) pdman.onResume(readyrunnable);
		}
		readSettings();
		
		checkAndRemindThemToGiveMeAGoodRating();
	}
	
	
	
	public void checkAndRemindThemToGiveMeAGoodRating() {
        final SharedPreferences mPrefs = PDActivity.this.getSharedPreferences(SHARED_PREFERENCES_APPSTUFF, 0);
        long firstopened = mPrefs.getLong("firstopened", -1);
        if (firstopened < 0) {
        	firstopened = System.currentTimeMillis();
        	Editor e = mPrefs.edit();
        	e.putLong("firstopened", firstopened);
        	e.commit();
        	return;
        }
        long timenow = System.currentTimeMillis();
        long elapsed = timenow - firstopened;
        long maxtime = 1000 * 60 * 15; //15 minute
        if (elapsed > maxtime && !(mPrefs.getBoolean("popupshown", false) )) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Rate Plasma Sound!");
			builder.setMessage("Plasma Sound is 100% ad free and cost free. Show your support by leaving a comment on the Market.");
			
//			TextView txt = new TextView(this);
//			txt.setTextSize(15);
//			txt.setText("Plasma Sound is written and maintained by me, RJ Marsan.\nIts 100% ad free and cost free, so show your support, or give me feedback by commenting on the Market \nor sending me an email. \nThis message won't be shown again. Promise!");
//			txt.setLayoutParams(new LayoutParams(300, 200));
//			txt.setPadding(10, 10, 10, 10);
//			txt.setBackgroundColor(Color.CYAN);
//			txt.setLines(6);
			//builder.setView(txt);
			
			builder.setPositiveButton("Market", new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse("market://details?id="+getApplication().getPackageName()));
					startActivity(intent);
					dialog.dismiss();
				}});
			builder.setNegativeButton("Never show again", new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}});
			AlertDialog alert = builder.create();
			
			alert.show();
        	Editor e = mPrefs.edit();
        	e.putBoolean("popupshown", true);
        	e.commit();
        }
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		plzBeLandscape();
	}
	
	public void plzBeLandscape() {
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.FROYO)
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		else
			setRequestedOrientation(6/** == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE**/);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (pdman != null) pdman.onPause();
		wl.release();
	}
	
	@Override
	public void onDestroy() {
		if (pdman != null) pdman.cleanup();
		super.onDestroy();
		wl.release();
	}


	
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
	    final MenuInflater inflater = getMenuInflater();
	    inflater.inflate(getMenu(), menu);
	    return true;
	}
	
	/**
	 * its 1:42, and where is Jake?
	 * Rj is goofy as fuck.
	 */
	
	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
	    switch (item.getItemId()) {
	    case com.rj.processing.plasmasoundhd.R.id.sequencer_settings:
	        sequencerSettings();
	        return true;
	    case com.rj.processing.plasmasoundhd.R.id.sequencer:
	        sequencer();
	        return true;
	    case com.rj.processing.plasmasoundhd.R.id.instrument_settings:
	        instrumentSettings();
	        return true;
	    case com.rj.processing.plasmasoundhd.R.id.effects_settings:
	        effectSettings();
	        return true;
	    case com.rj.processing.plasmasoundhd.R.id.save_settings:
	        saveSettings();
	        return true;
	    case com.rj.processing.plasmasoundhd.R.id.load_settings:
	        loadSettings();
	        return true;

	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	public void waveformEditor() {
		final Intent i = new Intent(this, WaveformEditor.class);
		this.startActivity(i);
	}
	
	public void sequencer() {
		final Intent i = new Intent(this, SequencerActivity.class);
		this.startActivity(i);
	}

	
	public void instrumentSettings() {
		if (Launcher.getUIType() == Launcher.GINGERBREAD_PHONE) {
			final Intent i = new Intent(this, com.rj.processing.plasmasound.PlasmaThereminAudioSettings.class);
			this.startActivity(i);
			return;
		}
		View fragment = this.findViewById(com.rj.processing.plasmasoundhd.R.id.audiosettings);
		View fragment2 = this.findViewById(com.rj.processing.plasmasoundhd.R.id.instsettings);
		View fragment3 = this.findViewById(com.rj.processing.plasmasoundhd.R.id.sequencersettings);
		if (fragment != null && fragment2 != null) {
			fragment2.setVisibility(View.GONE);
			fragment3.setVisibility(View.GONE);
			if (fragment.isShown()) {
				fragment.setVisibility(View.GONE);
			} else {
				fragment.setVisibility(View.VISIBLE);
				fragment.setBackgroundDrawable(getResources().getDrawable(com.rj.processing.plasmasoundhd.R.drawable.gradient));
			}
		}
	}
	
	public void sequencerSettings() {
		if (Launcher.getUIType() == Launcher.GINGERBREAD_PHONE) {
			final Intent i = new Intent(this, com.rj.processing.plasmasound.PlasmaThereminSequencerSettings.class);
			this.startActivity(i);
			return;
		}
		View fragment = this.findViewById(com.rj.processing.plasmasoundhd.R.id.audiosettings);
		View fragment2 = this.findViewById(com.rj.processing.plasmasoundhd.R.id.instsettings);
		View fragment3 = this.findViewById(com.rj.processing.plasmasoundhd.R.id.sequencersettings);
		if (fragment != null && fragment2 != null) {
			fragment2.setVisibility(View.GONE);
			fragment.setVisibility(View.GONE);
			if (fragment3.isShown()) {
				fragment3.setVisibility(View.GONE);
			} else {
				fragment3.setVisibility(View.VISIBLE);
				fragment3.setBackgroundDrawable(getResources().getDrawable(com.rj.processing.plasmasoundhd.R.drawable.gradient));
			}
		}
	}

	public void effectSettings() {
		if (Launcher.getUIType() == Launcher.GINGERBREAD_PHONE) {
			final Intent i = new Intent(this, com.rj.processing.plasmasound.PlasmaThereminEffectsSettings.class);
			this.startActivity(i);
			return;
		}
		View fragment = this.findViewById(com.rj.processing.plasmasoundhd.R.id.instsettings);
		View fragment2 = this.findViewById(com.rj.processing.plasmasoundhd.R.id.audiosettings);
		View fragment3 = this.findViewById(com.rj.processing.plasmasoundhd.R.id.sequencersettings);
		if (fragment != null && fragment2 != null) {
			fragment2.setVisibility(View.GONE);
			fragment3.setVisibility(View.GONE);
			if (fragment.isShown()) {
				fragment.setVisibility(View.GONE);
			} else {
				fragment.setVisibility(View.VISIBLE);
				fragment.setBackgroundDrawable(getResources().getDrawable(com.rj.processing.plasmasoundhd.R.drawable.gradient));
			}
		}
	}
	
	public void hideBoth() {
		if (Launcher.getUIType() != Launcher.GINGERBREAD_PHONE) {
			View fragment = this.findViewById(com.rj.processing.plasmasoundhd.R.id.instsettings);
			View fragment2 = this.findViewById(com.rj.processing.plasmasoundhd.R.id.audiosettings);
			View fragment3 = this.findViewById(com.rj.processing.plasmasoundhd.R.id.sequencersettings);
			if (fragment != null && fragment2 != null) {
				fragment2.setVisibility(View.GONE);
				fragment.setVisibility(View.GONE);
			}
			if (fragment3 != null) {
				fragment3.setVisibility(View.GONE);
			}
		}
	}

	
    @Override
    public void onBackPressed() {
		if (Launcher.getUIType() != Launcher.GINGERBREAD_PHONE) {
			View fragment = this.findViewById(com.rj.processing.plasmasoundhd.R.id.instsettings);
			View fragment2 = this.findViewById(com.rj.processing.plasmasoundhd.R.id.audiosettings);
			System.out.println("fragment1" +fragment.isShown()+ "   fragment2:"+fragment2.isShown());
			if (fragment.isShown() || fragment2.isShown()) {
				System.out.println("Hiding fragments");
				hideBoth();
			} else {
				//super.onBackPressed();
			}
		}
    }


	public void saveSettings() {
		JSONPresets.getPresets().showSaveMenu(this, this);
	}
	public void loadSettings() {
		JSONPresets.getPresets().showLoadMenu(this, this);
	}
	
	@Override
	public void onActivityResult(final int i, final int j, final Intent res) {
		super.onActivityResult(i, j, res);
		readSettings();
	}

    public void readSettings() {
        final SharedPreferences mPrefs = PDActivity.this.getSharedPreferences(SHARED_PREFERENCES_AUDIO, 0);
    	if (inst!=null) inst.updateSettings(mPrefs);
    }

    
    
    @Override
    public void onStop() {
    	super.onStop();
    	JSONPresets.getPresets().removeListener(this);
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	JSONPresets.getPresets().addListener(this);
    }

	@Override
	public void presetChanged(JSONObject preset) {
	}

    
	@Override
	public Instrument getInst() {
		return inst;
	}
	@Override
	public MTManager getMTManager() {
		return mtManager;
	}
	@Override
	public PDManager getPD() {
		return pdman;
	}

    

}
