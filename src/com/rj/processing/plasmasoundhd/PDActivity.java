package com.rj.processing.plasmasoundhd;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import processing.core.PApplet;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
	public Handler mHandler = new Handler(); 
	
	public PDManager pdman;
	public Instrument inst;
	public PlasmaSubFragment frag;
	public SequencerActivity sequencer;
	public CameraActivity cameratab;
	public PlasmaSound instrument;
	
	ArrayList<View> sideviews = new ArrayList<View>();
	
	HashMap<String, MenuItem> settingsItems = new HashMap<String, MenuItem>();
	
	PowerManager.WakeLock wl;
	Handler handler;

	
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
	Runnable postreadyrunnable = null;
	Runnable postresumerunnable = null;
	
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
			return com.rj.processing.plasmasoundhd.R.menu.main_menu; 
	}
	
	
	View loadingview;
	View preferenceview;
	
	public void onCreate(final Bundle savedinstance) {
		initStatics();
		super.onCreate(savedinstance);
		handler = new Handler();
		PSND.readFromResources(this);
		//runSequencer(false);
		plzBeLandscape();
		loadingview = this.getLayoutInflater().inflate(com.rj.processing.plasmasoundhd.R.layout.loadingscreenmall, null);
		this.addContentView(loadingview, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		if (isHoneycombOrGreater) {
			preferenceview = this.getLayoutInflater().inflate(
							com.rj.processing.plasmasoundhd.R.layout.prefsoverlay,
							null);
			this.addContentView(preferenceview, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}
		hideBoth();
		sequencer = new SequencerActivity(this);
		instrument = new PlasmaSound(this);
		if (!isHoneycombOrGreater)  {
			runTheremin(true,true);
		} else {
			setupSidebarList();
			cameratab = new CameraActivity(this); //honeycomb only at LEAST
			setupActionbar(getIntent());
		}
	}
	
	
	public void initStatics() {
		isHoneycombOrGreater = Build.VERSION.SDK_INT >= 11;
		Launcher.setUiType(this);
		PATCH_PATH = Launcher.getUIType() == Launcher.PHONE ? "simplesine.small.4.2.pd"  : "simplesine4.2.pd";
	}
	
	
	void setupActionbar(Intent intent) {
		if (getActionBar() == null) return;
	    final ActionBar actionBar = getActionBar();
	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	    // remove the activity title to make space for tabs
	    actionBar.setDisplayShowTitleEnabled(false);

	    actionBar.addTab(actionBar.newTab().setText(com.rj.processing.plasmasoundhd.R.string.instrument_name)
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

	    actionBar.addTab(actionBar.newTab().setText(com.rj.processing.plasmasoundhd.R.string.sequencer_name)
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
	    actionBar.addTab(actionBar.newTab().setText(com.rj.processing.plasmasoundhd.R.string.camera_name)
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
	    
	    if (intent.getCategories().contains("android.intent.category.DESK_DOCK"))
	    	actionBar.selectTab(actionBar.getTabAt(2 /*runCamera*/));
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	    if (intent.getCategories().contains("android.intent.category.DESK_DOCK")) {
			if (isHoneycombOrGreater)  {
				
				handler.post(new Runnable() {  public void run() {
					if (getActionBar() == null) return;
				    final ActionBar actionBar = getActionBar();
				    actionBar.selectTab(actionBar.getTabAt(2 /*runCamera*/));
				}});
			}
	    }
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		Log.d("PDActivity", "Running low on memory! ==========");
	}
	
	public void setupSidebarList() {
		View fragment = this.findViewById(com.rj.processing.plasmasoundhd.R.id.audiosettings);
		View fragment2 = this.findViewById(com.rj.processing.plasmasoundhd.R.id.instsettings);
		View fragment3 = this.findViewById(com.rj.processing.plasmasoundhd.R.id.sequencersettings);
		View fragment4 = this.findViewById(com.rj.processing.plasmasoundhd.R.id.motionsettings);
		sideviews.add(fragment);
		sideviews.add(fragment2);
		sideviews.add(fragment3);
		sideviews.add(fragment4);
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
			if (pdready)
				frag.setup();
			else
				postreadyrunnable = new Runnable() { public void run() { frag.setup(); } };
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
			if (pdready)
				frag.setup();
			else
				postreadyrunnable = new Runnable() { public void run() { frag.setup(); } };
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
			if (pdready)
				frag.setup();
			else
				postreadyrunnable = new Runnable() { public void run() { frag.setup(); } };
		}
		if (fragmentTransaction) {
			FragmentManager man = this.getSupportFragmentManager();
			FragmentTransaction trans = man.beginTransaction();
			removeTheremin(trans);
			removeSequencer(trans);
			addCamera(trans);
			//trans.commit();
			trans.commitAllowingStateLoss();
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
	    if (frag != null && pdready) frag.setup();
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
			if (postreadyrunnable != null) postreadyrunnable.run();
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
	    		if (getResources().getBoolean(com.rj.processing.plasmasoundhd.R.bool.should_bug_about_donate)) {
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
		Log.d("PDActivity", "New configuation!");
		plzBeLandscape();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
	    super.onSaveInstanceState(outState);
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
    public void onStart() {
    	super.onStart();
    	JSONSequencerPresets.getPresets().addListener(this);
    }

    @Override
    public void onStop() {
    	super.onStop();
    	JSONPresets.getPresets().removeListener(this);
    	JSONSequencerPresets.getPresets().removeListener(this);
		if (cameratab != null) cameratab.destroyCameraUI(); //just to be safe.
		if (frag != null) frag.background();
    }
    
    @Override
    protected void onRestart() {
    	super.onRestart();
		if (frag != null) frag.resume();
    }
	
	@Override
	public void onDestroy() {
		if (pdman != null) pdman.cleanup();
		if (frag != null) frag.destroy();
		if (wl != null && wl.isHeld()) wl.release();
		if (cameratab != null) cameratab.destroyCameraUI(); //just to be safe.
		super.onDestroy();
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
		checkAndGiveThemATutorial();
		if (frag != null) {
			frag.onResume();
			frag.resume();
		}
		if (postresumerunnable != null) {
			postresumerunnable.run();
			postresumerunnable = null;
		}
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
	    case com.rj.processing.plasmasoundhd.R.id.sequencer_settings:
	        sequencerSettings(item);
	        return true;
	    case com.rj.processing.plasmasoundhd.R.id.motion_settings:
	        motionSettings(item);
	        return true;
	    case com.rj.processing.plasmasoundhd.R.id.instrument:
	        instrument();
	        return true;
	    case com.rj.processing.plasmasoundhd.R.id.sequencer:
	        sequencer();
	        return true;
	    case com.rj.processing.plasmasoundhd.R.id.instrument_settings:
	        instrumentSettings(item);
	        return true;
	    case com.rj.processing.plasmasoundhd.R.id.effects_settings:
	        //item.setIcon(com.rj.processing.plasmasoundhd.R.drawable.ic_menu_record);
	        effectSettings(item);
	        return true;
	    case com.rj.processing.plasmasoundhd.R.id.save_settings:
	        saveSettings();
	        return true;
	    case com.rj.processing.plasmasoundhd.R.id.load_settings:
	        loadSettings();
	        return true;
	    case com.rj.processing.plasmasoundhd.R.id.about:
	        about();
	        return true;
	    case com.rj.processing.plasmasoundhd.R.id.record:
	        record();
	        return true;
	    case com.rj.processing.plasmasoundhd.R.id.tutorial:
	        showTutorialDialog();
	        return true;

	    default:
	    	frag.onOptionsItemSelected(item);
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	public void waveformEditor() {
		final Intent i = new Intent(this, WaveformEditor.class);
		this.startActivity(i);
	}
	
	public void sequencer() {
		runSequencer(true, true);
	}

	public void instrument() {
		runTheremin(true, true);
	}
	
	
	public boolean clearSidebar() {
		boolean anyvisible = false;
		for (View v : sideviews) {
			if (v.getVisibility() == View.VISIBLE)
				anyvisible = true;
			v.setVisibility(View.GONE);
		}
		setAllSettingsIconsInactive();
		return anyvisible;
	}
	
	public boolean checkSidebar() {
		boolean anyvisible = false;
		for (View v : sideviews) {
			if (v.getVisibility() == View.VISIBLE)
				anyvisible = true;
		}
		return anyvisible;
	}


	
	public void instrumentSettings(MenuItem item) {
		showSidebar(com.rj.processing.plasmasoundhd.R.id.audiosettings,
				com.rj.processing.plasmasound.PlasmaThereminAudioSettings.class, item, "keyboard",
				com.rj.processing.plasmasoundhd.R.drawable.ic_menu_keyboard_settings_active);
	}
	
	public void sequencerSettings(MenuItem item) {
		showSidebar(com.rj.processing.plasmasoundhd.R.id.sequencersettings,
				com.rj.processing.plasmasound.PlasmaThereminSequencerSettings.class, item, "sequencer",
				com.rj.processing.plasmasoundhd.R.drawable.ic_menu_sequencer_settings_active);
	}

	public void effectSettings(MenuItem item) {
		showSidebar(com.rj.processing.plasmasoundhd.R.id.instsettings,
				com.rj.processing.plasmasound.PlasmaThereminEffectsSettings.class, item, "effect",
				com.rj.processing.plasmasoundhd.R.drawable.ic_menu_effects_settings_active);
	}
	
	public void motionSettings(MenuItem item) {
		showSidebar(com.rj.processing.plasmasoundhd.R.id.motionsettings,
				null                                                              , item, "motion",
				com.rj.processing.plasmasoundhd.R.drawable.ic_menu_sequencer_settings_active);
	}
	
	public void setAllSettingsIconsInactive() {
		setInactiveSettingsIcon("effect", com.rj.processing.plasmasoundhd.R.drawable.ic_menu_effects_settings);
		setInactiveSettingsIcon("motion", com.rj.processing.plasmasoundhd.R.drawable.ic_menu_sequencer_settings);
		setInactiveSettingsIcon("sequencer", com.rj.processing.plasmasoundhd.R.drawable.ic_menu_sequencer_settings);
		setInactiveSettingsIcon("keyboard", com.rj.processing.plasmasoundhd.R.drawable.ic_menu_keyboard_settings);
	}

	
	public void showSidebar(int viewid, Class<?> fallback, MenuItem item, String name, int iconid) {
		if (!isHoneycombOrGreater) {
			final Intent i = new Intent(this, fallback);
			this.startActivity(i);
			return;
		}
		View fragment = this.findViewById(viewid);
		if (fragment != null) {
			if (fragment.getVisibility() == View.VISIBLE) {
				clearSidebar();
			} else {
				clearSidebar();
				fragment.setVisibility(View.VISIBLE);
				fragment.setBackgroundDrawable(getResources().getDrawable(com.rj.processing.plasmasoundhd.R.drawable.gradient));
				setActiveSettingsIcon(item, name, iconid);
			}
		}
	}
	public void setActiveSettingsIcon(MenuItem item, String name, int iconid) {
		settingsItems.put(name, item);
		item.setIcon(iconid);
	}
	public void setInactiveSettingsIcon(String name, int originalid) {
		MenuItem item = settingsItems.get(name);
		if (item != null)
			item.setIcon(originalid);
	}
	
	

	
	public void hideBoth() {
		if (isHoneycombOrGreater) {
			clearSidebar();
		}
	}

	
	@Override
    public boolean pOnBackPressed() {
		if (isHoneycombOrGreater) {
			boolean anyhidden = checkSidebar();
			if (anyhidden) {
				runOnUiThread(new Runnable() { public void run() {
					clearSidebar();
				}});
				return true;
			}
		} else {
			if (frag != instrument) {
				runTheremin(true, true);
				return true;
			}
		}
		
		//if we should actually go back, let's do a hack so we don't *actually*
		runOnUiThread(new Runnable() { public void run() {
			frag.destroy();
			finish();
			System.exit(0);
		}});
		return true;

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
