package com.rj.processing.plasmasoundhd;

import com.rj.processing.plasmasoundhd.R;

import android.content.Context;
import android.net.Uri;

public class MarketPackages {

	public static boolean isAndroidMarket(Context context) {
		return context.getResources().getBoolean(R.bool.android_market);
	}
	public static boolean isAmazonAppstore(Context context) {
		return context.getResources().getBoolean(R.bool.amazon_appstore);
	}
	public static boolean supportsDonation(Context context) {
		return context.getResources().getBoolean(R.bool.supports_donate);
	}
	
	public static Uri getMarketPackageUri(Context context) {
		if (isAndroidMarket(context))
			return Uri.parse("market://details?id="+context.getApplicationInfo().packageName);
		else
			return Uri.parse("http://www.amazon.com/gp/mas/dl/android?p="+context.getApplicationInfo().packageName);
	}
	
	public static Uri getMarketDonateUri(Context context) {
		String donatepack = context.getResources().getString(R.string.app_package_donate);
		if (isAndroidMarket(context))
			return Uri.parse("market://details?id="+donatepack);
		else
			return Uri.parse("http://www.amazon.com/gp/mas/dl/android?p="+donatepack);
	}
	
	public static Uri getSoundcloudUri(Context context) {
		if (isAndroidMarket(context))
			return Uri.parse("market://details?id=com.soundcloud.android");
		else
			return Uri.parse("http://www.amazon.com/gp/mas/dl/android?p=com.soundcloud.android");
	}
}
