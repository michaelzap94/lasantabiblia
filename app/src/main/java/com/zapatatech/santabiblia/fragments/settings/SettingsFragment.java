package com.zapatatech.santabiblia.fragments.settings;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.zapatatech.santabiblia.DatabaseHelper.ContentDBHelper;
import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.retrofit.Pojos.POJOSyncUp;
import com.zapatatech.santabiblia.utilities.CommonMethods;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

import static com.zapatatech.santabiblia.utilities.CommonMethods.DEFAULT_BIBLE_EXIST;
import static com.zapatatech.santabiblia.utilities.CommonMethods.MAIN_BIBLE_SELECTED;

public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String TAG = "SettingsFragment";
    private static final String PREF_BACK_UP = "pref_back_up";
    private Activity mActivity;
    private PreferenceScreen screen;
    private CompositeDisposable disposable = new CompositeDisposable();


    protected static void setListPreferenceData(Context ctx, ListPreference lp) {
        String bibleSelected = PreferenceManager.getDefaultSharedPreferences(ctx).getString(MAIN_BIBLE_SELECTED, null);

        //ArrayList<String> listBibles = BibleCreator.getInstance(ctx).listOfAssetsByType("bibles");

        //BIBLES EXAMPLE: RVR60.type-bible.db | TRADUCCIÓN EN LENGUAJE ACTUAL.type-bible.db

        ArrayList[] biblesDownloaded = CommonMethods.getBiblesDownloaded(ctx);//2 elements returned: listBibles AND listBiblesDisplayName

        ArrayList<String> listBibles = biblesDownloaded[0];
        ArrayList<String> listBiblesDisplayName = biblesDownloaded[1];

        CharSequence[] entries = listBiblesDisplayName.toArray(new CharSequence[listBiblesDisplayName.size()]);
        CharSequence[] entryValues = listBibles.toArray(new CharSequence[listBibles.size()]);
        lp.setEntries(entries);
        lp.setDefaultValue(bibleSelected);
        lp.setEntryValues(entryValues);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Log.d(TAG, "onCreatePreferences: " + getActivity());
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
        addSyncUpFunctionality();
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

    private void addSyncUpFunctionality(){
//        POJOSyncUp syncUpObj = ContentDBHelper.getInstance(mActivity).getSyncUp(null);
//        String summary = "Not available";
//        if(syncUpObj != null && !syncUpObj.getEmail().equals("offline")) {
//            summary = "Last synced: " + syncUpObj.getUpdated();
//        }
        final PreferenceCategory thirdCategory = (PreferenceCategory) findPreference("pref_section_third");
        Preference backUp = new Preference(screen.getContext());
        backUp.setKey(PREF_BACK_UP);
        backUp.setTitle("Back up and Sync Up");
        //backUp.setSummary(summary);
        backUp.setIcon(R.drawable.ic_sync);
        backUp.setOrder(0);
        thirdCategory.addPreference(backUp);
        backUp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try{
                    CommonMethods.retrofitStartSyncUp(mActivity, disposable);
                } catch (Exception e) {
                    Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }

    private void updateSyncUpDate(){
        POJOSyncUp syncUpObj = ContentDBHelper.getInstance(mActivity).getSyncUp(null);
        String summary = "Not available";
        if(syncUpObj != null && !syncUpObj.getEmail().equals("offline")) {
            summary = "Last synced: " + syncUpObj.getUpdated();
        }
        Preference backUp = (Preference) getPreferenceManager().findPreference(PREF_BACK_UP);
        backUp.setSummary(summary);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: ");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: " + getActivity());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        //ONLINE PREFERENCES ONLY================================================================================================
        if(CommonMethods.checkUserStatus(mActivity) == CommonMethods.USER_ONLINE){
            updateSyncUpDate();
            registerWorkManagerListener(mActivity);
        }
    }

    public void registerWorkManagerListener(Activity _mActivity){
        WorkManager.getInstance(_mActivity).getWorkInfosForUniqueWorkLiveData(CommonMethods.DATA_SYNCUP_UNIQUE)
                .observe((LifecycleOwner) _mActivity, (workInfoList) -> {
                    if(workInfoList != null && workInfoList.size() > 0) {
                        WorkInfo workInfo = workInfoList.get(0);
                        Log.d(TAG, "startWorkManagerDownloadResource: workInfo: " + workInfo);
                        Log.d(TAG, "startWorkManagerDownloadResource: workInfo.getState(): " + workInfo.getState());
                        if (workInfo != null && workInfo.getState()  == WorkInfo.State.SUCCEEDED) {
                            updateSyncUpDate();
                        } else if (workInfo != null && workInfo.getState()  == WorkInfo.State.CANCELLED) {

                        } else if (workInfo != null && workInfo.getState()  == WorkInfo.State.FAILED) {

                        }

                        if (workInfo != null && workInfo.getState().isFinished()) {
                            Log.d(TAG, "registerWorkManagerListener: pruning " + workInfo);
                            WorkManager.getInstance(_mActivity).pruneWork();//kill the workmanager we started before this
                        }
                    }
                });
    }
}
