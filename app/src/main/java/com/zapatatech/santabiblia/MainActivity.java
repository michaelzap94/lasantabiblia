package com.zapatatech.santabiblia;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.zapatatech.santabiblia.utilities.CommonMethods;

import java.util.Locale;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MainActivity";
    //FLAGS==========================
    static final String FLAG_LANG = "Lang";
    private String flagInSharedPref;
    private SharedPreferences sp;
    //==========================================================
    //private static int SPLASH_TIME_OUT = 600;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //=============================================================================================
        //SET INITIAL LANGUAGE==============================================================
        sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        flagInSharedPref = sp.getString(FLAG_LANG, "");
        Log.d(TAG, "onCreate: flag " + flagInSharedPref);
        Log.d(TAG, "onCreate: flag resources " + getResources().getConfiguration().locale.getLanguage());
//        String defaultLang = Locale.getDefault().getLanguage();
//        String currentLang = getResources().getConfiguration().locale.getLanguage();
        //only change language if necessary
        if (!flagInSharedPref.equals("") && !flagInSharedPref.equals(getResources().getConfiguration().locale.getLanguage())) {
            Locale locale = new Locale(flagInSharedPref);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }
        //=============================================================================================
        CommonMethods.checkBibleSelectedExist(MainActivity.this);

        //======================================================================================
        //TODO: REMOVE THIS IMPLEMENTATION OF SLASH SCREEN: THIS IS TEMPORAL TO AVOID A BUTTON
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                showLoginOrHomePage();
//            }
//        }, SPLASH_TIME_OUT);
        showLoginOrHomePage();
    }

    /** If user has selected offline use or is logged in -> Take him to the Home activity.
     * ELSE -> take him to the Login Activity
     */
    private void showLoginOrHomePage(){
        int userStatus = CommonMethods.checkUserStatus(this);
        String account_type = CommonMethods.getAccountType(this);
        //TODO: Check the credentials of User are still valid
//        if(CommonMethods.getAccountType(this, "google") == "google") {
//            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//            if(account != null) {
//            }
//        }
        //==============================================================
        //ONLY ALLOW TO USE THE APP-> if USER_OFFLINE OR USER_ONLINE, not WHEN USER_NONE
        if(userStatus == CommonMethods.USER_NONE) {
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        } else if(userStatus == CommonMethods.USER_OFFLINE) {
            if(account_type != null){
                CommonMethods.retrofitVerifyCredentials(MainActivity.this);
            } else {
                Intent intent = new Intent(MainActivity.this, Home.class);
                startActivity(intent);
                finish();
            }
        } else { //CommonMethods.USER_ONLINE
            if(account_type != null){
                //so we don't make too many network requests
                //CHECK THE expiry date OF THE ACCESS_TOKEN, IF EXPIRED, VERIFY:
                CommonMethods.retrofitVerifyCredentials(MainActivity.this);
            } else {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        }

    }

}
