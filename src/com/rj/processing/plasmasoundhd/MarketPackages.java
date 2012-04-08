package com.rj.processing.plasmasoundhd;

import com.rj.processing.plasmasound.R;

import android.content.Context;
import android.net.Uri;

public class MarketPackages {

	public static boolean isAndroidMarket(Context context) {
		return context.getResources().getBoolean(R.bool.android_market);
	}
	public static boolean isAmazonAppstore(Context context) {
		return context.getResources().getBoolean(R.bool.amazon_appstore);
	}
	
	public static Uri getMarketPackageUri(Context context) {
		if (isAndroidMarket(context))
			return Uri.parse("market://details?id="+context.getApplicationInfo().packageName);
		else
			return Uri.parse("market://details?id="+context.getApplicationInfo().packageName);
	}
}
