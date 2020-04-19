package com.michaelzap94.santabiblia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.michaelzap94.santabiblia.utilities.CommonMethods;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CommonMethods.bottomBarActionHandler((BottomNavigationView) findViewById(R.id.bottom_navigation), R.id.bnav_home, MainActivity.this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }

    protected void checkDatabaseExistLoad(){
        SharedPreferences sp = MainActivity.this.PreferencesManager.getDefault
    }
}
