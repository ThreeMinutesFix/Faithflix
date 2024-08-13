package com.primeplay.faithflix.Ui;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.primeplay.faithflix.ModStopper;
import com.primeplay.faithflix.R;
import com.primeplay.faithflix.Utils;
import com.primeplay.faithflix.util.InternetCheckReceiver;

public class BaseActivity extends AppCompatActivity {
    protected Utils vpnUtils;
    InternetCheckReceiver internetCheckReceiver;
    private boolean isOffline = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transparentStatusAndNavigation();
        vpnUtils = new Utils(this);
        if (Utils.isVPNActive(this) || Utils.isSuspiciousAppInstalled()) {
            Intent intent = new Intent(this, ModStopper.class);
            startActivity(intent);
            finish();
        }

        internetCheckReceiver = new InternetCheckReceiver() {
            @Override
            protected void onNetworkConnected() {
                // Only show the "You are online now" Snackbar if the user was previously offline
                if (isOffline) {
                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.you_are_online_now), Snackbar.LENGTH_SHORT).show();
                    isOffline = false; // Reset the flag since the user is now online
                }
            }

            @Override
            protected void onNetworkDisConnected() {
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.you_are_offline_now), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.setting), v -> {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }).show();
                isOffline = true; // Set the flag since the user is now offline
            }
        };

        // Register the receiver
        registerReceiver(internetCheckReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


    }

    private void transparentStatusAndNavigation() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    private void setWindowFlag(final int bits, boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(internetCheckReceiver);
    }
}

