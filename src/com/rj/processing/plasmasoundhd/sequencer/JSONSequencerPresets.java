package com.rj.processing.plasmasoundhd.sequencer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.EditText;

import com.rj.processing.plasmasoundhd.R;
import com.rj.processing.plasmasoundhd.PDActivity;
import com.rj.processing.plasmasoundhd.SequencerActivity;

public class JSONSequencerPresets {
	public static String PRESETS = "PRESETS";
	public static String HAS_MY_PRESETS = "HAS_MY_PRESETS_SEQUENCER";
	public static String JSON_FILENAME = "sequences.json";
	public static interface PresetListener {
		public void presetChanged(JSONObject preset);
	}
	
	
	
	private static JSONSequencerPresets singleton;
	public static JSONSequencerPresets getPresets() {
		if (singleton == null) {
			singleton = new JSONSequencerPresets();
		}
		return singleton;
	}

	private ArrayList<PresetListener> listeners;
	
	
	private JSONObject currentsetting;
	
	
	
	public JSONSequencerPresets() {
		listeners = new ArrayList<PresetListener>();
	}
	public void addListener(PresetListener listen) {
		this.listeners.add(listen);
	}
	public void removeListener(PresetListener listen) {
		this.listeners.remove(listen);
	}
	public void notifyListeners(JSONObject preset) {
		//Log.d("Sequences", "Notifying all "+listeners.size()+" listeners");
		for (PresetListener  l : listeners) l.presetChanged(preset);
	}
	
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
	
	
	
	

	
	
	

	public void showLoadMenu(final Context c, final Sequencer sequencer) {
		try {
			final String[] items = getPresetNames(c);
			if (items == null || items.length <= 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(c);
				builder.setTitle(R.string.preset_seq_load_title_none);
				builder.setPositiveButton("OK", null);
				AlertDialog alert;
				alert = builder.create();
				alert.show();
				return;
			}
			int selection = -1;
			int i=0;
			for (String item : items) {
				if (currentsetting != null && item.equals(currentsetting.get("name"))) {
					selection = i;
				}
				i++;
			}
			
			AlertDialog.Builder builder = new AlertDialog.Builder(c);
			builder.setTitle(R.string.preset_seq_load_title);
			AlertDialog alert;
			builder.setSingleChoiceItems(items, selection, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
			        loadPreset(c,sequencer, items[item]);
			        dialog.dismiss();
			    }
			});
			alert = builder.create();
			alert.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showSaveMenu(final Context c, final Sequencer sequencer) {
		try {
			final String[] items = getPresetNames(c);
			if (items == null || items.length <= 0) {
				showSaveAsMenu(c, sequencer);
				return;
			}
			
			AlertDialog.Builder builder = new AlertDialog.Builder(c);
			builder.setTitle(R.string.preset_seq_save_title);
			builder.setItems(items, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
			        savePreset(c,sequencer, items[item]);
			    }
			});
			builder.setPositiveButton(R.string.preset_seq_save_new, new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int whichButton) {  
					showSaveAsMenu(c, sequencer);
				}
				}); 
			builder.setNegativeButton(R.string.preset_seq_save_delete, new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int whichButton) {  
					showDeleteMenu(c, sequencer);
				}
				}); 

			AlertDialog alert = builder.create();
			alert.show();
		} catch (Exception e) {
			
		}
	}
	
	public void showDeleteMenu(final Context c, final Sequencer p) {
		try {
			final String[] items = getPresetNames(c);
			
			AlertDialog.Builder builder = new AlertDialog.Builder(c);
			builder.setTitle(R.string.preset_seq_delete_title);
			builder.setItems(items, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
			        deletePreset(c,p, items[item]);
			    }
			});
			AlertDialog alert = builder.create();
			alert.show();
		} catch (Exception e) {
			
		}
	}

	
	public void showSaveAsMenu(final Context c, final Sequencer sequencer ) {
		AlertDialog.Builder builder = new AlertDialog.Builder(c);
		builder.setTitle(R.string.preset_seq_saveas_title);
		builder.setMessage(R.string.preset_seq_saveas_message);
		final EditText text = new EditText(c);
		builder.setView(text);
		builder.setPositiveButton(R.string.preset_seq_saveas_ok, new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int whichButton) {  
			  String value = text.getText().toString();  
			  savePreset(c, sequencer, value);
			}  
			}); 
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	
	public JSONObject loadDefault(Context c, Sequencer s) {
		JSONObject jpreset = getDefaultPreset(c);
		if (jpreset == null) return null;
		currentsetting = jpreset;
		s.loadSequence(c,jpreset);
		this.notifyListeners(jpreset);
		return jpreset;
	}
	
	public void loadPreset(Context c, Sequencer e, String preset) {
		JSONObject jpreset = getPresetFromName(preset, c);
		currentsetting = jpreset;
		e.loadSequence(c,jpreset);
		this.notifyListeners(jpreset);
		updateDefault(c);
	}
	
	public void savePreset(Context c, Sequencer e) {
		try {
			savePreset(c, e, currentsetting.getString("name"));
		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}
	
	public void savePreset(Context c, Sequencer e, String preset) {
		try {
			JSONObject presetsobj = getPresets(c);
			if (presetsobj == null) {
				presetsobj = new JSONObject();
				JSONObject presets = new JSONObject();
				presetsobj.put("presets", presets);
			}
			presetsobj.put("default", preset);
			JSONObject presets = presetsobj.getJSONObject("presets");
			JSONObject presetobj = new JSONObject();
			presetobj.put("name", preset);
			presetobj = e.sequenceToJSON(presetobj);
			presets.put(preset, presetobj);
			currentsetting = presetobj;
			writePresets(presetsobj, c);
			updateDefault(c);
		} catch (Exception j ) {
			j.printStackTrace();
		}
	}
	

	public void deletePreset(Context c, Sequencer e, String preset) {
		try {
			JSONObject presetsobj = getPresets(c);
			JSONObject presets = presetsobj.getJSONObject("presets");
			presets.remove(preset);
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
		populateWithDefaults(context);
		return getPresetsInternal(context);
	}
	public JSONObject getDefaultPresets(Context context) {
		try {
			InputStream is = context.getResources().openRawResource(com.rj.processing.plasmasoundhd.R.raw.sequences);
			Writer writer = new StringWriter();
			char[] buffer = new char[1024];
			try {
			    Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			    int n;
			    while ((n = reader.read(buffer)) != -1) {
			        writer.write(buffer, 0, n);
			    }
			} finally {
			    is.close();
			}
	
			String jsonString = writer.toString();
		return new JSONObject(jsonString);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private JSONObject getPresetsInternal(Context context) {
		try {
			File jsonFile = new File(context.getFilesDir(), JSON_FILENAME);
			if (!jsonFile.exists()) return null;
		    byte[] buffer = new byte[(int) jsonFile.length()];
		    BufferedInputStream f = new BufferedInputStream(new FileInputStream(jsonFile));
		    f.read(buffer);		
			String jsonString = new String(buffer);
			//Log.d("Sequences", "Presets:\n" +jsonString);
			JSONObject object = new JSONObject(jsonString);
			return object;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	private void populateWithDefaults(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PDActivity.SHARED_PREFERENCES_APPSTUFF, 0);
		if (prefs.getBoolean(HAS_MY_PRESETS, false)) return;
		
		JSONObject presets = getDefaultPresets(context);
		JSONObject existing = getPresetsInternal(context);
		if (existing != null) {
			try {
				presets.put("default",existing.getString("default"));
				JSONObject presetsobj = presets.getJSONObject("presets");
				JSONObject existobj = existing.getJSONObject("presets");
				Iterator iter = existobj.keys();
				while (iter.hasNext()) {
					String key = (String)iter.next();
					presetsobj.put(key, existobj.getJSONObject(key));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		writePresets(presets,context);
		
		Editor edit = prefs.edit();
		edit.putBoolean(HAS_MY_PRESETS, true);
		edit.commit();
	}
	
	public void writePresets(JSONObject json, Context context) {
		try {
			File jsonFile = new File(context.getFilesDir(), JSON_FILENAME);
			
			String out = json.toString(4);
			//Log.d("Sequences", "Presets:"+out);
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
			JSONObject presets = presetobj.getJSONObject("presets");
			return presets.getJSONObject(name);
		} catch (JSONException j ) {
			j.printStackTrace();
		}
		 return null;
	}
	
	public String[] getPresetNames(Context context) {
		try {
			JSONObject presetobj = getPresets(context);
			if (presetobj == null) return null;
			JSONObject presets = presetobj.getJSONObject("presets");
			String[] presetnames = new String[presets.length()];
			int i = 0;
			Iterator iter = presets.keys();
			while (iter.hasNext()) {
				String key = (String)iter.next();
				JSONObject preset = presets.getJSONObject(key);
				presetnames[i] = preset.getString("name");
				i++;
			}
		return presetnames;
		} catch (JSONException j) {
			j.printStackTrace();
		}
		return null;
	}
}
