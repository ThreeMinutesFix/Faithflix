package com.primeplay.faithflix.Ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.primeplay.faithflix.R;
import com.primeplay.faithflix.fragmentsUi.AccountFragment;
import com.primeplay.faithflix.fragmentsUi.HomeFragment;

import me.ibrahimsn.lib.NiceBottomBar;

public class PrimeActivity extends BaseActivity {
    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;
    AccountFragment accountFragment;
    HomeFragment homeFragment;
    public static NiceBottomBar bottomBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prime);
        transparentStatusAndNavigation();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        homeFragment = new HomeFragment();
        openHomeFragment(fragmentTransaction);
        bottomBar = findViewById(R.id.primeBottomBar);
        bottomBar.setOnItemSelected(integer -> {

            int selectedItemIndex = integer;
            FragmentTransaction newTransaction = fragmentManager.beginTransaction();
            if (selectedItemIndex == 0) {
                openHomeFragment(newTransaction);
            } else if (selectedItemIndex == 1) {
//                openSearchFragment(newTransaction);
            } else if (selectedItemIndex == 2) {
//                openComingFragment(newTransaction);
            } else if (selectedItemIndex == 3) {
//                favoriteOpen(newTransaction);
            } else if (selectedItemIndex == 4) {
                openSpaceFragment(newTransaction);
            }
            return null;
        });
    }

    private void openHomeFragment(FragmentTransaction fragmentTransaction)
    {
        HomeFragment homeFragment = new HomeFragment();
        fragmentTransaction.replace(R.id.prime_container, homeFragment);
        fragmentTransaction.commit();
    }
    private void openSpaceFragment(FragmentTransaction newTransaction) {
        AccountFragment accounts = new AccountFragment();
        newTransaction.replace(R.id.prime_container, accounts);
        newTransaction.addToBackStack(null);
        newTransaction.commit();
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
}