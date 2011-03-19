package com.rj.processing.plasmasound.pd.instruments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.widget.EditText;

import com.rj.processing.plasmasound.PlasmaSound;
import com.rj.processing.plasmasound.Utils;

public class Preset {
	public static String PRESETS = "PRESETS";

	public void showLoadMenu(final PlasmaSound p) {
		SharedPreferences s = p.getSharedPreferences(PlasmaSound.SHARED_PREFERENCES_AUDIO, 0);
		Object o = Utils.stringToObject(s.getString(PRESETS, ""));
		try {
			final String[] items = (String[])o;
			
			AlertDialog.Builder builder = new AlertDialog.Builder(p);
			builder.setTitle("Pick a saved instance");
			builder.setItems(items, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
			        loadPreset(p,p.inst, items[item]);
			    }
			});
			AlertDialog alert = builder.create();
			alert.show();
		} catch (Exception e) {
			
		}
	}
	
	public void showSaveMenu(final PlasmaSound p) {
		SharedPreferences s = p.getSharedPreferences(PlasmaSound.SHARED_PREFERENCES_AUDIO, 0);
		Object o = Utils.stringToObject(s.getString(PRESETS, ""));
		try {
			final String[] items = (String[])o;
			
			AlertDialog.Builder builder = new AlertDialog.Builder(p);
			builder.setTitle("Pick a saved instance");
			builder.setItems(items, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
			        savePreset(p,p.inst, items[item]);
			    }
			});
			builder.setNeutralButton("New", new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int whichButton) {  
					showSaveAsMenu(p);
				}
				}); 
			AlertDialog alert = builder.create();
			alert.show();
		} catch (Exception e) {
			
		}
	}
	
	public void showSaveAsMenu(final PlasmaSound p ) {
		AlertDialog.Builder builder = new AlertDialog.Builder(p);
		builder.setTitle("Name?");
		builder.setMessage("Pick a name for the preset");
		final EditText text = new EditText(p);
		builder.setView(text);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int whichButton) {  
			  String value = text.getText().toString();  
			  savePreset(p, p.inst, value);
			}  
			}); 
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void loadPreset(PlasmaSound p, Instrument e, String preset) {
		
	}
	
	public void savePreset(PlasmaSound p, Instrument e, String preset) {
		
	}
}
