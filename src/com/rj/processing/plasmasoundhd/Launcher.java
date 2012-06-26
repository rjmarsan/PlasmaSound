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
	public static int PHONE = 1;
	public static int TABLET = 3;
	//public static int ICS_PHONE = 4;
	
	public static int TOTAL_CRAP = 0;
	public static int PRETTY_CRAP = 1;
	public static int DECENT = 2;
	public static int POWERFUL = 3;
	public static int F_ING_POWERFUL = 4;
	
	public static int size;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
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
	
	

}