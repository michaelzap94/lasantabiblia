package com.zapatatech.santabiblia.fragments.settings;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

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

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.zapatatech.santabiblia.utilities.CommonMethods.MAIN_BIBLE_SELECTED;
import static com.zapatatech.santabiblia.utilities.CommonMethods.getAccessToken;
import static com.zapatatech.santabiblia.utilities.CommonMethods.getRefreshToken;

public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String TAG = "SettingsFragment";
    private Activity mActivity;

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
        mActivity = getActivity();
        //================================================================================================
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
        //================================================================================================
            Preference signOutButton = findPreference("pref_sign_out");
            signOutButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    //Blacklist the current refresh token, so it cannot be used to create new access tokens
                    retrofitLogout();
                    return true;
                }
            });
        //================================================================================================
    }

    private void retrofitLogout(){

        String refreshToken = getRefreshToken(mActivity);
        String accessToken = getAccessToken(mActivity);
        if( refreshToken != null && accessToken != null ) {
            RetrofitAuthService logOutService = RetrofitServiceGenerator.createService(RetrofitAuthService.class, accessToken);

            HashMap<String, Object> logOutObject = new HashMap<>();
            logOutObject.put("refresh", refreshToken);

            Call<AuthInfo> call = logOutService.requestLogOut(logOutObject);
            call.enqueue(new Callback<AuthInfo >() {
                @Override
                public void onResponse(Call<AuthInfo> call, Response<AuthInfo> response) {
                    if (response.isSuccessful()) {
                        // user object available
                        Log.d(TAG, "onResponse: success" + response.body());
                        if(!response.body().getStatus().equals("success") ){
                            String error = "Sorry, something went wrong";
                            if(response.body().getDetail() != null) {
                                error = response.body().getDetail();
                            } else if (response.body().getError() != null) {
                                error = response.body().getError().toString();
                            }
                            Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // parse the response body …
                        APIError error = RetrofitErrorUtils.parseError(response);
                        // … and use it to show error information
                        Toast.makeText(mActivity, error.message(), Toast.LENGTH_SHORT).show();
                    }
                    //Remove Both tokens and change status to USER_NONE
                    CommonMethods.logOutOfApp(mActivity);
                }

                @Override
                public void onFailure(Call<AuthInfo> call, Throwable t) {
                    // something went completely south (like no internet connection)
                    Log.d("onFailure Error", t.getMessage());
                    //Remove Both tokens and change status to USER_NONE
                    CommonMethods.logOutOfApp(mActivity);
                }
            });
        } else {
            //Remove Both tokens if any and change status to USER_NONE
            CommonMethods.logOutOfApp(mActivity);
        }
    }


}
