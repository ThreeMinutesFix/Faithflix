package com.primeplay.faithflix;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.widget.Toast;

import java.util.List;
import java.util.regex.Pattern;

public class VPNUtils {

    static Activity activity = new Activity();

    static {
        System.loadLibrary("native-lib");
    }

    public native boolean isLoggerAppInstalled();
    public VPNUtils(Activity activity) {
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


}
