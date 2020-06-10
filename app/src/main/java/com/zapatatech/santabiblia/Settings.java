package com.zapatatech.santabiblia;

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
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zapatatech.santabiblia.fragments.settings.SettingsFragment;
import com.zapatatech.santabiblia.utilities.CommonMethods;

import java.util.Locale;
import java.util.Set;

public class Settings extends AppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    private static final String TAG = "Settings";
    private BottomNavigationView bottomNavigationView;
    private static final String CAN_GO_BACK = "CAN_GO_BACK";
    private boolean canGoBack;
    private Toolbar mToolbar;
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    //GETTERS=========================
    public AppBarLayout getmAppBarLayout(){
        return mAppBarLayout;
    }
    public CollapsingToolbarLayout getmCollapsingToolbarLayout(){
        return collapsingToolbarLayout;
    }
    public Toolbar getmToolbar(){
        return mToolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.settings_appbarlayout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(mToolbar);
        setTitle(R.string.settings);

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
        collapsingToolbarLayout.setTitle(pref.toString());
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

    public static void updateCanGoBack(boolean canGoBack, Settings activity){
        //ActionBar arrow show only if INNER settings
        ActionBar actionBar = activity.getSupportActionBar();

        if(actionBar != null && canGoBack == false){
            actionBar.setDisplayHomeAsUpEnabled(false);
            activity.getmCollapsingToolbarLayout().setTitle(activity.getResources().getString(R.string.settings));
        } else {
            actionBar.setDisplayHomeAsUpEnabled(true);
            activity.getmAppBarLayout().setExpanded(false);
//            CollapsingToolbarLayout collapsingToolbar = activity.getmCollapsingToolbarLayout();
//            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();
//            params.setScrollFlags(0); // list other flags here by |
//            collapsingToolbar.setLayoutParams(params);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(CommonMethods.checkUserStatus(this) == CommonMethods.USER_ONLINE){
            MenuItem item = menu.add(Menu.NONE, 1, Menu.NONE, "Log In");
            item.setIcon(R.drawable.ic_bookmarked);
            item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            item.setOnMenuItemClickListener (new MenuItem.OnMenuItemClickListener(){
                @Override
                public boolean onMenuItemClick (MenuItem item){

                    return true;
                }
            });
        } else {
            MenuItem item = menu.add(Menu.NONE, 1, Menu.NONE, "Log In");
            item.setIcon(R.drawable.ic_account_circle);
            item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            item.setOnMenuItemClickListener (new MenuItem.OnMenuItemClickListener(){
                @Override
                public boolean onMenuItemClick (MenuItem item){
                    CommonMethods.logOutOfApp(Settings.this);
                    return true;
                }
            });
        }
//        int book_bookmarked = prefs.getInt(CommonMethods.BOOK_BOOKMARKED, 0);
//        int chapter_bookmarked = prefs.getInt(CommonMethods.CHAPTER_BOOKMARKED, 0);
//        MenuItem item = menu.add(Menu.NONE, 1, Menu.NONE, "Bookmark");
//        if((chapter_bookmarked != 0 && book_bookmarked != 0) && (chapter_bookmarked == chapter_number && book_bookmarked == book_number)){
//            item.setIcon(R.drawable.ic_bookmarked);
//        } else {
//            item.setIcon(R.drawable.ic_notbookmarked);
//        }
//        item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//        item.setOnMenuItemClickListener (new MenuItem.OnMenuItemClickListener(){
//            @Override
//            public boolean onMenuItemClick (MenuItem item){
//                Log.d(TAG, "onMenuItemClick: " + item.getIcon().toString());
//                int chapter_bookmarked = prefs.getInt(CommonMethods.CHAPTER_BOOKMARKED, 0);
//                int book_bookmarked = prefs.getInt(CommonMethods.BOOK_BOOKMARKED, 0);
//                Log.d(TAG, "onMenuItemClick: chapter_bookmarked: " + chapter_bookmarked + " book_bookmarked: " + book_bookmarked);
//                if((chapter_bookmarked != 0 && book_bookmarked != 0) && (chapter_bookmarked == chapter_number && book_bookmarked == book_number)) {
//                    CommonMethods.setBookmark(prefs, 0, 0);
//                    item.setIcon(R.drawable.ic_notbookmarked);
//                } else {
//                    //when clicked it was not bookmarked, so bookmark it
//                    CommonMethods.setBookmark(prefs, book_number, chapter_number);
//                    item.setIcon(R.drawable.ic_bookmarked);
//                }
//                return true;
//            }
//        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                getSupportFragmentManager().popBackStack();
                boolean canGoBack = getSupportFragmentManager().getBackStackEntryCount()>1;
                Log.d(TAG, "onOptionsItemSelected: "+canGoBack);
                Settings.updateCanGoBack(canGoBack, Settings.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
