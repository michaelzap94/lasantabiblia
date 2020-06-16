package com.zapatatech.santabiblia.fragments.settings;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import com.zapatatech.santabiblia.DatabaseHelper.BibleCreator;
import com.zapatatech.santabiblia.Login;
import com.zapatatech.santabiblia.MainActivity;
import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.interfaces.retrofit.RetrofitAuthService;
import com.zapatatech.santabiblia.models.APIError;
import com.zapatatech.santabiblia.models.AuthInfo;
import com.zapatatech.santabiblia.utilities.CommonMethods;
import com.zapatatech.santabiblia.utilities.RetrofitErrorUtils;
import com.zapatatech.santabiblia.utilities.RetrofitServiceGenerator;
import com.zapatatech.santabiblia.utilities.Util;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.zapatatech.santabiblia.utilities.CommonMethods.MAIN_BIBLE_SELECTED;
import static com.zapatatech.santabiblia.utilities.CommonMethods.getAccessToken;
import static com.zapatatech.santabiblia.utilities.CommonMethods.getRefreshToken;
import static com.zapatatech.santabiblia.utilities.CommonMethods.logOutOfApp;

public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String TAG = "SettingsFragment";
    private static final String MAIN_CONTENT_DB = "content.db";
    private static final String TEMP_FILE_EXT = "-temp";
    private Activity mActivity;
    private PreferenceScreen screen;

    protected static void setListPreferenceData(Context ctx, ListPreference lp) {
        String bibleSelected = PreferenceManager.getDefaultSharedPreferences(ctx).getString(MAIN_BIBLE_SELECTED, null);

        //ArrayList<String> listBibles = BibleCreator.getInstance(ctx).listOfAssetsByType("bibles");

        //TODO: implement logic to only extract bibles
        ArrayList<String> listBibles = new ArrayList<>();
        ArrayList<String> listBiblesDisplayName = new ArrayList<>();
        for (String dbName: ctx.databaseList()) {
            if(!dbName.contains("-journal") && !dbName.equals(MAIN_CONTENT_DB) && !dbName.contains(TEMP_FILE_EXT)) {
                listBibles.add(dbName);
                //split filename in _
                String[] resultSplit = dbName.split("_");
                String displayName = Util.joinArrayResourceName(" ", true, resultSplit);
                listBiblesDisplayName.add(displayName);
            }
        }

        CharSequence[] entries = listBiblesDisplayName.toArray(new CharSequence[listBiblesDisplayName.size()]);
        CharSequence[] entryValues = listBibles.toArray(new CharSequence[listBibles.size()]);
        lp.setEntries(entries);
        lp.setDefaultValue(bibleSelected);
        lp.setEntryValues(entryValues);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
        mActivity = getActivity();
        screen = this.getPreferenceScreen(); // "null". See onViewCreated.
        //ALL PREFERENCES ================================================================================================
            final ListPreference listPreference = (ListPreference) findPreference("pref_bible_selected");
            // THIS IS REQUIRED IF YOU DON'T HAVE 'entries' and 'entryValues' in your XML
            setListPreferenceData(mActivity, listPreference);
            listPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    setListPreferenceData(mActivity, listPreference);
                    return false;
                }
            });

        //ONLINE PREFERENCES ONLY================================================================================================
        if(CommonMethods.checkUserStatus(mActivity) == CommonMethods.USER_ONLINE){
            addPreferencesForLoggedInUsers();
        }
    }

    private void addPreferencesForLoggedInUsers(){
        final PreferenceCategory thirdCategory = (PreferenceCategory) findPreference("pref_section_third");
        Preference backUp = new Preference(screen.getContext());
        backUp.setKey("pref_back_up");
        backUp.setTitle("Back up and Sync Up");
        backUp.setSummary("Last synced: 28/10/20 13:34");
        backUp.setIcon(R.drawable.ic_sync);
        backUp.setOrder(0);
        thirdCategory.addPreference(backUp);
        //================================================================================================
        //add Sign out button programatically
        final PreferenceCategory lastCategory = (PreferenceCategory) findPreference("pref_section_last");
        Preference signOutButton = new Preference(screen.getContext());
        signOutButton.setKey("pref_sign_out");
        signOutButton.setTitle("Sign Out");
        signOutButton.setSummary("Come back soon!");
        signOutButton.setIcon(R.drawable.ic_exit);
        lastCategory.addPreference(signOutButton);
        signOutButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //Blacklist the current refresh token, so it cannot be used to create new access tokens
                CommonMethods.retrofitLogout(mActivity);
                return true;
            }
        });
        //================================================================================================
//        Create the Preferences Manually - so that the key can be set programatically.
//        PreferenceCategory category = new PreferenceCategory(screen.getContext());
//        category.setTitle("Channel Configuration");
//        screen.addPreference(category);
    }

}
