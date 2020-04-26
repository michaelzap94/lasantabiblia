package com.michaelzap94.santabiblia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.michaelzap94.santabiblia.utilities.CommonMethods;

public class Search extends AppCompatActivity {
    private static final String TAG = "Search";
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);



        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        CommonMethods.bottomBarActionHandler(bottomNavigationView, R.id.bnav_search, Search.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.bnav_search);
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
