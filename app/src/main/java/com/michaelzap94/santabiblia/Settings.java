package com.michaelzap94.santabiblia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.michaelzap94.santabiblia.fragments.settings.SettingsFragment;
import com.michaelzap94.santabiblia.utilities.CommonMethods;

public class Settings extends AppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    private static final String TAG = "Settings";
    private static final String CAN_GO_BACK = "CAN_GO_BACK";
    private BottomNavigationView bottomNavigationView;
    private boolean canGoBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        updateCanGoBack(canGoBack, Settings.this);
        //Populate one Fragment to cover the WHOLE settings screen
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_settings_fragment_item, new SettingsFragment())
                .commit();

        CommonMethods.bottomBarActionHandler((BottomNavigationView) findViewById(R.id.bottom_navigation), R.id.bnav_settings, Settings.this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBoolean(CAN_GO_BACK, canGoBack);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            canGoBack = savedInstanceState.getBoolean(CAN_GO_BACK);
        } else {
            canGoBack = getSupportFragmentManager().getBackStackEntryCount()>0;
        }
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
        //WHEN USER TAPS ON A SECTION IN SETTINGS AND ALLOWS YOU TO CUSTOMIZE transitions/animations
        return false;
    }

    public static void updateCanGoBack(boolean canGoBack, AppCompatActivity activity){
        //ActionBar arrow show only if INNER settings
        ActionBar actionBar = activity.getSupportActionBar();

        if(actionBar != null && canGoBack == false){
            actionBar.setDisplayHomeAsUpEnabled(false);
        } else {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getSupportFragmentManager().popBackStack();
            boolean canGoBack = getSupportFragmentManager().getBackStackEntryCount()>1;
            Log.d(TAG, "onOptionsItemSelected: "+canGoBack);
            Settings.updateCanGoBack(canGoBack, Settings.this);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        boolean canGoBack = getSupportFragmentManager().getBackStackEntryCount()>1;
        Settings.updateCanGoBack(canGoBack, Settings.this);
        super.onBackPressed();
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        overridePendingTransition(0, 0);
    }
}
