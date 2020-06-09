package com.zapatatech.santabiblia;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.zapatatech.santabiblia.utilities.CommonMethods;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MainActivity";
    //==========================================================
    //private static int SPLASH_TIME_OUT = 600;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        //=============================================================================================
        CommonMethods.checkBibleSelectedExist(MainActivity.this);
        //=============================================================================================
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
        //ONLY ALLOW TO USE THE APP-> if USER_OFFLINE OR USER_ONLINE, not WHEN USER_NONE
        Intent i = (CommonMethods.checkUserStatus(this) > CommonMethods.USER_NONE) ? new Intent(MainActivity.this, Home.class) : new Intent(MainActivity.this, Login.class);
        startActivity(i);
        finish();
    }

}
