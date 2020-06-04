package com.michaelzap94.santabiblia.fragments.settings;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.michaelzap94.santabiblia.DatabaseHelper.BibleCreator;
import com.michaelzap94.santabiblia.R;

import java.util.ArrayList;

import static com.michaelzap94.santabiblia.utilities.CommonMethods.DEFAULT_BIBLE_EXIST;
import static com.michaelzap94.santabiblia.utilities.CommonMethods.MAIN_BIBLE_SELECTED;

public class SettingsFragment extends PreferenceFragmentCompat {

    protected static void setListPreferenceData(Context ctx, ListPreference lp) {
        String bibleSelected = PreferenceManager.getDefaultSharedPreferences(ctx).getString(MAIN_BIBLE_SELECTED, null);

        ArrayList<String> listBibles = BibleCreator.getInstance(ctx).listOfAssetsByType("bibles");
        CharSequence[] entries = listBibles.toArray(new CharSequence[listBibles.size()]);
        CharSequence[] entryValues = entries;
        lp.setEntries(entries);
        lp.setDefaultValue(bibleSelected);
        lp.setEntryValues(entryValues);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        final ListPreference listPreference = (ListPreference) findPreference("pref_bible_selected");

        // THIS IS REQUIRED IF YOU DON'T HAVE 'entries' and 'entryValues' in your XML
        setListPreferenceData(getActivity(), listPreference);
        listPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                setListPreferenceData(getActivity(), listPreference);
                return false;
            }
        });

        }

}
