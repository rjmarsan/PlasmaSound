/* The following code was written by Matthew Wiggins 
 * and is released under the APACHE 2.0 license 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package amir.android.icebreaking;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.rj.processing.plasmasound.R;

public class SeekBarPreference extends Preference implements
		OnSeekBarChangeListener, OnCheckedChangeListener, OnClickListener {
	private static final String androidns = "http://schemas.android.com/apk/res/android";
	private static final String psndns = "http://schemas.rjmarsan.com/apk/res/plasmasound";

		
	public int maximum = 100;
	public int minimum = 0;
	public int interval = 1;
	public String description = "";
	public String subtext = "";
	boolean yEnabled;
	private SeekBar bar;
	private ToggleButton yaxis;

	private float oldValue = 50;
	private int mDefault = 0;
	private TextView monitorBox;

	public SeekBarPreference(final Context context) {
		super(context);
	}

	public SeekBarPreference(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		maximum = attrs.getAttributeIntValue(androidns, "max", 100);
		minimum = attrs.getAttributeIntValue(androidns, "min", 0);
		interval = attrs.getAttributeIntValue(androidns, "interval", 1);
		description = attrs.getAttributeValue(androidns, "summary");
		subtext = attrs.getAttributeValue(androidns, "text");
	    mDefault = attrs.getAttributeIntValue(androidns,"defaultValue", 0);
	    yEnabled = attrs.getAttributeBooleanValue(psndns,"yenabled", false);
	}

	public SeekBarPreference(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected View onCreateView(final ViewGroup parent) {
		//Log.d("SeekBar", "MAKING NEW VIEW preference: "+getTitle()+" ");
	    if (shouldPersist())
	        oldValue = getPersistedInt(mDefault);

	    
		  LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		  ViewGroup viewgroup = (ViewGroup)inflater.inflate(R.layout.prefs_seekbar, null);
		  
		  TextView title = (TextView)viewgroup.findViewById(R.id.title);
		  TextView description = (TextView)viewgroup.findViewById(R.id.subtext);
		  TextView subMonitorBox = (TextView)viewgroup.findViewById(R.id.unitstext);
		  monitorBox = (TextView)viewgroup.findViewById(R.id.valuetext);
		  bar = (SeekBar)viewgroup.findViewById(R.id.seekbar);
		  boolean enabled = getSharedPreferences().getBoolean(getKey()+"_y", false);
		  //Log.d("Preference", getTitle()+ "   enabled at start: "+enabled + "   y enabled: "+yEnabled);
		  if (yaxis != null)
			  enabled = yaxis.isChecked();
		  yaxis = (ToggleButton)viewgroup.findViewById(R.id.toggleyaxis);
		  if (yEnabled)  {
			  yaxis.setChecked(enabled);
			  setBarState(!enabled);
			  yaxis.setOnCheckedChangeListener(this);
			  yaxis.setOnClickListener(this);
			  yaxis.setVisibility(View.VISIBLE);
		  } else {
			  yaxis.setVisibility(View.INVISIBLE);
		  }

		title.setText(getTitle());
		description.setText(this.description);
		subMonitorBox.setText(this.subtext);
		bar.setMax(maximum + minimum);
		bar.setProgress((int) this.oldValue);
		bar.setOnSeekBarChangeListener(this);
//		
//		
		this.monitorBox.setText(bar.getProgress() + "");
//
		
		return viewgroup;
		
	}

	@Override
	public void onProgressChanged(final SeekBar seekBar, final int progress,
			final boolean fromUser) {
		this.monitorBox.setText(progress + "");
	}

	@Override
	public void onStartTrackingTouch(final SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(final SeekBar seekBar) {
		int progress = seekBar.getProgress();
		progress = Math.round(((float) progress) / interval) * interval;

		if (!callChangeListener(progress)) {
			seekBar.setProgress((int) this.oldValue);
			return;
		}

		seekBar.setProgress(progress);
		this.oldValue = progress;
		this.monitorBox.setText(progress + "");
		updatePreference(progress);

		notifyChanged();

	}

	@Override
	protected Object onGetDefaultValue(final TypedArray ta, final int index) {

		final int dValue = ta.getInt(index, 50);

		return validateValue(dValue);
	}

	@Override
	protected void onSetInitialValue(final boolean restoreValue, final Object defaultValue) {

//		int temp = restoreValue ? getPersistedInt(50) : (Integer) defaultValue;
//
//		if (!restoreValue)
//			persistInt(temp);
//
//		this.oldValue = temp;
//		
	    super.onSetInitialValue(restoreValue, defaultValue);
	    if (restoreValue) 
	    	this.oldValue = shouldPersist() ? getPersistedInt(50) : this.oldValue;
	    else 
	    	this.oldValue = (Integer)defaultValue;

	}

	private int validateValue(int value) {

		if (value > maximum + minimum)
			value = maximum + minimum;
		else if (value < minimum)
			value = 0;
		value = value + minimum;
		return value;
	}

	private void updatePreference(final int newValue) {

		final SharedPreferences.Editor editor = getEditor();
		editor.putInt(getKey(), newValue);
		editor.commit();
		
	}
	
	private void updatePreferenceY(boolean enabled) {

		final SharedPreferences.Editor editor = getEditor();
		editor.putBoolean(getKey()+"_y", enabled);
		//editor.commit();
		editor.commit();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView,final boolean isChecked) {
		//Log.d("SeekBar", "CHECKEDDDDDDDD: "+getTitle());
		if (buttonView == yaxis) {
			updatePreferenceY(isChecked);
			setBarState(!isChecked);
		}
	}
	
	public void setBarState(boolean isEnabled) {
//		bar.setFocusable(isEnabled);
//		//bar.requestFocus();
//		bar.setClickable(isEnabled);
		//Log.d("SeekBar", "preference: "+getTitle()+" Bar set enabled: "+isEnabled);
		bar.setEnabled(isEnabled);
//		bar.invalidate();
//		
//		bar.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onClick(View arg0) {
//		Log.d("SeekBar", "preference: "+getTitle()+" click "+arg0);
//		if (arg0 == yaxis) {
//			Log.d("SeekBar", "preference: "+getTitle()+" click!");
//			updatePreferenceY(yaxis.isChecked());
//			setBarState(!yaxis.isChecked());
//		}		
	}


}
