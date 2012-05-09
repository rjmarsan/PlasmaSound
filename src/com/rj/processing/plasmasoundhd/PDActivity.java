package com.rj.processing.plasmasoundhd;

import java.io.File;

import org.json.JSONObject;

import processing.core.PApplet;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import com.rj.processing.plasmasoundhd.pd.instruments.PSND;
import com.rj.processing.plasmasoundhd.sequencer.JSONSequencerPresets;

public class PDActivity extends PApplet implements TouchListener, PlasmaActivity, JSONPresets.PresetListener, JSONSequencerPresets.PresetListener {

	public static final String SHARED_PREFERENCES_AUDIO = "shared_prefs_audio";
	public static final String SHARED_PREFERENCES_APPSTUFF = "appstufffz";
	
	public static String PATCH_PATH;
//	public static final String PATCH_PATH = "simplesine4.2.pd";
	public static boolean isHoneycombOrGreater = false;
	
	public MTManager mtManager;
	
	public PDManager pdman;
	public Instrument inst;
	public PlasmaSubFragment frag;
	public SequencerActivity sequencer;
	public CameraActivity cameratab;
	public PlasmaSound instrument;
	
	MenuItem effectSettingsItem;
	MenuItem keyboardSettingsItem;
	MenuItem sequencerSettingsItem;
	
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
	public boolean keepTitlebar() { return isHoneycombOrGreater; }
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
		initStatics();
		super.onCreate(savedinstance);
		PSND.readFromResources(this);
		//runSequencer(false);
		plzBeLandscape();
		loadingview = this.getLayoutInflater().inflate(com.rj.processing.plasmasound.R.layout.loadingscreenmall, null);
		this.addContentView(loadingview, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		if (isHoneycombOrGreater) {
			preferenceview = this.getLayoutInflater().inflate(
							com.rj.processing.plasmasound.R.layout.prefsoverlay,
							null);
			this.addContentView(preferenceview, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}
		hideBoth();
		sequencer = new SequencerActivity(this);
		instrument = new PlasmaSound(this);
		cameratab = new CameraActivity(this);
		if (!isHoneycombOrGreater) 
			runTheremin(false,true);
		else
			setupActionbar();
	}
	
	
	public void initStatics() {
		isHoneycombOrGreater = Build.VERSION.SDK_INT >= 11;
		Launcher.setUiType(this);
		PATCH_PATH = Launcher.getUIType() == Launcher.PHONE ? "simplesine.small.4.2.pd"  : "simplesine4.2.pd";
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
					public void onTabReselected(Tab arg0, android.app.FragmentTransaction arg1) {
					}
					@Override
					public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
						runTheremin(true, true);
					}
					@Override
					public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
					}
				}), true);

	    actionBar.addTab(actionBar.newTab().setText(com.rj.processing.plasmasound.R.string.sequencer_name)
	            .setTabListener(new TabListener() {
					@Override
					public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
					}
					@Override
					public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
						runSequencer(true, true);
					}
					@Override
					public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
					}
				}));
	    actionBar.addTab(actionBar.newTab().setText(com.rj.processing.plasmasound.R.string.camera_name)
	            .setTabListener(new TabListener() {
					@Override
					public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
					}
					@Override
					public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
						runCamera(true, true);
					}
					@Override
					public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
					}
				}));
	}
	
	
	public void runTheremin(boolean setup, boolean fragmentTransaction)  {
		//Log.d("PDActivity", "Running theremin");
		hideBoth();
		if (frag != null) {
			//Log.d("PDActivity", "Backgrounding current");
			frag.background();
		}
		frag = instrument;
		if (setup) {
			//Log.d("PDActivity", "Setting up theremin");
			frag.setup();
		}
		if (fragmentTransaction) {
			FragmentManager man = this.getSupportFragmentManager();
			FragmentTransaction trans = man.beginTransaction();
			removeSequencer(trans);
			removeCamera(trans);
			addTheremin(trans);
			trans.commit();
		}
	}
	public void runSequencer(boolean setup, boolean fragmentTransaction) {
		//Log.d("PDActivity", "Running sequencer");
		hideBoth();
		if (frag != null) {
			//Log.d("PDActivity", "Backgrounding current");
			frag.background();
		}
		frag = sequencer;
		if (setup) {
			//Log.d("PDActivity", "Setting up sequencer");
			frag.setup();
		}
		if (fragmentTransaction) {
			FragmentManager man = this.getSupportFragmentManager();
			FragmentTransaction trans = man.beginTransaction();
			removeTheremin(trans);
			removeCamera(trans);
			addSequencer(trans);
			trans.commit();
		}
	}
	public void runCamera(boolean setup, boolean fragmentTransaction) {
		hideBoth();
		if (frag != null) {
			frag.background();
		}
		frag = cameratab;
		if (setup) {
			frag.setup();
		}
		if (fragmentTransaction) {
			FragmentManager man = this.getSupportFragmentManager();
			FragmentTransaction trans = man.beginTransaction();
			removeTheremin(trans);
			removeSequencer(trans);
			addCamera(trans);
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
	public void removeCamera(FragmentTransaction ft) {
		android.support.v4.app.FragmentManager man = this.getSupportFragmentManager();
		if (man.findFragmentByTag(CameraActivity.TAG) != null)
			ft.remove(cameratab);
	}
	public void addCamera(FragmentTransaction trans) {
		android.support.v4.app.FragmentManager man = this.getSupportFragmentManager();
		if (man.findFragmentByTag(CameraActivity.TAG) == null)
			trans.add(cameratab, CameraActivity.TAG);
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
		checkAndGiveThemATutorial();
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
//	        	if (System.currentTimeMillis() % 10 == 1) {
	    		if (getResources().getBoolean(com.rj.processing.plasmasound.R.bool.should_bug_about_donate)) {
	        		showRatingDialog();
	        	}
	        	Editor e = mPrefs.edit();
	        	e.putBoolean("popupshown", true);
	        	e.commit();
	        }
        }
	}
	
	public void checkAndGiveThemATutorial() {
        final SharedPreferences mPrefs = PDActivity.this.getSharedPreferences(SHARED_PREFERENCES_APPSTUFF, 0);
        if (!(mPrefs.getBoolean("tutorialshown", false))) {
    		showTutorialDialog();
        	Editor e = mPrefs.edit();
        	e.putBoolean("tutorialshown", true);
        	e.commit();
        }
	}

	void showTutorialDialog() {
		MiscDialogs.showTutorialDialog(this);
	}
	
	void showRatingDialog() {
		MiscDialogs.showRatingDialog(this);
	}
	 
	void showAboutDialog() { 
		MiscDialogs.showAboutDialog(this);
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
		//menu.
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
	        sequencerSettings(item);
	        return true;
	    case com.rj.processing.plasmasound.R.id.instrument:
	        instrument();
	        return true;
	    case com.rj.processing.plasmasound.R.id.sequencer:
	        sequencer();
	        return true;
	    case com.rj.processing.plasmasound.R.id.instrument_settings:
	        instrumentSettings(item);
	        return true;
	    case com.rj.processing.plasmasound.R.id.effects_settings:
	        //item.setIcon(com.rj.processing.plasmasound.R.drawable.ic_menu_record);
	        effectSettings(item);
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
	    case com.rj.processing.plasmasound.R.id.clear_sequence_settings:
	        clearSequenceSettings();
	        return true;
	    case com.rj.processing.plasmasound.R.id.about:
	        about();
	        return true;
	    case com.rj.processing.plasmasound.R.id.record:
	        record();
	        return true;
	    case com.rj.processing.plasmasound.R.id.camera:
	        camera();
	        return true;
	    case com.rj.processing.plasmasound.R.id.tutorial:
	        showTutorialDialog();
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

	
	public void instrumentSettings(MenuItem item) {
		if (!isHoneycombOrGreater) {
			final Intent i = new Intent(this, com.rj.processing.plasmasound.PlasmaThereminAudioSettings.class);
			this.startActivity(i);
			return;
		}
		View fragment = this.findViewById(com.rj.processing.plasmasound.R.id.audiosettings);
		View fragment2 = this.findViewById(com.rj.processing.plasmasound.R.id.instsettings);
		View fragment3 = this.findViewById(com.rj.processing.plasmasound.R.id.sequencersettings);
		if (fragment != null && fragment2 != null) {
			setAllSettingsIconsInactive();
			fragment2.setVisibility(View.GONE);
			fragment3.setVisibility(View.GONE);
			if (fragment.isShown()) {
				fragment.setVisibility(View.GONE);
			} else {
				fragment.setVisibility(View.VISIBLE);
				fragment.setBackgroundDrawable(getResources().getDrawable(com.rj.processing.plasmasound.R.drawable.gradient));
				setActiveInstrumentSettingsIcon(item);
			}
		}
	}
	
	public void setActiveInstrumentSettingsIcon(MenuItem item) {
		keyboardSettingsItem = item;
		item.setIcon(com.rj.processing.plasmasound.R.drawable.ic_menu_keyboard_settings_active);
	}
	public void setInactiveInstrumentSettingsIcon() {
		if (keyboardSettingsItem != null)
			keyboardSettingsItem.setIcon(com.rj.processing.plasmasound.R.drawable.ic_menu_keyboard_settings);
	}

	
	
	public void sequencerSettings(MenuItem item) {
		if (!isHoneycombOrGreater) {
			final Intent i = new Intent(this, com.rj.processing.plasmasound.PlasmaThereminSequencerSettings.class);
			this.startActivity(i);
			return;
		}
		View fragment = this.findViewById(com.rj.processing.plasmasound.R.id.audiosettings);
		View fragment2 = this.findViewById(com.rj.processing.plasmasound.R.id.instsettings);
		View fragment3 = this.findViewById(com.rj.processing.plasmasound.R.id.sequencersettings);
		if (fragment != null && fragment2 != null) {
			setAllSettingsIconsInactive();
			fragment2.setVisibility(View.GONE);
			fragment.setVisibility(View.GONE);
			if (fragment3.isShown()) {
				fragment3.setVisibility(View.GONE);
			} else {
				fragment3.setVisibility(View.VISIBLE);
				fragment3.setBackgroundDrawable(getResources().getDrawable(com.rj.processing.plasmasound.R.drawable.gradient));
				setActiveSequencerSettingsIcon(item);
			}
		}
	}
	public void setActiveSequencerSettingsIcon(MenuItem item) {
		sequencerSettingsItem = item;
		item.setIcon(com.rj.processing.plasmasound.R.drawable.ic_menu_sequencer_settings_active);
	}
	public void setInactiveSequencerSettingsIcon() {
		if (sequencerSettingsItem != null)
			sequencerSettingsItem.setIcon(com.rj.processing.plasmasound.R.drawable.ic_menu_sequencer_settings);
	}



	public void effectSettings(MenuItem item) {
		if (!isHoneycombOrGreater) {
			final Intent i = new Intent(this, com.rj.processing.plasmasound.PlasmaThereminEffectsSettings.class);
			this.startActivity(i);
			return;
		}
		View fragment = this.findViewById(com.rj.processing.plasmasound.R.id.instsettings);
		View fragment2 = this.findViewById(com.rj.processing.plasmasound.R.id.audiosettings);
		View fragment3 = this.findViewById(com.rj.processing.plasmasound.R.id.sequencersettings);
		if (fragment != null && fragment2 != null) {
			setAllSettingsIconsInactive();
			fragment2.setVisibility(View.GONE);
			fragment3.setVisibility(View.GONE);
			if (fragment.isShown()) {
				fragment.setVisibility(View.GONE);
			} else {
				fragment.setVisibility(View.VISIBLE);
				fragment.setBackgroundDrawable(getResources().getDrawable(com.rj.processing.plasmasound.R.drawable.gradient));
				setActiveEffectSettingsIcon(item);
			}
		}
	}
	public void setActiveEffectSettingsIcon(MenuItem item) {
		effectSettingsItem = item;
		item.setIcon(com.rj.processing.plasmasound.R.drawable.ic_menu_effects_settings_active);
	}
	public void setInactiveEffectSettingsIcon() {
		if (effectSettingsItem != null)
			effectSettingsItem.setIcon(com.rj.processing.plasmasound.R.drawable.ic_menu_effects_settings);
	}
	
	public void setAllSettingsIconsInactive() {
		//Log.d("PDActivity", "Hiding all icons");
		setInactiveEffectSettingsIcon();
		setInactiveInstrumentSettingsIcon();
		setInactiveSequencerSettingsIcon();
	}

	
	public void hideBoth() {
		if (isHoneycombOrGreater) {
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
			setAllSettingsIconsInactive(); 
		}
	}

	
	@Override
    public boolean pOnBackPressed() {
		if (isHoneycombOrGreater) {
			View fragment = this.findViewById(com.rj.processing.plasmasound.R.id.instsettings);
			View fragment2 = this.findViewById(com.rj.processing.plasmasound.R.id.audiosettings);
			System.out.println("fragment1" +fragment.isShown()+ "   fragment2:"+fragment2.isShown());
			if (fragment.isShown() || fragment2.isShown()) {
				System.out.println("Hiding fragments");
				runOnUiThread(new Runnable() { public void run() {
					hideBoth(); 
				}});
				return true;
			} else {
				return false;
			}
		} else {
			if (frag == sequencer) {
				runTheremin(true, true);
				return true;
			}
			return false;
		}
    }
	
	@Override
	public void onBackPressed() {
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
	public void clearSequenceSettings() {
		sequencer.clear();
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
		MiscDialogs.checkForSoundcloudAndDoThatOrNot(this, filename, name);
	}

	
	public void neverShowSoundcloudAgain() {
        final SharedPreferences mPrefs = PDActivity.this.getSharedPreferences(SHARED_PREFERENCES_APPSTUFF, 0);
    	Editor e = mPrefs.edit();
    	e.putBoolean("soundcloud_ask", false);
    	e.commit();
	}
	
	public boolean shouldShowSoundcloud() {
        final SharedPreferences mPrefs = PDActivity.this.getSharedPreferences(SHARED_PREFERENCES_APPSTUFF, 0);
        return mPrefs.getBoolean("soundcloud_ask", true);
	}

	
	public void camera() {
		Log.d("Camera", "initializing camera!");
		instrument.toggleCamera();
	}
	
	
	@Override
	public void onActivityResult(final int i, final int j, final Intent res) {
		super.onActivityResult(i, j, res);
		readSettings();
	}

    public void readSettings() {
    	if (inst == null) {
    		//Log.d("ReadSettings", "Called with inst being null!");
    		//Thread.dumpStack();
    		return;
    	}
        final SharedPreferences mPrefs = PDActivity.this.getSharedPreferences(SHARED_PREFERENCES_AUDIO, 0);
		//Log.d("ReadSettings", "Called with inst being all good");
    	inst.updateSettings(this, mPrefs);
    }

    
    
    @Override
    public void onStop() {
    	super.onStop();
    	instrument.removeCamera();
    	JSONPresets.getPresets().removeListener(this);
    	JSONSequencerPresets.getPresets().removeListener(this);
    }
    
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//		try {
//			//Log.d("PDActivity", "Destroying! saving!!!");
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
