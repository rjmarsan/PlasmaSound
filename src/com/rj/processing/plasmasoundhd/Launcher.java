package com.rj.processing.plasmasoundhd;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;


public class Launcher extends Activity {
	public static int GINGERBREAD_PHONE = 1;
	public static int HONEYCOMB_TABLET = 3;
	public static int ICS_PHONE = 4;
	
	
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

}