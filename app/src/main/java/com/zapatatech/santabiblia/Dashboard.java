package com.zapatatech.santabiblia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zapatatech.santabiblia.fragments.dashboard.DashboardMainFragment;
import com.zapatatech.santabiblia.fragments.settings.SettingsFragment;
import com.zapatatech.santabiblia.models.Label;
import com.zapatatech.santabiblia.utilities.CommonMethods;

public class Dashboard extends AppCompatActivity {
    private static final String TAG = "Dashboard";
    private BottomNavigationView bottomNavigationView;
    private static final String CAN_GO_BACK_DASH = "CAN_GO_BACK_DASH_DASH";
    private boolean canGoBack;
    private Toolbar mToolbar;
    private DashboardMainFragment mFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setTitle(R.string.dashboard);

        if (savedInstanceState == null) {
            updateCanGoBack(canGoBack, Dashboard.this);
            // The Activity is NOT being re-created so we can instantiate a new Fragment
            // and add it to the Activity
            mFragment = new DashboardMainFragment();
            //Populate one Fragment to cover the WHOLE settings screen
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.dashboard_fragment, mFragment, "dashboard_fragment")
                    .commit();
        } else {
            // The Activity IS being re-created so we don't need to instantiate the Fragment or add it,
            // but if we need a reference to it, we can use the tag we passed to .replace
            mFragment = (DashboardMainFragment) getSupportFragmentManager().findFragmentByTag("dashboard_fragment");
        }

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        CommonMethods.bottomBarActionHandler(bottomNavigationView, R.id.bnav_dashboard, Dashboard.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.bnav_dashboard);
//        getSupportActionBar().setTitle(R.string.dashboard);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBoolean(CAN_GO_BACK_DASH, canGoBack);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            canGoBack = savedInstanceState.getBoolean(CAN_GO_BACK_DASH);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getSupportFragmentManager().popBackStack();
            boolean canGoBack = getSupportFragmentManager().getBackStackEntryCount()>1;
            Log.d(TAG, "onOptionsItemSelected: "+canGoBack);
            Dashboard.updateCanGoBack(canGoBack, Dashboard.this);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        boolean canGoBack = getSupportFragmentManager().getBackStackEntryCount()>1;
        Dashboard.updateCanGoBack(canGoBack, Dashboard.this);
        super.onBackPressed();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void refreshLabelFragmentAfterEdit(Label newLabel) {
        //go back to Label fragment
        getSupportFragmentManager().popBackStack();
        //go back to All Labels fragments
        getSupportFragmentManager().popBackStack();
        //refresh the Edit fragment
        DashboardMainFragment.onLabelClickedFromList(this, newLabel);
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        overridePendingTransition(0, 0);
    }

}
