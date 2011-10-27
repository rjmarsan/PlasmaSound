/* The following code was written by Matthew Wiggins 
 * and is released under the APACHE 2.0 license 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package amir.android.icebreaking;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.rj.processing.plasmasound.R;
import com.rj.processing.plasmasoundhd.PDActivity;

public class SeekBarPreferenceView extends LinearLayout implements
		OnSeekBarChangeListener, OnCheckedChangeListener {
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
	private String title = "";
	private String key = "";

	private float oldValue = 50;
	private int mDefault = 0;
	private TextView monitorBox;

	public SeekBarPreferenceView(final Context context) {
		super(context);
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (bar!=null) bar.setEnabled(enabled );// && ! (yEnabled && yaxis != null) );
		if (yaxis!=null) yaxis.setEnabled(enabled);
	}
	

	public SeekBarPreferenceView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		maximum = attrs.getAttributeIntValue(psndns, "max", 100);
		minimum = attrs.getAttributeIntValue(psndns, "min", 0);
		interval = attrs.getAttributeIntValue(psndns, "interval", 1);
		description = attrs.getAttributeValue(psndns, "summary");
		subtext = attrs.getAttributeValue(androidns, "text");
	    mDefault = attrs.getAttributeIntValue(psndns,"defaultValue", 0);
	    yEnabled = attrs.getAttributeBooleanValue(psndns,"yenabled", false);
	    title = attrs.getAttributeValue(psndns, "title" );
	    key = attrs.getAttributeValue(psndns, "key" );
	    
	    View v = onCreateView(this);
	    //Log.d("SeekBar", "Added view : "+v);

	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	}
	
	private String getTitle() {
		return this.title;
	}
	private boolean shouldPersist() {
		return true;
	}
	private int getPersistedInt(int mdefault) {
		try  {
			return (int)getSharedPreferences().getInt(getKey(), mdefault);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				return (int)getSharedPreferences().getFloat(getKey(), mdefault);
			} catch (Exception ee) { ee.printStackTrace(); }
		}
		return mdefault;
	}
	private SharedPreferences getSharedPreferences() {
		return getContext().getSharedPreferences(PDActivity.SHARED_PREFERENCES_AUDIO, 0);
	}
	private String getKey() {
		return key;
	}
	private Editor getEditor() {
		return getSharedPreferences().edit();
	}

	public SeekBarPreferenceView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}

	protected View onCreateView(final ViewGroup parent) {
		//Log.d("SeekBar", "MAKING NEW VIEW preference: "+getTitle()+" ");

	    
		  LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		  ViewGroup viewgroup = (ViewGroup)inflater.inflate(R.layout.prefs_seekbar, parent);
		  
		  TextView title = (TextView)viewgroup.findViewById(R.id.title);
		  TextView description = (TextView)viewgroup.findViewById(R.id.subtext);
		  TextView subMonitorBox = (TextView)viewgroup.findViewById(R.id.unitstext);
		  monitorBox = (TextView)viewgroup.findViewById(R.id.valuetext);
		  bar = (SeekBar)viewgroup.findViewById(R.id.seekbar);
		  yaxis = (ToggleButton)viewgroup.findViewById(R.id.toggleyaxis);

		  notifyChange();
		  
		  
			title.setText(getTitle());
			description.setText(this.description);
			subMonitorBox.setText(this.subtext);

		
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

		seekBar.setProgress(progress);
		this.oldValue = progress;
		this.monitorBox.setText(progress + "");
		updatePreference(progress);

	}

	private void updatePreference(final int newValue) {

		final SharedPreferences.Editor editor = getEditor();
		editor.putInt(getKey(), newValue);
		editor.commit();
		
	}
	
	private void updatePreferenceY(boolean enabled) {

		final SharedPreferences.Editor editor = getEditor();
		editor.putBoolean(getKey()+"_y", enabled);
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
		//Log.d("SeekBar", "preference: "+getTitle()+" Bar set enabled: "+isEnabled);
		bar.setEnabled(isEnabled);
	}



	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
	    super.onLayout(changed,l, t, r, b);
	}

	
	
	public void notifyChange() {
	    if (shouldPersist())
	        oldValue = getPersistedInt(mDefault);

		  boolean enabled = getSharedPreferences().getBoolean(getKey()+"_y", false);
		  //Log.d("Preference", getTitle()+ "   enabled at start: "+enabled + "   y enabled: "+yEnabled);
		  if (yaxis != null)
			  enabled = yaxis.isChecked();
		  if (yEnabled)  {
			  yaxis.setChecked(enabled);
			  setBarState(!enabled);
			  yaxis.setOnCheckedChangeListener(this);
			  yaxis.setVisibility(View.VISIBLE);
		  } else {
			  yaxis.setVisibility(View.INVISIBLE);
		  }

		bar.setMax(maximum + minimum);
		bar.setProgress((int) this.oldValue);
		bar.setOnSeekBarChangeListener(this);
//		
//		
		this.monitorBox.setText(bar.getProgress() + "");
	}

}
