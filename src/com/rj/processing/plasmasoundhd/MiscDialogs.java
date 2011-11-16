package com.rj.processing.plasmasoundhd;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rj.processing.plasmasounddonate.R;

public class MiscDialogs {

	public static void showAboutDialog(final Context context) { 
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.about_dialog_title);
		try {
			PackageInfo pack = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			//builder.setMessage(context.getText(R.string.about_dialog_message));
			TextView textcontent = new TextView(context);
			textcontent.setMovementMethod(LinkMovementMethod.getInstance());
			textcontent.setText(Html.fromHtml(String.format(context.getResources().getString(R.string.about_dialog_message),pack.versionName, ""+pack.versionCode)));
			textcontent.setLinkTextColor(Color.GREEN);
			textcontent.setPadding(5,5,5,5);
			textcontent.setTextSize(15);
			builder.setView(textcontent);
		} catch (Exception e) {
			e.printStackTrace();
			builder.setMessage(context.getText(R.string.about_dialog_message));
		}
		
		builder.setPositiveButton(R.string.rating_dialog_market, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id="+context.getApplicationInfo().packageName));
				context.startActivity(intent);
				dialog.dismiss();
			}});
		
		builder.setNegativeButton(R.string.rating_dialog_donate, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String donatepack = context.getResources().getString(R.string.app_package_donate);
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id="+donatepack));
				context.startActivity(intent);
				dialog.dismiss();
			}});

//		builder.setNegativeButton(R.string.rating_dialog_, new OnClickListener() {
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//			}});
		AlertDialog alert = builder.create();
		
		alert.show();
	}
	
	
	
	public static void showRatingDialog(final Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.rating_dialog_title);
		try {
			PackageInfo pack = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			//builder.setMessage(context.getText(R.string.about_dialog_message));
			TextView textcontent = new TextView(context);
			textcontent.setMovementMethod(LinkMovementMethod.getInstance());
			textcontent.setText(Html.fromHtml(context.getResources().getString(R.string.rating_dialog_message)));
			textcontent.setLinkTextColor(Color.GREEN);
			textcontent.setPadding(5,5,5,5);
			textcontent.setTextSize(15);
			builder.setView(textcontent);
		} catch (Exception e) {
			e.printStackTrace();
			builder.setMessage(context.getText(R.string.about_dialog_message));
		}

		//builder.setMessage(R.string.rating_dialog_message);
		
		builder.setPositiveButton(R.string.rating_dialog_market, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id="+context.getApplicationInfo().packageName));
				context.startActivity(intent);
				dialog.dismiss();
			}});
		builder.setNeutralButton(R.string.rating_dialog_donate, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String donatepack = context.getResources().getString(R.string.app_package_donate);
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id="+donatepack));
				context.startActivity(intent);
				dialog.dismiss();
			}});
		builder.setNegativeButton(R.string.rating_dialog_neveragain, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}});
		AlertDialog alert = builder.create();
		
		alert.show();
	}
	
	
	
	
//	public static void showTutorialDialog(final PDActivity context) { 
//		AlertDialog.Builder builder = new AlertDialog.Builder(context);
//		builder.setTitle(com.rj.processing.plasmasound.R.string.tutorial_dialog_title);
//		final LayoutInflater inflater = context.getLayoutInflater();
//		final LinearLayout blanklayout = new LinearLayout(context);
//		final int[] slides = {
//				R.layout.demoscreen1,
//				R.layout.demoscreen2
//		};
//		inflater.inflate(slides[0], blanklayout);
//		//blanklayout.addView(layout);
//		builder.setView(blanklayout);
//		
//		/**
//		 * Tutorial stuff:
//		 * Continuum:
//		 * X controls pitch
//		 * Y controls another parameter
//		 * Continuum params modify range, keyboard type, etc.
//		 * Effect params modify volume, waveform, delay, tremolo, etc.
//		 * Sequencer:
//		 * touch and drag to modify y-value of sequence note
//		 * save and load sequeces with menu
//		 */
//		builder.setPositiveButton("Ok", new OnClickListener() {
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//			}});
//		
//		builder.setNegativeButton("Next", new OnClickListener() {
//			public void onClick(DialogInterface dialog, int which) {
//				inflater.inflate(slides[1], blanklayout);
//				dialog.cancel();
//			}});
//		AlertDialog alert = builder.create();
//		alert.show();
//	}
	public static class Counter {
		public int count = 0;
	}
	public static void showTutorialDialog(final PDActivity context) { 
//		if (1+1 == 2) return;

		final Dialog alert = new Dialog(context);
		alert.show();
		final LayoutInflater inflater = context.getLayoutInflater();
		final LinearLayout demoborder = (LinearLayout)inflater.inflate(R.layout.demoframe, null);
		final ViewGroup blanklayout = (ViewGroup)demoborder.findViewById(R.id.dynocontent);
		final int[] slides = {
				R.layout.demoscreen0,
				R.layout.demoscreen1,
				R.layout.demoscreen2,
				R.layout.demoscreen3,
				R.layout.demoscreen4,
				R.layout.demoscreen5,
				R.layout.demoscreen6,
				R.layout.demoscreen7,
				R.layout.demoscreen8,
				R.layout.demoscreen9,
		};
		final int[] slidetitles = {
				R.string.tutorial_dialog_slide0,
				R.string.tutorial_dialog_slide1,
				R.string.tutorial_dialog_slide2,
				R.string.tutorial_dialog_slide3,
				R.string.tutorial_dialog_slide4,
				R.string.tutorial_dialog_slide5,
				R.string.tutorial_dialog_slide6,
				R.string.tutorial_dialog_slide7,
				R.string.tutorial_dialog_slide8,
				R.string.tutorial_dialog_slide9,
		};
		final Counter c = new Counter();
		c.count = 0;
		
		inflater.inflate(slides[c.count], blanklayout);

		alert.setContentView(demoborder);
		alert.setTitle(slidetitles[0]);
//		demoborder.postInvalidateDelayed(2000);
//		alert.getWindow().getDecorView().postInvalidateDelayed(2000);
//		demoborder.getHandler().postAtTime(new Runnable() { public void run() {
//			alert.getWindow().getDecorView().requestLayout();
//			demoborder.postInvalidate();
//		}}, System.currentTimeMillis()+1000);
		
		/**
		 * Tutorial stuff:
		 * Continuum:
		 * X controls pitch
		 * Y controls another parameter
		 * Continuum params modify range, keyboard type, etc.
		 * Effect params modify volume, waveform, delay, tremolo, etc.
		 * Sequencer:
		 * touch and drag to modify y-value of sequence note
		 * save and load sequeces with menu
		 */
		Button ok = (Button) demoborder.findViewById(R.id.ok);
		ok.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				alert.dismiss();
			}});
		
		final Button next = (Button) demoborder.findViewById(R.id.next);
		final Button back = (Button) demoborder.findViewById(R.id.back);
		back.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				c.count -= 1;
				if (c.count == slides.length ) {
					alert.dismiss();
				} else {
					blanklayout.removeAllViews();
					inflater.inflate(slides[c.count], blanklayout);
					alert.setTitle(slidetitles[c.count]);
					if (c.count == 0) {
						back.setEnabled(false);
					} else {
						back.setEnabled(true);
						next.setText(R.string.tutorial_dialog_next);
					}
				} 
			}
		});
		back.setEnabled(false);

				
		next.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				c.count += 1;
				if (c.count == slides.length ) {
					alert.dismiss();
				} else {
					blanklayout.removeAllViews();
					inflater.inflate(slides[c.count], blanklayout);
					alert.setTitle(slidetitles[c.count]);
					if (c.count == slides.length - 1) {
						next.setText(R.string.tutorial_dialog_donebox);
						back.setEnabled(true);
					} else if ( c.count == 0) {
						back.setEnabled(false);
						next.setText(R.string.tutorial_dialog_next);
					} else {
						next.setText(R.string.tutorial_dialog_next);
						back.setEnabled(true);
					}
				} 
			}});
	}


	
	
}
