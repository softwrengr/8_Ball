package com.junglesoft.magicball.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by eapple on 24/09/2018.
 */

public class NetworkUtils {
    public static final String _2G = "2G";
    public static final String _3G = "3G";
    public static final String _4G = "4G";
    public static final String WIFI = "Wifi";
    public static final String BLUETOOTH = "Bluetooth";
    public static final String WIMAX = "Wimax";
    public static final String[] MOBILE_NETWORK_TYPES = {_2G, _3G, _4G};
    public static String UNKNOWN = "Unknown";

    public static boolean isNetworkConnected(final Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isNetworkConnectingOrConnected(final Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null
                && activeNetworkInfo.isConnectedOrConnecting();
    }
}
