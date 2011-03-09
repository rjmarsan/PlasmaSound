/* The following code was written by Matthew Wiggins 
 * and is released under the APACHE 2.0 license 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package amir.android.icebreaking;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SeekBarPreference extends Preference implements
		OnSeekBarChangeListener {
	private static final String androidns = "http://schemas.android.com/apk/res/android";

	
	private static final int TITLE_ID = 1;
	private static final int MONITOR_ID = 2;
	private static final int SLIDER_ID = 3;
	private static final int SUBMONITOR_ID = 4;
	private static final int DESCRIPTION_ID = 5;
	
	
	
	public int maximum = 100;
	public int minimum = 0;
	public int interval = 1;
	public String description = "";
	public String subtext = "";

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
	}

	public SeekBarPreference(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected View onCreateView(final ViewGroup parent) {
	    if (shouldPersist())
	        oldValue = getPersistedInt(mDefault);

		
		final RelativeLayout layout = new RelativeLayout(getContext());
		layout.setPadding(15, 5, 15, 5);

		
		
		//setup title
		final RelativeLayout.LayoutParams titleparams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		titleparams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		titleparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

		final TextView title = new TextView(getContext());
		title.setText(getTitle());
		title.setTextSize(24);
		title.setTextColor(Color.WHITE);
		title.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
		title.setGravity(Gravity.LEFT);
		title.setLayoutParams(titleparams);
		title.setId(TITLE_ID);
		
		
		//setup description
		final RelativeLayout.LayoutParams descriptionparams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		descriptionparams.addRule(RelativeLayout.ALIGN_BASELINE, title.getId());
		descriptionparams.addRule(RelativeLayout.RIGHT_OF, title.getId());

		final TextView description = new TextView(getContext());
		description.setText(this.description);
		description.setTextSize(16);
		description.setPadding(10, 0, 0, 0);
		description.setTextColor(Color.GRAY);
		description.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
		description.setGravity(Gravity.LEFT);
		description.setLayoutParams(descriptionparams);
		description.setId(DESCRIPTION_ID);
		
		
		//setup submonitor text
		final RelativeLayout.LayoutParams submonitorParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		submonitorParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

		final TextView subMonitorBox = new TextView(getContext());
		subMonitorBox.setTextSize(16);
		subMonitorBox.setText(this.subtext);
		subMonitorBox.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
		subMonitorBox.setLayoutParams(submonitorParams);
		subMonitorBox.setPadding(2, 5, 0, 0);
		subMonitorBox.setId(SUBMONITOR_ID);
		
		
		//setup monitor text
		final RelativeLayout.LayoutParams monitorparams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		monitorparams.addRule(RelativeLayout.LEFT_OF, subMonitorBox.getId());
		monitorparams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

		this.monitorBox = new TextView(getContext());
		this.monitorBox.setTextSize(22);
		this.monitorBox.setTextColor(Color.WHITE);
		this.monitorBox.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
		this.monitorBox.setLayoutParams(monitorparams);
		this.monitorBox.setPadding(2, 5, 0, 0);
		this.monitorBox.setId(MONITOR_ID);
		
		//set the submonitor box on the baseline of the monitor box
		submonitorParams.addRule(RelativeLayout.ALIGN_BASELINE, this.monitorBox.getId());
		
		
		//setup slider
		final RelativeLayout.LayoutParams sliderparaams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		sliderparaams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		sliderparaams.addRule(RelativeLayout.BELOW, title.getId());

		final SeekBar bar = new SeekBar(getContext());
		bar.setMax(maximum + minimum);
		bar.setProgress((int) this.oldValue);
		bar.setLayoutParams(sliderparaams);
		bar.setOnSeekBarChangeListener(this);
		bar.setId(SLIDER_ID);
		
		
		this.monitorBox.setText(bar.getProgress() + "");

		layout.addView(title);
		layout.addView(bar);
		layout.addView(description);
		layout.addView(subMonitorBox);
		layout.addView(this.monitorBox);
		layout.setId(android.R.id.widget_frame);

		return layout;
		
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

}
