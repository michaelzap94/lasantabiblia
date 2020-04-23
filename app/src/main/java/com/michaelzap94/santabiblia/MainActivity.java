package com.michaelzap94.santabiblia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.michaelzap94.santabiblia.DatabaseHelper.BibleCreator;
import com.michaelzap94.santabiblia.utilities.CommonMethods;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MainActivity extends BaseActivityTopDrawer  {
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_main_base);
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        CommonMethods.checkDatabaseExistLoad(MainActivity.this);

        CommonMethods.bottomBarActionHandler((BottomNavigationView) findViewById(R.id.bottom_navigation), R.id.bnav_home, MainActivity.this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        overridePendingTransition(0, 0);
    }
}
