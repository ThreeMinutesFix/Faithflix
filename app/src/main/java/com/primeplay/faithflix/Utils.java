package com.primeplay.faithflix;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.regex.Pattern;

public class Utils {

    static Activity activity = new Activity();

    static {
        System.loadLibrary("native-lib");
    }

    public native boolean isLoggerAppInstalled();

    public Utils(Activity activity) {
        this.activity = activity;
    }

    public static boolean isVPNActive(Context context) {
        // Check if VPN service is active
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = connectivityManager.getAllNetworks();

        for (Network network : networks) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSuspiciousAppInstalled() {
        PackageManager packageManager = activity.getPackageManager();
        List<PackageInfo> packages = packageManager.getInstalledPackages(0);

        for (PackageInfo packageInfo : packages) {
            String packageName = packageInfo.packageName;
            for (String pattern : BuildConfig.SUSPICIOUS_PATTERNS.split(",")) {
                if (Pattern.matches(pattern, packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String toBase64(String message) {
        byte[] data;
        try {
            data = message.getBytes("UTF-8");
            String base64Sms = Base64.encodeToString(data, Base64.NO_WRAP);
            return base64Sms;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param message the encoded message
     * @return the decoded message
     */
    public static String fromBase64(String message) {
        byte[] data = Base64.decode(message, Base64.NO_WRAP);
        try {
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

}
