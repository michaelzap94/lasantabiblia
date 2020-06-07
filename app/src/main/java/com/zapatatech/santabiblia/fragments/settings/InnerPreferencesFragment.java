package com.zapatatech.santabiblia.fragments.settings;


import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.Settings;

import static com.zapatatech.santabiblia.utilities.CommonMethods.DEFAULT_BIBLE_EXIST;

public class InnerPreferencesFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
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

        boolean canGoBack = getActivity().getSupportFragmentManager().getBackStackEntryCount()>0;
        Settings.updateCanGoBack(canGoBack, (Settings)getActivity());
    }
}
