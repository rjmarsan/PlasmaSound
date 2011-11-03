package com.rj.processing.plasmasoundhd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;


public class Launcher extends Activity {
	public static int GINGERBREAD_PHONE = 1;
	public static int HONEYCOMB_TABLET = 3;
	public static int ICS_PHONE = 4;
	
	public static int TOTAL_CRAP = 0;
	public static int PRETTY_CRAP = 1;
	public static int DECENT = 2;
	public static int POWERFUL = 3;
	public static int F_ING_POWERFUL = 4;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
		Log.d("Launcher", "Launch type: "+getUIType());
		if (getUIType() == HONEYCOMB_TABLET) {
			Intent intent = new Intent(this, com.rj.processing.plasmasoundhd.PlasmaSound.class);
		    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );        
		    this.startActivityForResult(intent, 1);
		} else if (getUIType() == GINGERBREAD_PHONE){
			Intent intent = new Intent(this, com.rj.processing.plasmasoundhd.PlasmaSound.class);
		    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );        
		    this.startActivityForResult(intent, 1);
		}
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == 1) {
			finish(); //lolhax
		}
	}
	

	@Override
	protected void onResume() {
		super.onResume();
		finish(); //lolhax
	}
	
	public static int getUIType() {
		if (Build.VERSION.SDK_INT >= 11) {
			return HONEYCOMB_TABLET;
		} else {
			return GINGERBREAD_PHONE;
		}
	}
	
	public static int getPhoneCPUPower(Context context) {
		 DisplayMetrics metrics = new DisplayMetrics();
		 Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		 display.getMetrics(metrics);
		 int size = (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);
		 if (size == Configuration.SCREENLAYOUT_SIZE_NORMAL || size == Configuration.SCREENLAYOUT_SIZE_SMALL) {
			 switch (metrics.densityDpi) {
				 case DisplayMetrics.DENSITY_LOW:
					 return TOTAL_CRAP;
				 case DisplayMetrics.DENSITY_MEDIUM:
					 return PRETTY_CRAP;
				 case DisplayMetrics.DENSITY_HIGH:
					 return DECENT;
				 case 320 /**DisplayMetrics.DENSITY_XHIGH**/:
					 return POWERFUL;
			 }
		 }
		 else if (size == Configuration.SCREENLAYOUT_SIZE_LARGE) {
			 switch (metrics.densityDpi) {
				 case DisplayMetrics.DENSITY_LOW:
					 return TOTAL_CRAP;
				 case DisplayMetrics.DENSITY_MEDIUM:
					 return DECENT;
				 case DisplayMetrics.DENSITY_HIGH:
					 return POWERFUL;
				 case 320 /**DisplayMetrics.DENSITY_XHIGH**/:
					 return F_ING_POWERFUL;
			 }
		 }
		 else if (size == 4/*Configuration.SCREENLAYOUT_SIZE_XLARGE*/) {
			 switch (metrics.densityDpi) {
				 case DisplayMetrics.DENSITY_LOW:
					 return DECENT;
				 case DisplayMetrics.DENSITY_MEDIUM:
					 return POWERFUL;
				 case DisplayMetrics.DENSITY_HIGH:
					 return F_ING_POWERFUL;
				 case 320 /**DisplayMetrics.DENSITY_XHIGH**/:
					 return F_ING_POWERFUL;
			 }
		 }
		 return DECENT;
		 
	}


}