package com.primeplay.faithflix;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;



import java.util.List;
import java.util.regex.Pattern;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;

public class AppCheck {

    private Context context;

    public AppCheck(Context context) {
        this.context = context;
    }

    public void checkAppStatus() {
        String targetPackageName = "com.reqable.android";

        if (isAppInstalled(targetPackageName)) {
            if (isAppRunning(targetPackageName)) {
                Toast.makeText(context, "You are not allowed to use this app.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "App is installed but not running.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, "App is not installed.", Toast.LENGTH_LONG).show();
        }
    }

    public  boolean isAppInstalled(String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private boolean isAppRunning(String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
                if (appProcess.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }


}

