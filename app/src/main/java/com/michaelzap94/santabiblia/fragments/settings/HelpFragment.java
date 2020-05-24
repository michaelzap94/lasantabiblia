package com.michaelzap94.santabiblia.fragments.settings;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.Settings;

public class HelpFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey);
        ((Settings) getActivity()).getmAppBarLayout().setExpanded(false);


        boolean canGoBack = getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0;
        Settings.updateCanGoBack(canGoBack, (Settings) getActivity());
    }
}