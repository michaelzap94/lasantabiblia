package com.zapatatech.santabiblia.fragments.settings;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.Settings;
import com.zapatatech.santabiblia.utilities.CommonMethods;

public class HelpFragment extends PreferenceFragmentCompat {

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        this.setRetainInstance(true);//screen rotation does not kill app
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey);

        boolean canGoBack = getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0;
        Settings.updateCanGoBack(canGoBack, (Settings) getActivity(), null);
    }
}