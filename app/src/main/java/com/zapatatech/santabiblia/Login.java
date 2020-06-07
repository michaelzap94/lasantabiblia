package com.zapatatech.santabiblia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.zapatatech.santabiblia.utilities.CommonMethods;

public class Login extends AppCompatActivity {
    private static final String TAG = "Login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void goToSignUp(View view){
        Intent i = new Intent(Login.this, SignUp.class);
        startActivity(i);
    }

    public void continueOffline(View view){
        boolean offlineTrue = CommonMethods.updateUserOfflineSelection(this, true);
        if(offlineTrue){
            Intent i = new Intent(Login.this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            Toast.makeText(this, "Offline Selection not Updated", Toast.LENGTH_SHORT).show();
        }
    }
}