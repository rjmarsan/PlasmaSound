package com.rj.processing.plasmasoundhd;




import org.json.JSONObject;

import amir.android.icebreaking.SeekBarPreferenceView;
import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rj.processing.plasmasoundhd.pd.instruments.JSONPresets;
import com.rj.processing.plasmasoundhd.pd.instruments.JSONPresets.PresetListener;

public class PlasmaThereminEffectsSettings extends Fragment implements OnSharedPreferenceChangeListener, PresetListener {
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        

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
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
        getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
    
    
    
	private SharedPreferences getSharedPreferences() {
		return getActivity().getSharedPreferences(PlasmaSound.SHARED_PREFERENCES_AUDIO, 0);
	}

    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	return inflater.inflate(R.layout.effectsettings, container);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	((PDActivity)getActivity()).readSettings();
    }

	@Override
	public void presetChanged(JSONObject preset) {
		Log.d("SETTINGS", "Settings changed!!!!!!!!!!!");
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
			} else if (v instanceof ViewGroup)  {
				notifyChange((ViewGroup)v);
			}
		}
	}


}

