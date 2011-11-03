package com.rj.processing.plasmasoundhd;

import java.io.File;

import org.json.JSONObject;

import processing.core.PApplet;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import com.rj.processing.mt.Cursor;
import com.rj.processing.mt.MTManager;
import com.rj.processing.mt.TouchListener;
import com.rj.processing.plasmasoundhd.pd.PDManager;
import com.rj.processing.plasmasoundhd.pd.instruments.Instrument;
import com.rj.processing.plasmasoundhd.pd.instruments.JSONPresets;
import com.rj.processing.plasmasoundhd.sequencer.JSONSequencerPresets;

public class PDActivity extends PApplet implements TouchListener, PlasmaActivity, JSONPresets.PresetListener, JSONSequencerPresets.PresetListener {

	public static final String SHARED_PREFERENCES_AUDIO = "shared_prefs_audio";
	public static final String SHARED_PREFERENCES_APPSTUFF = "appstufffz";
	
	public static final String PATCH_PATH = Launcher.getUIType() == Launcher.GINGERBREAD_PHONE ? "simplesine.small.4.2.pd"  : "simplesine4.2.pd";
//	public static final String PATCH_PATH = "simplesine4.2.pd";
	
	
	public MTManager mtManager;
	
	public PDManager pdman;
	public Instrument inst;
	public PlasmaSubFragment frag;
	public SequencerActivity sequencer;
	public PlasmaSound instrument;
	
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
	public String sketchRenderer() { return PApplet.P3D; } 
	public boolean keepTitlebar() { return Launcher.getUIType() != Launcher.GINGERBREAD_PHONE; }
	/** return false to keep presets from being loaded **/
	public boolean loadPresets() { return true; }
	/** override to select a custom menu **/
	int getMenu() { 
		if (frag != null) 
			return frag.getMenu(); 
		else 
			return com.rj.processing.plasmasound.R.menu.main_menu; 
	}
	
	
	View loadingview;
	View preferenceview;
	
	public void onCreate(final Bundle savedinstance) {
		super.onCreate(savedinstance);
		//runSequencer(false);
		plzBeLandscape();
		loadingview = this.getLayoutInflater().inflate(com.rj.processing.plasmasound.R.layout.loadingscreenmall, null);
		this.addContentView(loadingview, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		if (Launcher.getUIType() != Launcher.GINGERBREAD_PHONE) {
			preferenceview = this.getLayoutInflater().inflate(
							com.rj.processing.plasmasound.R.layout.prefsoverlay,
							null);
			this.addContentView(preferenceview, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}
		hideBoth();
		sequencer = new SequencerActivity(this);
		instrument = new PlasmaSound(this);
		if (Build.VERSION.SDK_INT < 11) 
			runTheremin(false,true);
		else
			setupActionbar();
	}
	
	void setupActionbar() {
		if (getActionBar() == null) return;
	    final ActionBar actionBar = getActionBar();
	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	    // remove the activity title to make space for tabs
	    actionBar.setDisplayShowTitleEnabled(false);

	    actionBar.addTab(actionBar.newTab().setText(com.rj.processing.plasmasound.R.string.instrument_name)
	            .setTabListener(new TabListener() {

					@Override
					public void onTabReselected(Tab arg0,
							android.app.FragmentTransaction arg1) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onTabSelected(Tab tab,
							android.app.FragmentTransaction ft) {
						runTheremin(true, true);
						//addTheremin(ft);
						
					}

					@Override
					public void onTabUnselected(Tab tab,
							android.app.FragmentTransaction ft) {
						//removeTheremin(ft);
						
					}
				}), true);

	    actionBar.addTab(actionBar.newTab().setText(com.rj.processing.plasmasound.R.string.sequencer_name)
	            .setTabListener(new TabListener() {
					@Override
					public void onTabReselected(Tab tab,
							android.app.FragmentTransaction ft) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onTabSelected(Tab tab,
							android.app.FragmentTransaction ft) {
						runSequencer(true, true);
						//addSequencer(ft);
						
					}

					@Override
					public void onTabUnselected(Tab tab,
							android.app.FragmentTransaction ft) {
						//removeSequencer(ft);
						
					}
				}));
	}
	
	
	public void runTheremin(boolean setup, boolean fragmentTransaction)  {
		hideBoth();
		if (frag != null) frag.background();
		frag = instrument;
		if (setup) frag.setup();
		if (fragmentTransaction) {
			FragmentManager man = this.getSupportFragmentManager();
			FragmentTransaction trans = man.beginTransaction();
			removeSequencer(trans);
			addTheremin(trans);
			trans.commit();
		}
	}
	public void runSequencer(boolean setup, boolean fragmentTransaction) {
		hideBoth();
		if (frag != null) frag.background();
		frag = sequencer;
		if (setup) frag.setup();
		if (fragmentTransaction) {
			FragmentManager man = this.getSupportFragmentManager();
			FragmentTransaction trans = man.beginTransaction();
			removeTheremin(trans);
			addSequencer(trans);
			trans.commit();
		}
	}

	
	public void removeTheremin(FragmentTransaction trans) {
		android.support.v4.app.FragmentManager man = this.getSupportFragmentManager();
		if (man.findFragmentByTag(PlasmaSound.TAG) != null)
			trans.remove(instrument);
	}
	public void addTheremin(FragmentTransaction trans) {
		android.support.v4.app.FragmentManager man = this.getSupportFragmentManager();
		if (man.findFragmentByTag(PlasmaSound.TAG) == null)
			trans.add(instrument, PlasmaSound.TAG);
	}
	public void removeSequencer(FragmentTransaction ft) {
		android.support.v4.app.FragmentManager man = this.getSupportFragmentManager();
		if (man.findFragmentByTag(SequencerActivity.TAG) != null)
			ft.remove(sequencer);
	}
	public void addSequencer(FragmentTransaction trans) {
		android.support.v4.app.FragmentManager man = this.getSupportFragmentManager();
		if (man.findFragmentByTag(SequencerActivity.TAG) == null)
			trans.add(sequencer, SequencerActivity.TAG);
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
	    if (frag != null) frag.setup();
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
			loadPresets();
			//readSettings();
			if (loadPresets()) {
				if (JSONPresets.getPresets().loadDefault(PDActivity.this, inst) == null) {}//if there is no defaults
					//readSettings();	    
			} else {
				//readSettings();
			}
			readSettings();	    
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
		if (frag != null) frag.touchAllUp(c);
	}
	@Override
	public void touchDown(final Cursor c) {
		if (frag != null) frag.touchDown(c);
	}
	
	@Override
	public void touchMoved(final Cursor c) {
		if (frag != null) frag.touchMoved(c);
	}
	
	@Override
	public void touchUp(final Cursor c) {
		if (frag != null) frag.touchUp(c);
	}
	
	
	
	@Override
	public void draw() {	
		if (frag != null) frag.draw();
	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "PlasmaSoundHDLock");
		wl.acquire();
		if (loadingview == null)
			loadingview = this.findViewById(com.rj.processing.plasmasound.R.id.loadingview);
		loadingview.setVisibility(View.VISIBLE);
		if (pdready == true) {
		    pdready = false;
			if (pdman != null) pdman.onResume(readyrunnable);
		}
		readSettings();
		
		checkAndRemindThemToGiveMeAGoodRating();
		if (frag != null) frag.onResume();
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
        if (!(mPrefs.getBoolean("popupshown", false))) {
	        if (elapsed > maxtime) {
	        	if (System.currentTimeMillis() % 10 == 1) {
	        		showRatingDialog();
	        	}
	        	Editor e = mPrefs.edit();
	        	e.putBoolean("popupshown", true);
	        	e.commit();
	        }
        }
	}
	
	void showRatingDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Rate Plasma Sound!");
		builder.setMessage("Plasma Sound is 100% ad free and cost free. Show your support by leaving a comment on the Market.\n(This won't be shown again, I promise)");
		
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
	}
	
	void showAboutDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("About Plasma Sound!");
		builder.setMessage("Plasma Sound is written by RJ Marsan, and is 100% ad free and cost free. Show your support by leaving a comment on the Market, or leave a donation below:");
		
		builder.setPositiveButton("Market", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id="+getApplication().getPackageName()));
				startActivity(intent);
				dialog.dismiss();
			}});
		
		builder.setNeutralButton("Donate", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(PDActivity.this, "Coming soon!", Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}});

		builder.setNegativeButton("Never show again", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}});
		AlertDialog alert = builder.create();
		
		alert.show();
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
		if (wl != null && wl.isHeld()) wl.release();
		if (frag != null) frag.onPause();
	}

	
	
	@Override
	public void onDestroy() {
		if (pdman != null) pdman.cleanup();
		super.onDestroy();
		if (wl != null && wl.isHeld()) wl.release();
	}


	
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
//	    final MenuInflater inflater = getMenuInflater();
//	    inflater.inflate(getMenu(), menu);
	    return true;
	}
	
	/**
	 * its 1:42, and where is Jake?
	 * Rj is goofy as fuck.
	 */
	
	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
	    switch (item.getItemId()) {
	    case com.rj.processing.plasmasound.R.id.sequencer_settings:
	        sequencerSettings();
	        return true;
	    case com.rj.processing.plasmasound.R.id.instrument:
	        instrument();
	        return true;
	    case com.rj.processing.plasmasound.R.id.sequencer:
	        sequencer();
	        return true;
	    case com.rj.processing.plasmasound.R.id.instrument_settings:
	        instrumentSettings();
	        return true;
	    case com.rj.processing.plasmasound.R.id.effects_settings:
	        effectSettings();
	        return true;
	    case com.rj.processing.plasmasound.R.id.save_settings:
	        saveSettings();
	        return true;
	    case com.rj.processing.plasmasound.R.id.load_settings:
	        loadSettings();
	        return true;
	    case com.rj.processing.plasmasound.R.id.load_sequence_settings:
	        loadSequenceSettings();
	        return true;
	    case com.rj.processing.plasmasound.R.id.save_sequence_settings:
	        saveSequenceSettings();
	        return true;
	    case com.rj.processing.plasmasound.R.id.about:
	        about();
	        return true;
	    case com.rj.processing.plasmasound.R.id.record:
	        record();
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
//		final Intent i = new Intent(this, SequencerActivity.class);
//		this.startActivity(i);
		runSequencer(true, true);
	}

	public void instrument() {
//		final Intent i = new Intent(this, SequencerActivity.class);
//		this.startActivity(i);
		runTheremin(true, true);
	}

	
	public void instrumentSettings() {
		if (Launcher.getUIType() == Launcher.GINGERBREAD_PHONE) {
			final Intent i = new Intent(this, com.rj.processing.plasmasound.PlasmaThereminAudioSettings.class);
			this.startActivity(i);
			return;
		}
		View fragment = this.findViewById(com.rj.processing.plasmasound.R.id.audiosettings);
		View fragment2 = this.findViewById(com.rj.processing.plasmasound.R.id.instsettings);
		View fragment3 = this.findViewById(com.rj.processing.plasmasound.R.id.sequencersettings);
		if (fragment != null && fragment2 != null) {
			fragment2.setVisibility(View.GONE);
			fragment3.setVisibility(View.GONE);
			if (fragment.isShown()) {
				fragment.setVisibility(View.GONE);
			} else {
				fragment.setVisibility(View.VISIBLE);
				fragment.setBackgroundDrawable(getResources().getDrawable(com.rj.processing.plasmasound.R.drawable.gradient));
			}
		}
	}
	
	public void sequencerSettings() {
		if (Launcher.getUIType() == Launcher.GINGERBREAD_PHONE) {
			final Intent i = new Intent(this, com.rj.processing.plasmasound.PlasmaThereminSequencerSettings.class);
			this.startActivity(i);
			return;
		}
		View fragment = this.findViewById(com.rj.processing.plasmasound.R.id.audiosettings);
		View fragment2 = this.findViewById(com.rj.processing.plasmasound.R.id.instsettings);
		View fragment3 = this.findViewById(com.rj.processing.plasmasound.R.id.sequencersettings);
		if (fragment != null && fragment2 != null) {
			fragment2.setVisibility(View.GONE);
			fragment.setVisibility(View.GONE);
			if (fragment3.isShown()) {
				fragment3.setVisibility(View.GONE);
			} else {
				fragment3.setVisibility(View.VISIBLE);
				fragment3.setBackgroundDrawable(getResources().getDrawable(com.rj.processing.plasmasound.R.drawable.gradient));
			}
		}
	}

	public void effectSettings() {
		if (Launcher.getUIType() == Launcher.GINGERBREAD_PHONE) {
			final Intent i = new Intent(this, com.rj.processing.plasmasound.PlasmaThereminEffectsSettings.class);
			this.startActivity(i);
			return;
		}
		View fragment = this.findViewById(com.rj.processing.plasmasound.R.id.instsettings);
		View fragment2 = this.findViewById(com.rj.processing.plasmasound.R.id.audiosettings);
		View fragment3 = this.findViewById(com.rj.processing.plasmasound.R.id.sequencersettings);
		if (fragment != null && fragment2 != null) {
			fragment2.setVisibility(View.GONE);
			fragment3.setVisibility(View.GONE);
			if (fragment.isShown()) {
				fragment.setVisibility(View.GONE);
			} else {
				fragment.setVisibility(View.VISIBLE);
				fragment.setBackgroundDrawable(getResources().getDrawable(com.rj.processing.plasmasound.R.drawable.gradient));
			}
		}
	}
	
	public void hideBoth() {
		if (Launcher.getUIType() != Launcher.GINGERBREAD_PHONE) {
			View fragment = this.findViewById(com.rj.processing.plasmasound.R.id.instsettings);
			View fragment2 = this.findViewById(com.rj.processing.plasmasound.R.id.audiosettings);
			View fragment3 = this.findViewById(com.rj.processing.plasmasound.R.id.sequencersettings);
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
			View fragment = this.findViewById(com.rj.processing.plasmasound.R.id.instsettings);
			View fragment2 = this.findViewById(com.rj.processing.plasmasound.R.id.audiosettings);
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
	
	public void saveSequenceSettings() {
		JSONSequencerPresets.getPresets().showSaveMenu(this, sequencer);
	}
	public void loadSequenceSettings() {
		JSONSequencerPresets.getPresets().showLoadMenu(this, sequencer);
	}
	
	public void about() {
		showAboutDialog();
	}
	
	public void record() {
		String name;
		try {
			name = JSONPresets.getPresets().getCurrent().getString("name");
			if (sequencer.isAdded()) {
				name += "_"+JSONSequencerPresets.getPresets().getCurrent().getString("name");
			}
		} catch (Exception e) {
			e.printStackTrace();
			name = "errorerrorerror";
		}
		File outfolder = new File(Environment.getExternalStorageDirectory(), "Plasma Sound");
		outfolder.mkdirs();
		String filename = pdman.recordOnOff(outfolder, name, true);
		if (filename != null) {
		    Intent share = new Intent(Intent.ACTION_SEND);
		    share.setType("audio/wav");

		    Uri uri = Uri.fromFile(new File(filename));
		    share.putExtra(Intent.EXTRA_STREAM, uri);
		    share.putExtra(Intent.EXTRA_TEXT, "Check out the recording I made with Plasma Sound!");

		    startActivity(Intent.createChooser(share, "Share Recording"));
		    
			Toast.makeText(this, "The recording is on your phone at: "+filename, Toast.LENGTH_LONG).show();

		} else {
			Toast.makeText(this, "Select record again to finish recording", Toast.LENGTH_LONG).show();
		}
	}

	
	@Override
	public void onActivityResult(final int i, final int j, final Intent res) {
		super.onActivityResult(i, j, res);
		readSettings();
	}

    public void readSettings() {
    	if (inst == null) {
    		Log.d("ReadSettings", "Called with inst being null!");
    		Thread.dumpStack();
    		return;
    	}
        final SharedPreferences mPrefs = PDActivity.this.getSharedPreferences(SHARED_PREFERENCES_AUDIO, 0);
		Log.d("ReadSettings", "Called with inst being all good");
    	inst.updateSettings(this, mPrefs);
    }

    
    
    @Override
    public void onStop() {
    	super.onStop();
    	JSONPresets.getPresets().removeListener(this);
    	JSONSequencerPresets.getPresets().removeListener(this);
    }
    
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//		try {
//			Log.d("PDActivity", "Destroying! saving!!!");
//			JSONPresets.getPresets().savePreset(this, inst);
//			JSONSequencerPresets.getPresets().savePreset(this, sequencer.sequencer);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//    	super.onSaveInstanceState(outState);
//    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	JSONSequencerPresets.getPresets().addListener(this);
    }

	@Override
	public void presetChanged(JSONObject preset) {
		if (frag != null) frag.presetChanged(preset);
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
