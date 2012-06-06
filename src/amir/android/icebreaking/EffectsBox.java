package amir.android.icebreaking;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.rj.processing.plasmasoundhd.R;
import com.rj.processing.plasmasoundhd.PDActivity;

public class EffectsBox extends LinearLayout implements OnCheckedChangeListener {
	private static final String androidns = "http://schemas.android.com/apk/res/android";
	private static final String psndns = "http://schemas.rjmarsan.com/apk/res/plasmasound";

	String key;
	boolean enabled;
	ToggleButton enableButton;


	String getOnOffKey() {
		return key+"_onoff";
	}
	
	public EffectsBox(final Context context) {
		this(context, null);
	}
	public EffectsBox(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	    if (attrs != null) enabled = attrs.getAttributeBooleanValue(psndns,"defaultValue", true);
	    else enabled = true;
	    key = attrs.getAttributeValue(psndns, "key" );
	}
	public EffectsBox(final Context context, final AttributeSet attrs, final int defStyle) {
		this(context, attrs);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		onCreateView();
	}
	void onCreateView() {
	    enabled = getPersistedBool(enabled);
	    enableButton = (ToggleButton)this.findViewById(R.id.onoff);
	    if (enableButton != null) {
		    enableButton.setOnCheckedChangeListener(this);
		    enableButton.setChecked(enabled);
		    toggleAllChildEffects(enabled);
	    }
	}

	
	
	public void toggleAllChildEffects(boolean enabled) {
		//Log.d("EffectsBox", "Toggling stuff!");
		for (int i=0; i<this.getChildCount(); i++) {
			View v = this.getChildAt(i);
			//if (v instanceof SeekBarPreferenceView) {
				v.setEnabled(enabled);
			//}
		}
	}
	
	
	private boolean getPersistedBool(boolean mdefault) {
		try  {
			boolean val = (getSharedPreferences().getInt(getOnOffKey(), mdefault? 1:0 )) <= 0 ? false : true;
			//Log.d("EffectsBox", "getSharedBool! "+val);
			return val;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				boolean val = Boolean.parseBoolean(getSharedPreferences().getString(getOnOffKey(), "true"));
				//Log.d("EffectsBox", "getParsedBool! "+val);
				return val;
			} catch (Exception ee) { ee.printStackTrace(); }
		}
		return mdefault;
	}
	private SharedPreferences getSharedPreferences() {
		return getContext().getSharedPreferences(PDActivity.SHARED_PREFERENCES_AUDIO, 0);
	}
	private Editor getEditor() {
		return getSharedPreferences().edit();
	}

	
	
	private void updateOnOffPreference(final boolean newValue) {
		//Log.d("EffectsBox", "Updating preference! "+newValue);
		final SharedPreferences.Editor editor = getEditor();
		editor.putInt(getOnOffKey(), newValue ? 1 : 0);
		editor.commit();	
	}
	
	public void notifyChange() {
		onCreateView();
	}
	

	
	@Override
	public void onCheckedChanged(CompoundButton buttonView,final boolean isChecked) {
		enabled = isChecked;
		updateOnOffPreference(enabled);
	    toggleAllChildEffects(enabled);
	}
	
	
	
}
