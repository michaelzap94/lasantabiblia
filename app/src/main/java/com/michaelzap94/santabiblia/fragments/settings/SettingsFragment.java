package com.michaelzap94.santabiblia.fragments.settings;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.michaelzap94.santabiblia.DatabaseHelper.BibleCreator;
import com.michaelzap94.santabiblia.Login;
import com.michaelzap94.santabiblia.MainActivity;
import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.utilities.CommonMethods;

import java.util.ArrayList;

import static com.michaelzap94.santabiblia.utilities.CommonMethods.DEFAULT_BIBLE_EXIST;
import static com.michaelzap94.santabiblia.utilities.CommonMethods.MAIN_BIBLE_SELECTED;

public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String TAG = "SettingsFragment";
    private Context mContext;

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
        mContext = getActivity();
        //================================================================================================
            final ListPreference listPreference = (ListPreference) findPreference("pref_bible_selected");
            // THIS IS REQUIRED IF YOU DON'T HAVE 'entries' and 'entryValues' in your XML
            setListPreferenceData(mContext, listPreference);
            listPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    setListPreferenceData(mContext, listPreference);
                    return false;
                }
            });
        //================================================================================================
            Preference signOutButton = findPreference("pref_sign_out");
            signOutButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    boolean offlineTrue = CommonMethods.updateUserOfflineSelection(mContext, false);
                    if(!offlineTrue){
                        Intent intent = new Intent(mContext, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);//CLEAR ALL ACTIVITIES
                        startActivity(intent);
                    } else {
                        Toast.makeText(mContext, "Offline Selection not Updated", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
        //================================================================================================
    }

}
