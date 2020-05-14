package com.michaelzap94.santabiblia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.michaelzap94.santabiblia.utilities.CommonMethods;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

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

    public void goToSearchSpecific(View view){
        Intent myIntent = new Intent(Search.this, SearchSpecific.class);
        myIntent.putExtra("id", view.getId());
        startActivity(myIntent);
        //activity.startActivity(new Intent(activity, Bible.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
        //activity.overridePendingTransition(0,0);
//        switch (view.getId()){
//            case R.id.search_card_conc:
//                break;
//            case R.id.search_card_dict:
//                break;
//            case R.id.search_card_label:
//                break;
//            case R.id.search_card_text:
//                break;
//            default:
//                break;
//        }
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
