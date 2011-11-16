package com.rj.processing.plasmasoundhd;




import org.json.JSONObject;

import amir.android.icebreaking.RadioGroupPrefs;
import amir.android.icebreaking.SeekBarPreferenceView;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rj.processing.plasmasounddonate.R;
import com.rj.processing.plasmasoundhd.pd.instruments.JSONPresets;
import com.rj.processing.plasmasoundhd.pd.instruments.JSONPresets.PresetListener;

public class PlasmaThereminEffectsSettings extends SettingsFragment implements OnSharedPreferenceChangeListener, PresetListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	return inflater.inflate(R.layout.effectsettings, container);
    }

}

