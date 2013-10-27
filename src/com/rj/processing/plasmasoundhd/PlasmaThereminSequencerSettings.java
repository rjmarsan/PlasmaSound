package com.rj.processing.plasmasoundhd;




import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rj.processing.plasmasound.R;
import com.rj.processing.plasmasoundhd.pd.instruments.JSONPresets.PresetListener;

public class PlasmaThereminSequencerSettings extends SettingsFragment implements OnSharedPreferenceChangeListener, PresetListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	return inflater.inflate(R.layout.sequencersettings, container);
    }

}


