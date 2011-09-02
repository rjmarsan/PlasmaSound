package com.rj.processing.plasmasoundhd.pd.instruments;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;

import com.rj.processing.plasmasound.PlasmaSound;
import com.rj.processing.plasmasoundhd.PlasmaActivity;

public class JSONPresets {
	public static String PRESETS = "PRESETS";
	public static String JSON_FILENAME = "presets.json";

	
	
	
	private static JSONPresets singleton;
	public static JSONPresets getPresets() {
		if (singleton != null) {
			singleton = new JSONPresets();
		}
		return singleton;
	}

	
	
	
	private JSONObject currentsetting;
	
	
	
	
	public JSONObject getCurrent() {
		if (currentsetting == null) {
			JSONObject newsetting = new JSONObject();
			try {
				newsetting.put("name", "Default");
			} catch (JSONException j) {
				j.printStackTrace();
			}
			currentsetting = newsetting;
		}
		return currentsetting;
	}
	
	
	
	public JSONObject readFromPreferences(Context c ) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("name", "From Preferences");
			String[] defaults = { "vibspeed", "midimax", "quantize_note_list",
					"delayfeedback", "sustain", "tremolospeed", "tremolodepth",
					"midimin", "waveform", "vibdepth", "filt", "revebrtime",
					"reverbfeedback", "delaytime", "volume", "attack",
					"release", "amp", "filter", "decay" };
			final SharedPreferences mPrefs = c.getSharedPreferences(
					PlasmaSound.SHARED_PREFERENCES_AUDIO, 0);
			for (String s : defaults) {
				if (mPrefs.contains(s)) {
					obj.put(s, mPrefs.getInt(s, 0));
				}
				if (mPrefs.contains(s+"_y")) {
					obj.put(s+"_y", mPrefs.getBoolean(s+"_y", false));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	
	
	

	public void showLoadMenu(final Context c, final PlasmaActivity p) {
		try {
			final String[] items = getPresetNames(c);
			
			AlertDialog.Builder builder = new AlertDialog.Builder(c);
			builder.setTitle("Pick a saved instance");
			builder.setItems(items, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
			        loadPreset(c,p.getInst(), items[item]);
			    }
			});
			AlertDialog alert = builder.create();
			alert.show();
		} catch (Exception e) {
			
		}
	}
	
	public void showSaveMenu(final Context c, final PlasmaActivity p) {
		try {
			final String[] items = getPresetNames(c);
			
			AlertDialog.Builder builder = new AlertDialog.Builder(c);
			builder.setTitle("Pick a saved instance");
			builder.setItems(items, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
			        savePreset(c,p.getInst(), items[item]);
			    }
			});
			builder.setNeutralButton("New", new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int whichButton) {  
					showSaveAsMenu(c, p);
				}
				}); 
			AlertDialog alert = builder.create();
			alert.show();
		} catch (Exception e) {
			
		}
	}
	
	public void showSaveAsMenu(final Context c, final PlasmaActivity p ) {
		AlertDialog.Builder builder = new AlertDialog.Builder(c);
		builder.setTitle("Name?");
		builder.setMessage("Pick a name for the preset");
		final EditText text = new EditText(c);
		builder.setView(text);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int whichButton) {  
			  String value = text.getText().toString();  
			  savePreset(c, p.getInst(), value);
			}  
			}); 
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	
	public void loadDefault(Context c, Instrument e) {
		JSONObject jpreset = getDefaultPreset(c);
		currentsetting = jpreset;
		e.updateSettingsFromJSON(jpreset);
	}
	
	public void loadPreset(Context c, Instrument e, String preset) {
		JSONObject jpreset = getPresetFromName(preset, c);
		currentsetting = jpreset;
		e.updateSettingsFromJSON(jpreset);
	}
	
	public void savePreset(Context c, Instrument e, String preset) {
		try {
			JSONObject presetsobj = getPresets(c);
			if (presetsobj == null) {
				presetsobj = new JSONObject();
				JSONArray presets = new JSONArray();
				presetsobj.put("presets", presets);
			}
			if (currentsetting != null) {
				presetsobj.put("default", currentsetting.getString("name"));
			}
			JSONArray presets = presetsobj.getJSONArray("presets");
			JSONObject presetobj = new JSONObject();
			presetobj.put("name", preset);
			presetobj = e.saveSettingsToJSON(presetobj);
			presets.put(presetobj);
			writePresets(presetsobj, c);
		} catch (Exception j ) {
			j.printStackTrace();
		}
	}
	
	public void updateDefault(Context c) {
		try {
			JSONObject presetsobj = getPresets(c);
			if (currentsetting != null) {
				presetsobj.put("default", currentsetting.getString("name"));
				writePresets(presetsobj, c);
			}
		} catch (Exception j ) {
			j.printStackTrace();
		}
	}

	
	
	
	public JSONObject getPresets(Context context) {
		try {
			File jsonFile = new File(context.getFilesDir(), JSON_FILENAME);
			if (!jsonFile.exists()) return null;
		    byte[] buffer = new byte[(int) jsonFile.length()];
		    BufferedInputStream f = new BufferedInputStream(new FileInputStream(jsonFile));
		    f.read(buffer);		
			String jsonString = new String(buffer);
			JSONObject object = new JSONObject(jsonString);
			return object;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void writePresets(JSONObject json, Context context) {
		try {
			File jsonFile = new File(context.getFilesDir(), JSON_FILENAME);
			
			String out = json.toString(4);
			Log.d("Presets", "Presets:"+out);
		    BufferedOutputStream f = new BufferedOutputStream(new FileOutputStream(jsonFile));
		    f.write(out.getBytes());
		    f.flush();
		    f.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public JSONObject getDefaultPreset(Context context) {
		try {
			JSONObject presetobj = getPresets(context);
			if (presetobj == null) return null;
			if (presetobj.has("default")) {
				String defaultname = presetobj.getString("default");
				return getPresetFromName(defaultname, context);
			}		
		} catch (JSONException j ) {
			j.printStackTrace();
		}
		 return null;
	}

	
	public JSONObject getPresetFromName(String name, Context context) {
		try {
			JSONObject presetobj = getPresets(context);
			if (presetobj == null) return null;
			JSONArray presets = presetobj.getJSONArray("presets");
			for (int i=0; i<presets.length(); i++) {
				JSONObject preset = presets.getJSONObject(i);
				if (name.equals(preset.getString("name"))) {
					return preset;
				}
			}
		} catch (JSONException j ) {
			j.printStackTrace();
		}
		 return null;
	}
	
	public String[] getPresetNames(Context context) {
		try {
			JSONObject presetobj = getPresets(context);
			if (presetobj == null) return null;
			JSONArray presets = presetobj.getJSONArray("presets");
			String[] presetnames = new String[presets.length()];
			for (int i=0; i<presets.length(); i++) {
				JSONObject preset = presets.getJSONObject(i);
				presetnames[i] = preset.getString("name");
			}
		return presetnames;
		} catch (JSONException j) {
			j.printStackTrace();
		}
		return null;
	}
}
