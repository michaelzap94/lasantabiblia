package com.michaelzap94.santabiblia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.michaelzap94.santabiblia.fragments.settings.SettingsFragment;
import com.michaelzap94.santabiblia.utilities.CommonMethods;

import java.util.Locale;

public class Settings extends AppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    private static final String TAG = "Settings";
    private BottomNavigationView bottomNavigationView;
    private static final String CAN_GO_BACK = "CAN_GO_BACK";
    private boolean canGoBack;
    private Toolbar mToolbar;
    private AppBarLayout mAppBarLayout;
    //FLAGS==========================
    static final String FLAG_LANG = "Lang";
    private Menu menu;
    private String flagInSharedPref;
    private Drawable flag_gb;
    private Drawable flag_es;
    private SharedPreferences sp;
    //GETTERS=========================
    public AppBarLayout getmAppBarLayout(){
        return mAppBarLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.settings_appbarlayout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setTitle(R.string.settings);

        //FLAGS================================================================================
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        flagInSharedPref = sp.getString(FLAG_LANG, "");

        flag_gb = ContextCompat.getDrawable(getApplicationContext(),R.drawable.flag_gb);
        flag_es = ContextCompat.getDrawable(getApplicationContext(),R.drawable.flag_es);
        //=====================================================================================

        updateCanGoBack(canGoBack, Settings.this);
        //Populate one Fragment to cover the WHOLE settings screen
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_settings_fragment_item, new SettingsFragment())
                .commit();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        CommonMethods.bottomBarActionHandler(bottomNavigationView, R.id.bnav_settings, Settings.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.bnav_settings);
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
        //WHEN USER TAPS ON A SECTION IN SETTINGS AND ALLOWS YOU TO CUSTOMIZE transitions/animations
        return false;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_languages, menu);

        //you need menu.findItem as the Menu has not been fully inflated yet. so findViewById will not work.
        MenuItem item_gb = menu.findItem(R.id.lang_en);
        MenuItem item_es = menu.findItem(R.id.lang_es);
        if(flagInSharedPref.length()>0) {

            switch (flagInSharedPref) {
                case "en":
                    menu.getItem(0).setIcon(flag_gb);
                    item_gb.setChecked(true);
                    break;
                case "es":
                    menu.getItem(0).setIcon(flag_es);
                    item_es.setChecked(true);
                    break;

            }
        }else{
            //default language is English for now
            sp.edit().putString(FLAG_LANG,"en").apply();
            menu.getItem(0).setIcon(flag_gb);
            item_gb.setChecked(true);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            getSupportFragmentManager().popBackStack();
//            boolean canGoBack = getSupportFragmentManager().getBackStackEntryCount()>1;
//            Log.d(TAG, "onOptionsItemSelected: "+canGoBack);
//            Settings.updateCanGoBack(canGoBack, Settings.this);
//        }
//        return true;

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case android.R.id.home:
                getSupportFragmentManager().popBackStack();
                boolean canGoBack = getSupportFragmentManager().getBackStackEntryCount()>1;
                Log.d(TAG, "onOptionsItemSelected: "+canGoBack);
                Settings.updateCanGoBack(canGoBack, Settings.this);
                return true;
            case R.id.lang_en:
                sp.edit().putString(FLAG_LANG,"en").apply();
                if (item.isChecked()){
                    item.setChecked(false);
                }else{
                    item.setChecked(true);
                    setLocale("en");
                }
//                menu.getItem(0).setIcon(flag_gb);
                return true;
            case R.id.lang_es:
                sp.edit().putString(FLAG_LANG,"es").apply();
                if (item.isChecked()){
                    item.setChecked(false);
                }else{
                    item.setChecked(true);
                    setLocale("es");
                }
//                menu.getItem(0).setIcon(flag_es);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setLocale(String lang) {
        //change language files===================
        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        //start Intent===================
        Intent refresh = new Intent(this, LanguageChangeLoader.class);
        refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);//CLEAR ALL ACTIVITIES
        startActivity(refresh);
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
