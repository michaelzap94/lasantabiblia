package com.michaelzap94.santabiblia.fragments.settings;


import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.michaelzap94.santabiblia.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

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

        }
}
