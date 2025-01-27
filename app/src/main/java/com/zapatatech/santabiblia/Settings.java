package com.zapatatech.santabiblia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import android.annotation.SuppressLint;
import android.content.Intent;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zapatatech.santabiblia.fragments.settings.InnerPreferencesFragment;
import com.zapatatech.santabiblia.fragments.settings.SettingsFragment;
import com.zapatatech.santabiblia.utilities.CommonMethods;

import java.util.Locale;
import java.util.Set;

public class Settings extends AppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback, PreferenceFragmentCompat.OnPreferenceStartScreenCallback {
    private static final String TAG = "Settings";
    private BottomNavigationView bottomNavigationView;
    private static final String CAN_GO_BACK = "CAN_GO_BACK";
    private boolean canGoBack;
    private Toolbar mToolbar;
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView userNameTv;
    private ImageView userPic;
    private SettingsFragment mFragment;
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
        Log.d(TAG, "onCreate: ");
        initStateSettings();
        
        if (savedInstanceState == null) {
            updateCanGoBack(canGoBack, Settings.this, null);
            // The Activity is NOT being re-created so we can instantiate a new Fragment
            // and add it to the Activity
            mFragment = new SettingsFragment();
            //Populate one Fragment to cover the WHOLE settings screen
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_settings_fragment_item, mFragment, "settings_fragment")
                    .commit();
        } else {
            // The Activity IS being re-created so we don't need to instantiate the Fragment or add it,
            // but if we need a reference to it, we can use the tag we passed to .replace
            mFragment = (SettingsFragment) getSupportFragmentManager().findFragmentByTag("settings_fragment");
        }

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        CommonMethods.bottomBarActionHandler(bottomNavigationView, R.id.bnav_settings, Settings.this);
    }
    
    public void initStateSettings(){
        //-----------------------------------------------------------------------------------------
        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        if(CommonMethods.checkUserStatus(this) == CommonMethods.USER_ONLINE) {
            Log.d(TAG, "onCreate: CommonMethods.USER_ONLINE");
            stub.setLayoutResource(R.layout.settings_collapsing_toolbar);
            View inflatedView = stub.inflate();
            //parent component
            mAppBarLayout = (AppBarLayout) findViewById(R.id.inflated_bar_layout);
            //children
            collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
            mToolbar = (Toolbar) inflatedView.findViewById(R.id.toolbar);
            //------------------------------------------------------------------
            userNameTv = inflatedView.findViewById(R.id.settings_subtitle_online);
            String userName = CommonMethods.decodeJWTAndCreateUser(this).getName();
            userNameTv.setText(userName);
            //String userPic = CommonMethods.decodeJWTAndCreateUser(this).getPic();

        } else {
            Log.d(TAG, "onCreate: ELSE CommonMethods.USER_ONLINE");

            stub.setLayoutResource(R.layout.settings_toolbar);
            View inflatedView = stub.inflate();
            //parent component
            mAppBarLayout = (AppBarLayout) findViewById(R.id.inflated_bar_layout);
            //children
            mToolbar = (Toolbar) inflatedView.findViewById(R.id.toolbar);
        }
        //-----------------------------------------------------------------------------------------
        setSupportActionBar(mToolbar);
        setTitle(R.string.settings);
        Log.d(TAG, "onCreate: AFTER");
        //set account icon instead of three dots
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_login_person);
        mToolbar.setOverflowIcon(drawable);
    }

//    private void setExpandEnabled(boolean enabled) {
//        mAppBarLayout.setExpanded(enabled, false);
//        mAppBarLayout.setActivated(enabled);
//        final AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();
//        if (enabled) params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
//        else params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED);
//        collapsingToolbarLayout.setLayoutParams(params);
//    }
//
//    public void lockAppBarClosed() {
//        mAppBarLayout.setExpanded(false, false);
//        mAppBarLayout.setActivated(false);
//        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)mAppBarLayout.getLayoutParams();
//        lp.height = (int) getResources().getDimension(R.dimen.toolbar_height);
//    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.bnav_settings);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBoolean(CAN_GO_BACK, canGoBack);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState: ");
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            canGoBack = savedInstanceState.getBoolean(CAN_GO_BACK);
        } else {
            canGoBack = getSupportFragmentManager().getBackStackEntryCount()>0;
        }
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
        Log.d(TAG, "onPreferenceStartFragment: " + pref.toString());
        //WHEN USER TAPS ON A SECTION IN SETTINGS AND ALLOWS YOU TO CUSTOMIZE transitions/animations
//        if(CommonMethods.checkUserStatus(this) == CommonMethods.USER_ONLINE) {
//            collapsingToolbarLayout.setTitle(pref.toString());
//        } else {
//            mToolbar.setTitle(pref.toString());
//        }
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        InnerPreferencesFragment fragment = new InnerPreferencesFragment();
//        Bundle args = new Bundle();
//        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, pref.getKey());
//        fragment.setArguments(args);
//        ft.replace(R.id.activity_settings_fragment_item, fragment, pref.getKey());
//        ft.addToBackStack(pref.getKey());
//        ft.commit();
        return false;
    }
    @Override
    public boolean onPreferenceStartScreen(PreferenceFragmentCompat preferenceFragmentCompat,
                                           PreferenceScreen preferenceScreen) {
        Log.d(TAG, "onPreferenceStartScreen: " + preferenceScreen.toString());
        return false;
    }

    public static void updateCanGoBack(boolean canGoBack, Settings activity, String title){
        Log.d(TAG, "updateCanGoBack: " + canGoBack);
        Log.d(TAG, "updateCanGoBack: title: " + title);
        //ActionBar arrow show only if INNER settings
        ActionBar actionBar = activity.getSupportActionBar();
        Log.d(TAG, "updateCanGoBack: " + activity);
        Log.d(TAG, "updateCanGoBack: " + actionBar);

        if(actionBar != null && canGoBack == false){
            Log.d(TAG, "updateCanGoBack: first");
            String mtitle = activity.getString(R.string.settings);;
            actionBar.setDisplayHomeAsUpEnabled(false);
            if(CommonMethods.checkUserStatus(activity) == CommonMethods.USER_ONLINE) {
                activity.getmCollapsingToolbarLayout().setTitle(mtitle);
            } else {
                activity.setTitle(mtitle);
            }
        } else {
            //TODO: replace collapsing toolbar with normal toolbar
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if(CommonMethods.checkUserStatus(activity) == CommonMethods.USER_ONLINE) {
                activity.getmAppBarLayout().setExpanded(false);
                if(title != null){
                    activity.getmCollapsingToolbarLayout().setTitle(title);
                }
            } else {
                if(title != null){
                    Log.d(TAG, "updateCanGoBack: " + activity.getmToolbar());
                    activity.setTitle(title);
                }
            }
//            CollapsingToolbarLayout collapsingToolbar = activity.getmCollapsingToolbarLayout();
//            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();
//            params.setScrollFlags(0); // list other flags here by |
//            collapsingToolbar.setLayoutParams(params);
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //show icons in the dropdown
        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            //noinspection RestrictedApi
            m.setOptionalIconsVisible(true);
        }

        if(CommonMethods.checkUserStatus(this) == CommonMethods.USER_ONLINE){
            MenuItem userDetails = menu.add(Menu.NONE, 2, Menu.NONE, "User Details");
            userDetails.setIcon(R.drawable.ic_edit_user_details);
            userDetails.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
            userDetails.setOnMenuItemClickListener (new MenuItem.OnMenuItemClickListener(){
                @Override
                public boolean onMenuItemClick (MenuItem item){

                    return true;
                }
            });
            MenuItem logOut = menu.add(Menu.NONE, 3, Menu.NONE, "Log Out");
            logOut.setIcon(R.drawable.ic_exit);
            logOut.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
            logOut.setOnMenuItemClickListener (new MenuItem.OnMenuItemClickListener(){
                @Override
                public boolean onMenuItemClick (MenuItem item){
                    CommonMethods.retrofitLogout(Settings.this);
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
                    String account_type = CommonMethods.getAccountType(Settings.this);
                    if(account_type != null){
                        //if you are offline because of internet or network unavailability but have credentials
                        CommonMethods.retrofitVerifyCredentials(Settings.this);
                    } else {
                        //since you are not online, -> do not do retrofitLogout()
                        CommonMethods.logOutOfApp(Settings.this);
                    }
                    return true;
                }
            });
        }
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
                Settings.updateCanGoBack(canGoBack, Settings.this, null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        boolean canGoBack = getSupportFragmentManager().getBackStackEntryCount()>1;
        Settings.updateCanGoBack(canGoBack, Settings.this, null);
        super.onBackPressed();
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        overridePendingTransition(0, 0);
    }
}
