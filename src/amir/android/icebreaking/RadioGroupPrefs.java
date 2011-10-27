/* The following code was written by Matthew Wiggins 
 * and is released under the APACHE 2.0 license 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package amir.android.icebreaking;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.rj.processing.plasmasoundhd.PDActivity;

public class RadioGroupPrefs extends RadioGroup implements
		OnCheckedChangeListener {
	private static final String androidns = "http://schemas.android.com/apk/res/android";
	private static final String psndns = "http://schemas.rjmarsan.com/apk/res/plasmasound";

		
	private String title = "";
	private String key = "";
	private String titleArrayRes;
	private String valueArrayRes;
	private String[] titles;
	private String[] values;
	private RadioButton[] buttons;

	private String mDefault = "";
	private int mDefaultEntry = -1;

	public RadioGroupPrefs(final Context context) {
		super(context);
	}
	
	

	public RadioGroupPrefs(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	    mDefault = attrs.getAttributeValue(psndns,"defaultValue");
	    mDefault = getPersisted(mDefault);
	    title = attrs.getAttributeValue(psndns, "title" );
	    key = attrs.getAttributeValue(psndns, "key" );
	    titleArrayRes = attrs.getAttributeValue(androidns, "entries" );
	    valueArrayRes = attrs.getAttributeValue(androidns, "entryValues" );
	    Log.d("RadioGroupPrefs", "title:" +titleArrayRes + " value:" +valueArrayRes);
	    Resources res = context.getResources();
	    titles = res.getStringArray(Integer.parseInt(titleArrayRes.replace("@", "")));
	    values = res.getStringArray(Integer.parseInt(valueArrayRes.replace("@", "")));
	    
	    for (int i=0; i<values.length; i++) 
	    	if (values[i].equals(mDefault)) mDefaultEntry = i;
	    
	    View v = onCreateView(this);
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
	private String getPersisted(String mdefault) {
		try  {
			return getSharedPreferences().getString(getKey(), mdefault+"");
		} catch (Exception e) {
			e.printStackTrace();
			try {
				return getSharedPreferences().getInt(getKey(), 0)+"";
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

	public RadioGroupPrefs(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs);
	}

	protected View onCreateView(final ViewGroup parent) {
		
		
		//Log.d("SeekBar", "MAKING NEW VIEW preference: "+getTitle()+" ");
		TextView title = new TextView(getContext());
		title.setText(this.title);
		title.setTextSize(18);
		title.setTextColor(Color.WHITE);
		LayoutParams param = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		param.rightMargin = 5;
		param.leftMargin = 20;
		title.setLayoutParams(param);
		this.addView(title);
		buttons = new RadioButton[titles.length];
		for (int i=0; i<buttons.length; i++) {
			RadioButton b = new RadioButton(getContext());
			b.setChecked(mDefaultEntry == i);
			this.addView(b);
			b.setText(titles[i]);
			b.setId(i);
			b.setTag(values[i]);
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.weight = 1f / buttons.length;
			b.setLayoutParams(params);
			this.setOnCheckedChangeListener(this);
			buttons[i] = b;
		}		
		return parent;
		
	}



	private void updatePreference(final String newValue) {

		final SharedPreferences.Editor editor = getEditor();
		editor.putString(getKey(), newValue);
		editor.commit();
		
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
	    super.onLayout(changed,l, t, r, b);
	}



	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		mDefault = (String)findViewById(checkedId).getTag();
		if (values.length > checkedId) {
			updatePreference(mDefault);
			Log.d("RadioGroupPrefs", "Updating checkbox");
		}
		for (int i=0; i<buttons.length; i++) {
			buttons[i].setChecked(i == checkedId);
		}
	}

	
	public void notifyChange() {
		String val = getPersisted(mDefault);
		for (int i=0; i<buttons.length; i++) {
			buttons[i].setChecked(buttons[i].getTag().equals(val));
		}
	}

}
