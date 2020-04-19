package com.michaelzap94.santabiblia;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.michaelzap94.santabiblia.utilities.CommonMethods;

public class Search extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);



        CommonMethods.bottomBarActionHandler((BottomNavigationView) findViewById(R.id.bottom_navigation), R.id.bnav_search, Search.this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }
}
