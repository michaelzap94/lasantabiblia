package com.michaelzap94.santabiblia.fragments.settings;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.michaelzap94.santabiblia.R;

import static com.michaelzap94.santabiblia.utilities.CommonMethods.BIBLE_EXIST;
import static com.michaelzap94.santabiblia.utilities.CommonMethods.MY_PREFS_NAME;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);



        }
}
