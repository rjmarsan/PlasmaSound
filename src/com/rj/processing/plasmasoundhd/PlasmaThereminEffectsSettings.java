package com.rj.processing.plasmasoundhd;




import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PlasmaThereminEffectsSettings extends Fragment implements OnSharedPreferenceChangeListener {
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);

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
    	((PlasmaSound)getActivity()).readSettings();
    }


}

