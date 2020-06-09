package com.zapatatech.santabiblia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

public class LanguageChangeLoader extends AppCompatActivity {
    private static final String TAG = "LanguageChangeLoader";
    private String flagInSharedPref;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_change_loader);
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        flagInSharedPref = sp.getString(Settings.FLAG_LANG, "en");
        //timeout
        new android.os.Handler().postDelayed(new Runnable() {
                    public void run() {
                        Intent refresh = new Intent(LanguageChangeLoader.this, MainActivity.class);
                        startActivity(refresh);
                        finish();
                    }
                },900);
    }

}
