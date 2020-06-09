package com.zapatatech.santabiblia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.zapatatech.santabiblia.utilities.Util;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

public class SignUp extends AppCompatActivity {
    private static final String TAG = "SignUp";
    private TextInputLayout name;
    private TextInputLayout email;
    private TextInputLayout password1;
    private TextInputLayout password2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //===================================================================================
        name = findViewById(R.id.signup_input_name);
        email = findViewById(R.id.signup_input_email);
        password1 = findViewById(R.id.signup_input_password1);
        password2 = findViewById(R.id.signup_input_password2);
        //===================================================================================
        //name.getEditText().setText(nameVal);

    }

    public boolean isValidInput(){
        boolean isValid = true;
        if (name.getEditText().getText().toString().trim().equalsIgnoreCase("")) {
            name.getEditText().setError("This field can not be blank");
            isValid = false;
        }
        if (email.getEditText().getText().toString().trim().equalsIgnoreCase("")) {
            email.getEditText().setError("This field can not be blank");
            isValid = false;
        }
        if(!Util.validEmail(email.getEditText().getText().toString().trim())){
            email.getEditText().setError("Please, provide a valid email");
            isValid = false;
        }
        if (password1.getEditText().getText().toString().trim().equalsIgnoreCase("")) {
            password1.getEditText().setError("This field can not be blank");
            isValid = false;
        }
        if (password2.getEditText().getText().toString().trim().equalsIgnoreCase("")) {
            password2.getEditText().setError("This field can not be blank");
            isValid = false;
        }
        if(!password1.getEditText().getText().toString().trim().equalsIgnoreCase(password2.getEditText().getText().toString().trim())){
            password2.getEditText().setError("Passwords must match");
            isValid = false;
        }
        return isValid;
    }

    public void startSignUp(View view){
        //1st check if input is valid
        if(isValidInput()){
            Toast.makeText(this, "valid input", Toast.LENGTH_SHORT).show();
            String nameValue = name.getEditText().getText().toString().trim();
            String emailValue = email.getEditText().getText().toString().trim();
            String password1Value = password1.getEditText().getText().toString().trim();
            String password2Value = password2.getEditText().getText().toString().trim();
        } else {
            Toast.makeText(this, "Please, provide a valid input", Toast.LENGTH_SHORT).show();
        }
    }

    public void goToLogin(View view){
        Intent i = new Intent(SignUp.this, Login.class);
        i.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i);
        finish();
    }

}