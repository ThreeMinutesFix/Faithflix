package com.primeplay.faithflix;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.primeplay.faithflix.util.InternetCheckReceiver;

public class BaseActivity extends AppCompatActivity {
    protected VPNUtils vpnUtils;
    InternetCheckReceiver internetCheckReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vpnUtils = new VPNUtils(this);
        if (VPNUtils.isVPNActive(this) || VPNUtils.isSuspiciousAppInstalled()) {
            Intent intent = new Intent(this, ModStopper.class);
            startActivity(intent);
            finish();
        }

        internetCheckReceiver = new InternetCheckReceiver() {
            @Override
            protected void onNetworkConnected() {
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.you_are_online_now), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            protected void onNetworkDisConnected() {
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.you_are_offline_now), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.setting), v -> {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }).show();
            }
        };

        // Register the receiver
        registerReceiver(internetCheckReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(internetCheckReceiver);
    }
}

