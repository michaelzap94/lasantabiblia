package com.zapatatech.santabiblia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zapatatech.santabiblia.utilities.CommonMethods;

public class Search extends AppCompatActivity {
    private static final String TAG = "Search";
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle(R.string.search);


        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        CommonMethods.bottomBarActionHandler(bottomNavigationView, R.id.bnav_search, Search.this);
    }

    public void goToSearchSpecific(View view){
        switch (view.getId()){
            case R.id.search_card_maps:
                Intent mapsIntent = new Intent(Search.this, Maps.class);
                startActivity(mapsIntent);
                break;
            default:
                Intent searchSpecificIntent = new Intent(Search.this, SearchSpecific.class);
                searchSpecificIntent.putExtra("id", view.getId());
                startActivity(searchSpecificIntent);
                break;
        }


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
