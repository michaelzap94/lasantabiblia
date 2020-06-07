package com.zapatatech.santabiblia.fragments.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.Settings;

public class NotificationsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_notifications, rootKey);

        boolean canGoBack = getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0;
        Settings.updateCanGoBack(canGoBack, (Settings) getActivity());
    }
}
