package com.zapatatech.santabiblia.fragments.settings;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.Settings;
import com.zapatatech.santabiblia.utilities.CommonMethods;

import static com.zapatatech.santabiblia.utilities.CommonMethods.DEFAULT_BIBLE_EXIST;

public class InnerPreferencesFragment extends PreferenceFragmentCompat {
    private static final String TAG = "InnerPreferencesFragmen";
    boolean canGoBack;
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if(CommonMethods.checkUserStatus(getActivity()) == CommonMethods.USER_ONLINE){
            MenuItem item2= menu.findItem(2);
            MenuItem item3= menu.findItem(3);
            item2.setVisible(false);
            item3.setVisible(false);
        } else {
            MenuItem item= menu.findItem(1);
            item.setVisible(false);
        }

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: ");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        Settings.updateCanGoBack(canGoBack, (Settings)getActivity(), "Help");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach: " + context);
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: " + getActivity());
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            // Restore some state before we've even inflated our own layout
            // This could be generic things like an ID that our Fragment represents
            canGoBack = savedInstanceState.getBoolean("canGoBack_inner", false);
            //getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            canGoBack = getActivity().getSupportFragmentManager().getBackStackEntryCount()>0;
            setHasOptionsMenu(true);
        }
        //this.setRetainInstance(true);//screen rotation does not kill app
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Log.d(TAG, "onCreatePreferences: " + getActivity());
        setPreferencesFromResource(R.xml.settings_preferences,rootKey);

        final SwitchPreferenceCompat switchPreferenceCompat = (SwitchPreferenceCompat) getPreferenceManager().findPreference("enable_sync");
        switchPreferenceCompat.setSummaryProvider(new Preference.SummaryProvider() {
            @Override
            public CharSequence provideSummary(Preference preference) {
                if (switchPreferenceCompat.isChecked()) {
                    return "Active";
                }
                return "Inactive";
            }
        });

        final Preference bibleExistPrefs = (Preference) getPreferenceManager().findPreference(DEFAULT_BIBLE_EXIST);
        boolean exist = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(DEFAULT_BIBLE_EXIST, false);
        bibleExistPrefs.setSummary(String.valueOf(exist));

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("canGoBack_inner", canGoBack);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewStateRestored: ");
        super.onViewStateRestored(savedInstanceState);
    }
}
