package com.rj.processing.plasmasoundhd;




import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class PlasmaThereminAudioSettings extends PreferenceFragment implements OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        getPreferenceManager().setSharedPreferencesName(
                PlasmaSound.SHARED_PREFERENCES_AUDIO);
        addPreferencesFromResource(R.xml.instrumentsettings);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	((PlasmaSound)getActivity()).readSettings();
    }

}

