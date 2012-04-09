package com.rj.processing.plasmasoundhd;

import java.io.File;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rj.processing.plasmasound.R;

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
				intent.setData(MarketPackages.getMarketPackageUri(context));
				context.startActivity(intent);
				dialog.dismiss();
			}});
		if (MarketPackages.supportsDonation(context)) {
			builder.setNegativeButton(R.string.rating_dialog_donate, new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(MarketPackages.getMarketDonateUri(context));
					context.startActivity(intent);
					dialog.dismiss();
				}});
		} else {
//			builder.setNegativeButton(R.string.rating_dialog_neveragain, new OnClickListener() {
//				public void onClick(DialogInterface dialog, int which) {
//					dialog.dismiss();
//				}});
		}
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
				intent.setData(MarketPackages.getMarketPackageUri(context));
				context.startActivity(intent);
				dialog.dismiss();
			}});
		if (MarketPackages.supportsDonation(context)) {
			builder.setNeutralButton(R.string.rating_dialog_donate, new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(MarketPackages.getMarketDonateUri(context));
					context.startActivity(intent);
					dialog.dismiss();
				}});
		}
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
		final LayoutInflater inflater = context.getLayoutInflater();
		final LinearLayout demoborder = (LinearLayout)inflater.inflate(R.layout.demoframe, new LinearLayout(context), false);
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

		//alert.setContentView(demoborder);
		alert.setContentView(demoborder);//, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
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
					WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
					lp.copyFrom(alert.getWindow().getAttributes());
					lp.width = alert.getWindow().getDecorView().getWidth();
					lp.height = LayoutParams.WRAP_CONTENT; //alert.getWindow().getDecorView().getHeight();
					alert.getWindow().setAttributes(lp);
					
					Log.d("MISCDialogs", "lp.height: "+alert.getWindow().getDecorView().getWidth()+"   context.height/2:"+(context.height));
					if (alert.getWindow().getDecorView().getWidth() > context.height) {
						//if the alert is at least twice as high as the screen, clamp the alert when we scroll
						LayoutParams p = blanklayout.getLayoutParams();
						p.width = blanklayout.getWidth();
						p.height = blanklayout.getHeight();
						blanklayout.setLayoutParams(p);
					}

					blanklayout.setVisibility(View.INVISIBLE);
					blanklayout.removeAllViews();
					inflater.inflate(slides[c.count], blanklayout);
					blanklayout.setVisibility(View.VISIBLE);
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
		
		alert.show();



	}


	
	
	public static void checkForSoundcloudAndDoThatOrNot(PDActivity context, String filename, String name) {
		if (filename != null) {
			try {
				//doing the soundcloud sharing
				File myAudiofile = new File(filename);
				String clientId = context.getResources().getString(com.rj.processing.plasmasound.R.string.soundcloud_api_clientid);
				Intent intent = new Intent("com.soundcloud.android.SHARE")
				  .putExtra(Intent.EXTRA_STREAM, Uri.fromFile(myAudiofile))
				  .putExtra("com.soundcloud.android.extra.title", name)
				  .putExtra("com.soundcloud.android.extra.tags", new String[] {
						  "Plasma Sound",
		                  "soundcloud:created-with-client-id="+clientId
		                  });
				context.startActivityForResult(intent, 0);
			} catch (ActivityNotFoundException e) {
			    if (context.shouldShowSoundcloud()) {
			    	doesTheUserWantToInstallSouncloud(context, filename, name);
					context.neverShowSoundcloudAgain();
			    } else {
			    	showRecordingShareChooser(context, filename, name);
			    }
			}
		} else {
			Toast.makeText(context, Utils.frmRes(context, com.rj.processing.plasmasound.R.string.export_toast_record_started), Toast.LENGTH_LONG).show();
		}
	}
	
	public static void showRecordingShareChooser(PDActivity context, String filename, String name) {
	    // SoundCloud Android app not installed
		//doing the default sharing
	    Intent share = new Intent(Intent.ACTION_SEND);
	    share.setType("audio/wav");

	    Uri uri = Uri.fromFile(new File(filename));
	    share.putExtra(Intent.EXTRA_STREAM, uri);
	    share.putExtra(Intent.EXTRA_TEXT, Utils.frmRes(context, com.rj.processing.plasmasound.R.string.export_extra_text));

	    context.startActivity(Intent.createChooser(share, Utils.frmRes(context, com.rj.processing.plasmasound.R.string.export_extra_title)));
		Toast.makeText(context, Utils.frmRes(context, com.rj.processing.plasmasound.R.string.export_toast_record_finished)+filename, Toast.LENGTH_LONG).show();
	}
	
	public static void doesTheUserWantToInstallSouncloud(final PDActivity context,final String filename,final String name) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.export_check_dialog_title);
		try {
			PackageInfo pack = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			//builder.setMessage(context.getText(R.string.about_dialog_message));
			TextView textcontent = new TextView(context);
			textcontent.setMovementMethod(LinkMovementMethod.getInstance());
			textcontent.setText(Html.fromHtml(context.getResources().getString(R.string.export_check_dialog_text)));
			textcontent.setLinkTextColor(Color.GREEN);
			textcontent.setPadding(10,5,10,5);
			textcontent.setTextSize(15);
			builder.setView(textcontent);
		} catch (Exception e) {
			e.printStackTrace();
			builder.setMessage(context.getText(R.string.export_check_dialog_text));
		}

		//builder.setMessage(R.string.rating_dialog_message);
		
		builder.setPositiveButton(R.string.export_check_dialog_soundcloud, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				context.startActivity(new Intent(Intent.ACTION_VIEW, MarketPackages.getSoundcloudUri(context)));
				dialog.dismiss();
			}});
		builder.setNeutralButton(R.string.export_check_dialog_neveragain, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
		    	showRecordingShareChooser(context, filename, name);
			}});
		AlertDialog alert = builder.create();
		
		alert.show();
	}
	
	/* from their github on how to do this */
	private static boolean isCompatibleSoundCloudInstalled(Context context) {
	    try {
	        PackageInfo info = context.getPackageManager()
	                                  .getPackageInfo("com.soundcloud.android",
	                PackageManager.GET_META_DATA);
	        // intent sharing only got introduced with version 22
	        return info != null && info.versionCode >= 22;
	    } catch (PackageManager.NameNotFoundException e) {
	        // not installed at all
	        return false;
	    }
	}
	
	
}
