package com.rj.processing.plasmasoundhd;




import org.json.JSONObject;

import amir.android.icebreaking.EffectsBox;
import amir.android.icebreaking.RadioGroupPrefs;
import amir.android.icebreaking.SeekBarPreferenceView;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rj.processing.plasmasoundhd.pd.instruments.JSONPresets;
import com.rj.processing.plasmasoundhd.pd.instruments.JSONPresets.PresetListener;
import com.rj.processing.plasmasoundhd.sequencer.JSONSequencerPresets;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public abstract class SettingsFragment extends Fragment implements OnSharedPreferenceChangeListener, PresetListener, JSONSequencerPresets.PresetListener {
	PDActivity parent = null;
	
    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	JSONPresets.getPresets().removeListener(this);
    	JSONSequencerPresets.getPresets().removeListener(this);
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	JSONPresets.getPresets().addListener(this);
    	JSONSequencerPresets.getPresets().addListener(this);
    }
    
	private SharedPreferences getSharedPreferences() {
		return getActivity().getSharedPreferences(PDActivity.SHARED_PREFERENCES_AUDIO, 0);
	}
    
    @Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
        getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        if (activity instanceof PDActivity) {
        	parent = (PDActivity) activity;
        }
    }
    
    @Override
    public abstract View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState);


    @Override
    public void onResume() {
        super.onResume();
    }
    
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	if (parent != null) parent.readSettings();
    }

	@Override
	public void presetChanged(JSONObject preset) {
		Log.d("INSTSETTINGS", "Settings changed!!!!!!!!!!!");
		if (this.getView() != null) {
			ViewGroup v = (ViewGroup) this.getView();
			notifyChange(v);
		}
	}

    

	public void notifyChange(ViewGroup group) {
		for (int i=0; i<group.getChildCount(); i++) {
			View v = group.getChildAt(i);
			if (v instanceof SeekBarPreferenceView) {
				((SeekBarPreferenceView)v).notifyChange();
			} else if (v instanceof RadioGroupPrefs) {
				((RadioGroupPrefs)v).notifyChange();
			} else if (v instanceof EffectsBox) {
				((EffectsBox)v).notifyChange();
				notifyChange((ViewGroup)v);
			} else if (v instanceof ViewGroup)  {
				notifyChange((ViewGroup)v);
			}
		}
	}

}



