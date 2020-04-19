package com.michaelzap94.santabiblia.utilities;


import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.michaelzap94.santabiblia.Bible;
import com.michaelzap94.santabiblia.Dashboard;
import com.michaelzap94.santabiblia.MainActivity;
import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.Search;
import com.michaelzap94.santabiblia.Settings;

public class CommonMethods {

    public static void bottomBarActionHandler(BottomNavigationView bottomNavigationView, final int itemId, final AppCompatActivity activity){

        //Set item selected
        bottomNavigationView.setSelectedItemId(itemId);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(itemId == menuItem.getItemId()) return true;
                switch (menuItem.getItemId()){
                    case R.id.bnav_home:
                        activity.startActivity(new Intent(activity, MainActivity.class));
                        //activity.overridePendingTransition(0,0);
                        return true;
                    case R.id.bnav_dashboard:
                        activity.startActivity(new Intent(activity, Dashboard.class));
                        //activity.overridePendingTransition(0,0);
                        return true;
                    case R.id.bnav_bible:
                        activity.startActivity(new Intent(activity, Bible.class));
                        //activity.overridePendingTransition(0,0);
                        return true;
                    case R.id.bnav_search:
                        activity.startActivity(new Intent(activity, Search.class));
                        //activity.overridePendingTransition(0,0);
                        return true;
                    case R.id.bnav_settings:
                        activity.startActivity(new Intent(activity, Settings.class));
                        //activity.overridePendingTransition(0,0);
                        return true;
                    default: return false;
                }
            }
        });
    }
}
