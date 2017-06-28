//
//  NetworkTools.java
//
//  Copyright (c) 2014 Nexage. All rights reserved.
//

package org.nexage.sourcekit.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkTools {

	private static final String TAG = HttpTools.class.getName();

	// This method return true if it's connected to Internet
	public static boolean connectedToInternet(Context context) {
		VASTLog.d(TAG, "Testing connectivity:");
		
		ConnectivityManager cm = (ConnectivityManager) context
			.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo wifiNetwork = cm
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetwork != null && wifiNetwork.isConnected()) {
			VASTLog.d(TAG, "Connected to Internet");
			return true;
		}

		NetworkInfo mobileNetwork = cm
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mobileNetwork != null && mobileNetwork.isConnected()) {
			VASTLog.d(TAG, "Connected to Internet");
			return true;
		}

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {
			VASTLog.d(TAG, "Connected to Internet");
			return true;
		}
		VASTLog.d(TAG, "No Internet connection");
		return false;
	}
}
