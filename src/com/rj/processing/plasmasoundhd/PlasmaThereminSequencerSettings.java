package com.rj.processing.plasmasoundhd;




import org.json.JSONObject;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.widget.ListView;

import com.rj.processing.plasmasoundhd.pd.instruments.JSONPresets;
import com.rj.processing.plasmasoundhd.pd.instruments.JSONPresets.PresetListener;

public class PlasmaThereminSequencerSettings extends PreferenceFragment implements OnSharedPreferenceChangeListener, PresetListener {

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        getPreferenceManager().setSharedPreferencesName(
                PlasmaSound.SHARED_PREFERENCES_AUDIO);
        addPreferencesFromResource(R.xml.sequencersettings);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	((PDActivity)getActivity()).readSettings();
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
        getPreferenceManager().setSharedPreferencesName(
                PlasmaSound.SHARED_PREFERENCES_AUDIO);
        this.setPreferenceScreen(getPreferenceScreen());
        getPreferenceScreen().bind((ListView)getView().findViewById(android.R.id.list));
	}

}



